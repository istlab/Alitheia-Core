<%@ page import="eu.sqooss.webui.*" %>

<div id="fileslist" class="group">
<% // List files per selected project and version

if (selectedProject != null && selectedProject.isValid()) {
    // Retrieve the selected project's object
    //Project selectedProject = ProjectsListView.getCurrentProject();

    // Retrieve the selected project's version ()
    //Long selectedVersion = selectedProject.getCurrentVersionId();
    Long selectedVersion = selectedProject.getLastVersion().getId();
    if (selectedVersion != null) {
        selectedVersion = selectedProject.getLastVersion().getId();
    }

    if (selectedVersion != null) {
        out.println("<h2>Files for"
            + " project " + selectedProject.getName()
            + " in version " + selectedProject.getCurrentVersionId()
            + "</h2>");

        // Display all files in the selected project version
        Long versionId = selectedProject.getCurrentVersionId();
        // TODO: The files list should be cached in the Project object,
        //       instead of calling the Terrier each time.
        out.println (
            terrier.getFiles4ProjectVersion(versionId).getHtml());
    } else {
        // We should probably just list the files in the project here,
        // version-independent
        out.println(Functions.error(
        "FIXME: Please select a <a href=\"/versions.jsp\">select a project version.</a>"));
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
