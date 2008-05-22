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

import java.util.List;

import eu.sqooss.service.db.ProjectVersion;

/**
 * This class wraps the <code>eu.sqooss.service.db.ProjectVersion</code>.
 */
public class WSProjectVersion {

    private long id;
    private String commitMsg;
    private long committerId;
    private long projectId;
    private String properties;
    private long timestamp;
    private long version;
    
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
     * @return the commitMsg
     */
    public String getCommitMsg() {
        return commitMsg;
    }
    
    /**
     * @param commitMsg the commitMsg to set
     */
    public void setCommitMsg(String commitMsg) {
        this.commitMsg = commitMsg;
    }
    
    /**
     * @return the committerId
     */
    public long getCommitterId() {
        return committerId;
    }
    
    /**
     * @param committerId the committerId to set
     */
    public void setCommitterId(long committerId) {
        this.committerId = committerId;
    }
    
    /**
     * @return the projectId
     */
    public long getProjectId() {
        return projectId;
    }
    
    /**
     * @param projectId the projectId to set
     */
    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }
    
    /**
     * @return the properties
     */
    public String getProperties() {
        return properties;
    }
    
    /**
     * @param properties the properties to set
     */
    public void setProperties(String properties) {
        this.properties = properties;
    }
    
    /**
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * @return the version
     */
    public long getVersion() {
        return version;
    }
    
    /**
     * @param version the version to set
     */
    public void setVersion(long version) {
        this.version = version;
    }
    
    /**
     * The method creates a new <code>WSProjectVersion</code> object
     * from the existent DAO object.
     * The method doesn't care of the db session. 
     * 
     * @param projectVersion - DAO project version object
     * 
     * @return The new <code>WSProjectVersion</code> object
     */
    public static WSProjectVersion getInstance(ProjectVersion projectVersion) {
        if (projectVersion == null) return null;
        try {
            WSProjectVersion wsProjectVersion = new WSProjectVersion();
            wsProjectVersion.setId(projectVersion.getId());
            wsProjectVersion.setCommitMsg(projectVersion.getCommitMsg());
            wsProjectVersion.setCommitterId(projectVersion.getCommitter().getId());
            wsProjectVersion.setProjectId(projectVersion.getProject().getId());
            wsProjectVersion.setProperties(projectVersion.getProperties());
            wsProjectVersion.setTimestamp(projectVersion.getTimestamp());
            wsProjectVersion.setVersion(projectVersion.getVersion());
            return wsProjectVersion;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * The method returns an array containing
     * all of the elements in the project versions list.
     * The list argument should contain DAO
     * <code>ProjectVersion</code> objects.
     *  
     * @param projectVersions - the project versions list;
     * the elements should be <code>ProjectVersion</code> objects  
     * 
     * @return - an array with <code>WSProjectVersion</code> objects;
     * if the list is null, empty or contains different object type
     * then the array is null
     */
    public static WSProjectVersion[] asArray(List<?> projectVersions) {
        WSProjectVersion[] result = null;
        if ((projectVersions != null) && (!projectVersions.isEmpty())) {
            result = new WSProjectVersion[projectVersions.size()];
            ProjectVersion currentElem;
            for (int i = 0; i < result.length; i++) {
                try {
                    currentElem = (ProjectVersion) projectVersions.get(i);
                } catch (ClassCastException e) {
                    return null;
                }
                result[i] = WSProjectVersion.getInstance(currentElem);
            }
        }
        return result;
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
