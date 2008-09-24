<%@ page import="eu.sqooss.webui.*"
%><%@ page import="eu.sqooss.webui.util.*"
%><%@ page import="eu.sqooss.webui.widgets.*"
%>          <form id="metrics" name="metrics" method="GET">
            <div id="metricslist">
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
        selectedProject.getEvaluatedMetrics();
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
        new MetricsTableView(selectedProject.getEvaluatedMetrics());
    metricsView.setProjectId(selectedProject.getId());
    metricsView.setSelectedMetrics(selectedProject.getSelectedMetrics());
    metricsView.setShowSelect(true);
    metricsView.setShowResult(false);
    // Construct the project metrics window
    Window winPrjMetrics = new Window();
    winPrjMetrics.setTitle("Metrics evaluated on project: "
        + selectedProject.getName());
    winPrjMetrics.setContent(metricsView.getHtml(in + 2));
    out.print(winPrjMetrics.render(in));
}
else {
    out.print(sp(in) + Functions.information("No project is selected."
        + " If you want to see metrics applied to a certain project,"
        + " <a href=\"/projects.jsp\">choose one</a> first."));
}
%>            </div>
            <div id="metricslist">
<%
//============================================================================
// Show all metrics installed in the SQO-OSS framework
//============================================================================
// Check if the user has requested a metrics refresh
if (request.getParameter("refreshAllMetrics") != null) {
    // TODO: Still not cached
}
// Check if the user has requested to show/hide this view
winVisible = "showAllMetrics";
if (request.getParameter(winVisible) != null) {
    // Check the "Show all installed metrics" flag
    if (request.getParameter(winVisible).equals(WinIcon.MAXIMIZE))
        settings.setShowAllMetrics(true);
    else if (request.getParameter(winVisible).equals(WinIcon.MINIMIZE))
        settings.setShowAllMetrics(false);
}
Window winAllMetrics = new Window();
// Construct the window's title icons
if (settings.getShowAllMetrics())
    winAllMetrics.addTitleIcon(WinIcon.minimize(
        request.getServletPath(), winVisible));
else
    winAllMetrics.addTitleIcon(WinIcon.maximize(
        request.getServletPath(), winVisible));
// Construct the window's content
if (settings.getShowAllMetrics()) {
    MetricsList allMetrics = new MetricsList();
    allMetrics.addAll(terrier.getAllMetrics());
    MetricsTableView allMetricsView = new MetricsTableView(allMetrics);
    allMetricsView.setShowResult(false);
    winAllMetrics.setContent(allMetricsView.getHtml(in));
}
// Display the window
winAllMetrics.setTitle("All installed metrics");
out.print(winAllMetrics.render(in));
%>            </div>
          </form>
