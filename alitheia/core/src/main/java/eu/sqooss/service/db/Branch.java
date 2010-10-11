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

/**
 * Keeps track of branching and merging operations. 
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 */
@Entity
@Table(name="BRANCH")
public class Branch extends DAObject {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="BRANCH_ID")
	@XmlElement
	private long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="BRANCH_VERSION")
	private ProjectVersion branchVersion;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="MERGE_VERSION")
	private ProjectVersion mergeVersion;
	
	@Column(name="BRANCH_NAME")
	private String name;
	
	public Branch() {}
	
	public Branch(ProjectVersion branch, String name, ProjectVersion merge) {
		this.branchVersion = branch;
		this.name = name;
		this.mergeVersion = merge;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public ProjectVersion getBranchVersion() {
		return branchVersion;
	}
	public void setBranchVersion(ProjectVersion branchVersion) {
		this.branchVersion = branchVersion;
	}
	public ProjectVersion getMergeVersion() {
		return mergeVersion;
	}
	public void setMergeVersion(ProjectVersion mergeVersion) {
		this.mergeVersion = mergeVersion;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public static List<ProjectVersion> getBranchVersions(StoredProject sp) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        String paramProject = "project_id";

        String query = "select pv " 
                + " from ProjectVersion pv, Branch b "
                + " where b.branchVersion = pv " 
                + " and pv.project =:" + paramProject;

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(paramProject, sp);

        return (List<ProjectVersion>) dbs.doHQL(query, parameters);

    }
}
