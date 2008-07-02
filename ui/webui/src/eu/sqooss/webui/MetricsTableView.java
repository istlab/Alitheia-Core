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

/**
 * The class <code>MetricsTableView</code> renders an HTML sequence that
 * present the specified metrics in a tabular format.
 *
 */
public class MetricsTableView extends ListView {

    /** Holds the list of metric indexed by their Ids. */
    private Map<Long,Metric> metrics = new HashMap<Long,Metric>();

    /** Holds the list of metrics which will be displayed as selected. */
    private List<Long> selectedMetrics = new ArrayList<Long>();

    /** Holds the Id of the project to which this view belongs. */
    private Long projectId = null;

    /** Enables the visualization of the metric Id column */
    private boolean showId = false;

    /** Enables the visualization of the metric select column */
    private boolean showSelect = false;

    /** Enables the visualization of the metric mnemonic column */
    private boolean showMnemonic = true;

    /** Enables the visualization of the metric description column */
    private boolean showDescription = true;

    /** Enables the visualization of the metric type column */
    private boolean showType = true;

    /** Enables the visualization of the metric's activation type column */
    private boolean showActivator = true;

    /** Enables the visualization of the metric result column */
    private boolean showResult = true;

    // Flag for enabling the visualization of the metrics' table header
    boolean showHeader = true;

    // Flag for enabling the visualization of the metrics' table footer
    boolean showFooter = true;

    // CSS class to use for the table element
    String tableClass = new String("table");

    // CSS class to use for the individual cells of the table
    String cellClass = new String();

    // HTML table identifier
    String tableId = new String("table");

    /**
     * Instantiates a new <code>MetricsTableView</code>metrics object, and
     * initializes it with the given metrics list.
     * 
     * @param metricsList the metrics list
     */
    public MetricsTableView (List<Metric> metricsList) {
        if (metricsList != null)
            for (Metric nextMetric : metricsList)
                metrics.put(nextMetric.getId(), nextMetric);
    }

