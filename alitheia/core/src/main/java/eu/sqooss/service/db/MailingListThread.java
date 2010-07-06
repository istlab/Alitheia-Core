/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - 2010 - Organization for Free and Open Source Software,  
 *                Athens, Greece.
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
 * Entity that holds information about a mailing list thread.
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 * 
 * @assoc 1 - n MailMessage
 * @assoc 1 - n MailingListThreadMeasurement
 */
@Entity
@Table(name="MAILINGLIST_THREAD")
@XmlRootElement(name="mlthread")
public class MailingListThread extends DAObject {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="MLTHREAD_ID")
	private long id;
	
    /** The mailing list this thread belongs to */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="MAILING_LIST_ID")
    private MailingList list;

    /** Flag to identify a thread as a flamewar */
	@Column(name="IS_FLAME")
	private boolean isFlameWar;
    
    /**
     * Get the last date this thread was updated, by convention the arrival date
     * of the last email that arrived on this thread
     */
	@Column(name="LAST_UPDATED")
    private Date lastUpdated;

    /**
     * A set containing the messages that belong to this thread
     */
	@OneToMany(mappedBy="thread", orphanRemoval=true)
    private Set<MailMessage> messages;
    
	/**
     * The files changed in this version
     */
    @OneToMany(fetch=FetchType.LAZY, mappedBy="thread", cascade=CascadeType.ALL)
    private Set<MailingListThreadMeasurement> measurements;
	
    public Set<MailMessage> getMessages() {
        return messages;
    }

    public void setMessages(Set<MailMessage> messages) {
        this.messages = messages;
    }

    public MailingListThread() {}
    
    public MailingListThread(MailingList l, Date d) {
        this.list = l;
        this.isFlameWar = false;
        this.lastUpdated = d;
    }

    public MailingList getList() {
        return list;
    }

    public void setList(MailingList list) {
        this.list = list;
    }

    public boolean getIsFlameWar() {
        return isFlameWar;
    }

    public void setIsFlameWar(boolean isFlameWar) {
        this.isFlameWar = isFlameWar;
    }
    
    public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setFlameWar(boolean isFlameWar) {
		this.isFlameWar = isFlameWar;
	}
    
    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    /**
     * Get the email that kickstarted this thread.
     */
    public MailMessage getStartingEmail() {

        DBService dbs = AlitheiaCore.getInstance().getDBService();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("thread", this);
        params.put("depth", 0);
        
        List<MailMessage> mm = dbs.findObjectsByProperties(MailMessage.class,
                params);

        if (!mm.isEmpty())
            return mm.get(0);

        return null;
    }
    
    /**
     * Get all messages in this thread by order of arrival.
     * 
     * @return The last MailMessage in a thread.
     */
    public List<MailMessage> getMessagesByArrivalOrder() {
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        String paramThread = "paramThread";
        
        String query = "select mm " +
                " from MailMessage mm, MailingListThread mt " +
                " where mm.thread = mt " +
                " and mt = :" + paramThread + 
                " order by mm.sendDate asc" ;
        Map<String,Object> params = new HashMap<String, Object>(1);
        params.put(paramThread, this);
        
        List<MailMessage> mm = (List<MailMessage>) dbs.doHQL(query, params);
        
        if (mm == null || mm.isEmpty())
            return Collections.emptyList();
        
        return mm;
    }
    
    /**
     * Get the number of levels in the reply tree.
     */
    public int getThreadDepth() {
        
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        String paramThread = "paramThread";
        
        String query = "select max(mm.depth) " +
                " from MailMessage mm, MailingListThread mt " +
                " where mt = :" + paramThread +
                " and mm.thread = mt";
        Map<String,Object> params = new HashMap<String, Object>(1);
        params.put(paramThread, this);
        
        List<Integer> mm = (List<Integer>) dbs.doHQL(query, params, 1);
        
        if (mm == null || mm.isEmpty())
            return 0;
        
        return mm.get(0).intValue();
    }
    
    /**
     * Get all emails at the provided depth, ordered by arrival time
     * @param level The thread depth level for which to select emails.
     * @return The emails at the specified thread depth.
     */
    public List<MailMessage> getMessagesAtLevel(int level) {
        
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        String paramThread = "paramThread";
        String paramDepth = "paramDepth";
        
        String query = "select mm " +
                " from MailMessage mm, MailingListThread mlt " +
                " where mm.thread = mlt" +
                " and mlt = :" + paramThread + 
                " and mm.depth = :" + paramDepth +
                " order by mm.sendDate asc";
        
        Map<String,Object> params = new HashMap<String, Object>(1);
        params.put(paramThread, this);
        params.put(paramDepth, level);
        
        List<MailMessage> mm = (List<MailMessage>) dbs.doHQL(query, params);
        
        if (mm == null || mm.isEmpty())
            return Collections.emptyList();
        
        return mm;
    }

	public void setMeasurements(Set<MailingListThreadMeasurement> measurements) {
		this.measurements = measurements;
	}

	public Set<MailingListThreadMeasurement> getMeasurements() {
		return measurements;
	}
}
