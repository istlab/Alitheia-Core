<%@ page import="eu.sqooss.webui.*"
%><%@ page import="eu.sqooss.webui.view.*"
%><%@ page import="eu.sqooss.webui.widgets.*"
%><%
//============================================================================
// List all developers in the selected project
//============================================================================
if (selectedProject.isValid()) {
%>          <div id="developerslist">
            <table width="100%" border="0" background="none">
              <tr>
                <td valign="top" style="width: 100%; padding-bottom: 0;">
<%
    //========================================================================
    // Display the list of developers that are working on the current project
    //========================================================================
    // Indentation depth
    in = 9;
    // Check for a multiple developer selection
    if (request.getParameter("select") != null) {
        if (request.getParameter("select").equals("all"))
            selectedProject.selectAllDevelopers();
        else if (request.getParameter("select").equals("none"))
            selectedProject.deselectAllDevelopers();
    }
    // Check for a single developer selection
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
        if (request.getParameter(winVisible).equals(WinIcon.MAXIMIZE))
            settings.setShowPVDevelopers(true);
        else if (request.getParameter(winVisible).equals(WinIcon.MINIMIZE))
            settings.setShowPVDevelopers(false);
    }
    Window winDevList = new Window();
    // Construct the window's title icons
    if (settings.getShowDevelopers())
        winDevList.addTitleIcon(WinIcon.minimize(
            request.getServletPath(), winVisible));
    else
        winDevList.addTitleIcon(WinIcon.maximize(
            request.getServletPath(), winVisible));
    // Construct the window's content
    if (settings.getShowDevelopers()) {
        // "Select all developers" icon
        WinIcon icoSelect = new WinIcon();
        icoSelect.setPath(request.getServletPath());
        icoSelect.setParameter("select");
        icoSelect.setValue("all");
        icoSelect.setImage("/img/icons/16x16/select-all.png");
        icoSelect.setAlt("Select all");
        winDevList.addToolIcon(icoSelect);
        // "Deselect all developers" icon
        WinIcon icoDeselect = new WinIcon();
        icoDeselect.setPath(request.getServletPath());
        icoDeselect.setParameter("select");
        icoDeselect.setValue("none");
        icoDeselect.setImage("/img/icons/16x16/deselect-all.png");
        icoDeselect.setAlt("Deselect all");
        winDevList.addToolIcon(icoDeselect);
        // Prepare the developers view
        selectedProject.setTerrier(terrier);
        DevelopersListView developersView =
            new DevelopersListView(selectedProject.getDevelopers());
        developersView.setSelectedDevelopers(
            selectedProject.getSelectedDevelopers());
        // Display the developers view
        winDevList.setContent(developersView.getHtml(in));
    }
    // Display the window
    winDevList.setTitle("Developers in project " + selectedProject.getName());
    out.print(winDevList.render(in));

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
        Window winDevResults = new Window();
        // Table chart icon
        WinIcon icoChart = new WinIcon();
        icoChart.setPath(request.getServletPath());
        icoChart.setParameter("chart");
        icoChart.setValue("2");
        icoChart.setImage("/img/icons/16x16/table-chart.png");
        icoChart.setAlt("Tabular");
        winDevResults.addToolIcon(icoChart);
        // Pie chart icon
        icoChart = new WinIcon();
        icoChart.setPath(request.getServletPath());
        icoChart.setParameter("chart");
        icoChart.setValue("4");
        icoChart.setImage("/img/icons/16x16/pie-chart.png");
        icoChart.setAlt("Pie chart");
        winDevResults.addToolIcon(icoChart);
        // Bar chart icon
        icoChart = new WinIcon();
        icoChart.setPath(request.getServletPath());
        icoChart.setParameter("chart");
        icoChart.setValue("8");
        icoChart.setImage("/img/icons/16x16/bar-chart.png");
        icoChart.setAlt("Bar chart");
        winDevResults.addToolIcon(icoChart);
        // Prepare the developers results view
        DevelopersResultView devResultsView =
            new DevelopersResultView(selectedProject);
        devResultsView.setSettings(settings);
        devResultsView.setTerrier(terrier);
        devResultsView.tempFolder = tempFolder;
        if (request.getParameter("chart") != null) {
            Long chartType = strToLong(request.getParameter("chart"));
            if (chartType != null)
                devResultsView.setChartType(chartType.intValue());
        }
        winDevResults.setContent(devResultsView.getHtml(in + 2));
        // Display the window
        winDevResults.setTitle("Evaluation results");
        out.println(winDevResults.render(in));
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
%>          <div id="projectslist">
<%
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
        out.print(sp(in) + Functions.error(
                "Unable to find any evaluated projects!"));
    }
}
%>          </div>
