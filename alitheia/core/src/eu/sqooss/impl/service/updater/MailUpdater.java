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
import eu.sqooss.service.db.MailThread;
import eu.sqooss.service.db.MailingList;
import eu.sqooss.service.db.MailingListThread;
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
        
        if ( lists.size() == 0 ) {
            logger.warn("Project <" + project.getName() + "> with ID " + project.getId() +
            " has no mailing lists.");
            dbs.commitDBSession();
            updater.removeUpdater(project.getName(), UpdaterService.UpdateTarget.MAIL);
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
               
        try {
            
            for (MailingList ml : mllist) {
                processList(mailAccessor, ml);
                createThreads(mailAccessor, ml);
                if (!dbs.commitDBSession()) 
                    dbs.rollbackDBSession();
                dbs.startDBSession();
            }
            
            MetricActivator ma = AlitheiaCore.getInstance().getMetricActivator();
            
            if (!updMails.isEmpty()) {
                ma.runMetrics(updMails, MailMessage.class);
                ma.runMetrics(updDevs, Developer.class);
            }
            
            if (!updMailingLists.isEmpty()) {
                ma.runMetrics(updMailingLists, MailingList.class);
            }
        } finally {
            if (dbs.isDBSessionActive())
                dbs.commitDBSession();
            updater.removeUpdater(project.getName(), UpdaterService.UpdateTarget.MAIL);
        }   
    }

    private void processList(MailAccessor mailAccessor, MailingList mllist) {
        List<String> fileNames = Collections.emptyList();
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
                    
                    /*512 characters should be enough for everybody*/
                    String subject = mm.getSubject();
                    if (mm.getSubject().length() > 512)
                        subject = subject.substring(0, 511);
                    
                    mmsg.setSubject(subject);
                    mmsg.setFilename(fileName);
                    dbs.addRecord(mmsg);
                    logger.debug("Adding message " + mm.getMessageID());
                    
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
    
    /**
     * Create a parent - child thread hierarchy
     */
    private void createThreads(MailAccessor mailAccessor, MailingList ml) {
        int newThreads = 0, updatedThreads = 0;
        //Get mails for this ML for the last month
        GregorianCalendar gc = new GregorianCalendar();
        MailMessage lastEmail = null;
        lastEmail = ml.getLatestEmail();
        HashMap<String, MimeMessage> processed = new HashMap<String, MimeMessage>();
        
        if (lastEmail == null) {
            return; //No messages for this mailing list
        }
        
        gc.setTime(lastEmail.getSendDate());
        gc.add(GregorianCalendar.MONTH, -1);
        
        List<MailMessage> mmList = ml.getMessagesNewerThan(gc.getTime());
        
        if (mmList.isEmpty())
            return;
        
        for (MailMessage mail : mmList) {
            List<String> refs = new ArrayList<String>();
            try {
                MimeMessage mm = mailAccessor.getMimeMessage(ml.getListId(), mail.getFilename());
                processed.put(mail.getFilename(), mm);
                
                /* Thread identification code. Naive, but works in most cases*/
                String[] inReplyTo = mm.getHeader("In-Reply-To");
                String[] references = mm.getHeader("References");
                boolean newThread = false;
                String parentId = null;
                
                if (inReplyTo == null) {
                    if (references == null) {
                        newThread = true;
                    } else {
                        //Arbitrarily set first message reference as parent.
                        //Mime message protocol does not specify any such
                        //ordering, in fact it does not specify any ordering
                        //scheme at all.
                        parentId = references[0];
                    }
                } else {
                    /*
                     * In most cases, the first in-reply-to entry corresponds to
                     * the answered email. If not, the thread is still valid.
                     */
                    parentId = inReplyTo[0];
                }
                
                MailingListThread mlt = null;
                /*Get the parent mail object*/
                MailMessage parentMail = MailMessage.getMessageById(parentId);
                
                if (parentId != null) {
                    /* Try to find the thread object created by the parent*/
                    Map<String,Object> properties = new HashMap<String,Object>();
                    properties.put("mail", parentMail);
                    List<MailThread> threads = dbs.findObjectsByProperties(MailThread.class, properties);
                    
                    /* Safeguard */
                    if (threads.size() > 1) {
                        logger.warn("Message " + parentMail + " belongs to " +
                        		"more than one thread?");
                    }
                    /*Parent-less child, to be de*/
                    if (threads != null && threads.size() == 0) {
                        newThread = true;
                    } else {
                        /*Add the processed message as child to the parent's thread*/
                        MailThread mt = new MailThread(mail, parentMail, 
                                threads.get(0).getThread(), 
                                (threads.get(0).getDepth()) + 1);
                        dbs.addRecord(mt);
                        logger.debug("Updating thread " + mt.getThread().getId());
                        updatedThreads++;
                    }
                }
                
                if (newThread) {
                    boolean childExists = false;
                    
                    /* 
                     * Check if a child mail has arrived before the 
                     * processed mail.
                     */
                    for(String key : processed.keySet()) {
                        MimeMessage child = processed.get(key);
                        if ((child.getHeader("In-Reply-To") != null
                                && child.getHeader("In-Reply-To")[0].equals(mail.getMessageId()))
                            || (child.getHeader("References") != null 
                                && child.getHeader("References")[0].equals(mail.getMessageId()))) {
                            
                            childExists = true;
                            
                            /*Get thread whose parent is the discovered child*/
                            MailMessage childMM = MailMessage.getMessageById(child.getMessageID());
                            MailingListThread thr = childMM.getThread();
                            
                            /*Set the old thread parent as child of the current mail
                             * and current email as top level parent of the thread
                             */
                            childMM.getThreadEntry().setParent(mail);
                            MailThread mt = new MailThread(mail, null, thr, 0);
                            dbs.addRecord(mt);
                            
                            logger.debug("Reconstructing thread " + thr.getId());
                            
                            /*New top level email added, increase depth level*/
                            for (MailMessage threadEntry : thr.getMessages()) {
                                threadEntry.getThreadEntry().setDepth(threadEntry.getThreadEntry().getDepth() + 1);
                            }
                        }
                    }
                    
                    if (childExists)
                        continue;
                    
                    /*Create a new thread*/
                    mlt =  new MailingListThread(ml);
                    dbs.addRecord(mlt);
                    /*Add this message as top-level parent to the thread*/
                    MailThread mt = new MailThread(mail, null, mlt, 0);
                    dbs.addRecord(mt);
                    mt.setMail(mail);
                    
                    logger.debug("Adding new thread " + mlt.getId());
                    newThreads++;
                } 
                
            } catch (IllegalArgumentException e) {
                //Ignored, should have been caught earlier
            } catch (FileNotFoundException e) {
                //Ignored, should have been caught earlier
            } catch (MessagingException e) {
                logger.warn("Could not get mime header for messageId: " 
                        + mail.getId() + " " + e.getMessage());
            }
        }
        
        logger.info("Mail thread updater - " + ml 
                + " " + newThreads + " new threads, " + updatedThreads 
                + " updated threads" );
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

