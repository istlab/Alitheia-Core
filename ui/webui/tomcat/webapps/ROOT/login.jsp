<% String title = "Login"; %>
<%@ page import="java.util.*" %>
<%@ include file="/inc/header.jsp" %>

<jsp :useBean id="user" class="eu.sqooss.webui.User" scope="session"/> 
<jsp :setProperty name="user" property="*"/>

<h1>Users</h1>

<%
String uid = request.getParameter("uid");
if ( (uid!=null) && uid.length() > 0 ) {
    boolean uid_is_valid = false;
    Integer user_id = new Integer( 9999 );
    try {
        user_id = Integer.valueOf(uid);
        uid_is_valid = true;
    } catch (NumberFormatException e1) {
        out.println("<font color=\"red\">You supplied a bogus user ID.</font>");
    }
    if (uid_is_valid) {
        user.setCurrentUserId(user_id);
        if (user.isLoggedIn(null)) {
            out.println("You are logged in, your username is <strong>");
            out.println(user.getCurrentUser() + "</strong>.");
        } else {
            out.println("<font color=\"red\">You are not logged in. </font></strong>");
        }
    }
} else {
    // Login form
    out.println("Your User ID is not known to the system yet.");
}
%>

<%@ include file="/inc/footer.jsp" %>
