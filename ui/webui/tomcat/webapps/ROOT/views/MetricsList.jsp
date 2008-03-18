<jsp:useBean id="MetricsListView"
    class="eu.sqooss.webui.MetricsTableView"
    scope="page"/>

<%@ page import="eu.sqooss.webui.*" %>

<% // List metrics per selected project or as total

if (ProjectsListView.getProjectId() != null) {
    MetricsTableView metrics =
        terrier.getMetrics4Project(ProjectsListView.getProjectId());
    if (metrics != null ) {
        metrics.getHtmlList();
    }
    else {
      out.println(error(terrier.getError()));
    }
}
else {
    MetricsListView.showDescription = false;
    out.println(MetricsListView.getHtmlList());
}

%>
