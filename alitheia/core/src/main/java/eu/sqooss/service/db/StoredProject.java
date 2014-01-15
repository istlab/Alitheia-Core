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

import java.util.Collections;
import java.util.List;
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

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.util.ConfigurationOptionUtils;
import eu.sqooss.service.db.util.StoredProjectUtils;

/**
 * This class represents a project that Alitheia "knows about".
 * These projects are the ones that are examined by the cruncher.
 * Basically, if the cruncher is operating on a project, there
 * has to be a record of this type in the system.
 * 
 * @assoc 1 - n Bug
 * @assoc 1 - n MailingList
 * @assoc 1 - n ProjectVersion
 * @assoc 1 - n StoredProjectMeasurement
 * @assoc 1 - n Developer
 * @assoc 1 - n Branch
 * @assoc "m defines\r" - "n\n\n" ConfigurationOption
 */
@XmlRootElement(name="project")
@Entity
@Table(name="STORED_PROJECT")
public class StoredProject extends DAObject {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="PROJECT_ID")
	@XmlElement
	private long id;

	@XmlElement
	@Column(name="PROJECT_NAME")
	private String name;
	
    /**
     * The versions that this project contains
     */
    @OneToMany(fetch=FetchType.LAZY, mappedBy="project", cascade=CascadeType.ALL)
    private List<ProjectVersion> projectVersions;
    
    @OneToMany(fetch=FetchType.LAZY, mappedBy="storedProject", cascade=CascadeType.ALL)
    private Set<Developer> developers;
    
    @OneToMany(fetch=FetchType.LAZY, mappedBy="storedProject", cascade=CascadeType.ALL)
    private Set<MailingList> mailingLists;
    
    @OneToMany(fetch=FetchType.LAZY, mappedBy="storedProject", cascade=CascadeType.ALL)
    private Set<StoredProjectMeasurement> measurements;
    
    @OneToMany(fetch=FetchType.LAZY, mappedBy="project", cascade=CascadeType.ALL)
	private Set<Bug> bugs;

    @OneToMany(fetch=FetchType.LAZY, mappedBy="project", cascade=CascadeType.ALL)
	private Set<StoredProjectConfig> configOpts;
   
    @ManyToOne(fetch=FetchType.LAZY, optional = true)
    @JoinColumn(name="CLUSTERNODE_ID")
    private ClusterNode clusternode;
	
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="project")
	private Set<Branch> branches;

    public StoredProject() {}
    
    public StoredProject(String name) {
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

    public String getWebsiteUrl() {
        return getConfigValue(ConfigOption.PROJECT_WEBSITE.getKey());
    }

    public void setWebsiteUrl(String url) {
    	DBService dbs = AlitheiaCore.getInstance().getDBService();
    	new StoredProjectUtils(dbs, new ConfigurationOptionUtils(dbs)).addConfig(this, ConfigOption.PROJECT_WEBSITE, url);
    }

    public String getContactUrl() {
    	return getConfigValue(ConfigOption.PROJECT_CONTACT.getKey());
    }

    public void setContactUrl(String url) {
    	DBService dbs = AlitheiaCore.getInstance().getDBService();
    	new StoredProjectUtils(dbs, new ConfigurationOptionUtils(dbs)).addConfig(this, ConfigOption.PROJECT_CONTACT, url);
    }

    public String getBtsUrl() {
    	return getConfigValue(ConfigOption.PROJECT_BTS_URL.getKey());
    }

    public void setBtsUrl(String url) {
    	DBService dbs = AlitheiaCore.getInstance().getDBService();
    	new StoredProjectUtils(dbs, new ConfigurationOptionUtils(dbs)).addConfig(this, ConfigOption.PROJECT_BTS_URL, url);
    }

    public String getScmUrl() {
    	return getConfigValue(ConfigOption.PROJECT_SCM_URL.getKey());
    }

    public void setScmUrl(String url) {
    	DBService dbs = AlitheiaCore.getInstance().getDBService();
    	new StoredProjectUtils(dbs, new ConfigurationOptionUtils(dbs)).addConfig(this, ConfigOption.PROJECT_SCM_URL, url);
    }

    public String getMailUrl() {
    	return getConfigValue(ConfigOption.PROJECT_ML_URL.getKey());
    }

    public void setMailUrl(String url) {
    	DBService dbs = AlitheiaCore.getInstance().getDBService();
    	new StoredProjectUtils(dbs, new ConfigurationOptionUtils(dbs)).addConfig(this, ConfigOption.PROJECT_ML_URL, url);
    }
    
    public List<ProjectVersion> getProjectVersions() {
        return projectVersions;
    }

    public List<ProjectVersion> getTaggedVersions() {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
    	return new StoredProjectUtils(dbs, new ConfigurationOptionUtils(dbs)).getTaggedVersions(this);
    }
    
    public void setProjectVersions(List<ProjectVersion> projectVersions) {
        this.projectVersions = projectVersions;
    }

    public Set<Developer> getDevelopers() {
        return developers;
    }

    public void setDevelopers(Set<Developer> developers) {
        this.developers = developers;
    }

    public Set<MailingList> getMailingLists() {
        return mailingLists;
    }

    public void setMailingLists(Set<MailingList> mailingLists) {
        this.mailingLists = mailingLists;
    }

    public Set<StoredProjectMeasurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(Set<StoredProjectMeasurement> measurements) {
        this.measurements = measurements;
    }

    public Set<StoredProjectConfig> getConfigOpts() {
        return configOpts;
    }

    public void setConfigOpts(Set<StoredProjectConfig> configOpts) {
        this.configOpts = configOpts;
    }

    public void setClusternode (ClusterNode assignment) {
        this.clusternode = assignment;
    }

    public ClusterNode getClusternode() {
        return clusternode;
    }
    
    public void setBranches(Set<Branch> branches) {
		this.branches = branches;
	}

	public Set<Branch> getBranches() {
		return branches;
	}
	
    public Set<Bug> getBugs() {
        return bugs;
    }

    public void setBugs(Set<Bug> bugs) {
        this.bugs = bugs;
    } 
    
    /**
     * Get the first (in an arbitrary definition of order) value for
     * a configuration option.  
     * @param key The {@link ConfigOption} to look the value for
     * @return The configuration value or null, if the option is not set
     */
    public String getConfigValue (ConfigOption key) {
    	return getConfigValue(key.getKey());
    }
    
    /**
     * Get the first (in an arbitrary definition of order) value for
     * a configuration option.  
     * @param key The key to retrieve a value for
     * @return The configuration value or null, if the option is not set
     */
    public String getConfigValue (String key) {
    	List<String> values = getConfigValues(key);
    	if (values.isEmpty())
    		return null;
    	return values.get(0);
    }
    
    /**
     * Get the values for a project configuration entry.
     * @param co The {@link ConfigOption} to look the value for
     * @return A list of values for the provided configuration option 
     */
    public List<String> getConfigValues (ConfigOption co) {
    	return getConfigValues(co.getKey());
    }
    
    /** 
     * Get the values for a project configuration entry.
     * 
     * @param key The key whose value we want to retrieve
     */
    public List<String> getConfigValues (String key) {
    	ConfigurationOption co = new ConfigurationOptionUtils(AlitheiaCore.getInstance().getDBService()).getConfigurationOptionByKey(key);
    	
    	if (co == null)
    		return Collections.emptyList();
    	
    	DBService dbs = AlitheiaCore.getInstance().getDBService();
    	return new StoredProjectUtils(dbs, new ConfigurationOptionUtils(dbs)).getValues(this, co);
    }

    /**
     * @see {@link StoredProjectUtils#isEvaluated(StoredProject)}
     */
    public boolean isEvaluated() {
    	DBService dbs = AlitheiaCore.getInstance().getDBService();
    	return new StoredProjectUtils(dbs, new ConfigurationOptionUtils(dbs)).isEvaluated(this);
    }

	@Override
    public String toString() {
        return getName();
    }
    
    public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		StoredProject test = (StoredProject) obj;
		return  (name != null && name.equals(test.name));
	}

	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + (null == name ? 0 : name.hashCode());
		return hash;
	}
}

// vi: ai nosi sw=4 ts=4 expandtab

