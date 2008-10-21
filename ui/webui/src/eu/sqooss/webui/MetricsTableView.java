/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008-2008 by Sebastian Kuegler <sebas@kde.org>
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

package eu.sqooss.webui;

import eu.sqooss.webui.util.MetricsList;

/**
 * The class <code>MetricsTableView</code> renders an HTML sequence that
 * present the specified metrics in a tabular format.
 *
 */
public class MetricsTableView extends ListView {
    /*
     * Holds the selected project's object.
     */
    protected Project project = null;

    /*
     * Holds the list of installed metrics.
     */
    private MetricsList instMetrics = new MetricsList();

    /*
     * Holds the list of metrics evaluated on the selected project.
     */
    private MetricsList evalMetrics = new MetricsList();

    /** Enables the visualization of the metric mnemonic column */
    private boolean showMnemonic = true;

    /** Enables the visualization of the metric description column */
    private boolean showDescription = true;

    /** Enables the visualization of the metric type column */
    private boolean showType = false;

    /** Enables the visualization of the metric's scope column */
    private boolean showScope = true;

    /**
     * Instantiates a new <code>MetricsTableView</code> object, and
     * initializes it with the given project object.
     * 
     * @param project the project object
     * @param fileId the file Id
     */
    public MetricsTableView (Project project) {
        super();
        if (project != null)
            this.project = project.isValid() ? project : null;
    }

    /**
     * Loads all the necessary information, that is associated with the
     * resources presented in this view.
     */
    private void loadData() {
        // Retrieve the list of installed metrics
        instMetrics = new MetricsList();
        instMetrics.addAll(terrier.getAllMetrics());

        // Retrieve the list of metrics evaluated on the selected project
        if (project != null)
            evalMetrics = project.getEvaluatedMetrics();
    }

    /**
     * This method enables or disables the visualization of the metric
     * mnemonic column, depending on the given boolean value:
     * <ul>
     *   <li>a value of <code>true<code> will enable the column
     *   <li>a value of <code>false<code> will disable the column
     * </ul>
     *
     * @param show the boolean flag
     */
    public void setShowMnemonic (boolean show) {
        showMnemonic = show;
    }

    /**
     * This method enables or disables the visualization of the metric
     * description column, depending on the given boolean value:
     * <ul>
     *   <li>a value of <code>true<code> will enable the column
     *   <li>a value of <code>false<code> will disable the column
     * </ul>
     *
     * @param show the boolean flag
     */
    public void setShowDescription (boolean show) {
        showDescription = show;
    }

    /**
     * This method enables or disables the visualization of the metric type
     * column, depending on the given boolean value:
     * <ul>
     *   <li>a value of <code>true<code> will enable the column
     *   <li>a value of <code>false<code> will disable the column
     * </ul>
     *
     * @param show the boolean flag
     */
    public void setShowType (boolean show) {
        showType = show;
    }

