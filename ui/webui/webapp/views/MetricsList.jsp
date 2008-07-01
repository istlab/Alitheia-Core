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
// Check if the user has requested a metrics refresh
if (request.getParameter("refreshAllMetrics") != null) {
    // TODO: Still not cached
}
// Check if the user has requested to show/hide this view
if (request.getParameter("showAllMetrics") != null) {
    // Check the "Show all installed metrics" flag
    if (request.getParameter("showAllMetrics").equals("true"))
        settings.setShowAllMetrics(true);
    else if (request.getParameter("showAllMetrics").equals("false"))
        settings.setShowAllMetrics(false);
}
// Display the view's title name
out.println(sp(in)
    + "<span style=\"float: left; width: 90%; text-align:left;\">"
    + "<h2>All installed metrics</h2>"
    + "</span>");
// Display the view's title bar
out.println(sp(in)
    + "<span style=\"float: right; width: 10%; text-align:right;\">"
    + "<a href=\"/metrics.jsp?showAllMetrics="
    + !settings.getShowAllMetrics()
    + "\">"
    + "<img alt=\""
    + ((settings.getShowAllMetrics()) ? "Hide" : "Show")
    + "\" src=\"/img/icons/16x16/"
    + ((settings.getShowAllMetrics()) ? "list-remove.png" : "list-add.png")
    + "\">"
    + "</a>"
    + "</span>");
// Display all installed metrics
if (settings.getShowAllMetrics()) {
    MetricsTableView allMetrics =
        new MetricsTableView(terrier.getAllMetrics());
    allMetrics.setShowResult(false);
    out.print(allMetrics.getHtml(in));
}
%>            </div>
          </form>
