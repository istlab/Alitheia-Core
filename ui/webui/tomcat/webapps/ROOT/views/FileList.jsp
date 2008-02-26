<%@ include file="/inc/functions.jsp" %>

<% // Let's list all project's files.

if (ProjectsListView.getProjectId() != null) {
    out.println(terrier
        .getFiles4Project(ProjectsListView.getProjectId())
            .getHtml());
}
else {
    out.println(error("You need to select a project first."));
}

%>
