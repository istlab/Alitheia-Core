/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

package eu.sqooss.service.db;

import java.sql.Time;

public class FileMetadata {
    private Long id;
    private String protection;
    private int links;
    private int userId;
    private int groupId;
    private Time accessTime;
    private Time modificationTime;
    private String fileStatusChange;
    private int size;
    private int blocks;


    public FileMetadata() {
	// Nothing to do
    }

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public String getProtection() {
	return protection;
    }

    public void setProtection(String protection) {
	this.protection = protection;
    }

    public int getLinks() {
	return links;
    }

    public void setLinks(int links) {
	this.links = links;
    }

    public int getUserId() {
	return userId;
    }

    public void setUserId(int userId) {
	this.userId = userId;
    }

    public int getGroupId() {
	return groupId;
    }

    public void setGroupId(int groupId) {
	this.groupId = groupId;
    }

    public Time getAccessTime() {
	return accessTime;
    }

    public void setAccessTime(Time accessTime) {
	this.accessTime = accessTime;
    }

    public Time getModificationTime() {
	return modificationTime;
    }

    public void setModificationTime(Time modificationTime){
	this.modificationTime = modificationTime;
    }

    public int getSize() {
	return size;
    }

    public void setSize(int size) {
	this.size = size;
    }

    public String getFileStatusChange() {
	return fileStatusChanged;
    }

    public void setFileStatusChange(String fileStatusChanged) {
	this.fileStatusChanged = fileStatusChanged;
    }

    public int getBlocks() {
	return blocks;
    }

    public void setBlocks(int blocks) {
	this.blocks = blocks;
    }
}
