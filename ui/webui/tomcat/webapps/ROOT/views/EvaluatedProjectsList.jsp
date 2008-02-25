<%@ page import="eu.sqooss.webui.*" %>

<%

ProjectsListView.retrieveData(terrier);
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
        if (selectedProject.getVersionLow() != null) {
            if ((selectedProject.getVersionHigh() != null)
                && (selectedProject.getVersionLow()
                    != selectedProject.getVersionHigh())) {
                out.println ("<br />Versions: "
                    + selectedProject.getVersionLow()
                    + " - "
                    + selectedProject.getVersionHigh());
            }
            else {
                out.println ("<br />Version: "
                    + selectedProject.getVersionLow());
            }
        }
        out.println("<hr>");
    }
    out.println(projects);
}

%>
</strong>