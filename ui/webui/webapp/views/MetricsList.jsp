<%@ page import="eu.sqooss.webui.*"
%><%@ page import="eu.sqooss.webui.util.*"
%><%@ page import="eu.sqooss.webui.widgets.*"
%>                <form id="metrics" name="metrics" method="GET">
                  <div id="metricslist">
<%
//============================================================================
// Show all metrics installed in the SQO-OSS framework
//============================================================================

// Indentation depth
in = 9;

/*
 * Check if the user has requested a refresh of the metrics list
 */
if (request.getParameter("refreshMetrics") != null) {
    if (selectedProject.isValid()) {
        selectedProject.flushMetrics();
        selectedProject.getEvaluatedMetrics();
    }
    // TODO: Still not cached
}

/*
 *  Check if the user has selected a metric
 */
if (request.getParameter("selectMetric") != null) {
    try {
        Long selId = new Long(request.getParameter("selectMetric"));
        selectedProject.selectMetric(selId);
    }
    catch (NumberFormatException ex) {}
}

/*
 *  Check if the user has deselected a metric
 */
if (request.getParameter("deselectMetric") != null) {
    try {
        Long selId = new Long(request.getParameter("deselectMetric"));
        selectedProject.deselectMetric(selId);
    }
    catch (NumberFormatException ex) {}
}

//winVisible = "showAllMetrics";
//if (request.getParameter(winVisible) != null) {
//    // Check the "Show all installed metrics" flag
//    if (request.getParameter(winVisible).equals(WinIcon.MAXIMIZE))
//        settings.setShowAllMetrics(true);
//    else if (request.getParameter(winVisible).equals(WinIcon.MINIMIZE))
//        settings.setShowAllMetrics(false);
//}

// Construct the window's title icons
//if (settings.getShowAllMetrics())
//    winAllMetrics.addTitleIcon(WinIcon.minimize(
//        request.getServletPath(), winVisible));
//else
//    winAllMetrics.addTitleIcon(WinIcon.maximize(
//        request.getServletPath(), winVisible));

/*
 * Construct the window's content
 */
Window winMetricsView = new Window();
if ((selectedProject.isValid() 
        && selectedProject.getEvaluatedMetrics().size() > 0))
    winMetricsView.setTitle("Evaluated metrics on"
            + " project " + selectedProject.getName());
else
    winMetricsView.setTitle("Available metrics");

/*
 * Initialise the data view's object
 */

MetricsTableView dataView = new MetricsTableView(selectedProject);
dataView.setServletPath(request.getServletPath());
dataView.setSettings(settings);
dataView.setTerrier(terrier);

/*
 * Display the window
 */
winMetricsView.setContent(dataView.getHtml(in));
out.print(winMetricsView.render(in));
%>                  </div>
                </form>
