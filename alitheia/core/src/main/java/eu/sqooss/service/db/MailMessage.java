/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - 2010 - Organization for Free and Open Source Software,  
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import eu.sqooss.core.AlitheiaCore;

/**
 * DAO Object for the MailMessage database table
 * @assoc 1 - n MailMessageMeasurement
 */
@Entity
@Table(name="MAILMESSAGE")
@XmlRootElement(name="mlmsg")
public class MailMessage extends DAObject {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="MAILMESSAGE_ID")
	private long id; 
	
	/**
     * the sender of the email
     */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="SENDER_ID")
    private Developer sender;

    /**
     * The list to which the email was originally sent
     */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="MLIST_ID")
    private MailingList list;

    /**
     * Unique ID for this message in the database
     */
	@Column(name="MESSAGEID")
    private String messageId;

    /**
     * The subject of the email
     */
	@Column(name="SUBJECT")
    private String subject;

    /**
     * The date on which the email was originally sent
     */
	@Column(name="SEND_DATE")
    private Date sendDate;
    
    /**
     * Message file name, to connect to the actual file.
     */
	@Column(name="FILE_NAME")
    private String fileName;
    
    /**
     * The thread this mail message belongs to.
     */
    @ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="THREAD_ID")
    private MailingListThread thread;
    
    /**
     * The message's nesting level in the thread it belongs to
     */
    @Column(name="DEPTH")
    private int depth;
    
    /**
     * The message that is the immediate parent to this email in the
     * thread they belong to.
     */
    @ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PARENT_ID")
    private MailMessage parent;
   
    @OneToMany(fetch=FetchType.LAZY, mappedBy="mail", cascade=CascadeType.ALL)
    private Set<MailMessageMeasurement> measurements;
    
    public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

    
    public MailingListThread getThread() {
        return thread;
    }

    public void setThread(MailingListThread thread) {
        this.thread = thread;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
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
    
    public MailMessage getParent() {
        return parent;
    }

    public void setParent(MailMessage parent) {
        this.parent = parent;
    }
    
    public void setMeasurements(Set<MailMessageMeasurement> measurements) {
		this.measurements = measurements;
	}

	public Set<MailMessageMeasurement> getMeasurements() {
		return measurements;
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
     * Return a stored mail message based on filename
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
    
    @Override
    public String toString() {
        return "MailMessage(" + sender + "," + subject + "," + sendDate + ")";
    }
}
