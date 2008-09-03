<%@ page import="eu.sqooss.webui.*"
%><%@ page import="eu.sqooss.webui.settings.*"
%><%@ page import="eu.sqooss.webui.view.*"
%><%@ page import="eu.sqooss.webui.widgets.*"
%><%
//============================================================================
// Lets the user select and display developer based evaluation results
//============================================================================
if (selectedProject.isValid()) {
%>                <div id="developerslist">
<%
    // Indentation depth
    in = 11;

    // Retrieve information for the selected project, if necessary
    selectedProject.retrieveData(terrier);

    if (selectedProject.getDevelopersCount() > 0) {
        BaseDataSettings viewConf = settings.getDataSettings(
                SelectedSettings.DEVELOPERS_DATA_SETTINGS);

%><%@ include file="/inc/DataViewParameters.jsp"
%><%

        /*
         * Initialise the date view's object
         */
        DeveloperDataView dataView =
            new DeveloperDataView(selectedProject);
        dataView.setServletPath(request.getServletPath());
        dataView.setSettings(settings);
        dataView.setTerrier(terrier);

        // Retrieve the highlighted metric (if any)
        if (request.getParameter("highMetric") != null) {
            dataView.setHighlightedMetric(
                    request.getParameter("highMetric"));
        }

        // Construct the window's content
        StringBuilder b = new StringBuilder("");
        Window winDataView = new Window();
        winDataView.setTitle("Developers in project "
                + selectedProject.getName());

        // Create a wrapper for the sub-views
        b.append(sp(in++) + "<table style=\"width: 100%\">\n");
        b.append(sp(in++) + "<tr>\n");

        //============================================================
        // Display the Info and Control panels
        //============================================================
        if (viewConf.getInfoPanelState()
                || viewConf.getControlPanelState()) {
            if (viewConf.getResultPanelState() == false)
                b.append(sp(in++) + "<td class=\"vvvleft\">\n");
            else
                b.append(sp(in++) + "<td class=\"vvvleft\">\n");

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
                SelectInput icoShowResource = new SelectInput();
                icoShowResource.setPath(request.getServletPath());
                icoShowResource.setParameter("showResource");
                icoShowResource.setLabelText("Username: ");
                icoShowResource.setButtonText("Show");
                for (Developer developer : selectedProject.getDevelopers())
                icoShowResource.addOption(
                        developer.getUsername(),
                        developer.getUsername());
                winInfoPanel.addToolIcon(icoShowResource);

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
                SelectInput icoAddResource = new SelectInput();
                icoAddResource.setPath(request.getServletPath());
                icoAddResource.setParameter("addResource");
                icoAddResource.setLabelText("Username: ");
                icoAddResource.setButtonText("Add");
                for (Developer developer : selectedProject.getDevelopers())
                icoAddResource.addOption(
                        developer.getUsername(),
                        developer.getUsername());
                winControlPanel.addToolIcon(icoAddResource);

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
                b.append(sp(in++) + "<td class=\"vvvright\""
                    + " style=\"padding-left: 5px;\">\n");
            else
                b.append(sp(in++) + "<td class=\"vvvright\">\n");

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

                // Construct the window's content
                dataView.setChartType(viewConf.getChartType());
                winResultPanel.setContent(dataView.getHtml(in + 2));

                // Display the window
                winResultPanel.setTitle("Evaluation results");
                b.append(winResultPanel.render(in));
            }

            b.append(sp(--in) + "</td>\n");
        }

        // Close the sub-views wrapper
        b.append(sp(--in) + "</tr>\n");
        b.append(sp(--in) + "</table>\n");

        // Toolbar icon - Info panel
        WinIcon icoInfoPanel = new WinIcon();
        icoInfoPanel.setPath(request.getServletPath());
        icoInfoPanel.setParameter("showInfoPanel");
        icoInfoPanel.setValue("" + !viewConf.getInfoPanelState());
        if (viewConf.getInfoPanelState()) {
            icoInfoPanel.setAlt("Hide info panel");
            icoInfoPanel.setStatus(false);
        }
        else {
            icoInfoPanel.setAlt("Show info panel");
        }
        icoInfoPanel.setImage("/img/icons/16x16/infopanel.png");
        winDataView.addToolIcon(icoInfoPanel);

        // Toolbar icon - Control panel
        WinIcon icoControlPanel = new WinIcon();
        icoControlPanel.setPath(request.getServletPath());
        icoControlPanel.setParameter("showControlPanel");
        icoControlPanel.setValue("" + !viewConf.getControlPanelState());
        if (viewConf.getControlPanelState()) {
            icoControlPanel.setAlt("Hide control panel");
            icoControlPanel.setStatus(false);
        }
        else {
            icoControlPanel.setAlt("Show control panel");
        }
        icoControlPanel.setImage("/img/icons/16x16/controlpanel.png");
        winDataView.addToolIcon(icoControlPanel);

        // Toolbar icon - Result panel
        WinIcon icoResultPanel = new WinIcon();
        icoResultPanel.setPath(request.getServletPath());
        icoResultPanel.setParameter("showResultPanel");
        icoResultPanel.setValue("" + !viewConf.getResultPanelState());
        if (viewConf.getResultPanelState()) {
            icoResultPanel.setAlt("Hide results panel");
            icoResultPanel.setStatus(false);
        }
        else {
            icoResultPanel.setAlt("Show results panel");
        }
        icoResultPanel.setImage("/img/icons/16x16/resultpanel.png");
        winDataView.addToolIcon(icoResultPanel);

        // Put a separator
        winDataView.addToolIcon(icoSeparator);

        // Results icon - tabular display
        WinIcon icoTabular = new WinIcon();
        icoTabular.setPath(request.getServletPath());
        icoTabular.setParameter("chartType");
        icoTabular.setValue("" + AbstractDataView.TABLE_CHART);
        icoTabular.setAlt("Show results in a table");
        icoTabular.setImage("/img/icons/16x16/tablechart.png");
        if (viewConf.getChartType() == AbstractDataView.TABLE_CHART)
            icoTabular.setStatus(false);
        winDataView.addToolIcon(icoTabular);

        // Results icon - line chart display
        WinIcon icoLineChart = new WinIcon();
        icoLineChart.setPath(request.getServletPath());
        icoLineChart.setParameter("chartType");
        icoLineChart.setValue("" + AbstractDataView.LINE_CHART);
        icoLineChart.setAlt("Show results as a line chart");
        icoLineChart.setImage("/img/icons/16x16/linechart.png");
        if (viewConf.getChartType() == AbstractDataView.LINE_CHART)
            icoLineChart.setStatus(false);
        winDataView.addToolIcon(icoLineChart);

        // Results icon - bar chart display
        WinIcon icoBarChart = new WinIcon();
        icoBarChart.setPath(request.getServletPath());
        icoBarChart.setParameter("chartType");
        icoBarChart.setValue("" + AbstractDataView.BAR_CHART);
        icoBarChart.setAlt("Show results as a bar chart");
        icoBarChart.setImage("/img/icons/16x16/bar-chart.png");
        if (viewConf.getChartType() == AbstractDataView.BAR_CHART)
            icoBarChart.setStatus(false);
        winDataView.addToolIcon(icoBarChart);

        /*
         * Disable the results display buttons, depending on the number of
         * selected resources and metrics.
         */
        if ((viewConf.getSelectedResources() == null)
                || (viewConf.getSelectedMetrics() == null)) {
            icoTabular.setStatus(false);
            icoLineChart.setStatus(false);
            icoBarChart.setStatus(false);
        }
        else if (viewConf.getSelectedResources().length == 1) {
            icoLineChart.setStatus(false);
        }

        icoCloseWin.setParameter(null);
        winDataView.addTitleIcon(icoCloseWin);
        winDataView.setContent(b.toString());
        out.print(winDataView.render(in - 2));
    }
    else
        out.print(sp(in) + Functions.information(
            "This project has no developers."));
}
//============================================================================
// Let the user choose a project, if none was selected
//============================================================================
else {
%><%@ include file="/inc/SelectProject.jsp"
%><%
}
%>                </div>
