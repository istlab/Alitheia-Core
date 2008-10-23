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

package eu.sqooss.webui.view;

import java.awt.Color;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.jfree.ui.RectangleInsets;

import eu.sqooss.webui.Functions;
import eu.sqooss.webui.Project;
import eu.sqooss.webui.datatype.Version;
import eu.sqooss.webui.util.VersionsList;

public class TimelineView extends AbstractDataView {
    VersionsList versions = new VersionsList();

    /**
     * Instantiates a new <code>TimelineView</code> object, and initializes
     * it with the given project object.
     * 
     * @param project the project object
     */
    public TimelineView(Project project) {
        super();
        this.project = project;
        supportedCharts = TABLE_CHART + LINE_CHART;
        viewDependencies = DEP_OTHER;
    }

    /**
     * Loads all the necessary information, that is associated with the
     * resources presented in this view.
     */
    private void loadData() {
        if ((project != null) && (project.isValid())) {
            /*
             * Load the list of versions referenced by events which occurred
             * within the selected time period.
             */
            if ((terrier != null)
                    && (settings.getTvDateFrom() != null)
                    && (settings.getTvDateFrom() != null))
                versions.addAll(terrier.getVersionsTimeline(project.getId(),
                        settings.getTvDateFrom(), settings.getTvDateTill()));
        }
    }

