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

class File extends WebuiItem {

    private static final String COMMENT = "<!-- File -->\n";
    private String name = "FILE_NAME_UNSET";
    private String status = "FILE_STATUS_UNSET";
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

    public String getHtml() {
        StringBuilder html = new StringBuilder(COMMENT);
        html.append("<b>File:</b> " + getLink());
        // The next line shows the version ID, not the version number
        //html.append(" <i>(ver." + getVersion() + ")</i>"); // Version
        html.append(" <i>(" + getStatus() + ")</i>"); // Status
        html.append("<br />");
        return html.toString();
    }
}
