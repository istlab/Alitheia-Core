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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import eu.sqooss.webui.Functions;
import eu.sqooss.webui.ListView;
import eu.sqooss.webui.Metric;
import eu.sqooss.webui.Project;
import eu.sqooss.webui.Result;
import eu.sqooss.webui.Version;
import eu.sqooss.webui.Metric.MetricActivator;
import eu.sqooss.webui.Metric.MetricType;
import eu.sqooss.webui.datatype.Developer;
import eu.sqooss.webui.datatype.File;

/**
 * The class <code>VerboseFileView</code> renders an HTML sequence that
 * verbosely presents the metric evaluation result of a single file in a
 * specific project version. In addition it provides mean for comparing the
 * results against the results calculated on this file in another project
 * revision.
 */
public class VerboseFileView extends ListView {
    /*
     * Hold the selected project's object.
     */
    private Project project;

    /*
     * Holds the selected file's Id.
     */
    private Long fileId;

    /*
     * Holds the selected file's object.
     */
    private File selFile = null;

    /*
     * Holds the file's version selection (a list of version numbers)
     */
    private List<Long> selectedVersions = new ArrayList<Long>();

    /*
     * Holds the file's metric selection (a list of metric Ids)
     */
    private List<Long> selectedMetrics = new ArrayList<Long>();

    /*
     * Holds the list of metrics that were evaluated on the selected file.
     */
    private Collection<String> mnemonics = null;

    /*
     * Holds the evaluation results for the selected file.
     */
    HashMap<String, Result> results = new HashMap<String, Result>();

    public static final int TABLE_CHART = 2;
    public static final int LINE_CHART = 4;
    // Default chart type
    private int chartType = TABLE_CHART;

    /**
     * Instantiates a new <code>VerboseFileView</code> object, and initializes
     * it with the given project object and file Id.
     * 
     * @param project the project object
     * @param fileId the file Id
     */
    public VerboseFileView(Project project, Long fileId) {
        super();
        this.project = project;
        this.fileId = fileId;
    }

    public void setSelectedVersions(String[] versions) {
        if (versions != null)
            for (String versionNum : versions) {
                try {
                Long value = new Long(versionNum);
                if (selectedVersions.contains(value) == false)
                    selectedVersions.add(value);
                }
                catch (NumberFormatException ex) { /* Do nothing */ }
            }
    }

    public void setSelectedMetrics(String[] metrics) {
        if (metrics != null)
            for (String metricId : metrics) {
                try {
                    Long value = new Long(metricId);
                    if (selectedMetrics.contains(value) == false)
                        selectedMetrics.add(value);
                }
                catch (NumberFormatException ex) { /* Do nothing */ }
            }
    }

    public void setChartType(int chartType) {
        this.chartType = chartType;
    }