    private List<Version> getVersionsInPeriod(Calendar from, Calendar till) {
        ArrayList<Version> result = new ArrayList<Version>();
        for (Long timestamp : versions.sortByTimestamp().keySet()) {
            if (timestamp != null) {
                if (timestamp < from.getTimeInMillis()) continue;
                if (timestamp >= till.getTimeInMillis()) break;
                result.add(versions.getVersionByTimestamp(timestamp));
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see eu.sqooss.webui.ListView#getHtml(long)
     */
    public String getHtml(long in) {
        if ((project == null) || (project.isValid() == false))
            return(sp(in) + Functions.error("Invalid project!"));

        // Hold the accumulated HTML content
        StringBuffer b = new StringBuffer("");

        // Load the selected versions' data
        loadData();

        if ((settings.getTvDateFrom() != null)
                && (settings.getTvDateFrom() != null)) {
            int viewRange = settings.getTvViewRange() != null
                    ? settings.getTvViewRange().intValue() : 1;

            Calendar calLow = Calendar.getInstance();
            calLow.setTimeInMillis(settings.getTvDateFrom());
            calLow.set(Calendar.HOUR, 0);
            calLow.set(Calendar.MINUTE, 0);
            calLow.set(Calendar.SECOND, 0);
            calLow.set(Calendar.MILLISECOND, 0);
            Calendar calHigh;

            switch (viewRange) {
            case 1:
                calHigh = (Calendar) calLow.clone();
                calHigh.add(Calendar.DATE, 1);
                break;
            case 2:
                while (calLow.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY)
                    calLow.add(Calendar.DATE, -1);
                calHigh = (Calendar) calLow.clone();
                calHigh.add(Calendar.DATE, 7);
                break;
            case 3:
                calLow.set(Calendar.DATE, 1);
                calHigh = (Calendar) calLow.clone();
                calHigh.add(Calendar.MONTH, 1);
                calHigh.set(Calendar.DATE, 1);
                break;
            default:
                calHigh = (Calendar) calLow.clone();
                calHigh.add(Calendar.DATE, 1);
                break;
            }

            switch (chartType) {
            case TABLE_CHART:
                b.append(tableChart(in, calLow, calHigh));
                break;
            case LINE_CHART:
                String chartFile = lineChart(calLow, calHigh);
                if (chartFile != null) {
                    chartFile = "/tmp/" + chartFile;
                    b.append(sp(in++) + "<table>\n");
                    b.append(sp(in++) + "</tr>\n");
                    b.append(sp(in) + "<td"
                            + " class=\"dvChartImage\""
                            + "<a class=\"dvChartImage\""
                            + " href=\"/fullscreen.jsp?"
                            + "chartfile=" + chartFile.replace("thb", "img") + "\">"
                            + "<img src=\"" + chartFile + "\">"
                            + "</a>"
                            + "</td>\n");
                    b.append(sp(--in) + "</tr>\n");
                    b.append(sp(--in) + "</table>\n");
                }
                else
                    b.append(Functions.information(
                            "Inapplicable results."));
                break;
            default:
                b.append(tableChart(in, calLow, calHigh));
                break;
            }
        }
        else {
            b.append(sp(in)
                    + "Select the time period for which you want to display results.");
        }

        return b.toString();
    }

    /**
     * Renders a control panel, that can be used for displaying various
     * information related to this view.
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

        // Create the "Info" table
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

        // Close the "Info" table
        b.append(sp(--in) + "</table>\n");

        return b.toString();
    }

    private String renderMonthSelect(long in, int selected, String idSuffix) {
        // Hold the accumulated HTML content
        StringBuilder b = new StringBuilder("");

        // Used to retrieve a localized date token names
        DateFormatSymbols dateLocalised =
            new DateFormatSymbols(settings.getUserLocale());

        // Create the select box
        b.append(sp(in++) + "<select id=\"month" + idSuffix + "\""
                + " class=\"icoTextInput\""
                + " onChange=\"javascript:"
                + " updateCalendar('" + idSuffix + "');\">\n");
        for (int i=0 ; i<12 ; i++) {
            if (selected != i)
                b.append(sp(in) + "<option value=\"" + i + "\">"
                        + dateLocalised.getShortMonths()[i] + "</option>\n");
            else
                b.append(sp(in) + "<option value=\"" + i + "\" selected>"
                        + dateLocalised.getShortMonths()[i] + "</option>\n");
        }

        b.append(sp(--in) + "</select>\n");
        return b.toString();
    }

    private String renderYearSelect(
            long in, long first, long last, long selected, String idSuffix) {
        // Hold the accumulated HTML content
        StringBuilder b = new StringBuilder("");

        // Create the select box
        b.append(sp(in++) + "<select id=\"year" + idSuffix + "\""
                + " class=\"icoTextInput\""
                + " onChange=\"javascript:"
                + " updateCalendar('" + idSuffix + "');\">\n");
        for (long i=first ; i<=last ; i++) {
            if (selected != i)
                b.append(sp(in) + "<option value=\"" + i + "\">"
                        + i + "</option>\n");
            else
                b.append(sp(in) + "<option value=\"" + i + "\" selected>"
                        + i + "</option>\n");
        }

        b.append(sp(--in) + "</select>\n");
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

        // Initialize both calendar objects
        Calendar projectStart   = Calendar.getInstance();
        Calendar rangeStart     = Calendar.getInstance();
        projectStart.setTimeInMillis(project.getFirstVersion().getTimestamp());
        if (settings.getTvDateFrom() != null)
            rangeStart.setTimeInMillis(settings.getTvDateFrom());
        else
            rangeStart = (Calendar) projectStart.clone();
        Calendar projectNow     = Calendar.getInstance();
        Calendar rangeEnd       = Calendar.getInstance();
        projectNow.setTimeInMillis(project.getLastVersion().getTimestamp());
        if (settings.getTvDateTill() != null)
            rangeEnd.setTimeInMillis(settings.getTvDateTill());
        else
            rangeEnd = (Calendar) projectNow.clone();

        // Render both calendars and their control fields
        Calendar phases = Calendar.getInstance();
        String[] suffix = new String[]{"From", "Till"};
        for (int i=0 ; i<2 ; i++) {
            phases = (i == 0) ? rangeStart : rangeEnd;
            // Calendar's navigation tool bar
            b.append(sp(in++) + "<div class=\"calLabel\">\n");
            b.append(sp(in) + "<label class=\"calLabel\""
                    + " for=\"day" + suffix[i] + "\">" + suffix[i] + ":</label>\n");
            b.append(sp(in) + "<input id=\"day" + suffix[i] + "\" type=\"text\""
                    + " class=\"icoTextInput\""
                    + " style=\"width: 2em; text-align: center;\""
                    + " maxlength=\"2\" value=\"" + phases.get(Calendar.DAY_OF_MONTH) + "\""
                    + " onChange=\"javascript:"
                    + " updateCalendar('" + suffix[i] + "');\">\n");
            b.append(renderMonthSelect(
                    in, phases.get(Calendar.MONTH), suffix[i]));
            b.append(renderYearSelect(in, projectStart.get(Calendar.YEAR),
                    projectNow.get(Calendar.YEAR), phases.get(Calendar.YEAR),
                    suffix[i]));
            b.append(sp(in) + "<img class=\"icon2\""
                    + " alt=\"Calendar\""
                    + " title=\"Show calendar\""
                    + " src=\"/img/icons/16x16/calendar.png\""
                    + " onClick=\"javascript: toggleCalendar('" + suffix[i] + "')\""
                    + ">");
            b.append(sp(--in) + "</div>");
            // Calendar's content
            b.append(sp(in)+ "<div id=\"cal" + suffix[i] +"\""
                    + " style=\"display: none;\">"
                    + "<script type=\"text/javascript\">"
                    + "document.write(renderCalendar("
                    + phases.get(Calendar.YEAR) + " ,"
                    + phases.get(Calendar.MONTH) + " ,'"
                    + suffix[i] + "'));"
                    + "</script>"
                    + "</div>");
        }

        b.append(sp(in++) + "<div class=\"calLabel\">\n");
        b.append(sp(in) + "<form>");
        for (int i=0 ; i<2 ; i++) {
            phases = (i == 0) ? projectStart : projectNow;
            b.append(sp(in) + "<input type=\"hidden\""
                    + " id=\"date" + suffix[i] + "\""
                    + " name=\"date" + suffix[i] + "\""
                    + " value=\"" + phases.getTimeInMillis() + "\""
                    + ">\n");
        }
        b.append(sp(in) + "<input class=\"icoButton\" type=\"submit\""
                + " value=\"Apply\">\n");
        b.append(sp(in) + "</form>");
        b.append(sp(--in) + "</div>");

        return b.toString();
    }

    private String tableChart (long in, Calendar calLow, Calendar calHigh) {
        // Hold the accumulated HTML content
        StringBuilder b = new StringBuilder("");

        b.append(sp(in++) + "<table>\n");
        // Table header
        b.append(sp(in++) + "<thead>\n");
        b.append(sp(in) + "<tr>"
                + "<td style=\"width: 20%;\"></td>"
                + "<td style=\"width: 10%;\"></td>"
                + "<td style=\"width: 70%;\"></td>"
                + "</tr>\n");
        b.append(sp(--in) + "</thead>\n");

        boolean firstResult = true;
        long viewRange = settings.getTvViewRange() != null
                ? settings.getTvViewRange() : 1;
        while (calLow.getTimeInMillis() < settings.getTvDateTill()) {
            if (viewRange == 2) {
                Calendar calPeriod;
                if ((calLow.get(Calendar.MONTH) != calHigh.get(Calendar.MONTH))
                        || (firstResult)) {
                    firstResult = false;
                    calPeriod = (Calendar) calLow.clone();
                    calPeriod.add(Calendar.MONTH, 1);
                    if (!((getVersionsInPeriod(calLow, calPeriod).size() == 0)
                            && (settings.getTvShowEmptyState() == false)))
                        b.append(sp(in) + "<tr>"
                                + "<td class=\"def_head_center\" colspan=\"3\">"
                                + Functions.formatMonth(
                                        calHigh.getTimeInMillis(),
                                        settings.getUserLocale())
                                + ", " + calHigh.get(Calendar.YEAR)
                                +"</td>"
                                + "</tr>\n");
                }

                int numVersions = getVersionsInPeriod(calLow, calHigh).size();
                if (!((numVersions == 0) && (settings.getTvShowEmptyState() == false))) {
                    b.append(sp(in++) + "<tr>\n");
                    b.append(sp(in) + "<td class=\"def_major\">"
                            + "Week " + calLow.get(Calendar.WEEK_OF_YEAR)
                            + "</td>\n");
                    b.append(sp(in) + "<td class=\"def_right\">"
                            + numVersions + "</td>\n");
                }

                if (numVersions > 0) {
                    b.append(sp(in) + "<td class=\"def\">"
                            + "<img style=\"height: 10px; width: 4px;\""
                            + " src=\"/img/icons/16x16/testl.png\">"
                            + "<img style=\"height: 10px; width: "
                            + numVersions
                            + "px;\""
                            + " src=\"/img/icons/16x16/testm.png\">"
                            + "<img style=\"height: 10px; width: 4px;\""
                            + " src=\"/img/icons/16x16/testr.png\">"
                            + "&nbsp;"
                            + "</td>\n");
                    b.append(sp(--in) + "</tr>\n");
                }
                else if (settings.getTvShowEmptyState()) {
                    b.append(sp(in) + "<td class=\"def\">&nbsp;</td>\n");
                    b.append(sp(--in) + "</tr>\n");
                }

                calLow.add(Calendar.DATE, 7);
                calHigh.add(Calendar.DATE, 7);
            }
            else if (viewRange == 3) {
                Calendar calPeriod;
                if ((calLow.get(Calendar.MONTH) == 0)
                        || (firstResult)) {
                    firstResult = false;
                    calPeriod = (Calendar) calLow.clone();
                    calPeriod.add(Calendar.YEAR, 1);
                    if (!((getVersionsInPeriod(calLow, calPeriod).size() == 0)
                            && (settings.getTvShowEmptyState() == false)))
                        b.append(sp(in) + "<tr>"
                                + "<td class=\"def_head_center\" colspan=\"3\">"
                                + calLow.get(Calendar.YEAR)
                                +"</td>"
                                + "</tr>\n");
                }

                int numVersions = getVersionsInPeriod(calLow, calHigh).size();
                if (!((numVersions == 0) && (settings.getTvShowEmptyState() == false))) {
                    b.append(sp(in++) + "<tr>\n");
                    b.append(sp(in) + "<td class=\"def_major\">"
                            + Functions.formatMonth(
                                    calLow.getTimeInMillis(),
                                    settings.getUserLocale())
                            + "</td>\n");
                    b.append(sp(in) + "<td class=\"def_right\">"
                            + numVersions + "</td>\n");
                }

                if (numVersions > 0) {
                    b.append(sp(in) + "<td class=\"def\">"
                            + "<img style=\"height: 10px; width: 4px;\""
                            + " src=\"/img/icons/16x16/testl.png\">"
                            + "<img style=\"height: 10px; width: "
                            + numVersions
                            + "px;\""
                            + " src=\"/img/icons/16x16/testm.png\">"
                            + "<img style=\"height: 10px; width: 4px;\""
                            + " src=\"/img/icons/16x16/testr.png\">"
                            + "&nbsp;"
                            + "</td>\n");
                    b.append(sp(--in) + "</tr>\n");
                }
                else if (settings.getTvShowEmptyState()) {
                    b.append(sp(in) + "<td class=\"def\">&nbsp;</td>\n");
                    b.append(sp(--in) + "</tr>\n");
                }

                calLow.add(Calendar.MONTH, 1);
                calHigh.set(Calendar.DATE, 1);
                calHigh.add(Calendar.MONTH, 1);
                calHigh.set(Calendar.DATE, 1);
            }
            else {
                Calendar calPeriod;
                if ((calLow.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
                        || (firstResult)) {
                    firstResult = false;
                    calPeriod = (Calendar) calLow.clone();
                    calPeriod.add(Calendar.DATE, 7);
                    if (!((getVersionsInPeriod(calLow, calPeriod).size() == 0)
                            && (settings.getTvShowEmptyState() == false)))
                        b.append(sp(in) + "<tr>"
                                + "<td class=\"def_head_center\" colspan=\"3\">"
                                + "Week " + calLow.get(Calendar.WEEK_OF_YEAR)
                                + ", " + calLow.get(Calendar.YEAR)
                                +"</td>"
                                + "</tr>\n");
                }

                int numVersions = getVersionsInPeriod(calLow, calHigh).size();
                if (!((numVersions == 0) && (settings.getTvShowEmptyState() == false))) {
                    b.append(sp(in++) + "<tr>\n");
                    b.append(sp(in) + "<td class=\"def_major\">"
                            + Functions.formatDaystamp(
                                    calLow.getTimeInMillis(),
                                    settings.getUserLocale())
                            + "</td>\n");
                    b.append(sp(in) + "<td class=\"def_right\">"
                            + numVersions + "</td>\n");
                }

                if (numVersions > 0) {
                    b.append(sp(in) + "<td class=\"def\">"
                            + "<img style=\"height: 10px; width: 4px;\""
                            + " src=\"/img/icons/16x16/testl.png\">"
                            + "<img style=\"height: 10px; width: "
                            + numVersions
                            + "px;\""
                            + " src=\"/img/icons/16x16/testm.png\">"
                            + "<img style=\"height: 10px; width: 4px;\""
                            + " src=\"/img/icons/16x16/testr.png\">"
                            + "&nbsp;"
                            + "</td>\n");
                    b.append(sp(--in) + "</tr>\n");
                }
                else if (settings.getTvShowEmptyState()) {
                    b.append(sp(in) + "<td class=\"def\">&nbsp;</td>\n");
                    b.append(sp(--in) + "</tr>\n");
                }

                calLow.add(Calendar.DATE, 1);
                calHigh.add(Calendar.DATE, 1);
            }
        }

        b.append(sp(--in) + "</table>\n");

        return b.toString();
    }

    private String lineChart (Calendar calLow, Calendar calHigh) {
        // Construct the chart's dataset
        TimePeriodValuesCollection data = new TimePeriodValuesCollection();
        // TODO:: Quick definition. Rework to support bug and email timelines.
        TimePeriodValues versionsData = new TimePeriodValues("Versions");
        while (calLow.getTimeInMillis() < settings.getTvDateTill()) {
            int numVersions = getVersionsInPeriod(calLow, calHigh).size();
            versionsData.add(
                    new Day(calLow.getTime()),
                    new Double(numVersions));
            calLow.add(Calendar.DATE, 1);
            calHigh.add(Calendar.DATE, 1);
        }
        if (versionsData.getItemCount() > 0)
            data.addSeries(versionsData);

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
            catch (IOException e) { /* Do nothing. */ }
        }

        return null;
    }
}
