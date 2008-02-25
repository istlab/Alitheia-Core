<!-- *** menu.jsp begins here *** -->
<jsp:useBean id="cruncher"
    class="eu.sqooss.webui.CruncherStatus"
    scope="application" />

<div id="menu">
    <ul>
        <li><a href="/" title="Click here for no information">Home</a></li>
        <li><a href="/playground/projects.jsp" title="Click here for projects we don't do anything with yet">Projects</a>
<%
if (ProjectsListView.getCurrentProject() != null) {
    out.print(" " + ProjectsListView.getCurrentProject().getName());
}
%>
        </li>
        <li><a href="/playground/users.jsp" title="Click here for registered crackheads">Users</a></li>
        <li><a href="/playground/metrics.jsp" title="Click here for some imaginatory metrics">Metrics</a></li>
        <li><a href="/playground/files.jsp" title="Click here for a random list of files">Files</a></li>
        <li><a href="/playground/terrier.jsp" title="Click here to see if Terrier works">Terriertest</a></li>
        
<%
if (!loggedIn) {
%>
        <li><a href="/login.jsp" title="Click here to log in">Login</a></li>
<%
} else {
%>
        <li><a href="/logout.jsp" title="Click here to log out">Logout</a></li>
<%
}
%>
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
out.println(cruncher.getStatus());
%>

</div>

<!-- *** menu.jsp ends here *** -->
