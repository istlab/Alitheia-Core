<!-- *** menu.jsp begins here *** -->

<div id="menu">
    <ul>
        <li><a href="/" title="Click here for no information">Home</a></li>
        <li><a href="/playground/projects.jsp" title="Click here for no information">Projects</a></li>
        <li><a href="/playground/users.jsp" title="Click here for no information">Users</a></li>
        <li><a href="/playground/metrics.jsp" title="Click here for no information">Metrics</a></li>
    </ul>
</div>

<div id="status">
<!-- First call to cruncher to obtain status -->
The cruncher is offline. (That's a wild guess.)<br />
<%
java.util.Date date = new java.util.Date();
out.println( "<p />" + String.valueOf( date ));
out.println( "<p />Your IP: " + request.getRemoteHost());
out.println("<p />" + application.getServerInfo());
//response.sendRedirect( "http://www.kde.org");


%>

</div>

<!-- *** menu.jsp begins here *** -->