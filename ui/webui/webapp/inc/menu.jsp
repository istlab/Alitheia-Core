<!-- *** menu.jsp begins here *** -->
<jsp:useBean id="cruncher"
    class="eu.sqooss.webui.CruncherStatus"
    scope="application" />

<div id="menu">
    <ul>
        <li><a href="/" title="Alitheia overview">Home</a></li>
<%
if (user.getLoggedIn()) {
%>
        <li><a href="/logout.jsp" title="Click here to log out">Logout</a></li>
<%
} else {
%>
        <li><a href="/login.jsp" title="Click here to log in or register">Login</a></li>
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
