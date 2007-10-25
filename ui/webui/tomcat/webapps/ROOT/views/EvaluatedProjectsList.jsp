<jsp:useBean id="projects" class="eu.sqooss.webui.ProjectList" scope="session"/>
<jsp:setProperty name="projects" property="*"/>

<ul>
<% // Let's list all projects.

projects.getCurrentProject();
String[] allProjects;
allProjects = projects.getAllProjects();

for (int i = 0; i < allProjects.length; i++) {
    out.println("<li>" + allProjects[i] + "</li>");
}

%>
</ul>
