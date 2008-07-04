<%@ page import="eu.sqooss.webui.*"
%>            <table>
              <tr>
                <td valign="top" style="width: 100%; padding-bottom: 0;">
<%
// Indentation depth
in = 9;
// Retrieve the list of evaluated project from the SQO-OSS framework
ProjectsListView.retrieveData(terrier);
if (ProjectsListView.hasProjects()) {
    ProjectsListView.setServletPath(request.getServletPath());
    out.print(Functions.simpleWindow(in, "Evaluated Projects",
        ProjectsListView.getHtml(in + 2)));
}
// No evaluated project available
else {
    out.print(Functions.simpleWindow(in, "Evaluated Projects",
        Functions.warning("Unable to find any evaluated projects.")));
}
%>                </td>
              </tr>
            </table>
<!-- EvaluatedProjectsList.jsp -->
