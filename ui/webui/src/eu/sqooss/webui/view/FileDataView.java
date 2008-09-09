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
import eu.sqooss.webui.datatype.File;
import eu.sqooss.webui.datatype.Version;

/**
 * The class <code>FileDataView</code> renders an HTML sequence that
 * verbosely presents metric result which were evaluated on a single file
 * in a single project.
 */
public class FileDataView extends AbstractDataView {

    /*
     * Holds the target file's Id.
     */
    private Long fileId;

    /*
     * Holds the target file's object.
     */
    private File selFile = null;

    /*
     * Holds the list of selected resources (<i>a list of file versions</i>).
     */
    private List<Long> selectedResources = new ArrayList<Long>();

    /*
     * Holds the evaluation results for the selected file.
     */
    HashMap<String, Result> results = new HashMap<String, Result>();

    /**
     * Instantiates a new <code>FileDataView</code> object,
     * and initializes it with the given project object and target file Id.
     * 
     * @param project the project object
     * @param fileId the file Id
     */
    public FileDataView(Project project, Long fileId) {
        super();
        this.project = project;
        this.fileId = fileId;
        supportedCharts = TABLE_CHART + LINE_CHART;
    }

    /**
     * Sets the resources which this view will present as selected.
     * 
     * @param selected the list of selected resources
     *   (<i>a list of file versions</i>).
     */
    private void setSelectedResources(List<String> selected) {
        if (selected != null)
            for (String resource : selected) {
                try {
                    Long value = new Long(resource);
                    if (selectedResources.contains(value) == false)
                        selectedResources.add(value);
                }
                catch (NumberFormatException ex) { /* Do nothing */ }
            }

        // Cleanup the corresponding session variable from invalid entries
        String[] validResources = new String[selectedResources.size()];
        int index = 0;
        for (Long nextVersion : selectedResources)
            validResources[index++] = nextVersion.toString();
        viewConf.setSelectedResources(validResources);
    }

