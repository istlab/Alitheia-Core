<%@ page import="eu.sqooss.webui.*" %>
<%

if (selectedProject.isValid()) {
    out.println("<div id=\"selectedproject\" class=\"group\">");

    // Update the Message Box
    if (msg.length() > 0)
        msg += "<br/>";
    msg += "<strong>Project:</strong> " + selectedProject.getName();
    msg += "<span class=\"forget\"><a href=\"?pid=none\">(forget)</a></span>";
    if (selectedProject.getCurrentVersion() != null) {
        msg += "<br /><strong>Version:</strong> " + selectedProject.getCurrentVersion().getNumber();
    } else {
        msg += "<br />No versions recorded.";
    }
%>
<table width="100%" border="0" background="none">
    <tr>
        <td valign="top" style="padding-right: 30px" width="50%">
        <%
            //================================================================
            // Display the selected project's metadata
            //================================================================
            out.println("\n<h2>" + selectedProject.getName()
                + " Metadata" + "</h2>\n");
            out.println(selectedProject.getInfo());
        %>
        </td>
        <td valign="top" width="50%" class="borderless">
        <%
            //================================================================
            // Display the selected project's version statistic and selector
            //================================================================
            String inputError = null;
            // Check if the user has selected an another project version
            String paramName = "version" + selectedProject.getId();
            if (request.getParameter(paramName) != null) {
                Long versionNumber = null;
                Version selVersion = null;
                try {
                    versionNumber = new Long(request.getParameter(paramName));
                    List<Version> versions = terrier.getVersionsByNumber(
                        selectedProject.getId(), new long[]{versionNumber});
                    if (versions.size() > 0)
                        selVersion = versions.get(0);
                    else
                        inputError = new String("Version with number"
                            + " " + versionNumber
                            + " does not exist in this project!");
                }
                catch (NumberFormatException e) {
                    inputError = new String("Wrong version number format!");
                }
                // Switch to the selected version
                if ((selVersion != null)
                    && (selVersion.getId().equals(
                        selectedProject.getCurrentVersionId()) == false)) {
                    selectedProject.setCurrentVersion(selVersion);
                }
            }
            if (selectedProject.getCurrentVersion() != null) {
                Version currentVersion = selectedProject.getCurrentVersion();
                Long versionNum = currentVersion.getNumber();
                Long versionId = currentVersion.getId();
                // Display the first and last known project versions
                if (selectedProject.getFirstVersion() != null) {
                    if (!selectedProject.getFirstVersion().equals(
                        selectedProject.getLastVersion())) {
                        out.println("\n<h2>" + selectedProject.getName()
                            + " Versions</h2>\n");
                        out.println("<strong>Versions:</strong> "
                            + selectedProject.getFirstVersion().getNumber()
                            + " - "
                            + selectedProject.getLastVersion().getNumber()
                            + "\n");
                        out.println("<br/><strong>Total:</strong> "
                            + selectedProject.countVersions() + "\n");
                    } else {
                        out.println("\n<h2>" + selectedProject.getName()
                            + " Version</h2>\n");
                        out.println("<br/><strong>Version:</strong> "
                            + selectedProject.getFirstVersion().getNumber()
                            + "\n");
                    }
                } else {
                    out.println("\n<h2>" + selectedProject.getName()
                        + "Versions</h2>\n");
                    out.println("Project doesn't seem to have versions recorded.");
                }

                // Show the version selector
                out.println("<br/><br/><strong>"
                    + "Choose the version you want to display:"
                    + "</strong><br/>");
                out.println(versionSelector(selectedProject));
                if (inputError != null) {
                    out.println(Functions.error(inputError));
                }
            } else {
                out.println("\n<h2>" + selectedProject.getName() + "Versions</h2>\n");
                out.println("Project doesn't seem to have versions recorded.");
                msg += "<br />No versions recorded.";
            }
        %>
        </td>
    </tr>
    </table>
    <%

    //========================================================================
    // Display the list of metrics that were evaluated on this project
    //========================================================================
    out.println("<h2>Metrics for " + selectedProject.getName() + "</h2>");
    out.println(selectedProject.showMetrics(false, false));


    //========================================================================
    // Display the source file statistic of the selected project
    //========================================================================
    %>
    <table width="100%">
        <tr>
            <td valign="top" style="padding-right: 30px" width="50%"></td>
            <td valign="top" width="50%">
    <%
    if (selectedProject.getCurrentVersion() != null) {
        out.println("<h2>Files in Version "
            + selectedProject.getCurrentVersion().getNumber()
            + "</h2>");
        out.println(selectedProject.getCurrentVersion().fileStats());
    } else {
        out.println("No versions in Project");
    }
    %>
            </td>
        </tr>
    </table>
</div>
    <%
} else {
    out.println(error("Invalid Project."));
}

%>
