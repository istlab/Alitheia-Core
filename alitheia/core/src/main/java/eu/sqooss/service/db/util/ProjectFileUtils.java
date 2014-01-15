package eu.sqooss.service.db.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Directory;
import eu.sqooss.service.db.ExecutionUnit;
import eu.sqooss.service.db.FileState;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileState;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.tds.PathChangeType;
import eu.sqooss.service.util.FileUtils;

public class ProjectFileUtils {
	private DBService dbs;
	
	public ProjectFileUtils(DBService db) {
		this.dbs = db;
	}
	
	/**
	 * If this <code>ProjectFile</code> represents a folder, then this method
	 * will return the <code>Directory<code> DAO that match the folder's name.
	 * 
	 * @return The corresponding <code>Directory</code> DAO
	 */
	public Directory toDirectory(ProjectFile pf) {
	    if ((pf.getIsDirectory()) && (pf.getFileName() != null)) {
	        Map<String, Object> props = new HashMap<>();
	        props.put("path", pf.getFileName());
	        List<Directory> matches =
	            dbs.findObjectsByProperties(Directory.class, props);
	        if ((matches != null) && (matches.size() > 0))
	            return matches.get(0);
	    }
	    return null;
	}

	/**
	 * Get the previous entry for the provided ProjectFile
	 * @param pf
	 * @return The previous file revision, or null if the file is not found
	 * or if the file was added in the provided revision
	 */
	public ProjectFile getPreviousFileVersion(ProjectFile pf) {
	
	    //No need to query if a file was just added
	    if (pf.isAdded()) {
	        return null;
	    }
	    
	    String query = null;
	    
	    if (pf.getCopyFrom() == null)
	        query = "select pf from ProjectVersion pv, ProjectFile pf where pf.projectVersion = pv.id and pv.project.id = :paramProject and pv.sequence < :paramsequence and  pf.name = :paramFile and pf.dir.id = :paramDir order by pv.sequence desc";
	    else
	        query = "select pf from ProjectVersion pv, ProjectFile pf where pf.projectVersion = pv.id and pv.project.id = :paramProject and pv.sequence < :paramsequence and ((pf.name = :paramFile and pf.dir.id = :paramDir) or ( pf.name = :paramCopyFromName and pf.dir.id = :paramCopyFromDir)) order by pv.sequence desc";
	    
	    Map<String,Object> parameters = new HashMap<>();
	    parameters.put("paramFile", pf.getName());
	    parameters.put("paramDir", pf.getDir().getId());
	    parameters.put("paramProject", pf.getProjectVersion().getProject().getId());
	    parameters.put("paramsequence", pf.getProjectVersion().getSequence());
	    
	    if (pf.getCopyFrom() != null) {
	        parameters.put("paramCopyFromName", pf.getCopyFrom().getName());
	        parameters.put("paramCopyFromDir", pf.getCopyFrom().getDir().getId());
	    }
	    List<?> projectFiles = dbs.doHQL(query, parameters, 1);
	
	    if (projectFiles.size() == 0) {
	        dbs.logger().warn("No previous versions for " + pf +
	                "\nQuery: " + query + ", params:" + parameters);
	        return null;
	    } else {
	        return (ProjectFile) projectFiles.get(0);
	    }
	}

