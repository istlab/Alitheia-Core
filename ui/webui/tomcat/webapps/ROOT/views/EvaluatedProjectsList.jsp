<jsp:useBean id="ProjectsListView"
    class="eu.sqooss.webui.ProjectsListView"
    scope="session"/>
<jsp:useBean id="terrier"
    class="eu.sqooss.webui.Terrier"
    scope="page"/>
<jsp:setProperty name="ProjectsListView" property="*"/>

<%@ page import="eu.sqooss.webui.*" %>

<%

ProjectsListView.setProjectId(request.getParameter("id"));
ProjectsListView.retrieveData(terrier);
String projects = ProjectsListView.getHtml();
if (projects == null) {
    out.println(error("Unable to find any evaluated projects."));
} else {
    out.println(projects);
    out.println("<p />Currently selected:");
    Project selected_project = ProjectsListView.getCurrentProject();
    if (selected_project == null) {
        out.println(error("No project selected."));
    } else {
        out.println("<strong>" + selected_project.getName() + "</strong>");
    }
}

%>
</strong>