<jsp:useBean id="MetricsListView"
    class="eu.sqooss.webui.MetricsTableView"
    scope="page"/>

<% // Let's list all projects.

if (ProjectsListView.getProjectId() != null) {
    out.println(terrier
        .getMetrics4Project(ProjectsListView.getProjectId())
            .getHtmlList());
}
else {
    MetricsListView.showDescription = false;
    out.println(MetricsListView.getHtmlList());
}

%>
