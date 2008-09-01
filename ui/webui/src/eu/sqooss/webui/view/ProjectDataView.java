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
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.jfree.ui.RectangleInsets;

import eu.sqooss.webui.Functions;
import eu.sqooss.webui.ListView;
import eu.sqooss.webui.Project;
import eu.sqooss.webui.Result;
import eu.sqooss.webui.datatype.TaggedVersion;
import eu.sqooss.webui.datatype.Version;

/**
 * The class <code>ProjectDataView</code> renders an HTML sequence that
 * presents an overview information for a single project.
 */
public class ProjectDataView extends ListView {
    /*
     * Holds the project object.
     */
    private Project project;

    /**
     * Instantiates a new <code>ProjectDataView</code>, and initializes it
     * with the given project object.
     * 
     * @param project the project
     */
    public ProjectDataView(Project project) {
        super();
        this.project = project;
    }

    /**
     * Renders the content of the source code related sub-view.
     * 
     * @param in the indentation depth
     * 
     * @return the content of the source code related sub-view.
     */
    public String getCodeInfo(long in) {
        if ((project == null) || (project.isValid() == false))
            return(sp(in) + Functions.error("Invalid project!"));
        StringBuilder b = new StringBuilder();
        b.append(sp(in++) + "<table>\n");

        // Project versions
        b.append(sp(in++) + "<tr>\n");
        b.append(sp(in) + "<td><b>Versions:</b></td>"
                + "<td>"
                + "<a href=\"/versions.jsp\">"
                + project.getVersionsCount()
                + "</a>"
                + "</td>\n");
        b.append(sp(--in) + "</tr>\n");

        // First and last version timestamps
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd MMM yyyy", settings.getUserLocale());
        b.append(sp(in++) + "<tr>\n");
        b.append(sp(in) + "<td><b>First:</b></td>"
                + "<td>"
                + dateFormat.format(project.getFirstVersion().getTimestamp())
                + "</td>\n");
        b.append(sp(--in) + "</tr>\n");
        b.append(sp(in++) + "<tr>\n");
        b.append(sp(in) + "<td><b>Last:</b></td>"
                + "<td>"
                + dateFormat.format(project.getLastVersion().getTimestamp())
                + "</td>\n");
        b.append(sp(--in) + "</tr>\n");

        // Tagged versions
        Collection<TaggedVersion> tagged = project.getTaggedVersions();
        b.append(sp(in++) + "<tr>\n");
        b.append(sp(in) + "<td><b>Tagged:</b></td>"
                + "<td>"
                + "<a href=\"/versions.jsp?vvvito=true\">"
                + tagged.size()
                + "</a>"
                + "</td>\n");
        b.append(sp(--in) + "</tr>\n");

        // Files in the latest version
        b.append(sp(in++) + "<tr>\n");
        Long filesCount = project.getLastVersion().getFilesCount();
        if (filesCount == null)
            filesCount = new Long(0);
        b.append(sp(in) + "<td><b>Files:</b></td>"
                + "<td>"
                + "<a href=\"/files.jsp\">"
                + filesCount
                + "</a>"
                + "</td>\n");
        b.append(sp(--in) + "</tr>\n");

        //====================================================================
        // Prepare the list of pre-selected key metrics
        //====================================================================

        // TODO: Read the key metrics selection from the configuration file!
        HashMap<String,String> keyMetrics= new HashMap<String, String>();
        keyMetrics.put("NOCL", "Classes");

        //====================================================================
        // Render an overview of the key metrics results
        //====================================================================

        // Retrieve the evaluation results for the last project version
        Version lastVersion = project.getLastVersion();
        if ((lastVersion != null) && (keyMetrics.isEmpty() == false)) {
            lastVersion.setTerrier(terrier);

            // Retrieve evaluation results from the key metrics
            HashMap<String, Result> results =
                lastVersion.getResults(keyMetrics.keySet());

            // Display the key metrics
            for (String mnemonic : results.keySet()) {
                b.append(sp(in++) + "<tr>\n");
                b.append(sp(in)
                        + "<td><b>" + keyMetrics.get(mnemonic) + ":</b></td>"
                        + "<td>" + results.get(mnemonic).getString() + "</td>"
                        + "\n");
                b.append(sp(--in) + "</tr>\n");
            }
        }
        b.append(sp(--in) + "</table>\n");

        //====================================================================
        // Render the key metrics chart
        //====================================================================

        // Prepare the storage for the chart data
        SortedMap<String, SortedMap<Date, String>> chartData =
            new TreeMap<String, SortedMap<Date,String>>();
        for (String mnemonic : keyMetrics.keySet())
            chartData.put(mnemonic, new TreeMap<Date, String>());

        // Simulate tagged versions on a project without any
        if (tagged.isEmpty()) {
            long counter = project.getVersionsCount();
            long range = counter / ((counter > 5) ? 5 : counter);
            while (counter > 0) {
                TaggedVersion nextVersion = new TaggedVersion(
                        project.getVersionByNumber(counter), terrier);
                if (nextVersion.isValid())
                    tagged.add(nextVersion);
                counter -= range;
            }
        }

        // Retrieve the key metrics results on all tagged version
        if ((tagged.isEmpty() == false) && (keyMetrics.isEmpty() == false)) {
            
            for (TaggedVersion tag : tagged) {
                HashMap<String, Result> verResults =
                    tag.getResults(keyMetrics.keySet());
                for (String mnemonic : verResults.keySet()) {
                    Result result = verResults.get(mnemonic);
                    result.setSettings(settings);
                    if (result.getIsPrintable())
                        chartData.get(mnemonic).put(
                                tag.getTimestamp(), result.getHtml(0));
                }
            }
        }

        // Generate and include the chart image into the rendered content
        if (chartData.isEmpty() == false) {
            String chartFile = null;
            chartFile = "/tmp/" + lineChart(chartData);
            if (chartFile != null) {
                b.append(sp(in++) + "<table>\n");
                b.append(sp(in++) + "<tr>\n");
                b.append(sp(in)
                        + "<td class=\"pdv_chart\" colspan=\"2\">"
                        + "<a class=\"pdv_chart\""
                        + " href=\"/fullscreen.jsp?"
                        + "chartfile=" + chartFile + "\">"
                        + "<img src=\"" + chartFile + "\">"
                        + "</a>"
                        + "</td>\n");
                b.append(sp(--in) + "</tr>\n");
                b.append(sp(--in) + "</table>\n");
            }
        }

        return b.toString();
    }

