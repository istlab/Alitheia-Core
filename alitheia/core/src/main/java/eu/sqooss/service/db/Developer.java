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

import java.util.HashSet;
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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.sqooss.service.db.DAObject;

/**
 * A DAObject representing a developer belonging to a project.
 * 
 * @assoc 1 - n DeveloperAlias
 * @assoc 1 - n ProjectVersion
 * @assoc 1 - n MailMessage
 * @assoc 1 - n BugReportMessage
 */
@XmlRootElement
@Entity
@Table(name="DEVELOPER")
public class Developer extends DAObject {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="DEVELOPER_ID")
	@XmlElement
	private long id; 

	/**
     * The developer's name
     */
	@XmlElement
	@Column(name="NAME")
    private String name;

    /**
     * The developer's username
     */
	@XmlElement
	@Column(name="USERNAME")
    private String username;

    /**
     * The list of developer emails
     */
	@XmlElement
	@OneToMany(fetch=FetchType.LAZY, mappedBy="developer", orphanRemoval=true, cascade=CascadeType.ALL)
    private Set<DeveloperAlias> aliases = new HashSet<>();
    
    /**
     * The project this developer belongs to
     */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="STORED_PROJECT_ID")
    private StoredProject storedProject;
	
    /**
     * The list of commits from this developer
     */
    @OneToMany(fetch=FetchType.LAZY, mappedBy="committer", orphanRemoval=true)
    private Set<ProjectVersion> commits;
	
    /**
     * The list of mails sent by this developer
     */
    @OneToMany(fetch=FetchType.LAZY, mappedBy="sender", orphanRemoval=true)
    private Set<MailMessage> mails;
	
    /**
     * The list of bug report messages sent by this developer
     */
    @OneToMany(fetch=FetchType.LAZY, mappedBy="reporter", orphanRemoval=true)
    private Set<BugReportMessage> bugReportMessages;

    public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
    
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public StoredProject getStoredProject() {
        return storedProject;
    }

    public void setStoredProject(StoredProject storedProject) {
        this.storedProject = storedProject;
    }
    
    public Set<ProjectVersion> getCommits() {
        return commits;
    }

    public void setCommits(Set<ProjectVersion> commits) {
        this.commits = commits;
    }

    public Set<MailMessage> getMails() {
        return mails;
    }

    public void setMails(Set<MailMessage> mails) {
        this.mails = mails;
    }

    public Set<BugReportMessage> getBugReportMessages() {
        return bugReportMessages;
    }

    public void setBugReportMessages(Set<BugReportMessage> bugReportMessages) {
        this.bugReportMessages = bugReportMessages;
    }
    
    public Set<DeveloperAlias> getAliases() {
        return aliases;
    }

    public void setAliases(Set<DeveloperAlias> aliases) {
        this.aliases = aliases;
    }
    
    /**
     * Adds a new email alias for the developer, if it doesn't already exist.
     * @param email The email to setup an alias for. This method does not check
     * if the email is a correct and valid email address.
     */
    public void addAlias(String email) {
        DeveloperAlias da = new DeveloperAlias(email, this);
        if (! getAliases().contains(da))
            getAliases().add(da);
    }
    
    public String toString() {
        StringBuffer dev =  new StringBuffer(); 
        dev.append(name).append(", aka:").append(username).append(" (");
        for (DeveloperAlias d : getAliases()) {
            dev.append("<");
            dev.append(d.getEmail());
            dev.append(">, ");
        }
        dev.deleteCharAt(dev.lastIndexOf(",")).append(")");
        return dev.toString();
    }
}

//vi: ai nosi sw=4 ts=4 expandtab

