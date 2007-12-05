<%@ page session="true" %>
<%@ page import="java.util.*" %>
<%@ include file="/inc/init.jsp" %>
<%

title = "Login";
String username = request.getParameter("username");
boolean showForm = true;
String errorMsg = "";

%>

<jsp:useBean id="user" class="eu.sqooss.webui.User" scope="session"/>
<jsp:setProperty name="user" property="*"/>


<%

if (user.isLoggedIn(null)) {
    msg = "Signed in as " + user.getCurrentUser() + ".";
    msg = msg + " <a href=\"/logout.jsp\">sign out</a>";
    showForm = false;
} else if ( (username!=null) && username.length() > 0 ) {
    boolean username_is_valid = false;
    Integer uid = user.getUserId(username);
    if (uid > 0) {
        user.setCurrentUserId(uid);
        if (user.isLoggedIn(null)) {
            msg = "<div class=\"green\">You are now signed in as ";
            msg = msg + user.getCurrentUser() + ".";
            msg = msg + " <a href=\"/logout.jsp\">Sign out</a></div>";
            showForm = true;
        } else {
            errorMsg = "You are not logged in.";
        }
    } else {
        errorMsg = "You are not known to the system.";
    }
}
%>

<%@ include file="/inc/header.jsp" %>

<h1>Login to the Alitheia System</h1>

<%
if (showForm) {
    out.println("<font color=\"red\">" + errorMsg + "</font>");
    %>
    <form id="loginform">

    <table>
    <tr>
        <td>
            Username:
        </td>
        <td>
            <input type="text" name="username" class="form" />
        </td>
    </tr><tr>
        <td>
            Password:
        </td>
        <td>
            <input type="password" name="password" class="form" />
        </td>
    </tr><tr>
        <td>
            &nbsp;
        </td>
        <td>
            <input type="submit" value="Sign in" class="form" />
        </td>
    </tr>
    </table>

    </form>

    <%
}
%>

<%@ include file="/inc/footer.jsp" %>
