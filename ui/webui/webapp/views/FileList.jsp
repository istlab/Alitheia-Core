<%@ page import="eu.sqooss.webui.*" %>

<div id="fileslist" class="group">
<% // List files per selected project

if (ProjectsListView.getCurrentProject() != null) {
    // Retrieve the selected project's object
    Project selectedProject = ProjectsListView.getCurrentProject();

    // Retrieve the selected project's version ()
    Long selectedVersion = selectedProject.getCurrentVersionId();
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
    }
    else {
        out.println(Functions.error(
        "You have to <a href=\"/projects.jsp\">select</a> a project version."));
    
    }
}
else {
    out.println(Functions.error(
        "You have to <a href=\"/projects.jsp\">select</a> a project first."));
}

%>
</div>