    public void attachSelectedFile () {
        // Retrieve the selected file's object
        if (fileId != null)
            selFile = project.getCurrentVersion().getFile(fileId);
        // Retrieve the selected file's results
        if (selFile != null) {
            selFile.setTerrier(this.terrier);
            if (selFile.getIsDirectory())
                mnemonics = project.getEvaluatedMetrics().getMetricMnemonics(
                        MetricActivator.PROJECTFILE,
                        MetricType.SOURCE_FOLDER).values();
            else
                mnemonics = project.getEvaluatedMetrics().getMetricMnemonics(
                        MetricActivator.PROJECTFILE,
                        MetricType.SOURCE_CODE).values();
            results = selFile.getResults(mnemonics);

            if (settings != null) {
                setSelectedVersions(settings.getVfvSelectedVersions());
                setSelectedMetrics(settings.getVfvSelectedMetrics());
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
        // Load the selected file's data
        attachSelectedFile();

        if (selFile == null) {
            b.append(sp(in) + Functions.error("File/folder not found!"));
        }
        else if ((selectedMetrics.isEmpty()) || (selectedVersions.isEmpty())) {
            b.append(sp(in) + "Select one or more metrics and resource versions.");
        }
        else {
            //----------------------------------------------------------------
            // Assemble the results dataset
            //----------------------------------------------------------------
            SortedMap<String, SortedMap<Long, String>> data =
                new TreeMap<String, SortedMap<Long,String>>();
            for (Long metricId : selectedMetrics) {
                Metric metric =
                    project.getEvaluatedMetrics().getMetricById(metricId);
                if (metric != null)
                    data.put(metric.getMnemonic(), new TreeMap<Long, String>());
            }
            SortedMap<Long, Long> mods = terrier.getFileModification(
                    project.getCurrentVersion().getId(), fileId);
            for (Long versionNum : selectedVersions) {
                Long nextFileId = mods.get(versionNum);
                if (nextFileId != null) {
                    HashMap<String, Result> verResults =
                        selFile.getResults(mnemonics, nextFileId);
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
                chartFile = "/tmp/" + lineChart(data);
                b.append(sp(in++)
                            + "<a class=\"chart\""
                            + " href=\"/fullscreen.jsp?"
                            + "chartfile=" + chartFile + "\">"
                            + "<img src=\"" + chartFile + "\">"
                            + "</a>");
                b.append(sp(in++) + "<table"
                        + " style=\"margin-top: 0;\">\n");
                b.append(sp(in++) + "</tr>\n");
                b.append(sp(in) + "<td class=\"chart\">");
                if (chartFile != null)
                    b.append("<img style=\"width: 100%;\""
                            + " src=\"/tmp/" + chartFile + "\""
                            + ">");
                else
                    b.append(Functions.information(
                            "Inapplicable results."));
                b.append("</td>\n");
                b.append(sp(--in) + "</tr>\n");
                b.append(sp(--in) + "</table>\n");
                break;
            default:
                b.append(tableChart(in, data));
                break;
            }

        }
        return b.toString();
    }

    public String getFileInfo (long in) {
        if ((project == null) || (project.isValid() == false))
            return(sp(in) + Functions.error("Invalid project!"));
        // Load the selected file's data
        attachSelectedFile();
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

    public String getFileControls (long in) {
        if ((project == null) || (project.isValid() == false))
            return(sp(in) + Functions.error("Invalid project!"));
        // Load the selected file's data
        attachSelectedFile();
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
            b.append(sp(in++) + "<div class=\"vfvmid\">\n");
            b.append(sp(in) + "<div class=\"vfvtitle\">Metrics</div>\n");
            b.append(sp(in++) + "<select class=\"vfvmid\""
                    + " name=\"vfvmid\""
                    + " multiple"
                    + " size=\"5\""
                    + ((results.isEmpty()) ? " disabled" : "")
                    + ">\n");
            for (String mnemonic : results.keySet()) {
                Metric metric = project.getEvaluatedMetrics()
                    .getMetricByMnemonic(mnemonic);
                if (metric != null)
                    b.append(sp(in) + "<option class=\"vfvmid\""
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
            b.append(sp(in++) + "<div class=\"vfvfid\">\n");
            b.append(sp(in) + "<div class=\"vfvtitle\">Modifications</div>\n");
            b.append(sp(in++) + "<select class=\"vfvfid\""
                    + " name=\"vfvfid\""
                    + " multiple"
                    + " size=\"5\""
                    + ((results.isEmpty()) ? " disabled" : "")
                    + ">\n");
            SortedMap<Long, Long> mods = terrier.getFileModification(
                    project.getCurrentVersion().getId(), fileId);
            for (Long version : mods.keySet()) {
                b.append(sp(in) + "<option class=\"vfvfid\""
                        + ((selectedVersions.contains(version))
                                ? " selected" : "")
                        + " value=\"" + version + "\">"
                        + "v." + version
                        + "</option>\n");
            }
            b.append(sp(--in) + "</select>\n");

            b.append(sp(--in) + "</div>\n");
            b.append(sp(in++) + "<div style=\"position: relative; clear: both; padding-top: 5px; border: 0; text-align: center;\">\n");
            b.append(sp(in)+ "<input type=\"hidden\" name=\"fid\" value=\"" + fileId + "\">\n");
            b.append(sp(in)+ "<input type=\"submit\" value=\"Apply\">\n");
            b.append(sp(--in)+ "</div>\n");
            b.append(sp(--in) + "</form>\n");
        }

        return b.toString();
    }

    public String tableChart (long in, SortedMap<String, SortedMap<Long, String>> values) {
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
        for (Long version : selectedVersions) {
            b.append(sp(in++) + "<tr>\n");
            b.append(sp(in) + "<td class=\"def_head\">"
                    + "In v." + version 
                    + "</td>\n");
            for (String mnemonic : values.keySet()) {
                String result = values.get(mnemonic).get(version).toString();
                b.append(sp(in) + "<td class=\"def_right\">"
                        + ((result != null) ? result : "N/A")
                        + "</td>\n");
            }
            b.append(sp(--in) + "</tr>\n");
        }
        b.append(sp(--in) + "</table>\n");

        return b.toString();
    }

    public String lineChart (SortedMap<String, SortedMap<Long, String>> values) {
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
