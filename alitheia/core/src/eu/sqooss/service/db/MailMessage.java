/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2008 by Vassilios Karakoidas <bkarak@aueb.gr>
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

import java.util.*;

import eu.sqooss.impl.service.CoreActivator;

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

    public MailMessage() {}
    
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
    
    public static MailMessage getMessageById(String messageId) throws DAOException {
    	DBService dbs = CoreActivator.getDBService();
    	Map<String,Object> properties = new HashMap<String, Object>(1);
    	properties.put("messageId", messageId);
    	List<MailMessage> msgList = dbs.findObjectsByProperties(MailMessage.class, properties);
    	
    	if ((msgList == null) || (msgList.isEmpty())) {
    	    return null;
    	}
    	if(msgList.size() > 1) {
    	    throw new DAOException("MailMessage", "More than one message of MailMessage retrieved for message id " + messageId);
    	}
    	
    	return msgList.get(0);
    }
}
