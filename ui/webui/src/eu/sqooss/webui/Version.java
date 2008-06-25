/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2008 by Sebastian Kuegler <sebas@kde.org>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package eu.sqooss.webui;

import java.util.*;

import eu.sqooss.ws.client.datatypes.WSDirectory;
import eu.sqooss.ws.client.datatypes.WSProjectVersion;
import eu.sqooss.ws.client.datatypes.WSMetricsResultRequest;
import eu.sqooss.ws.client.datatypes.WSVersionStats;

/**
 * This class represents a version of a project that has been evaluated
 * by the SQO-OSS framework.
 * <br/>
 * It provides access to the version's metadata and files.
 */
public class Version extends WebuiItem {

    private Long projectId;
    private Long number;
    private Long filesNumber = null;
    private List<Result> results;
    private WSVersionStats stats = null;
    private Stack<Long> dirStack = new Stack<Long>();

    // Contains a list of all files in this version indexed by their Id
    protected SortedMap<Long, File> files = new TreeMap<Long, File>();

    /**
     * Creates a new a <code>Version</code> instance.
     */
    public Version () {}

    /**
     * Creates a new a <code>Version</code> instance, and initializes it with
     * the information provided from the given <code>WSProjectVersion</code>
     * object.
     */
    public Version (WSProjectVersion wsVersion, Terrier terrier) {
        if (wsVersion != null) {
            this.id = wsVersion.getId();
            try {
                this.name = new Long(wsVersion.getVersion()).toString();
            }
            catch (NumberFormatException ex) {}
            this.number = wsVersion.getVersion();
            this.projectId = wsVersion.getProjectId();
        }
        setTerrier(terrier);
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long p) {
        projectId = p;
    }

    public Long getNumber () {
        return number;
    }

    public void setNumber(Long n) {
        number = n;
    }

    /**
     * Return the number of files that exist in this particular project
     * version.
     * 
     * @return the files number
     */
    public Long getFilesNumber() {
        if (filesNumber == null)
            filesNumber = terrier.getFilesCount(id);
        return filesNumber;
    }

    /**
     * Retrieves all files that exist in this project version from the
     * attached SQO-OSS framework. All files that are found will be copied
     * into the <code>files<code> member field.
     */
    public void getAllFiles() {
        if (terrier == null)
            return;
        if (isValid()) {
            // Fill the files cache if empty
            if (files.isEmpty()) {
                for (File nextFile : terrier.getFilesInVersion(id))
                    files.put(nextFile.getId(), nextFile);
            }
        }
        else
            terrier.addError("Invalid project version!");
    }

    public void switchDir(Long directoryId) {
        // Skip if the user tries to switch to the same directory
        if (dirStack.peek().equals(directoryId)) return;
        // Flush the currently cached files
        files.clear();
        // Check if the user tries to switch to a higher level directory
        if (dirStack.contains(directoryId))
            while ((dirStack.isEmpty() == false)
                    || (dirStack.peek().equals(directoryId) == false))
                dirStack.pop();
        // Add the sub-directory to the stack
        else
            dirStack.push(directoryId);
    }

    public void previousDir() {
        // Shift one level higher in the directory tree
        files.clear();
        dirStack.pop();
    }

    public void topDir() {
        // Switch to the root directory
        files.clear();
        dirStack.clear();
    }

    /**
     * Fetch all files in the current directory that exist in this project
     * version from the attached SQO-OSS framework. All files that are found
     * are copied into the <code>files<code> member.
     */
    public void getFilesInCurrentDirectory() {
        if (terrier == null)
            return;
        if (isValid()) {
            // Fill the files cache if empty
            if (files.isEmpty()) {
                // Initialize the version's directory tree if empty
                if (dirStack.isEmpty()) {
                    WSDirectory rootDir = terrier.getRootDirectory(projectId);
                    if (rootDir != null)
                        dirStack.push(rootDir.getId());
                }
                // Fetch all files in the current directory for this version
                if (dirStack.size() > 0) {
                    Long currentDirId = dirStack.peek();
                    List<File> filesList = terrier.getFilesInDirectory(
                            getId(), currentDirId);
                    if (filesList.size() > 0) {
                        files = new TreeMap<Long, File>();
                        for (File nextFile : filesList)
                            files.put(nextFile.getId(), nextFile);
                    }
                }
            }
        }
        else
            terrier.addError("Invalid project version!");
    }