	/**
	 * Returns the project version DAO where this file was deleted.
	 * 
	 * For a project files in a deleted state, this method will return the
	 * project version DAO of the same file.
	 *
	 * @param pf the project's file
	 *
	 * @return The project version's number where this file was deleted,
	 *   or <code>null</code> if this file still exist.
	 */
	public ProjectVersion getDeletionVersion(ProjectFile pf) {
	
	    // Skip files which are in a "DELETED" state
	    if (pf.isDeleted()) {
	        return pf.getProjectVersion();
	    }
	
	    String paramName = "paramName"; 
	    String paramDir = "paramDir"; 
	    String paramProject = "paramProject";
	    String paramDirectory = "paramDirectory";
	    String paramOrder = "paramsequence";
	    String paramStatusAdded = "paramStatusAdded";
	    String paramStatusDeleted = "paramStatusDeleted";
	    
	    /* The query needs to cater for a file being deleted
	     * and re-added in the same directory so it only 
	     * considers the latest incarnation of the file
	     */
	    String query = "select pv " +
	        " from ProjectFile pf, ProjectVersion pv " +
	        " where pf.projectVersion = pv " +
	        " and pf.state= :" + paramStatusDeleted +
	        " and pf.name = :" + paramName +
	        " and pf.dir = :" + paramDir + 
	        " and pf.isDirectory = :" + paramDirectory +
	        " and pv.project = :" + paramProject +
	        " and pv.sequence > " +
	        "           (select max(pv1.sequence) " +
	        "           from ProjectVersion pv1, ProjectFile pf1" +
	        "           where pf1.projectVersion = pv1" +
	        "           and pf1.state = :" + paramStatusAdded +
	        "           and pf1.name = :" + paramName +
	        "           and pf1.dir = :" + paramDir +
	        "           and pf1.isDirectory = :" + paramDirectory +
	        "           and pv1.project = :" + paramProject +
	        "           and pv1.sequence < :" + paramOrder + 
	        "           group by pv1) ";
	
	    HashMap<String, Object> params = new HashMap<>();
	    params.put(paramStatusDeleted, this.deleted());
	    params.put(paramStatusAdded, this.added());
	    params.put(paramName, pf.getName());
	    params.put(paramDir, pf.getDir());
	    params.put(paramDirectory, pf.getIsDirectory());
	    params.put(paramProject, pf.getProjectVersion().getProject());
	    params.put(paramOrder, pf.getProjectVersion().getSequence());
	
	    @SuppressWarnings("unchecked")
		List<ProjectVersion> pvs = (List<ProjectVersion>) dbs.doHQL(query, params);
	                   
	    if (pvs.size() <= 0)
	        return null;
	    else 
	        return pvs.get(0);
	}

	/**
	 * Gets the enclosing directory of the given project file.
	 *
	 * @param pf the project file
	 *
	 * @return The <code>ProjectFile</code> DAO of the enclosing directory,
	 *   or <code>null</code> if the given file is located in the project's
	 *   root folder (<i>or the given file is the root folder</i> ).
	 */
	public ProjectFile getEnclosingDirectory(ProjectFile pf) {
	    
	    String paramName = "paramName"; 
	    String paramDir = "paramDir";
	    String paramIsDir = "paramIsDir"; 
	    String paramProject = "paramProject";
	    String paramSequence = "paramSequence";
	    
	    String query = "select pf " +
	        " from ProjectFile pf, ProjectVersion pv " +
	        " where pf.projectVersion = pv " +
	        " and pf.name = :" + paramName +
	        " and pf.dir = :" + paramDir + 
	        " and pf.isDirectory = :" + paramIsDir + 
	        " and pv.project = :" + paramProject +
	        " and pv.sequence <= :" + paramSequence +
	        " order by pv.sequence desc";
	    
	    HashMap<String, Object> params = new HashMap<>();
	    
	    params.put(paramName, FileUtils.basename(pf.getDir().getPath()));
	    params.put(paramDir, new DirectoryUtils(this.dbs).getDirectoryByPath(FileUtils.dirname(pf.getDir().getPath()), false));
	    params.put(paramProject, pf.getProjectVersion().getProject());
	    params.put(paramIsDir, true);
	    params.put(paramSequence, pf.getProjectVersion().getSequence());
	    
	    @SuppressWarnings("unchecked")
		List<ProjectFile> pfs = (List<ProjectFile>) dbs.doHQL(query, params, 1);
	    
	    if (pfs.size() <= 0)
	        return null;
	    else 
	        return pfs.get(0);
	}

	/**
	 * Constructs a hash map of all project version time stamps where this
	 * particular file was modified, and the file's DAO Id in these versions.
	 * The project version time stamp is used as a hash key, while the project
	 * file Id in that version as a hash value.
	 * 
	 * @param pf the project file DAO
	 * 
	 * @return the modifications hash map
	 */
	@SuppressWarnings("unchecked")
	public List<ProjectFile> getFileModifications(ProjectFile pf) {
	    
	    String paramFile = "paramFile";
	    String paramDir = "paramDir";
	    String paramProject = "paramProject";
	
	    String query = "select pf"
	        + " from ProjectVersion pv, ProjectFile pf"
	        + " where pf.projectVersion = pv.id "
	        + " and pf.name = :" + paramFile
	        + " and pf.dir = :" + paramDir
	        + " and pv.project = :" + paramProject
	        + " order by pv.sequence asc";
	    Map<String, Object> parameters = new HashMap<>();
	    parameters.put(paramFile, pf.getName());
	    parameters.put(paramDir, pf.getDir());
	    parameters.put(paramProject, pf.getProjectVersion().getProject());
	
	    return (List<ProjectFile>) dbs.doHQL(query, parameters);
	}

