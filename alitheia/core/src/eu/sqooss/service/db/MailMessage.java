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


package eu.sqooss.service.db;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.core.AlitheiaCore;

/**
 * DAO Object for the MailMessage database table
 */
public class MailMessage extends DAObject {
    /**
     * the sender of the email
     */
    Developer sender;

    /**
     * The list to which the email was orignally sent
     */
    MailingList list;

    /**
     * Unique ID for this message in the database
     */
    String messageId;

    /**
     * The subject of the email
     */
    String subject;

    /**
     * The date on which the email was originally sent
     */
    Date sendDate;

    /**
     * The date on which the email was archived by the list managing
     * software
     */
    Date arrivalDate;
    
    /**
     * Message file name, to connect to the actual file.
     */
    String fileName;
    
    public Date getArrivalDate() {
	return arrivalDate;
    }
    
    public void setArrivalDate(Date ad) {
	this.arrivalDate = ad;
    }

    public Developer getSender() {
        return sender;
    }

    public void setSender( Developer value ) {
        sender = value;
    }

    public MailingList getList() {
        return list;
    }

    public void setList( MailingList value ) {
        list = value;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId( String value ) {
        messageId = value;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate( Date value ) {
        sendDate = value;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject( String value ) {
        subject = value;
    }
    
    public String getFilename() {
        return fileName;
    }

    public void setFilename(String filename) {
        this.fileName = filename;
    }
    
    /**
     * Return a stored mail message based on messageId
     */
    public static MailMessage getMessageById(String messageId) {
    	DBService dbs = AlitheiaCore.getInstance().getDBService();
    	Map<String,Object> properties = new HashMap<String, Object>(1);
    	properties.put("messageId", messageId);
    	List<MailMessage> msgList = dbs.findObjectsByProperties(MailMessage.class, properties);
    	
    	if ((msgList == null) || (msgList.isEmpty())) {
    	    return null;
    	}
    	
    	return msgList.get(0);
    }
    
    /**
     * 
     *Return a stored mail message based on filename
     */
    public static MailMessage getMessageByFileName(String filename) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        Map<String,Object> properties = new HashMap<String, Object>(1);
        properties.put("fileName", filename);
        List<MailMessage> msgList = dbs.findObjectsByProperties(MailMessage.class, properties);
        
        if ((msgList == null) || (msgList.isEmpty())) {
            return null;
        }
        
        return msgList.get(0);
    }
    
    /**
     * Get the latest known mail message for the provided project, or null.  
     */
    public static MailMessage getLatestMailMessage(StoredProject sp) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        String paramStoredProject = "paramStoredProject";

        String query = "select mm " 
                + " from MailMessage mm, MailingList ml "
                + " where mm.list = ml " 
                + " and ml.storedProject = :" + paramStoredProject 
                + " order by mm.sendDate desc";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(paramStoredProject, sp);

        List<MailMessage> mm = (List<MailMessage>) dbs.doHQL(query, params, 1);

        if (!mm.isEmpty())
            return mm.get(0);

        return null;
    }

    /**
     * Get the thread this mail message belongs to.
     * 
     * @return The thread this mail belongs to or null if the message cannot
     *  be found in the mailthread table (e.g. when the message is too old).
     */
    public MailingListThread getThread() {
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("mail", this);

        List<MailThread> mt = dbs.findObjectsByProperties(MailThread.class,
                params);

        if (!mt.isEmpty())
            return mt.get(0).getThread();

        return null;
    }
    
    @Override
    public String toString() {
        return "MailMessage(" + sender + "," + subject + "," + sendDate + ")";
    }
}