    /**
     * Loads all the necessary information, that is associated with the
     * resources presented in this view.
     */
    private void loadData () {
        if ((project != null) && (project.isValid())) {
            // Pre-load the target file's object
            if (fileId != null)
                selFile = project.getCurrentVersion().getFile(fileId);

            if (selFile != null) {
                selFile.setTerrier(this.terrier);
                /*
                 * Load the list of metrics that were evaluated on this resource
                 * type and are related to the presented resource type
                 */
                if (selFile.getIsDirectory())
                    evaluated = project.getEvaluatedMetrics().getMetricMnemonics(
                            MetricActivator.PROJECTFILE,
                            MetricType.SOURCE_FOLDER);
                else
                    evaluated = project.getEvaluatedMetrics().getMetricMnemonics(
                            MetricActivator.PROJECTFILE,
                            MetricType.SOURCE_CODE);

                // Retrieve the selected file's results
                results = selFile.getResults(evaluated.values());

                if (viewConf != null) {
                    // Load the list of selected metrics
                    setSelectedMetrics(viewConf.getSelectedMetrics());

                    // Load the list of selected versions
                    setSelectedResources(viewConf.getSelectedResources());
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see eu.sqooss.webui.ListView#getHtml(long)
     */
    public String getHtml(long in) {
        if ((project == null) || (project.isValid() == false))
            return(sp(in) + Functions.error("Invalid project!"));

        // Holds the accumulated HTML content
        StringBuilder b = new StringBuilder("");

        // Load the selected versions' data
        loadData();

        if (selFile == null) {
            b.append(sp(in) + Functions.error("File/folder not found!"));
        }
        else if ((selectedMetrics.isEmpty()) || (selectedResources.isEmpty())) {
            b.append(sp(in)
                    + "Select one or more metrics and file/folder versions.");
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
             * < metric_mnemonic < file_version, evaluation_value > >
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
            SortedMap<Long, Long> mods = terrier.getFileModification(
                    project.getCurrentVersion().getId(), fileId);
            for (Long versionNum : selectedResources) {
                Long nextFileId = mods.get(versionNum);
                if (nextFileId != null) {
                    HashMap<String, Result> verResults =
                        selFile.getResults(evaluated.values(), nextFileId);
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
                            + "chartfile=" + chartFile.replace("thb", "img") + "\">"
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
        // Load the selected file's data
        loadData();
        // Hold the accumulated HTML content
        StringBuilder b = new StringBuilder("");

        if (selFile == null) {
            b.append(sp(in) + Functions.error("File/folder not found!"));
        }
        else {
            b.append(sp(in++) + "<table>\n");

            // File name
            b.append(sp(in) + "<tr>"
                    + "<td><b>Name</b></td>"
                    + "<td>" + selFile.getShortName() + "</td>"
                    + "</tr>\n");

            // File type
            b.append(sp(in) + "<tr>"
                    + "<td><b>Type</b></td>"
                    + "<td>"
                    + ((selFile.getIsDirectory()) ? "folder" : "file")
                    + "</td>"
                    + "</tr>\n");

            // Version number
            Version version = project.getVersion(selFile.getVersion());
            b.append(sp(in) + "<tr>"
                    + "<td><b>Version</b></td>"
                    + "<td>"
                    + ((version != null) ? version.getNumber() : "N/A")
                    + "</td>"
                    + "</tr>\n");

            // File status
            b.append(sp(in) + "<tr>"
                    + "<td><b>Status</b></td>"
                    + "<td>"
                    + selFile.getStatus().toLowerCase(settings.getUserLocale())
                    + "</td>"
                    + "</tr>\n");

            // Developer name
            Developer commiter = null;
            if (version != null)
                commiter = project.getDevelopers().getDeveloperById(
                        version.getCommitterId());
            b.append(sp(in) + "<tr>"
                    + "<td><b>Commiter</b></td>"
                    + "<td>"
                    + ((commiter != null) ? commiter.getUsername() : "N/A")
                    + "</td>"
                    + "</tr>\n");

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
        // Load the selected file's data
        loadData();
        // Hold the accumulated HTML content
        StringBuilder b = new StringBuilder("");

        if (selFile == null) {
            b.append(sp(in) + Functions.error("File/folder not found!"));
        }
        else {
            b.append(sp(in++) + "<form>\n");
            //----------------------------------------------------------------
            // Display the list of metrics evaluated on this file
            //----------------------------------------------------------------
            b.append(sp(in++) + "<div class=\"dvSubpanelLeft\">\n");
            b.append(sp(in) + "<div class=\"dvSubtitle\">Metrics</div>\n");
            b.append(sp(in++) + "<select class=\"dvSubselect\""
                    + " name=\"selMetrics\""
                    + " multiple"
                    + " size=\"5\""
                    + ((results.isEmpty()) ? " disabled" : "")
                    + ">\n");
            for (String mnemonic : results.keySet()) {
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
            // Display the list of file modifications
            //----------------------------------------------------------------
            b.append(sp(in++) + "<div class=\"dvSubpanelRight\">\n");
            b.append(sp(in) + "<div class=\"dvSubtitle\">Modifications</div>\n");
            b.append(sp(in++) + "<select class=\"dvSubselect\""
                    + " name=\"selResources\""
                    + " multiple"
                    + " size=\"5\""
                    + ((results.isEmpty()) ? " disabled" : "")
                    + ">\n");
            SortedMap<Long, Long> mods = terrier.getFileModification(
                    project.getCurrentVersion().getId(), fileId);
            for (Long version : mods.keySet()) {
                b.append(sp(in) + "<option class=\"dvSubselect\""
                        + ((selectedResources.contains(version))
                                ? " selected" : "")
                        + " value=\"" + version + "\">"
                        + "v." + version
                        + "</option>\n");
            }
            b.append(sp(--in) + "</select>\n");

            b.append(sp(--in) + "</div>\n");
            b.append(sp(in++) + "<div style=\"position: relative; clear: both; padding-top: 5px; border: 0; text-align: center;\">\n");
            b.append(sp(in)+ "<input type=\"submit\" value=\"Apply\">\n");
            b.append(sp(--in)+ "</div>\n");
            b.append(sp(--in) + "</form>\n");
        }

        return b.toString();
    }

    private String tableChart (long in, SortedMap<String, SortedMap<Long, String>> values) {
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
        for (Long version : selectedResources) {
            b.append(sp(in++) + "<tr>\n");
            b.append(sp(in) + "<td class=\"def_head\">"
                    + "In v." + version 
                    + "</td>\n");
            for (String mnemonic : values.keySet()) {
                String result = null;
                if (values.get(mnemonic).get(version) != null)
                    result = values.get(mnemonic).get(version).toString();
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
