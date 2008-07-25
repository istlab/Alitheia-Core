<%--
==============================================================================
  This file instantiates most shared objects and defines shared variables
  commonly used by the majority of the WebUI JSP pages. Therefore, it should
  be included at the top of every JSP page.
==============================================================================
--%><%@ page import="java.util.*"
%><%@ page import="java.io.File"
%><%@ page import="eu.sqooss.webui.*"
%><%@ page import="eu.sqooss.webui.widgets.*"
%><%@ page session="true"
%><%!
// Holds the bundle with the WebUI's configuration properties
ResourceBundle configProperties = null;
// A text buffer for all error that occured during the WebUI's init phase
String initError = null;

/**
 * Initialises the WebUI when Tomcat loads it for a first time
 */
public void jspInit() {
    try {
        configProperties = ResourceBundle.getBundle("/config");
    }
    catch (MissingResourceException ex) {
        configProperties = null;
        initError = ex.toString();
    }
}
%><jsp:useBean
    id="terrier"
    class="eu.sqooss.webui.Terrier"
    scope="session"><%
    // Initialise the Terrier's configuration properties
    terrier.initConfig(configProperties);
%></jsp:useBean><jsp:setProperty name="terrier" property="*"
/><jsp:useBean
    id="user"
    class="eu.sqooss.webui.User"
    scope="session"
/><jsp:setProperty name="user" property="*"
/><jsp:useBean
    id="settings"
    class="eu.sqooss.webui.SelectedSettings"
    scope="session"
/><jsp:setProperty name="settings" property="*"
/><jsp:useBean
    id="ProjectsListView"
    class="eu.sqooss.webui.ProjectsListView"
    scope="session"
/><jsp:setProperty name="ProjectsListView" property="*"
/><jsp:useBean
    id="selectedProject"
    class="eu.sqooss.webui.Project"
    scope="session"
/><jsp:setProperty name="selectedProject" property="*"
/><jsp:useBean id="validator"
    class="eu.sqooss.webui.InputValidator"
    scope="session"
/><jsp:setProperty name="validator" property="*"
/><jsp:useBean
    id="cruncher"
    class="eu.sqooss.webui.CruncherStatus"
    scope="application"
/><%@ include file="/inc/functions.jsp"
%><%
// Set an initial size of the servlet's response
response.setBufferSize(16384);
// Retrieve the user locale
settings.setUserLocale(request.getLocale());
// Default name for this application
// TODO: Read it from the configuration bundle
String appName  = "Alitheia";
// Default page title (sub-pages can overwrite it in their scope)
String title    = "Alitheia";
// Default message content (sub-pages can overwrite it in their scope)
String msg      = "";
// Identation depth
long in = 0;
// Holds the Id of the project that the user has selected (if any)
Long projectId = null;
String winVisible = null;
String inputError = null;

//============================================================================
// Shared icons
//============================================================================
// Prepare the shared separator icon
WinIcon icoSeparator = new WinIcon();
icoSeparator.setImage("/img/icons/16x16/separator.png");

// Prepare the shared close icon
WinIcon icoCloseWin = new WinIcon();
icoCloseWin.setPath(request.getServletPath());
icoCloseWin.setImage("/img/icons/16x16/application-exit.png");
icoCloseWin.setAlt("Close");
icoCloseWin.setValue(WinIcon.MINIMIZE);

//============================================================================
// Local file storage
//============================================================================
// Folder relative to this web application's root (deployment) folder, where
// the application will store all generated temporary files.
String tempFolderName = "tmp";
File tempFolder = new File(
    config.getServletContext().getRealPath("/")
    + File.separatorChar
    + tempFolderName);
if (tempFolder.exists() == false)
    tempFolder.mkdir();
settings.setTempFolder(tempFolder);

//============================================================================
// Check if the user has selected a project (or switched to a new project)
//============================================================================
if (request.getParameter("pid") != null) {
    if (request.getParameter("pid").equals("none"))
        selectedProject.invalidate();
    else
        projectId = strToLong(request.getParameter("pid"));
}
if ((projectId != null)
    && ((selectedProject.isValid() == false)
        || (selectedProject.getId().equals(projectId)) == false)) {
    // Search for this project in the local cache first
    Project objProject = ProjectsListView.getProject(projectId);
    if (objProject != null) {
        selectedProject.flushData();
        selectedProject.setTerrier(terrier);
        selectedProject.copy(objProject);
    }
    // Retrieve the project from the SQO-OSS framework
    else {
        selectedProject.flushData();
        selectedProject.setId(projectId);
        selectedProject.retrieveData(terrier);
    }
}
//============================================================================
// Check if the user has selected a (new) project version
//============================================================================
if (selectedProject.isValid()) {
    // Construct the proper request parameter's name
    String vparam = "version" + selectedProject.getId();
    // Check if the user has selected a project version
    if (request.getParameter(vparam) != null) {
        Long versionNumber = null;
        Version selVersion = null;
        try {
            // Retrieve the selected version from the SQO-OSS framework
            versionNumber = new Long(request.getParameter(vparam));
            List<Version> versions = terrier.getVersionsByNumber(
                selectedProject.getId(), new long[]{versionNumber});
            if (versions.size() > 0)
                selVersion = versions.get(0);
            else
                inputError = new String("Version with number"
                    + " " + versionNumber
                    + " does not exist!");
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
}
%><%@ include file="/inc/login.jsp"
%><!-- init.jsp -->
