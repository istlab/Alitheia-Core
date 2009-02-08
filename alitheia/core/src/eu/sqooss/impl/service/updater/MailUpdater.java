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

package eu.sqooss.impl.service.updater;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.tds.MailAccessor;
import eu.sqooss.service.tds.ProjectAccessor;
import eu.sqooss.service.updater.UpdaterException;
import eu.sqooss.service.updater.UpdaterService;

/**
 * Synchronises raw mails with the database
 */
class MailUpdater extends Job {
    private StoredProject project;
    private AlitheiaCore core;
    private Logger logger;
    private UpdaterServiceImpl updater;
    private DBService dbs;

    /*Cache mail ids to call the metric activator with them*/
    private Set<Long> updMails = new TreeSet<Long>();
    
    /*Cache mailinglist ids to call the metric activator with them*/
    private Set<Long> updMailingLists = new TreeSet<Long>();
    
    private Set<Long> updDevs = new TreeSet<Long>();
    
    public MailUpdater(StoredProject project,
                       UpdaterServiceImpl updater,
                       AlitheiaCore core,
                       Logger logger) throws UpdaterException {
        if (project == null || core == null || logger == null) {
            throw new UpdaterException("Cannot initialise MailUpdater (path/core/logger is null)");
        }

        this.core = core;
        this.project = project;
        this.logger = logger;
        this.updater = updater;
        this.dbs = core.getDBService();
    }

    public int priority() {
        return 0x1;
    }

    protected void run() throws Exception {

        ProjectAccessor spAccessor = core.getTDSService().getAccessor(project.getId());
        MailAccessor mailAccessor = spAccessor.getMailAccessor();
        MetricActivator ma = AlitheiaCore.getInstance().getMetricActivator();
        List<Long> listIds = Collections.emptyList();
        try {
            //Process mailing lists first
            dbs.startDBSession();
            listIds = processMailingLists(mailAccessor);
            
            if (!updMailingLists.isEmpty()) {
                ma.runMetrics(updMailingLists, MailingList.class);
            }
            
            dbs.commitDBSession();
            
            for (Long mlId : listIds) {
                dbs.startDBSession();
                List<String> filenames = Collections.emptyList();
                MailingList ml = DAObject.loadDAObyId(mlId, MailingList.class);
                dbs.commitDBSession();
                if (ml == null) {
                    logger.warn("No mailing list with id " + mlId);
                    continue;
                }
                
                filenames = processList(mailAccessor, ml);
                dbs.startDBSession();
                ml = dbs.attachObjectToDBSession(ml);
                MailThreadUpdater mtu = new MailThreadUpdater(ml, logger);
                AlitheiaCore.getInstance().getScheduler().enqueue(mtu);
                logger.info("Added thread update job for " + ml);                
                
                if (!updMails.isEmpty()) {
                    ma.runMetrics(updMails, MailMessage.class);
                    ma.runMetrics(updDevs, Developer.class);
                }
                updMails.clear();
                updDevs.clear();
                dbs.commitDBSession();
            }
        } catch (IllegalArgumentException e) {
            logger.error("MailUpdater: IllegalArgumentException: " 
                    + e.getMessage() + " at:", e);
            throw e;
        } catch (FileNotFoundException e) {
            logger.error("MailUpdater: FileNotFoundException: " 
                    + e.getMessage() + " at:", e);
            throw e;
        } catch (MessagingException e) {
            logger.error("MailUpdater: MessagingException: " 
                    + e.getMessage() + " at:", e);
            throw e;
        } finally {
            updater.removeUpdater(project.getName(), UpdaterService.UpdateTarget.MAIL);
        }   
    }

    private List<Long> processMailingLists(MailAccessor mailAccessor) {
        List<String> lists = mailAccessor.getMailingLists();
        
        if ( lists.size() == 0 ) {
            logger.warn("Project <" + project.getName() + "> with ID " + project.getId() +
            " has no mailing lists.");
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
                updMailingLists.add(nml.getId());
            }
        }
        List<Long> listIds = new ArrayList<Long>();
        List<MailingList> mailingLists = getMailingLists(this.project);
        
        for (MailingList ml : mailingLists) {
            listIds.add(ml.getId());
        }
        
        return listIds;
    }

    private List<String> processList(MailAccessor mailAccessor, MailingList mllist) 
        throws IllegalArgumentException, FileNotFoundException, MessagingException {
        List<String> fileNames = Collections.emptyList();
        String listId = mllist.getListId();
        try {
            fileNames = mailAccessor.getNewMessages(listId);
        } catch (FileNotFoundException e) {
            logger.warn("Mailing list <" + listId + "> vanished: " + e.getMessage());
            return Collections.emptyList();
        }

        for (String fileName : fileNames) {
            if (!dbs.isDBSessionActive()) dbs.startDBSession();
            String msg = String.format("Message <%s> in list <%s> ", fileName,
                    listId);

            MimeMessage mm = mailAccessor.getMimeMessage(listId, fileName);
            if (mm == null) {
                logger.info("Failed to parse message.");
                continue;
            }
            Address[] senderAddr = mm.getFrom();
            if (senderAddr == null) {
                logger.warn(project.getName() + ": " + msg
                        + "  has no sender. Ignoring");
                continue;
            }
            Address actualSender = senderAddr[0];
            String senderEmail = null;
            if (actualSender instanceof InternetAddress) {
                senderEmail = ((InternetAddress) actualSender).getAddress();
            } else {
                InternetAddress inet = new InternetAddress(actualSender
                        .toString());
                senderEmail = inet.getAddress();
            }

            Developer sender = Developer.getDeveloperByEmail(senderEmail,
                    mllist.getStoredProject());

            if (sender == null)
            	logger.error(project.getName() 
            			+ ": Error adding developer");
            
            if (!updDevs.contains(sender.getId())) {
                updDevs.add(sender.getId());
            }

            MailMessage mmsg = MailMessage.getMessageById(fileName);
            if (mmsg == null) {
                // if the message does not exist in the database, then
                // write a new one
                mmsg = new MailMessage();
                mmsg.setList(mllist);
                mmsg.setMessageId(mm.getMessageID());
                mmsg.setSender(sender);
                mmsg.setSendDate(mm.getSentDate());
                mmsg.setArrivalDate(mm.getReceivedDate());

                /* 512 characters should be enough for everybody */
                String subject = mm.getSubject();
                if (subject != null) {
                	if (mm.getSubject().length() > 512)
                		subject = subject.substring(0, 511);
                }

                mmsg.setSubject(subject);
                mmsg.setFilename(fileName);
                dbs.addRecord(mmsg);
                logger.debug("Adding message " + mm.getMessageID());
                
                updMails.add(mmsg.getId());
                if (dbs.commitDBSession()) {
                    if (!mailAccessor.markMessageAsSeen(mllist.getListId(), fileName))
                        logger.warn("Failed to mark message <" + fileName
                                + "> as seen");
                }
            }
        }
        return fileNames;
    }
    
    private List<MailingList> getMailingLists(StoredProject sp) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("storedProject", sp);
        return dbs.findObjectsByProperties(MailingList.class, params);
    }
    
    @Override
    public String toString() {
        return "MailUpdaterJob - Project:{" + project + "}";
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

