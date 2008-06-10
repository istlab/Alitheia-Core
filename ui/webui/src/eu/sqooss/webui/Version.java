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

import eu.sqooss.ws.client.datatypes.WSProjectVersion;

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
    private Long filesNumber;

    /** Empty ctor, only sets the jsp page that can be used to display
     * details about this Version.
     */
    public Version () {
        page = "version.jsp";
    }

    /** Ctor that fully initialises a Version from a WSProjectVersion object.
     */
    public Version (WSProjectVersion wsVersion, Terrier t) {
        id = wsVersion.getId();
        terrier = t;
        number = wsVersion.getVersion();
        name = "" + number;
        projectId = wsVersion.getProjectId();
        filesNumber = getFilesNumber();
    }

    /** Initialise some data of this Version. This method can be used when we
     * don't have a WSProjectVersion to use for data initialisation, or if we
     * don't want to use one for performance reasons.
     */
    public Version(Long projectId, Long versionId, Terrier t) {
        id = versionId;
        this.projectId = projectId;
        terrier = t;
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

    /** Return the number of files that are part of this particular project version.
     */
    public Long getFilesNumber() {
        return terrier.getFilesNumber4ProjectVersion(id);
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

    /** Return an HTML list of all the Files in this Version.
     *
     *
     */
    public String listFiles() {
        return terrier.getFiles4ProjectVersion(id).getHtml();
    }

    /** Return an HTML representation of this Version.
     *
     */
    public String getHtml() {
        StringBuilder html = new StringBuilder(COMMENT);
        html.append("<b>Version:</b> " + id);
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
