<%@ page import="eu.sqooss.webui.*" %>

<jsp:useBean id="ProjectsListView"
class="eu.sqooss.webui.ProjectsListView"
scope="session"/>
<jsp:setProperty name="ProjectsListView" property="*"/>

<%

// Retrieve the list of evaluated project from the connected SQO-OSS system
ProjectsListView.retrieveData(terrier);

if (ProjectsListView.hasProjects()) {
    // Generate the HTML content dispaying all evaluated projects
    out.println("<div id=\"projectslist\" class=\"group\">");
    out.println(ProjectsListView.getHtml());
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

%>

