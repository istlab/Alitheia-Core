<!-- *** menu.jsp begins here *** -->

<jsp:useBean id="cruncher" class="eu.sqooss.webui.CruncherStatus" scope="application" />

<div id="menu">
    <ul>
        <li><a href="/" title="Click here for no information">Home</a></li>
        <li><a href="/playground/projects.jsp" title="Click here for projects we don't do anything with yet">Projects</a></li>
        <li><a href="/playground/users.jsp" title="Click here for registered crackheads">Users</a></li>
        <li><a href="/playground/metrics.jsp" title="Click here for some imaginatory metrics">Metrics</a></li>
        <li><a href="/playground/files.jsp" title="Click here for a random list of files">Files</a></li>
        <li><a href="/login.jsp" title="Click here to log in">Login</a></li>
        <li><a href="/logout.jsp" title="Click here to log out">Logout</a></li>
    </ul>
</div>

<%
    //msg = msg + request.getParameter("msg");
    if ( (msg!=null) && msg.length() > 0 ) {
%>
<div id="sidebar">
<legend>Messages:</legend>
    <%= msg %>
</div>
<%
}
%>
<div id="sidebar">
<legend>Status:</legend>
<!-- First call to cruncher to obtain status -->
<%
cruncher.hit();
out.println(cruncher.getStatus() + "<br />");

java.util.Date date = new java.util.Date();
out.println( "<p />" + String.valueOf( date ));
out.println( "<p />Your IP: " + request.getRemoteHost());
out.println("<p />" + application.getServerInfo());

//response.sendRedirect( "http://www.kde.org");


%>

</div>

<!-- *** menu.jsp ends here *** -->
