<%@ include file="/inc/init.jsp" %>
<%@ include file="/inc/functions.jsp" %>

<%
    title = "Homepage";
%>
<%@ page session="true" %>
<%@ include file="/inc/header.jsp" %>

<h2>This is the Alitheia System</h2>

<%
out.println(dude());
%>
<%@ include file="/inc/news.jsp" %>


<h2>Evaluated Projects</h2>
<%@ include file="/views/EvaluatedProjectsList.jsp" %>

<hr />
<h2>Available Metrics</h2>
<%@ include file="/views/MetricsList.jsp" %>


<%@ include file="/inc/footer.jsp" %>
