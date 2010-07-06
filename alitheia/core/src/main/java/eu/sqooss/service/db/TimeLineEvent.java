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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * An event in a project's time line.
 */
@Entity
@Table(name="TIMELINE")
@XmlRootElement(name="timeline")
public class TimeLineEvent extends DAObject {
    
	public enum TimeLineEventType {
	    BUG, MAIL, SRC;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="INVOCATION_RULE_ID")
	@XmlElement
	private long id;

	@XmlElement
	@Column(name="SEQ_NUM")
    private long sequenceNum;

	@XmlElement
	@Column(name="TIMESTAMP")
    private Date timestamp;
    
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="STORED_PROJECT_ID")
    private StoredProject project;

	@XmlElement
	@Column(name="EVENT_ID")
    private long eventId;
    
	@XmlElement
	@Column(name="EVENT_TYPE")
    private TimeLineEventType type;
    
    public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
    
    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public TimeLineEventType getType() {
        return type;
    }

    public void setType(TimeLineEventType type) {
        this.type = type;
    }
    
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public StoredProject getProject() {
        return project;
    }

    public void setProject(StoredProject sp) {
        this.project = sp;
    }
    
    public long getSequenceNum() {
        return sequenceNum;
    }
    
    public void setSequenceNum(long sequenceNumber) {
        this.sequenceNum = sequenceNumber;
    }
    
    public static void addTimeLineEvent(ProjectVersion pv) {
        addEvent(pv.getProject(), pv.getDate(), pv.getId());
    }
    
    public static void addTimeLineEvent(MailMessage m) {
        addEvent(m.getList().getStoredProject(), m.getSendDate(), m.getId());
    }
    
    public static void addTimeLineEvent(Bug b) {
        addEvent(b.getProject(), b.getCreationTS(), b.getId());
    }
    
    private static void addEvent(StoredProject sp, Date timestamp, long eventId) {
        
    }
}
