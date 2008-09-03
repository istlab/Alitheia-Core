/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2008 by Sebastian Kuegler <sebas@kde.org>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package eu.sqooss.webui.view;

import java.awt.Color;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import eu.sqooss.webui.Functions;
import eu.sqooss.webui.Metric;
import eu.sqooss.webui.Project;
import eu.sqooss.webui.Result;
import eu.sqooss.webui.Metric.MetricActivator;
import eu.sqooss.webui.Metric.MetricType;
import eu.sqooss.webui.datatype.Developer;
import eu.sqooss.webui.datatype.TaggedVersion;
import eu.sqooss.webui.datatype.Version;

/**
 * The class <code>VersionDataView</code> renders an HTML sequence that
 * verbosely presents metric result that were evaluated on the project
 * versions of a single project.
 */
public class VersionDataView extends AbstractDataView {
    /*
     * Holds the list of selected resources (<i>a list of version numbers</i>).
     */
    private List<Long> selectedResources = new ArrayList<Long>();

    /**
     * Instantiates a new <code>VerboseDataView</code> object,
     * and initializes it with the given project object.
     * 
     * @param project the project object
     */
    public VersionDataView(Project project) {
        super();
        this.project = project;
    }

    /**
     * Sets the resources which this view will present as selected.
     * 
     * @param selected the array of selected resources
     *   (<i>a list of version numbers</i>).
     */
    private void setSelectedVersions(String[] selected) {
        if (selected != null)
            for (String resource : selected) {
                try {
                    Long value = new Long(resource);
                    if ((selectedResources.contains(value) == false)
                            && (project.getLastVersion().getNumber() >= value)
                            && (project.getFirstVersion().getNumber() <= value))
                        selectedResources.add(value);
                }
                catch (NumberFormatException ex) { /* Do nothing */ }
            }

        // Cleanup the corresponding session variable from invalid entries
        String[] validVersions = new String[selectedResources.size()];
        int index = 0;
        for (Long nextVersion : selectedResources)
            validVersions[index++] = nextVersion.toString();
        settings.setVvvSelectedVersions(validVersions);
    }

    /**
     * Loads all the necessary information, that is associated with the
     * selected project versions.
     */
    private void attachSelectedVersions () {
        if ((project != null) && (project.isValid())) {
            // Pre-load the selected project versions
            for (Long version : selectedResources) {
                project.getVersionByNumber(version);
            }

            // Load the list of evaluated version metrics for this project
            evaluated = project.getEvaluatedMetrics().getMetricMnemonics(
                    MetricActivator.PROJECTVERSION,
                    MetricType.SOURCE_CODE);

            if (settings != null) {
                // Load the list of selected metrics
                setSelectedMetrics(settings.getVvvSelectedMetrics());

                // Load the list of selected versions
                setSelectedVersions(settings.getVvvSelectedVersions());
            }
        }
    }

