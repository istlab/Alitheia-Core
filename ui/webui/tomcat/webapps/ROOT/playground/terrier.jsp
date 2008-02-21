 <%@ include file="/inc/init.jsp" %>
<%
title = "Testing Terrier";
%>
<%@ include file="/inc/header.jsp" %>
<%@ include file="/inc/functions.jsp" %>

<%@ page import="java.util.*" %>
<%@ page import="eu.sqooss.webui.*" %>
<%@ page import="eu.sqooss.ws.client.datatypes.*" %>

<jsp:useBean id="terrier" class="eu.sqooss.webui.Terrier" scope="page"/>
<jsp:setProperty name="terrier" property="*"/>


<h1>Testing Terrier</h1>

<%

String request_id = request.getParameter("id");
if (request_id == null) {
    Vector<Project> projects = terrier.getEvaluatedProjects();
    if (projects.size() == 0) {
        out.println("<ul>Terrier didn't find any evaluated projects in the DB.</ul>");
    } else {
        StringBuilder html = new StringBuilder("<h2>All Evaluated Projects</h2>\n<!-- Projects -->\n<ul>");
        for (Project p: projects) {
            html.append("\n\t<li><a href=\"?id=" + p.getId() + "\">" + p.getName() + "</a> </li>" );
        }
        out.println(html.toString());
    }
    out.println("\n</ul>");
} else {
    Long id = Long.parseLong(request_id);
    WSStoredProject testproject = terrier.getProject(id);
    if (testproject == null) {
        out.println(error(terrier.getError()));
    } else {
        //out.println(testproject.getRepository());
        StringBuilder html = new StringBuilder(
            "\n<h2>Project: " + testproject.getName() + "</h2>");
        html.append("\nRepository: " + testproject.getRepository());
        out.println(html.toString());
    }
}


Long id = new Long(1);




out.println("<hr />" + terrier.getDebug());


%>


<%@ include file="/inc/footer.jsp" %>