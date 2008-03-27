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

/* A bean for rendering a table or a list of Metrics available for a
 * project that is being evaluated.
 *
 * @author Sebastian Kuegler <sebas@kde.org>
 *
 *
 */
public class MetricsTableView {

    /* Holds the names of Metrics, indexed by ID: ID, Name
     * This Map should be in sync with metricDescriptions.
     */
    Map<Integer,String> metricNames = new HashMap<Integer,String>();

    /* Holds the descriptions of Metrics, indexed by ID: ID, Description
     * This Map should be in sync with metricNames.
     */
    Map<Integer,String> metricDescriptions = new HashMap<Integer,String>();

    // Holds the ID of the selected project, if any
    Long projectId;

    /* Show the ID in the table HTML output? */
    boolean showId = true;

    /* Show the name of the metric in the table HTML output? */
    boolean showName = true;

    // Show the description of the metric in the table HTML output?
    boolean showDescription = true;

    /* Show the header of the table? */
    boolean showHeader = true;

    /* Show the footer of the table? */
    boolean showFooter = false;

    /* CSS class to use for the table element */
    String tableClass = new String();

    /* CSS class to use for the individual cells of the table */
    String cellClass = new String();

    /* Identifier in the HTML output */
    String tableId = new String();

    public MetricsTableView () {
        retrieveData();
    }

    public MetricsTableView (Long projectId) {
        this.projectId = projectId;
    }

    public void addMetric (Metric metric) {
        metricNames.put(
                metric.getId().intValue(),
                metric.getName());
        metricDescriptions.put(
                metric.getId().intValue(),
                metric.getDescription());
    }

    /* Retrieves data (right now, we're setting Dummy data, later on,
     * this function retrieves data from the database.
     */
    public void retrieveData () {

        metricNames.put(0, "Line Count");
        metricDescriptions.put(0, "Implements wc -l");

        metricNames.put(1, "Cyclomatic Complexity");
        metricDescriptions.put(1, "How complex is the code?");

        metricNames.put(2, "Developer Interaction");
        metricDescriptions.put(2, "Communication between developers");

        metricNames.put(3, "Commit Statistics");
        metricDescriptions.put(3, "Number of commits");

        metricNames.put(4, "Mailing List Activity");
        metricDescriptions.put(4, "How many emails have been sent?");

    }

    /* @return HTML code representing a list of Metrics.
     *
     */
    public String getHtml() {

        // Count columns so we know how an empty table row looks like
        int columns = 0;
        if (showId) {
            columns++;
        }
        if (showName) {
            columns++;
        }
        if (showDescription) {
            columns++;
        }

        // Prepare some CSS tricks
        String css_class = new String();
        String cell_class = new String();
        String table_id = new String();

        if (tableClass.length() > 0) {
            css_class = " class=\"" + tableClass + "\" ";
        }
        if (cellClass.length() > 0) {
            cell_class = " class=\"" + cellClass + "\" ";
        }
        if (tableId.length() > 0) {
            table_id = " class=\"" + tableId + "\" ";
        }

        StringBuilder html = new StringBuilder("<!-- MetricsTableView -->\n");
        html.append("<table " + table_id + " " + css_class + " cellspacing=\"0\">\n");

        // Table header
        html.append("<thead><tr>");
        if (showId) {
            html.append("\n\t<td " + cell_class + ">ID</td>");
        }
        if (showName) {
            html.append("\n\t<td " + cell_class + ">Metric</td>");
        }
        if (showDescription) {
            html.append("\n\t<td " + cell_class + ">Description</td>");
        }
        html.append("\n</tr></thead>\n\n");

        // Table footer
        if (showFooter) {
            // Dummy.
            html.append("\n<tfoot>\n<tr>");
            html.append("\n\t<td  " + cell_class + " colspan=\"" + columns + "\">&nbsp;</td>");
            html.append("\n</tr>\n</tfoot>\n\n");
        }
        // Table rows
        html.append("<tbody>");
        for (Integer key: metricNames.keySet()) {
            html.append("\n<tr>");
            if (showId) {
                html.append("\n\t<td " + cell_class + ">" + key + "</td>");
            }
            if (showName) {
                html.append("\n\t<td " + cell_class + ">" + metricNames.get(key) + "</td>");
            }
            if (showDescription) {
                html.append("\n\t<td " + cell_class + ">" + metricDescriptions.get(key) + "</td>");
            }
            html.append("\n</tr>");
        }

        html.append("\n</tbody>");
        html.append("\n</table>");

        return html.toString();
    }

    /**
     * Generates a simple, unordered list of all metric descriptors in a HTML
     * format.
     * 
     * @return The list of metric descriptors in a HTML format.
     */
    public String getHtmlList() {
        StringBuilder html = new StringBuilder("<!-- MetricsList -->\n<ul>");

        if (! metricNames.isEmpty()) {
            for (Integer key: metricNames.keySet()) {
                html.append("\n\t<li>");
                html.append(metricNames.get(key));
                if (showDescription) {
                    html.append(" <i>" + metricDescriptions.get(key) + "</i>");
                }
                html.append("</li>");
            }
            html.append("\n</ul>");
        }
        else {
            html.append (Functions.NOT_YET_EVALUATED);
        }

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
        showName = show;
    }

    /*
    * @return true or false (show the name in the table or not?).
    */
    public boolean getShowName () {
        return showName;
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
}
