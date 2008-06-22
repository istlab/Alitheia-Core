/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008-2008 by Sebastian Kuegler <sebas@kde.org>
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

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.SortedMap;

public class FileListView extends ListView {

    private List<File> files = new Vector<File>();

    // Contains the Id of the selected project (if any)
    private Long projectId;

    // Contains the Id of the selected project's version (if any)
    private Long versionId;

    /**
     * Instantiates a new empty <code>FileListView</code> object.
     */
    public FileListView () {}

    /**
     * Instantiates a new <code>FileListView</code> object and initializes it
     * with the given list of project files.
     */
    public FileListView (Vector<File> filesList) {
        files = filesList;
    }

    /**
     * Instantiates a new <code>FileListView</code> object and initializes it
     * with the given list of project files.
     */
    public FileListView (SortedMap<Long, File> filesList) {
        for (File nextFile : filesList.values()) {
            files.add(nextFile);
        }
    }

    /**
     * Return the number of files that are stored in this object.
     *
     * @return The number of files.
     */
    public Integer size() {
        return files.size();
    }

    /**
     * Adds a single file to the stored files list.
     *
     * @param file the file object
     */
    public void addFile(File file) {
        files.add(file);
    }

    /**
     * Initializes this object with a new list of files.
     *
     * @param files the new files list
     */
    public void setFiles(List<File> filesList) {
        this.files = filesList;
    }

    /**
     * Gets the Id of the project that is associated with this view.
     *
     * @return The project Id, or <code>null</code> when none is associated.
     */
    public Long getProjectId() {
        return projectId;
    }

    /**
     * Sets the Id of the project that is associated with this view.
     *
     * @param projectId the project Id
     */
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    /**
     * Gets the Id of the project's version that is associated with this view.
     *
     * @return The version Id, or <code>null</code> when none is associated.
     */
    public Long getVersionId() {
        return versionId;
    }

    /**
     * Sets the Id of the project's version that is associated with this view.
     *
     * @param versionId the version Id
     */
    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    // TODO: Implement and document it
    public Vector<String> filterItems (Vector<String> items) {
        // TODO: Remove some files from this item list, such as Makefile, COPYING,
        // or alternatively, only show a list of certain extensions, like .cpp, .h ...
        return items;
    }

    // TODO: Document it
    public String getHtml(long in) {
        StringBuffer html = new StringBuffer();
        html.append(sp(in++) + "<ul>\n");
        // Display all folders first
        long dirCount = 0;
        for (File nextFile : this.files) {
            if (nextFile.getIsDirectory()) {
                dirCount++;
                html.append((nextFile != null)
                        ? sp(in) + "<li>" + nextFile.getHtml(this.versionId)
                                + sp(in) + "</li>\n"
                        : "");
            }
        }
        // Display all files
        long fileCount = 0;
        for (File nextFile : this.files) {
            if (nextFile.getIsDirectory() == false) {
                fileCount++;
                html.append((nextFile != null)
                        ? sp(in) + "<li>" + nextFile.getHtml(this.versionId)
                                + sp(in) + "</li>\n"
                        : "");
            }
        }
        html.append(sp(--in) + "</ul>\n");
        // Display the statistics line
        html.append(sp(in) + "<b>Total:</b> "
                + ((dirCount > 0)
                        ? ((dirCount > 1)
                                ? dirCount + " folders"
                                : "one folder")
                        : "")
                + (((fileCount > 0) && (dirCount > 0)) ? " and " : "")
                + ((fileCount > 0)
                        ? ((fileCount > 1)
                                ? fileCount + " files"
                                : "one file")
                        : "")
                + " found\n");
        return html.toString();
    }

    // TODO: Modify and document it
    public String getHtml(
            Terrier terrier,
            Map<Long, String> selectedMetrics) {
        StringBuffer html = new StringBuffer();
        html.append(files.size() + " file(s) found.\n");
        // Display the list of files
        html.append("<ul>\n");
        for (File nextFile: files) {
            html.append((nextFile != null)
                    ? "<li>" + nextFile.getHtml(0) + "</li>\n"
                    : "");
        }
        html.append("</ul>\n");
        return html.toString();
    }

}