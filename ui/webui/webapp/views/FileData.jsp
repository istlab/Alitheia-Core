<%@ page import="eu.sqooss.webui.*"
%><%@ page import="eu.sqooss.webui.settings.*"
%><%@ page import="eu.sqooss.webui.view.*"
%><%@ page import="eu.sqooss.webui.widgets.*"
%><%
//============================================================================
//Lets the user select and display file based evaluation results
//============================================================================

    // Indentation depth
    in = 11;

    if (selFile != null) {
        BaseDataSettings viewConf = settings.getDataSettings(
                SelectedSettings.FILE_DATA_SETTINGS);

%><%@ include file="/inc/DataViewParameters.jsp"
%><%

        /*
         * Initialise the data view's object
         */
        FileDataView dataView =
                new FileDataView(selectedProject, selFile.getId());
        dataView.setServletPath(request.getServletPath());
        dataView.setSettings(settings, SelectedSettings.FILE_DATA_SETTINGS);
        dataView.setTerrier(terrier);

        // Retrieve the highlighted metric (if any)
        if (request.getParameter("highMetric") != null) {
            dataView.setHighlightedMetric(
                    request.getParameter("highMetric"));
        }

        // Construct the window's content
        StringBuilder b = new StringBuilder("");
        Window winDataView = new Window();
        if (selFile.getIsDirectory())
            winDataView.setTitle("Results in folder "
                + selFile.getShortName());
        else
            winDataView.setTitle("Results in file "
                + selFile.getShortName());

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

%><%@ include file="/inc/DataViewIcons.jsp"
%><%
        icoCloseWin.setParameter("fid");
        icoCloseWin.setValue("none");

        winDataView.setContent(b.toString());
        out.print(winDataView.render(in - 2));
    }
    else
        out.print(sp(in) + Functions.information(
                "Invalid file or folder."));
%>