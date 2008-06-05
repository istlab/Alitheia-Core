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

import eu.sqooss.ws.client.datatypes.WSProjectFile;

/**
 * This class represents a File in a project or version that has been 
 * evaluated by Alitheia.
 * It provides access to the files' metadata and some convenience 
 * functions for display.
 *
 * The File class is part of the high-level webui API.
 */
class File extends WebuiItem {

    private static final String COMMENT = "<!-- File -->\n";
    private String name = "FILE_NAME_UNSET";
    private String status = "FILE_STATUS_UNSET"; // status is one of ADDED, MODIFIED or DELETED
    private Long versionId;
    private Long id;
    private Terrier terrier;

    public File (WSProjectFile wsFile, Terrier t) {
        versionId = wsFile.getProjectVersionId();
        name = wsFile.getFileName();
        status = wsFile.getStatus();
        id = wsFile.getId();
        terrier = t;
        // TODO: measurements (and metrics?)
        // TODO: isDirectory()?
    }

    public File(Long versionId, Long fileId, String name, String status, Terrier t) {
        this.versionId = versionId;
        this.name = name;
        this.status = status;
        this.id = fileId;
        this.versionId = versionId;
        this.terrier = t;
    }

    public Long getVersion () {
        return versionId;
    }

    public String getName () {
        return name;
    }

    public String getStatus () {
        return status;
    }

    public Long getId() {
        return id;
    }

    public String getLink() {
        return "<a href=\"files.jsp?fid=" + id + "\">" + name + "</a>";
    }

    public String getStatusIcon() {
        String iconname = "vcs_status";
        String tooltip = "status unknown";
        if (status.equals("ADDED")) {
            iconname = "vcs_add";
            tooltip = "file was added";
        } else if (status.equals("MODIFIED")) {
            iconname = "vcs_update";
            tooltip = "file was modified";
        } else if (status.equals("DELETED")) {
            iconname = "vcs_remove";
            tooltip = "file was removed";
        }
        return icon(iconname, 0, tooltip);
    }

    public String getHtml() {
        StringBuilder html = new StringBuilder(COMMENT);
        html.append(getStatusIcon() + "&nbsp;" + getLink());
        return html.toString();
    }
}
