<%@ page import="eu.sqooss.webui.*" %>

<div id="fileslist" class="group">
<% // List files per selected project and version

if (selectedProject.isValid()) {

    out.println("<h2>Files for"
        + " project " + selectedProject.getName()
        + "</h2>");

    out.println(selectedProject.fileStats());
    out.println(selectedProject.listFiles());


    Version selectedVersion = selectedProject.getCurrentVersion();
    if (selectedVersion != null) {
        out.println("<h2>Files for"
            + " project " + selectedProject.getName()
            + " in version " + selectedProject.getCurrentVersionId()
            + "</h2>");

        // Display all files in the selected project version
        out.println(selectedVersion.fileStats());
        out.println(selectedVersion.listFiles());
    } else {
        out.println(Functions.error("Please select a <a href=\"/versions.jsp\">select a project version.</a>"));
    }
} else {
    out.println("<h2>Please select a select a project</h2>");

    // FIXME: Clicking on the project entries should get you to the file list
    // It now brings you to the project's dashboard
    %>
        <%@ include file="/views/EvaluatedProjectsList.jsp" %>
    <%
}

%>
</div>
