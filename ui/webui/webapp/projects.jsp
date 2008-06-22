<%@ include file="/inc/init.jsp"
%><%@ page session="true"
%><%
// Indentation depth
in = 6;
// Check is there is a connection with the SQO-OSS framework
if (terrier.isConnected()) {
    if (selectedProject.isValid())
        title = "Project: " + selectedProject.getName();
    else
        title = "Evaluated Projects";
%><%@ include file="/inc/header.jsp"
%><%
    // Display the selected project's information
    if (selectedProject.isValid()) {
%><%@ include file="/views/Project.jsp"
%><%
}
// Display the list of evaluate project
    else {
%>          <div id="projectslist" class="group">
            <h2>Evaluated Projects</h2>
<%@ include file="/views/EvaluatedProjectsList.jsp"
%>          </div>
<%
    }
}
// Can not establish a connection with the SQO-OSS framework
else {
    title = "Disconnected";
%><%@ include file="/inc/header.jsp"
%><%
    out.print(sp(in) + Functions.error("Alitheia is offline."));
}
%><%@ include file="/inc/footer.jsp"
%>
