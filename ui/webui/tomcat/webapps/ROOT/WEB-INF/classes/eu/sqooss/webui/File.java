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

class File {

    String name;
    String status;
    String protection;
    Integer links;
    Long userId;
    Long groupId;
    Long accessTime;
    Long modificationTime;
    String fileStatus;
    Integer size;
    Integer blocks;

    public File (WSProjectFile wsFile) {
        name = wsFile.getName();
        status = wsFile.getStatus();

//        WSFileMetadata fileMeta = wsFile.getProjectFileMetadata();
//        if (fileMeta != null) {
//            protection = fileMeta.getProtection();
//            links = fileMeta.getLinks();
//            userId = fileMeta.getUserId();
//            groupId = fileMeta.getGroupId();
//            accessTime = fileMeta.getAccessTime();
//            modificationTime = fileMeta.getModificationTime();
//            fileStatus = fileMeta.getFileStatusChange();
//            size = fileMeta.getSize();
//            blocks = fileMeta.getBlocks();
//        }
    }

    public String getName () {
        return name;
    }

    public String getStatus () {
        return status;
    }

    public Long getUserId () {
        return userId;
    }
    
    public Long getGroupId () {
            return groupId;
    }
    
    public Long getAccessTime () {
        return accessTime;
    }
    
    public Long getModificationTime () {
        return modificationTime;
    }
    
    public String getFileStatus () {
            return fileStatus;
    }

    public Integer getSize () {
        return size;
    }

    public Integer getBlocks () {
        return blocks;
    }

    public String getHtml() {
        StringBuilder html = new StringBuilder("<!-- File -->\n");
        html.append("<h3>File: " + getName() + "</h3>");
        html.append("<ul>");
        html.append("<li>Size (blocks): " + getSize() + "(" + getBlocks() + ")</li>");
        html.append("<li>Last accessed (modified): " + getAccessTime() + "(" + getModificationTime() + ")</li>");
        html.append("<li>User (group): " + getUserId() + "(" + getGroupId() + ")</li>");
        html.append("<li>Status (changed): " + getStatus() + "(" + getFileStatus() + ")</li>");
        html.append("</ul>");
        return html.toString();
    }
}
