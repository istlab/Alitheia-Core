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

package eu.sqooss.plugins.maildir;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.MailMessage;
import eu.sqooss.service.db.MailingList;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.service.tds.MailAccessor;
import eu.sqooss.service.tds.ProjectAccessor;
import eu.sqooss.service.updater.MetadataUpdater;

/**
 * Synchronises raw mails with the database.
 */
public class MailDirUpdater implements MetadataUpdater {

	private DBService dbs;
	private StoredProject project;
	private Logger logger;
	private float progress = 0;
	private long total = 0L;
	private long processed = 0L;
	private MailingList ml;
	
    public MailDirUpdater() {}
    
    @Override
	public void setUpdateParams(StoredProject arg0, Logger arg1) {
    	project = arg0;
    	logger = arg1;
		this.dbs = AlitheiaCore.getInstance().getDBService();	
	}
    
    @Override
    public int progress() {
        return (int) progress;
    }

	@Override
	public void update() throws Exception {

        ProjectAccessor spAccessor = AlitheiaCore.getInstance().getTDSService().getAccessor(project.getId());
        MailAccessor mailAccessor = spAccessor.getMailAccessor();
        List<Long> listIds = Collections.emptyList();
        try {
            //Process mailing lists first
            dbs.startDBSession();
            listIds = processMailingLists(mailAccessor);
            
            for (Long mlId : listIds) {
                MailingList ml = DAObject.loadDAObyId(mlId, MailingList.class);
                List<String> msgs = mailAccessor.getNewMessages(ml.getListId());
                total += msgs.size();
            }
            
            if (total == 0)
                return;
            
            for (Long mlId : listIds) {
                ml = DAObject.loadDAObyId(mlId, MailingList.class);
                processList(mailAccessor);
            }
        } catch (IllegalArgumentException e) {
            err("MailUpdater: IllegalArgumentException: " + e.getMessage());
            throw e;
        }  
    }

    private List<Long> processMailingLists(MailAccessor mailAccessor) {
        List<String> lists = mailAccessor.getMailingLists();
        
        if ( lists.size() == 0 ) {
            warn("No mailing lists");
        }
        
        List<MailingList> mllist = getMailingLists(project);

        //check if the mailing lists exist
        for ( String listId : lists ) {
            boolean exists = false;

            for ( MailingList ml : mllist ) {
                if(ml.getListId().compareTo(listId) == 0) {
                    exists = true;
                    break;
                 }
            }
            if(!exists) {
                // add the mailing list
                MailingList nml = new MailingList();
                nml.setListId(listId);
                nml.setStoredProject(project);
                dbs.addRecord(nml);
            }
        }
        List<Long> listIds = new ArrayList<Long>();
        List<MailingList> mailingLists = getMailingLists(this.project);
        
        for (MailingList ml : mailingLists) {
            listIds.add(ml.getId());
        }
        
        return listIds;
    }
    
    private List<MailingList> getMailingLists(StoredProject sp) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("storedProject", sp);
        return dbs.findObjectsByProperties(MailingList.class, params);
    }

    @Override
    public String toString() {
        String txt =  "MailUpdaterJob - Project:{" + project + "}";
        
        if (ml != null)
            txt += ", current list:{" + ml.getListId() + "}, " + progress + "%";
        return txt;
    }
    
    private void processList(MailAccessor mailAccessor)
            throws IllegalArgumentException, FileNotFoundException,
            MessagingException {
        List<String> fileNames = Collections.emptyList();
        String listId = ml.getListId();

        try {
            fileNames = mailAccessor.getNewMessages(listId);
        } catch (FileNotFoundException e) {
            warn("Mailing list <" + listId + "> vanished: " + e.getMessage());
        }

        Set<Job> jobs = new HashSet<Job>();
        debug("Processing list:" + ml.getListId() + " " + fileNames.size() + " new emails");
        for (String fileName : fileNames) {
            
            MailMessageJob job = new MailMessageJob(ml, fileName, logger);
            
            jobs.add(job);
            
            processed++;
            progress = (float) ((double)processed / (double)total) * 100;
        }
        
        try {
            AlitheiaCore.getInstance().getScheduler().enqueueNoDependencies(jobs);
        } catch (SchedulerException e) {
            err("Failed to enqueue mail processing jobs");
        }
    }
    
    /** Convenience method to write warning messages per project */
    protected void warn(String message) {
        logger.warn(project.getName() + ":" + message);
    }
    
    /** Convenience method to write error messages per project */
    protected void err(String message) {
        logger.error(project.getName() + ":" + message);
    }
    
    /** Convenience method to write info messages per project */
    protected void info(String message) {
        logger.info(project.getName() + ":" + message);
    }
    
    /** Convenience method to write debug messages per project */
    protected void debug(String message) {
        logger.debug(project.getName() + ":" + message);
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

