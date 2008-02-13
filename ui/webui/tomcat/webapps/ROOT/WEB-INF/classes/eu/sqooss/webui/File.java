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
    
    /** Parses an ArrayList of WSResult and offers convenience methods to get data
     *  out of it.
     * 
     * @param data The ArrayList for one file
     * 
     */
    public File (ArrayList data) {
        name = data.get(0).toString();
        status = data.get(1).toString();
        protection = data.get(2).toString();
        links = Integer.parseInt(data.get(3).toString()); // Does this work?
        userId = Long.parseLong(data.get(4).toString()); // Does this work? :>
        groupId = Long.parseLong(data.get(5).toString());
        accessTime = Long.parseLong(data.get(6).toString());
        modificationTime = Long.parseLong(data.get(7).toString());
        fileStatus = data.get(8).toString();
        size = Integer.parseInt(data.get(9).toString());
        blocks = Integer.parseInt(data.get(10).toString());
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
        StringBuilder html = new StringBuilder("<!-- File -->\n<ul>");
        html.append("<h3>File: " + getName() + "</h3>");
        html.append("<br />Size (blocks): " + getSize() + "(" + getBlocks() + ")");
        html.append("<br />Last accessed (modified): " + getAccessTime() + "(" + getModificationTime() + ")");
        html.append("<br />User (group): " + getUserId() + "(" + getGroupId() + ")");
        html.append("<br />Status (changed): " + getStatus() + "(" + getFileStatus() + ")");
        return html.toString();
    }
}
