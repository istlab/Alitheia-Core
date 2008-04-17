<!-- *** menu.jsp begins here *** -->
<jsp:useBean id="cruncher"
    class="eu.sqooss.webui.CruncherStatus"
    scope="application" />

    <div id="sidebar">
      <fieldset id="pages">
        <legend>Menu</legend>
        <ul>
          <li><a href="/" title="Alitheia overview">Home</a></li>
          <li><a href="/projects.jsp" title="Project details">Projects</a></li>
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
      </fieldset>

<%
    //msg = msg + request.getParameter("msg");
    if ( (msg!=null) && msg.length() > 0 ) {
%>
      <fieldset id="messages">
        <legend>Messages</legend>
<%= msg %>
      </fieldset>
<%
}
%>
      <fieldset id="status">
        <legend>Status</legend>
<%
cruncher.hit();
out.println(cruncher.getStatus());
%>
      </fieldset>
    </div>
