<%@ page import="eu.sqooss.webui.*"
%><%@ page import="eu.sqooss.webui.settings.*"
%><%@ page import="eu.sqooss.webui.view.*"
%><%@ page import="eu.sqooss.webui.widgets.*"
%><%
//============================================================================
// Display the selected project's timeline
//============================================================================
if (selectedProject.isValid()) {
%>                <div id="timelineview">
<script type="text/javascript">

var monthDays = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
var dowShortNames = ['S', 'M', 'T', 'W', 'T', 'F', 'S'];

function isLeapYear(year) {
    if ((((year % 4) == 0) && ((year % 100) != 0)) || ((year % 400 == 0)))
        return 1;
    return 0;
}

function renderCalendar(year, month, id) {
    // Retrieve the number of days in the given month
    var daysTotal = monthDays[month];
    // Add one day, if the selected month is February and the year is leap
    if (month == 1)
        daysTotal += isLeapYear(year);
    // Fix the previously selected day number if necessary
    var selDay = document.getElementById('day' + id);
    if (selDay.value > daysTotal)
        selDay.value = daysTotal;

    // Get the "Day of week" number of the first day in the given month
    var objDate = new Date(year, month, 1);
    var firstDow = objDate.getDay();

    //Render the calendar's content
    var content = '<table class=\"cal\">';
    content += '  <tr class="calLabel">';
    for (shortDay = 0; shortDay < 7; shortDay++)
        content += '  <td class="calLabel">' + dowShortNames[shortDay] + '</td>'
    content += '  </tr>';
    content += '  <tr>';
    for(pos = 1; pos <= 42; pos++){
        if ((pos - firstDow > 0) && (pos - firstDow <= daysTotal)) {
            nextDay = pos - firstDow;
            if (nextDay == selDay.value)
                content += '    <td class="calSelected">' + nextDay + '</td>';
            else
                content += '    <td class="calCell"'
                        + ' onClick="javascript: updateDate('
                        + nextDay + ',' + month + ',' + year + ',\'' + id
                        + '\')"' + '">' + nextDay + '</td>';
        } else
            content += '    <td class="calEmpty">&nbsp;</td>';
        if(((pos % 7) == 0) && (pos < 36)) {
            content += '  </tr>';
            content += '  <tr>';
        }
    }
    content += '  </tr>';
    content += '  </table>';

    return content;
}

function updateCalendar(id) {
    var year = document.getElementById('year' + id).value;
    var month = document.getElementById('month' + id).value;
    var calId = 'cal' + id;
    document.getElementById(calId).innerHTML = renderCalendar(year, month, id);
    updateTimeline(id);
}

function updateDate(day, month, year, id) {
    document.getElementById('day' + id).value = day;
    document.getElementById('month' + id).value = month;
    document.getElementById('year' + id).value = year;
    updateCalendar(id);
}

function updateTimeline(id, tillId) {
    var year  = document.getElementById('year'  + id).value;
    var month = document.getElementById('month' + id).value;
    var day   = document.getElementById('day'   + id).value;
    var date  = new Date(year, month, day);
    var timeline = document.getElementById('date'  + id);
    timeline.value = date.getTime();
}

function toggleCalendar(id) {
    var calendar = document.getElementById('cal' + id);
    if(calendar.style.display == "block") {
        calendar.style.display = "none";
    }
    else {
        calendar.style.display = "block";
    }
}
</script>
<%
    // Indentation depth
    in = 9;

    // Retrieve information for the selected project, if necessary
    selectedProject.retrieveData(terrier);

    BaseDataSettings viewConf = settings.getDataSettings(
            SelectedSettings.TIMELINE_DATA_SETTINGS);

    /*
     * Check, if the user has selected to show or hide a certain panel.
     */
    if (request.getParameter("showInfoPanel") != null) {
        if (request.getParameter("showInfoPanel").equals("true"))
            viewConf.setInfoPanelState(true);
        else if (request.getParameter("showInfoPanel").equals("false"))
            viewConf.setInfoPanelState(false);
    }
    if (request.getParameter("showControlPanel") != null) {
        if (request.getParameter("showControlPanel").equals("true"))
            viewConf.setControlPanelState(true);
        else if (request.getParameter("showControlPanel").equals("false"))
            viewConf.setControlPanelState(false);
    }
    if (request.getParameter("showResultPanel") != null) {
        if (request.getParameter("showResultPanel").equals("true"))
            viewConf.setResultPanelState(true);
        else if (request.getParameter("showResultPanel").equals("false"))
            viewConf.setResultPanelState(false);
    }

    /*
     * Check, if the user has selected a time period.
     */
    if (request.getParameter("dateFrom") != null) {
        settings.setTvDateFrom(strToLong(request.getParameter("dateFrom")));
    }
    if (request.getParameter("dateTill") != null) {
        settings.setTvDateTill(strToLong(request.getParameter("dateTill")));
    }

    /*
     * Initialise the data view's object
     */
     TimelineView dataView = new TimelineView(selectedProject);
     dataView.setServletPath(request.getServletPath());
     dataView.setSettings(settings, SelectedSettings.TIMELINE_DATA_SETTINGS);
     dataView.setTerrier(terrier);

     /*
      * Check, if the user has selected a view range
      */
     if (request.getParameter("tvViewRange") != null) {
         settings.setTvViewRange(strToLong(request.getParameter("tvViewRange")));
     }

    // Construct the window's content
    StringBuilder b = new StringBuilder("");
    Window winDataView = new Window();
    winDataView.setTitle("Timeline of project: " + selectedProject.getName());

    // Create a wrapper for the sub-views
    b.append(sp(in++) + "<table style=\"width: 100%\">\n");
    b.append(sp(in++) + "<tr>\n");

    //============================================================
    // Display the Info and Command panels
    //============================================================
    if (viewConf.getInfoPanelState()
            || viewConf.getControlPanelState()) {
        if (viewConf.getResultPanelState() == false)
            b.append(sp(in++) + "<td class=\"dvPanelLeft\">\n");
        else
            b.append(sp(in++) + "<td class=\"dvPanelLeft\">\n");

        // =======================================================
        // Construct and render the Info panel
        // =======================================================
        if (viewConf.getInfoPanelState()) {
            // Construct the window's title icons
            Window winInfoPanel = new Window();
            winVisible = "showInfoPanel";
            icoCloseWin.setParameter(winVisible);
            icoCloseWin.setValue("false");
            winInfoPanel.addTitleIcon(icoCloseWin);

            // Construct the windows's toolbar

            // Construct the window's content
            winInfoPanel.setContent(dataView.getInfo(in + 2));

            // Display the window
            winInfoPanel.setTitle("Information");
            b.append(winInfoPanel.render(in));
        }
        // =======================================================
        // Construct and render the Control panel
        // =======================================================
        if (viewConf.getControlPanelState()) {
            // Construct the window's title icons
            Window winControlPanel = new Window();
            winVisible = "showControlPanel";
            icoCloseWin.setParameter(winVisible);
            icoCloseWin.setValue("false");
            winControlPanel.addTitleIcon(icoCloseWin);

            // Construct the windows's toolbar

            // Construct the window's content
            winControlPanel.setContent(dataView.getControls(in + 2));

            // Display the window
            winControlPanel.setTitle("Control center");
            b.append(winControlPanel.render(in));
        }

        b.append(sp(--in) + "</td>\n");
    }
    //============================================================
    // Display the Result panel
    //============================================================
    if (viewConf.getResultPanelState()) {
        if (viewConf.getInfoPanelState()
                || viewConf.getControlPanelState())
            b.append(sp(in++) + "<td class=\"dvPanelRight\""
                    + " style=\"padding-left: 5px;\">\n");
        else
            b.append(sp(in++) + "<td class=\"dvPanelRight\">\n");

        // =======================================================
        // Construct and render the Result panel
        // =======================================================
        if (viewConf.getResultPanelState()) {
            // Construct the window's title icons
            Window winResultPanel = new Window();
            winVisible = "showResultPanel";
            icoCloseWin.setParameter(winVisible);
            icoCloseWin.setValue("false");
            winResultPanel.addTitleIcon(icoCloseWin);

            SelectInput icoDisplaySelector = new SelectInput();
            icoDisplaySelector.setPath(request.getServletPath());
            icoDisplaySelector.setParameter("tvViewRange");
            icoDisplaySelector.setLabelText("View:");
            icoDisplaySelector.setButtonText("Apply");
            icoDisplaySelector.addOption("1", "Daily");
            icoDisplaySelector.addOption("2", "Weekly");
            icoDisplaySelector.addOption("3", "Monthly");
            winResultPanel.addToolIcon(icoDisplaySelector);
            
            // Construct the window's content
            dataView.setChartType(viewConf.getChartType());
            winResultPanel.setContent(dataView.getHtml(in + 2));

            // Display the window
            winResultPanel.setTitle("Project timeline");
            b.append(winResultPanel.render(in));
        }

        b.append(sp(--in) + "</td>\n");
    }

    // Close the sub-views wrapper
    b.append(sp(--in) + "</tr>\n");
    b.append(sp(--in) + "</table>\n");

%><%@ include file="/inc/DataViewIcons.jsp"
%><%

    /*
     * Display the window
     */
    winDataView.setContent(b.toString());
    out.print(winDataView.render(in));
}
//============================================================================
// Let the user choose a project, if none was selected
//============================================================================
else {
%><%@ include file="/inc/SelectProject.jsp"
%><%
}
%>                </div>
