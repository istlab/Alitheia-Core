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

import java.util.*;

/** A bean for rendering a table or a list of Metrics available for a
 * project that is being evaluated.
 *
 */
public class MetricsTableView {

    // Stores the list of metric indexed by metric Id
    Map<Long,Metric> metrics = new HashMap<Long,Metric>();

    // Holds the Id of the project, on which this metrics were evaluated
    Long projectId = null;

    // Flag for enabling the visualization of metrics' Ids
    boolean showId = false;

    // Flag for enabling the visualization of metrics' mnemonic names
    boolean showMnemonic = true;

    // Flag for enabling the visualization of metrics' descriptions
    boolean showDescription = true;

    // Flag for enabling the visualization of metrics' types
    boolean showType = true;

    // Flag for enabling the link to the results page
    boolean showResult = true;

    // Flag for enabling the visualization of metrics' table header
    boolean showHeader = true;

    // Flag for enabling the visualization of metrics' table footer
    boolean showFooter = true;

    /* CSS class to use for the table element */
    String tableClass = new String("table");

    /* CSS class to use for the individual cells of the table */
    String cellClass = new String();

    /* Identifier in the HTML output */
    String tableId = new String("table");

    /**
     * Instantiates a new metrics table view
     */
    public MetricsTableView () {}

    /**
     * Instantiates a new metrics table view, from a list of metrics.
     * 
     * @param metricsList the metrics list
     */
    public MetricsTableView (List<Metric> metricsList) {
        if (metricsList != null)
            for (Metric nextMetric : metricsList)
                metrics.put(nextMetric.getId(), nextMetric);
    }

    /**
     * Sets the Id of the project to which this view belongs.
     *
     * @param projectId the Id of the project
     */
    public void setProjectId (Long projectId) {
        this.projectId = projectId;
    }

    /**
     * Adds additional metric to the locally stored metrics list.
     *
     * @param metric a <code>Metric</code> instance
     */
    public void addMetric (Metric metric) {
        metrics.put(metric.getId().longValue(), metric);
    }

    /**
     * Constructs a logically correct error message when the list of metric
     * is empty
     *
     * @return the error message content
     */
    private String errNoMetrics () {
        // Distinguish between "all metric for project" and "all metrics"
        if (projectId != null) {
            return Functions.error(Functions.NOT_YET_EVALUATED);
        }
        else {
            return Functions.error(Functions.NO_INSTALLED_METRICS);
        }
    }

    /**
     * Produces a HTML table, that displays the locally stored metric
     * information. The table content can be prior adjusted by using the
     * various display flags.
     *
     * @return HTML code representing the list of metrics
     */
    public String getHtml() {
        if (metrics.isEmpty()) {
            return errNoMetrics();
        }

        // Count the table columns so we know how an empty table row looks like
        int columns = 0;
        if (showId) {
            columns++;
        }
        if (showMnemonic) {
            columns++;
        }
        if (showDescription) {
            columns++;
        }
        if (showType) {
            columns++;
        }
        if (showResult) {
            columns++;
        }

        // Prepare some CSS tricks
        // TODO: Wouldn't it be easier to simply switch the CSS file instead
        // and keep the id and class names the same?
        String table_id = new String();
        String css_class = new String();
        String head_class = new String(" class=\"head\"");
        String foot_class = new String(" class=\"head\"");
        String cell_class = new String();
        String cell_name_class = new String(" class=\"name\"");

        // Construct the table's CSS Id
        if (tableId.length() > 0) {
            table_id = " id=\"" + tableId + "\" ";
        }
        // Construct the table's CSS class name
        if (tableClass.length() > 0) {
            css_class = " class=\"" + tableClass + "\" ";
        }
        if (cellClass.length() > 0) {
            cell_class = " class=\"" + cellClass + "\" ";
        }

        StringBuilder html = new StringBuilder("<!-- MetricsTableView -->\n");
        // Create a table
        html.append("\n<div" + table_id + ">");
        html.append("\n<table>");

        // Table header
        if (showHeader) {
            html.append("\n<thead>");
            html.append("\n\t<tr" + head_class + ">");
            if (showId) {
                html.append("\n\t\t<td" + head_class + ">ID</td>");
            }
            if (showMnemonic) {
                html.append("\n\t\t<td" + head_class + ">Name</td>");
            }
            if (showDescription) {
                html.append("\n\t\t<td" + head_class + ">Description</td>");
            }
            if (showType) {
                html.append("\n\t\t<td" + head_class + ">Type</td>");
            }
            if (showResult) {
                html.append("\n\t\t<td" + head_class + ">Results</td>");
            }
            html.append("\n\t</tr>");
            html.append("\n</thead>\n");
        }

        // Table footer
        if (showFooter) {
            html.append("\n<tfoot>");
            html.append("\n\t<tr" + foot_class + ">");
            html.append("\n\t\t<td" + foot_class
                    + " colspan=\"" + columns + "\">"
                    + "TOTAL: " + metrics.size() + " metrics"
                    + "</td>");
            html.append("\n\t</tr>");
            html.append("\n</tfoot>\n");
        }

        // Table rows
        html.append("\n<tbody>");
        for (Long key: metrics.keySet()) {
            html.append("\n<tr>");
            if (showId) {
                html.append("\n\t<td " + cell_class + ">"
                        + key + "</td>");
            }
            if (showMnemonic) {
                html.append("\n\t<td " + cell_name_class + ">"
                        + metrics.get(key).getMnemonic() + "</td>");
            }
            if (showDescription) {
                html.append("\n\t<td " + cell_class + ">"
                        + metrics.get(key).getDescription() + "</td>");
            }
            if (showType) {
                html.append("\n\t<td " + cell_class + ">"
                        + metrics.get(key).getType() + "</td>");
            }
            if (showResult) {
                html.append("\n\t<td " + cell_class + ">"
                        + metrics.get(key).getLink() + "</td>");
            }
            html.append("\n</tr>");
        }
        html.append("\n</tbody>");

        // Close the table
        html.append("\n</table>");
        html.append("\n</div>");

        return html.toString();
    }

