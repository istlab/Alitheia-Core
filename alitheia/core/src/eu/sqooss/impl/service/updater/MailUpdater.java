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

package eu.sqooss.impl.service.updater;

import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
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
        return 0;
    }

    protected void run() {
        dbs.startDBSession();
        ProjectAccessor spAccessor = core.getTDSService().getAccessor(project.getId());
        MailAccessor mailAccessor = spAccessor.getMailAccessor();
        List<String> lists = mailAccessor.getMailingLists();
        if(lists.size() == 0) {
            logger.warn("Project <" + project.getName() + "> with ID " + project.getId() +
            " has no mailing lists.");
            dbs.commitDBSession();
            updater.removeUpdater(project.getName(), UpdaterService.UpdateTarget.MAIL);
            return;
        }
        List<MailingList> mllist = getMailingLists(project);
        boolean refresh = false;
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
                refresh = true;
            }
        }

        // if we added a new mailing list, retrieve them again
        if (refresh) {
            mllist = getMailingLists(project);
        }
        
        for (MailingList ml : mllist) {
            processList(mailAccessor, ml);
            createThreads(mailAccessor, ml);
        }
        
        try {
            dbs.commitDBSession();
            MetricActivator ma = AlitheiaCore.getInstance().getMetricActivator();
            
            if (!updMails.isEmpty()) {
                ma.runMetrics(updMails, MailMessage.class);
                ma.runMetrics(updDevs, Developer.class);
            }
            
            if (!updMailingLists.isEmpty()) {
                ma.runMetrics(updMailingLists, MailingList.class);
            }
        } finally {
            updater.removeUpdater(project.getName(), UpdaterService.UpdateTarget.MAIL);
        }   
    }

    private void processList(MailAccessor mailAccessor, MailingList mllist) {
        List<String> fileNames = null;
        String listId = mllist.getListId();
        try {
            fileNames = mailAccessor.getNewMessages(listId);
        } catch (FileNotFoundException e) {
            logger.warn("Mailing list <" + listId + "> vanished: " + e.getMessage());
            return;
        }

        for (String fileName : fileNames) {
            String msg = String.format("Message <%s> in list <%s> ", fileName, listId);
            
            try {
                MimeMessage mm = mailAccessor.getMimeMessage(listId, fileName);
                if (mm == null) {
                    logger.info("Failed to parse message.");
                    continue;
                }
                Address[] senderAddr = mm.getFrom();
                if (senderAddr == null) {
                    logger.warn(project.getName() + ": " 
                            + msg + "  has no sender. Ignoring");
                    continue;
                }
                Address actualSender = senderAddr[0];
                String senderEmail = null;
                if (actualSender instanceof InternetAddress) {
                    senderEmail = ((InternetAddress)actualSender).getAddress();
                } else {
                    InternetAddress inet = new InternetAddress(actualSender.toString());
                    senderEmail = inet.getAddress();
                }

                Developer sender = Developer.getDeveloperByEmail(senderEmail,
                        mllist.getStoredProject());

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
                    mmsg.setSubject(mm.getSubject());
                    mmsg.setFilename(fileName);
                    
                    logger.debug("Adding message " + mm.getMessageID());
                    dbs.addRecord(mmsg);
                    updMails.add(mmsg.getId());
                }

                if (!mailAccessor.markMessageAsSeen(listId, fileName)) {
                    logger.warn("Failed to mark message as seen.");
                }
            } catch (FileNotFoundException e) {
                logger.warn(msg + "not found: " + e.getMessage());
            } catch (MessagingException me) {
                logger.warn(msg + " could not be parsed! - " + me.toString());
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn(msg + " error - " + e.getMessage());
            }
	}
    }
    
    private List<MailingList> getMailingLists(StoredProject sp) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("storedProject", sp);
        return dbs.findObjectsByProperties(MailingList.class, params);
    }
    
    private void createThreads(MailAccessor mailAccessor, MailingList ml) {
                
        //Get mails for this ML for the last month
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(ml.getLatestEmail().getSendDate());
        gc.add(GregorianCalendar.MONTH, -1);
        
        List<MailMessage> mmList = ml.getMessagesNewerThan(gc.getTime());
        ThreadableMail root = new ThreadableMail("Root", "Root", new String[0]);
      //Create threadables from mailmessages
        for (MailMessage mail : mmList) {
            List<String> refs = new ArrayList<String>();
            try {
                MimeMessage mm = mailAccessor.getMimeMessage(ml.getListId(), mail.getFilename());
                String[] inReplyTo = mm.getHeader("In-Reply-To");
                String[] references = mm.getHeader("References");
                
                if (inReplyTo != null) {
                    for (String reply : inReplyTo) {
                    
                    }
                }
                
                if (references != null) {
                    for (String ref : references) {
                    
                    }
                }
                
            } catch (IllegalArgumentException e) {
                //Ignored, should have been caught earlier
            } catch (FileNotFoundException e) {
                //Ignored, should have been caught earlier
            } catch (MessagingException e) {
                logger.warn("Could not get mime header for messageId: " 
                        + mail.getId() + " " + e.getMessage());
            }
            
            ThreadableMail t = new ThreadableMail(mail.getSubject(), 
                    mail.getMessageId(), refs.toArray(new String[refs.size()] ));
            t.setNext(root);
            t.setChild(null);
        }
        
        Threader t = new Threader();
        Threadable newroot = t.thread(root);

    }
    
}

// vi: ai nosi sw=4 ts=4 expandtab

