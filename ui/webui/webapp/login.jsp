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
      if (registerShown) {
        document.getElementById(id).style.display = 'none';
      }
      else {
        document.getElementById(id).style.display = 'block';
      }
      registerShown = !registerShown;
    }
    
    </script>
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
            <input type="button" value="Register" class="form"
              onclick="javascript:showRegister('registerForm')"/>
        </td>
    </tr>
    </table>
    
    <div id="registerForm" style="display: none;">
      Register form comes here ...
    </div>

    </form>

    <%
} else {
    out.println("You are signed in. To sign in as a different user, please sign out first.");
}
%>

<%@ include file="/inc/footer.jsp" %>
