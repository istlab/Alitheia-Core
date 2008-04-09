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
    
    // Retrieve the form parameters from the previous login attempt (if any)
    String prvUsername = (validator.isEmpty(username)) ? "" : username;
    String prvPassword = (validator.isEmpty(password)) ? "" : password;
    String prvConfirm = (validator.isEmpty(regPassword)) ? "" : regPassword;
    String prvEmail = (validator.isEmpty(regEmail)) ? "" : regEmail;
%>

    <script language="JavaScript">
    var registerShown = false;
    
    function showRegister(table,button) {
      var displayType = (registerShown) ? 'none' : '';
      var actionValue = (registerShown) ? 'Sign in' : 'Register';
      document.getElementById(table).rows[2].style.display = displayType;
      document.getElementById(table).rows[3].style.display = displayType;
      document.getElementById(button).value = actionValue;
      document.getElementById("action").value = actionValue;
      registerShown = !registerShown;
    }
    
    </script>
    <form id="loginform" method="POST">

    <table id="registerForm">
        <tr>
            <td>Username:</td>
            <td>
                <input type="text" name="username" class="form" 
                  value="<% out.println(prvUsername); %>"/>
            </td>
        </tr>
        <tr>
            <td>Password:</td>
            <td>
                <input type="password" name="password" class="form" 
                  value="<% out.println(prvPassword); %>"/>
            </td>
        </tr>
        <tr style="display: none;">
            <td>Confirm:</td>
            <td>
                <input type="password" name="confirm" class="form" 
                  value="<% out.println(prvConfirm); %>"/>
            </td>
        </tr>
        <tr style="display: none;">
            <td>Email:</td>
            <td>
                <input type="text" name="email" class="form" 
                  value="<% out.println(prvEmail); %>"/>
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
    <input id="action" type="hidden" name="action" value="Sign in">
    </form>

    <%
} else {
    out.println("You are signed in. To sign in as a different user, please sign out first.");
}
%>

<%@ include file="/inc/footer.jsp" %>
