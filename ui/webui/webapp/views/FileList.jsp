<%@ page import="eu.sqooss.webui.*"
%><%@ page import="eu.sqooss.webui.view.*"
%><%@ page import="eu.sqooss.webui.widgets.*"
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
        // Check, if the user has expanded/collapsed a directory
        if (request.getParameter("dst") != null) {
            Long directoryId = getId(request.getParameter("dst"));
            if (directoryId != null)
                selectedVersion.stateDir(directoryId);
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
            Window winFileVerbose = new Window();
            winFileVerbose.setTitle("Source file results");
            winFileVerbose.addTitleIcon(icoCloseWin);
            winFileVerbose.setContent(verboseView.getHtml(in));
            out.print(winFileVerbose.render(in));
        }
        //====================================================================
        // Display a file browser for the selected project version
        //====================================================================
        else {
            // Retrieve the list of files, if not yet done
            selectedVersion.setTerrier(terrier);
            selectedVersion.setSettings(settings);
            selectedVersion.getFilesInCurrentDirectory();
            // Construct the window's content
            Window winFileBrowser = new Window();
            StringBuilder b = new StringBuilder("");
            // Check if this version is empty
            if (selectedVersion.isEmptyVersion()) {
                b.append(sp(in) + Functions.information(
                    "No files found in this project version!"));
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
                // Show result icons
                WinIcon icoResults = new WinIcon();
                icoResults.setPath(request.getServletPath());
                icoResults.setParameter("showResults");
                icoResults.setImage("/img/icons/16x16/view-statistics.png");
                icoResults.setStatus(
                    selectedProject.getSelectedMetrics().isEmpty() == false);
                if (settings.getShowFileResultsOverview()) {
                    icoResults.setValue("false");
                    icoResults.setAlt("Hide results");
                }
                else {
                    icoResults.setValue("true");
                    icoResults.setAlt("Show results");
                }
                winFileBrowser.addToolIcon(icoResults);
                                // Top folder icon
                WinIcon icoTopDir = new WinIcon();
                icoTopDir.setPath(request.getServletPath());
                icoTopDir.setParameter("did");
                icoTopDir.setValue("top");
                icoTopDir.setImage("/img/icons/16x16/go-first.png");
                icoTopDir.setAlt("Top folder");
                icoTopDir.setStatus(selectedVersion.isSubDir());
                winFileBrowser.addToolIcon(icoTopDir);
                // Previous folder icon
                WinIcon icoPrevDir = new WinIcon();
                icoPrevDir.setPath(request.getServletPath());
                icoPrevDir.setParameter("did");
                icoPrevDir.setValue("prev");
                icoPrevDir.setImage("/img/icons/16x16/go-previous.png");
                icoPrevDir.setAlt("Previous folder");
                icoPrevDir.setStatus(selectedVersion.isSubDir());
                winFileBrowser.addToolIcon(icoPrevDir);
                // Directory browser icon
                WinIcon icoDirBrowser = new WinIcon();
                icoDirBrowser.setPath(request.getServletPath());
                icoDirBrowser.setImage("/img/icons/16x16/browser.png");
                icoDirBrowser.setAlt("Directory browser");
                winVisible = "showFVDirBrowser";
                icoDirBrowser.setParameter(winVisible);
                icoDirBrowser.setValue(WinIcon.MAXIMIZE);
                if (request.getParameter(winVisible) != null) {
                    if (request.getParameter(winVisible).equals(WinIcon.MAXIMIZE))
                        settings.setShowFVDirBrowser(true);
                    else if (request.getParameter(winVisible).equals(WinIcon.MINIMIZE))
                        settings.setShowFVDirBrowser(false);
                }
                icoDirBrowser.setValue("" + !settings.getShowFVDirBrowser());
                icoDirBrowser.setStatus(!settings.getShowFVDirBrowser());
                winFileBrowser.addToolIcon(icoDirBrowser);
                // Sub-views table
                b.append(sp(in++) + "<table>\n");
                b.append(sp(in++) + "<tr>\n");
                //============================================================
                // Display the directory browser
                //============================================================
                if (settings.getShowFVDirBrowser()) {
                    b.append(sp(in++) + "<td"
                        + " style=\"width: 30%; vertical-align: top;\">\n");
                    Window winDirBrowser = new Window();
                    // Construct the window's title icons
                    icoCloseWin = new WinIcon();
                    icoCloseWin.setPath(request.getServletPath());
                    icoCloseWin.setImage("/img/icons/16x16/application-exit.png");
                    icoCloseWin.setAlt("Close");
                    winVisible = "showFVDirBrowser";
                    icoCloseWin.setParameter(winVisible);
                    icoCloseWin.setValue(WinIcon.MINIMIZE);
                    Window winFileVerbose = new Window();
                    winDirBrowser.addTitleIcon(icoCloseWin);
                    // Construct the window's content
                    if (settings.getShowFVDirBrowser()) {
                        DirBrowserView dirBrowserView =
                            new DirBrowserView(selectedVersion.directories);
                        dirBrowserView.setServletPath(
                            request.getServletPath());
                        dirBrowserView.setSelectedDir(
                            selectedVersion.getCurrentDir());
                        winDirBrowser.setContent(
                            dirBrowserView.getHtml(in + 2));
                    }
                    // Display the window
                    winDirBrowser.setTitle("Source folders");
                    b.append(winDirBrowser.render(in));
                    b.append(sp(--in) + "</td>\n");
                }
                //============================================================
                // Display the files in the selected directory
                //============================================================
                if (settings.getShowFVDirBrowser())
                    b.append(sp(in++) + "<td"
                        + " style=\"width: 70%; vertical-align: top;\">\n");
                else
                    b.append(sp(in++) + "<td"
                        + " style=\"width: 100%; vertical-align: top;\">\n");
                // Ask the user to select some metrics, if none
                if (selectedProject.getSelectedMetrics().isEmpty()) {
                    b.append(sp(in) + Functions.information(
                        "Please, select one or more source file"
                        + " related metrics."));
                    b.append(sp(in) + "<div style=\"height: 0.5em;\"></div>");
                }
                Window winFileList = new Window();
                FileListView browser = new FileListView(
                    selectedVersion.files);
                browser.setVersionId(selectedVersion.getId());
                browser.setSettings(settings);
                winFileList.setContent(browser.getHtml(in + 2));
                winFileList.setTitle("Files in "
                    + selectedVersion.getCurrentDirName());
                b.append(winFileList.render(in));
            }
            b.append(sp(--in) + "</td>\n");
            b.append(sp(--in) + "</tr>\n");
            // Close the sub-views table
            b.append(sp(--in) + "</table>\n");
            // Display the window
            winFileBrowser.setContent(b.toString());
            winFileBrowser.setTitle("Files in version "
                + selectedVersion.getNumber());
            out.print(winFileBrowser.render(in));
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
        Window winPrjList = new Window();
        winPrjList.setTitle("Evaluated projects");
        winPrjList.setContent(ProjectsListView.getHtml(in + 2));
        out.print(winPrjList.render(in));
    }
    // No evaluated projects found
    else {
        out.print(sp(in) + Functions.information(
                "Unable to find any evaluated projects."));
    }
}
%>          </div>
