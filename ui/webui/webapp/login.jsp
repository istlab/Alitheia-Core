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

    <script language="JavaScript">
    var registerShown = false;
    
    function showRegister(id) {
      var displayType = (registerShown) ? 'none' : '';
      document.getElementById(id).rows[2].style.display = displayType;
      document.getElementById(id).rows[3].style.display = displayType;
      registerShown = !registerShown;
    }
    
    </script>
    <form id="loginform" method="POST">

    <table id="registerForm">
        <tr>
            <td>Username:</td>
            <td>
                <input type="text" name="username" class="form" />
            </td>
        </tr>
        <tr>
            <td>Password:</td>
            <td>
                <input type="password" name="password" class="form" />
            </td>
        </tr>
        <tr style="display: none;">
            <td>Confirm:</td>
            <td>
                <input type="password" name="confirm" class="form" />
            </td>
        </tr>
        <tr style="display: none;">
            <td>Email:</td>
            <td>
                <input type="text" name="email" class="form" />
            </td>
        </tr>
        <tr>
            <td colspan="2">
                &nbsp;
                <input type="submit" value="Sign in" class="form" />
                <input type="button" value="Register" class="form"
                    onclick="javascript:showRegister('registerForm')"/>
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
