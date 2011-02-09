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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
	
	private static final String qNextSequence = 
	    "select count(b) from Branch b where b.project = :project";
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="BRANCH_ID")
	@XmlElement
	private long id;

	@Column(name="BRANCH_NAME", unique=true, nullable = false)
	@XmlElement
	private String name;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="STORED_PROJECT_ID")
	@XmlElement
	private StoredProject project;
	
	@ManyToMany
	@JoinTable(
      name="BRANCH_INCOMING",
      inverseJoinColumns={@JoinColumn(name="PROJECT_VERSION_ID", referencedColumnName="PROJECT_VERSION_ID")},
      joinColumns={@JoinColumn(name="BRANCH_ID", referencedColumnName="BRANCH_ID")})
    private Set<ProjectVersion> branchIncoming = new HashSet<ProjectVersion>();
	
    @ManyToMany
    @JoinTable(
      name="BRANCH_OUTGOING",
      inverseJoinColumns={@JoinColumn(name="PROJECT_VERSION_ID", referencedColumnName="PROJECT_VERSION_ID")},
      joinColumns={@JoinColumn(name="BRANCH_ID", referencedColumnName="BRANCH_ID")})
    private Set<ProjectVersion> branchOutgoing = new HashSet<ProjectVersion>();
	
    public Branch() {}
    
    public Branch(StoredProject sp, String name) {
        this.project = sp;
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
	
	public void setProject(StoredProject project) {
		this.project = project;
	}

	public StoredProject getProject() {
		return project;
	}
	
	public Set<ProjectVersion> getBranchIncoming() {
        return branchIncoming;
    }

    public void setBranchIncoming(Set<ProjectVersion> branchIncoming) {
        this.branchIncoming = branchIncoming;
    }

    public Set<ProjectVersion> getBranchOutgoing() {
        return branchOutgoing;
    }

    public void setBranchOutgoing(Set<ProjectVersion> branchOutgoing) {
        this.branchOutgoing = branchOutgoing;
    }
	
	public static Branch fromName(StoredProject sp, String name, boolean create) {
		DBService db = AlitheiaCore.getInstance().getDBService();
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("name", name);
		params.put("project", sp);
		
		List<Branch> branches = (List<Branch>)db.doHQL(qBranchByName, params);
		if (branches.isEmpty()) {
		    if (!create)
		        return null;
		    Branch b = new Branch();
		    b.setProject(sp);
		    b.setName(name);
		    db.addRecord(b);
		    return fromName(sp, name, false);
		}
		
		return branches.get(0);
	}

    public static String suggestName(StoredProject sp) {
        DBService db = AlitheiaCore.getInstance().getDBService();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("project", sp);

        List<Long> ids = (List<Long>) db.doHQL(qNextSequence, params);
        if (ids.isEmpty())
            return "1";
        else
            return String.valueOf(ids.get(0) + 1);
    }
}
