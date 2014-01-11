package eu.sqooss.service.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Stores all standard project-wide configuration options that
 * the system knows about.  
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 *
 */
public enum ConfigOption {
    
	/**
     * The project's original BTS URL
     */
    PROJECT_BTS_SOURCE("eu.sqooss.project.bts.source", "The project's original BTS URL"),
    
    /**
     * The project's BTS type
     */
    PROJECT_BTS_TYPE("eu.sqooss.project.bts.type", "The project's BTS type"),
	
    /**
     * The project's BTS URL
     */
    PROJECT_BTS_URL("eu.sqooss.project.bts.url", "The project's local BTS mirror URL"),
    
    /**
     * The project's contact address
     */
    PROJECT_CONTACT("eu.sqooss.project.contact", "The project's contact address"),
    
    /**
     * The project's MailingList source URL
     */
    PROJECT_ML_SOURCE("eu.sqooss.project.ml.source", "The project's source mailing list URL"),
    
    /**
     * The project's MailingList source URL
     */
    PROJECT_ML_TYPE("eu.sqooss.project.ml.type", "The project's local mailing list type"),

    /**
     * The project's MailingList URL
     */
    PROJECT_ML_URL("eu.sqooss.project.ml.url", "The project's local mailing list URL"),
    
    /**
     * The project's name
     */
    PROJECT_NAME("eu.sqooss.project.name", "The project's name"),
    
    /**
     * The project's SCM type
     */
    PROJECT_SCM_TYPE("eu.sqooss.project.scm.type", "The project's SCM type"),

    /**
     * The project's SCM URL
     */
    PROJECT_SCM_URL("eu.sqooss.project.scm.url", "The project's local SCM URL"),
    
    /**
     * The project's SCM URL
     */
    PROJECT_SCM_SOURCE("eu.sqooss.project.scm.source", "The project's original SCM URL"),
    
    /**
     * The source code paths to process while executing the updater
     */
    PROJECT_SCM_PATHS_INCL("eu.sqooss.project.scm.path.incl", "The source code paths to process"),
    
    /**
     * The source code paths to process not to process
     */
    PROJECT_SCM_PATHS_EXCL("eu.sqooss.project.scm.path.excl", "The source code paths not to process"),
    
    /**
     * The project's website
     */
    PROJECT_WEBSITE("eu.sqooss.project.website", "The project's website");
    
    
    private final String propname;
    private final String desc;
    
    public String getName() {
        return propname;
    }

    public String getDesc() {
        return desc;
    }

    private ConfigOption(String name, String desc) {
        this.propname = name;
        this.desc = desc;
    }
    
    /**
     * Set an array of values for this configuration option for the specified
     * project.
     * 
     * @param sp The project to add the configuration value
     * @param value The value to set
     * @param overwrite If the key already has a value in the config database,
     *  the method determines whether to overwrite the value or append the
     *  provided value to the existing list of values.
     */
	public void setValues(DBService dbs, StoredProject sp, List<String> values,
			boolean overwrite) {
		String paramProject = "paramProject";
		String paramConfOpt = "paramConfOpt";
		
		StringBuilder query = new StringBuilder();
		query.append(" select spc ");
		query.append(" from StoredProjectConfig spc");
		query.append(" where spc.project =:").append(paramProject);
		query.append(" and spc.confOpt =:").append(paramConfOpt);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(paramProject, sp);
		params.put(paramConfOpt, this);

		@SuppressWarnings("unchecked")
		List<StoredProjectConfig> curValues =
				(List<StoredProjectConfig>) dbs.doHQL(query.toString(),params);
		
		assert curValues.size() <= 1 : "At most one StoredProjectConfig should exist for a project and option combination.";
		
		if (overwrite) {
			dbs.deleteRecords(curValues);
			curValues.clear();
		}
		
		StoredProjectConfig spc;
		if(curValues.isEmpty()) {
			spc = new StoredProjectConfig(this, new HashSet<String>(values), sp);
			dbs.addRecord(spc);
		} else {
			curValues.get(0).getValues().addAll(values);
		}
		
	}
	
	/**
	 * Get the configured values for a project.
	 * @param sp The project to retrieve the configuration values for
	 * @return A list of configuration values that correspond to the provided
	 * project
	 */
	public List<String> getValues(DBService dbs, StoredProject sp) {
		String paramProject = "paramProject";
		String paramConfOpt = "paramConfOpt";
		
		StringBuilder query = new StringBuilder();
		query.append(" select spc ");
		query.append(" from StoredProjectConfig spc ");
		query.append(" where spc.project =:").append(paramProject);
		query.append(" and spc.confOpt =:").append(paramConfOpt);
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(paramProject, sp);
		params.put(paramConfOpt, this);
		
		@SuppressWarnings("unchecked")
		List<StoredProjectConfig> spcs = (List<StoredProjectConfig>)dbs.doHQL(query.toString(), params);
		
		assert spcs.size() <= 1 : "At most one StoredProjectConfig should exist for a project and option combination.";
		
		if(spcs.size() == 0) {
			return null;
		} else {
			return new ArrayList<String>(spcs.get(0).getValues());
		}
	}
	
	public static ConfigOption fromKey(String key) {
		for(ConfigOption opt : ConfigOption.values()) {
			if (opt.getName().equals(key)) {
				return opt;
			}
		}
		
		return null;
	}
}

