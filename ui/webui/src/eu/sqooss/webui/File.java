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
 * This class represents a single project file from a project version that
 * is stored in the SQO-OSS framework.
 * <br/>
 * It provides access to the project file's metadata and some convenience
 * functions for proper HTML rendering.
 */
public class File extends WebuiItem {

    // The short name of this file
    private String shortName;

    // The status of this file in this project version
    private String status;

    // The Id of the project version where this file belongs
    private Long versionId;

    // Defines if this file is a directory or a data file
    private boolean isDirectory = false;

    // Holds the Id of the Directory DAO that match this folder's name
    Long toDirectoryId;

    // The list of results from metric that has been evaluated on this file
    private List<Result> results = new ArrayList<Result>();

    /**
     * Instantiates a new <code>File</code> and initializes its fields with
     * the data stored in the given <code>WSProjectFile</code> object.
     *
     * @param wsFile the <code>WSProjectFile</code> instance
     */
    public File (WSProjectFile wsFile) {
        if (wsFile != null) {
            setId(wsFile.getId());
            setName(wsFile.getFileName());
            this.versionId = wsFile.getProjectVersionId();
            this.shortName = wsFile.getShortName();
            this.status = wsFile.getStatus();
            this.isDirectory = wsFile.getDirectory();
            this.toDirectoryId = wsFile.getToDirectoryId();
        }
    }

    /**
     * Gets the project version's Id.
     *
     * @return The project version's Id.
     */
    public Long getVersion () {
        return versionId;
    }

    /**
     * Gets the short name of this file i.e. without the preceeding path.
     *
     * @return The short name of this file.
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Gets the file status.
     *
     * @return The file status.
     */
    public String getStatus () {
        return status;
    }

    /**
     * This method will check if this project file is a directory.
     * 
     * @return <code>true<code> when this project file is a directory,
     *   or <code>false<code> otherwise.
     */
    public boolean getIsDirectory() {
        return isDirectory;
    }

    /**
     * When this project file is a directory, then this method will return the
     * Id of the directory DAO that matches the project file's name.
     *
     * @return the Id of the matching directory DAO, or <code>null<code>
     *   when this project file is not a directory.
     */
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
     * Constructs a HTML link for selecting this directory.
     *
     * @return The rendered HTML content.
     */
    public String getDirLink() {
        return "<a href=\"files.jsp?"
            + "did=" + toDirectoryId
            + "\">" + shortName + "/</a>";
    }

    /**
     * Return a HTML string showing a status icon indicating whether this file
     * has been modified, deleted, added or left unchanged in the specified
     * project version.
     *
     * @param versionId the version Id
     *
     * @return The rendered HTML content.
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
     * Adds a new evaluation result entry to the list of result for this file.
     *
     * @param resultEntry the result entry
     */
    public void addResult(Result resultEntry) {
        if (results.contains(resultEntry) == false)
            results.add(resultEntry);
    }

    /**
     * Gets the list of results that are currently stored in this file.
     * 
     * @return The list of results.
     */
    public List<Result> getResults() {
        return results;
    }

    /* (non-Javadoc)
     * @see eu.sqooss.webui.WebuiItem#getHtml(long)
     */
    public String getHtml(long in) {
        StringBuilder b = new StringBuilder("");
        b.append(sp(in) + getShortName());
        return b.toString();
    }

    /**
     * Return a HTML representation of the file state and results in the given
     * project version.
     * 
     * @param versionId the project version's Id
     * 
     * @return The rendered HTML content.
     */
    public String getHtml(Long versionId) {
        StringBuilder html = new StringBuilder("");
        if (getIsDirectory()) {
            html.append(getStatusIcon(versionId)
                    + "&nbsp;" + getDirLink()
                    + " (<i>Folder</i>)\n");
        }
        else {
            if (results.size() > 0) {
                html.append(getStatusIcon(versionId)
                        + "&nbsp;" + getLink() + "\n");
                if (settings.getShowFileResultsOverview()) {
                    html.append("<ul>\n");
                    for (Result nextResult : results)
                        html.append("<li>" + nextResult.getMnemonic()
                                + " : " + nextResult.getHtml(0));
                    html.append("</ul>\n");
                }
            }
            else {
                html.append(getStatusIcon(versionId)
                        + "&nbsp;" + getShortName());
                if (settings.getShowFileResultsOverview())
                    html.append(" (<i>No results found</i>)");
                html.append("\n");
            }
        }
        return html.toString();
    }
}
