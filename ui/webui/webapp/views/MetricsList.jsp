<%@ page import="eu.sqooss.webui.*"
%>          <form id="metrics" name="metrics" method="GET">
            <div id="metricslist" class="group">
<%
// Indentation depth
in = 7;
//out.println(debugRequest(request));
//============================================================================
// Show metric per project, when a project selection exists
//============================================================================
if (selectedProject.isValid()) {
    // Check if the user has requested a metrics refresh
    if (request.getParameter("refreshPrjMetrics") != null) {
        selectedProject.flushMetrics();
    }
    // Check for a metric selection
    if (request.getParameter("selectMetric") != null) {
        try {
            Long selId = new Long(request.getParameter("selectMetric"));
            selectedProject.selectMetric(selId);
        }
        catch (NumberFormatException ex) {}
    }
    if (request.getParameter("deselectMetric") != null) {
        try {
            Long selId = new Long(request.getParameter("deselectMetric"));
            selectedProject.deselectMetric(selId);
        }
        catch (NumberFormatException ex) {}
    }
    // Prepare the metrics view
    MetricsTableView metricsView =
        new MetricsTableView(selectedProject.retrieveMetrics());
    metricsView.setProjectId(selectedProject.getId());
    metricsView.setSelectedMetrics(selectedProject.getSelectedMetrics());
    metricsView.setShowSelect(true);
    metricsView.setShowResult(false);
    // Display the metrics
    out.println(sp(in) + "<h2>Metrics evaluated on project: "
        + selectedProject.getName() + "</h2>");
    out.print(metricsView.getHtml(in));
}
else {
    out.print(sp(in) + Functions.warning("No project is selected."
        + " If you want to see metrics applied to a certain project,"
        + " <a href=\"/projects.jsp\">choose one</a> first."));
}
%>            </div>
            <div id="metricslist" class="group">
<%
//============================================================================
// Show all metrics installed in the SQO-OSS framework
//============================================================================
out.println(sp(in) + "<h2>All installed metrics</h2>");
if (request.getParameter("showAllMetrics") != null) {
    // Check the "Show all installed metrics" flag
    if (request.getParameter("showAllMetrics").equals("true"))
        settings.setShowAllMetrics(true);
    else if (request.getParameter("showAllMetrics").equals("false"))
        settings.setShowAllMetrics(false);
}
// Check if the user has requested a metrics refresh
if (request.getParameter("refreshAllMetrics") != null) {
    // TODO: Still not cached
}
// Add a link for show/hide all installed metrics
if (settings.getShowAllMetrics())
    out.println(sp(in) + "<a href=\"/metrics.jsp?showAllMetrics=false\">"
        + "Hide all installed metrics" + "</a><br/>");
else
    out.println(sp(in) + "<a href=\"/metrics.jsp?showAllMetrics=true\">"
        + "Show all installed metrics" + "</a><br/>");
// Display all installed metrics
if (settings.getShowAllMetrics()) {
    MetricsTableView allMetrics =
        new MetricsTableView(terrier.getAllMetrics());
    allMetrics.setShowResult(false);
    out.print(allMetrics.getHtml(in));
}
%>            </div>
          </form>
