<%@ page import="eu.sqooss.webui.*"
%><%
// Retrieve the list of evaluated project from the SQO-OSS framework
ProjectsListView.retrieveData(terrier);
if (ProjectsListView.hasProjects()) {
    ProjectsListView.setServletPath(request.getServletPath());
    out.print(ProjectsListView.getHtml(in));
}
// No evaluated project available
else {
    out.print(sp(in) + Functions.warning(
        "Unable to find any evaluated projects."));
}
%>
<!-- EvaluatedProjectsList.jsp -->
