<jsp:useBean id="ProjectsListView" class="eu.sqooss.webui.ProjectsListView" scope="page"/>
<jsp:setProperty name="ProjectsListView" property="*"/>
<%

String projects = ProjectsListView.getHtml();
if (projects == null) {
    out.println(error("No projects found. Bummer."));
} else {
    out.println(projects);
    out.println("<p />Currently selected:");
    String selected_project = ProjectsListView.getCurrentProject();
    if (selected_project == null) {
        out.println(error("No project selected."));
    } else {
        out.println("<strong>" + selected_project + "</strong>");
    }
}

%>
</strong>