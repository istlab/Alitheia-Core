/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 Athens University of Economics and Business
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

import java.util.Date;
import java.util.Set;

/**
 * This class represents the data relating to bugs, stored in the database
 */
public class Bug extends DAObject {
    
    /** The project this bug belongs to */
    private StoredProject project;
    
    /**
     * The bugID in the original bug tracking system. Used to correlate
     * entries to filesystem/other database bug reports.
     */
    private String bugID;
    
    /** The bug resolution status. */
    private BugStatus status;
    
    /** Creation timestamp. */
    private Date creationTS;
    
    /** The timestamp of the last update. */
    private Date deltaTS;
    
    /** The user who reported this. */
    private Developer reporter;
    
    /** The bug's resolution status. */
    private String resolution;
    
    /** A short description of the bug. */
    private String shortDesc;
    
    /** The list of messages associated to this bug */
    private Set<BugReportMessage> reportMessages;

    public String getBugID() {
        return bugID;
    }

    public void setBugID(String bugID) {
        this.bugID = bugID;
    }

    public BugStatus getStatus() {
        return status;
    }

    public void setStatus(BugStatus status) {
        this.status = status;
    }

    public Date getCreationTS() {
        return creationTS;
    }

    public void setCreationTS(Date creationTS) {
        this.creationTS = creationTS;
    }

    public Date getDeltaTS() {
        return deltaTS;
    }

    public void setDeltaTS(Date deltaTS) {
        this.deltaTS = deltaTS;
    }

    public Developer getReporter() {
        return reporter;
    }

    public void setReporter(Developer reporter) {
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

    public Set<BugReportMessage> getReportMessages() {
        return reportMessages;
    }

    public void setReportMessages(Set<BugReportMessage> reportMessages) {
        this.reportMessages = reportMessages;
    }

    public StoredProject getProject() {
        return project;
    }

    public void setProject(StoredProject project) {
        this.project = project;
    }
}

//vi: ai nosi sw=4 ts=4 expandtab

