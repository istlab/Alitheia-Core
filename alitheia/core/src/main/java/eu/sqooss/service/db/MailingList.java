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

/**
 * Instances of this class represent the basic details of a project
 * mailing list stored in the database
 * 
 * @assoc 1 - n MailMessage
 * @assoc 1 - n MailingListThread
 * 
 */
@Entity
@Table(name="MAILINGLIST")
public class MailingList extends DAObject {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="MLIST_ID")
	private long id; 
	
	/**
     * List unique ID within the database
     */
	@Column(name="MLIST_LISTID")
    private String listId;

    /**
     * The project to which this list is related
     */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PROJECT_ID")
    private StoredProject storedProject;

    /**
     * The set of available messages in this list
     */
	@OneToMany(mappedBy="list", orphanRemoval=true, cascade = CascadeType.ALL)
    private Set<MailMessage> messages;

    /**
     * The set of threaded discussions in this list
     */
	@OneToMany(mappedBy="list", orphanRemoval=true, cascade = CascadeType.ALL)
    private Set<MailingListThread> threads; 
    
    public MailingList() {}

    public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
    
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
    
    @Override
    public String toString() {
        return "Mailing list("+ storedProject.getName() + "," + listId + ")"; 
    }
}
