<%@ page import="eu.sqooss.webui.*"
%><%@ page import="eu.sqooss.webui.settings.*"
%><%@ page import="eu.sqooss.webui.view.*"
%><%@ page import="eu.sqooss.webui.widgets.*"
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
        BaseDataSettings viewConf = settings.getDataSettings(
                SelectedSettings.VERSION_DATA_SETTINGS);

%><%@ include file="/inc/DataViewParameters.jsp"
%><%

        /*
         * Check if the user switched to tagged versions input only
         */
        if (request.getParameter("onlyTagged") != null) {
            if (request.getParameter("onlyTagged").equals("true"))
                settings.setVdvInputTaggedOnly(true);
            else if (request.getParameter("onlyTagged").equals("false"))
                settings.setVdvInputTaggedOnly(false);
            // Force the numeric version input in case of "untagged" project
            if (selectedProject.getTaggedVersions().size() < 1)
                settings.setVdvInputTaggedOnly(false);
        }

        /*
         * Initialise the data view's object
         */
        VersionDataView dataView =
            new VersionDataView(selectedProject);
        dataView.setServletPath(request.getServletPath());
        dataView.setSettings(settings, SelectedSettings.VERSION_DATA_SETTINGS);
        dataView.setTerrier(terrier);

        // Retrieve the highlighted metric (if any)
        if (request.getParameter("highMetric") != null) {
            dataView.setHighlightedMetric(
                    request.getParameter("highMetric"));
        }

        // Construct the window's content
        StringBuilder b = new StringBuilder("");
        Window winDataView = new Window();
        winDataView.setTitle("Versions in project "
                + selectedProject.getName());

        // Create a wrapper for the sub-views
        b.append(sp(in++) + "<table style=\"width: 100%\">\n");
        b.append(sp(in++) + "<tr>\n");

        //============================================================
        // Display the Info and Command panels
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
                TextInput icoShowVersion = new TextInput();
                icoShowVersion.setPath(request.getServletPath());
                icoShowVersion.setParameter("showResource");
                icoShowVersion.setText("Show version:");
                winInfoPanel.addToolIcon(icoShowVersion);

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
                WinIcon icoVersionRange = new WinIcon();
                icoVersionRange.setPath(request.getServletPath());
                icoVersionRange.setParameter("onlyTagged");
                if (settings.getVdvInputTaggedOnly()) {
                    icoVersionRange.setAlt(
                            "Switch to a manual version number input.");
                    icoVersionRange.setValue("false");
                    icoVersionRange.setImage(
                            "/img/icons/16x16/add_version.png");
                }
                else {
                    icoVersionRange.setAlt(
                            "Select from a list of tagged project versions.");
                    icoVersionRange.setValue("true");
                    icoVersionRange.setImage(
                            "/img/icons/16x16/add_tagged.png");
                    // Check, if this project has any tagged version
                    if (selectedProject.getTaggedVersions().size() < 1) {
                        icoVersionRange.setStatus(false);
                    }
                }
                winControlPanel.addToolIcon(icoVersionRange);

                winControlPanel.addToolIcon(icoSeparator);

                if (settings.getVdvInputTaggedOnly()) {
                    SelectInput icoVersionSelector = new SelectInput();
                    icoVersionSelector.setPath(request.getServletPath());
                    icoVersionSelector.setParameter("addResource");
                    icoVersionSelector.setLabelText("Tagged:");
                    icoVersionSelector.setButtonText("Add");
                    for (TaggedVersion tag : selectedProject.getTaggedVersions())
                        if (viewConf.isSelectedResource(
                                tag.getNumber().toString()) == false)
                            icoVersionSelector.addOption(
                                    tag.getNumber().toString(),
                                    tag.getNumber().toString());
                    winControlPanel.addToolIcon(icoVersionSelector);
                }
                else {
                    TextInput icoVersionSelector = new TextInput();
                    icoVersionSelector.setPath(request.getServletPath());
                    icoVersionSelector.setParameter("addResource");
                    icoVersionSelector.setText("Add version:");
                    winControlPanel.addToolIcon(icoVersionSelector);
                }

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

%><%@ include file="/inc/DataViewIcons.jsp"
%><%

        winDataView.setContent(b.toString());
        out.print(winDataView.render(in - 2));
    }
    else
        out.print(sp(in) + Functions.information(
            "This project has no versions."));
}
//============================================================================
// Let the user choose a project, if none was selected
//============================================================================
else {
%><%@ include file="/inc/SelectProject.jsp"
%><%
}
%>                </div>
