package eu.sqooss.service.db.util;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Directory;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileState;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.Tag;

public class ProjectVersionUtils {
	private DBService dbs;
	private ProjectFileUtils pfu;
	
	public ProjectVersionUtils(DBService db, ProjectFileUtils pfu) {
		this.dbs = db;
		this.pfu = pfu;
	}

	/**
	 * Allow moving backward in version history by finding the most-recent
	 * version of this project before the current one, or null if there
	 * is no such version.
	 * 
	 * @return Previous version, or null
	 */
	public ProjectVersion getPreviousVersion(ProjectVersion pv) {
	    
	    String paramOrder = "versionOrder"; 
	    String paramProject = "projectId";
	    
	    String query = "select pv from ProjectVersion pv where " +
			" pv.project.id =:" + paramProject +
			" and pv.sequence < :" + paramOrder + 
			" order by pv.sequence desc";
	    
	    Map<String,Object> parameters = new HashMap<>();
	    parameters.put(paramOrder, pv.getSequence());
	    parameters.put(paramProject, pv.getProject().getId());
	
	    List<?> projectVersions = dbs.doHQL(query, parameters, 1);
	    
	    if(projectVersions == null || projectVersions.size() == 0) {
	        return null;
	    } else {
	        return (ProjectVersion) projectVersions.get(0);
	    }
	}

	/**
	 * Allow moving forward in version history by finding the earliest
	 * version of this project later than the current one, or null if there
	 * is no such version.
	 * 
	 * @return Next version, or null
	 */
	public ProjectVersion getNextVersion(ProjectVersion pv) {
	    
	    String paramTS = "versionsequence"; 
	    String paramProject = "projectId";
	    
	    String query = "select pv from ProjectVersion pv where " +
	        " pv.project.id =:" + paramProject +
	        " and pv.sequence > :" + paramTS + 
	        " order by pv.sequence asc";
	    
	    Map<String,Object> parameters = new HashMap<>();
	    parameters.put(paramTS, pv.getSequence());
	    parameters.put(paramProject, pv.getProject().getId());
	
	    List<?> projectVersions = dbs.doHQL(query, parameters, 1);
	    
	    if(projectVersions == null || projectVersions.size() == 0) {
	        return null;
	    } else {
	        return (ProjectVersion) projectVersions.get(0);
	    }
	}

	/**
	 * Look up a project version based on the SCM system provided
	 * revision id. This does a database lookup and 
	 * returns the ProjectVersion recorded for that SCM revision,
	 * or null if there is no such revision (for instance because
	 * the updater has not added it yet or the revision number is
	 * invalid in some way). This is a lookup, not a creation, of
	 * revisions.
	 * 
	 * @param project Project to look up
	 * @return ProjectVersion object corresponding to the revision,
	 *         or null if there is none.
	 */
	public ProjectVersion getVersionByRevision(StoredProject project, String revisionId) {
	
	    Map<String,Object> parameters = new HashMap<>();
	    parameters.put("project", project);
	    parameters.put("revisionId", revisionId);
	
	    List<ProjectVersion> versions = dbs.findObjectsByProperties(ProjectVersion.class, parameters);
	    if (versions == null || versions.size() == 0) {
	        return null;
	    } else {
	        return versions.get(0);
	    }
	}

	/**
	 * Look up a project version based on the given time stamp. This does a
	 * database lookup and returns the <code>ProjectVersion</code> DAO, which
	 * carries the same time stamp or <code>null</code> if a matching version
	 * can not be found (for instance because the updater has not added it yet
	 * or a version with such a time stamp doesn't exist in this project).
	 * Depending on the underlying SCM timestamp keeping accuracy and commit
	 * frequency, more than one revisions can match the given timestamp;
	 * in that case only the first match is returned  
	 * <br/> 
	 * This is a lookup, not a creation, of revisions.
	 * 
	 * @param project <code>Project</code> DAO to look up
	 * @param timestamp Version time stamp to look up for this project
	 * @return ProjectVersion object carrying that time stamp,
	 *         or <code>null</code> if there is none.
	 */
	public ProjectVersion getVersionByTimestamp(
	        StoredProject project, long timestamp) {
	
	    Map<String,Object> parameters = new HashMap<>();
	    parameters.put("project", project);
	    parameters.put("timestamp", timestamp);
	
	    List<ProjectVersion> versions = dbs.findObjectsByProperties(
	            ProjectVersion.class, parameters);
	    if (versions == null || versions.size() == 0) {
	        return null;
	    } else {
	        return versions.get(0);
	    }
	}

