<%@ page import="eu.sqooss.webui.*"
%><%@ page import="eu.sqooss.webui.view.*"
%><%
if (selectedProject.isValid()) {
    // Indentation depth
    in = 5;
    out.println(sp(in) + "<div id=\"selectedproject\" class=\"group\">");
    // Update the Message Box
    if (msg.length() > 0)
        msg += "<br/>";
    msg += "<strong>Project:</strong> " + selectedProject.getName();
    msg += "<span class=\"forget\"><a href=\"?pid=none\">(forget)</a></span>";
    if (selectedProject.getCurrentVersion() != null) {
        msg += "<br /><strong>Version:</strong> "
            + selectedProject.getCurrentVersion().getNumber();
    }
    else {
        msg += "<br />No versions recorded.";
    }
%>            <table width="100%" border="0" background="none">
              <tr>
                <td valign="top" style="padding-right: 30px; width: 60%;">
<%
    //========================================================================
    // Display the selected project's metadata
    //========================================================================
    // Indentation depth
    in = 9;
    out.println(sp(in) + "<h2>" + selectedProject.getName()
        + " metadata" + "</h2>");
    ProjectInfoView infoView = new ProjectInfoView(selectedProject);
    out.println(infoView.getHtml(in));
%>                </td>
                <td valign="top"style="width: 40%;">
<%
    //========================================================================
    // Display the selected project's version statistic and selector
    //========================================================================
    // Indentation depth
    in = 9;
    String inputError = null;
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
    // Display the project versions statistic
    if (selectedProject.getCurrentVersion() != null) {
        Version currentVersion = selectedProject.getCurrentVersion();
        Long versionNum = currentVersion.getNumber();
        Long versionId = currentVersion.getId();
        // Display the first and last known project versions
        if (selectedProject.getFirstVersion() != null) {
            if (!selectedProject.getFirstVersion().equals(
                selectedProject.getLastVersion())) {
                out.println(sp(in) + "<h2>" + selectedProject.getName()
                    + " Versions</h2>");
                out.println(sp(in) + "<strong>Versions:</strong> "
                    + selectedProject.getFirstVersion().getNumber()
                    + " - "
                    + selectedProject.getLastVersion().getNumber());
                out.println(sp(in) + "<br/>");
                out.println(sp(in) + "<strong>Total:</strong> "
                    + selectedProject.countVersions());
            } else {
                out.println(sp(in) + "<h2>" + selectedProject.getName()
                    + " Version</h2>");
                out.println(sp(in) + "<strong>Version:</strong> "
                    + selectedProject.getFirstVersion().getNumber());
            }
        } else {
            out.println(sp(in) + "<h2>" + selectedProject.getName()
                + "Versions</h2>");
            out.print(sp(in) + Functions.warning(
                "No versions found."));
        }
        // Show the version selector
        out.println(sp(in) + "<br/>");
        // Display any accumulate errors
        if (inputError != null)
            out.println(sp(in) + Functions.error(inputError));
        out.println(sp(in) + "<br/>");
        out.println(sp(in) + "<strong>"
            + "Choose the version you want to display:"
            + "</strong>");
        out.println(sp(in) + "<br/>");
        out.print(versionSelector(selectedProject, in));
    } else {
        out.println(sp(in) + "<h2>"
            + selectedProject.getName() + "Versions</h2>");
        out.print(sp(in) + Functions.warning("No versions found."));
    }
%>                </td>
              </tr>
              <tr>
                <td valign="top" style="width: 60%;">
                  <div class="win">
<%
    //========================================================================
    // Display the list of developers that are working on this project
    //========================================================================
    // Indentation depth
    in = 9;
    // Check if the user has requested to show/hide this view
    if (request.getParameter("showPVDevelopers") != null) {
        if (request.getParameter("showPVDevelopers").equals("true"))
            settings.setShowPVDevelopers(true);
        else if (request.getParameter("showPVDevelopers").equals("false"))
            settings.setShowPVDevelopers(false);
    }
    // Display the window title
    out.println(sp(in)
        + "<div class=\"winTitle\">"
        + "&nbsp;<b>Developers<b>"
        + "<div class=\"winTitleBar\">"
        + "<a style=\"vertical-align: middle;\""
        + " href=\"/projects.jsp?showPVDevelopers="
        + !settings.getShowPVDevelopers()
        + "\">"
        + "<img alt=\""
        + ((settings.getShowPVDevelopers()) ? "Hide" : "Show")
        + "\" src=\"/img/icons/16x16/"
        + ((settings.getShowPVDevelopers()) ? "list-remove.png" : "list-add.png")
        + "\">"
        + "</a>"
        + "</div>"
        + "</div>");
    // Display the content
    if (settings.getShowPVDevelopers()) {
        // Prepare the developers view
        selectedProject.setTerrier(terrier);
        DevelopersListView developersView =
        new DevelopersListView(selectedProject.getDevelopers());
        // Display the developers
        out.print(developersView.getHtml(in));
    }
%>                  </div>
                </td>
                <td valign="top" style="width: 40%;">
                  <div class="win">
<%
    //========================================================================
    // Display the source file statistic of the selected project
    //========================================================================
    // Indentation depth
    in = 9;
    // Check if the user has requested to show/hide this view
    if (request.getParameter("showPVFileStat") != null) {
        if (request.getParameter("showPVFileStat").equals("true"))
            settings.setShowPVFileStat(true);
        else if (request.getParameter("showPVFileStat").equals("false"))
            settings.setShowPVFileStat(false);
    }
    // Display the window title
    out.println(sp(in)
        + "<div class=\"winTitle\">"
        + "&nbsp;<b>Files"
        + ((selectedProject.getCurrentVersion() != null)
            ? " in version "
                + selectedProject.getCurrentVersion().getNumber()
            : "")
        + "<b>"
        + "<div class=\"winTitleBar\">"
        + "<a style=\"vertical-align: middle;\""
        + " href=\"/projects.jsp?showPVFileStat="
        + !settings.getShowPVFileStat()
        + "\">"
        + "<img alt=\""
        + ((settings.getShowPVFileStat()) ? "Hide" : "Show")
        + "\" src=\"/img/icons/16x16/"
        + ((settings.getShowPVFileStat()) ? "list-remove.png" : "list-add.png")
        + "\">"
        + "</a>"
        + "</div>"
        + "</div>");
    // Display the content
    if (settings.getShowPVFileStat()) {
        out.println(sp(in) + "<div id=\"table\">");
        if (selectedProject.getCurrentVersion() != null)
            out.print(selectedProject.getCurrentVersion().fileStats(in));
        else
            out.print(sp(in) + Functions.warning("No versions in Project"));
        out.println(sp(in) + "</div>");
    }
%>                  </div>
                </td>
              </tr>
              <tr>
                <td valign="top" colspan="2" style="width: 100%;">
                  <div class="win">
<%
    //========================================================================
    // Display the list of metrics that were evaluated on this project
    //========================================================================
    // Indentation depth
    in = 9;
    // Check if the user has requested a metrics refresh
    if (request.getParameter("refreshPrjMetrics") != null) {
        selectedProject.flushMetrics();
    }
    // Check if the user has requested to show/hide this view
    if (request.getParameter("showPVMetrics") != null) {
        if (request.getParameter("showPVMetrics").equals("true"))
            settings.setShowPVMetrics(true);
        else if (request.getParameter("showPVMetrics").equals("false"))
            settings.setShowPVMetrics(false);
    }
    // Display the window title
    out.println(sp(in)
        + "<div class=\"winTitle\">"
        + "&nbsp;<b>Evaluated metrics<b>"
        + "<div class=\"winTitleBar\">"
        + "<a style=\"vertical-align: middle;\""
        + " href=\"/projects.jsp?showPVMetrics="
        + !settings.getShowPVMetrics()
        + "\">"
        + "<img alt=\""
        + ((settings.getShowPVMetrics()) ? "Hide" : "Show")
        + "\" src=\"/img/icons/16x16/"
        + ((settings.getShowPVMetrics()) ? "list-remove.png" : "list-add.png")
        + "\">"
        + "</a>"
        + "</div>"
        + "</div>");
    // Display the content
    if (settings.getShowPVMetrics()) {
        // Prepare the metrics view
        MetricsTableView metricsView =
            new MetricsTableView(selectedProject.retrieveMetrics());
        metricsView.setProjectId(selectedProject.getId());
        metricsView.setSelectedMetrics(selectedProject.getSelectedMetrics());
        metricsView.setShowResult(false);
        // Display the metrics
        out.print(metricsView.getHtml(in));
    }
%>                  </div>
                </td>
              </tr>
            </table>
          </div>
<%
}
else {
    out.print(sp(in) + Functions.error("Invalid Project."));
}
%><!-- Project.jsp -->
