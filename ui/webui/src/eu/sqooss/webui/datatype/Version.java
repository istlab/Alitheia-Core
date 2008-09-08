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

package eu.sqooss.webui.datatype;

import java.util.*;

import eu.sqooss.webui.Functions;
import eu.sqooss.webui.Result;
import eu.sqooss.webui.Terrier;
import eu.sqooss.webui.util.Directory;
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
public class Version extends AbstractDatatype {

    /*
     * Project version's specific meta-data
     */
    protected Long projectId;
    protected Long committerId;
    protected Long number;
    protected Date timestamp;

    /*
     * 
     */
    protected Long filesNumber = null;

    /*
     * 
     */
    protected WSVersionStats stats = null;

    public HashMap<Long, Directory> directories =
        new HashMap<Long, Directory>();

    /*
     * 
     */
    protected Stack<Long> dirHistory = new Stack<Long>();

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
            this.committerId = wsVersion.getCommitterId();
            /*
             * NOTE: The Timestamp has to multiplied with 1000, since
             * <code>eu.sqooss.impl.service.updater.SourceUpdater</code> does
             * divide it on 1000 for an unknown reason.
             */
            timestamp = new Date(wsVersion.getTimestamp() * 1000);
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

    public Long getCommitterId() {
        return committerId;
    }

    public void setCommiterId(Long commiterId) {
        this.committerId = commiterId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    //========================================================================
    // FILE RETRIEVAL METHODS
    //========================================================================

    /**
     * Gets the total number of files that exist in this particular project
     * version.
     * 
     * @return Total number of files in this version.
     */
    public Long getFilesCount() {
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
                if (dirHistory.isEmpty()) {
                    WSDirectory rootDir = terrier.getRootDirectory(projectId);
                    if (rootDir != null) {
                        dirHistory.push(rootDir.getId());
                        if (directories.containsKey(rootDir.getId()) == false) {
                            Directory dir =
                                new Directory(rootDir.getId(), "ROOT");
                            dir.setCollapsed(false);
                            directories.put(rootDir.getId(), dir);
                        }
                    }
                }
                // Fetch all files in the current directory for this version
                if (dirHistory.size() > 0) {
                    Long curDirId = dirHistory.peek();
                    List<File> filesList = terrier.getFilesInDirectory(
                            getId(), curDirId);
                    if (filesList.size() > 0) {
                        files = new TreeMap<Long, File>();
                        for (File nextFile : filesList) {
                            files.put(nextFile.getId(), nextFile);
                            if (nextFile.getIsDirectory()) {
                                Directory parent = directories.get(curDirId);
                                Directory child = new Directory(
                                        nextFile.getToDirectoryId(),
                                        nextFile.getShortName());
                                if (directories.containsKey(child.getId()) == false) {
                                    child.setParent(parent.getId());
                                    directories.put(child.getId(), child);
                                    parent.addChild(child.getId());
                                }
                            }
                        }
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
        // Switch to the selected directory
        files.clear();
        dirHistory.push(directoryId);
        Directory dir = directories.get(getCurrentDir());
            if (dir != null) dir.setCollapsed(false);
    }

    public void stateDir(Long directoryId) {
        Directory dir = directories.get(directoryId);
        if (dir != null) {
            dir.setCollapsed(!dir.isCollapsed());
            if (dir.isCollapsed() == false)
                switchDir(dir.getId());
            else if (dir.isRoot() == false)
                switchDir(dir.getParent());
        }
    }

    private void collapseChilds(Directory dir) {
        if (dir == null) return;
        for (Long childId : dir.getChilds()) {
            Directory child = directories.get(childId);
            if (child != null) {
                child.setCollapsed(true);
                collapseChilds(child);
            }
        }
    }

    public void previousDir() {
        // Shift one level higher in the directory history
        files.clear();
        if (dirHistory.size() > 0) {
            dirHistory.pop();
            Directory dir = directories.get(getCurrentDir());
            if (dir != null) {
                dir.setCollapsed(false);
                collapseChilds(dir);
                while (dir.isRoot() == false) {
                    dir = directories.get(dir.getParent());
                    if (dir == null) break;
                    dir.setCollapsed(false);
                }
            }
        }
    }

    public void topDir() {
        // Switch to the root directory
        files.clear();
        dirHistory.clear();
        directories.clear();
    }

    public Long getCurrentDir() {
        if (dirHistory.isEmpty())
            return null;
        else
            return dirHistory.peek();
    }
    
    public String getCurrentDirName() {
        if (dirHistory.size() > 0) {
            Directory currentDir = directories.get(dirHistory.peek());
            if (currentDir != null)
                return currentDir.getName();
        }
        return null;
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
        long total = stats.getAddedCount()
                + stats.getModifiedCount()
                + stats.getDeletedCount();
        html.append(sp(in) + "<tr>"
                + "<td class=\"borderless\">" + icon("vcs_status")
                + "<strong>Total files changed:</strong>" + "</td>"
                + "<td class=\"borderless\">" + total + "</td>"
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
        return ((files.isEmpty()) && (dirHistory.size() < 2));
    }

    public boolean isEmptyDir() {
        return files.isEmpty();
    }

    public boolean isSubDir() {
        return (dirHistory.size() > 1);
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
     * @see eu.sqooss.webui.datatype.AbstractDatatype#getResults(java.util.Collection, java.lang.Long)
     */
    @Override
    public HashMap<String, Result> getResults (
            Collection<String> mnemonics, Long resourceId) {
        /*
         * Return an empty list upon invalid parameters
         */
        if ((resourceId == null) || (mnemonics == null))
            return new HashMap<String, Result>();
        /*
         * Skip already retrieved metric results.
         */
        for (String mnemonic : results.keySet()) {
            if (mnemonics.contains(mnemonic))
                mnemonics.remove(mnemonic);
        }
        /*
         * Construct the result request's object.
         */
        if (mnemonics.size() > 0) {
            WSMetricsResultRequest reqResults = new WSMetricsResultRequest();
            reqResults.setDaObjectId(new long[]{resourceId});
            reqResults.setProjectVersion(true);
            reqResults.setMnemonics(
                    mnemonics.toArray(new String[mnemonics.size()]));
            /*
             * Retrieve the evaluation results from the SQO-OSS framework
             */
            for (Result nextResult : terrier.getResults(reqResults))
                results.put(nextResult.getMnemonic(), nextResult);
        }
        return results;
    }

    /* (non-Javadoc)
     * @see eu.sqooss.webui.WebuiItem#getHtml(long)
     */
    @Override
    public String getHtml(long in) {
        StringBuilder html = new StringBuilder("");
        html.append(sp(in) + listResults(in));
        return html.toString();
    }

}