    /**
     * Returns an HTML snippet presenting the file statistics in this project
     * version. The generated statistic will include information regarding
     * the number of added, modified or deleted files as well the total number
     * of files that exist in this version.
     * 
     * @param in the indentation depth
     * 
     * @return The rendered HTML content.
     */
    public String fileStats(long in) {
        // Fetch the version's statistic if not already performed
        if ((stats == null) && (getId() != null)) {
            long[] versionIds = {this.getId().longValue()};
            List<WSVersionStats> wsstats =
                terrier.getVersionsStatistics(versionIds);
            if (wsstats.size() > 0)
                stats = wsstats.get(0);
        }
        // No statistics available
        if (stats == null)
            return "No statistics available!";
        // Render the statistics page
        StringBuilder html = new StringBuilder("");
        html.append(sp(in++) + "<table>\n");
        html.append(sp(in) + "<tr>"
                + "<td>" + icon("vcs_add")
                + "<strong>Files added:</strong>" + "</td>"
                + "<td>" + stats.getAddedCount() + "</td>"
                + "</tr>\n");
        html.append(sp(in) + "<tr>"
                + "<td>" + icon("vcs_update")
                + "<strong>Files modified:</strong>" + "</td>"
                + "<td>" + stats.getModifiedCount() + "</td>"
                + "</tr>\n");
        html.append(sp(in) + "<tr>"
                + "<td>" + icon("vcs_remove")
                + "<strong>Files deleted:</strong>" + "</td>"
                + "<td>" + stats.getDeletedCount() + "</td>"
                + "</tr>\n");
        html.append(sp(in) + "<tr>"
                + "<td colspan=\"2\"><hr />" + "</td>"
                + "</tr>\n");
        html.append(sp(in) + "<tr>"
                + "<td>" + icon("vcs_status")
                + "<strong>Total files changed:</strong>" + "</td>"
                + "<td>" + stats.getTotalCount() + "</td>"
                + "</tr>\n");
        html.append(sp(--in) + "</table>\n");
        return html.toString();
    }

    /**
     * Gets the file with the given Id from the local cache.
     * 
     * @param fileId the file Id
     * 
     * @return The file object.
     */
    public File getFile(Long fileId) {
        if (fileId != null)
            return files.get(fileId);
        return null;
    }

    /**
     * Return an HTML list of all files in this version combined with results
     * from the metrics that were selected for this project.
     * 
     * @param project the project object
     * @param in the indentation depth
     * 
     * @return The files list as HTML.
     */
    public String listFiles(Project project, long in) {
        // Retrieve the list of files if not yet done
        //getAllFiles();
        getFilesInCurrentDirectory();
        // Check if this version contains no files
        if ((files.isEmpty()) && (dirStack.size() < 2)) {
            return (sp(in) + Functions.warning(
                    "No files found for this project version!"));
        }
        // Render the files list page (inclusive evaluation results)
        else {
            StringBuilder html = new StringBuilder();
            // Retrieve results from all selected metrics (if any)
            Map<Long, String> selectedMetrics =
                project.getSelectedMetricMnemonics();
            if ((project != null) && (files.size() > 0)) {
                if (selectedMetrics.size() > 0)
                    fetchFilesResults(selectedMetrics);
            }
            // Ask the user to select some metrics, when none are selected
            if (selectedMetrics.isEmpty()) {
                html.append(sp(in) + Functions.warning(
                        "No Metrics have been selected!"
                        + " Select a metric"
                        + " <a href=\"metrics.jsp\">here</a>"
                        + " to view results."));
            }
            // Display the browser's navigation bar
            html.append(sp(in) + "<br/>\n");
            html.append(sp(in));
            if (selectedMetrics.isEmpty() == false) {
                if (settings.getShowFileResultsOverview())
                    html.append("&nbsp;<a href=\""
                            + getServletPath()
                            + "?showResults=false"
                            + "\""
                            + " class=\"button\""
                            + ">Hide results</a>");
                else
                    html.append("&nbsp;<a href=\""
                            + getServletPath()
                            + "?showResults=true"
                            + "\""
                            + " class=\"button\""
                            + ">Show results</a>");
            }
            if ((dirStack.size() > 1)) {
                html.append("&nbsp;<a href=\""
                        + getServletPath()
                        + "?did=top" + "\""
                        + "\""
                        + " class=\"button\""
                        + ">Top</a>");
                html.append("&nbsp;<a href=\""
                        + getServletPath()
                        + "?did=prev" + "\""
                        + "\""
                        + " class=\"button\""
                        + ">Previous</a>\n");
            }
            html.append(sp(in) + "<br/>\n");
            // Display the browser's content
            if ((project != null) && (files.size() > 0)) {
                FileListView view = new FileListView(files);
                view.setVersionId(this.id);
                view.setSettings(settings);
                html.append(view.getHtml(in));
            }
            else
                html.append(sp(in) + "<ul>"
                        + "<li>"
                        + Functions.icon("vcs_empty", 0, "Empty folder")
                        + "&nbsp;<i>Empty</i>"
                        + "</ul>\n");
            return html.toString();
        }
    }