	/**
	 * Convenience method to find the oldest revision stored in the Alitheia
	 * database.
	 * 
	 * @param sp Project to lookup
	 * @return The oldest recorded project revision
	 */
	public ProjectVersion getFirstProjectVersion(StoredProject sp) {
	
	    Map<String,Object> parameterMap = new HashMap<>();
	    parameterMap.put("sp", sp);
	    List<?> pvList = dbs.doHQL("from ProjectVersion pv where pv.project=:sp"
	            + " and pv.sequence = 1",
	            parameterMap);
	
	    return (pvList == null || pvList.isEmpty()) ? null : (ProjectVersion) pvList.get(0);
	}

	/**
	 * Convenience method to find the latest project version for
	 * a given project.
	 * 
	 * @return The <code>ProjectVersion</code> DAO for the latest version,
	 *   or <code>null</code> if not found
	 */
	public ProjectVersion getLastProjectVersion(StoredProject sp) {
	
	    Map<String,Object> parameterMap = new HashMap<>();
	    parameterMap.put("sp", sp);
	    List<?> pvList = dbs.doHQL("from ProjectVersion pv where pv.project=:sp"
	            + " and pv.sequence = (select max(pv2.sequence) from "
	            + " ProjectVersion pv2 where pv2.project=:sp)",
	            parameterMap);
	
	    return (pvList == null || pvList.isEmpty()) ? null : (ProjectVersion) pvList.get(0);
	}

	/**
	 * Consider using a utility method from {@link ProjectVersion} instead. 
	 */
	public List<ProjectFile> getVersionFiles(ProjectVersion pv, Directory d, EnumSet<ProjectVersion.MASK> mask) {
	
	    String paramDirectory = "paramDirectory";
	    String paramIsDirectory = "is_directory";
	    String paramVersionId = "paramVersionId";
	    String paramProjectId = "paramProjectId";
	    String paramState = "paramStatus";
	
	    Map<String,Object> params = new HashMap<>();
	    StringBuffer q = new StringBuffer("select distinct pf ");
	    
	    if (pv.getSequence() == this.getLastProjectVersion(pv.getProject()).getSequence()) {
	        q.append(" from ProjectFile pf, ProjectVersion pv");
	        q.append(" where pv.id = :").append(paramVersionId);
	        q.append(" and pf.validUntil is null ");
	    } else {
	        q.append(" from ProjectVersion pv, ProjectVersion pv2,");
	        q.append(" ProjectVersion pv3, ProjectFile pf ");
	        q.append(" where pv.project.id = :").append(paramProjectId);
	        q.append(" and pv.id = :").append(paramVersionId);
	        q.append(" and pv2.project.id = :").append(paramProjectId);
	        q.append(" and pv3.project.id = :").append(paramProjectId);
	        q.append(" and ((pf.validFrom.id = pv2.id and pf.validUntil.id = pv3.id)");
	        q.append("     or (pf.validFrom.id = pv2.id and pf.validUntil.id is null))");
	        q.append(" and pv2.sequence <= pv.sequence");
	        q.append(" and pv3.sequence >= pv.sequence");
	        
	        params.put(paramProjectId, pv.getProject().getId());
	    }
	    
	    q.append(" and pf.state <> :").append(paramState);
	    
	    if (d != null) {
	    	q.append(" and pf.dir = :").append(paramDirectory);
	    }
	        
	    if (!mask.equals(ProjectVersion.MASK.ALL)) {
	    	q.append(" and pf.isDirectory = :").append(paramIsDirectory);
	    }
	
	 	params.put(paramState, pfu.deleted());
	 	params.put(paramVersionId, pv.getId());
	    
	 	if (d != null) {
	 		params.put(paramDirectory, d);
	 	}
	 	
	    if (!mask.equals(ProjectVersion.MASK.ALL)) {
	        Boolean isDirectory = ((mask.equals(ProjectVersion.MASK.DIRECTORIES))?true:false);
	        params.put(paramIsDirectory, isDirectory);
	    }
	    
	    @SuppressWarnings("unchecked")
		List<ProjectFile> projectFiles = (List<ProjectFile>) dbs.doHQL(q.toString(), params);
	
	    if (projectFiles == null) 
	        return Collections.emptyList();
	
	    return projectFiles;
	
	}

