/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2009 - Organization for Free and Open Source Software,  
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.mail.internet.MimeMessage;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.MailMessage;
import eu.sqooss.service.db.MailThread;
import eu.sqooss.service.db.MailingList;
import eu.sqooss.service.db.MailingListThread;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.tds.InvalidAccessorException;
import eu.sqooss.service.tds.MailAccessor;
import eu.sqooss.service.updater.UpdaterException;

/**
 * Job that organises emails in threads. Should be started each time a 
 * mailing list has received new emails.
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 *
 */
public class MailThreadUpdater extends Job {

    private MailingList ml;
    private Logger logger;
    private DBService dbs;
    private MailAccessor mailAccessor;
    
    private Set<Long> updMailThreads = new TreeSet<Long>(); 
    
    public MailThreadUpdater(MailingList ml, Logger l)
            throws UpdaterException {
        this.ml = ml;
        this.logger = l;
        
        dbs = AlitheiaCore.getInstance().getDBService();
        try {
            mailAccessor = AlitheiaCore.getInstance().getTDSService().getAccessor(
                    ml.getStoredProject().getId()).getMailAccessor();
        } catch (InvalidAccessorException e) {
            logger.error("Could not get MailAccessor for project" 
                    + ml.getStoredProject().getName());
        }
    }

    public int priority() {
        return 0x2;
    }

    @Override
    protected void run() throws Exception {
        dbs.startDBSession();
        ml = dbs.attachObjectToDBSession(ml);
        int newThreads = 0, updatedThreads = 0;
        MailMessage lastEmail = null;
        lastEmail = ml.getLatestEmail();
        HashMap<String, MimeMessage> processed = new HashMap<String, MimeMessage>();
        
        if (lastEmail == null) {
            return; //No messages for this mailing list
        }
        
        String paramMl = "paramMl";
        String query = " select mm.id " +
            " from  MailingList ml, MailMessage mm" +
            " where mm.list = ml " +
            " and ml = :" + paramMl +
            " and not exists (from MailThread mt where mt.mail = mm)" +
            " order by mm.arrivalDate asc"; 
            
        Map<String,Object> params = new HashMap<String, Object>(1);
        params.put(paramMl, ml);
        
        List<Long> mmList = (List<Long>) dbs.doHQL(query, params);
        
        if (mmList.isEmpty())
            return;
        
        for (Long mailId : mmList) {
            if (!dbs.isDBSessionActive())
                dbs.startDBSession();
            MailMessage mail = MailMessage.loadDAObyId(mailId, MailMessage.class);
            
            // Message has been already added to thread
            if (mail.getThread() != null)
                continue;

            MimeMessage mm = mailAccessor.getMimeMessage(ml.getListId(), 
                    mail.getFilename());
            
            processed.put(mail.getFilename(), mm);

            /* Thread identification code. Naive, but works */
            String[] inReplyTo = mm.getHeader("In-Reply-To");
            String[] references = mm.getHeader("References");
            boolean newThread = false, reference = false;
            String parentId = null;

            if (inReplyTo == null) {
                if (references == null) {
                    newThread = true;
                } else {
                    // Arbitrarily set first message reference as parent.
                    // The mime message protocol does not specify any such
                    // ordering, in fact it does not specify any ordering
                    // scheme at all.
                    parentId = references[0];
                    reference = true;
                }
            } else {
                /*
                 * In most cases, the first in-reply-to entry corresponds to the
                 * answered email. If not, the thread is still valid.
                 */
                parentId = inReplyTo[0];
            }

            MailingListThread mlt = null;
            /* Get the parent mail object */
            MailMessage parentMail = MailMessage.getMessageById(parentId);

            if (parentId != null) {
                /* Try to find the thread object created by the parent */
                List<MailThread> threads = MailingListThread.getThreadForMail(
                        parentMail, ml);

                /* Safeguard */
                if (threads != null && threads.size() > 1) {
                    logger.warn("Message " + parentMail + " belongs to "
                            + "more than one thread?");
                }
                
                /* Parent-less child, parent might have arrived later */
                if (threads != null && threads.size() == 0) {
                    newThread = true;
                } else {
                    /*
                     * Mails identified as children to a thread only by the
                     * References header, are placed at the same depth level as
                     * their parent (Usenet news style).
                     */
                    int depth = threads.get(0).getDepth();
                    if (!reference) {
                        depth = depth + 1;
                    }
                    /* Add the processed message as child to the parent's thread */
                    MailThread mt = new MailThread(mail, parentMail, 
                            threads.get(0).getThread(), depth);
                    dbs.addRecord(mt);
                    threads.get(0).getThread().setLastUpdated(mail.getSendDate());
                    logger.debug("Updating thread " + mt.getThread().getId());
                    updMailThreads.add(threads.get(0).getId());
                    updatedThreads++;
                }
            }

            if (newThread) {
                boolean childExists = false;

                /*
                 * Check if a child mail has arrived before the processed mail.
                 */
                for (String key : processed.keySet()) {
                    MimeMessage child = processed.get(key);
                    if ((child.getHeader("In-Reply-To") != null && 
                            child.getHeader("In-Reply-To")[0].equals(mail.getMessageId()))
                       || (child.getHeader("References") != null && 
                            child.getHeader("References")[0].equals(mail.getMessageId()))) {

                        childExists = true;

                        /* Get thread whose parent is the discovered child */
                        MailMessage childMM = MailMessage.getMessageById(child.getMessageID());
                        MailingListThread thr = childMM.getThread();

                        /*
                         * Set the old thread parent as child of the current
                         * mail and current email as top level parent of the
                         * thread
                         */
                        childMM.getThreadEntry().setParent(mail);
                        MailThread mt = new MailThread(mail, null, thr, 0);
                        dbs.addRecord(mt);
                        thr.setLastUpdated(mail.getSendDate());
                        logger.debug("Reconstructing thread " + thr.getId());

                        /* New top level email added, increase depth level */
                        for (MailMessage threadEntry : thr.getMessages()) {
                            threadEntry.getThreadEntry().setDepth(
                                    threadEntry.getThreadEntry().getDepth() + 1);
                        }
                        updMailThreads.add(thr.getId());
                        updatedThreads++;
                    }
                }

                if (childExists)
                    continue;

                /* Create a new thread */
                mlt = new MailingListThread(ml, mail.getSendDate());
                dbs.addRecord(mlt);
                /* Add this message as top-level parent to the thread */
                MailThread mt = new MailThread(mail, null, mlt, 0);
                dbs.addRecord(mt);
                mt.setMail(mail);
                logger.debug("Adding new thread " + mlt.getId());
                updMailThreads.add(mlt.getId());
                newThreads++;
            } 
            dbs.commitDBSession();
        }
        dbs.startDBSession();
        logger.info("Mail thread updater - " + ml 
                + " " + newThreads + " new threads, " + updatedThreads 
                + " updated threads" );
        AlitheiaCore.getInstance().getMetricActivator().runMetrics(updMailThreads, MailingListThread.class);
        dbs.commitDBSession();
    }   
    
    @Override
    public String toString() {
        String msg; 
        boolean commitSession = false;
        if (!dbs.isDBSessionActive()) {
            dbs.startDBSession();
            commitSession = true;
        }
        ml = dbs.attachObjectToDBSession(ml);
        msg =  "MailThreadUpdater Job - Project:{" + ml.getStoredProject() 
        + "}, Mailing List: {" + ml.getListId() + "}";
        
        if (commitSession) 
            dbs.commitDBSession();
        
        return msg;
    }
}
