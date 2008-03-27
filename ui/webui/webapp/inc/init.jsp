<%
/*
    This file defines variables and defaults for the webui.
    It should be included in every JSP page, at the top.
    
    TODO: needs porting
*/

String title    = "Alitheia";
String msg      = "";
%>

<jsp:useBean id="ProjectsListView"
    class="eu.sqooss.webui.ProjectsListView"
    scope="session"/>
<jsp:setProperty name="ProjectsListView" property="*"/>

<jsp:useBean id="terrier"
    class="eu.sqooss.webui.Terrier"
    scope="session"/>
<jsp:setProperty name="terrier" property="*"/>

<jsp:useBean id="user"
    class="eu.sqooss.webui.Users"
    scope="session"/>
<jsp:setProperty name="user" property="*"/>

<%

String username = request.getParameter("username");
ProjectsListView.setProjectId(request.getParameter("pid"));
boolean loggedIn = false;
String errorMsg = "";

if (user.isLoggedIn(null)) {
    msg = "Signed in as " + user.getCurrentUsers() + ".";
    msg = msg + " <a href=\"/logout.jsp\">sign out</a>";
    loggedIn = true;
} else if ( (username!=null) && username.length() > 0 ) {
    boolean username_is_valid = false;
    Integer uid = user.getUsersId(username);
    if (uid > 0) {
        user.setCurrentUsersId(uid);
        if (user.isLoggedIn(null)) {
            msg = "<div class=\"green\">You are now signed in as ";
            msg = msg + user.getCurrentUsers() + ".";
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