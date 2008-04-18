<jsp:useBean id="metricsView"
    class="eu.sqooss.webui.MetricsTableView"
    scope="page"/>

<%@ page import="eu.sqooss.webui.*" %>

<div id="metricslist" class="group">
<% // List metrics per selected project or as total

// Show metric per project, when a project selection exists
if (ProjectsListView.getProjectId() != null) {
    metricsView =
        terrier.getMetrics4Project(ProjectsListView.getProjectId());
}
// Show all installed metrics, when no project has been selected
else {
    metricsView = terrier.getAllMetrics();
    // We don't provide metric names yet, therefore show the description
    if (metricsView != null) {
        metricsView.setShowDescription(true);
    }
}

// Display the accumulated metrics list
if (metricsView != null ) {
    out.println(metricsView.getHtmlList());
} else {
    out.println(Functions.error(terrier.getError()));
}

%>
</div>