	/**
	 * Return the latest file version matching the provided arguments
	 * 
	 * @param projectId The project to search for
	 * @param name The name of the file
	 * @param path The directory path this file resides in
	 * @param version The version number
	 * 
	 * @return A list of ProjectFile objects matching the search arguments
	 *  which can be empty if no matching files where found
	 */
	public ProjectFile findFile(Long projectId, String name,
	        String path, String version) {
		return this.findFile(projectId, name, path, version, false);
	}

	/**
	 * Return the latest file version matching the provided arguments
	 * 
	 * @param projectId The project to search for
	 * @param name The name of the file
	 * @param path The directory path this file resides in
	 * @param version The version number
	 * 
	 * @return A list of ProjectFile objects matching the search arguments
	 *  which can be empty if no matching files where found
	 */
	@SuppressWarnings("unchecked")
	public ProjectFile findFile(Long projectId, String name,
	        String path, String version, boolean inclDeleted) {
	    List<ProjectFile> pfs = new ArrayList<>();
	    Map<String, Object> parameters = new HashMap<>();
	    
	    if (projectId == null || name == null) {
	        return null;
	    }
	    
	    String paramProjectId = "paramProjectId";
	    String paramName = "paramName";
	    String paramVersion = "paramVersion";
	    String paramPath = "paramPath";
	    String paramStatus = "paramStatus";
	    StringBuilder query = new StringBuilder();
	    
	    query.append("select pf ");
	    query.append("from ProjectFile pf, ProjectVersion pv, StoredProject sp, Directory d ");
	    query.append(" where pf.projectVersion = pv.id ");
	    if (!inclDeleted)
	    	query.append(" and pf.state <> :").append(paramStatus); 
	    query.append(" and pv.project.id = :").append(paramProjectId); 
	    query.append(" and pf.name = :").append(paramName);
	    query.append(" and pf.dir.id = d.id "); 
	    query.append(" and d.path = :").append(paramPath);
	    query.append(" and pv.sequence <= ( ");
	    query.append("    select pv1.sequence ");
	    query.append("    from ProjectVersion pv1 ");
	    query.append("    where pv1.revisionId = :").append(paramVersion);
	    query.append("    and pv1.project.id = :").append(paramProjectId).append(")");
	    query.append(" order by pv.sequence desc");
	
	    if (!inclDeleted)
	    	parameters.put(paramStatus, this.deleted());        
	    parameters.put(paramProjectId, projectId);
	    parameters.put(paramName, name);
	    parameters.put(paramPath, path);
	    parameters.put(paramVersion, version);
	    
	    pfs = (List<ProjectFile>) dbs.doHQL(query.toString(), parameters, 1);
	    
	    if (pfs.isEmpty()) 
	        return null;
	    
	    return pfs.get(0);
	}

	@SuppressWarnings("unchecked")
	public List<ExecutionUnit> getChangedExecutionUnits(ProjectFile pf) {
	    Map<String, Object> params = new HashMap<>();
	    
	    params.put("file", pf);
	    return (List<ExecutionUnit>)dbs.doHQL("from ExecutionUnit eu where eu.file = :file and eu.changed = true", params);
	}

	public ProjectFileState fromStatus(FileState status) {
	    if (!dbs.isDBSessionActive())
	        return null;
	
	    Map<String, Object> params = new HashMap<>();
	    params.put("status", status);
	    List<ProjectFileState> pfs = dbs.findObjectsByProperties(
	            ProjectFileState.class, params);
	
	    if (!pfs.isEmpty()) {
	        return pfs.get(0);
	    }
	
	    ProjectFileState state = new ProjectFileState();
	    state.setFileStatus(status);
	
	    dbs.addRecord(state);
	
	    return fromStatus(status);
	}

	public ProjectFileState fromPathChangeType(PathChangeType pct) {
	    switch (pct) {
	    case ADDED:
	        return this.fromStatus(FileState.ADDED);
	    case MODIFIED:
	        return this.fromStatus(FileState.MODIFIED);
	    case DELETED:
	        return this.fromStatus(FileState.DELETED);    
	    case REPLACED:
	        return this.fromStatus(FileState.REPLACED);
	    default:
	        return null;
	    }
	}

	public ProjectFileState replaced() {
	    return this.fromStatus(FileState.REPLACED);
	}

	public ProjectFileState deleted() {
	    return this.fromStatus(FileState.DELETED);
	}

	public ProjectFileState modified() {
	    return this.fromStatus(FileState.MODIFIED);
	}

	public ProjectFileState added() {
	    return this.fromStatus(FileState.ADDED);
	}

}
