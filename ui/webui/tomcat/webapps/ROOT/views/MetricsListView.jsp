<jsp:useBean id="MetricsListView" class="eu.sqooss.webui.MetricsListView" scope="session"/>
<jsp:setProperty name="MetricsListView" property="*"/>

<% // Let's list all projects.

out.println(MetricsListView.getHtml());

%>
