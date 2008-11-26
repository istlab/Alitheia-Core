/*
 * Copyright 2008 - Organization for Free and Open Source Software,
 *                Athens, Greece.
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

package eu.sqooss.impl.service.web.services.datatypes;

public class WSFileModification {
    private Long versionTimestamp;
    private Long fileId;
    /* TODO: [byotov] - The inclusion of a "versionRevision" field was
     * necessary in order to quickly fix of the Eclipse Plug-in. Once I have
     * enough time, I'll fix the plug-in to rely upon version timestamps
     * instead and then remove the extra field.
     */
    private String versionRevision;

    public WSFileModification(Long versionTimestamp, Long fileId,
            String versionRevision) {
        this.versionTimestamp = versionTimestamp;
        this.fileId = fileId;
        this.versionRevision = versionRevision;
    }

    public Long getProjectVersionTimestamp() {
        return versionTimestamp;
    }

    public void setProjectVersionTimestamp(Long versionTimestamp) {
        this.versionTimestamp = versionTimestamp;
    }

    public Long getProjectFileId() {
        return fileId;
    }

    public void setProjectFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getProjectVersionNum() {
        return versionRevision;
    }

    public void setProjectVersionNum(String versionRevision) {
        this.versionRevision = versionRevision;
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