    /* (non-Javadoc)
     * @see eu.sqooss.webui.ListView#getHtml(long)
     */
    public String getHtml(long in) {
        if ((project == null) || (project.isValid() == false))
            return(sp(in) + Functions.error("Invalid project!"));

        // Hold the accumulated HTML content
        StringBuilder b = new StringBuilder("");

        // Load the selected versions' data
        attachSelectedVersions();
        if (project.getVersionsCount() < 1) {
            b.append(sp(in)
                    + Functions.error("This project has no versions!"));
        }
        else if ((selectedMetrics.isEmpty()) || (selectedResources.isEmpty())) {
            b.append(sp(in)
                    + "Select one or more metrics and project versions.");
        }
        else {
            //----------------------------------------------------------------
            // Cleanup procedures
            //----------------------------------------------------------------
            /*
             * Clear the highlighted metric variable, in case the selected
             * metrics list is narrowed to a single metric only.
             */
            if (selectedMetrics.size() == 1)
                highlightedMetric = null;
            //----------------------------------------------------------------
            // Assemble the results dataset
            //----------------------------------------------------------------
            /*
             * Data set format:
             * < metric_mnemonic < version_number, evaluation_value > >
             */
            SortedMap<String, SortedMap<Long, String>> data =
                new TreeMap<String, SortedMap<Long,String>>();
            // Prepare the data set
            for (Long metricId : selectedMetrics) {
                Metric metric =
                    project.getEvaluatedMetrics().getMetricById(metricId);
                if (metric != null)
                    data.put(metric.getMnemonic(), new TreeMap<Long, String>());
            }
            // Fill the data set
            for (Long versionNum : selectedResources) {
                Version nextVersion = project.getVersionByNumber(versionNum);
                if (nextVersion != null) {
                    nextVersion.setTerrier(terrier);
                    HashMap<String, Result> verResults =
                        nextVersion.getResults(
                                evaluated.values(),
                                nextVersion.getId());
                    for (Long metricId : selectedMetrics) {
                        Metric metric = project.getEvaluatedMetrics()
                            .getMetricById(metricId);
                        if (metric != null) {
                            Result result = verResults.get(
                                    metric.getMnemonic());
                            if (result != null) {
                                result.setSettings(settings);
                                data.get(metric.getMnemonic()).put(
                                        versionNum,
                                        result.getHtml(0));
                            }
                            else {
                                data.get(metric.getMnemonic()).put(
                                        versionNum, null);
                            }
                        }
                    }
                }
            }
            //----------------------------------------------------------------
            // Display the results in the selected form
            //----------------------------------------------------------------
            String chartFile = null;
            switch (chartType) {
            case TABLE_CHART:
                b.append(tableChart(in, data));
                break;
            case LINE_CHART:
                /*
                 * Generate the results chart.
                 */
                if ((highlightedMetric != null)
                        && (data.containsKey(highlightedMetric)))
                    chartFile = "/tmp/" + lineChart(
                            data.subMap(highlightedMetric, highlightedMetric +"\0"));
                else
                    chartFile = "/tmp/" + lineChart(data);
                /*
                 * Display the generated results chart.
                 */
                if (chartFile != null) {
                    b.append(sp(in++) + "<table"
                            + " style=\"margin-top: 0;\">\n");
                    /*
                     * Display the aggregation chart's option, only if results
                     * for at least two metrics are available. Otherwise
                     * display only a single metric's option.
                     */
                    String leadOption = "ALL";
                    if (selectedMetrics.size() == 1) {
                        leadOption = data.firstKey();
                    }
                    b.append(sp(in++) + "</tr>\n");
                    if ((highlightedMetric != null)
                            && (data.containsKey(highlightedMetric)))
                        b.append(sp(in) + "<td class=\"vfv_chart_title\">"
                                + "<a href=\"" 
                                + getServletPath()
                                + "\">"
                                + "ALL" + "</a>"
                                + "</td>\n");
                    else
                        b.append(sp(in) + "<td"
                                + " class=\"vfv_chart_title_selected\">"
                                + leadOption
                                + "</td>\n");
                    /*
                     * Display the chart cell
                     */
                    int chartRowSpan = 2;
                    if (data.size() > 1)
                        chartRowSpan += data.size();
                    b.append(sp(in) + "<td"
                            + " class=\"vfv_chart_image\""
                            + " rowspan=\"" + chartRowSpan + "\">"
                            + "<a class=\"vfvchart\""
                            + " href=\"/fullscreen.jsp?"
                            + "chartfile=" + chartFile + "\">"
                            + "<img src=\"" + chartFile + "\">"
                            + "</a>"
                            + "</td>\n");
                    b.append(sp(--in) + "</tr>\n");
                    /*
                     * Display a chart option for each of the selected metrics,
                     * unless only one metric is selected.
                     */
                    if (selectedMetrics.size() > 1) {
                        for (String mnemonic : data.keySet()) {
                            b.append(sp(in++) + "<tr>\n");
                            if ((highlightedMetric != null)
                                    && (highlightedMetric.equals(mnemonic)))
                                b.append(sp(in) + "<td"
                                        + " class=\"vfv_chart_title_selected\">"
                                        + mnemonic
                                        + "</td>\n");
                            else
                                b.append(sp(in) + "<td"
                                        + " class=\"vfv_chart_title\">"
                                        + "<a href=\"" 
                                        + getServletPath()
                                        + "?vfvsm=" + mnemonic
                                        + "\">"
                                        + mnemonic + "</a>"
                                        + "</td>\n");
                            b.append(sp(--in) + "</tr>\n");
                        }
                    }
                    /*
                     * Display an empty transparent cell to align the options
                     * row with the chart row. 
                     */
                    b.append(sp(in++) + "<tr>\n");
                    b.append(sp(in) + "<td"
                            + " class=\"vfv_chart_title_empty\">"
                            + "&nbsp;"
                            + "</td>\n");
                    b.append(sp(--in) + "</tr>\n");
                    b.append(sp(--in) + "</table>\n");
                }
                else
                    b.append(Functions.information(
                            "Inapplicable results."));
                break;
            default:
                b.append(tableChart(in, data));
                break;
            }

        }
        return b.toString();
    }

