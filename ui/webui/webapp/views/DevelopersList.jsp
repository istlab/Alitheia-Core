<%@ page import="eu.sqooss.webui.*"
%><%@ page import="eu.sqooss.webui.view.*"
%><%
//============================================================================
// List all developers in the selected project
//============================================================================
if (selectedProject.isValid()) {
%>          <div id="developerslist" class="group">
            <table width="100%" border="0" background="none">
              <tr>
                <td valign="top" style="width: 100%;">
                  <div class="win">
<%
    // Indentation depth
    in = 7;
    // Check for a developer selection
    if (request.getParameter("selectDeveloper") != null) {
        try {
            Long selId = new Long(request.getParameter("selectDeveloper"));
            selectedProject.selectDeveloper(selId);
        }
        catch (NumberFormatException ex) {}
    }
    if (request.getParameter("deselectDeveloper") != null) {
        try {
            Long selId = new Long(request.getParameter("deselectDeveloper"));
            selectedProject.deselectDeveloper(selId);
        }
        catch (NumberFormatException ex) {}
    }
    // Check if the user has requested to show/hide this view
    if (request.getParameter("showDevelopers") != null) {
        if (request.getParameter("showDevelopers").equals("true"))
            settings.setShowPVDevelopers(true);
        else if (request.getParameter("showDevelopers").equals("false"))
            settings.setShowPVDevelopers(false);
    }
    // Display the window title
    out.println(sp(in)
        + "<div class=\"winTitle\">"
        + "&nbsp;<b>Developers in project " + selectedProject.getName() + "<b>"
        + "<div class=\"winTitleBar\">"
        + "<a style=\"vertical-align: middle;\""
        + " href=\"/developers.jsp?showDevelopers="
        + !settings.getShowDevelopers()
        + "\">"
        + "<img alt=\""
        + ((settings.getShowDevelopers()) ? "Hide" : "Show")
        + "\" src=\"/img/icons/16x16/"
        + ((settings.getShowDevelopers()) ? "list-remove.png" : "list-add.png")
        + "\">"
        + "</a>"
        + "</div>"
        + "</div>");
    // Display the content
    if (settings.getShowDevelopers()) {
        // Prepare the developers view
        selectedProject.setTerrier(terrier);
        DevelopersListView developersView =
            new DevelopersListView(selectedProject.getDevelopers());
        developersView.setSelectedDevelopers(
            selectedProject.getSelectedDevelopersIds());
        // Display the developers view
        out.print(developersView.getHtml(in));
    }
    
    if ((selectedProject.getSelectedDevelopers().size() > 0)
        && (selectedProject.getSelectedMetricMnemonics().size() > 0)) {
%>                  </div>
                </td>
              </tr>
              <tr>
                <td valign="top" style="width: 100%;">
                  <div class="win">
<%
    // Display the window title
    out.println(sp(in)
        + "<div class=\"winTitle\">"
        + "&nbsp;<b>Evaluation results<b>"
        + "</div>");
        // Prepare the developers results view
        DevelopersResultView devResultsView =
            new DevelopersResultView(selectedProject);
        devResultsView.setSettings(settings);
        devResultsView.setTerrier(terrier);
        // Display the developers results view
        out.print(devResultsView.getHtml(in));
    }
%>                  </div>
                </td>
              </tr>
            </table>
<%
}
//============================================================================
// Let the user choose a project, if none was selected
//============================================================================
else {
%>          <div id="projectslist" class="group">
<%
    // Check if the user has selected a project
    if (request.getParameter("pid") != null)
        ProjectsListView.setCurrentProject(
            strToLong(request.getParameter("pid")));
    // Retrieve the list of projects from the connected SQO-OSS framework
    ProjectsListView.retrieveData(terrier);
    // Display the list of evaluated projects (if any)
    if (ProjectsListView.hasProjects()) {
        out.println(sp(in) + "<h2>Please select a project first</h2>");
        ProjectsListView.setServletPath(request.getServletPath());
        out.println(ProjectsListView.getHtml(in));
    }
    // No evaluated projects found
    else {
        out.print(sp(in) + Functions.error(
                "Unable to find any evaluated projects!"));
    }
}
%>          </div>
