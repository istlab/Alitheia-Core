<%@ page import="eu.sqooss.webui.*" %>

<jsp:useBean id="ProjectsListView"
class="eu.sqooss.webui.ProjectsListView"
scope="session"/>
<jsp:setProperty name="ProjectsListView" property="*"/>

<%
if (selectedProject != null) {
    //ProjectsListView.setProjectId();
    // Retrieve the list of evaluated project from the connected SQO-OSS system
    ProjectsListView.retrieveData(terrier);

    if (ProjectsListView.hasProjects()) {
        // Generate the HTML content dispaying all evaluated projects
        String projects = ProjectsListView.getHtml();

        //selectedProject = ProjectsListView.getCurrentProject();

        %>
        <%@ include file="/views/Project.jsp" %>
        <%

            out.println("<div id=\"projectslist\" class=\"group\">");
            out.println(projects);
            out.println("</div>");
    } else {
        out.println("<div id=\"error\" class=\"group\">");
        if (cruncher.isOnline()) {
            out.println(Functions.error("Unable to find any evaluated projects."));
        } else {
            out.println(cruncher.getStatus());
        }
        out.println("</div>");
    }
}
//out.println(Functions.error(terrier.getError()));

%>

