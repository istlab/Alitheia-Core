<jsp:useBean id="FileListView"
    class="eu.sqooss.webui.FileListView"
    scope="session"/>
<jsp:setProperty name="FileListView" property="*"/>

<% // Let's list all projects.

if (ProjectsListView.getProjectId() != null) {
    out.println(terrier
        .getFiles4Project(ProjectsListView.getProjectId())
            .getHtml());
}
else {
    out.println(FileListView.getHtml());
}

%>
