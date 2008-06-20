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

import java.util.ArrayList;
import java.util.List;

import eu.sqooss.ws.client.datatypes.WSProjectFile;

// TODO: Auto-generated Javadoc
/**
 * This class represents a File in a project or version that has been
 * evaluated by Alitheia.
 * It provides access to the files' metadata and some convenience
 * functions for display.
 * 
 * The File class is part of the high-level webui API.
 */
class File extends WebuiItem {

    /** The Constant COMMENT. */
    private static final String COMMENT = "<!-- File -->\n";

    // The short name of this file
    private String shortName;

    /** The name. */
    private String name = "FILE_NAME_UNSET";
    
    /** The status. */
    private String status = "FILE_STATUS_UNSET"; // status is one of ADDED, MODIFIED or DELETED
    
    /** The version id. */
    private Long versionId;
    
    /** The id. */
    private Long id;
    
    /** The is directory. */
    private Boolean isDirectory;
    
    // Holds the Id of the Directory DAO that match this folder's name
    Long toDirectoryId;
    
    /** The terrier. */
    private Terrier terrier;
    
    /** The results. */
    private List<Result> results = new ArrayList<Result>();

    /**
     * Initialise the File from a WSProjectFile, setting the data and storing a
     * reference to the Terrier class that can then be used to fetch additional information.
     * 
     * @param wsFile the ws file
     * @param t the t
     */
    public File (WSProjectFile wsFile, Terrier t) {
        this.versionId = wsFile.getProjectVersionId();
        this.shortName = wsFile.getShortName();
        this.name = wsFile.getFileName();
        this.status = wsFile.getStatus();
        this.id = wsFile.getId();
        this.isDirectory = wsFile.getDirectory();
        this.toDirectoryId = wsFile.getToDirectoryId();
        this.terrier = t;
    }
    
    /**
     * Initialise a file with raw data.
     * 
     * @param versionId the version id
     * @param fileId the file id
     * @param name the name
     * @param status the status
     * @param t the t
     */
    public File(Long versionId, Long fileId, String name, String status, Terrier t) {
        this.versionId = versionId;
        this.name = name;
        this.status = status;
        this.id = fileId;
        this.versionId = versionId;
        this.terrier = t;
    }

    /**
     * Gets the version.
     * 
     * @return the version
     */
    public Long getVersion () {
        return versionId;
    }

    /**
     * Gets the short name of this file.
     * 
     * @return The short name of this file.
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Gets the full name of this file (inclusive path).
     * 
     * @return The full name of this file.
     */
    public String getName () {
        return name;
    }

    /**
     * Gets the status.
     * 
     * @return the status
     */
    public String getStatus () {
        return status;
    }

    /* (non-Javadoc)
     * @see eu.sqooss.webui.WebuiItem#getId()
     */
    public Long getId() {
        return id;
    }

    public Long getToDirectoryId() {
        return toDirectoryId;
    }

    /**
     * Constructs a HTML link for selecting this file.
     * 
     * @return The HTML link for selecting this file.
     */
    public String getLink() {
        return "<a href=\"files.jsp?fid=" + id + "\">" + shortName + "</a>";
    }

    /**
     * Constructs a HTML link for selecting this folder.
     * 
     * @return The HTML link for selecting this folder.
     */
    public String getDirLink() {
        return "<a href=\"files.jsp?"
            + "did=" + toDirectoryId
            + "\">" + shortName + "/</a>";
    }

    /**
     * Gets the checks if is directory.
     * 
     * @return the checks if is directory
     */
    public Boolean getIsDirectory() {
        return isDirectory;
    }

    /**
     * Return a HTML string showing a status icon indicating whether this file
     * has been modified, deleted, added or left unchanged in the specified
     * project version.
     * 
     * @param versionId the version Id
     * 
     * @return the HTML code for the status icon
     */
    public String getStatusIcon(Long versionId) {
        String iconname = "vcs_unchanged";
        String tooltip = null;
        if ((versionId != null) && (this.versionId == versionId))
            if (status.equals("ADDED")) {
                iconname = "vcs_add";
                tooltip = "Added in this version";
            } else if (status.equals("MODIFIED")) {
                iconname = "vcs_update";
                tooltip = "Modified in this version";
            } else if (status.equals("DELETED")) {
                iconname = "vcs_remove";
                tooltip = "Removed in this version";
            }
        return icon(iconname, 0, tooltip);
    }

    /**
     * Return a HTML representation of the file state and results in the given
     * project version.
     * 
     * @param versionId the project version's Id
     * 
     * @return the HTML for this file
     */
    public String getHtml(Long versionId) {
        StringBuilder html = new StringBuilder("");
        if (getIsDirectory()) {
            html.append(getStatusIcon(versionId)
                    + "&nbsp;" + getDirLink()
                    + " (<i>Folder</i>)\n");
        }
        else {
            html.append(getStatusIcon(versionId)
                    + "&nbsp;" + getLink() + "\n");
            if (results.size() > 0) {
                html.append("<ul>\n");
                for (Result nextResult : results)
                    html.append("<li>" + nextResult.getHtml());
                html.append("</ul>\n");
            }
            else
                html.append(" (<i>No results found</i>)\n");
        }
        return html.toString();
    }

    /**
     * Adds a new evaluation result entry to the list of result for this file.
     * 
     * @param resultEntry the result entry
     */
    public void addResult(Result resultEntry) {
        if (results.contains(resultEntry) == false)
            results.add(resultEntry);
    }

}
