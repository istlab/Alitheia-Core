<%@ page import="eu.sqooss.webui.*" %>

<%

// Retrieve the list of evaluated project from the connected SQO-OSS system
ProjectsListView.retrieveData(terrier);

// Generate the view dispaying all evaluated projects
String projects = ProjectsListView.getHtml();

if (projects == null) {
    out.println(error("Unable to find any evaluated projects."));
} else {
    out.println("<p />Selected:");
    Project selectedProject = ProjectsListView.getCurrentProject();
    if (selectedProject == null) {
        out.println(error("No project selected."));
    } else {
        out.println("<strong>" + selectedProject.getName() + "</strong>");
        out.println(selectedProject.getInfo());
        
        // Display the number of files in the selected project version
        // TODO: Add user interface for selecting project version. Currently
        //       defaults to the last known version
        // TODO: The files number should be cached in the Project object,
        //       instead of calling the Terrier each time.
        if (selectedProject.getLastVersion() != null) {
            Long versionNum = selectedProject.getLastVersion();
            Long versionId = selectedProject.getVersionId(versionNum);
            out.println ("<br />Files: "
                + terrier.getFilesNumber4ProjectVersion(versionId)
                + " (in version " + versionNum + ")");
        }

        // Display the first and last known project versions
        if (selectedProject.getFirstVersion() != null) {
            if (selectedProject.getFirstVersion()
                != selectedProject.getLastVersion()) {
                out.println ("<br />Versions: "
                    + selectedProject.getFirstVersion()
                    + " - "
                    + selectedProject.getLastVersion());
            }
            else {
                out.println ("<br />Version: "
                    + selectedProject.getFirstVersion());
            }
        }
        out.println("<hr>");
    }
    out.println(projects);
}

%>
</strong>