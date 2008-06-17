<%@ page import="java.util.*" %>
<%@ page import="eu.sqooss.webui.*" %>
<%@ page session="true" %>
<%!

ResourceBundle configProperties = null;
String initError = null;


public void jspInit() {
    try {
        configProperties = ResourceBundle.getBundle("/config");
    }
    catch (MissingResourceException ex) {
        configProperties = null;
        initError = ex.toString();
    }
}
%>
<%@ include file="/inc/functions.jsp" %>
<%
response.setBufferSize(16384);


/*
    This file instantiates shared objects and defines shared variables
    commonly used by the majority of the WebUI JSP pages. Therefore, it
    should be included at the top of every JSP page.
*/

String title    = "Alitheia";
String msg      = "";

%>

<jsp:useBean id="terrier"
    class="eu.sqooss.webui.Terrier"
    scope="session">
    <%
        // Initialise the Terrier's configuration properties
        terrier.initConfig(configProperties);
    %>
    <jsp:setProperty name="terrier" property="*"/>
</jsp:useBean>

<jsp:useBean
    id="user"
    class="eu.sqooss.webui.User"
    scope="session">
    <jsp:setProperty name="user" property="loggedIn" value="false"/>
</jsp:useBean>

<jsp:useBean
    id="settings"
    class="eu.sqooss.webui.SelectedSettings"
    scope="session">
    <jsp:setProperty name="settings" property="showAllMetrics" value="true"/>
</jsp:useBean>

<jsp:useBean
    id="ProjectsListView"
    class="eu.sqooss.webui.ProjectsListView"
    scope="session">
    <jsp:setProperty name="ProjectsListView" property="*"/>
</jsp:useBean>

<%
// We also keep a Project as the session state.
// The project is either valid, and can be used to display the
// Project's information.
// It can also be invalid, that means that the id is null.
%>
<jsp:useBean
    id="selectedProject"
    class="eu.sqooss.webui.Project"
    scope="session">
    <jsp:setProperty name="selectedProject" property="*"/>
</jsp:useBean>

<%
//============================================================================
// Check if the user has selected a project (or switched to a new project)
//============================================================================
Long projectId = null;
if (request.getParameter("pid") != null)
    if (request.getParameter("pid").equals("none"))
        selectedProject.invalidate();
    else
        projectId = getId(request.getParameter("pid"));

if ((projectId != null)
        && ((selectedProject.isValid() == false)
            || (selectedProject.getId().equals(projectId)) == false)) {
        // Search for this project in the local cache first
        Project objProject = ProjectsListView.getProject(projectId);
        if (objProject != null) {
            selectedProject.setTerrier(terrier);
            selectedProject.copyFrom(objProject);
        }
        // Retrieve the project from the SQO-OSS framework
        else {
            selectedProject.setId(projectId);
            selectedProject.setTerrier(terrier);
            selectedProject.retrieveData();
        }
}

//============================================================================
// Check if the user has selected a (new) project version
//============================================================================
if (selectedProject.isValid()) {
    // Construct the proper request parameter's name
    String vparam = "version" + selectedProject.getId();

    if (request.getParameter(vparam) != null) {
        String req = request.getParameter(vparam);
        // Shortcut request for selecting the last project version
        if (req.equals("last")) {
            selectedProject.setCurrentVersionId(
                selectedProject.getLastVersion().getId());
        }
        // Shortcut request for selecting the first project version
        else if (req.equals("first")) {
            selectedProject.setCurrentVersionId(
                selectedProject.getFirstVersion().getId());
        }
        // Shortcut request for de-selecting the previously selected version
        else if (req.equals("none")) {
            selectedProject.setCurrentVersionId(null);
        }
        // Select a new project version 
        else {
            Long versionId = getId(req);
            if (versionId != null)
                selectedProject.setCurrentVersionId(versionId);
        }
    }
}
%>

<jsp:useBean id="validator"
    class="eu.sqooss.webui.InputValidator"
    scope="session"/>
<jsp:setProperty name="validator" property="*"/>

<%@ include file="/inc/login.jsp" %>
