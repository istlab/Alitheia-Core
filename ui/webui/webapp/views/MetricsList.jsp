<jsp:useBean id="metricsView"
    class="eu.sqooss.webui.MetricsTableView"
    scope="page"/>

<%@ page import="eu.sqooss.webui.*" %>

<div class="group">
<% // List metrics per selected project or as total

if (ProjectsListView.getProjectId() != null) {
    metricsView =
        terrier.getMetrics4Project(ProjectsListView.getProjectId());
    if (metricsView != null ) {
        out.println(metricsView.getHtmlList());
    }
    else {
      out.println(Functions.error(terrier.getError()));
    }
}
else {
    metricsView.setShowDescription(false);
    out.println(metricsView.getHtmlList());
}

%>
</div>
