/*
 * Copyright 2010 - Organization for Free and Open Source Software,  
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.tds.MailAccessor;
import eu.sqooss.service.tds.ProjectAccessor;

public class MailMessageJob extends Job{

    private static String[] dateFmts = {
        "EEE MMM d HH:mm:ss yyyy",  //Fri Dec  5 12:50:00 2003
        "d MMM yyyy HH:mm:ss Z",    //28 Nov 2000 18:26:25 -0500
        "MM/dd/yy KK:mm a",         //9/15/00 12:40 PM
        "d MMM yyyy HH:mm"          //16 March 1998 20:10
    };
    
    Logger logger;
    String fileName;
    MailingList ml;
    StoredProject project;
    int progress = 0;
    
    public MailMessageJob(MailingList ml, String f, Logger l) {
        super(AlitheiaCore.getInstance().getDBService());
        this.logger = l;
        this.ml = ml;
        this.fileName = f;
    }
    
    @Override
    public long priority() {
        return 3;
    }

    @Override
    protected void run() throws Exception {
        
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        dbs.startDBSession();

        ml = dbs.attachObjectToDBSession(ml);
        project = ml.getStoredProject();
        
        ProjectAccessor spAccessor = AlitheiaCore.getInstance().getTDSService().getAccessor(project.getId());
        MailAccessor mailAccessor = spAccessor.getMailAccessor();
                
        String msg = String.format("Message <%s> in list <%s> ", fileName,
                ml.getListId());

        MimeMessage mm = mailAccessor.getMimeMessage(ml.getListId(), fileName);

        if (mm == null) {
            warn("Failed to parse message " + fileName);
            mailAccessor.markMessageAsSeen(ml.getListId(), fileName);
            return;
        }

        Address[] senderAddr = mm.getFrom();
        String devName = "";
        if (senderAddr == null) {
            warn("Message " + msg + "  has no sender. Ignoring");
            return;
        }

        Address actualSender = senderAddr[0];
        String senderEmail = null;
        if (actualSender instanceof InternetAddress) {
            senderEmail = ((InternetAddress) actualSender).getAddress();
            devName = ((InternetAddress) actualSender).getPersonal();
        } else {
            InternetAddress inet = new InternetAddress(
                    actualSender.toString());
            senderEmail = inet.getAddress();
        }

        // Purify the developer's name
        if (devName != null && devName.contains("\"")) {
            devName = devName.replace("\"", "");
        }

        Developer sender = null;

        // Try to find developer from name first
        if (devName != null) {
            sender = Developer.getDeveloperByName(devName,
                    ml.getStoredProject(), false);
        }

        if (sender == null) {
            // Dev not found by name, try email
            if (!senderEmail.contains("@")) {
                // Email cannot be used, drop this mail
                warn(msg + ": Not an email address: " + senderEmail);
                mailAccessor.markMessageAsSeen(ml.getListId(), fileName);
                return;
            }

            sender = Developer.getDeveloperByEmail(senderEmail,
                    ml.getStoredProject(), true);

            // Found dev by email, but not by name
            // Add a name to the developer, if we have one
            if (devName != null)
                sender.setName(devName);
        } else {
            // Add a new email alias, if not exists
            sender.addAlias(senderEmail);
        }

        // By now we should have a developer associated with the
        // processed email;
        // if not some other error occurs, complain about this and
        // abandon
        if (sender == null) {
            err("Error adding developer");
            return;
        }

        MailMessage mmsg = MailMessage.getMessageById(fileName);
        if (mmsg == null) {
            // if the message does not exist in the database, then
            // write a new one
            mmsg = new MailMessage();
            mmsg.setList(ml);
            mmsg.setMessageId(mm.getMessageID());
            mmsg.setSender(sender);

            Date sentDate = getSentDate(mm);
            if (sentDate != null) {
                mmsg.setSendDate(sentDate);
            } else {
                warn(msg + " does not contain a parsable date, ignoring");
                mailAccessor.markMessageAsSeen(ml.getListId(), fileName);
                return;
            }

            /* 512 characters should be enough subject for everybody */
            String subject = mm.getSubject();
            if (subject != null) {
                if (mm.getSubject().length() > 512)
                    subject = subject.substring(0, 511);
            }

            mmsg.setSubject(subject);
            mmsg.setFilename(fileName);
            dbs.addRecord(mmsg);
            debug("Adding message " + mm.getMessageID());

            if (dbs.commitDBSession()) {
                if (!mailAccessor.markMessageAsSeen(ml.getListId(),
                        fileName))
                    warn("Failed to mark message <" + fileName
                            + "> as seen");
            }   
        }        
    }
    
    private Date getSentDate(MimeMessage mm) {
        Date d = null;
        String date = null;
        try {
            d = mm.getSentDate();
            String[] dates = mm.getHeader("Date");

            if (dates != null && dates.length > 0)
                date = mm.getHeader("Date")[0];

        } catch (MessagingException e) {
            // Swallow this exception here
        }

        if (d != null) // Date is standards compliant
            return d;
        else
            return getDate(date);
    }

    /*
     * Try hard to parse dates by hand as various Microsoft MUAs, Emacs,
     * Evolution and others don't feel like respecting the standards (namely
     * rfc822 and its extension draft-ietf-drums-msg-fmt-08)
     */
    private Date getDate(String date) {
        if (date == null)
            return null;

        Date d = null;
        for (String fmt : dateFmts) {
            try {
                DateFormat df = new SimpleDateFormat(fmt);
                d = df.parse(date.trim());
            } catch (ParseException e) {
                continue;
            }
            break;
        }
        return d;
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
    
    @Override
    public String toString() {
        String txt =  "MailMessageJob - Message:{" + fileName + "}";
        return txt;
    }
}
