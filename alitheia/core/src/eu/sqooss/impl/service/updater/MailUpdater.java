/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 Georgios Gousios <gousiosg@gmail.com>
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

import java.util.List;
import java.util.Set;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import eu.sqooss.core.AlitheiaCore;

import eu.sqooss.service.db.DAOException;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.MailMessage;
import eu.sqooss.service.db.MailingList;
import eu.sqooss.service.db.StoredProject;

import eu.sqooss.service.logging.Logger;

import eu.sqooss.service.scheduler.Job;

import eu.sqooss.service.tds.MailAccessor;
import eu.sqooss.service.tds.TDAccessor;

import eu.sqooss.service.updater.UpdaterException;
import eu.sqooss.service.updater.UpdaterService;

/**
 * Synchronises raw mails with the database
 *
 * @author Vassilios Karakoidas (bkarak@aueb.gr)
 */
class MailUpdater extends Job {
    private StoredProject project;
    private AlitheiaCore core;
    private Logger logger;
    private UpdaterServiceImpl updater;
    private DBService dbs;
    
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
        TDAccessor spAccessor = core.getTDSService().getAccessor(project.getId());
        MailAccessor mailAccessor = spAccessor.getMailAccessor();
        Set<MailingList> mllist = project.getMailingLists();
        if(mllist.size() == 0) {
            logger.warn("Project <" + project.getName() + " - " + project.getId() +
                        "> seem to have 0 (zero) mailing lists!!");
        }
        for ( MailingList ml : mllist ) {
            System.out.println(1);
            processList(mailAccessor, ml);
        }
        updater.removeUpdater(project.getName(), UpdaterService.UpdateTarget.MAIL);
        dbs.commitDBSession();
    }

    private void processList(MailAccessor mailAccessor, MailingList mllist) {
        List<String> messageIds = null;
        String listId = mllist.getListId();
        try {
            messageIds = mailAccessor.getNewMessages(listId);
        } catch (FileNotFoundException e) {
            logger.warn("Mailing list <" + listId + "> vanished: " + e.getMessage());
            return;
        }

        for ( String messageId : messageIds ) {
            String msg = String.format("Message <%s> in list <%s> ", messageId, listId);

            logger.info(msg);
            try {
                MimeMessage mm = mailAccessor.getMimeMessage(listId, messageId);
                if (mm == null) {
                    logger.info("Failed to parse message.");
                    continue;
                }
                Address[] senderAddr = mm.getFrom();
                if (senderAddr == null) {
                    logger.info("Message has no sender?");
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
                MailMessage mmsg = MailMessage.getMessageById(messageId);
                if (mmsg == null) {
                    mmsg = new MailMessage();
                    mmsg.setList(mllist);
                    mmsg.setMessageId(mm.getMessageID());
                    mmsg.setSender(sender);
                    mmsg.setSendDate(mm.getSentDate());
                    mmsg.setArrivalDate(mm.getReceivedDate());
                    mmsg.setSubject(mm.getSubject());
                    dbs.addRecord(mmsg);
                }
                
                if (!mailAccessor.markMessageAsSeen(listId, messageId)) {
                    logger.warn("Failed to mark message as seen.");
                }
            } catch (FileNotFoundException e) {
                logger.warn(msg + "not found: " + e.getMessage());
            } catch (MessagingException me) {
                logger.warn(msg + " could not be parsed! - " + me.toString());
            } catch (DAOException daoe) {
                logger.warn(msg + " error - " + daoe.toString());
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn(msg + " error - " + e.getMessage());
            }
	}
    }
}

/*
 * TODO:
 * - check consistency (regularly?) by examining all messages and all database entries
 * - prevent multiple update jobs from running at once
 */

// vi: ai nosi sw=4 ts=4 expandtab