    /**
     * Renders the content of the developers and mailing lists related
     * sub-view.
     * 
     * @param in the indentation depth
     * 
     * @return the content of the source code related sub-view.
     */
    public String getDevsInfo(long in) {
        if ((project == null) || (project.isValid() == false))
            return(sp(in) + Functions.error("Invalid project!"));
        StringBuilder b = new StringBuilder();
        b.append(sp(in++) + "<table>\n");

        // Project developers
        b.append(sp(in++) + "<tr>\n");
        b.append(sp(in) + "<td><b>Developers:</b></td>"
                + "<td>" + project.getDevelopersCount() + "</td>\n");
        b.append(sp(--in) + "</tr>\n");

        // Number of mailing lists
        b.append(sp(in++) + "<tr>\n");
        b.append(sp(in) + "<td><b>Mailing lists:</b></td>"
                + "<td>" + project.getMailingListCount() + "</td>\n");
        b.append(sp(--in) + "</tr>\n");

        b.append(sp(--in) + "</table>\n");

        return b.toString();
    }

    /**
     * Renders the content of the bugs related sub-view.
     * 
     * @param in the indentation depth
     * 
     * @return the content of the source code related sub-view.
     */
    public String getBugsInfo(long in) {
        if ((project == null) || (project.isValid() == false))
            return(sp(in) + Functions.error("Invalid project!"));
        StringBuilder b = new StringBuilder();
        b.append(sp(in++) + "<table>\n");

        // TODO: Implement
        b.append(sp(in++) + "<tr>\n");
        b.append(sp(in) + "<td style=\"color: #999999;\">"
                + "<i>Pending implementation.</i>"
                + "</td>\n");
        b.append(sp(--in) + "</tr>\n");

        b.append(sp(--in) + "</table>\n");

        return b.toString();
    }

    @Override
    public String getHtml(long indentationDepth) {
        // TODO Auto-generated method stub
        return null;
    }

    private String lineChart (SortedMap<String, SortedMap<Date, String>> values) {
        // Construct the chart's dataset
        TimePeriodValuesCollection data = new TimePeriodValuesCollection();
        for (String nextLine : values.keySet()) {
            TimePeriodValues lineData = new TimePeriodValues(nextLine);
            SortedMap<Date, String> lineValues = values.get(nextLine);
            for (Date nextX : lineValues.keySet()) {
                if (lineValues.get(nextX) == null) continue;
                try {
                    lineData.add(
                            new Day(nextX),
                            new Double(lineValues.get(nextX)));
                }
                catch (NumberFormatException ex) { /* Skip it. */ }
            }
            if (lineData.getItemCount() > 0)
                data.addSeries(lineData);
        }
        // Generate the chart
        if (data.getSeriesCount() > 0) {
            JFreeChart chart;
            chart = ChartFactory.createTimeSeriesChart(
                    null, "Time", "Result",
                    data,
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
            catch (IOException e) { /* Do nothing. */ }
        }

        return null;
    }
}
