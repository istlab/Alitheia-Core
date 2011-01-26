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
import eu.sqooss.service.util.FileUtils;

/**
 * Instances of this class represent a file relating to a project as
 * stored in the database
 * 
 * @assoc 1 - n ProjectFileMeasurement
 * 
 */
@Entity
@Table(name="PROJECT_FILE")
@XmlRootElement(name="file")
public class ProjectFile extends DAObject{
   
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="PROJECT_FILE_ID")
	@XmlElement
	private long id; 
	
	/**
     * The filename (name without a directory for files, the directory
     * name for directories)
     */
	@Column(name="FILE_NAME")
	@XmlElement
    private String name;

    /**
     * the version of the project to which this file relates
     */
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="PROJECT_VERSION_ID")
    private ProjectVersion projectVersion;

    /**
     * The file's status in this revision 
     */
    @ManyToOne
    @JoinColumn(name="STATE_ID")
    @XmlElement
    private ProjectFileState state;

    /**
     * If this "file" is actually a directory then this is set to true
     */
    @Column(name="IS_DIRECTORY")
    @XmlElement(name = "isdir")
    private boolean isDirectory;

    /**
     * The SVN directory for which this file can be found
     */
    @ManyToOne
    @JoinColumn(name="DIRECTORY_ID")
    @XmlElement
    private Directory dir;
    
    /**
     * The start revision a file has been valid from (the 
     * addition/copy revision)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="VALID_FROM_ID")
    private ProjectVersion validFrom;
    
    /**
     * The revision this file version stopped being 
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="VALID_TO_ID")
    private ProjectVersion validUntil;

    /**
     * The ProjectFile this file was copied from. Only gets a value 
     * for file copy operations
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="COPY_FROM_ID")
    private ProjectFile copyFrom;
    
    /**
     * File measurements for this file
     */
    @OneToMany(mappedBy = "projectFile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ProjectFileMeasurement> measurements;
    
    public ProjectFile() {
        // Nothing to see here
        isDirectory = false; //By default, all entries are files
    }

    public ProjectFile(ProjectVersion pv) {
        this.projectVersion = pv;
        this.setValidFrom(pv);
        this.setValidUntil(null);
        isDirectory = false; //By default, all entries are files
    }

    /**
     * "Copy" constructor creates a new object that is just like
     * the given project file, except the project version is different.
     * @param f File data to duplicate
     * @param v New project version to set
     * @throws IllegalArgumentException if the project implicit in the file
     *          and the version does not match (e.g. assigning a version from
     *          a different project to a file).
     */
    public ProjectFile(ProjectFile f, ProjectVersion v) 
        throws IllegalArgumentException {
        if (f.getProjectVersion().getProject().getId() != v.getProject().getId()) {
            throw new IllegalArgumentException(
                    "ProjectFile(" + f.getProjectVersion().getProject().getId() + ") " +
                    "and ProjectVersion(" + v.getProject().getId() + ") " +
                    "project ID mismatch.");
        }
        this.dir = f.getDir();
        this.isDirectory = f.getIsDirectory();
        this.measurements = null;
        this.name = f.getName();
        this.projectVersion = v;
        this.validFrom = v;
        this.validUntil = null;
        //this.status = f.getStatus();
    }
    
    /**
     * Sets the filename of this file. For directories, this
     * is the directory name. Only one level of names is used --
     * this is *not* the pathname.
     * @param name Name to use
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the filename -- not the pathname -- of this file
     * or directory. See basename(1).
     * @return Name of this file
     */
    public String getName() {
        return name;
    }

    public void setProjectVersion(ProjectVersion projectVersion ) {
        this.projectVersion = projectVersion;
    }

    public ProjectVersion getProjectVersion() {
        return projectVersion;
    }

    public void setState(ProjectFileState state) {
        this.state = state;
    }

    public ProjectFileState getState() {
        return state;
    }

    public boolean isDeleted() {
        return (state.equals(ProjectFileState.deleted()));
    }

    public boolean isAdded() {
        return (state.equals(ProjectFileState.added()));
    }
    
    public boolean isReplaced() {
        return (state.equals(ProjectFileState.replaced()));
    }
    
    public boolean isModified() {
        return (state.equals(ProjectFileState.modified()));
    }
   
    public boolean getIsDirectory() {
        return isDirectory;
    }

    public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}

    public void setIsDirectory(boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    public Directory getDir() {
        return dir;
    }

    public void setDir(Directory dir) {
        this.dir = dir;
    }
    
    public ProjectVersion getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(ProjectVersion validFrom) {
        this.validFrom = validFrom;
    }

    public ProjectVersion getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(ProjectVersion validUntil) {
        this.validUntil = validUntil;
    }
    
    public ProjectFile getCopyFrom() {
        return copyFrom;
    }

    public void setCopyFrom(ProjectFile copyFrom) {
        this.copyFrom = copyFrom;
    }

    public Set<ProjectFileMeasurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(Set<ProjectFileMeasurement> measurements) {
        this.measurements = measurements;
    }
    
    /**
     * Returns the full path to the file, relative to the repository root
     * @return
     */
    public String getFileName() {
        String result = dir.getPath();
        if (!result.endsWith("/"))
            result += "/";
        result += name;
        return result;
    }

    /**
     * If this <code>ProjectFile</code> represents a folder, then this method
     * will return the <code>Directory<code> DAO that match the folder's name.
     * 
     * @return The corresponding <code>Directory</code> DAO
     */
    public Directory toDirectory() {
        if ((isDirectory) && (getFileName() != null)) {
            DBService dbs = AlitheiaCore.getInstance().getDBService();
            Map<String, Object> props = new HashMap<String, Object>();
            props.put("path", getFileName());
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
    public ProjectFile getPreviousFileVersion() {
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        //No need to query if a file was just added
        if (this.isAdded()) {
            return null;
        }

        String paramFile = "paramFile";
        String paramOrder = "paramsequence";
        String paramDir = "paramDir";
        String paramProject = "paramProject";
        String paramCopyFromName = "paramCopyFromName";
        String paramCopyFromDir = "paramCopyFromDir";

        String query = "select pf" +
            " from ProjectVersion pv, ProjectFile pf" +
            " where pf.projectVersion = pv.id " +
            " and pv.project = :" + paramProject +
            " and pv.sequence < :" + paramOrder +
            " and "; 
            if (this.copyFrom != null) {
                query += "(("; 
            }
            
            query += " pf.name = :" + paramFile +
            " and pf.dir = :" + paramDir;
            if (this.copyFrom != null) {
                query += " ) or ( pf.name = :" + paramCopyFromName +
                " and pf.dir = :" + paramCopyFromDir +
                "     ))" ;
            }
            query += " order by pv.sequence desc";
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramFile, this.getName());
        parameters.put(paramDir, this.getDir());
        parameters.put(paramProject, this.getProjectVersion().getProject());
        parameters.put(paramOrder, this.getProjectVersion().getSequence());
        
        if (this.copyFrom != null) {
            parameters.put(paramCopyFromName, this.getCopyFrom().getName());
            parameters.put(paramCopyFromDir, this.getCopyFrom().getDir());
        }
        List<?> projectFiles = dbs.doHQL(query, parameters, 1);

        if(projectFiles == null || projectFiles.size() == 0) {
            return null;
        }else {
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
    public static ProjectVersion getDeletionVersion(ProjectFile pf) {
        DBService db = AlitheiaCore.getInstance().getDBService();

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

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(paramStatusDeleted, ProjectFileState.deleted());
        params.put(paramStatusAdded, ProjectFileState.added());
        params.put(paramName, pf.getName());
        params.put(paramDir, pf.getDir());
        params.put(paramDirectory, pf.getIsDirectory());
        params.put(paramProject, pf.getProjectVersion().getProject());
        params.put(paramOrder, pf.getProjectVersion().getSequence());

        List<ProjectVersion> pvs = (List<ProjectVersion>) db.doHQL(query, params);
                       
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
    public ProjectFile getEnclosingDirectory() {
        DBService db = AlitheiaCore.getInstance().getDBService();
        
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
        
        HashMap<String, Object> params = new HashMap<String, Object>();
        
        params.put(paramName, FileUtils.basename(this.getDir().getPath()));
        params.put(paramDir, Directory.getDirectory(FileUtils.dirname(this.getDir().getPath()), false));
        params.put(paramProject, this.getProjectVersion().getProject());
        params.put(paramIsDir, true);
        params.put(paramSequence, this.getProjectVersion().getSequence());
        
        List<ProjectFile> pfs = (List<ProjectFile>) db.doHQL(query, params, 1);
        
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
    public static List<ProjectFile> getFileModifications(ProjectFile pf) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        
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
        Map<String, Object> parameters = new HashMap<String, Object>();
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
    public static ProjectFile findFile(Long projectId, String name,
            String path, String version) {
    	return findFile(projectId, name, path, version, false);
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
    public static ProjectFile findFile(Long projectId, String name,
            String path, String version, boolean inclDeleted) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        List<ProjectFile> pfs = new ArrayList<ProjectFile>();
        Map<String, Object> parameters = new HashMap<String, Object>();
        
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
        	parameters.put(paramStatus, ProjectFileState.deleted());        
        parameters.put(paramProjectId, projectId);
        parameters.put(paramName, name);
        parameters.put(paramPath, path);
        parameters.put(paramVersion, version);
        
        pfs = (List<ProjectFile>) dbs.doHQL(query.toString(), parameters, 1);
        
        if (pfs.isEmpty()) 
            return null;
        
        return pfs.get(0);
    }
    
    public String toString() {
    	StringBuilder sb  = new StringBuilder();
        return sb.append(projectVersion.getRevisionId())
        		.append(":").append(((getFileName()==null)?"":getFileName()))
        		.append(" (").append(getState()).append(")").toString();
    }

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		ProjectFile test = (ProjectFile) obj;
		return  (name != null && name.equals(test.name)) &&
				(state != null && state.equals(test.state)) &&
				dir != null	&& dir.equals(test.dir) &&
				projectVersion != null && projectVersion.equals(test.projectVersion);
	}

	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + name.hashCode();
		hash = 31 * hash + (null == state ? 0 : state.hashCode());
		hash = 31 * hash + (null == dir ? 0 : dir.hashCode());
		hash = 31 * hash + (null == projectVersion ? 0 : projectVersion.hashCode());
		return hash;
	}
}

//vi: ai nosi sw=4 ts=4 expandtab

