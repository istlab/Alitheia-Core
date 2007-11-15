<!-- *** menu.jsp begins here *** -->
<jsp:useBean id="cruncher" class="eu.sqooss.webui.CruncherStatus" scope="application" />
<jsp:useBean id="user" class="eu.sqooss.webui.User" scope="session" />

<div id="menu">
    <ul>
        <li><a href="/" title="Click here for no information">Home</a></li>
        <li><a href="/playground/projects.jsp" title="Click here for projects we don't do anything with yet">Projects</a></li>
        <li><a href="/playground/users.jsp" title="Click here for registered crackheads">Users</a></li>
        <li><a href="/playground/metrics.jsp" title="Click here for some imaginatory metrics">Metrics</a></li>
        <li><a href="/playground/files.jsp" title="Click here for a random list of files">Files</a></li>
        <li><a href="/login.jsp" title="Click here to log in">Login</a></li>
    </ul>
</div>

<div id="status">
    <%@ include file="/views/Message.jsp" %>
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
