<%@ page import="java.util.*"
%>            <td style="width: 25%;">
              <div id="sidebar">
<%
// Set the current identation depth
in = 9;
//============================================================================
// Menu Box
//============================================================================
%>                <fieldset id="pages">
                  <legend>Menu</legend>
                  <ul>
                    <li><a href="/" title="Alitheia overview">Home</a></li>
<%
// Check is there is a connection with the SQO-OSS framework
if (terrier.isConnected()) {
    String prjLabel = selectedProject.isValid()
            ? selectedProject.getName() : "Projects";
%>                    <li><a href="/projects.jsp" title="Project details"><%=prjLabel %></a></li>
<%
    // Project related menu entries
    if (selectedProject.isValid()) {
%>                  <ul class="l2">
                      <li><a href="/timeline.jsp" title="Project Timeline">Timeline</a></li>
                      <li><a href="/versions.jsp" title="Project Versions">Versions</a></li>
                      <li><a href="/developers.jsp" title="Project Developers">Developers</a></li>
                      <li><a href="/files.jsp" title="Project Files">Files</a></li>
                  </ul>
<%
    }
%>                    <li><a href="/metrics.jsp" title="Metric details">Metrics</a></li>
<%
    // Login related menu entries
    if (user.getLoggedIn()) {
//                    <li><a href="/logout.jsp" title="Click here to log out">Logout</a></li>
    } else {
//                    <li><a href="/login.jsp" title="Click here to log in or register">Login</a></li>
    }
}
%>                  </ul>
                </fieldset>
<%
//============================================================================
// Message Box
//============================================================================
if ( (msg!=null) && msg.length() > 0 ) {
%>                <fieldset id="messages">
                  <legend>Messages</legend><%= msg %>
                </fieldset>
<%
}
//============================================================================
// Status Box
//============================================================================
%>                <fieldset id="status">
                  <legend>Status</legend>
<%
cruncher.hit();
out.println(sp(in) + cruncher.getStatus());
%>                </fieldset>
<%
//============================================================================
// Error Box
//============================================================================
if (terrier.getErrors().size() > 0) {
%>                <fieldset id="error">
                  <legend>Errors</legend>
                  <ul>
<%
    // Dump all errors from the communication with the SQO-OSS framework
    Stack<String> errors = terrier.getErrors();
    while (errors.size() > 0) {
%>                    <li style="color: red;"><%= errors.pop() %></li>
<%
    }
%>                  </ul>
                </fieldset>
<%
}
%>              </div>
            </td>
<!-- menu.jsp -->
