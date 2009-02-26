/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - Organization for Free and Open Source Software,  
 *                 Athens, Greece.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.sqooss.core.AlitheiaCore;

/**
 * This class represents the data relating to bugs, stored in the database
 * 
 * @assoc 1 - n BugReportMessage
 * @assoc 1 - n Developer
 *  
 */
public class Bug extends DAObject {
    
    /** The project this bug belongs to */
    private StoredProject project;
    
    /** When this bug was last touched from the updater */
    private Date updateRun;
    
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
    private BugResolution resolution;
    
    /** The bug's resolution priority*/
    private BugPriority priority;
    
    /** The bug's severity.*/
    private BugSeverity severity;
    
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

    public BugResolution getResolution() {
        return resolution;
    }

    public void setResolution(BugResolution resolution) {
        this.resolution = resolution;
    }
    
    public BugPriority getPriority() {
        return priority;
    }

    public void setPriority(BugPriority priority) {
        this.priority = priority;
    }
    
    public BugSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(BugSeverity severity) {
        this.severity = severity;
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

    public Date getUpdateRun() {
        return updateRun;
    }

    public void setUpdateRun(Date updateRun) {
        this.updateRun = updateRun;
    }
    
    /**
     * Get the latest entry processed by the bug updater
     */
    @SuppressWarnings("unchecked")
    public static Bug getLastUpdate(StoredProject sp) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        if (sp == null)
            return null;
        
        String paramStoredProject = "storedProject";
        
        String query = " select b " +
            " from Bug b, StoredProject sp" +
            " where b.project=sp" +
            " and sp = :" + paramStoredProject + 
            " order by b.updateRun desc";
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(paramStoredProject, sp);
        
        List<Bug> buglist = (List<Bug>) dbs.doHQL(query, params,1);
        
        if (buglist.isEmpty())
            return null;
        
        return buglist.get(0);
    }
    
    /**
     * Get a list of all bug report comments for this specific bug,
     * ordered by the time the comment was left (old to new).  
     */
    @SuppressWarnings("unchecked")
    public List<BugReportMessage> getAllReportComments() {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        
        String paramBugID = "paramBugID";
        String paramStoredProject = "stroredProject";
        
        String query = "select brm " +
        		"from Bug b, BugReportMessage brm " +
        		"where brm.bug = b " +
        		"and b.bugID = :" + paramBugID +
        		" and b.project =:" + paramStoredProject +
        		" order by brm.timestamp asc" ;
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(paramBugID, bugID);
        params.put(paramStoredProject, project);
        
        return (List<BugReportMessage>) dbs.doHQL(query, params);
    }
    
    /**
     * Get the latest entry for the bug with the provided Id.
     */
    public static Bug getBug(String bugID, StoredProject sp) {    
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        
        String paramBugID = "paramBugID";
        String paramStoredProject = "stroredProject";
        
        String query = "select b " +
        	        "from Bug b " +
        	        "where b.bugID = :" + paramBugID + 
        	        " and b.project = :" + paramStoredProject +
        	        " order by b.timestamp desc";
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(paramBugID, bugID);
        params.put(paramStoredProject, sp);
        
        List<Bug> bug = (List<Bug>) dbs.doHQL(query, params, 1);
        
        if (bug.isEmpty())
            return null;
        else 
            return bug.get(0);
    }
}

//vi: ai nosi sw=4 ts=4 expandtab

