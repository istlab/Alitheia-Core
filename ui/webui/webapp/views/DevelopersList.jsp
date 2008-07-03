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
                <td valign="top" style="width: 1000%;">
                  <div class="win">
<%
    // Indentation depth
    in = 7;
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
        // Display the developers view
        out.print(developersView.getHtml(in));
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
