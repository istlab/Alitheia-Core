<jsp:useBean id="MetricsListView" class="eu.sqooss.webui.MetricsListView" scope="session"/>
<jsp:setProperty name="MetricsListView" property="*"/>

<% // Let's list all projects.

out.println(MetricsListView.getHtml());

/*
projects.getCurrentProject();
String[] allProjects;
allProjects = projects.getAllProjects();

for (int i = 0; i < allProjects.length; i++) {
    out.println("<li>" + allProjects[i] + "</li>");
}
*/
%>
