<%@ include file="/inc/init.jsp"
%><%@ page session="true"
%><%
//============================================================================
// Check is there is a connection with the SQO-OSS framework
//============================================================================
if (terrier.isConnected()) {
    if (selectedProject.isValid())
        title = "Project: " + selectedProject.getName();
    else
        title = "Evaluated Projects";
%><%@ include file="/inc/header.jsp"
%><%
    // Display the selected project's information
    if (selectedProject.isValid()) {
%><%@ include file="/views/ProjectData.jsp"
%><%
}
// Display the list of evaluate project
    else {
%>                <div id="projectslist">
<%@ include file="/views/EvaluatedProjectsList.jsp"
%>                </div>
<%
    }
}
//============================================================================
//Can not establish a connection with the SQO-OSS framework
//============================================================================
else {
%><%@ include file="/inc/Disconnected.jsp"
%><%
}
%><%@ include file="/inc/footer.jsp"
%>
