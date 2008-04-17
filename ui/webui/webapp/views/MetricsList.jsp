<jsp:useBean id="metricsView"
    class="eu.sqooss.webui.MetricsTableView"
    scope="page"/>

<%@ page import="eu.sqooss.webui.*" %>

<div id="metricslist" class="group">
<% // List metrics per selected project or as total

if (ProjectsListView.getProjectId() != null) {
    metricsView =
        terrier.getMetrics4Project(ProjectsListView.getProjectId());
}
else {
    metricsView = terrier.getAllMetrics();
    if (metricsView != null) {
        metricsView.setShowDescription(false);
    }
}

if (metricsView != null ) {
    out.println(metricsView.getHtmlList());
} else {
    out.println(Functions.error(terrier.getError()));
}

%>
</div>
