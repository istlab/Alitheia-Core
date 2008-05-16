/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

/**
 * This class wraps the <code>eu.sqooss.service.db.ProjectFile</code>
 */
public class WSProjectFile {
    
    private long id;
    private long projectVersionId;
    private long directoryId;
    private String fileName;
    private String status;
    private boolean isDirectory;
    
    /**
     * @return the id
     */
    public long getId() {
        return id;
    }
    
    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }
    
    /**
     * @return the projectVersionId
     */
    public long getProjectVersionId() {
        return projectVersionId;
    }
    
    /**
     * @param projectVersionId the projectVersionId to set
     */
    public void setProjectVersionId(long projectVersionId) {
        this.projectVersionId = projectVersionId;
    }
    
    /**
     * @return the directoryId
     */
    public long getDirectoryId() {
        return directoryId;
    }
    
    /**
     * @param directoryId the directoryId to set
     */
    public void setDirectoryId(long directoryId) {
        this.directoryId = directoryId;
    }
    
    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * @return the isDirectory
     */
    public boolean isDirectory() {
        return isDirectory;
    }
    
    /**
     * @param isDirectory the isDirectory to set
     */
    public void setDirectory(boolean isDirectory) {
        this.isDirectory = isDirectory;
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
