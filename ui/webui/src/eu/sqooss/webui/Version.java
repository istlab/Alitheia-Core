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

import eu.sqooss.webui.datatypes.File;
import eu.sqooss.ws.client.datatypes.WSDirectory;
import eu.sqooss.ws.client.datatypes.WSProjectVersion;
import eu.sqooss.ws.client.datatypes.WSMetricsResultRequest;
import eu.sqooss.ws.client.datatypes.WSVersionStats;

/**
 * This class represents a version of a project that has been evaluated
 * by the SQO-OSS framework.
 * <br/>
 * It provides access to the version's meta-data, source files contained in
 * this version, and various methods for accessing and presenting version
 * and file based results.
 */
public class Version extends WebuiItem {

    /*
     * Project version's meta-data
     */
    private Long projectId;
    private Long number;

    /*
     * 
     */
    private Long filesNumber = null;

    /*
     * Holds the list of results from metrics that has been evaluated on this
     * project version, indexed by metric mnemonic name.
     */
    private HashMap<String, Result> results = new HashMap<String, Result>();

    /*
     * 
     */
    private WSVersionStats stats = null;

    /*
     * 
     */
    private Stack<Long> dirStack = new Stack<Long>();

    /**
     * A cache for all files that exist in this project version indexed by
     * their file Id.
     */
    public SortedMap<Long, File> files = new TreeMap<Long, File>();

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
                this.number = wsVersion.getVersion();
            }
            catch (NumberFormatException ex) {}
            this.name = this.number.toString();
            this.projectId = wsVersion.getProjectId();
        }
        setTerrier(terrier);
    }

    /**
     * Gets the Id of the project where this version belongs.
     * 
     * @return The project Id.
     */
    public Long getProjectId() {
        return projectId;
    }

    /**
     * Sets the Id of the project where this version belongs.
     * 
     * @param p the project Id
     */
    public void setProjectId(Long p) {
        projectId = p;
    }

    /**
     * Gets the number of this version.
     * 
     * @return The version's number.
     */
    public Long getNumber () {
        return number;
    }

    /**
     * Sets the number of this version.
     * 
     * @param n the version's number
     */
    public void setNumber(Long n) {
        number = n;
    }

    //========================================================================
    // FILE RETRIEVAL METHODS
    //========================================================================

    /**
     * Gets the number of files that exist in this particular project version.
     * 
     * @return The number of files in this version.
     */
    public Long getFilesNumber() {
        if (filesNumber == null)
            filesNumber = terrier.getFilesCount(id);
        return filesNumber;
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
    * Fills the local cache with the given list of files.
    * 
    * @param filesList the files list
    */
   public void setFiles(SortedMap<Long, File> filesList) {
       if (filesList != null)
           files = filesList;
   }

    /**
     * Retrieves all files that exist in this project version from the
     * attached SQO-OSS framework.
     * <br/> All files that are found will be copied into the local cache i.e.
     * the <code>files<code> member field.
     */
    public void getAllFiles() {
        if (terrier == null) return;
        if (isValid()) {
            // Fill the files cache, if empty
            if (files.isEmpty())
                for (File nextFile : terrier.getFilesInVersion(id))
                    files.put(nextFile.getId(), nextFile);
        }
        else
            terrier.addError("Invalid project version!");
    }

    /**
     * Fetch all files in the current directory that exist in this project
     * version from the attached SQO-OSS framework. All files that are found
     * will be copied into the local cache i.e. the <code>files<code> member
     * field.
     */
    public void getFilesInCurrentDirectory() {
        if (terrier == null) return;
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

    //========================================================================
    // DIRECTORY NAVIGATION METHODS
    //========================================================================

    public void switchDir(Long directoryId) {
        // Skip, if the user tries to switch to the same directory
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

    //========================================================================
    // RESULT RETRIEVAL METHODS
    //========================================================================

    public void fetchVersionResults (Map<Long, String> selectedMetrics) {
        if (isValid() == false) return;
        if ((selectedMetrics == null) || (selectedMetrics.isEmpty())) return;
        // Create an results request object
        WSMetricsResultRequest request = new WSMetricsResultRequest();
        request.setProjectVersion(true);
        // Set the selected DAO Ids
        request.setDaObjectId(new long[]{getId()});
        // Set the mnemonics of the selected metrics
        String[] mnemonics = new String[selectedMetrics.size()];
        int index = 0;
        for (String nextMnem : selectedMetrics.values()) {
            mnemonics[index++] = nextMnem;
        }
        request.setMnemonics(mnemonics);
        // Retrieve the evaluation result from the SQO-OSS framework
        for (Result nextResult : terrier.getResults(request))
            results.put(nextResult.getMnemonic(), nextResult);
    }

    public void fetchFilesResults (Map<Long, String> selectedMetrics) {
        if (isValid() == false) return;
        if ((selectedMetrics == null) || (selectedMetrics.isEmpty())) return;
        if (files.isEmpty()) return;
        // Create an results request object
        WSMetricsResultRequest request =
            new WSMetricsResultRequest();
        request.setProjectFile(true);
        // Set the selected DAO Ids
        int index = 0;
        long[] fileIds = new long[files.size()];
        for (Long nextId : files.keySet()) {
            fileIds[index++] = nextId;
        }
        request.setDaObjectId(fileIds);
        // Set the mnemonics of the selected metrics
        String[] mnemonics = new String[selectedMetrics.size()];
        index = 0;
        for (String nextMnem : selectedMetrics.values()) {
            mnemonics[index++] = nextMnem;
        }
        request.setMnemonics(mnemonics);
        // Retrieve the evaluation result from the SQO-OSS framework
        for (Result nextResult : terrier.getResults(request)) {
            if (files.containsKey(nextResult.getId()))
                files.get(nextResult.getId()).addResult(nextResult);
        }
    }

    //========================================================================
    // RESULTS RENDERING METHODS
    //========================================================================

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
        html.append(sp(in++) + "<table class=\"borderless\">\n");
        html.append(sp(in) + "<tr>"
                + "<td class=\"borderless\">" + icon("vcs_add")
                + "<strong>Files added:</strong>" + "</td>"
                + "<td class=\"borderless\">" + stats.getAddedCount() + "</td>"
                + "</tr>\n");
        html.append(sp(in) + "<tr>"
                + "<td class=\"borderless\">" + icon("vcs_update")
                + "<strong>Files modified:</strong>" + "</td>"
                + "<td class=\"borderless\">" + stats.getModifiedCount() + "</td>"
                + "</tr>\n");
        html.append(sp(in) + "<tr>"
                + "<td class=\"borderless\">" + icon("vcs_remove")
                + "<strong>Files deleted:</strong>" + "</td>"
                + "<td class=\"borderless\">" + stats.getDeletedCount() + "</td>"
                + "</tr>\n");
        html.append(sp(in) + "<tr>"
                + "<td class=\"borderless\" colspan=\"2\"><hr />" + "</td>"
                + "</tr>\n");
        html.append(sp(in) + "<tr>"
                + "<td class=\"borderless\">" + icon("vcs_status")
                + "<strong>Total files changed:</strong>" + "</td>"
                + "<td class=\"borderless\">" + stats.getTotalCount() + "</td>"
                + "</tr>\n");
        html.append(sp(--in) + "</table>\n");
        return html.toString();
    }

    /**
     * Checks, if this version contains no files
     * 
     * @return <code>true</code>, if this version is empty,
     *   or <code>false</code> otherwise.
     */
    public boolean isEmptyVersion() {
        return ((files.isEmpty()) && (dirStack.size() < 2));
    }

    public boolean isEmptyDir() {
        return files.isEmpty();
    }

    public boolean isSubDir() {
        return (dirStack.size() > 1);
    }

    public String listResults(long in) {
        StringBuilder b = new StringBuilder("");
        if (results.isEmpty()) {
            b.append(sp(in) + Functions.getMsg("No results available."));
            return b.toString();
        }
        // Display the evaluation result for this version
        b.append(sp(in++) + "<ul>\n");
        for (Result nextResult : results.values())
            b.append(sp(in) + "<li>"
                    + " Metric: " + nextResult.getMnemonic()
                    + " Result: " + nextResult.getString()
                    + "</li>");
        b.append(sp(--in) + "</ul>\n");
        return b.toString();
    }

    /* (non-Javadoc)
     * @see eu.sqooss.webui.WebuiItem#getHtml(long)
     */
    public String getHtml(long in) {
        StringBuilder html = new StringBuilder("");
        html.append(sp(in) + listResults(in));
        return html.toString();
    }

}
