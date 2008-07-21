<%@ page import="eu.sqooss.webui.*"
%><%
// Indentation depth
in = 9;
// Retrieve the list of evaluated project from the SQO-OSS framework
ProjectsListView.retrieveData(terrier);
Window winEvalProjects = new Window();
winEvalProjects.setTitle("Evaluated Projects");
if (ProjectsListView.hasProjects()) {
    ProjectsListView.setServletPath(request.getServletPath());
    winEvalProjects.setContent(ProjectsListView.getHtml(in + 2));
}
// No evaluated project available
else {
    winEvalProjects.setContent("Unable to find any evaluated projects.");
}
out.print(winEvalProjects.render(in));
%><!-- EvaluatedProjectsList.jsp -->
