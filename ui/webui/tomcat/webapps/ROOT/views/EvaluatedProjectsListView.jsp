<jsp:useBean id="EvaluatedProjectsListView" class="eu.sqooss.webui.EvaluatedProjectsListView" scope="session"/>
<jsp:setProperty name="EvaluatedProjectsListView" property="*"/>

<%

out.println(EvaluatedProjectsListView.getHtml());

out.println("<p /><strong>");
out.println(EvaluatedProjectsListView.getCurrentProject());
//out.println(EvaluatedProjectsListView.getCurrentProjectId());
%>
</strong>