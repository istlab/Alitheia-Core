/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 * 
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007 Georgios Gousios <gousiosg@aueb.gr>
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

package eu.sqooss.impl.service.updater;

import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;

import eu.sqooss.service.db.DAOException;
import eu.sqooss.service.db.MailingList;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;

import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.tds.MailAccessor;
import eu.sqooss.service.tds.TDAccessor;
import eu.sqooss.service.updater.UpdaterException;

/** 
 * Synchronises raw mails with the database
 * 
 * @author Vassilios Karakoidas (bkarak@aueb.gr)
 */
public class MailUpdater {
    private String path;
    private AlitheiaCore core;
    private Logger logger;
    
    public MailUpdater(String path, 
	    	       AlitheiaCore core,
	    	       Logger logger) throws UpdaterException {
	if(path == null || core == null || logger == null) {
	    throw new UpdaterException("Cannot initialise MailUpdater (path/core/logger is null)");
	}
	
	this.core = core;
	this.path = path;
	this.logger = logger;
    }
    
    public void doUpdate() throws UpdaterException {
	try {
	    core.getScheduler().enqueue(new MailUpdaterJob(path, core, logger));
	} catch (Exception e) {
	    throw new UpdaterException(e.getMessage());
	}
    }
}

class MailUpdaterJob extends Job {
    private AlitheiaCore core;
    private String path;
    private Logger logger;
    
    MailUpdaterJob(String path, AlitheiaCore core, Logger logger) {
	this.path = path;
	this.core = core;
	this.logger = logger;
    }

    public int priority() {
	return 0;
    }

    protected void run() throws Exception {
	try {
	    StoredProject sp = StoredProject.getProjectByName(path, logger);
	    TDAccessor spAccessor = core.getTDSService().getAccessor(sp.getId());
	    MailAccessor mailAccessor = spAccessor.getMailAccessor();
	    List<MailingList> mllist = MailingList.getListsPerProject(sp);
	    for ( MailingList ml : mllist ) {
		String listId = ml.getListId();
		List<String> listIds = mailAccessor.getMessages(ml.getListId());
		for ( String id : listIds ) {
		    String raw = mailAccessor.getRawMessage( listId, id);
		    // TODO: parse the message & add it to the database
		}
	    }
	} catch ( DAOException daoe ) {
	    logger.warn(daoe.getMessage());
	}	
    }
}