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
    // Refresh the list of metric evaluated on the selected project
    if (selectedProject.isValid()) {
        selectedProject.flushMetrics();
        selectedProject.getEvaluatedMetrics();
    }
    // Refresh the list of installed metrics
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

/*
 * Construct the window's content
 */
Window winMetricsView = new Window();
if ((selectedProject.isValid() 
        && selectedProject.getEvaluatedMetrics().size() > 0))
    winMetricsView.setTitle("Metrics evaluated on"
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
 * Construct the window icons
 */
icoCloseWin.setPath("/");
icoCloseWin.setParameter(null);
winMetricsView.addTitleIcon(icoCloseWin);

/*
 * Construct the toolbar icons
 */
WinIcon icoRefresh = new WinIcon();
icoRefresh.setPath(request.getServletPath());
icoRefresh.setParameter("refreshMetrics");
icoRefresh.setValue("true");
icoRefresh.setAlt("Refresh");
icoRefresh.setImage("/img/icons/16x16/refresh.png");
winMetricsView.addToolIcon(icoRefresh);

/*
 * Display the window
 */
winMetricsView.setContent(dataView.getHtml(in));
out.print(winMetricsView.render(in));
%>                  </div>
                </form>
