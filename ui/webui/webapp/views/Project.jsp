<%@ page import="eu.sqooss.webui.*"
%><%@ page import="eu.sqooss.webui.view.*"
%><%
String inputError = null;
if (selectedProject.isValid()) {
    // Indentation depth
    in = 5;
    out.println(sp(in) + "<div id=\"selectedproject\" class=\"group\">");
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
%>            <table>
              <tr>
                <td valign="top" style="width: 60%; padding-bottom: 0;">
<%
    //========================================================================
    // Display the selected project's metadata
    //========================================================================
    // Indentation depth
    in = 11;
    // Check if the user has requested to show/hide this view
    winVisible = "showPVMetadata";
    if (request.getParameter(winVisible) != null) {
        if (request.getParameter(winVisible).equals(WinIcon.enable))
            settings.setShowPVMetadata(true);
        else if (request.getParameter(winVisible).equals(WinIcon.disable))
            settings.setShowPVMetadata(false);
    }
    // Construct the window's title icons
    if (settings.getShowPVMetadata())
        winShowIco = WinIcon.minimize(request.getServletPath(), winVisible);
    else
        winShowIco = WinIcon.maximize(request.getServletPath(), winVisible);
    // Construct the window's content
    winContent = null;
    if (settings.getShowPVMetadata()) {
        ProjectInfoView infoView = new ProjectInfoView(selectedProject);
        winContent = infoView.getHtml(in);
    }
    // Display the window
    winTitle = selectedProject.getName() + " overview";
    out.print(Functions.interactiveWindow(
        9, winTitle, winContent, new WinIcon[]{winShowIco}));

    //========================================================================
    // Display the list of developers that are working on this project
    //========================================================================
    // Indentation depth
    in = 11;
    // Check if the user has requested to show/hide this view
    winVisible = "showPVDevelopers";
    if (request.getParameter(winVisible) != null) {
        if (request.getParameter(winVisible).equals(WinIcon.enable))
            settings.setShowPVDevelopers(true);
        else if (request.getParameter(winVisible).equals(WinIcon.disable))
            settings.setShowPVDevelopers(false);
    }
    // Construct the window's title icons
    if (settings.getShowPVDevelopers())
        winShowIco = WinIcon.minimize(request.getServletPath(), winVisible);
    else
        winShowIco = WinIcon.maximize(request.getServletPath(), winVisible);
    // Construct the window's content
    winContent = null;
    if (settings.getShowPVDevelopers()) {
        // Prepare the developers view
        selectedProject.setTerrier(terrier);
        DevelopersListView developersView =
        new DevelopersListView(selectedProject.getDevelopers());
        // Display the developers
        winContent = developersView.getHtml(in);
    }
    // Display the window
    winTitle = "Developers";
    out.print(Functions.interactiveWindow(
        9, winTitle, winContent, new WinIcon[]{winShowIco}));
%>                </td>
                <td valign="top"style="width: 40%; padding-bottom: 0;">
<%
    //========================================================================
    // Display the selected project's version statistic and selector
    //========================================================================
    // Indentation depth
    in = 11;
    // Check if the user has selected an another project version
    String paramName = "version" + selectedProject.getId();
    if (request.getParameter(paramName) != null) {
        Long versionNumber = null;
        Version selVersion = null;
        try {
            // Retrieve the selected version from the SQO-OSS framework
            versionNumber = new Long(request.getParameter(paramName));
            List<Version> versions = terrier.getVersionsByNumber(
                selectedProject.getId(), new long[]{versionNumber});
            if (versions.size() > 0)
                selVersion = versions.get(0);
            else
                inputError = new String("Version with number"
                    + " " + versionNumber
                    + " does not exist!");
        }
        catch (NumberFormatException e) {
            inputError = new String("Wrong version number format!");
        }
        // Switch to the selected version
        if ((selVersion != null)
            && (selVersion.getId().equals(
                selectedProject.getCurrentVersionId()) == false)) {
            selectedProject.setCurrentVersion(selVersion);
        }
    }
    // Check if the user has requested to show/hide this view
    winVisible = "showPVVersions";
    if (request.getParameter(winVisible) != null) {
        if (request.getParameter(winVisible).equals(WinIcon.enable))
            settings.setShowPVVersions(true);
        else if (request.getParameter(winVisible).equals(WinIcon.disable))
            settings.setShowPVVersions(false);
    }
    // Construct the window's title icons
    if (settings.getShowPVVersions())
        winShowIco = WinIcon.minimize(request.getServletPath(), winVisible);
    else
        winShowIco = WinIcon.maximize(request.getServletPath(), winVisible);
    // Construct the window content
    winContent = null;
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
                        + selectedProject.countVersions() + "\n");
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
            winContent = b.toString();
        }
        else
            winContent = sp(in) + Functions.warning("No versions found.");
    }
    // Display the window
    winTitle = "Versions";
    out.print(Functions.interactiveWindow(
        9, winTitle, winContent, new WinIcon[]{winShowIco}));

    //========================================================================
    // Display the source file statistic of the selected project
    //========================================================================
    // Indentation depth
    in = 11;
    // Check if the user has requested to show/hide this view
    winVisible = "showPVFileStat";
    if (request.getParameter(winVisible) != null) {
        if (request.getParameter(winVisible).equals(WinIcon.enable))
            settings.setShowPVFileStat(true);
        else if (request.getParameter(winVisible).equals(WinIcon.disable))
            settings.setShowPVFileStat(false);
    }
    // Construct the window's title icons
    if (settings.getShowPVFileStat())
        winShowIco = WinIcon.minimize(request.getServletPath(), winVisible);
    else
        winShowIco = WinIcon.maximize(request.getServletPath(), winVisible);
    // Construct the window's content
    winContent = null;
    if (settings.getShowPVFileStat()) {
        if (selectedProject.getCurrentVersion() != null)
            winContent = selectedProject.getCurrentVersion().fileStats(in);
        else
            winContent = sp(in) + Functions.warning("No versions found.");
    }
    // Display the window
    winTitle = "Files"
        + ((selectedProject.getCurrentVersion() != null)
            ? " in version " + selectedProject.getCurrentVersion().getNumber()
            : "");
    out.print(Functions.interactiveWindow(
        9, winTitle, winContent, new WinIcon[]{winShowIco}));
