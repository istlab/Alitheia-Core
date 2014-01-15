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
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Index;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.util.ProjectFileUtils;
import eu.sqooss.service.db.util.ProjectVersionUtils;

/**
 * Instances of this class represent the data about a version of a
 * project as stored in the database
 * 
 * @assoc 1 - n ProjectFile
 * @assoc 1 - n ProjectVersionMeasurement
 * @assoc 1 - n ProjectVersionParent
 * @assoc 1 - n Tag
 * @assoc "m\n\n outgoing\r" - "n\n\n" Branch
 * @assoc "m\n\n incoming\r" - "n\n\n" Branch
 * @assoc 1 - n NameSpace
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
    @Index(name="IDX_PROJECT_VERSION_REVISION", 
            columnNames={"STORED_PROJECT_ID", "REVISION_ID"})
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
    @ManyToOne
    @JoinColumn(name="COMMITTER_ID")
    private Developer committer;

    /**
     * The commit message provided by the developer as the revision was made
     */
    @XmlElement
    @Column(name="COMMIT_MESSAGE", length=512)
    private String commitMsg;
    
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
     * The set of known tags in this version of the project
     */
    @OneToMany(fetch=FetchType.LAZY, mappedBy="projectVersion", orphanRemoval=true, cascade=CascadeType.ALL)
    private Set<Tag> tags;
    
    /**
     * The set of measurements available for the given version of the project
     */
    @OneToMany(mappedBy="projectVersion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ProjectVersionMeasurement> measurements;

    /**
     * The parent revisions of this revision.
     */
    @OneToMany(mappedBy="child", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ProjectVersionParent> parents;
    
    @ManyToMany
    @JoinTable(
            name="BRANCH_INCOMING",
            joinColumns={@JoinColumn(name="PROJECT_VERSION_ID", referencedColumnName="PROJECT_VERSION_ID")},
            inverseJoinColumns={@JoinColumn(name="BRANCH_ID", referencedColumnName="BRANCH_ID")})
    private Set<Branch> incomingBranches;
    
    @ManyToMany
    @JoinTable(
            name="BRANCH_OUTGOING",
            joinColumns={@JoinColumn(name="PROJECT_VERSION_ID", referencedColumnName="PROJECT_VERSION_ID")},
            inverseJoinColumns={@JoinColumn(name="BRANCH_ID", referencedColumnName="BRANCH_ID")})
    private Set<Branch> outgoingBranches;

    /**
     * The namespaces that have been updated in this revision
     */
    @OneToMany(mappedBy="changeVersion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<NameSpace> namespaces;
    
    
    /**
	 * Mask used to select directories
	 * Mask used to select files
	 * Mask used to select both files and directories
	 */
    public enum MASK {
    	_FILES, _DIRECTORIES;
    	public static final EnumSet<MASK> FILES = EnumSet.of(_FILES);
    	public static final EnumSet<MASK> DIRECTORIES = EnumSet.of(_DIRECTORIES);
    	public static final EnumSet<MASK> ALL = EnumSet.allOf(MASK.class);
    }

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
    	if (versionFiles == null) {
    		versionFiles = new HashSet<>();
    	}
    		
    	return versionFiles;
    }
    
    public void setVersionFiles( Set<ProjectFile> versionFiles ) {
        this.versionFiles = versionFiles;
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
    
    public void setParents(Set<ProjectVersionParent> parents) {
        this.parents = parents;
    }

    public Set<ProjectVersionParent> getParents() {
    	if (parents == null) {
    		parents = new HashSet<>();
    	}
        return parents;
    }
    
    public Set<Branch> getIncomingBranches() {
        if (incomingBranches == null) 
            incomingBranches = new HashSet<>();
        return incomingBranches;
    }

    public void setIncomingBranches(Set<Branch> incomingBranches) {
        this.incomingBranches = incomingBranches;
    }

    public Set<Branch> getOutgoingBranches() {
        if (outgoingBranches == null) 
            outgoingBranches = new HashSet<>();
        return outgoingBranches;
    }

    public void setOutgoingBranches(Set<Branch> outgoingBranches) {
        this.outgoingBranches = outgoingBranches;
    }
    

    public void setNamespaces(Set<NameSpace> namespaces) {
        this.namespaces = namespaces;
    }

    public Set<NameSpace> getNamespaces() {
        if (namespaces == null) 
            namespaces = new HashSet<>();
        return namespaces;
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
       
    public String toString() {
        return "ProjectVersion(\"" + this.project.getName() + "\",r" + this.revisionId +")";
    }

    /**
     * Returns all files that are live in this version. 
     */
    public List<ProjectFile> getFiles() {
        return new ProjectVersionUtils(AlitheiaCore.getInstance().getDBService(), new ProjectFileUtils(AlitheiaCore.getInstance().getDBService())).getVersionFiles(this, null, MASK.ALL);
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
	public List<ProjectFile> getFiles(Directory d, EnumSet<MASK> mask) {
	    return new ProjectVersionUtils(AlitheiaCore.getInstance().getDBService(), new ProjectFileUtils(AlitheiaCore.getInstance().getDBService())).getVersionFiles(this, d, mask);
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
	    return this.getFiles(d, MASK.ALL);
	}

	/**
	 * Returns all of the files visible in a given project version that match
	 * the provided Pattern. The Pattern is evaluated against the file path.
	 * 
	 * @param p
	 *            A regular expression pattern. File paths that match the
	 *            provided pattern are returned as results.
	 * @return List of files visible in that version, whose path matches the
	 *         specified pattern (may be empty, not null)
	 * 
	 */
	public List<ProjectFile> getFiles(Pattern p) {
	    return this.getFiles(p, MASK.ALL);
	}

    /**
     * Returns all of the files visible in a given project version that match
     * the provided Pattern. The Pattern is evaluated against the file path.
     * The match can be restricted to either files or directories, for speed.
     *
     * @param p
     *            A regular expression pattern. File paths that match the
     *            provided pattern are returned as results.
     * @param mask Used to restrict the matched against to either files or
     *             directories
     *
     * @return List of files visible in that version, whose path matches the
     *         specified pattern (may be empty, not null)
     *
     */
    public List<ProjectFile> getFiles(Pattern p, EnumSet<MASK> mask) {
        List<ProjectFile> files = new ProjectVersionUtils(AlitheiaCore.getInstance().getDBService(), new ProjectFileUtils(AlitheiaCore.getInstance().getDBService())).getVersionFiles(this, null, mask);
        Set<ProjectFile> matchedFiles = new HashSet<>();

        for ( ProjectFile pf : files ) {
            Matcher m = p.matcher(pf.getFileName());

            if (m.find()) {
                matchedFiles.add(pf);
            }
        }

        return new ArrayList<>(matchedFiles);
    }
	
	/**
     * Returns all directories that are visible in a given project 
     * version. 
     *
     * @return List of directories visible in that version (may be empty, 
     * not null)
     */
    public List<ProjectFile> allDirs() {
        return new ProjectVersionUtils(AlitheiaCore.getInstance().getDBService(), new ProjectFileUtils(AlitheiaCore.getInstance().getDBService())).getVersionFiles(this, null, MASK.DIRECTORIES);
    }
    
    /**
     * Returns all the files that are visible in a given project 
     * version. 
     *
     * @return List of files visible in that version (may be empty, 
     * not null)
     */
    public List<ProjectFile> allFiles() {
        return new ProjectVersionUtils(AlitheiaCore.getInstance().getDBService(), new ProjectFileUtils(AlitheiaCore.getInstance().getDBService())).getVersionFiles(this, null, MASK.FILES);
    }

    /**
     * @see {@link ProjectVersionUtils#isTag(ProjectVersion)}
     */
    public boolean isTag() {
    	return new ProjectVersionUtils(AlitheiaCore.getInstance().getDBService(), new ProjectFileUtils(AlitheiaCore.getInstance().getDBService())).isTag(this);
    }

    /**
     * Return true if this version's actions generated a branch.
     */
    public boolean isBranch() {
    	if (getOutgoingBranches().size() > getIncomingBranches().size() 
    	        && getIncomingBranches().size() > 0)
    	    return true;
    	return false;
    }
    
    /**
     * Return true if this version's actions generated a merge.
     */
    public boolean isMerge() {
        if (getOutgoingBranches().size() < getIncomingBranches().size() && 
                getIncomingBranches().size() > 1)
            return true;
        return false;
    }
    
    /**
     * Return an outgoing branch when it's a merge, an incoming branch when it's a branch
     */
    public Branch getBranch() {
        List<Branch> branches = new ArrayList<>();
        if (isMerge() || !isBranch()) {
            branches.addAll(getOutgoingBranches());
        } else { //isBranch()
            branches.addAll(getIncomingBranches());
        }
        return branches.get(0);
    }
    
    /**
     * Filter files that changed in a version by their path name
     */
    public Set<ProjectFile> getVersionFiles(Pattern p) {
        Set<ProjectFile> result = new HashSet<>();
        Set<ProjectFile> files = getVersionFiles();

        Iterator<ProjectFile> it = files.iterator();
        while (it.hasNext()) {
            ProjectFile pf = it.next();
            Matcher m = p.matcher(pf.getFileName());
            if (m.matches())
                result.add(pf);
        }

        return result;
    }
    
    public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		ProjectVersion test = (ProjectVersion) obj;
		return  (revisionId != null && revisionId.equals(test.revisionId)) &&
				(timestamp != 0 && timestamp == test.timestamp) &&
				project != null	&& project.equals(test.project);
	}

	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + (int)timestamp;
		hash = 31 * hash + (null == revisionId ? 0 : revisionId.hashCode());
		hash = 31 * hash + (null == project ? 0 : project.hashCode());
		return hash;
	}
}

//vi: ai nosi sw=4 ts=4 expandtab
