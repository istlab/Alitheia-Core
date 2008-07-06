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
    out.print(Functions.simpleWindow(in,
        "Metrics evaluated on project: " + selectedProject.getName(),
        metricsView.getHtml(in + 2)));
}
else {
    out.print(sp(in) + Functions.information("No project is selected."
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
winVisible = "showAllMetrics";
if (request.getParameter(winVisible) != null) {
    // Check the "Show all installed metrics" flag
    if (request.getParameter(winVisible).equals("true"))
        settings.setShowAllMetrics(true);
    else if (request.getParameter(winVisible).equals("false"))
        settings.setShowAllMetrics(false);
}
// Construct the window's title icons
if (settings.getShowAllMetrics())
    winShowIco = WinIcon.minimize(request.getServletPath(), winVisible);
else
    winShowIco = WinIcon.maximize(request.getServletPath(), winVisible);
// Construct the window's content
winContent = null;
if (settings.getShowAllMetrics()) {
    MetricsTableView allMetrics =
        new MetricsTableView(terrier.getAllMetrics());
    allMetrics.setShowResult(false);
    winContent = allMetrics.getHtml(in);
}
// Display the window
winTitle = "All installed metrics";
out.print(Functions.interactiveWindow(
    9, winTitle, winContent, new WinIcon[]{winShowIco}));
%>            </div>
          </form>