%>                </td>
              </tr>
              <tr>
                <td valign="top" colspan="2" style="width: 100%; padding-top: 0;">
<%
    //========================================================================
    // Display the list of metrics that were evaluated on this project
    //========================================================================
    // Indentation depth
    in = 11;
    // Check if the user has requested a metrics refresh
    if (request.getParameter("refreshPrjMetrics") != null) {
        selectedProject.flushMetrics();
    }
    // Check if the user has requested to show/hide this view
    winVisible = "showPVMetrics";
    if (request.getParameter(winVisible) != null) {
        if (request.getParameter(winVisible).equals(WinIcon.enable))
            settings.setShowPVMetrics(true);
        else if (request.getParameter(winVisible).equals(WinIcon.disable))
            settings.setShowPVMetrics(false);
    }
    // Construct the window's title icons
    if (settings.getShowPVMetrics())
        winShowIco = WinIcon.minimize(request.getServletPath(), winVisible);
    else
        winShowIco = WinIcon.maximize(request.getServletPath(), winVisible);
    // Construct the window's content
    winContent = null;
    if (settings.getShowPVMetrics()) {
        // Prepare the metrics view
        MetricsTableView metricsView =
            new MetricsTableView(selectedProject.getEvaluatedMetrics());
        metricsView.setProjectId(selectedProject.getId());
        metricsView.setSelectedMetrics(selectedProject.getSelectedMetrics());
        metricsView.setShowResult(false);
        // Display the metrics
        winContent = metricsView.getHtml(in);
    }
    // Display the window
    winTitle = "Evaluated metrics";
    out.print(Functions.interactiveWindow(
        9, winTitle, winContent, new WinIcon[]{winShowIco}));
%>                </td>
              </tr>
            </table>
          </div>
<%
}
else {
    out.print(sp(in) + Functions.error("Invalid Project."));
}
%><!-- Project.jsp -->
