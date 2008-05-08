<%@ page import="eu.sqooss.webui.*" %>

<%@ include file="/inc/functions.jsp" %>

<div id="fileslist" class="group">
<% // List files per selected project

if (ProjectsListView.getCurrentProject() != null) {
    Project selectedProject = ProjectsListView.getCurrentProject();
    // Display the files in the selected project version
    // TODO: Add user interface for selecting project version. Currently
    //       defaults to the last known version
    // TODO: The files list should be cached in the Project object,
    //       instead of calling the Terrier each time.
    if (selectedProject.getCurrentVersion() != null) {
        Long versionNum = selectedProject.getCurrentVersion();
        Long versionId = selectedProject.getVersionId(versionNum);
        out.println (
            terrier.getFiles4ProjectVersion(versionId).getHtml());
    }
}
else {
    out.println(Functions.error("You need to select a project first."));
}

%>
</div>
