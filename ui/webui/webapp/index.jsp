<%@ include file="/inc/init.jsp" %>
<%
    title = "Homepage";
%>
<%@ page session="true" %>
<%@ include file="/inc/header.jsp" %>

<h2>Alitheia News</h2>
<%@ include file="/inc/news.jsp" %>


<h2>Evaluated Projects</h2>
<%@ include file="/views/EvaluatedProjectsList.jsp" %>

<%@ include file="/inc/footer.jsp" %>