    /**
     * Renders an info panel related to the selected project versions
     * 
     * @param in the indentation depth
     * 
     * @return The generated HTML content.
     */
    public String getInfo (long in) {
        if ((project == null) || (project.isValid() == false))
            return(sp(in) + Functions.error("Invalid project!"));

        // Hold the accumulated HTML content
        StringBuilder b = new StringBuilder("");

        // Load the selected versions' data
        attachSelectedVersions();

        Version selVersion = null;
        if (settings.getVvvHighlightedVersion() != null)
            selVersion = project.getVersionByNumber(
                    settings.getVvvHighlightedVersion());

        if (project.getVersionsCount() < 1) {
            b.append(sp(in)
                    + Functions.error("This project has no versions!"));
        }
        else if (selVersion == null) {
            b.append(sp(in++) + "<table>\n");

            // Project name
            b.append(sp(in) + "<tr>"
                    + "<td><b>Project</b></td>"
                    + "<td>" + project.getName() + "</td>"
                    + "</tr>\n");

            // Versions number
            b.append(sp(in) + "<tr>"
                    + "<td><b>Versions</b></td>"
                    + "<td>" + project.getVersionsCount() + "</td>"
                    + "</tr>\n");

            // Tagged versions
            b.append(sp(in) + "<tr>"
                    + "<td><b>Tagged</b></td>"
                    + "<td>" + project.getTaggedVersions().size() + "</td>"
                    + "</tr>\n");
            
            // Last version fields
            Version version = project.getLastVersion();
            if (version != null) {
                b.append(sp(in) + "<tr>"
                        + "<td><b>Latest</b></td>"
                        + "<td>" + version.getNumber() + "</td>"
                        + "</tr>\n");

                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "dd MMM yyyy", settings.getUserLocale());
                b.append(sp(in) + "<tr>"
                        + "<td><b>Date</b></td>"
                        + "<td>"
                        + dateFormat.format(version.getTimestamp())
                        + "</td>"
                        + "</tr>\n");

                Developer commiter =
                    project.getDevelopers().getDeveloperById(
                            version.getCommitterId());
                b.append(sp(in) + "<tr>"
                        + "<td><b>Commiter</b></td>"
                        + "<td>"
                        + ((commiter != null) ? commiter.getUsername() : "N/A")
                        + "</td>"
                        + "</tr>\n");
            }

            b.append(sp(--in) + "</table>\n");
        }
        else {
            b.append(sp(in++) + "<table>\n");

            // Version number
            b.append(sp(in) + "<tr>"
                    + "<td><b>Version</b></td>"
                    + "<td>" + selVersion.getNumber() + "</td>"
                    + "</tr>\n");

            // Version timestamp
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "dd MMM yyyy", settings.getUserLocale());
            b.append(sp(in) + "<tr>"
                    + "<td><b>Commited</b></td>"
                    + "<td>"
                    + dateFormat.format(selVersion.getTimestamp())
                    + "</td>"
                    + "</tr>\n");

