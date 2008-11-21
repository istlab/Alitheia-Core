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

package eu.sqooss.service.db;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.sqooss.core.AlitheiaCore;

/**
 * Instances of this class represent t=he basic details of a project
 * mailing list stored in the database
 */
public class MailingList extends DAObject {
    /**
     * List unique ID within the database
     */
    private String listId;

    /**
     * The project to which this list is related
     */
    private StoredProject storedProject;

    /**
     * The set of available messages in this list
     */
    private Set<MailMessage> messages;

    /**
     * The set of threaded discussions in this list
     */
    private Set<MailingListThread> threads; 
    
    public MailingList() {}

    public String getListId() {
        return listId;
    }

    public void setListId(String li) {
        this.listId = li;
    }

    public StoredProject getStoredProject() {
        return storedProject;
    }

    public void setStoredProject(StoredProject sp) {
        this.storedProject = sp;
    }

    public Set<MailMessage> getMessages() {
        return messages;
    }

    public void setMessages(Set<MailMessage> messages) {
        this.messages = messages;
    }
    
    public Set<MailingListThread> getThreads() {
        return threads;
    }

    public void setThreads(Set<MailingListThread> threads) {
        this.threads = threads;
    }
    
    /**
     * Get messages in this mailing list whose arrival date
     * is newer that the provided date.
     * 
     * @param d The date to compare the arrival date with
     * @return A list of messages newer than <tt>d</tt>
     */
    public List<MailMessage> getMessagesNewerThan(Date d) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        String paramDate = "paramDate";
        String paramMailingList = "paramML";
        
        String query =  " select mm " +
            " from MailMessage mm, MailingList ml " +
            " where mm.list = ml " +
            " and mm.list = :" + paramMailingList +
            " and mm.sendDate > :" + paramDate;
        
        Map<String,Object> params = new HashMap<String, Object>();
        params.put(paramDate, d);
        params.put(paramMailingList, this);
        
        List<MailMessage> msgs = (List<MailMessage>) dbs.doHQL(query, params);
        
        if (msgs == null || msgs.size() == 0)
            return Collections.emptyList();
            
        return msgs;
    }
    
    /**
     * Get messages in this mailing list whose arrival date
     * is newer that the provided date.
     * 
     * @param d The date to compare the arrival date with
     * @return A list of messages newer than <tt>d</tt>
     */
    public MailMessage getLatestEmail() {
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        String paramMailingList = "paramML";
        
        String query =  " select mm " +
            " from MailMessage mm, MailingList ml " +
            " where mm.list = ml " +
            " and mm.list = :" + paramMailingList +
            " order by mm.sendDate desc";
        
        Map<String,Object> params = new HashMap<String, Object>();
        params.put(paramMailingList, this);
        
        
        List<MailMessage> ml = (List<MailMessage>) dbs.doHQL(query, params, 1);
        
        if (ml.isEmpty())
            return null;
        
        return ml.get(0); 
    }
    
    
    @Override
    public String toString() {
        return "Mailing list("+ storedProject.getName() + "," + listId + ")"; 
    }
}
