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
                <td valign="top" style="width: 100%; padding-bottom: 0;">
<%
    //========================================================================
    // Display the list of developers that are working on the current project
    //========================================================================
    // Indentation depth
    in = 9;
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
    winVisible = "showDevelopers";
    if (request.getParameter(winVisible) != null) {
        if (request.getParameter(winVisible).equals("true"))
            settings.setShowPVDevelopers(true);
        else if (request.getParameter(winVisible).equals("false"))
            settings.setShowPVDevelopers(false);
    }
    // Construct the window's title icons
    if (settings.getShowDevelopers())
        winShowIco = WinIcon.minimize(request.getServletPath(), winVisible);
    else
        winShowIco = WinIcon.maximize(request.getServletPath(), winVisible);
    // Construct the window's content
    winContent = null;
    if (settings.getShowDevelopers()) {
        // Prepare the developers view
        selectedProject.setTerrier(terrier);
        DevelopersListView developersView =
            new DevelopersListView(selectedProject.getDevelopers());
        developersView.setSelectedDevelopers(
            selectedProject.getSelectedDevelopersIds());
        // Display the developers view
        winContent = developersView.getHtml(in);
    }
    // Display the window
    winTitle = "Developers in project " + selectedProject.getName();
    out.print(Functions.interactiveWindow(
        9, winTitle, winContent, new WinIcon[]{winShowIco}));

    //========================================================================
    // Display the available evaluation result for the selected developers
    //========================================================================
    if ((selectedProject.getSelectedDevelopers().size() > 0)
        && (selectedProject.getSelectedMetrics().size() > 0)) {
%>                </td>
              </tr>
              <tr>
                <td valign="top" style="width: 100%; padding-top: 0;">
<%
        // Prepare the developers results view
        DevelopersResultView devResultsView =
            new DevelopersResultView(selectedProject);
        devResultsView.setSettings(settings);
        devResultsView.setTerrier(terrier);
        devResultsView.tempFolder = tempFolder;
        // Display the window
        out.println(Functions.simpleWindow(in, "Evaluation results",
            devResultsView.getHtml(in)));
    }
%>                </td>
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
