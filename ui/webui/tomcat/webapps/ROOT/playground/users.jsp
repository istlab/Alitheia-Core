<% String title = "Users"; %>
<%@ page import="java.util.*" %>
<%@ include file="/inc/header.jsp" %>

<jsp :useBean id="user" class="eu.sqooss.webui.User" scope="session"/> 
<jsp :setProperty name="user" property="*"/>

<h1>Users</h1>

    <%
    String uid = request.getParameter("uid");
    if ( (uid!=null) && uid.length() > 0 ) {
        out.println("You are:");
        out.println("<ul><li> Foobar [" + uid + "]</li></ul>");
    } else {
%>
<form id="loginform">
    <input type="text" name="user_id" />
    <input type="password" name="password" />
</form>

<%
        out.println("Your User ID is not known to the system yet.");
    }
    %>

<%@ include file="/inc/footer.jsp" %>
