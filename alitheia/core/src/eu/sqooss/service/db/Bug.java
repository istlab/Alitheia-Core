/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2008 by Panos Louridas <louridas@aueb.gr>
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

import eu.sqooss.service.db.DAObject;
import java.util.Properties;

import java.util.Date;

/**
 * This class represents the data relating to bugs, stored in the database
 */
public class Bug extends DAObject {
    /**
     * The commit which resolves the bug
     */
    private ProjectVersion commit;
    
   /**
     * A URL which points to more information about the bug.
     */
    private String bugFileLoc;
    
    /**
     * Each bug has a "severity" field, indicating the severity of the impact of the bug. 
     * The intended meanings of the built-in values of this field are as follows:
     * Blocker          Blocks development and/or testing work
     * Critical         Crashes, loss of data, severe memory leak
     * Major            Major loss of function
     * Minor            Minor loss of function, or other problem where easy workaround is present
     * Trivial          Cosmetic problem
     * Enhancement      Request for enhancement
     */
    private String severity;
    
    /**
     * Each bug has a status. If a bug has a status which shows it has been resolved, 
     * it also has a resolution, otherwise the resolution field is empty. Values of the field include:
     * UNCONFIRMED
     * NEW 
     * ASSIGNED
     * REOPENED
     * RESOLVED
     * VERIFIED
     * CLOSED
     */
    private String status;
    
    /**
     * Creation timestamp.
     */
    private Date creationTS;
    
    /**
     * The deadline for this bug.
     */
    private Date deadline;
    
    /**
     * The timestamp of the last update.
     */
    private Date deltaTS;
    
    /**
     * The original estimate of the total effort required to fix this bug (in hours).
     */
    private float estimatedTime;
    
    /**
     * A set of keywords.
     */
    private String keywords;
    
    /**
     * The operating system on which the bug was observed.
     */
    private String operatingSystem;
    
    /**
     * The priority of the bug.
     */
    private String priority;
    
    /**
     * The product.
     */
    private String product;
    
    /**
     * The current estimate of the remaining effort required to fix this bug (in hours).
     */
    private float remainingTime;
    
    /**
     * The platform on which the bug was reported.
     */
    private String reportPlatform;
    
    /**
     * The user who reported this.
     */
    private String reporter;
    
    /**
     * The bug's resolution status.
     */
    private String resolution;
    
    /**
     * A short description of the bug.
     */
    private String shortDesc;
     
        public String getBugFileLoc() {
        return bugFileLoc;
    }

    public void setBugFileLoc(String bugFileLoc) {
        this.bugFileLoc = bugFileLoc;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreationTS() {
        return creationTS;
    }

    public void setCreationTS(Date creationTS) {
        this.creationTS = creationTS;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Date getDeltaTS() {
        return deltaTS;
    }

    public void setDeltaTS(Date deltaTS) {
        this.deltaTS = deltaTS;
    }

    public float getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(float estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public float getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(float remainingTime) {
        this.remainingTime = remainingTime;
    }

    public String getReportPlatform() {
        return reportPlatform;
    }

    public void setReportPlatform(String reportPlatform) {
        this.reportPlatform = reportPlatform;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

        public Bug() {
        // Nothing to do here
    }

    public ProjectVersion getCommit() {
        return commit;
    }
    
    public void setCommit(ProjectVersion commit) {
        this.commit = commit;
    }
}

//vi: ai nosi sw=4 ts=4 expandtab

