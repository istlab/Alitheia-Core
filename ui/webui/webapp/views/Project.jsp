<%@ page import="eu.sqooss.webui.*" %>
<%

if (selectedProject != null && selectedProject.isValid()) {
    out.println("<div id=\"selectedproject\" class=\"group\">");

    // Update Message Box
    msg += "<br /><strong>Project:</strong> " + selectedProject.getName();
    msg += "<span class=\"forget\"><a href=\"?pid=none\">(forget)</a></span>";
    msg += "<br /><strong>Version:</strong> " + selectedProject.getCurrentVersion().shortName();
%>
<table>
    <tr>
        <td valign="top" style="padding-right: 30px">
        <%
            // Metadata
            out.println("\n<h2>" + selectedProject.getName() + " Metadata</h2>\n");
            out.println(selectedProject.getInfo());
        %>
        </td>
        <td valign="top">
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
            //out.println("Number of versions: " + selectedProject.countVersions());
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
        %>
        </td>
    </tr>
    </table>
    <%

    // Metrics
    out.println("<h2>Metrics for " + selectedProject.getName() + "</h2>");
    out.println(selectedProject.showMetrics());


    // Files
    String versionFileList = selectedProject.getCurrentVersion().listFiles();
    String projectFileList = selectedProject.listFiles();
    int v_c = selectedProject.getCurrentVersion().getFileCount();
    int p_c = selectedProject.getFileCount();

    out.println("\n<table width=\"100%\">\n\t<tr><td style=\"padding-right: 30px\">");

    out.println("<h2>Files in " + selectedProject.getName() + " (" + p_c + ")</h2>");
    out.println(projectFileList);

    out.println("\n\t\t</td><td>");

    out.println("<h2>Files in Version " + selectedProject.getCurrentVersionId() + " (" + v_c + ")</h2>");
    out.println(versionFileList);

    out.println("\n\t\t</td>\n\t</tr>\n</table>");

    out.println("</div>"); // End of this group
    out.println("<div style=\"margin-bottom: 20px;\"></div>");
} else {
    out.println(error("Invalid Project."));
}

%>











