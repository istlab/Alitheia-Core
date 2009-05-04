<%@ page import="eu.sqooss.webui.*"
%><%@ page import="eu.sqooss.webui.view.*"
%><%@ page import="eu.sqooss.webui.widgets.*"
%><%
if (selectedProject.isValid()) {
    // Project developers
    String devsStr = "" + selectedProject.getDevelopersCount()
            + (selectedProject.getDevelopersCount() == 1
                    ? "  member"
                    : "  members");
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
    // Project mails
    String mailsStr = "" + selectedProject.getMailsCount()
            + (selectedProject.getMailsCount() == 1 
                    ? " mail"
                    : " mails");
    // Project bugs
    String bugsStr = "" + selectedProject.getBugsCount()
            + (selectedProject.getBugsCount() == 1 
                    ? " bug"
                    : " bugs");
    
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
                          <a class="piv" href="timeline.jsp"><%= bugsStr %></a>
                        </td>
                        <td class="piv piv_project">
                          <a class="piv" href="projects.jsp"><%= selectedProject.getName() %></a>
                        </td>
                        <td class="piv piv_resource" style="color: #939393;">
                          <a class="piv" href="timeline.jsp"><%= mailsStr %></a>
                        </td>
                      </tr>
                    </table>
                  </div>
                </div>
<%
}
%><!-- ProjectIntro.jsp -->
