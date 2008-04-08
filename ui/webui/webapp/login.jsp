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
    
    function showRegister(table,button) {
      var displayType = (registerShown) ? 'none' : '';
      var buttonValue = (registerShown) ? 'Sign in' : 'Register';
      document.getElementById(table).rows[2].style.display = displayType;
      document.getElementById(table).rows[3].style.display = displayType;
      document.getElementById(button).value = buttonValue;
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
            <td>&nbsp;</td>
            <td align="right">
                <input type="submit" value="Sign in" class="form" id="loginButton"/>
                <input type="button" value="New user" class="form"
                    onclick="javascript:showRegister('registerForm','loginButton')"/>
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