    /**
     * Generates a simple, unordered list of all metric descriptors in a HTML
     * format. The list's row content can be prior adjusted by using the
     * various display flags.
     *
     * @return The list of metric descriptors in a HTML format.
     */
    public String getHtmlList() {
        if (metrics.isEmpty()) {
            return errNoMetrics();
        }
        StringBuilder html = new StringBuilder("<!-- MetricsList -->\n");
        html.append("\n<ul>");
        for (Long key: metrics.keySet()) {
            html.append("\n\t<li>");
            html.append(metrics.get(key).getMnemonic());
            if (showDescription) {
                html.append(" <i>"
                        + metrics.get(key).getDescription()
                        + "</i>");
            }
            html.append("</li>");
        }
        html.append("\n</ul>");

        return html.toString();
    }

    // ===[ GETTERS AND SETTERS ]=============================================

    /**
     * @return The CSS class that is used for the whole table.
     */
    public String getTableClass () {
       return tableClass;
    }

    /*
     * @param css_class The CSS class to use for the table.
     */
    public void setTableClass (String css_class) {
        tableClass = css_class;
    }

    /*
    * @return The CSS class that is used for the table's cells.
    */
    public String getCellClass () {
       return cellClass;
    }

    /*
    * @param cell_class Set the CSS class to use for the table's cells.
    */
    public void setCellClass (String cell_class) {
        cellClass = cell_class;
    }

    /*
    * @return The ID that is used for the whole table.
    */
    public String getTableId () {
        return tableId;
    }

    /*
    * @param table_id Set the ID to use for the table.
    */
    public void setTableId (String table_id) {
        tableId = table_id;
    }

    /*
     * @param show Wether to show the Metric ID in the table or not.
     */
    public void setShowId (boolean show) {
        showId = show;
    }

    /*
    * @return The CSS class that is used for the whole table.
    */
    public boolean getShowId () {
        return showId;
    }

    /*
    * @return The CSS class that is used for the whole table.
    */
    public void setShowName (boolean show) {
        showMnemonic = show;
    }

    /*
    * @return true or false (show the name in the table or not?).
    */
    public boolean getShowName () {
        return showMnemonic;
    }

    /*
    * @param show Wether to show the Metric's description in the Table or not
    */
    public void setShowDescription (boolean show) {
        showDescription = show;
    }

    /*
    * @return true or false (Show Metric's description in the table?)
    */
    public boolean getShowDescription () {
        return showDescription;
    }

    /*
    * @return true or false (Show Metric's Type in the table?)
    */
    public boolean getShowType() {
        return showType;
    }

    /*
    * @param show wether to show the Metric's Type in the Table or not
    */
    public void setShowType(boolean showType) {
        this.showType = showType;
    }

    /*
    * @return true or false (Show a link to the Metric's result)
    */
    public boolean getShowResult() {
        return showType;
    }

    /*
    * @param set wether to show a link to the Metric's result in the Table or not
    */
    public void setShowResult(boolean showResult) {
        this.showResult = showResult;
    }
}
