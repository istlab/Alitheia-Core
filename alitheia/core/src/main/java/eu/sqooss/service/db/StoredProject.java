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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import eu.sqooss.service.db.BugStatus.Status;

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
        return getConfigValue(ConfigOption.PROJECT_WEBSITE.getName());
    }

    public void setWebsiteUrl(String url) {
    	addConfig(ConfigOption.PROJECT_WEBSITE, url);
    }

    public String getContactUrl() {
    	return getConfigValue(ConfigOption.PROJECT_CONTACT.getName());
    }

    public void setContactUrl(String url) {
    	addConfig(ConfigOption.PROJECT_CONTACT, url);
    }

    public String getBtsUrl() {
    	return getConfigValue(ConfigOption.PROJECT_BTS_URL.getName());
    }

    public void setBtsUrl(String url) {
    	addConfig(ConfigOption.PROJECT_BTS_URL, url);
    }

    public String getScmUrl() {
    	return getConfigValue(ConfigOption.PROJECT_SCM_URL.getName());
    }

    public void setScmUrl(String url) {
    	addConfig(ConfigOption.PROJECT_SCM_URL, url);
    }

    public String getMailUrl() {
    	return getConfigValue(ConfigOption.PROJECT_ML_URL.getName());
    }

    public void setMailUrl(String url) {
    	addConfig(ConfigOption.PROJECT_ML_URL, url);
    }
    
    public List<ProjectVersion> getProjectVersions() {
        return projectVersions;
    }

    public List<ProjectVersion> getTaggedVersions() {
        return Tag.getTaggedVersions(this);
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
    	return getConfigValue(key.getName());
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
    	return getConfigValues(co.getName());
    }
    
    /** 
     * Get the values for a project configuration entry.
     * 
     * @param key The key whose value we want to retrieve
     */
    public List<String> getConfigValues (String key) {
    	ConfigurationOption co = ConfigurationOption.fromKey(key);
    	
    	if (co == null)
    		return Collections.emptyList();
    	
    	return co.getValues(this);
    }
    
    /**
	 * Set the value for a project configuration key. If the key does not exist
	 * in the configuration key table, it will be created with and empty
	 * description. The schema allows multiple values per key, so there is no
	 * need to encode multiple key values in a single configuration entry.
	 * 
	 * @param key The key to set the value for
	 * @param value The value to be set 
	 */
    public void setConfigValue (String key, String value) {
    	updateConfigValue(null, key, value, true);
    }
    
    
    /**
	 * Append a value to a project configuration key. If the key does not exist
	 * in the configuration key table, it will be created with and empty
	 * description. The schema allows multiple values per key, so there is no
	 * need to encode multiple key values in a single configuration entry.
	 * 
	 * @param key The key to set the value for
	 * @param value The value to be set 
	 */
    public void addConfigValue(String key, String value) {
    	updateConfigValue(null, key, value, false);
    }
    
    /**
	 * Append a value to a project configuration option. If the configuration
	 * option does not exist in the database, it will be created. The schema
	 * allows multiple values per key, so there is no need to encode multiple
	 * key values in a single configuration entry.
	 * 
	 * @param co The configuration option to store a value for
	 * @param value The value to set to the configuration option
	 */
	public void addConfig(ConfigOption co, String value) {
		updateConfigValue(co, null, value, false);
	}
    
    private void updateConfigValue (ConfigOption configOpt, String key, 
    		String value, boolean update) {
    	DBService dbs = AlitheiaCore.getInstance().getDBService();
    	ConfigurationOption co = null;
    	
    	if (configOpt == null) {
    		co = ConfigurationOption.fromKey(key);
    	
    		if (co == null) {
    			co = new ConfigurationOption(key, "");
    			dbs.addRecord(co);
    		}
    	} else {
    		co = ConfigurationOption.fromKey(configOpt.getName());
        	
    		if (co == null) {
    			co = new ConfigurationOption(configOpt.getName(), 
    					configOpt.getDesc());
    			dbs.addRecord(co);
    		}
    	}
    	
    	List<String> values = new ArrayList<String>();
    	values.add(value);
    	co.setValues(this, values, update);
    }

    //================================================================
    // Static table information accessors
    //================================================================
    

    /**
     * Convenience method to retrieve a stored project from the
     * database by name; this is different from the constructor
     * that takes a name parameter. This method actually searches
     * the database, whereas the constructor makes a new project
     * with the given name.
     * 
     * @param name Name of the project to search for
     * @return StoredProject object or null if not found
     */
    public static StoredProject getProjectByName(String name) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        Map<String,Object> parameterMap = new HashMap<String,Object>();
        parameterMap.put("name",name);
        List<StoredProject> prList = dbs.findObjectsByProperties(StoredProject.class, parameterMap);
        return (prList == null || prList.isEmpty()) ? null : prList.get(0);
    }

    /**
     * Count the total number of projects in the database.
     * 
     * @return number of stored projects in the database
     */
    public static int getProjectCount() {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        List<?> l = dbs.doHQL("SELECT COUNT(*) FROM StoredProject");
        if ((l == null) || (l.size() < 1)) {
            return 0;
        }
        Long i = (Long) l.get(0);
        return i.intValue();
    }

    /**
     * Returns the total number of versions for the project with the given Id.
     *
     * @param projectId - the project's identifier
     *
     * @return The total number of version for that project.
     */
    public long getVersionsCount() {
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        Map<String,Object> parameterMap = new HashMap<String,Object>();
        parameterMap.put("pid", this.getId());
        List<?> pvList = dbs.doHQL("select count(*)"
                + " from ProjectVersion pv"
                + " where pv.project.id=:pid",
                parameterMap);

        return (pvList == null || pvList.isEmpty()) ? 0 : (Long) pvList.get(0);
    }

    /**
     * Returns the total number of mails which belong to the project with the
     * given Id.
     *
     * @param projectId - the project's identifier
     *
     * @return The total number of mails associated with that project.
     */
    public long getMailsCount() {
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        Map<String,Object> parameterMap = new HashMap<String,Object>();
        parameterMap.put("pid", this.getId());
        List<?> res = dbs.doHQL("select count(*)"
                + " from MailMessage mm, MailingList ml"
                + " where ml.storedProject.id=:pid"
                + " and mm.list.id=ml.id",
                parameterMap);

        return (res == null || res.isEmpty()) ? 0 : (Long) res.get(0);
    }

    /**
     * Returns the total number of bugs which belong to the project with the
     * given Id.
     *
     * @return The total number of bugs associated with that project.
     */
    public long getBugsCount() {
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        Map<String,Object> parameterMap = new HashMap<String,Object>();
        parameterMap.put("pid", this.getId());
        List<?> res = dbs.doHQL("select count(*)"
                + " from Bug bg"
                + " where bg.project.id=:pid"
                + " and bg.status.status='" + Status.NEW + "'",
                parameterMap);

        return (res == null || res.isEmpty()) ? 0 : (Long) res.get(0);
    }
    
    /**
     * Check whether any metric has run on the given project.
     * @return
     */
    public boolean isEvaluated() {
    	DBService dbs = AlitheiaCore.getInstance().getDBService();
    	for (Metric m : Metric.getAllMetrics()) {
    		if (m.isEvaluated(this))
    			return true;
    	}
    	return false;
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

