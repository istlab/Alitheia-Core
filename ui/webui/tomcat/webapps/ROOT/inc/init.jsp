<%
/*
    This file defines variables and defaults for the webui.
    It should be included in every JSP page, at the top.
*/

String title = "Alitheia";
String msg = "";
%>
<jsp:useBean id="user" class="eu.sqooss.webui.User" scope="session"/>
<jsp:setProperty name="user" property="*"/>
<%

String username = request.getParameter("username");
boolean loggedIn = false;
String errorMsg = "";

if (user.isLoggedIn(null)) {
    msg = "Signed in as " + user.getCurrentUser() + ".";
    msg = msg + " <a href=\"/logout.jsp\">sign out</a>";
    loggedIn = true;
} else if ( (username!=null) && username.length() > 0 ) {
    boolean username_is_valid = false;
    Integer uid = user.getUserId(username);
    if (uid > 0) {
        user.setCurrentUserId(uid);
        if (user.isLoggedIn(null)) {
            msg = "<div class=\"green\">You are now signed in as ";
            msg = msg + user.getCurrentUser() + ".";
            msg = msg + " <a href=\"/logout.jsp\">Sign out</a></div>";
            loggedIn = true;
        } else {
            errorMsg = "You are not logged in.";
        }
    } else {
        errorMsg = "You are not known to the system.";
    }
}


%>