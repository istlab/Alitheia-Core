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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DAObject;

/**
 * Instances of this class represent the data of an SVN tag for a
 * project, as stored in the database
 * 
 * @assoc 1 - 1 ProjectVersion
 */
@Entity
@Table(name="TAG")
public class Tag extends DAObject {
    
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="PROJECT_FILE_ID")
	@XmlElement
	private long id;

	/**
     * The version of the project to which this tag relates
     */
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="TAG_VERSION")
    private ProjectVersion projectVersion;

    /**
     * The name of the tag provided at the time it was committed by the
     * developer
     */
	@Column(name="TAG_NAME")
    private String name;

    public Tag() {
        // Nothing to do
    }
    
    public Tag(ProjectVersion pv) {
        this.projectVersion = pv;
    }

    public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
    
    public ProjectVersion getProjectVersion() {
        return projectVersion;
    }

    public void setProjectVersion(ProjectVersion pv) {
        this.projectVersion = pv;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static ProjectVersion getProjectVersionForNamedTag(String tagName,
            StoredProject sp) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        String paramTagName = "tagname";
        String paramProject = "project_id";

        String query = "select pv " 
                + " from ProjectVersion pv, Tag t "
                + " where t.projectVersion = pv " 
                + " and t.name = :" + paramTagName 
                + " and pv.project =:" + paramProject;

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(paramTagName, tagName);
        parameters.put(paramProject, sp);

        List<?> projectVersions = dbs.doHQL(query, parameters, 1);

        if (projectVersions == null || projectVersions.size() == 0) {
            return null;
        } else {
            return (ProjectVersion) projectVersions.get(0);
        }
    }
    
    public static List<ProjectVersion> getTaggedVersions(StoredProject sp) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        String paramProject = "project_id";

        String query = "select pv " 
                + " from ProjectVersion pv, Tag t "
                + " where t.projectVersion = pv " 
                + " and pv.project =:" + paramProject;

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(paramProject, sp);

        return (List<ProjectVersion>) dbs.doHQL(query, parameters);

    }

    @Override
    public String toString() {
        return "<Tag: " + name + " from version "
                + projectVersion.getRevisionId() + ">";
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

