/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2009 - 2010 - Organization for Free and Open Source Software,  
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A representation of the status of the file in this revision:
 * <ul>
 * <li>ADDED</li>
 * <li>MODIFIED</li>
 * <li>DELETED</li>
 * <li>REPLACED</li>
 * </ul>
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 * 
 * @assoc 1 - n ProjectFile
 */
@Entity
@Table(name="PROJECT_FILE_STATE")
@XmlRootElement
public class ProjectFileState extends DAObject {
    
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="PROJECT_FILE_STATE_ID")
	private long id; 
    
    @Column(name="STATUS")
    @XmlElement
    private int status;
    
    @OneToMany(fetch = FetchType.LAZY, orphanRemoval=true, mappedBy="state")
    private Set<ProjectFile> files;
    
    @Override
	public String toString() {
    	return FileState.fromInt(this.status).toString();
    }
    
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
    public FileState getFileStatus() {
    	return FileState.fromInt(this.status);
    }
    
    public void setFileStatus(FileState s) {
    	this.status = s.getState();
    }

    public Set<ProjectFile> getFiles() {
        return files;
    }

    public void setFiles(Set<ProjectFile> files) {
        this.files = files;
    }

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