    /** Set the internal list of Files.
     *
     */
    public void setFiles(SortedMap<Long, File> f) {
        if (f != null)
            files = f;
    }

    public void fetchVersionResults () {
        // prepare ResultRequester for file retrieval
        long[] ids = {getId()};
        WSMetricsResultRequest request = new WSMetricsResultRequest();
        request.setDaObjectId(ids);
        request.setProjectVersion(true);
        String[] mnemonics = new String[1];
        mnemonics[0] = "LOC"; // FIXME: Use metric here...
        request.setMnemonics(mnemonics);
        results = terrier.getResults(request);
    }

    public void fetchFilesResults (Map<Long, String> selectedMetrics) {
        // Fetch the evaluation result for the files in the list
        if (files.size() > 0) {
            // Create an results request object
            WSMetricsResultRequest resultRequest =
                new WSMetricsResultRequest();
            int index = 0;
            long[] fileIds = new long[files.size()];
            for (File nextFile : files.values()) {
                fileIds[index++] = nextFile.getId();
            }
            resultRequest.setDaObjectId(fileIds);
            resultRequest.setProjectFile(true);
            String[] mnemonics = new String[selectedMetrics.size()];
            index = 0;
            for (String nextMnem : selectedMetrics.values()) {
                mnemonics[index++] = nextMnem;
            }
            resultRequest.setMnemonics(mnemonics);
            // Retrieve the evaluation result from the SQO-OSS framework
            List<Result> results = terrier.getResults(resultRequest);
            // Distribute the results between the files
            if (results != null) {
                // Prepare a file_id to file mapping
                //Map<Long, File> filesMap = new HashMap<Long, File>();
                //for (File nextFile : files.values())
                //    filesMap.put(nextFile.getId(), nextFile);
                for (Result nextResult : results) {
                    //File affectedFile = filesMap.get(nextResult.getId());
                    File affectedFile = files.get(nextResult.getId());
                    if (affectedFile != null) {
                        files.get(nextResult.getId()).addResult(nextResult);
                    }
                }
            }
        }
    }

    public String showResults() {
        StringBuilder html = new StringBuilder();
        html.append("Found: " + results.size());
        html.append("\n<ul>");
        for (Result nextResult : results)
            html.append("\n\t<li>" + nextResult.getHtml(0) + "</li>");
        html.append("</ul>");
        return html.toString();
    }

    /* (non-Javadoc)
     * @see eu.sqooss.webui.WebuiItem#getHtml(long)
     */
    public String getHtml(long in) {
        StringBuilder html = new StringBuilder("");
        html.append("<b>Version:</b> " + id);
        html.append("<b>Results:</b> " + showResults());
        return html.toString();
    }

    /** Return a short HTML representation of this Version.
     */
    public String shortName () {
        return "v" + id;
    }

    /** Return a longer HTML representation of this Version.
     */
    public String longName () {
        return getHtml(0); // Yeah, we're lazy.
    }

}
