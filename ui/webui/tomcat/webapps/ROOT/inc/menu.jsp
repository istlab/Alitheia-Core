<!-- *** menu.jsp begins here *** -->
<jsp:useBean id="cruncher" class="eu.sqooss.webui.CruncherStatus" scope="application" />

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
