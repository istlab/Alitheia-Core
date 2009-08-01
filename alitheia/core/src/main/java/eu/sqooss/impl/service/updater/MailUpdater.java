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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.MailMessage;
import eu.sqooss.service.db.MailingList;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.tds.MailAccessor;
import eu.sqooss.service.tds.ProjectAccessor;
import eu.sqooss.service.updater.UpdaterException;

/**
 * Synchronises raw mails with the database.
 */
class MailUpdater extends UpdaterBaseJob {
    
    
    /*Cache mailinglist ids to call the metric activator with them*/
    private Set<Long> updMailingLists = new TreeSet<Long>();
    
    private static String[] dateFmts = {
        "EEE MMM d HH:mm:ss yyyy",  //Fri Dec  5 12:50:00 2003
        "d MMM yyyy HH:mm:ss Z",    //28 Nov 2000 18:26:25 -0500
        "MM/dd/yy KK:mm a",         //9/15/00 12:40 PM
        "d MMM yyyy HH:mm"          //16 March 1998 20:10
    };
    
    public MailUpdater() throws UpdaterException {

    }

    public int priority() {
        return 0x1;
    }

    protected void run() throws Exception {

        ProjectAccessor spAccessor = AlitheiaCore.getInstance().getTDSService().getAccessor(project.getId());
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
                ListUpdaterJob luj = new ListUpdaterJob(project.getId(), mlId);
                AlitheiaCore.getInstance().getScheduler().enqueue(luj);
                info("Added list update job for listid=" + mlId);
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
    
    private List<MailingList> getMailingLists(StoredProject sp) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("storedProject", sp);
        return dbs.findObjectsByProperties(MailingList.class, params);
    }
    
    @Override
    public Job getJob() {
        return this;
    }
    
    @Override
    public String toString() {
        return "MailUpdaterJob - Project:{" + project + "}";
    }
    

    private class ListUpdaterJob extends Job {

        long listid;
        long projectid;
        
        /*Cache mail ids to call the metric activator with them*/
        private Set<Long> updMails = new TreeSet<Long>();
        private Set<Long> updDevs = new TreeSet<Long>();
        
        public ListUpdaterJob(long projectid, long listid) {
            this.projectid = projectid;
            this.listid = listid;
        }
         
        @Override
        public int priority() {
            return 0x1;
        }

        @Override
        protected void run() throws Exception {
            ProjectAccessor spAccessor = AlitheiaCore.getInstance().getTDSService().getAccessor(projectid);
            MailAccessor mailAccessor = spAccessor.getMailAccessor();
            MetricActivator ma = AlitheiaCore.getInstance().getMetricActivator();
            dbs.startDBSession();
            MailingList ml = DAObject.loadDAObyId(listid, MailingList.class);

            if (ml == null) {
                warn("No mailing list with id " + listid);
                return;
            }

            processList(mailAccessor, ml);
            dbs.startDBSession();
            ml = dbs.attachObjectToDBSession(ml);
            MailThreadUpdater mtu = new MailThreadUpdater(ml, logger);
            AlitheiaCore.getInstance().getScheduler().enqueue(mtu);
            info("Added thread update job for " + ml);

            if (!updMails.isEmpty()) {
                ma.runMetrics(updMails, MailMessage.class);
                ma.runMetrics(updDevs, Developer.class);
            }
            dbs.commitDBSession();
        }
        
        @Override
        public String toString() {
            return "ListUpdaterJob - Projectid:{" + projectid + "} MailingList:{" + listid + "}";
        }
        
        private List<String> processList(MailAccessor mailAccessor, MailingList mllist) 
            throws IllegalArgumentException, FileNotFoundException, MessagingException {
            List<String> fileNames = Collections.emptyList();
            String listId = mllist.getListId();

            try {
                fileNames = mailAccessor.getNewMessages(listId);
            } catch (FileNotFoundException e) {
                warn("Mailing list <" + listId + "> vanished: "
                        + e.getMessage());
                return Collections.emptyList();
            }

            for (String fileName : fileNames) {
                if (!dbs.isDBSessionActive())
                    dbs.startDBSession();
                String msg = String.format("Message <%s> in list <%s> ",
                        fileName, listId);

                MimeMessage mm = mailAccessor.getMimeMessage(listId, fileName);

                if (mm == null) {
                    warn("Failed to parse message " + fileName);
                    mailAccessor
                            .markMessageAsSeen(mllist.getListId(), fileName);
                    continue;
                }

                Address[] senderAddr = mm.getFrom();
                String devName = "";
                if (senderAddr == null) {
                    warn("Message " + msg + "  has no sender. Ignoring");
                    continue;
                }

                Address actualSender = senderAddr[0];
                String senderEmail = null;
                if (actualSender instanceof InternetAddress) {
                    senderEmail = ((InternetAddress) actualSender).getAddress();
                    devName = ((InternetAddress) actualSender).getPersonal();
                } else {
                    InternetAddress inet = new InternetAddress(actualSender
                            .toString());
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
                            mllist.getStoredProject(), false);
                }

                if (sender == null) {
                    // Dev not found by name, try email
                    if (!senderEmail.contains("@")) {
                        // Email cannot be used, drop this mail
                        warn(msg + ": Not an email address: " + senderEmail);
                        mailAccessor.markMessageAsSeen(mllist.getListId(),
                                fileName);
                        continue;
                    }

                    sender = Developer.getDeveloperByEmail(senderEmail, 
                            mllist.getStoredProject(), true);

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
                    continue;
                }

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

                    Date sentDate = getSentDate(mm);
                    if (sentDate != null) {
                        mmsg.setSendDate(sentDate);
                    } else {
                        warn(msg
                                + " does not contain a parsable date, ignoring");
                        mailAccessor.markMessageAsSeen(mllist.getListId(),
                                fileName);
                        continue;
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

                    updMails.add(mmsg.getId());
                    if (dbs.commitDBSession()) {
                        if (!mailAccessor.markMessageAsSeen(mllist.getListId(),
                                fileName))
                            warn("Failed to mark message <" + fileName
                                    + "> as seen");
                    }
                }
            }
            return fileNames;
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
                //Swallow this exception here
            }
            
            if (d != null)  //Date is standards compliant
                return d;
            else 
                return getDate(date); 
        }
        
        /* Try hard to parse dates by hand as various Microsoft MUAs, Emacs,
         * Evolution and others don't feel like respecting the standards 
         * (namely rfc822 and its extension draft-ietf-drums-msg-fmt-08)
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
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

