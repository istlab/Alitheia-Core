<%@ page session="true" %>
<%@ page import="java.util.*" %>
<%@ include file="/inc/init.jsp" %>
<%
title = "Login";
%>

<%@ include file="/inc/header.jsp" %>

<%
// Display login page's title and accumulated errors (if any)
out.println("<h1>Login to the Alitheia System</h1>");
errorMsg += "<tr />" + terrier.getError();
out.println("<font color=\"red\">" + errorMsg + "</font>");
out.println("<br />");

if ((actionResult != null)
        && (actionResult.compareToIgnoreCase(RES_LOGIN_SUCCESS) == 0)) {
    out.println ("You have been successfuly loged in.");
    out.println ("<br />");
}
if ((actionResult != null)
        && (actionResult.compareToIgnoreCase(RES_REG_SUCCESS) == 0)) {
    out.println ("Thank you for your registeration to SQO-OSS!");
    out.println ("<br />");
    out.println ("A confirmation email will in short be sent to you.");
    out.println ("<br />");
}
else if (!loggedIn) {
    // Retrieve the form parameters from the previous login attempt (if any)
    String prvUsername = (validator.isEmpty(username)) ? "" : username;
    String prvPassword = (validator.isEmpty(password)) ? "" : password;
    String prvConfirm = (validator.isEmpty(regPassword)) ? "" : regPassword;
    String prvEmail = (validator.isEmpty(regEmail)) ? "" : regEmail;
    
    // Retrieve the type of the previous login page (login or register)
    String displayRegister = "display: none;";
    String loginType = "Sign in";
    String registerShown = "false";
    if (postAction.compareToIgnoreCase("Register") == 0) {
        displayRegister = "";
        loginType = "Register";
        registerShown = "true";
    }
%>

    <script language="JavaScript">
    var registerShown = <% out.print (registerShown); %>;
    
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
        <tr style="<% out.print (displayRegister); %>">
            <td>Confirm:</td>
            <td>
                <input type="password" name="confirm" class="form" 
                    value="<% out.println(prvConfirm); %>"/>
            </td>
        </tr>
        <tr style="<% out.print (displayRegister); %>">
            <td>Email:</td>
            <td>
                <input type="text" name="email" class="form" 
                    value="<% out.println(prvEmail); %>"/>
            </td>
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td align="right">
                <input type="submit" class="form" id="loginButton"
                    value="<% out.print (loginType); %>"/>
                <input type="button" class="form" value="New user"
                    onclick="javascript:showRegister('registerForm','loginButton')"/>
            </td>
        </tr>
    </table>
    <input id="action" type="hidden" name="action" value="<% out.print (loginType); %>">
    </form>

<%
} else {
    out.println("You are signed in.");
    out.println("<br />");
    out.println("To sign in as a different user, please sign out first.");
}
%>

<%@ include file="/inc/footer.jsp" %>
