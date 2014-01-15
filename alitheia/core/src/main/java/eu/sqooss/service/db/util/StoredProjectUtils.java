package eu.sqooss.service.db.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.service.db.BugStatus.Status;
import eu.sqooss.service.db.ConfigOption;
import eu.sqooss.service.db.ConfigurationOption;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.StoredProjectConfig;

public class StoredProjectUtils {
	private DBService dbs;
	private ConfigurationOptionUtils cou;
	
	public StoredProjectUtils(DBService db, ConfigurationOptionUtils cou) {
		this.dbs = db;
		this.cou = cou;
	}

	public ProjectVersion getProjectVersionForNamedTag(StoredProject sp,
	        String tagName) {
	
	    String paramTagName = "tagname";
	    String paramProject = "project_id";
	
	    String query = "select pv " 
	            + " from ProjectVersion pv, Tag t "
	            + " where t.projectVersion = pv " 
	            + " and t.name = :" + paramTagName 
	            + " and pv.project =:" + paramProject;
	
	    Map<String, Object> parameters = new HashMap<>();
	    parameters.put(paramTagName, tagName);
	    parameters.put(paramProject, sp);
	
	    List<?> projectVersions = dbs.doHQL(query, parameters, 1);
	
	    if (projectVersions == null || projectVersions.size() == 0) {
	        return null;
	    } else {
	        return (ProjectVersion) projectVersions.get(0);
	    }
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
	public void setConfigValue(StoredProject sp, String key, String value) {
		updateConfigValue(sp, null, key, value, true);
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
	public void addConfigValue(StoredProject sp, String key, String value) {
		updateConfigValue(sp, null, key, value, false);
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
	public void addConfig(StoredProject sp, ConfigOption co, String value) {
		updateConfigValue(sp, co, null, value, false);
	}

	private void updateConfigValue (StoredProject sp, ConfigOption configOpt, String key, 
			String value, boolean update) {
		ConfigurationOption co = null;
		
		if (configOpt == null) {
			co = cou.getConfigurationOptionByKey(key);
		
			if (co == null) {
				co = new ConfigurationOption(key, "");
				dbs.addRecord(co);
			}
		} else {
			co = cou.getConfigurationOptionByKey(configOpt.getKey());
	    	
			if (co == null) {
				co = new ConfigurationOption(configOpt.getKey(), 
						configOpt.getDesc());
				dbs.addRecord(co);
			}
		}
		
		List<String> values = new ArrayList<>();
		values.add(value);
		this.setValues(sp, co, values, update);
	}

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
	public StoredProject getProjectByName(String name) {
	
	    Map<String,Object> parameterMap = new HashMap<>();
	    parameterMap.put("name",name);
	    List<StoredProject> prList = dbs.findObjectsByProperties(StoredProject.class, parameterMap);
	    return (prList == null || prList.isEmpty()) ? null : prList.get(0);
	}

	/**
	 * Count the total number of projects in the database.
	 * 
	 * @return number of stored projects in the database
	 */
	public int getProjectCount() {
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
	public long getVersionsCount(StoredProject sp) {
	
	    Map<String,Object> parameterMap = new HashMap<>();
	    parameterMap.put("pid", sp.getId());
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
	public long getMailsCount(StoredProject sp) {
	
	    Map<String,Object> parameterMap = new HashMap<>();
	    parameterMap.put("pid", sp.getId());
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
	public long getBugsCount(StoredProject sp) {
	
	    Map<String,Object> parameterMap = new HashMap<>();
	    parameterMap.put("pid", sp.getId());
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
	public boolean isEvaluated(StoredProject sp) {
		for (Metric m : this.getAllMetrics()) {
			if (this.isEvaluated(sp, m))
				return true;
		}
		return false;
	}
	
	/**
	 * Get a list of all installed metrics.
	 * 
	 * @return A list of all installed metrics, which might be empty if no
	 *         metric is installed.
	 */
	@SuppressWarnings("unchecked")
	public List<Metric> getAllMetrics() {
		return (List<Metric>) dbs.doHQL("from Metric");
	}

	public List<StoredProjectConfig> fromProject(StoredProject sp) {
		
		Map<String, Object> params = new HashMap<>();
		params.put("project", sp);
		
		return dbs.findObjectsByProperties(StoredProjectConfig.class, params);
	}

	@SuppressWarnings("unchecked")
	public List<ProjectVersion> getTaggedVersions(StoredProject sp) {
	
	    String paramProject = "project_id";
	
	    String query = "select pv " 
	            + " from ProjectVersion pv, Tag t "
	            + " where t.projectVersion = pv " 
	            + " and pv.project =:" + paramProject;
	
	    Map<String, Object> parameters = new HashMap<>();
	    parameters.put(paramProject, sp);
	
	    return (List<ProjectVersion>) dbs.doHQL(query, parameters);
	
	}

	/**
	 * Set an array of values for the given configuration option for the specified
	 * project.
	 * 
	 * @param sp The project to add the configuration value
	 * @param overwrite If the key already has a value in the config database,
	 *  the method determines whether to overwrite the value or append the
	 *  provided value to the existing list of values.
	 * @param co the configuration option
	 * @param value The value to set
	 */
	public void setValues(StoredProject sp, ConfigurationOption co, List<String> values,
			boolean overwrite) {
		
		String paramProject = "paramProject";
		String paramConfOpt = "paramConfOpt";
		
		StringBuilder query = new StringBuilder();
		query.append(" select spc ");
		query.append(" from StoredProjectConfig spc,");
		query.append("      ConfigurationOption co ");
		query.append(" where spc.confOpt = co ");
		query.append(" and spc.project =:").append(paramProject);
		query.append(" and spc.confOpt =:").append(paramConfOpt);
	
		Map<String, Object> params = new HashMap<>();
		params.put(paramProject, sp);
		params.put(paramConfOpt, co);
	
		@SuppressWarnings("unchecked")
		List<StoredProjectConfig> curValues = 
			(List<StoredProjectConfig>) dbs.doHQL(query.toString(),params);
		boolean found = false;
		if (overwrite) {
			dbs.deleteRecords(curValues);
			for (String newValue : values) {
				StoredProjectConfig newspc = new StoredProjectConfig(
						co, newValue, sp);
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
							co, newValue, sp);
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
	@SuppressWarnings("unchecked")
	public List<String> getValues(StoredProject sp, ConfigurationOption co) {
		
		String paramProject = "paramProject";
		String paramConfOpt = "paramConfOpt";
		
		StringBuilder query = new StringBuilder();
		query.append(" select spc.value ");
		query.append(" from StoredProjectConfig spc, ConfigurationOption co ");
		query.append(" where spc.confOpt = co ");
		query.append(" and spc.project =:").append(paramProject);
		query.append(" and spc.confOpt =:").append(paramConfOpt);
		
		Map<String, Object> params = new HashMap<>();
		params.put(paramProject, sp);
		params.put(paramConfOpt, co);
		
		return (List<String>) dbs.doHQL(query.toString(), params);
	}

	/**
	 * Check whether the metric was ever run on the provided project.
	 * @param m 
	 */
	public boolean isEvaluated (StoredProject p, Metric m) {
		StringBuffer query = new StringBuffer();
	
		switch (m.getMetricType().getEnumType()) {
		case PROJECT_VERSION:
			query.append("select pvm from ProjectVersionMeasurement pvm ")
				 .append("where pvm.metric=:metric and pvm.projectVersion.project=:project");
			break;
		case SOURCE_FILE:
		case SOURCE_DIRECTORY:
			query.append("select pfm from ProjectFileMeasurement pfm ")
				 .append("where pfm.metric=:metric ")
				 .append("and pfm.projectFile.projectVersion.project=:project");
			break;
		case MAILTHREAD:
			query.append("select mltm from MailingListThreadMeasurement mltm ")
				 .append("where mltm.metric=:metric ")
				 .append("and mltm.thread.list.storedProject=:project");
			break;
		case MAILMESSAGE:
			query.append("select mmm from MailMessageMeasurement mmm ")
				.append("where mmm.metric=:metric")
				.append("and mmm.mail.list.storedProject=:project");
			break;
		case ENCAPSUNIT:
		    query.append("select eum from EncapsulationUnitMeasurement eum ")
	            .append("where eum=:metric ")
	            .append("and eum.encapsulationUnit.file.projectVersion.project=:project");
	        break;
		case EXECUNIT:
		    query.append("select eum from EncapsulationUnitMeasurement eum ")
	            .append("where eum=:metric ")
	            .append("and eum.encapsulationUnit.file.projectVersion.project=:project");
	        break;
		case NAMESPACE:
		    query.append("select nm from NameSpaceMeasurement nm ")
	            .append("where nm=:metric ")
	            .append("and nm.namespace.changeVersion.project=:project");
	        break;
		case BUG:
		case MAILING_LIST:
		case DEVELOPER:
			return false; //No DAO result types for those types yet
		case PROJECT:
			// TODO find out if no DAO result types for this type either
			break;
		default:
			break;
		}
		
		Map<String, Object> params = new HashMap<>();
		params.put("project", p);
		params.put("metric", m);
		
		if (dbs.doHQL(query.toString(), params, 1).size() >= 1)
			return true;
		
		return false;
	}

}
