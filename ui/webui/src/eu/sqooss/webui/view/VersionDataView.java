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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
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
 * verbosely presents metric result which were evaluated on the project
 * versions of a single project.
 */
public class VersionDataView extends AbstractDataView {
    /*
     * Holds the list of selected resources (<i>a list of version time
     * stamps</i>).
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
        supportedCharts = TABLE_CHART + LINE_CHART;
    }

    /**
     * Sets the resources which this view will present as selected.
     * 
     * @param selected the list of selected resources
     *   (<i>a list of version numbers</i>).
     */
    private void setSelectedResources(List<String> selected) {
        ArrayList<String> valid = new ArrayList<String>();
        if (selected != null)
            for (String resource : selected) {
                Version version = project.getVersionByScmId(resource);
                if (version == null)
                    continue;
                if (selectedResources.contains(version.getTimestamp()) == false)
                    selectedResources.add(version.getTimestamp());
                if (valid.contains(resource) == false)
                    valid.add(resource);
            }

        // Cleanup the corresponding session variable from invalid entries
        viewConf.setSelectedResources(valid.toArray(new String[valid.size()]));
    }

    /**
     * Loads all the necessary information, that is associated with the
     * resources presented in this view.
     */
    private void loadData() {
        if ((project != null) && (project.isValid())) {
            /*
             * Load the list of metrics that were evaluated on this resource
             * type and are related to the presented resource type
             */
            evaluated = project.getEvaluatedMetrics().getMetricMnemonics(
                    MetricActivator.PROJECTVERSION,
                    MetricType.SOURCE_CODE);
            evaluated.putAll(project.getEvaluatedMetrics().getMetricMnemonics(
                    MetricActivator.PROJECTVERSION,
                    MetricType.PROJECT_WIDE));

            if (viewConf != null) {
                // Load the list of selected metrics
                setSelectedMetrics(viewConf.getSelectedMetrics());

                // Load the list of selected versions
                setSelectedResources(viewConf.getSelectedResources());
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
        loadData();

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
            // Assemble the results data-set
            //----------------------------------------------------------------
            /*
             * Data set format:
             * < metric_mnemonic < version_timestamp, evaluation_value > >
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
            for (Long versionTimestamp : selectedResources) {
                Version nextVersion =
                    project.getVersionByTimestamp(versionTimestamp);
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
                                        nextVersion.getTimestamp(),
                                        result.getHtml(0));
                            }
                            else {
                                data.get(metric.getMnemonic()).put(
                                        nextVersion.getTimestamp(),
                                        null);
                            }
                        }
                    }
                }
            }
            //----------------------------------------------------------------
            // Display the results in the selected form
            //----------------------------------------------------------------
            String chartName = null;
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
                    chartName = lineChart(
                            data.subMap(highlightedMetric, highlightedMetric +"\0"));
                else
                    chartName = lineChart(data);
                /*
                 * Display the generated results chart.
                 */
                if (chartName != null) {
                    String thumbURL = settings.getTempURL(chartName);
                    String chartURL = settings.getTempURL(
                            chartName.replace("thb", "img"));
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
                        b.append(sp(in) + "<td class=\"dvChartTitle\">"
                                + "<a href=\"" 
                                + getServletPath()
                                + "\">"
                                + "ALL" + "</a>"
                                + "</td>\n");
                    else
                        b.append(sp(in) + "<td"
                                + " class=\"dvChartTitleSelected\">"
                                + leadOption
                                + "</td>\n");
                    /*
                     * Display the chart cell
                     */
                    int chartRowSpan = 2;
                    if (data.size() > 1)
                        chartRowSpan += data.size();
                    b.append(sp(in) + "<td"
                            + " class=\"dvChartImage\""
                            + " rowspan=\"" + chartRowSpan + "\">"
                            + "<a class=\"dvChartImage\""
                            + " href=\"/fullscreen.jsp?"
                            + "chartfile=" + chartURL + "\">"
                            + "<img src=\"" + thumbURL + "\">"
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
                                        + " class=\"dvChartTitleSelected\">"
                                        + mnemonic
                                        + "</td>\n");
                            else
                                b.append(sp(in) + "<td"
                                        + " class=\"dvChartTitle\">"
                                        + "<a href=\"" 
                                        + getServletPath()
                                        + "?highMetric=" + mnemonic
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
                            + " class=\"dvChartTitleEmpty\">"
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
        loadData();

        Version selVersion = null;
        if (viewConf.getHighlightedResource() != null) {
            try {
                selVersion = project.getVersionByScmId(
                        viewConf.getHighlightedResource());
            }
            catch (NumberFormatException ex) { /* Do nothing */ }
        }

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
                        + "<td><b>Latest ID</b></td>"
                        + "<td>"+ version.getName() + "</td>"
                        + "</tr>\n");

                b.append(sp(in) + "<tr>"
                        + "<td><b>Commited</b></td>"
                        + "<td>"
                        + Functions.formatTimestamp(
                                version.getTimestamp(),
                                settings.getUserLocale())
                        + "</td>"
                        + "</tr>\n");

                Developer commiter = project.getDeveloperById(version.getCommitterId());
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

            // Version revision
            b.append(sp(in) + "<tr>"
                    + "<td><b>Version ID</b></td>"
                    + "<td>" + selVersion.getName() + "</td>"
                    + "</tr>\n");

            // Version timestamp
            b.append(sp(in) + "<tr>"
                    + "<td><b>Commited</b></td>"
                    + "<td>"
                    + Functions.formatTimestamp(
                            selVersion.getTimestamp(),
                            settings.getUserLocale())
                    + "</td>"
                    + "</tr>\n");

            // Version commiter
            Developer commiter =
                project.getDeveloperById(selVersion.getCommitterId());
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
        loadData();

        if (project.getVersionsCount() < 1) {
            b.append(sp(in)
                    + Functions.error("This project has no versions!"));
        }
        else {
            b.append(sp(in++) + "<form>\n");
            //----------------------------------------------------------------
            // Display the list of version metrics evaluated on this project
            //----------------------------------------------------------------
            b.append(sp(in++) + "<div class=\"dvSubpanelLeft\">\n");
            b.append(sp(in) + "<div class=\"dvSubtitle\">Metrics</div>\n");
            b.append(sp(in++) + "<select class=\"dvSubselect\""
                    + " name=\"selMetrics\""
                    + " multiple"
                    + " size=\"5\""
                    + ((evaluated.isEmpty()) ? " disabled" : "")
                    + ">\n");
            for (String mnemonic : evaluated.values()) {
                Metric metric = project.getEvaluatedMetrics()
                    .getMetricByMnemonic(mnemonic);
                if (metric != null)
                    b.append(sp(in) + "<option class=\"dvSubselect\""
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
            b.append(sp(in++) + "<div class=\"dvSubpanelRight\">\n");
            b.append(sp(in) + "<div class=\"dvSubtitle\">Version Ids</div>\n");
            b.append(sp(in++) + "<select class=\"dvSubselect\""
                    + " name=\"selResources\""
                    + " multiple"
                    + " size=\"5\""
                    + ((selectedResources.size() < 1) ? " disabled" : "")
                    + ">\n");
            for (Long versionTimestamp : selectedResources) {
                boolean isTagged = project.getTaggedVersions()
                        .getTaggedVersionByTimestamp(versionTimestamp) != null;
                Version version = 
                    project.getVersionByTimestamp(versionTimestamp);
                b.append(sp(in) + "<option class=\"dvSubselect\""
                        + " selected"
                        + " value=\"" + version.getName() + "\">"
                        + (isTagged ? "* " : "") + version.getName()
                        + "</option>\n");
            }
            b.append(sp(--in) + "</select>\n");

            b.append(sp(--in) + "</div>\n");
            b.append(sp(in++) + "<div class=\"dvSubpanelApply\">\n");
            b.append(sp(in) + "<input type=\"submit\" value=\"Apply\">\n");
            b.append(sp(--in) + "</div>\n");
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
        for (Long versionTimestamp : selectedResources) {
            Version version = 
                project.getVersionByTimestamp(versionTimestamp);
            b.append(sp(in++) + "<tr>\n");
            b.append(sp(in) + "<td class=\"def_head\">"
                    + version.getName() 
                    + "</td>\n");
            for (String mnemonic : values.keySet()) {
                String result = null;
                if (values.get(mnemonic).get(versionTimestamp) != null) {
                    result = values.get(mnemonic).get(versionTimestamp).toString();
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
        // Construct the chart's data-set
        DefaultCategoryDataset data = new DefaultCategoryDataset();
        for (String nextLine : values.keySet()) {
            SortedMap<Long, String> lineValues = values.get(nextLine);
            for (Long nextX : lineValues.keySet()) {
                if (lineValues.get(nextX) == null) continue;
                try {
                    data.addValue(
                            new Double(lineValues.get(nextX)),
                            nextLine,
                            project.getVersionByTimestamp(nextX).getName());
                }
                catch (NumberFormatException ex) { /* Skip */ }
            }
        }
        // Generate the chart
        if (data.getColumnCount() > 0) {
            JFreeChart chart;
            chart = ChartFactory.createLineChart(
                    null, null, "Evaluation Results",
                    data, PlotOrientation.VERTICAL,
                    true, true, false);
            chart.setBackgroundPaint(new Color(0, 0, 0, 0));
            chart.setPadding(RectangleInsets.ZERO_INSETS);
            // Save the chart into a temporary file
            try {
                java.io.File image = java.io.File.createTempFile(
                        "img", ".png", settings.getTempFolder());
                java.io.File thumbnail = new java.io.File(
                        settings.getTempFolder()
                        + java.io.File.separator
                        + image.getName().replace("img", "thb"));
                ChartUtilities.saveChartAsPNG(image, chart, 960, 720);
                ChartUtilities.saveChartAsPNG(thumbnail, chart, 320, 240);
                return thumbnail.getName();
            }
            catch (IOException e) {
                // Do nothing
            }
        }

        return null;
    }

}
