 <%@ include file="/inc/init.jsp" %>
<%
title = "Testing Terrier";
%>
<%@ include file="/inc/header.jsp" %>
<%@ include file="/inc/functions.jsp" %>

<%@ page import="java.util.*" %>
<%@ page import="eu.sqooss.webui.*" %>

<jsp:useBean id="MetricsListView" class="eu.sqooss.webui.MetricsTableView" scope="session"/>


<h1>Testing Terrier</h1>

<%

String request_id = request.getParameter("id");
StringBuilder html = new StringBuilder();
if (request_id == null) {
    Vector<Project> projects = terrier.getEvaluatedProjects();
    if (projects.size() == 0) {
        html.append(
            "<ul>Terrier didn't find any evaluated projects in the DB.</ul>");
    } else {
        html.append(
            "<h2>All Evaluated Projects</h2>\n<!-- Projects -->\n<ul>");
        for (Project p: projects) {
            html.append(
                "\n\t<li><a href=\"?id=" + p.getId()
                + "\">" + p.getName() + "</a> </li>" );
        }
    }
    html.append("\n</ul>");
} else {
    Long id = Long.parseLong(request_id);
    Project projectView = terrier.getProject(id);
    if (projectView == null) {
        html.append(error(terrier.getError()));
    } else {
        html.append(projectView.getHtml());
        if (terrier.projectHasVersion(id)) {
            html.append("<hr>");
            MetricsTableView metricView = terrier.getMetrics4Project(id);
            if (metricView == null) {
                html.append(error(terrier.getError()));
            } else {
                html.append(metricView.getHtml());
            }
        } else {
            html.append ("<br /><i>This project has not yet been evaluated</i>");
        }
    }
}

out.println(html.toString());

%>


<%@ include file="/inc/footer.jsp" %>