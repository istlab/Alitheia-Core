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

import eu.sqooss.webui.Result.ResourceType;
import eu.sqooss.ws.client.datatypes.WSProjectVersion;
import eu.sqooss.ws.client.datatypes.WSMetric;
import eu.sqooss.ws.client.datatypes.WSMetricsResultRequest;
import eu.sqooss.ws.client.datatypes.WSVersionStats;

/**
 * This class represents a Version of a project that has been evaluated
 * by Alitheia.
 * It provides access to version metadata and files in this version.
 *
 * The Version class is part of the high-level webui API.
 */
public class Version extends WebuiItem {

    private static final String COMMENT = "<!-- Version -->\n";
    private Long projectId;
    private Long number;
    private Long filesNumber = null;
    private Result[] results;
    private WSVersionStats stats = null;

    /** Empty ctor, only sets the jsp page that can be used to display
     * details about this Version.
     */
    public Version () {
        page = "version.jsp";
    }

    /**
     * Creates a new a <code>Version</code> instance from the given
     * <code>WSProjectVersion</code> object.
     */
    public Version (WSProjectVersion wsVersion, Terrier t) {
        id = wsVersion.getId();
        terrier = t;
        number = wsVersion.getVersion();
        name = "" + number;
        projectId = wsVersion.getProjectId();
    }

    /** Initialise some data of this Version. This method can be used when we
     * don't have a WSProjectVersion to use for data initialisation, or if we
     * don't want to use one for performance reasons.
     */
    public Version(Long projectId, Long versionId, Terrier t) {
        id = versionId;
        this.projectId = projectId;
        terrier = t;
        fetchVersionResults();
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
            filesNumber = terrier.getFilesNumber4ProjectVersion(id);
        return filesNumber;
    }

    /** Fetch the files that are part of this particular Version from the SCL.
     * After this method has been called the files in this Version are available
     * in the files member.
     */
    public void getFiles () {
        if (!isValid()) {
            addError("no ID in getFiles()");
            return;
        }
        fs = terrier.getProjectVersionFiles(id);
        fileCount = fs.size();
        if ( fs == null || fs.size() == 0 ) {
            return;
        }
        files = new TreeMap<Long, File>();
        Iterator<File> filesIterator = fs.iterator();
        while (filesIterator.hasNext()) {
            File nextFile = filesIterator.next();
            files.put(nextFile.getId(), nextFile);
        }
    }

    public String fileStats() {
        // Fetch the version's statistic if not already performed
        if ((stats == null) && (getId() != null)) {
            long[] versionIds = {this.getId().longValue()};
            WSVersionStats[] wsstats =
                terrier.getVersionsStatistics(versionIds);
            if ((wsstats != null) && (wsstats.length > 0))
                stats = wsstats[0];
        }
        // No statistics available
        if (stats == null)
            return "No statistics available for the selected version!";
        // Render the statistics page
        StringBuilder html = new StringBuilder("\n\n<table>");
        html.append("\n\t<tr><td>" + icon("vcs_add") +
                "<strong>Files added:</strong></td>\n\t<td>"
                + stats.getAddedCount() + "</td></tr>");
        html.append("\n\t<tr><td>" + icon("vcs_update") +
                "<strong>Files modified:</strong></td>\n\t<td>"
                + stats.getModifiedCount() + "</td></tr>");
        html.append("\n\t<tr><td>" + icon("vcs_remove") +
                "<strong>Files deleted:</strong></td>\n\t<td>"
                + stats.getDeletedCount() + "</td></tr>");
        html.append("\n\t<tr><td colspan=\"2\"><hr /></td></tr>");
        html.append("\n\t<tr><td>" + icon("vcs_status") +
                "<strong>Total files changed:</strong></td><td>"
                + stats.getTotalCount() + "</td>\n\t</tr>");
        html.append("\n</table>");
        return html.toString();
    }


    /**
     * Return an HTML list of all files in this version combined with results
     * from the metrics that were selected for this project.
     *
     * @param project the project object
     *
     * @return The files list as HTML.
     */
    public String listFiles(Project project) {
        // TODO: Replace with a file browser!
        
        // Retrieve the list of files if not yet done
        if (files == null)
            getFiles();
        // Render the files list (plus eval. results) page
        StringBuilder html = new StringBuilder();
        if (files != null) {
            if (project != null) {
                Map<Long, String> selectedMetrics = project.getSelectedMetricMnenmonics();
                if (selectedMetrics.size() > 0) {
                    fetchFilesResults(selectedMetrics);
                }  else {
                    html.append(Functions.error("No Metrics have been selected, select a metric <a href=\"metrics.jsp\">here</a> to view results."));
                }
            }
            FileListView view = new FileListView(files);
            html.append(view.getHtml());
            return html.toString();
        }
        return "<strong>No files found for this project version!</strong>";
    }

    /** Count the number of files and store this number internally.
     */
    public void setFileCount(Integer n) {
        fileCount = n;
    }

    public int getFileCount() {
        return fileCount;
    }

    /** Set the internal list of Files.
     *
     */
    public void setFiles(SortedMap<Long, File> f) {
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
            Result[] results = terrier.getResults(resultRequest);
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
        html.append("Found: " + results.length);
        html.append("\n<ul>");
        for (int i = 0; i < results.length; i++) {
            html.append("\n\t<li>" + results[i].getHtml() + "</li>");
        }
        html.append("</ul>");
        return html.toString();
    }

    /** Return an HTML representation of this Version.
     *
     */
    public String getHtml() {
        StringBuilder html = new StringBuilder(COMMENT);
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
        return getHtml(); // Yeah, we're lazy.
    }
}
