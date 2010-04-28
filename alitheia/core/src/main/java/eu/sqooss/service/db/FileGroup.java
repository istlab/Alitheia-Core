/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 - 2010 - Organization for Free and Open Source Software,  
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
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;

/**
 * This class represents a FileGroup in the database. FileGroups are
 * cool, you should try them some day.
 */
@Entity
@Table(name="FILE_GROUP")
public class FileGroup extends DAObject {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="PROJECT_FILE_ID")
	@XmlElement
	private long id;
	
	/**
     * The FileGroup name
     */
	@Column(name="FILE_GROUP_NAME")
    private String name;
    
    /**
     * The FileGroup path (on the local file store?)
     */
	@Column(name="GROUP_SUBPATH")
    private String subPath;
    
    /**
     * A regular expression. Why not?
     */
	@Column(name="REGEX")
    private String regex;
    
    /**
     * The frequency of recaluation: daily, 4-daily etc.
     */
	@Column(name="RECALC_FREQ")
    private int recalcFreq;
    
    /**
     * The date on which the FileGroup was last accessed by a metric
     */
	@Column(name="LAST_USED")
    private Date lastUsed;
    
    /**
     * The ProjectVersion where this FG was created
     */
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="PROJECT_VERSION_ID")
    private ProjectVersion projectVersion;
    
    /**
     * The measurements for this file group
     */
	@Transient
    private Set<FileGroupMeasurement> measurements;

    public FileGroup() {
        // Nothing to do
    }

    public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubPath() {
        return subPath;
    }

    public void setSubPath(String subPath) {
        this.subPath = subPath;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public int getRecalcFreq() {
        return recalcFreq;
    }

    public void setRecalcFreq(int recalcFreq) {
        this.recalcFreq = recalcFreq;
    }

    public Date getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Date lastUsed) {
        this.lastUsed = lastUsed;
    }

    public ProjectVersion getProjectVersion() {
        return projectVersion;
    }

    public void setProjectVersion(ProjectVersion projectVersion ) {
        this.projectVersion = projectVersion;
    }

    public Set<FileGroupMeasurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(Set<FileGroupMeasurement> measurements) {
        this.measurements = measurements;
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
