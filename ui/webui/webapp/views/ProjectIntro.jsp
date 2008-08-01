<%@ page import="eu.sqooss.webui.*"
%><%@ page import="eu.sqooss.webui.view.*"
%><%@ page import="eu.sqooss.webui.widgets.*"
%><%
if (selectedProject.isValid()) {
%>                <div id="selectedproject">
                  <div class="piv">
                    <table class="piv">
                      <tr>
                        <td class="piv piv_resource"><%
    // =======================================================================
    // Project developers
    // =======================================================================
    long developersCount = selectedProject.getDevelopersCount();
        out.print("<a class=\"piv\" href=\"developers.jsp\">");
    out.print(developersCount);
    if (developersCount == 1)
        out.print(" developer");
    else
        out.print(" developers");
    out.print("</a>");
%></td>
                        <td class="piv piv_resource"><%
    // =======================================================================
    // Project versions
    // =======================================================================
    long versionsCount = selectedProject.getVersionsCount();
    out.print("<a class=\"piv\" href=\"projects.jsp\">");
    out.print(versionsCount);
    if (versionsCount == 1)
        out.print(" version");
    else
        out.print(" versions");
    out.print("</a>");
%></td>
                        <td class="piv piv_resource"><%
    // =======================================================================
    // Files in the latest project version
    // =======================================================================
    long filesCount = selectedProject.getLastVersion().getFilesCount();
        out.print("<a class=\"piv\" href=\"files.jsp\">");
    out.print(filesCount);
    if (filesCount == 1)
        out.print(" file");
    else
        out.print(" files");
    out.print("</a>");
%></td>
                      </tr>
                      <tr>
                        <td class="piv piv_resource" style="color: #939393;">N/A bugs</td>
                        <td class="piv piv_project"><%
    // =======================================================================
    // Project name
    // =======================================================================
    out.print("<a class=\"piv\" href=\"projects.jsp\">");
    out.print(selectedProject.getName());
    out.print("</a>");
%></td>
                        <td class="piv piv_resource" style="color: #939393;">N/A mailing lists</td>
                      </tr>
                    </table>
                  </div>
                </div>
<%
}
%><!-- ProjectIntro.jsp -->
