/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - Organization for Free and Open Source Software,  
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
import java.util.Set;

import eu.sqooss.core.AlitheiaCore;

/**
 * An object that encapsulates a single configuration option.
 * Inlcudes methods to get the values for a specific project.
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 *
 */
public class ConfigurationOption extends DAObject {
	private String key;
	private String description;
	private Set<StoredProject> projects;
	
	
    public ConfigurationOption() {}
	
	public ConfigurationOption(String key, String description) {
		this.key = key;
		this.description = description;
	}

	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Set<StoredProject> getProjects() {
        return projects;
    }

    public void setProjects(Set<StoredProject> projects) {
        this.projects = projects;
    }
	
	/**
	 * 
	 * @param sp
	 * @param value
	 * @param overwrite If the key already has a value in the config database,
	 * the method determines whether to overwrite the value or append the 
	 * provided value to the existing list of values.
	 */
	public void setValues(StoredProject sp, List<String> values,
			boolean overwrite) {
		DBService dbs = AlitheiaCore.getInstance().getDBService();
		
		String paramProject = "paramProject";
		String paramConfOpt = "paramConfOpt";
		
		StringBuilder query = new StringBuilder();
		query.append(" select spc ");
		query.append(" from StoredProjectConfig spc,");
		query.append("      ConfigurationOption co ");
		query.append(" where spc.confOpt = co ");
		query.append(" and spc.project =:").append(paramProject);
		query.append(" and spc.confOpt =:").append(paramConfOpt);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(paramProject, sp);
		params.put(paramConfOpt, this);

		List<StoredProjectConfig> curValues = 
			(List<StoredProjectConfig>) dbs.doHQL(query.toString(),params);
		boolean found = false;
		if (overwrite) {
			dbs.deleteRecords(curValues);
			for (String newValue : values) {
				StoredProjectConfig newspc = new StoredProjectConfig(
						this, newValue, sp);
				dbs.addRecord(newspc);
			}
		} else { //Merge values
			for (String newValue : values) {
				for (StoredProjectConfig conf : curValues) {
					if (conf.getValue().equals(newValue)) {
						found = true;
					}
				}
				if (!found) {
					StoredProjectConfig newspc = new StoredProjectConfig(
							this, newValue, sp);
					dbs.addRecord(newspc);
				}
			}
		}
	}
	
	/**
	 * Get the configured values for a project.
	 * @param sp The project to retrieve the configuration values for
	 * @return A list of configuration values that correspond to the provided
	 * project
	 */
	public List<String> getValues(StoredProject sp) {
		DBService dbs = AlitheiaCore.getInstance().getDBService();
		
		String paramProject = "paramProject";
		String paramConfOpt = "paramConfOpt";
		
		StringBuilder query = new StringBuilder();
		query.append(" select spc.value ");
		query.append(" from StoredProjectConfig spc, ConfigurationOption co ");
		query.append(" where spc.confOpt = co ");
		query.append(" and spc.project =:").append(paramProject);
		query.append(" and spc.confOpt =:").append(paramConfOpt);
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(paramProject, sp);
		params.put(paramConfOpt, this);
		
		return (List<String>) dbs.doHQL(query.toString(), params);
	}
	
	public static ConfigurationOption fromKey(String key) {
		DBService dbs = AlitheiaCore.getInstance().getDBService();
		
		String paramKey = "key";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(paramKey, key);
		
		List<ConfigurationOption> opts =  dbs.findObjectsByProperties(ConfigurationOption.class, params);
		
		if (opts.isEmpty())
			return null;
		
		return opts.get(0);
	}
	
	public String toString() {
		return key + " - " + description; 
	}
}
