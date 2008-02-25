<%@ page import="eu.sqooss.webui.*" %>

<%

ProjectsListView.setProjectId(request.getParameter("id"));
ProjectsListView.retrieveData(terrier);
String projects = ProjectsListView.getHtml();
if (projects == null) {
    out.println(error("Unable to find any evaluated projects."));
} else {
    out.println("<p />Selected:");
    Project selectedProject = ProjectsListView.getCurrentProject();
    if (selectedProject == null) {
        out.println(error("No project selected."));
    } else {
        out.println("<strong>" + selectedProject.getName() + "</strong>");
        out.println(selectedProject.getInfo());
        out.println("<hr>");
    }
    out.println(projects);
}

%>
</strong>