    /**
     * Adds an additional metric to the locally stored metrics list.
     *
     * @param metric a <code>Metric</code> instance
     */
    public void addMetric (Metric metric) {
        if (metric != null)
            metrics.put(metric.getId().longValue(), metric);
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
     * This method enables or disables the visualization of the metric Id
     * column, depending on the given boolean value:
     * <ul>
     *   <li>a value of <code>true<code> will enable the column
     *   <li>a value of <code>false<code> will disable the column
     * </ul>
     *
     * @param show the boolean flag
     */
    public void setShowId (boolean show) {
        showId = show;
    }

    /**
     * This method enables or disables the visualization of the metric select
     * column, depending on the given boolean value:
     * <ul>
     *   <li>a value of <code>true<code> will enable the column
     *   <li>a value of <code>false<code> will disable the column
     * </ul>
     *
     * @param show the boolean flag
     */
    public void setShowSelect (boolean show) {
        showSelect = show;
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

    /**
     * This method enables or disables the visualization of the metric result
     * column, depending on the given boolean value:
     * <ul>
     *   <li>a value of <code>true<code> will enable the column
     *   <li>a value of <code>false<code> will disable the column
     * </ul>
     *
     * @param show the boolean flag
     */
    public void setShowResult (boolean show) {
        showResult = show;
    }

    // TODO: Replace with a property bundle
    /**
     * Constructs a proper error message when the list of metric is empty
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

    /* (non-Javadoc)
     * @see eu.sqooss.webui.ListView#getHtml(long)
     */
    public String getHtml(long in) {
        // Skip on empty metrics list
        if (metrics.isEmpty())
            return errNoMetrics();

        // Calculate the number of visible table columns
        int columns = 0;
        if (showId)             columns++;
        if (showSelect)         columns++;
        if (showMnemonic)       columns++;
        if (showDescription)    columns++;
        if (showType)           columns++;
        if (showActivator)      columns++;
        if (showResult)         columns++;

        // Prepare some CSS tricks
        // TODO: Wouldn't it be easier to simply switch the CSS file instead
        // and keep the id and class names the same?
        String table_id = new String();
        String css_class = new String();
        String head_class = new String(" class=\"head\"");
        String foot_class = new String(" class=\"foot\"");
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

        StringBuilder html = new StringBuilder("");
        // Create a table
        html.append(sp(in++) + "<div" + table_id + ">\n");
        html.append(sp(in++) + "<table>\n");

        // Table header
        if (showHeader) {
            html.append(sp(in++) + "<thead>\n");
            html.append(sp(in++) + "<tr" + head_class + ">\n");
            if (showId)
                html.append(sp(in) + "<td" + head_class + ">ID</td>\n");
            if (showSelect)
                html.append(sp(in) + "<td" + head_class + ">Selection</td>\n");
            if (showMnemonic)
                html.append(sp(in) + "<td" + head_class + ">Name</td>\n");
            if (showDescription)
                html.append(sp(in) + "<td" + head_class + ">Description</td>\n");
            if (showType)
                html.append(sp(in) + "<td" + head_class + ">Type</td>\n");
            if (showActivator)
                html.append(sp(in) + "<td" + head_class + ">Activator</td>\n");
            if (showResult)
                html.append(sp(in) + "<td" + head_class + ">Results</td>\n");
            html.append(sp(--in) + "</tr>\n");
            html.append(sp(--in) + "</thead>\n");
        }

        // Table footer
        if (showFooter) {
            html.append(sp(in++) + "<tfoot>\n");
            html.append(sp(in++) + "<tr" + foot_class + ">\n");
            html.append(sp(in) + "<td" + foot_class
                    + " colspan=\"" + (columns - 1) + "\">"
                    + "TOTAL: " + metrics.size() + " metrics"
                    + "</td>\n");
            html.append(sp(in) + "<td" + foot_class
                    + " style=\"text-align: right;\""
                    + " colspan=\"" + (columns - 1) + "\">"
                    + "<form>"
                    + "<input type=\"hidden\" name=\""
                    + ((projectId == null)
                            ? "refreshAllMetrics"
                            : "refreshPrjMetrics" )
                    + "\""
                    + " value=\"true\">"
                    + "<input type=\"submit\" class=\"submit\""
                    + " value=\"Refresh\">"
                    + "</form>"
                    + "</td>\n");
            html.append(sp(--in) + "</tr>\n");
            html.append(sp(--in) + "</tfoot>\n");
        }

        // Table rows
        html.append(sp(in++) + "<tbody>\n");
        for (Long key: metrics.keySet()) {
            html.append(sp(in++) + "<tr>\n");
            if (showId) {
                html.append(sp(in) + "<td " + cell_class + ">"
                        + key + "</td>\n");
            }
            if (showSelect) {
                html.append(sp(in) + "<td " + cell_class + ">"
                        + ((selectedMetrics.contains(key))
                                ? metrics.get(key).getDeselectMetricLink()
                                : metrics.get(key).getSelectMetricLink())
                        + "</td>\n");
            }
            if (showMnemonic) {
                html.append(sp(in) + "<td " + cell_name_class + ">"
                        + metrics.get(key).getMnemonic() + "</td>\n");
            }
            if (showDescription) {
                html.append(sp(in) + "<td " + cell_class + ">"
                        + metrics.get(key).getDescription() + "</td>\n");
            }
            if (showType) {
                html.append(sp(in) + "<td " + cell_class + ">"
                        + metrics.get(key).getType() + "</td>\n");
            }
            if (showActivator) {
                html.append(sp(in) + "<td " + cell_class + ">"
                        + metrics.get(key).getActivator() + "</td>\n");
            }
            if (showResult) {
                html.append(sp(in) + "<td " + cell_class + ">"
                        + metrics.get(key).getLink() + "</td>\n");
            }
            html.append(sp(--in) + "</tr>\n");
        }
        html.append(sp(--in) + "</tbody>\n");

        // Close the table
        html.append(sp(--in) + "</table>\n");
        html.append(sp(--in) + "</div>\n");

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

     public void setSelectedMetrics (List<Long> selected) {
         if (selected != null)
             this.selectedMetrics = selected;
     }
}
