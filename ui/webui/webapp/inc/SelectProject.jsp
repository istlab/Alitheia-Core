                <div id="projectslist">
<%
    in = 9;
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
        out.print(sp(in) + Functions.information(
                "Unable to find any evaluated projects!"));
    }
%>