            // Version commiter
            Developer commiter =
                project.getDevelopers().getDeveloperById(
                        selVersion.getCommitterId());
            b.append(sp(in) + "<tr>"
                    + "<td><b>Commiter</b></td>"
                    + "<td>"
                    + ((commiter != null) ? commiter.getUsername() : "N/A")
                    + "</td>"
                    + "</tr>\n");

            // Version tag
            TaggedVersion tag =
                project.getTaggedVersions().getTaggedVersionById(
                        selVersion.getId());
            if (tag != null) {
                b.append(sp(in) + "<tr>"
                        + "<td><b>Tag name</b></td>"
                        + "<td>"
                        + tag.getTags().get(0)
                        + "</td>"
                        + "</tr>\n");
            }

            b.append(sp(--in) + "</table>\n");
        }

        return b.toString();
    }

    /**
     * Renders a control panel, that can be used for controlling various
     * rendering features of this view.
     * 
     * @param in the indentation depth
     * 
     * @return The generated HTML content.
     */
    public String getControls (long in) {
        if ((project == null) || (project.isValid() == false))
            return(sp(in) + Functions.error("Invalid project!"));

        // Hold the accumulated HTML content
        StringBuilder b = new StringBuilder("");

        // Load the selected versions' data
        attachSelectedVersions();
        if (project.getVersionsCount() < 1) {
            b.append(sp(in)
                    + Functions.error("This project has no versions!"));
        }
        else {
            b.append(sp(in++) + "<form>\n");
            //----------------------------------------------------------------
            // Display the list of version metrics evaluated on this project
            //----------------------------------------------------------------
            b.append(sp(in++) + "<div class=\"vvvmid\">\n");
            b.append(sp(in) + "<div class=\"vvvtitle\">Metrics</div>\n");
            b.append(sp(in++) + "<select class=\"vvvmid\""
                    + " name=\"vvvmid\""
                    + " multiple"
                    + " size=\"5\""
                    + ((evaluated.isEmpty()) ? " disabled" : "")
                    + ">\n");
            for (String mnemonic : evaluated.values()) {
                Metric metric = project.getEvaluatedMetrics()
                    .getMetricByMnemonic(mnemonic);
                if (metric != null)
                    b.append(sp(in) + "<option class=\"vvvmid\""
                            + ((selectedMetrics.contains(metric.getId()))
                                    ? " selected" : "")
                            + " value=\"" + metric.getId() + "\">"
                            + "" + mnemonic
                            + "</option>\n");
            }
            b.append(sp(--in) + "</select>\n");
            b.append(sp(--in) + "</div>\n");
            //----------------------------------------------------------------
            // Display the list of selected versions
            //----------------------------------------------------------------
            b.append(sp(in++) + "<div class=\"vvvvid\">\n");
            b.append(sp(in) + "<div class=\"vvvtitle\">Versions</div>\n");
            b.append(sp(in++) + "<select class=\"vvvvid\""
                    + " name=\"vvvvid\""
                    + " multiple"
                    + " size=\"5\""
                    + ((selectedResources.size() < 1) ? " disabled" : "")
                    + ">\n");
            for (Long version : selectedResources) {
                b.append(sp(in) + "<option class=\"vvvvid\""
                        + " selected"
                        + " value=\"" + version + "\">"
                        + (project.getTaggedVersions().getTaggedVersionByNumber(version) != null ? "* " : "") 
                        + "v." + version
                        + "</option>\n");
            }
            b.append(sp(--in) + "</select>\n");

            b.append(sp(--in) + "</div>\n");
            b.append(sp(in++) + "<div style=\"position: relative; clear: both; padding-top: 5px; border: 0; text-align: center;\">\n");
            b.append(sp(in) + "<input type=\"submit\" value=\"Apply\">\n");
            b.append(sp(--in)+ "</div>\n");
            b.append(sp(--in) + "</form>\n");
        }

        return b.toString();
    }

    private String tableChart (
            long in,
            SortedMap<String, SortedMap<Long, String>> values) {
        // Hold the accumulated HTML content
        StringBuilder b = new StringBuilder("");

        b.append(sp(in++) + "<table"
                + " style=\"width: " + (80 + 80*(values.size()))+ "px;\""
                + ">\n");

        //--------------------------------------------------------------------
        // Table header
        //--------------------------------------------------------------------
        b.append(sp(in++) + "<thead>\n");
        b.append(sp(in++) + "<tr>\n");
        b.append(sp(in) + "<td class=\"def_invisible\""
                + " style=\"width: 80px;\">"
                + "&nbsp;</td>\n");
        for (String mnemonic : values.keySet()) {
            Metric metric = project.getEvaluatedMetrics()
                    .getMetricByMnemonic(mnemonic);
            b.append(sp(in) + "<td class=\"def_head\""
                    + " style=\"width: 80px;\""
                    + " title=\"" + metric.getDescription() + "\">"
                    + mnemonic
                    + "</td>\n");
        }
        b.append(sp(--in) + "</tr>\n");
        b.append(sp(--in) + "</thead>\n");

        //--------------------------------------------------------------------
        // Display all available results per metric and version
        //--------------------------------------------------------------------
        for (Long resource : selectedResources) {
            b.append(sp(in++) + "<tr>\n");
            b.append(sp(in) + "<td class=\"def_head\">"
                    + resource 
                    + "</td>\n");
            for (String mnemonic : values.keySet()) {
                String result = null;
                if (values.get(mnemonic).get(resource) != null) {
                    result = values.get(mnemonic).get(resource).toString();
                    try {
                        NumberFormat localise = 
                            NumberFormat.getNumberInstance(
                                    settings.getUserLocale());
                        result = localise.format(new Double(result));
                    }
                    catch (NumberFormatException ex) { /* Do nothing */ }
                    catch (IllegalArgumentException ex) { /* Do nothing */ }
                }
                b.append(sp(in) + "<td class=\"def_right\">"
                        + ((result != null) ? result : "N/A")
                        + "</td>\n");
            }
            b.append(sp(--in) + "</tr>\n");
        }
        b.append(sp(--in) + "</table>\n");

        return b.toString();
    }

    private String lineChart (SortedMap<String, SortedMap<Long, String>> values) {
        // Construct the chart's dataset
        XYSeriesCollection data = new XYSeriesCollection();
        for (String nextLine : values.keySet()) {
            XYSeries lineData = new XYSeries(nextLine);
            SortedMap<Long, String> lineValues = values.get(nextLine);
            for (Long nextX : lineValues.keySet()) {
                if (lineValues.get(nextX) == null) continue;
                try {
                    lineData.add(nextX, new Double(lineValues.get(nextX)));
                }
                catch (NumberFormatException ex) { /* Skip */ }
            }
            if (lineData.getItemCount() > 0)
                data.addSeries(lineData);
        }
        // Generate the chart
        if (data.getSeriesCount() > 0) {
            JFreeChart chart;
            chart = ChartFactory.createXYLineChart(
                    null, "Version", "Result",
                    data, PlotOrientation.VERTICAL,
                    true, true, false);
            chart.setBackgroundPaint(new Color(0, 0, 0, 0));
            chart.setPadding(RectangleInsets.ZERO_INSETS);
            // Save the chart into a temporary file
            try {
                java.io.File tmpFile = java.io.File.createTempFile(
                        "img", ".png", settings.getTempFolder());
                ChartUtilities.saveChartAsPNG(tmpFile, chart, 640, 480);
                return tmpFile.getName();
            }
            catch (IOException e) {
                // Do nothing
            }
        }

        return null;
    }

}
