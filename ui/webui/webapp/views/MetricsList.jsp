<%@ page import="eu.sqooss.webui.*" %>

<div id="metricslist" class="group">
    <form id="metrics" name="metrics" method="GET">
<%
//out.println(debugRequest(request));
//============================================================================
// Show metric per project, when a project selection exists
//============================================================================
if (selectedProject.isValid()) {
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
    out.println("<h2>Metrics evaluated on project: "
        + selectedProject.getName()
        + "</h2>");
    out.println(selectedProject.showMetrics(true, true));
    // Print the accumulated errors (if any)
    if (terrier.hasErrors())
        out.println(Functions.error(terrier.getError()));
}
//============================================================================
// Show all metrics installed in the SQO-OSS framework
//============================================================================
// Add some space
out.println("<br/>");
out.println("<h2>All installed metrics</h2>");
if (request.getParameter("showAllMetrics") != null) {
    // Check the "Show all installed metrics" flag
    if (request.getParameter("showAllMetrics").equals("true"))
        settings.setShowAllMetrics(true);
    else if (request.getParameter("showAllMetrics").equals("false"))
        settings.setShowAllMetrics(false);
}
// Add a link for show/hide all installed metrics
if (settings.getShowAllMetrics())
    out.println("<a href=\"/metrics.jsp?showAllMetrics=false\">"
        + "Hide all installed metrics" + "</a><br/>");
else
    out.println("<a href=\"/metrics.jsp?showAllMetrics=true\">"
        + "Show all installed metrics" + "</a><br/>");
// Display all installed metrics
if (settings.getShowAllMetrics()) {
    MetricsTableView allMetrics =
        new MetricsTableView(terrier.getAllMetrics());
    allMetrics.setShowResult(false);
    out.println(allMetrics.getHtml());
}
// Print the accumulated errors (if any)
if (terrier.hasErrors())
    out.println(Functions.error(terrier.getError()));

if (selectedProject == null) {
    %>
    <p />
    No project is selected. If you want to see metrics applied to a certain project,
    <a href="/projects.jsp">choose one</a> first.
    <%
}
%>
    </form>
</div>
