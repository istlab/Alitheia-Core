<jsp:useBean id="ProjectsListView" class="eu.sqooss.webui.ProjectsListView" scope="page"/>
<jsp:setProperty name="ProjectsListView" property="*"/>
<%
out.println(ProjectsListView.getHtml());
out.println("<p />Currently selected: <strong>");
out.println(ProjectsListView.getCurrentProject());
%>
</strong>