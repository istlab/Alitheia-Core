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

import java.util.HashSet;
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

import org.hibernate.annotations.Index;

/**
 * Instances of this class represent a file relating to a project as
 * stored in the database
 * 
 * @assoc 1 - n ProjectFileMeasurement
 * @assoc 1 - n EncapsulationUnit
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
    @Index(name="IDX_PROJECT_FILE_NAME")
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
     * If this "file" contains source code files, it is marked as a module
     */
    @Column(name="IS_MODULE", nullable = true)
    @XmlElement(name = "ismodule")
    private Boolean module;
    
    /**
     * File measurements for this file
     */
    @OneToMany(mappedBy = "projectFile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ProjectFileMeasurement> measurements;
    
    /**
     * Classes defined in this file
     */
    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<EncapsulationUnit> encapsulationUnits;
    
    /**
     * Methods defined in this file
     */
    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ExecutionUnit> executionUnits;
    
    public ProjectFile() {
        // Nothing to see here
        isDirectory = false; //By default, all entries are files
        module = null;
    }

    public ProjectFile(ProjectVersion pv) {
        this();
        this.projectVersion = pv;
        this.setValidFrom(pv);
        this.setValidUntil(null);
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
        return (state.getFileStatus() == FileState.DELETED);
    }

    public boolean isAdded() {
        return (state.getFileStatus() == FileState.ADDED);
    }
    
    public boolean isReplaced() {
        return (state.getFileStatus() == FileState.REPLACED);
    }
    
    public boolean isModified() {
        return (state.getFileStatus() == FileState.MODIFIED);
    }
   
    public boolean getIsDirectory() {
        return isDirectory;
    }
    
    public Boolean isModule() {
        return module;
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
	
	public void setModule(Boolean isModule) {
        this.module = isModule;
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
    

    public void setEncapsulationUnits(Set<EncapsulationUnit> encapsulationUnits) {
        this.encapsulationUnits = encapsulationUnits;
    }

    public Set<EncapsulationUnit> getEncapsulationUnits() {
        if (encapsulationUnits == null)
            encapsulationUnits = new HashSet<>();
        return encapsulationUnits;
    }
    
    public void setExecutionUnits(Set<ExecutionUnit> executionUnits) {
        if (executionUnits == null)
            executionUnits = new HashSet<>();
        this.executionUnits = executionUnits;
    }

    public Set<ExecutionUnit> getExecutionUnits() {
        return executionUnits;
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

