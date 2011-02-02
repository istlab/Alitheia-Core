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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlElement;

import eu.sqooss.core.AlitheiaCore;

/**
 * Keeps track of branches. 
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 * 
 * @assoc 1 - n ProjectVersion
 */
@Entity
@Table(name="BRANCH", uniqueConstraints=@UniqueConstraint(columnNames="BRANCH_NAME"))
public class Branch extends DAObject {
	
	private static final String qBranchByName = 
		"from Branch b where b.name = :name and b.project = :project";
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="BRANCH_ID")
	@XmlElement
	private long id;
	
	@OneToMany(mappedBy="branch", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Collection<ProjectVersion> versions;

	@Column(name="BRANCH_NAME", unique=true, nullable = false)
	@XmlElement
	private String name;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="STORED_PROJECT_ID")
	@XmlElement
	private StoredProject project;
	
	public Branch() {
		versions = new ArrayList<ProjectVersion>();
	}
	
	public Branch(StoredProject sp, String name) {
		versions = new ArrayList<ProjectVersion>();
		project = sp;
		this.name = name;
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
	
	public Collection<ProjectVersion> getVersions() {
		return versions;
	}

	public void setVersions(Collection<ProjectVersion> versions) {
		this.versions = versions;
	}
	
	public void addVersion(ProjectVersion pv) {
		if (versions == null)
			versions = new ArrayList<ProjectVersion>();
		versions.add(pv);
	}
	
	public void setProject(StoredProject project) {
		this.project = project;
	}

	public StoredProject getProject() {
		return project;
	}
	
	public static Branch fromName(StoredProject sp, String name) {
		DBService db = AlitheiaCore.getInstance().getDBService();
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("name", name);
		params.put("project", sp);
		
		List<Branch> branches = (List<Branch>)db.doHQL(qBranchByName, params);
		if (branches.isEmpty())
			return null;
		
		return branches.get(0);
	}
}
