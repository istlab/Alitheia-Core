<jsp:useBean id="EvaluatedProjectListView" class="eu.sqooss.webui.ProjectList" scope="session"/>
<jsp:setProperty name="EvaluatedProjectListView" property="*"/>

<%

out.println(EvaluatedProjectListView.getHtml());

%>
