<%@ page import="eu.sqooss.webui.*"
%><%@ page import="eu.sqooss.webui.view.*"
%><%@ page import="eu.sqooss.webui.widgets.*"
%><%
if (selectedProject.isValid()) {
    // Project developers
    String devsStr = "" + selectedProject.getDevelopersCount()
            + (selectedProject.getDevelopersCount() == 1
                    ? " developer"
                    : " developers");
    // Project versions
    String versStr = "" + selectedProject.getVersionsCount()
            + (selectedProject.getVersionsCount() == 1 
                    ? " version"
                    : " versions");
    // Files in the latest project version
    String filesStr = "" + selectedProject.getLastVersion().getFilesCount()
            + (selectedProject.getLastVersion().getFilesCount() == 1
                    ? " file"
                    : " files");
%>                <div id="selectedproject">
                  <div class="piv">
                    <table class="piv">
                      <tr>
                        <td class="piv piv_resource">
                          <a class="piv" href="developers.jsp"><%= devsStr %></a>
                        </td>
                        <td class="piv piv_resource">
                          <a class="piv" href="versions.jsp"><%= versStr %></a>
                        </td>
                        <td class="piv piv_resource">
                          <a class="piv" href="files.jsp"><%= filesStr %></a>
                        </td>
                      </tr>
                      <tr>
                        <td class="piv piv_resource" style="color: #939393;">
                          N/A bugs
                        </td>
                        <td class="piv piv_project">
                          <a class="piv" href="projects.jsp"><%= selectedProject.getName() %></a>
                        </td>
                        <td class="piv piv_resource" style="color: #939393;">
                          N/A mailing lists
                        </td>
                      </tr>
                    </table>
                  </div>
                </div>
<%
}
%><!-- ProjectIntro.jsp -->
