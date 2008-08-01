<%@ page import="eu.sqooss.webui.*"
%><%@ page import="eu.sqooss.webui.view.*"
%><%@ page import="eu.sqooss.webui.widgets.*"
%><%
if (selectedProject.isValid()) {
%>                <div id="selectedproject">
<%
    // Update the Message Box
    if (msg.length() > 0) msg += "<br/>";
    msg += "<strong>Project:</strong> " + selectedProject.getName();
    msg += "<span class=\"forget\"><a href=\"?pid=none\">(forget)</a></span>";
    if (selectedProject.getCurrentVersion() != null) {
        msg += "<br /><strong>Version:</strong> "
            + selectedProject.getCurrentVersion().getNumber();
    }
    else {
        msg += "<br />No versions recorded.";
    }
%>                  <table>
                    <tr>
                      <td valign="top" style="width: 60%; padding-bottom: 0;">
<%
    //========================================================================
    // Display the selected project's metadata
    //========================================================================
    // Indentation depth
    in = 14;
    // Check if the user has requested to show/hide this view
    winVisible = "showPVMetadata";
    if (request.getParameter(winVisible) != null) {
        if (request.getParameter(winVisible).equals(WinIcon.MAXIMIZE))
            settings.setShowPVMetadata(true);
        else if (request.getParameter(winVisible).equals(WinIcon.MINIMIZE))
            settings.setShowPVMetadata(false);
    }
    Window winPrjMetadata = new Window();
    // Construct the window's title icons
    if (settings.getShowPVMetadata())
        winPrjMetadata.addTitleIcon(WinIcon.minimize(
            request.getServletPath(), winVisible));
    else
        winPrjMetadata.addTitleIcon(WinIcon.maximize(
            request.getServletPath(), winVisible));
    // Construct the window's content
    if (settings.getShowPVMetadata()) {
        ProjectInfoView infoView = new ProjectInfoView(selectedProject);
        winPrjMetadata.setContent(infoView.getHtml(in));
    }
    // Display the window
    winPrjMetadata.setTitle(selectedProject.getName() + " overview");
    out.print(winPrjMetadata.render(in - 2));

    //========================================================================
    // Display the list of developers that are working on this project
    //========================================================================
    // Indentation depth
    in = 14;
    // Check if the user has requested to show/hide this view
    winVisible = "showPVDevelopers";
    if (request.getParameter(winVisible) != null) {
        if (request.getParameter(winVisible).equals(WinIcon.MAXIMIZE))
            settings.setShowPVDevelopers(true);
        else if (request.getParameter(winVisible).equals(WinIcon.MINIMIZE))
            settings.setShowPVDevelopers(false);
    }
    Window winPrjDevelopers = new Window();
    // Construct the window's title icons
    if (settings.getShowPVDevelopers())
        winPrjDevelopers.addTitleIcon(WinIcon.minimize(
            request.getServletPath(), winVisible));
    else
        winPrjDevelopers.addTitleIcon(WinIcon.maximize(
            request.getServletPath(), winVisible));
    // Construct the window's content
    if (settings.getShowPVDevelopers()) {
        // Prepare the developers view
        selectedProject.setTerrier(terrier);
        DevelopersListView developersView =
        new DevelopersListView(selectedProject.getDevelopers());
        // Display the developers
        winPrjDevelopers.setContent(developersView.getHtml(in));
    }
    // Display the window
    winPrjDevelopers.setTitle("Developers");
    out.print(winPrjDevelopers.render(in - 2));
%>                      </td>
                      <td valign="top"style="width: 40%; padding-bottom: 0; padding-left: 5px;">
<%
    //========================================================================
    // Display the selected project's version statistic and selector
    //========================================================================
    // Indentation depth
    in = 14;
    // Check if the user has requested to show/hide this view
    winVisible = "showPVVersions";
    if (request.getParameter(winVisible) != null) {
        if (request.getParameter(winVisible).equals(WinIcon.MAXIMIZE))
            settings.setShowPVVersions(true);
        else if (request.getParameter(winVisible).equals(WinIcon.MINIMIZE))
            settings.setShowPVVersions(false);
    }
    Window winPrjVersions = new Window();
    // Construct the window's title icons
    if (settings.getShowPVVersions())
        winPrjVersions.addTitleIcon(WinIcon.minimize(
            request.getServletPath(), winVisible));
    else
        winPrjVersions.addTitleIcon(WinIcon.maximize(
            request.getServletPath(), winVisible));
    // Construct the window content
    if (settings.getShowPVVersions()) {
        if (selectedProject.getCurrentVersion() != null) {
            StringBuilder b = new StringBuilder("");
            Version currentVersion = selectedProject.getCurrentVersion();
            Long versionNum = currentVersion.getNumber();
            Long versionId = currentVersion.getId();
            // Display the first and last known project versions
            if (selectedProject.getFirstVersion() != null) {
                if (!selectedProject.getFirstVersion().equals(
                    selectedProject.getLastVersion())) {
                    b.append(sp(in) + "<strong>Versions:</strong> "
                        + selectedProject.getFirstVersion().getNumber()
                        + " - "
                        + selectedProject.getLastVersion().getNumber()
                        + "\n");
                    b.append(sp(in) + "<br/>\n");
                    b.append(sp(in) + "<strong>Total:</strong> "
                        + selectedProject.getVersionsCount() + "\n");
                } else {
                    b.append(sp(in) + "<strong>Version:</strong> "
                        + selectedProject.getFirstVersion().getNumber()
                        + "\n");
                }
            }
            else
                b.append(sp(in) + Functions.warning("No versions found."));
            // Show the version selector
            b.append(sp(in) + "<br/>\n");
            // Display any accumulate errors
            if (inputError != null)
                b.append(sp(in) + Functions.error(inputError));
            b.append(sp(in) + "<strong>"
                + "Choose the version you want to display:"
                + "</strong>\n");
            b.append(sp(in) + "<br/>\n");
            b.append(versionSelector(selectedProject, in));
            winPrjVersions.setContent(b.toString());
        }
        else
            winPrjVersions.setContent(sp(in) + Functions.warning(
                "No versions found."));
    }
    // Display the window
    winPrjVersions.setTitle("Versions");
    out.print(winPrjVersions.render(in - 2));

    //========================================================================
    // Display the source file statistic of the selected project
    //========================================================================
    // Indentation depth
    in = 14;
    // Check if the user has requested to show/hide this view
    winVisible = "showPVFileStat";
    if (request.getParameter(winVisible) != null) {
        if (request.getParameter(winVisible).equals(WinIcon.MAXIMIZE))
            settings.setShowPVFileStat(true);
        else if (request.getParameter(winVisible).equals(WinIcon.MINIMIZE))
            settings.setShowPVFileStat(false);
    }
    Window winPrjFiles = new Window();
    // Construct the window's title icons
    if (settings.getShowPVFileStat())
        winPrjFiles.addTitleIcon(WinIcon.minimize(
            request.getServletPath(), winVisible));
    else
        winPrjFiles.addTitleIcon(WinIcon.maximize(
            request.getServletPath(), winVisible));
    // Construct the window's content
    if (settings.getShowPVFileStat()) {
        if (selectedProject.getCurrentVersion() != null)
            winPrjFiles.setContent(
                selectedProject.getCurrentVersion().fileStats(in));
        else
            winPrjFiles.setContent(
                sp(in) + Functions.warning("No versions found."));
    }
    // Display the window
    winPrjFiles.setTitle("Files"
        + ((selectedProject.getCurrentVersion() != null)
            ? " in version " + selectedProject.getCurrentVersion().getNumber()
            : ""));
    out.print(winPrjFiles.render(in - 2));
%>                      </td>
                    </tr>
                    <tr>
                      <td valign="top" colspan="2" style="padding-top: 0;">
<%
    //========================================================================
    // Display the list of metrics that were evaluated on this project
    //========================================================================
    // Indentation depth
    in = 14;
    // Check if the user has requested a metrics refresh
    if (request.getParameter("refreshPrjMetrics") != null) {
        selectedProject.flushMetrics();
    }
    // Check if the user has requested to show/hide this view
    winVisible = "showPVMetrics";
    if (request.getParameter(winVisible) != null) {
        if (request.getParameter(winVisible).equals(WinIcon.MAXIMIZE))
            settings.setShowPVMetrics(true);
        else if (request.getParameter(winVisible).equals(WinIcon.MINIMIZE))
            settings.setShowPVMetrics(false);
    }
    Window winPrjMetrics = new Window();
    // Construct the window's title icons
    if (settings.getShowPVMetrics())
        winPrjMetrics.addTitleIcon(
            WinIcon.minimize(request.getServletPath(), winVisible));
    else
        winPrjMetrics.addTitleIcon(
            WinIcon.maximize(request.getServletPath(), winVisible));
    // Construct the window's content
    if (settings.getShowPVMetrics()) {
        // Prepare the metrics view
        MetricsTableView metricsView =
            new MetricsTableView(selectedProject.getEvaluatedMetrics());
        metricsView.setProjectId(selectedProject.getId());
        metricsView.setSelectedMetrics(selectedProject.getSelectedMetrics());
        metricsView.setShowResult(false);
        // Display the metrics
        winPrjMetrics.setContent(metricsView.getHtml(in));
    }
    // Display the window
    winPrjMetrics.setTitle("Evaluated metrics");
    out.print(winPrjMetrics.render(in - 2));
%>                      </td>
                    </tr>
                  </table>
                </div>
<%
}
else {
    out.print(sp(in) + Functions.error("Invalid Project."));
}
%><!-- Project.jsp -->
