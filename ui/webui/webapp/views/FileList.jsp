<%@ page import="eu.sqooss.webui.*"
%><%@ page import="eu.sqooss.webui.view.*"
%><%
//============================================================================
// List all files in the selected project version
//============================================================================
if (selectedProject.isValid()) {
%>          <div id="fileslist" class="group">
<%
    Version selectedVersion = selectedProject.getCurrentVersion();
    if (selectedVersion != null) {
        // Check, if the user has switched to another directory
        if (request.getParameter("did") != null) {
            if (request.getParameter("did").equals("top"))
                selectedVersion.topDir();
            else if (request.getParameter("did").equals("prev"))
                selectedVersion.previousDir();
            else {
                Long directoryId = getId(request.getParameter("did"));
                if (directoryId != null)
                    selectedVersion.switchDir(directoryId);
            }
        }
        // Check, if the user has enabled/disabled the results overview flag
        if (request.getParameter("showResults") != null) {
            if (request.getParameter("showResults").equals("true"))
                settings.setShowFileResultsOverview(true);
            else if (request.getParameter("showResults").equals("false"))
                settings.setShowFileResultsOverview(false);
        }
        //====================================================================
        // Display the selected file verbosely
        //====================================================================
        if (request.getParameter("fid") != null) {
            // Display a verbose information about the selected file
            Long fileId = strToLong(request.getParameter("fid"));
            VerboseFileView verboseView =
                new VerboseFileView(selectedProject, fileId);
            verboseView.setTerrier(terrier);
            verboseView.setServletPath(request.getServletPath());
            verboseView.setSettings(settings);
            // Check if the user asks for a comparison with another version
            if (request.getParameter("cvnum") != null) {
                verboseView.compareAgainst(
                    strToLong(request.getParameter("cvnum")));
            }
            icoCloseWin = new WinIcon();
            icoCloseWin.setPath(request.getServletPath());
            icoCloseWin.setImage("/img/icons/16x16/application-exit.png");
            icoCloseWin.setAlt("Close");
            winContent = null;
            winContent = verboseView.getHtml(in);
            out.print(Functions.interactiveWindow(in,
                "Source file results",
                winContent, null,
                new WinIcon[]{icoCloseWin}, null));
        }
        //====================================================================
        // Display a file browser for the selected project version
        //====================================================================
        else {
            // Construct the window's content
            winContent = "";
            winFooter = null;
            List<WinIcon> toolbar = new ArrayList<WinIcon>();
            selectedVersion.setTerrier(terrier);
            selectedVersion.setSettings(settings);
            // Retrieve the list of files, if not yet done
            selectedVersion.getFilesInCurrentDirectory();
            // Check if this version is empty
            if (selectedVersion.isEmptyVersion()) {
                winContent += sp(in) + Functions.information(
                    "No files found in this project version!");
            }
            // Construct a file browser for the selected project version
            else {
                // Retrieve results from all selected metrics, if requested
                if (selectedVersion.isEmptyDir() == false) {
                    if (settings.getShowFileResultsOverview()) {
                        selectedVersion.fetchFilesResults(
                            selectedProject.getSelectedMetrics());
                    }
                }
                // Ask the user to select some metrics, if none
                if (selectedProject.getSelectedMetrics().isEmpty()) {
                    winContent += sp(in) + Functions.information(
                        "Please, select one or more source file"
                        + " related metrics.");
                }
                // Show result icons
                WinIcon icoResults = new WinIcon();
                icoResults.setPath(request.getServletPath());
                icoResults.setParameter("showResults");
                icoResults.setImage("/img/icons/16x16/view-statistics.png");
                icoResults.setStatus(
                    selectedProject.getSelectedMetrics().isEmpty() == false);
                if (settings.getShowFileResultsOverview()) {
                    icoResults.setValue(WinIcon.disable);
                    icoResults.setAlt("Hide results");
                }
                else {
                    icoResults.setValue(WinIcon.enable);
                    icoResults.setAlt("Show results");
                }
                toolbar.add(icoResults);
                // Top folder icon
                WinIcon icoTopDir = new WinIcon();
                icoTopDir.setPath(request.getServletPath());
                icoTopDir.setParameter("did");
                icoTopDir.setValue("top");
                icoTopDir.setImage("/img/icons/16x16/go-first.png");
                icoTopDir.setAlt("Top folder");
                icoTopDir.setStatus(selectedVersion.isSubDir());
                toolbar.add(icoTopDir);
                // Previous folder icon
                WinIcon icoPrevDir = new WinIcon();
                icoPrevDir.setPath(request.getServletPath());
                icoPrevDir.setParameter("did");
                icoPrevDir.setValue("prev");
                icoPrevDir.setImage("/img/icons/16x16/go-previous.png");
                icoPrevDir.setAlt("Previous folder");
                icoPrevDir.setStatus(selectedVersion.isSubDir());
                toolbar.add(icoPrevDir);

                if (selectedVersion.isEmptyDir()) {
                    winContent += sp(in + 2) + "<ul>"
                        + "<li>"
                        + Functions.icon("vcs_empty", 0, "Empty folder")
                        + "&nbsp;<i>Empty</i>"
                        + "</ul>\n";
                }
                else {
                    FileListView browser = new FileListView(
                        selectedVersion.files);
                    browser.setVersionId(selectedVersion.getId());
                    browser.setSettings(settings);
                    winContent += browser.getHtml(in + 2);
                    winFooter = browser.getStatus();
                }
            }
            out.print(Functions.interactiveWindow(in,
                "Files in version " + selectedVersion.getNumber(),
                winContent, winFooter,
                null, toolbar.toArray(new WinIcon[toolbar.size()])));
        }
    }
    else
        out.print(sp(in) + Functions.information(
            "Please select a project version first."));
}
//============================================================================
// Let the user choose a project, if none was selected
//============================================================================
else {
%>          <div id="projectslist" class="group">
<%
    // Check if the user has selected a project
    if (request.getParameter("pid") != null)
        ProjectsListView.setCurrentProject(
            strToLong(request.getParameter("pid")));
    // Retrieve the list of projects from the connected SQO-OSS framework
    ProjectsListView.retrieveData(terrier);
    if (ProjectsListView.hasProjects()) {
        ProjectsListView.setServletPath(request.getServletPath());
        out.print(Functions.simpleWindow(in, "Select a project",
            ProjectsListView.getHtml(in + 2)));
    }
    // No evaluated projects found
    else {
        out.print(sp(in) + Functions.information(
                "Unable to find any evaluated projects."));
    }
}
%>          </div>
