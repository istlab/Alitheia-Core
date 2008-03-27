<%@ page import="eu.sqooss.webui.*" %>

<%!
public static String versionSelector(Long projectId, Long currentVer) {
    String form =
        "<form id=\"selectversion\">"
        + "Selected version:"
        + "&nbsp;"
        + "<input type=\"text\" name=\"version" + projectId
            + "\" value=\"" + currentVer+ "\" class=\"form\"/>"
        + "&nbsp;"
        + "<input type=\"submit\" value=\"Change\"  class=\"form\"/>"
        + "</form>";
    //String form = "bl\'\"a";
    return form;
}
%>

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
        // TODO: The files number should be cached in the Project object,
        //       instead of calling the Terrier each time.
        if (selectedProject.getSelectedVersion() != null) {
            Long projectId = selectedProject.getId();
            String inputError = null;
            if (request.getParameter("version" + projectId) != null) {
                Long changeSelected = null;
                try {
                    changeSelected =
                        new Long(request.getParameter("version1"));
                }
                catch (NumberFormatException e) {
                    inputError = new String("Wrong version format!");
                }
                if ((changeSelected != null)
                    && (changeSelected != selectedProject.getSelectedVersion())) {
                    if (selectedProject.getVersionId(changeSelected) != null) {
                        selectedProject.setSelectedVersion(changeSelected);
                    }
                    else {
                        inputError = "No such project version!";
                    }
                }
            }
            Long versionNum = selectedProject.getSelectedVersion();
            Long versionId = selectedProject.getVersionId(versionNum);
            out.println ("<br />Files: "
                + terrier.getFilesNumber4ProjectVersion(versionId)
                + " (in version " + versionNum + ")");
            out.println (versionSelector(projectId, versionNum));
            if (inputError != null) {
                out.println(error(inputError));
            }
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