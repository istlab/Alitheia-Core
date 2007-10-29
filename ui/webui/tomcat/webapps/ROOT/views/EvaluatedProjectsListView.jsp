<jsp:useBean id="EvaluatedProjectsListView" class="eu.sqooss.webui.EvaluatedProjectsListView" scope="session"/>
<jsp:setProperty name="EvaluatedProjectsListView" property="*"/>

<%

out.println(EvaluatedProjectsListView.getHtml());

%>