	/**
	 * For a given metric and project, return the latest version of that
	 * project that was actually measured.  If no measurements have been made, 
	 * it returns null. For the returned revision which is not null, the
	 * revision is greater than 0, there is a measurement in the database.
	 * @param p Project to look for
	 * @param m Metric to look for
	 * 
	 * @return Last version measured, or revision 0.
	 */
	public ProjectVersion getLastMeasuredVersion(StoredProject p, Metric m) {
	    String query = "select pv from ProjectVersionMeasurement pvm, ProjectVersion pv" +
	       " where pvm.projectVersion = pv" +
	       " and pvm.metric = :metric and pv.project = :project" +
	       " order by pv.sequence desc";
	
	    HashMap<String, Object> params = new HashMap<>(4);
	    params.put("metric", m);
	    params.put("project", p);
	    @SuppressWarnings("unchecked")
		List<ProjectVersion> pv = (List<ProjectVersion>) 
	        dbs.doHQL( query, params, 1);
	    
	    if (pv.isEmpty())
	        return null;
	    
	    return pv.get(0);
	}

	/**
	 * Gets the number of files in the given version which are in the
	 * selected file state.
	 *
	 * @param state the file state
	 * 
	 * @return The number of files in that version and that state.
	 */
	public long getFilesCount(ProjectVersion pv, ProjectFileState state) {
	    // Construct the field names
	    String parVersionId     = "project_version_id"; 
	    String parFileStatus    = "file_status";
	    // Construct the query string
	    String query = "select count(*) from ProjectFile pf"
	        + " where pf.projectVersion=:" + parVersionId
	        + " and pf.status=:" + parFileStatus;
	    // Execute the query
	    Map<String,Object> parameters = new HashMap<>();
	    parameters.put(parVersionId, pv);
	    parameters.put(parFileStatus, state);
	    List<?> queryResult = dbs.doHQL(query, parameters);
	    // Return the query's result (if found)
	    if(queryResult != null && queryResult.size() > 0)
	        return (Long) queryResult.get(0);
	    // Default result
	    return 0;
	}

	/**
	 * Get the number of files (excluding directories and deleted files) in this version. 
	 * @return The number of live files in a specific project version.
	 */
	public long getLiveFilesCount(ProjectVersion pv) {
	
		String paramVersionId = "paramVersion";
	    String paramIsDirectory = "paramIsDirectory";
	    String paramProjectId = "paramProject";
	    String paramState = "paramState";
	    Map<String, Object> params = new HashMap<>();
	
	    StringBuffer q = new StringBuffer("select count(pf) ");
	    
	    if (pv.getSequence() == getLastProjectVersion(pv.getProject()).getSequence()) {
	        q.append(" from ProjectFile pf, ProjectVersion pv");
	        q.append(" where pv.id = :").append(paramVersionId);
	        q.append(" and pf.validUntil is null ");
	    } else {
	        q.append(" from ProjectVersion pv, ProjectVersion pv2,");
	        q.append(" ProjectVersion pv3, ProjectFile pf ");
	        q.append(" where pv.project.id = :").append(paramProjectId);
	        q.append(" and pv.id = :").append(paramVersionId);
	        q.append(" and pv2.project.id = :").append(paramProjectId);
	        q.append(" and pv3.project.id = :").append(paramProjectId);
	        q.append(" and ((pf.validFrom.id = pv2.id and pf.validUntil.id = pv3.id)");
	        q.append("     or (pf.validFrom.id = pv2.id and pf.validUntil.id is null))");
	        q.append(" and pv2.sequence <= pv.sequence");
	        q.append(" and pv3.sequence >= pv.sequence");
	        
	        params.put(paramProjectId, pv.getProject().getId());
	    }
	    q.append(" and pf.isDirectory = :").append(paramIsDirectory);
	    q.append(" and pf.state <> :").append(paramState);
	
	    
	    params.put(paramVersionId, pv.getId());
	    params.put(paramIsDirectory, Boolean.FALSE);
	    params.put(paramState, pfu.deleted());
	    
	    return (Long) dbs.doHQL(q.toString(), params).get(0);
	}

	/**
	 * Return true if this version's actions generated a tag.
	 */
	public boolean isTag(ProjectVersion pv) {
		Map<String, Object> props = new HashMap<>();
		props.put("projectVersion", pv);
		
		List<Tag> tags = dbs.findObjectsByProperties(Tag.class, props);
		
		if (tags.isEmpty())
			return false;
		
		return true;
	}

}
