<%@ page import="eu.sqooss.webui.*" %>

<div id="metricslist" class="group">
    <form id="metrics" name="metrics" method="GET">
<%
//out.println(debugRequest(request));
//============================================================================
// Show metric per project, when a project selection exists
//============================================================================
if (selectedProject.isValid()) {
    out.println("<h2>Metrics evaluated on project: "
        + selectedProject.getName()
        + "</h2>");
    out.println(selectedProject.showMetrics(true));
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
if (request.getParameter("metricsForm") != null) {
    // Check the "Show all installed metrics" flag
    if (request.getParameter("showAllMetrics") == null)
        settings.setShowAllMetrics(false);
    else
        settings.setShowAllMetrics(true);
}
// Add a checkbox for show/hide all installed metrics
out.println("<input type=\"checkbox\""
    + ((settings.getShowAllMetrics()) ? " checked" : "")
    + " name=\"showAllMetrics\">"
    + "Show all registered metrics"
    + "&nbsp;<input type=\"submit\" value=\"Apply\">"
    + "<br/>");
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
        <input type="hidden" name="metricsForm" value="true">
    </form>
</div>
