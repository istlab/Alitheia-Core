<%
/*
    This file instaniates shared objects and defines shared variables
    commonly used by the majority of the WebUI JSP pages. Therefore, it
    should be included at the top of every JSP page.
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

<jsp:useBean id="validator"
    class="eu.sqooss.webui.InputValidator"
    scope="session"/>
<jsp:setProperty name="user" property="*"/>

<%

// Action parameter sent by various input forms
String postAction = request.getParameter("action");
if (postAction == null) {
  postAction = new String("");
}

// Login form parameters
String username = request.getParameter("username");
String password = request.getParameter("password");

// Registration form parameters
String regPassword = request.getParameter("confirm");
String regEmail = request.getParameter("email");

// Flag for authenticated user
boolean loggedIn = false;

// Flag for failed authentication or registration
boolean loginFailure = false;

ProjectsListView.setProjectId(request.getParameter("pid"));

String errorMsg = "";

if (user.isLoggedIn(null)) {
    msg = "Signed in as " + user.getCurrentUsers() + ".";
    msg = msg + " <a href=\"/logout.jsp\">sign out</a>";
    loggedIn = true;
}
// Check for registration request
else if (postAction.compareToIgnoreCase("Register") == 0) {
    if (validator.isEmpty(username)) {
        errorMsg += "Invalid username!<br />";
        loginFailure = true;
    }
    if (validator.isEmpty(password)) {
        errorMsg += "Invalid password!<br />";
        loginFailure = true;
    }
    if (validator.isEmpty(regPassword)) {
        errorMsg += "Invalid password!<br />";
        loginFailure = true;
    }
    if (validator.isEmpty(regEmail)) {
        errorMsg += "Invalid email address!<br />";
        loginFailure = true;
    }
    if (password.compareTo(regPassword) != 0) {
        errorMsg += "Passwords do not match!<br />";
        loginFailure = true;
    }
}
// Check for login request
else if (postAction.compareToIgnoreCase("Sign in") == 0) {
    if (validator.isEmpty(username)) {
        errorMsg += "Invalid username!<br />";
    }
    else {
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
}


%>