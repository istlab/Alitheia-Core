<%@ page session="true" %>
<%@ page import="java.util.*" %>
<%@ include file="/inc/init.jsp" %>
<%
title = "Login";

%>

<%@ include file="/inc/header.jsp" %>


<%
if (!loggedIn) {
    out.println("<h1>Login to the Alitheia System</h1>");
    out.println("<font color=\"red\">" + errorMsg + "</font>");
    %>
    <form id="loginform" method="POST">

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
} else {
    out.println("You are signed in. To sign in as a different user, please sign out first.");
}
%>

<%@ include file="/inc/footer.jsp" %>
