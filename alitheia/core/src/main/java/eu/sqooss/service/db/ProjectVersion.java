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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import eu.sqooss.core.AlitheiaCore;

/**
 * Instances of this class represent the data about a version of a
 * project as stored in the database
 * 
 * @assoc 1 - n ProjectFile
 * @assoc 1 - n ProjectVersionMeasurement
 */
@XmlRootElement(name="version")
@Entity
@Table(name="PROJECT_VERSION")
public class ProjectVersion extends DAObject {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="PROJECT_VERSION_ID")
	@XmlElement
	private long id;

	/**
     * The project to which this object relates
     */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="STORED_PROJECT_ID")
    private StoredProject project;

    /**
     * The SCM version identifier to which this object relates
     */
    @XmlElement
    @Column(name="REVISION_ID")
    private String revisionId;

    /**
     * The date/time at which this version occurs, in milliseconds
     * since the epoch. @see getTimestamp(), getDate()
     */
    @XmlElement
    @Column(name="TIMESTAMP")
    private long timestamp;

    /**
     * The developer causing this revision of the project
     */
    @XmlElement
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="COMMITTER_ID")
    private Developer committer;

    /**
     * The commit message provided by the developer as the revision was made
     */
    @XmlElement
    @Column(name="COMMIT_MESSAGE", length=512)
    private String commitMsg;

    /**
     * SCM properties associated with this version. For future use.
     */
    @Column(name="PROPERTIES", length=512)
    private String properties;
    
    /**
     * The order of this version. The ordering of revisions depends on
     * the project's SCM.  
     */
    @XmlElement
    @Column(name="VERSION_SEQUENCE")
    private long sequence;
    
    /**
     * The files changed in this version
     */
    @OneToMany(fetch=FetchType.LAZY, mappedBy="projectVersion", orphanRemoval=true, cascade=CascadeType.ALL)
    private Set<ProjectFile> versionFiles;
    
    /**
     * The file groups contained in that version
     */
    @OneToMany(fetch=FetchType.LAZY, mappedBy="projectVersion", orphanRemoval=true, cascade=CascadeType.ALL)
    private Set<FileGroup> fileGroups;
    
    /**
     * The set of known tags in this version of the project
     */
    @OneToMany(fetch=FetchType.LAZY, mappedBy="projectVersion", orphanRemoval=true, cascade=CascadeType.ALL)
    private Set<Tag> tags;
    
    /**
     * The set of branches that were branched in this version of the project
     */
    @OneToMany(fetch=FetchType.LAZY, mappedBy="branchVersion", orphanRemoval=true, cascade=CascadeType.ALL)
    private Set<Branch> branched;
    
	/**
     * The set of branches that were merged in this version of the project
     */
    @OneToMany(mappedBy="mergeVersion", orphanRemoval=true, cascade=CascadeType.ALL)
    private Set<Branch> merged;
    
   
    /**
     * The set of measurements available for the given version of the project
     */
    @Transient
    private Set<ProjectVersionMeasurement> measurements;
    
    /**
	 * Mask used to select directories
	 */
	public static final int MASK_DIRECTORIES = 0x2;

	//Select files, directories or both while querying
	/**
	 * Mask used to select files
	 */
	public static final int MASK_FILES = 0x1;

	/**
	 * Mask used to select both files and directories
	 */
	public static final int MASK_ALL = MASK_FILES | MASK_DIRECTORIES;


	public ProjectVersion() {
		// Nothing to do
	}

	public ProjectVersion(StoredProject project) {
		this.project = project;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Set<Branch> getBranched() {
		return branched;
	}

	public void setBranched(Set<Branch> branched) {
		this.branched = branched;
	}

	public Set<Branch> getMerged() {
		return merged;
	}

	public void setMerged(Set<Branch> merged) {
		this.merged = merged;
	}

	public StoredProject getProject() {
		return project;
	}

    public void setProject(StoredProject project) {
        this.project = project;
    }

    public String getRevisionId() {
        return this.revisionId;
    }

    public void setRevisionId(String revisionId) {
        this.revisionId = revisionId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * Convenience method that returns the timestamp of this
     * version as a Java Date (which also has a resolution of
     * milliseconds since the epoch).
     * 
     * @return Date for this version
     */
    public Date getDate() {
        return new Date(timestamp);
    }

    /**
     * Convenience method that sets the timestamp on this version
     * from a Java Date.
     * 
     * @param d New date to use as a timestamp
     */
    public void setDate(Date d) {
        setTimestamp(d.getTime());
    }
    
    public Developer getCommitter() {
        return committer;
    }

    public void setCommitter(Developer committer) {
        this.committer = committer;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public String getCommitMsg() {
        return commitMsg;
    }

    public void setCommitMsg(String commitMsg) {
        this.commitMsg = commitMsg;
    }
    
    public long getSequence() {
        return sequence;
    }
    
    public void setSequence(long sequence) {
        this.sequence = sequence;
    }
  
    /**
     * Returns the files that were changed in this revision
     */
    public Set<ProjectFile> getVersionFiles() {
        return versionFiles;
    }
    
    public void setVersionFiles( Set<ProjectFile> versionFiles ) {
        this.versionFiles = versionFiles;
    }
  
    /**
     * Return the file groups that were changed in this version
     */
    public Set<FileGroup> getFileGroups() {
        return fileGroups;
    }
    
    public void setFileGroups(Set<FileGroup> fileGroups) {
        this.fileGroups = fileGroups;
    }
    
    /**
     * If this version has an associated tag, return it. 
     */
    public Set<Tag> getTags() {
        return tags;
    }
    
    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
        
    /**
     * Get all measurements associated with this version
     */
    public Set<ProjectVersionMeasurement> getMeasurements() {
        return measurements;
    }
    
    public void setMeasurements(Set<ProjectVersionMeasurement> measurements) {
        this.measurements = measurements;
    }

    /**
     * Less-than-or-equal (operator <=) for project versions.
     * The compared version must not be null.
     * 
     * @param p comparison version
     * @return true if this <= p, in terms of revision order
     */
    public boolean lte(ProjectVersion p) {
        if (p.getProject().getId() != p.getProject().getId())
            throw new IllegalArgumentException("Project " + p.getProject() + 
                    " != " + getProject() + ", cannot compare versions");
        return this.sequence <= p.getSequence();
    }
    
    /**
     * Less-than (operator <) for project versions. 
     * The compared version must not be null.
     * 
     * @param p comparison version
     * @return true if this <= p, in terms of revision order
     */
    public boolean lt(ProjectVersion p) {
        if (p.getProject().getId() != p.getProject().getId())
            throw new IllegalArgumentException("Project " + p.getProject() + 
                    " != " + getProject() + ", cannot compare versions");
        return this.sequence < p.getSequence();
    }
    
    /**
     * Greater-than-or-equal (operator >=) for project versions.
     * The compared version must not be null.
     * 
     * @param p comparison version
     * @return true if this > p, in terms of revision order
     */
    public boolean gte (ProjectVersion p) {
        if (p.getProject().getId() != p.getProject().getId())
            throw new IllegalArgumentException("Project " + p.getProject() + 
                    " != " + getProject() + ", cannot compare versions");
        return this.sequence >= p.getSequence();
    }
    
    /**
     * Greater-than (operator >) for project versions.
     * The compared version must not be null.
     * 
     * @param p comparison version
     * @return true if this > p, in terms of revision order
     */
    public boolean gt (ProjectVersion p) {
        if (p.getProject().getId() != p.getProject().getId())
            throw new IllegalArgumentException("Project " + p.getProject() + 
                    " != " + getProject() + ", cannot compare versions");
        return this.sequence > p.getSequence();
    }
    
    /**
     * Version equality method. Note that this is not supposed to be equivalent 
     * to {@link #equals(Object)}, it just compares the revisionId and order
     * for 2 revisions provided they are in the same project. 
     * @param p comparison version
     * @return true if the versions are equal semantically.
     */
    public boolean eq (ProjectVersion p) {
        if (p.getProject().getId() != p.getProject().getId())
            throw new IllegalArgumentException("Project " + p.getProject() + 
                    " != " + getProject() + ", cannot compare versions");
        
        if (!p.getRevisionId().equals(revisionId)) 
            return false;
        
        if (!(p.getSequence() == sequence))
            return false;
        
        return true;
        
    }
       
    /**
     * Allow moving backward in version history by finding the most-recent
     * version of this project before the current one, or null if there
     * is no such version.
     * 
     * @return Previous version, or null
     */
    public ProjectVersion getPreviousVersion() {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        
        String paramOrder = "versionOrder"; 
        String paramProject = "projectId";
        
        String query = "select pv from ProjectVersion pv where " +
			" pv.project.id =:" + paramProject +
			" and pv.sequence < :" + paramOrder + 
			" order by pv.sequence desc";
        
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramOrder, this.getSequence());
        parameters.put(paramProject, this.getProject().getId());

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
    public ProjectVersion getNextVersion() {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        
        String paramTS = "versionsequence"; 
        String paramProject = "projectId";
        
        String query = "select pv from ProjectVersion pv where " +
            " pv.project.id =:" + paramProject +
            " and pv.sequence > :" + paramTS + 
            " order by pv.sequence asc";
        
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramTS, this.getSequence());
        parameters.put(paramProject, this.getProject().getId());

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
     * @param revision SVN revision number to look up for this project
     * @return ProjectVersion object corresponding to the revision,
     *         or null if there is none.
     */
    public static ProjectVersion getVersionByRevision(StoredProject project, String revisionId) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
   
        Map<String,Object> parameters = new HashMap<String,Object>();
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
    public static ProjectVersion getVersionByTimestamp(
            StoredProject project, long timestamp) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
   
        Map<String,Object> parameters = new HashMap<String,Object>();
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
    public static ProjectVersion getFirstProjectVersion(StoredProject sp) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        Map<String,Object> parameterMap = new HashMap<String,Object>();
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
    public static ProjectVersion getLastProjectVersion(StoredProject sp) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        Map<String,Object> parameterMap = new HashMap<String,Object>();
        parameterMap.put("sp", sp);
        List<?> pvList = dbs.doHQL("from ProjectVersion pv where pv.project=:sp"
                + " and pv.sequence = (select max(pv2.sequence) from "
                + " ProjectVersion pv2 where pv2.project=:sp)",
                parameterMap);

        return (pvList == null || pvList.isEmpty()) ? null : (ProjectVersion) pvList.get(0);
    }
    
    /**
     * For a given metric and project, return the latest version of that
     * project that was actually measured.  If no measurements have been made, 
     * it returns null. For the returned revision which is not null, the
     * revision is greater than 0, there is a measurement in the database.
     * 
     * @param m Metric to look for
     * @param p Project to look for
     * @return Last version measured, or revision 0.
     */
    public static ProjectVersion getLastMeasuredVersion(Metric m, StoredProject p) {
        String query = "select pv from ProjectVersionMeasurement pvm, ProjectVersion pv" +
           " where pvm.projectVersion = pv" +
           " and pvm.metric = :metric and pv.project = :project" +
           " order by pv.sequence desc";

        HashMap<String, Object> params = new HashMap<String, Object>(4);
        params.put("metric", m);
        params.put("project", p);
        List<ProjectVersion> pv = (List<ProjectVersion>) 
            AlitheiaCore.getInstance().getDBService().doHQL( query, params, 1);
	    
        if (pv.isEmpty())
            return null;
        
        return pv.get(0);
    }

    /**
     * Gets the number of files in the given version which are in the
     * selected file state.
     * 
     * @param pv the version DAO
     * @param state the file state
     * 
     * @return The number of files in that version and that state.
     */
    public long getFilesCount(ProjectFileState state) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        // Construct the field names
        String parVersionId     = "project_version_id"; 
        String parFileStatus    = "file_status";
        // Construct the query string
        String query = "select count(*) from ProjectFile pf"
            + " where pf.projectVersion=:" + parVersionId
            + " and pf.status=:" + parFileStatus;
        // Execute the query
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(parVersionId, this);
        parameters.put(parFileStatus, state);
        List<?> queryResult = dbs.doHQL(query, parameters);
        // Return the query's result (if found)
        if(queryResult != null || queryResult.size() > 0)
            return (Long) queryResult.get(0);
        // Default result
        return 0;
    }
    
    /**
     * Get the number of files (excluding directories and deleted files) in this version. 
     * @return The number of live files in a specific project version.
     */
    public long getLiveFilesCount() {
    	DBService dbs = AlitheiaCore.getInstance().getDBService();

    	String paramVersionId = "paramVersion";
        String paramIsDirectory = "paramIsDirectory";
        String paramProjectId = "paramProject";
        String paramState = "paramState";
    
        StringBuffer q = new StringBuffer("select count(pf) ");
        q.append(" from ProjectVersion pv, ProjectVersion pv2,");
        q.append(" ProjectVersion pv3, ProjectFile pf ");
        q.append(" where pv.project.id = :").append(paramProjectId);
        q.append(" and pv.id = :").append(paramVersionId);
        q.append(" and pv2.project.id = :").append(paramProjectId);
        q.append(" and pv3.project.id = :").append(paramProjectId);
        q.append(" and pf.validFrom.id = pv2.id");
        q.append(" and pf.validUntil.id = pv3.id");
        q.append(" and pv2.sequence <= pv.sequence");
        q.append(" and pv3.sequence >= pv.sequence");
        q.append(" and pf.isDirectory = :").append(paramIsDirectory);
        q.append(" and pf.state <> :").append(paramState);
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(paramVersionId, this.getId());
        params.put(paramIsDirectory, Boolean.FALSE);
        params.put(paramState, ProjectFileState.deleted());
        params.put(paramProjectId, this.getProject().getId());
        
        return (Long) dbs.doHQL(q.toString(), params).get(0);
    }
    

    public String toString() {
        return "ProjectVersion(\"" + this.project.getName() + "\",r" + this.revisionId +")";
    }
    
    private List<ProjectFile> getVersionFiles(Directory d, int mask) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        String paramDirectory = "paramDirectory";
        String paramIsDirectory = "is_directory";
        String paramVersionId = "paramVersionId";
        String paramProjectId = "paramProjectId";
        String paramState = "paramStatus";

        StringBuffer q = new StringBuffer("select pf ");
        q.append(" from ProjectVersion pv, ProjectVersion pv2,");
        q.append(" ProjectVersion pv3, ProjectFile pf ");
        q.append(" where pv.project.id = :").append(paramProjectId);
        q.append(" and pv.id = :").append(paramVersionId);
        q.append(" and pv2.project.id = :").append(paramProjectId);
        q.append(" and pv3.project.id = :").append(paramProjectId);
        q.append(" and pf.validFrom.id = pv2.id");
        q.append(" and pf.validUntil.id = pv3.id");
        q.append(" and pv2.sequence <= pv.sequence");
        q.append(" and pv3.sequence >= pv.sequence");
        q.append(" and pf.state <> :").append(paramState);
        
 	   if (d != null) {
 	    	q.append(" and pf.dir = :").append(paramDirectory);
 	    }
 	        
 	    if (mask != ProjectVersion.MASK_ALL) {
 	    	q.append(" and pf.isDirectory = :").append(paramIsDirectory);
 	    }
 	    
 	    Map<String,Object> params = new HashMap<String,Object>();
 	    
 	 //   params.put(paramVersionSequence, this.sequence);
     	params.put(paramProjectId, this.project.getId());
     	params.put(paramState, ProjectFileState.deleted());
     	params.put(paramVersionId, this.getId());
 	    
     	if (d != null) {
     		params.put(paramDirectory, d);
     	}
     	
 	    if (mask != ProjectVersion.MASK_ALL) {
 	        Boolean isDirectory = ((mask == ProjectVersion.MASK_DIRECTORIES)?true:false);
 	        params.put(paramIsDirectory, isDirectory);
 	    }
 	    
 	    List<ProjectFile> projectFiles = (List<ProjectFile>) dbs.doHQL(q.toString(), params);

 	    if (projectFiles == null) 
 	        return Collections.emptyList();

 	    return projectFiles;

    }
    
    /**
     * Returns all files that are live in this version. 
     */
    public List<ProjectFile> getFiles() {
    	return getVersionFiles(null, ProjectVersion.MASK_ALL);
    }

	
    /**
	 * Returns either all the files or the directories or both 
	 * that are visible in a given project version and in a given directory. 
	 * Does not list recursively. Does not return null, but the list may be empty.
	 *
	 * @param d Directory to list
	 * @param mask Used to restrict the returned values to either files or
	 * directories
	 * @return List of files visible in that version (may be empty, not null)
	 */
	public List<ProjectFile> getFiles(Directory d, int mask) {
	    return getVersionFiles(d, mask);
	}
	
	/**
	 * Returns all of the files visible in a given project version
	 * and in a given directory. Does not list recursively.
	 * Does not return null, but the list may be empty.
	 *
	 * @param d Directory to list
	 * @return List of files visible in that version (may be empty, not null)
	 */
	public List<ProjectFile> getFiles(Directory d) {
	    return getFiles(d, ProjectVersion.MASK_ALL);
	}
	
	/**
	 * Returns all of the files visible in a given project version that match
	 * the provided Pattern. The Pattern is evaluated against the file path.
	 * Does not return null, but the list may be empty.
	 * 
	 * @param p
	 *            A regular expression pattern. File paths that match the
	 *            provided pattern are returned as results.
	 * @return List of files visible in that version, whose path matches the
	 *         specified pattern (may be empty, not null)
	 * 
	 */
	public List<ProjectFile> getFiles(Pattern p) {      
	    List<ProjectFile> files = this.getFiles();
	    List<ProjectFile> matchedFiles = new ArrayList<ProjectFile>();
	    
	    if (files == null) {
	        return matchedFiles;
	    }
	    
	    for ( ProjectFile pf : files ) {
	        Matcher m = p.matcher(pf.getFileName());
	        
	        if (m.matches() && !matchedFiles.contains(pf)) {
	            for(ProjectFile tmpPF : matchedFiles) {
	                if (tmpPF.getFileName().equals(pf.getFileName())) {
	                    System.err.println("Duplicate filename in file list:" 
	                    		+ tmpPF.getFileName());
	                }
	            }
	            
	            matchedFiles.add(pf);
	        }
	    }
	    
	    return matchedFiles;
	}
	
	 /**
     * Returns all directories that are visible in a given project 
     * version. 
     *
     * @param version Project and version to look at
     * @return List of directories visible in that version (may be empty, 
     * not null)
     */
    public List<ProjectFile> allDirs() {
        return getVersionFiles(null, ProjectVersion.MASK_DIRECTORIES);
    }
    
    /**
     * Returns all the files that are visible in a given project 
     * version. 
     *
     * @param version Project and version to look at
     * @return List of files visible in that version (may be empty, 
     * not null)
     */
    public List<ProjectFile> allFiles() {
    	return getVersionFiles(null, ProjectVersion.MASK_FILES);
    }
    
    /**
     * Return true if this version's actions generated a tag.
     */
    public boolean isTag() {
    	DBService dbs = AlitheiaCore.getInstance().getDBService();
    	Map<String, Object> props = new HashMap<String, Object>();
    	props.put("projectVersion", this);
    	
    	List<Tag> tags = dbs.findObjectsByProperties(Tag.class, props);
    	
    	if (tags.isEmpty())
    		return false;
    	
    	return true;
    }
    
    /**
     * Return true if this version's actions generated a branch.
     */
    public boolean isBranch() {
    	DBService dbs = AlitheiaCore.getInstance().getDBService();
    	Map<String, Object> props = new HashMap<String, Object>();
    	props.put("branchVersion", this);
    	List<Branch> branches = dbs.findObjectsByProperties(Branch.class, props);
    	
    	if (branches.isEmpty())
    		return false;
    	
    	return true;
    }
}

//vi: ai nosi sw=4 ts=4 expandtab

