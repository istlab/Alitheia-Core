 <%@ include file="/inc/init.jsp" %>
<%
title = "Testing Terrier";
%>
<%@ include file="/inc/header.jsp" %>
<%@ include file="/inc/functions.jsp" %>

<%@ page import="eu.sqooss.webui.*" %>

<jsp:useBean id="terrier" class="eu.sqooss.webui.Terrier" scope="page"/>
<jsp:setProperty name="terrier" property="*"/>


<h1>Testing Terrier</h1>

<%
Long id = new Long(1);
Project testproject = terrier.getProject(id);

if (testproject == null) {
    out.println(error(terrier.getError()));
} else {
    out.println(testproject.getHtml());
}

out.println("<hr />" + terrier.getDebug());
%>


<%@ include file="/inc/footer.jsp" %>