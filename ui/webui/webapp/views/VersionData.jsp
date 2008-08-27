<%@ page import="eu.sqooss.webui.*"
%><%@ page import="eu.sqooss.webui.datatype.*"
%><%@ page import="eu.sqooss.webui.view.*"
%><%@ page import="eu.sqooss.webui.widgets.*"
%><%@ page import="eu.sqooss.webui.Metric.MetricActivator"
%><%@ page import="eu.sqooss.webui.Metric.MetricType"
%><%
//============================================================================
// Lets the user select and display version based evaluation results
//============================================================================
if (selectedProject.isValid()) {
%>                <div id="fileslist">
<%
    // Indentation depth
    in = 11;

    // Retrieve information for the selected project, if necessary
    selectedProject.retrieveData(terrier);

    if (selectedProject.getVersionsCount() > 0) {
        // Retrieve the list of selected versions (if any)
        if (request.getParameter("vvvvid") != null) {
            settings.setVvvSelectedVersions(
                request.getParameterValues("vvvvid"));
        }

        // Retrieve the list of selected version metrics (if any)
        if (request.getParameter("vvvmid") != null) {
            settings.setVvvSelectedMetrics(
                request.getParameterValues("vvvmid"));
        }

        /*
         * Check, if the user has selected to show or hide a certain panel.
         */
        if (request.getParameter("showVvvInfoPanel") != null) {
            if (request.getParameter("showVvvInfoPanel").equals("true"))
                settings.setShowVvvInfoPanel(true);
            else if (request.getParameter("showVvvInfoPanel").equals("false"))
                settings.setShowVvvInfoPanel(false);
        }
        if (request.getParameter("showVvvControlPanel") != null) {
            if (request.getParameter("showVvvControlPanel").equals("true"))
                settings.setShowVvvControlPanel(true);
            else if (request.getParameter("showVvvControlPanel").equals("false"))
                settings.setShowVvvControlPanel(false);
        }
        if (request.getParameter("showVvvResultPanel") != null) {
            if (request.getParameter("showVvvResultPanel").equals("true"))
                settings.setShowVvvResultPanel(true);
            else if (request.getParameter("showVvvResultPanel").equals("false"))
                settings.setShowVvvResultPanel(false);
        }

        /*
         * Check, if the user has switched to another type for displaying
         * the metric evaluation results.
         */
        if (request.getParameter("vvvchart") != null) {
            Long chartType = getId(request.getParameter("vvvchart"));
            if (chartType != null)
                settings.setVvvChartType(chartType.intValue());
            /*
             * Show the Info and Control panels after the user switched
             * (or forced from the code above) to a tabular display of
             * evaluation results.
             */
            if (chartType == VersionVerboseView.TABLE_CHART) {
                settings.setShowVvvInfoPanel(true);
                settings.setShowVvvControlPanel(true);
            }
        }

        /*
         * Force the tabular display of evaluation results in case the
         * versions selection contains only one version.
         */
        if ((settings.getVvvSelectedVersions() != null)
            && (settings.getVvvSelectedVersions().length == 1)
            && (settings.getVvvChartType() == VersionVerboseView.LINE_CHART)) {
            settings.setVvvChartType(VersionVerboseView.TABLE_CHART);
            if ((settings.getShowVvvInfoPanel() == false)
                && (settings.getShowVvvControlPanel() == false)) {
                settings.setShowVvvInfoPanel(true);
                settings.setShowVvvControlPanel(true);
            }
        }

        /*
         * By default, force the Info and Control panels hide, in case the
         * chart display is active, unless the user had explicitly
         * requested their visualisation.
         */ 
        if ((settings.getVvvChartType() == VersionVerboseView.LINE_CHART)
            && (request.getParameter("showVvvControlPanel") == null)
            && (request.getParameter("showVvvInfoPanel") == null)) {
            settings.setShowVvvInfoPanel(false);
            settings.setShowVvvControlPanel(false);
        }

        /*
         * Keep showing the Info and Control panels, untill both metric AND
         * version have been selected.
         */
        if ((settings.getVvvSelectedVersions() == null)
            || (settings.getVvvSelectedMetrics() == null)) {
            settings.setShowVvvInfoPanel(true);
            settings.setShowVvvControlPanel(true);
        }

        /*
         * Initialise the verbose view's object
         */
        VersionVerboseView verboseView =
            new VersionVerboseView(selectedProject);
        verboseView.setServletPath(request.getServletPath());
        verboseView.setSettings(settings);
        verboseView.setTerrier(terrier);

        // Retrieve the highlighted metric (if any)
        if (request.getParameter("vvvsm") != null) {
            verboseView.setHighlightedMetric(request.getParameter("vvvsm"));
        }

        // Construct the window's content
        StringBuilder b = new StringBuilder("");
        Window winVersionVerbose = new Window();
        winVersionVerbose.setTitle("Versions in project "
            + selectedProject.getName());

        // Create a wrapper for the sub-views
        b.append(sp(in++) + "<table style=\"width: 100%\">\n");
        b.append(sp(in++) + "<tr>\n");
        //============================================================
        // Display the Info and Command panels
        //============================================================
        if (settings.getShowVvvInfoPanel()
                || settings.getShowVvvControlPanel()) {
            if (settings.getShowVvvResultPanel() == false)
                b.append(sp(in++) + "<td class=\"vvvleft\">\n");
            else
                b.append(sp(in++) + "<td class=\"vvvleft\">\n");

            // =======================================================
            // Display the version info screen
            // =======================================================
            if (settings.getShowVvvInfoPanel()) {
                // Construct the window's title icons
                Window winInfoScreen = new Window();
                winVisible = "showVvvInfoPanel";
                icoCloseWin.setParameter(winVisible);
                icoCloseWin.setValue("false");
                winInfoScreen.addTitleIcon(icoCloseWin);
                // Construct the window's content
                winInfoScreen.setContent(verboseView.getInfo(in + 2));
                // Display the window
                winInfoScreen.setTitle("Metadata");
                b.append(winInfoScreen.render(in));
            }
            // =======================================================
            // Display the version command screen
            // =======================================================
            if (settings.getShowVvvControlPanel()) {
                // Construct the window's title icons
                Window winCommandScreen = new Window();
                winVisible = "showVvvControlPanel";
                icoCloseWin.setParameter(winVisible);
                icoCloseWin.setValue("false");
                winCommandScreen.addTitleIcon(icoCloseWin);
                // Construct the window's content
                winCommandScreen.setContent(verboseView.getControls(in + 2));
                // Display the window
                winCommandScreen.setTitle("Control center");
                b.append(winCommandScreen.render(in));
            }

            b.append(sp(--in) + "</td>\n");
        }
        //============================================================
        // Display the Results panel
        //============================================================
        if (settings.getShowVvvResultPanel()) {
            if (settings.getShowVvvInfoPanel()
                    || settings.getShowVvvControlPanel())
                b.append(sp(in++) + "<td class=\"vvvright\""
                    + " style=\"padding-left: 5px;\">\n");
            else
                b.append(sp(in++) + "<td class=\"vvvright\">\n");

            // =======================================================
            // Display the version results
            // =======================================================
            if (settings.getShowVvvResultPanel()) {
                // Construct the window's title icons
                Window winChartsScreen = new Window();
                winVisible = "showVvvResultPanel";
                icoCloseWin.setParameter(winVisible);
                icoCloseWin.setValue("false");
                winChartsScreen.addTitleIcon(icoCloseWin);
                // Construct the window's content
                verboseView.setChartType(settings.getVvvChartType());
                winChartsScreen.setContent(verboseView.getHtml(in + 2));
                // Display the window
                winChartsScreen.setTitle("Results");
                b.append(winChartsScreen.render(in));
            }

            b.append(sp(--in) + "</td>\n");
        }
        // Close the sub-views wrapper
        b.append(sp(--in) + "</tr>\n");
        b.append(sp(--in) + "</table>\n");

        // Toolbar icon - Info panel
        WinIcon icoInfoWin = new WinIcon();
        icoInfoWin.setPath(request.getServletPath());
        icoInfoWin.setParameter("showVvvInfoPanel");
        icoInfoWin.setValue("" + !settings.getShowVvvInfoPanel());
        if (settings.getShowVvvInfoPanel()) {
            icoInfoWin.setAlt("Hide info panel");
            icoInfoWin.setStatus(false);
        }
        else {
            icoInfoWin.setAlt("Show info panel");
        }
        icoInfoWin.setImage("/img/icons/16x16/infopanel.png");
        winVersionVerbose.addToolIcon(icoInfoWin);

        // Toolbar icon - Control panel
        WinIcon icoControlWin = new WinIcon();
        icoControlWin.setPath(request.getServletPath());
        icoControlWin.setParameter("showVvvControlPanel");
        icoControlWin.setValue("" + !settings.getShowVvvControlPanel());
        if (settings.getShowVvvControlPanel()) {
            icoControlWin.setAlt("Hide control panel");
            icoControlWin.setStatus(false);
        }
        else {
            icoControlWin.setAlt("Show control panel");
        }
        icoControlWin.setImage("/img/icons/16x16/controlpanel.png");
        winVersionVerbose.addToolIcon(icoControlWin);

        // Toolbar icon - result panel
        WinIcon icoResWin = new WinIcon();
        icoResWin.setPath(request.getServletPath());
        icoResWin.setParameter("showVvvResultPanel");
        icoResWin.setValue("" + !settings.getShowVvvResultPanel());
        if (settings.getShowVvvResultPanel()) {
            icoResWin.setAlt("Hide results panel");
            icoResWin.setStatus(false);
        }
        else {
            icoResWin.setAlt("Show results panel");
        }
        icoResWin.setImage("/img/icons/16x16/resultpanel.png");
        winVersionVerbose.addToolIcon(icoResWin);

        // Put a separator
        winVersionVerbose.addToolIcon(icoSeparator);

        // Results icon - tabular display
        WinIcon icoTabular = new WinIcon();
        icoTabular.setPath(request.getServletPath());
        icoTabular.setParameter("vvvchart");
        icoTabular.setValue("" + VersionVerboseView.TABLE_CHART);
        icoTabular.setAlt("Show results in a table");
        icoTabular.setImage("/img/icons/16x16/tablechart.png");
        if (settings.getVvvChartType() == VersionVerboseView.TABLE_CHART)
            icoTabular.setStatus(false);
        winVersionVerbose.addToolIcon(icoTabular);

        // Results icon - line chart display
        WinIcon icoLineChart = new WinIcon();
        icoLineChart.setPath(request.getServletPath());
        icoLineChart.setParameter("vvvchart");
        icoLineChart.setValue("" + VersionVerboseView.LINE_CHART);
        icoLineChart.setAlt("Show results as a line chart");
        icoLineChart.setImage("/img/icons/16x16/linechart.png");
        if (settings.getVvvChartType() == VersionVerboseView.LINE_CHART)
            icoLineChart.setStatus(false);
        winVersionVerbose.addToolIcon(icoLineChart);

        /*
         * Disable the results display buttons, depending on the number of
         * selected versions and metrics.
         */
        if ((settings.getVvvSelectedVersions() == null)
            || (settings.getVvvSelectedMetrics() == null)) {
            icoTabular.setStatus(false);
            icoLineChart.setStatus(false);
        }
        else if (settings.getVvvSelectedVersions().length == 1) {
            icoLineChart.setStatus(false);
        }

        icoCloseWin.setParameter(null);
        winVersionVerbose.addTitleIcon(icoCloseWin);
        winVersionVerbose.setContent(b.toString());
        out.print(winVersionVerbose.render(in - 2));
    }
    else
        out.print(sp(in) + Functions.information(
            "This project has no versions."));
}
//============================================================================
// Let the user choose a project, if none was selected
//============================================================================
else {
%>                <div id="projectslist">
<%
    in = 9;
    // Check if the user has selected a project
    if (request.getParameter("pid") != null)
        ProjectsListView.setCurrentProject(
            strToLong(request.getParameter("pid")));

    // Retrieve the list of projects from the connected SQO-OSS framework
    ProjectsListView.retrieveData(terrier);
    if (ProjectsListView.hasProjects()) {
        ProjectsListView.setServletPath(request.getServletPath());
        Window winPrjList = new Window();
        winPrjList.setTitle("Evaluated projects");
        winPrjList.setContent(ProjectsListView.getHtml(in + 2));
        out.print(winPrjList.render(in));
    }
    // No evaluated projects found
    else {
        out.print(sp(in) + Functions.information(
                "Unable to find any evaluated projects."));
    }
}
%>                </div>
