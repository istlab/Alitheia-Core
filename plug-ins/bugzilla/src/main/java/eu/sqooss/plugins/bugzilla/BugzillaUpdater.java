/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - 2010 - Organization for Free and Open Source Software,  
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

package eu.sqooss.plugins.bugzilla;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.Bug;
import eu.sqooss.service.db.BugPriority;
import eu.sqooss.service.db.BugReportMessage;
import eu.sqooss.service.db.BugResolution;
import eu.sqooss.service.db.BugSeverity;
import eu.sqooss.service.db.BugStatus;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.BugPriority.Priority;
import eu.sqooss.service.db.BugResolution.Resolution;
import eu.sqooss.service.db.BugSeverity.Severity;
import eu.sqooss.service.db.BugStatus.Status;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.tds.BTSAccessor;
import eu.sqooss.service.tds.BTSEntry;
import eu.sqooss.service.tds.BTSEntry.BTSEntryComment;
import eu.sqooss.service.updater.MetadataUpdater;

/**
 * Bug updater. Reads data from the TDS and updates the bug metadata
 * database. 
 */
public class BugzillaUpdater implements MetadataUpdater  {

    private BTSAccessor bts;
    private StoredProject project;
    private Logger logger;
    private DBService dbs;
    private float progress;
    
    public BugzillaUpdater() {}

	@Override
	public void setUpdateParams(StoredProject project, Logger log) {
		this.project = project;
		this.logger = log;
		this.dbs = AlitheiaCore.getInstance().getDBService();
	}
	
	@Override
    public int progress() {
        return (int)progress;
    }

	@Override
    public void update() throws Exception {
	    int numprocessed = 0;
        dbs.startDBSession();
        project = dbs.attachObjectToDBSession(project);
        
        Scheduler s = AlitheiaCore.getInstance().getScheduler();
        
        //Get latest updated date
        List<String> bugIds = null;

        this.bts = AlitheiaCore.getInstance().getTDSService().getAccessor(
                project.getId()).getBTSAccessor();
        if (Bug.getLastUpdate(project) != null) {
            bugIds = bts.getBugsNewerThan(Bug.getLastUpdate(project).getUpdateRun());
        } else {
            bugIds = bts.getAllBugs();
        }
        logger.info(project.getName() + ": Got " + bugIds.size() + " new bugs");
        logger.info(project.getName() + ": Spawing jobs");

        Set<Job> jobs = new HashSet<Job>();
        
        // Update
        for (String bugID : bugIds) {
            BugzillaXMLJob job = new BugzillaXMLJob(project, bugID, logger);
            numprocessed++;
            
            jobs.add(job);
            
            progress = (float) (((double)numprocessed/(double)bugIds.size())*100);
        }
        
        s.enqueueNoDependencies(jobs);

        if (dbs.isDBSessionActive())
            dbs.commitDBSession();
    }
        
    @Override
    public String toString() {
        return "BugzilaUpdater - Project:{" + project +"}, " + progress + "%";
    }
}
