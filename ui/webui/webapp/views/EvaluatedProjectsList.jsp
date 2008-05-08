<%@ page import="eu.sqooss.webui.*" %>

<%!
public static String versionSelector(Long projectId, Long currentVer) {
    String form =
        "<form id=\"selectversion\">"
        + "Current version:"
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

if (ProjectsListView.hasProjects()) {
    // Generate the HTML content dispaying all evaluated projects
    String projects = ProjectsListView.getHtml();

    Project selectedProject = ProjectsListView.getCurrentProject();
    if (selectedProject != null) {
        out.println("<div id=\"selectedproject\" class=\"group\">");
        out.println("Currently selected:");
        out.println("<strong>" + selectedProject.getName() + "</strong>");
        out.println("<span class=\"forget\"><a href=\"?pid=none\">(forget)</a></span>");

        out.println(selectedProject.getInfo());

        // Display the number of files in the selected project version
        // TODO: The files number should be cached in the Project object,
        //       instead of calling the Terrier each time.
        if (selectedProject.getCurrentVersionId() != null) {
            Long projectId = selectedProject.getId();
            String inputError = null;
            if (request.getParameter("version" + projectId) != null) {
                Long changeSelected = null;
                try {
                    changeSelected =
                        new Long(request.getParameter("version" + projectId));
                }
                catch (NumberFormatException e) {
                    inputError = new String("Wrong version format!");
                }
                if ((changeSelected != null)
                    && (changeSelected != selectedProject.getCurrentVersionId())) {
                    if (selectedProject.getCurrentVersionId() != null) {
                        selectedProject = ProjectsListView.getCurrentProject();
//.setVersion(changeSelected);
                    }
                    else {
                        inputError = "No such project version!";
                    }
                }
            }
            Long versionNum = selectedProject.getCurrentVersion().getName();
            Long versionId = selectedProject.getCurrentVersion().getId();
            out.println ("<br />Files: "
                + terrier.getFilesNumber4ProjectVersion(versionId)
                + " (in version " + versionNum + ")");
            out.println (versionSelector(projectId, versionNum));
            if (inputError != null) {
                out.println(Functions.error(inputError));
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
            } else {
                out.println ("<br />Version: "
                    + selectedProject.getFirstVersion());
            }
        }
        out.println("<pre>" + selectedProject.listFiles() + "</pre>");
        out.println("</div>"); // End of this group
        out.println("<div style=\"margin-bottom: 20px;\"></div>");
    }
    out.println("<div id=\"projectslist\" class=\"group\">");
    out.println(projects);
    out.println("</div>");
}
else {
    out.println("<div id=\"error\" class=\"group\">");
    if (cruncher.isOnline()) {
        out.println(Functions.error("Unable to find any evaluated projects."));
    } else {
        out.println(cruncher.getStatus());
    }
    out.println("</div>");
}

%>