    /* (non-Javadoc)
     * @see eu.sqooss.webui.ListView#getHtml(long)
     */
    public String getHtml(long in) {
        // Hold the accumulated HTML content
        StringBuilder b = new StringBuilder("");

        // Load the required metrics data
        loadData();

        // Calculate the number of visible table columns
        int columns = 0;
        if (showMnemonic)       columns++;
        if (showDescription)    columns++;
        if (showType)           columns++;
        if (showScope)          columns++;

        // Shortcut some frequently used CSS classes
        String head_class = " class=\"def_head\"";
        String subhead_class = " class=\"def_subhead\"";
        String foot_class = " class=\"def_foot\"";
        String cell_class = " class=\"def\"";

        // Create the metrics table
        b.append(sp(in++) + "<table class=\"def\">\n");

        // Table header
        b.append(sp(in++) + "<thead>\n");
        b.append(sp(in++) + "<tr>\n");

        if (showMnemonic)
            b.append(sp(in) + "<td" + head_class + ">Name</td>\n");
        if (showDescription)
            b.append(sp(in) + "<td" + head_class + ">Description</td>\n");
        if (showType)
            b.append(sp(in) + "<td" + head_class + ">Type</td>\n");
        if (showScope)
            b.append(sp(in) + "<td" + head_class + ">Scope</td>\n");

        b.append(sp(--in) + "</tr>\n");
        b.append(sp(--in) + "</thead>\n");

        // Table footer
        b.append(sp(in++) + "<tfoot>\n");
        b.append(sp(in++) + "<tr>\n");
        b.append(sp(in) + "<td" + foot_class
                + " colspan=\"" + columns + "\">"
                + "TOTAL: " + instMetrics.size() + " metrics"
                + "</td>\n");
        b.append(sp(--in) + "</tr>\n");
        b.append(sp(--in) + "</tfoot>\n");

        // Table rows
        b.append(sp(in++) + "<tbody>\n");
        if (project != null)
            b.append(sp(in) + "<tr><td" + subhead_class
                    + " style=\"text-align: center;\""
                    + " colspan=\"" + columns + "\">"
                    + "<i>Evaluated</i>"
                    + "</td></tr>\n");
        for (Metric nextMetric : instMetrics) {
            if (evalMetrics.contains(nextMetric) == false)
                continue;
            b.append(sp(in++) + "<tr>\n");
            if (showMnemonic) {
                b.append(sp(in) + "<td" + cell_class + "><b>"
                        + nextMetric.getMnemonic() + "</b></td>\n");
            }
            if (showDescription) {
                b.append(sp(in) + "<td" + cell_class + ">"
                        + nextMetric.getDescription() + "</td>\n");
            }
            if (showType) {
                b.append(sp(in) + "<td" + cell_class + ">"
                        + nextMetric.getType() + "</td>\n");
            }
            if (showScope) {
                b.append(sp(in) + "<td" + cell_class + ">"
                        + nextMetric.getScope() + "</td>\n");
            }
            b.append(sp(--in) + "</tr>\n");
        }
        if ((project != null) && (evalMetrics.isEmpty()))
            b.append(sp(in) + "<td" + cell_class
                    + " colspan=\"" + columns + "\">"
                    + "<i>" + "None Evaluated" + "</i>"
                    + "</td>\n");
        if (evalMetrics.size() < instMetrics.size())
            b.append(sp(in) + "<tr><td" + subhead_class
                    + " style=\"text-align: center;\""
                    + " colspan=\"" + columns + "\">"
                    + "Available"
                    + "</td></tr>\n");
        for (Metric nextMetric : instMetrics.sortByMnemonic().values()) {
            if (evalMetrics.contains(nextMetric))
                continue;
            b.append(sp(in++) + "<tr>\n");
            if (showMnemonic) {
                b.append(sp(in) + "<td" + cell_class + "><b>"
                        + nextMetric.getMnemonic() + "</b></td>\n");
            }
            if (showDescription) {
                b.append(sp(in) + "<td" + cell_class + ">"
                        + nextMetric.getDescription() + "</td>\n");
            }
            if (showType) {
                b.append(sp(in) + "<td" + cell_class + ">"
                        + nextMetric.getType() + "</td>\n");
            }
            if (showScope) {
                b.append(sp(in) + "<td" + cell_class + ">"
                        + nextMetric.getScope() + "</td>\n");
            }
            b.append(sp(--in) + "</tr>\n");
        }
        if (instMetrics.isEmpty())
            b.append(sp(in) + "<td" + cell_class
                    + " colspan=\"" + columns + "\">"
                    + "<i>" + Functions.NO_INSTALLED_METRICS + "</i>"
                    + "</td>\n");
        b.append(sp(--in) + "</tbody>\n");

        // Close the table
        b.append(sp(--in) + "</table>\n");
        b.append(sp(--in) + "</div>\n");

        return b.toString();
    }
}
