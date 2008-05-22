<%@ page import="eu.sqooss.webui.*" %>
<%

if (selectedProject.isValid()) {
    out.println("<div id=\"selectedproject\" class=\"group\">");

    // Update Message Box
    msg += "<br /><strong>Project:</strong> " + selectedProject.getName();
    msg += "<span class=\"forget\"><a href=\"?pid=none\">(forget)</a></span>";
    if (selectedProject.getCurrentVersion() != null) {
        msg += "<br /><strong>Version:</strong> " + selectedProject.getCurrentVersion().shortName();
    } else {
        msg += "<br />No versions recorded.";
    }
%>
<table width="100%" border="0" background="none">
    <tr>
        <td valign="top" style="padding-right: 30px" width="50%">
        <%
            // Metadata
            out.println("\n<h2>" + selectedProject.getName() + " Metadata</h2>\n");
            out.println(selectedProject.getInfo());
        %>
        </td>
        <td valign="top" width="50%" class="borderless">
        <%
            // Versions
            out.println("\n<h2>" + selectedProject.getName() + " Versions");
            // Display the number of files in the selected project version
            // TODO: The files number should be cached in the Project object,
            //       instead of calling the Terrier each time.
            //if (selectedProject.getCurrentVersionId() != null) {
                //projectId = selectedProject.getId();
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

                if (changeSelected != null && (changeSelected != selectedProject.getCurrentVersionId())) {
                    selectedProject.setCurrentVersionId(changeSelected);
                }
            }
            if (selectedProject.getCurrentVersion() != null) {
                Version currentVersion = selectedProject.getCurrentVersion();
                Long versionNum = currentVersion.getNumber();
                Long versionId = currentVersion.getId();
                // Display the first and last known project versions
                if (selectedProject.getFirstVersion() != null) {
                    if (selectedProject.getFirstVersion()
                        != selectedProject.getLastVersion()) {
                        out.println (
                            + selectedProject.getFirstVersion().getId()
                            + " - "
                            + selectedProject.getLastVersion().getId()
                            + " (" + selectedProject.countVersions() + " total)");
                    } else {
                        out.println ("<br />Version: "
                            + selectedProject.getFirstVersion());
                    }
                } else {
                    out.println("Project doesn't seem to have versions recorded.");
                }
                out.println("</h2>\n");
    
                // Show the version selector
                out.println("Choose the version you want to display: ");
                out.println(versionSelector(selectedProject));
                if (inputError != null) {
                    out.println(Functions.error(inputError));
                }
            } else {
                msg += "<br />No versions recorded.";
            }
        %>
        </td>
    </tr>
    </table>
    <%

    // Metrics
    out.println("<h2>Metrics for " + selectedProject.getName() + "</h2>");
    out.println(selectedProject.showMetrics());


    // Files
    int v_c = 0;
    String versionFileList = "";
    if (selectedProject.getCurrentVersion() != null) {
        versionFileList = selectedProject.getCurrentVersion().listFiles();
        v_c = selectedProject.getCurrentVersion().getFileCount();
    }

    //String projectFileList = selectedProject.listFiles();
    String projectFileList = selectedProject.fileStat();
    int p_c = selectedProject.getFileCount();

    out.println("\n<table width=\"100%\">\n\t<tr><td valign=\"top\" style=\"padding-right: 30px\" width=\"50%\">");

    out.println("<h2>Files in " + selectedProject.getName() + " (" + p_c + ")</h2>");
    out.println(projectFileList);

    out.println("\n\t\t</td><td valign=\"top\" width=\"50%\">");

    if (selectedProject.getCurrentVersion() != null) {
        out.println("<h2>Files in Version " + selectedProject.getCurrentVersionId() + " (" + v_c + ")</h2>");
        out.println(versionFileList);
    } else {
        out.println("No versions in Project");
    }
    out.println("\n\t\t</td>\n\t</tr>\n</table>");

    out.println("</div>"); // End of this group
    //out.println("<div style=\"margin-bottom: 20px;\"></div>");
} else {
    out.println(error("Invalid Project."));
}

%>











