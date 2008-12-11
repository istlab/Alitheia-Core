/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Paul J. Adams <paul.adams@siriusit.co.uk>
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.util.FileUtils;

/**
 * Instances of this class represent a file relating to a project as
 * stored in the database
 */
public class ProjectFile extends DAObject{
    // File status constants
    public static final String STATE_ADDED    = "ADDED";
    public static final String STATE_MODIFIED = "MODIFIED";
    public static final String STATE_DELETED  = "DELETED";
    public static final String STATE_REPLACED  = "REPLACED";
    
    //Select files, directories or both while querying
    /**
     * Mask used to select files
     */
    public static final int MASK_FILES = 0x1;
    
    /**
     * Mask used to select directories
     */
    public static final int MASK_DIRECTORIES = 0x2;
    
    /**
     * Mask used to select both files and directories
     */
    public static final int MASK_ALL = MASK_FILES | MASK_DIRECTORIES;
    
    /**
     * The filename
     */
    private String name;

    /**
     * the version of the project to which this file relates
     */
    private ProjectVersion projectVersion;

    /**
     * A representation of the status of the file in this revision:
     * <ul>
     * <li>ADDED</li>
     * <li>MODIFIED</li>
     * <li>DELETED</li>
     * </ul>
     */
    private String status;

    /**
     * If this "file" is actually a directory then this is set to true
     */
    private boolean isDirectory;

    /**
     * The SVN directory for which this file can be found
     */
    private Directory dir;

    /**
     * The ProjectFile this file was copied from. Only gets a value 
     * for file copy operations
     */
    private ProjectFile copyFrom;
    
    /**
     * File measurements for this file
     */
    private Set<ProjectFileMeasurement> measurements;
    
    public ProjectFile() {
        // Nothing to see here
        isDirectory = false; //By default, all entries are files
    }

    public ProjectFile(ProjectVersion pv) {
        this.projectVersion = pv;
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
        this.status = f.getStatus();
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

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public boolean isDeleted() {
        return "DELETED".equalsIgnoreCase(status);
    }

    public boolean isAdded() {
        return "ADDED".equalsIgnoreCase(status);
    }
    
    public void makeDeleted() {
        setStatus("DELETED");
    }
    
    public boolean getIsDirectory() {
        return isDirectory;
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
    public static ProjectFile getPreviousFileVersion(ProjectFile pf) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();

        //No need to query if a file was just added
        if (pf.isAdded()) {
            return null;
        }

        String paramFile = "paramFile";
        String paramOrder = "paramOrder";
        String paramDir = "paramDir";
        String paramProject = "paramProject";
        String paramCopyFromName = "paramCopyFromName";
        String paramCopyFromDir = "paramCopyFromDir";

        String query = "select pf" +
            " from ProjectVersion pv, ProjectFile pf" +
            " where pf.projectVersion = pv.id " +
            " and pv.project = :" + paramProject +
            " and pv.order < :" + paramOrder +
            " and "; 
            if (pf.copyFrom != null) {
                query += "(("; 
            }
            
            query += " pf.name = :" + paramFile +
            " and pf.dir = :" + paramDir;
            if (pf.copyFrom != null) {
                query += " ) or ( pf.name = :" + paramCopyFromName +
                " and pf.dir = :" + paramCopyFromDir +
                "     ))" ;
            }
            query += " order by pv.order desc";
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramFile, pf.getName());
        parameters.put(paramDir, pf.getDir());
        parameters.put(paramProject, pf.getProjectVersion().getProject());
        parameters.put(paramOrder, pf.getProjectVersion().getOrder());
        
        if (pf.copyFrom != null) {
            parameters.put(paramCopyFromName, pf.getCopyFrom().getName());
            parameters.put(paramCopyFromDir, pf.getCopyFrom().getDir());
        }
        List<?> projectFiles = dbs.doHQL(query, parameters, 1);

        if(projectFiles == null || projectFiles.size() == 0) {
            return null;
        }else {
            return (ProjectFile) projectFiles.get(0);
        }
    }

    /**
     * Returns all of the files visible in a given project version
     * and in a given directory. Does not list recursively.
     * Does not return null, but the list may be empty.
     *
     * @param version Project and version to look at
     * @param d Directory to list
     * @return List of files visible in that version (may be empty, not null)
     */
    public static List<ProjectFile> getFilesForVersion(ProjectVersion version,
            Directory d) {
        return getFilesForVersion(version, d, MASK_ALL);
    }
    
    /**
     * Returns either all the files or the directories or both 
     * that are visible in a given project version and in a given directory. 
     * Does not list recursively. Does not return null, but the list may be empty.
     *
     * @param version Project and version to look at
     * @param d Directory to list
     * @param mask Used to restrict the returned values to either files or
     * directories
     * @return List of files visible in that version (may be empty, not null)
     */
    @SuppressWarnings("unchecked")
    public static List<ProjectFile> getFilesForVersion(ProjectVersion version,
            Directory d, int mask) {
        if (version==null || d==null) {
            throw new IllegalArgumentException("Project version or directory" +
            		" is null in getFilesForVersion.");
	}

        DBService dbs = AlitheiaCore.getInstance().getDBService();

        String paramVersion = "paramVersion";
        String paramDirectory = "paramDirectory";
        String paramIsDirectory = "is_directory";

        String query = "select pf " +
        	" from ProjectFile pf, FileForVersion ffv " +
        	" where ffv.file = pf " +
        	" and ffv.version = :" + paramVersion +
        	" and pf.dir = :" + paramDirectory;
            
        if (mask != MASK_ALL) {
            query += " and pf.isDirectory = :" + paramIsDirectory;
        }
        
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramDirectory, d);
        parameters.put(paramVersion, version);
        
        if (mask != MASK_ALL) {
            Boolean isDirectory = ((mask == MASK_DIRECTORIES)?true:false);
            parameters.put(paramIsDirectory, isDirectory);
        }
        
        List<ProjectFile> projectFiles = (List<ProjectFile>) dbs.doHQL(query, parameters);
        if (projectFiles == null || projectFiles.size() == 0) {
            return Collections.emptyList();
        } else {
            return projectFiles;
        }
    }
    
    /**
     * Returns all of the files visible in a given project version
     * that match the provided Pattern. The Pattern is evaluated
     * against the file path.
     * Does not return null, but the list may be empty.
     *
     * @param version Project and version to look at
     * @param filter SQL-like expression to filter out unwanted paths
     * @return List of files visible in that version, whose path matches the 
     * specifed pattern (may be empty, not null)
     * 
     */
    public static List<ProjectFile> getFilesForVersion(ProjectVersion version, Pattern p) {      
        Set<ProjectFile> files = version.getFilesForVersion();
        List<ProjectFile> matchedFiles = new ArrayList<ProjectFile>();
        
        if (files == null) {
            return matchedFiles;
        }
        
        for ( ProjectFile pf : files ) {
            Matcher m = p.matcher(pf.getFileName());
            
            if (m.matches() && !matchedFiles.contains(pf)) {
                for(ProjectFile tmpPF : matchedFiles) {
                    if (tmpPF.getFileName().equals(pf.getFileName())) {
                        System.err.println("Duplicate filename in file list:" + tmpPF.getFileName());
                    }
                }
                
                matchedFiles.add(pf);
            }
        }
        
        return matchedFiles;
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
        String paramOrder = "paramOrder";
        
        /* The query needs to cater for a file being deleted
         * and re-added in the same directory so it only 
         * considers the latest incarnation of the file
         */
        String query = "select pv " +
            " from ProjectFile pf, ProjectVersion pv " +
            " where pf.projectVersion = pv " +
            " and pf.status='DELETED' " +
            " and pf.name = :" + paramName +
            " and pf.dir = :" + paramDir + 
            " and pf.isDirectory = :" + paramDirectory +
            " and pv.project = :" + paramProject +
            " and pv.order > " +
            "           (select max(pv1.order) " +
            "           from ProjectVersion pv1, ProjectFile pf1" +
            "           where pf1.projectVersion = pv1" +
            "           and pf1.status = 'ADDED'" +
            "           and pf1.name = :" + paramName +
            "           and pf1.dir = :" + paramDir +
            "           and pf1.isDirectory = :" + paramDirectory +
            "           and pv1.project = :" + paramProject +
            "           and pv1.order < :" + paramOrder + 
            "           group by pv1) ";

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(paramName, pf.getName());
        params.put(paramDir, pf.getDir());
        params.put(paramDirectory, pf.getIsDirectory());
        params.put(paramProject, pf.getProjectVersion().getProject());
        params.put(paramOrder, pf.getProjectVersion().getOrder());

        List<ProjectVersion> pvs = (List<ProjectVersion>) db.doHQL(query, params);
                       
        if (pvs.size() <= 0)
            return null;
        else 
            return pvs.get(0);
    }

    /**
     * Gets the parent folder of the given project file.
     *
     * @param pf the project file
     *
     * @return The <code>ProjectFile</code> DAO of the parent folder,
     *   or <code>null</code> if the given file is located in the project's
     *   root folder (<i>or the given file is the root folder</i> ).
     */
    public ProjectFile getParentFolder() {
        DBService db = AlitheiaCore.getInstance().getDBService();
        
        String paramName = "paramName"; 
        String paramDir = "paramDir"; 
        String paramProject = "paramProject";
        String paramOrder = "paramOrder";
        
        String query = "select pf " +
            " from ProjectFile pf, ProjectVersion pv " +
            " where pf.projectVersion = pv " +
            " and pf.name = :" + paramName +
            " and pf.dir = :" + paramDir + 
            " and pf.isDirectory = 'true'" +
            " and pv.project = :" + paramProject +
            " and pv.order <= :" + paramOrder +
            " order by pv.order desc";
            
        HashMap<String, Object> params = new HashMap<String, Object>();
        
        params.put(paramName, FileUtils.basename(getDir().getPath()));
        params.put(paramDir, Directory.getDirectory(FileUtils.dirname(getDir().getPath()), false));
        params.put(paramProject, getProjectVersion().getProject());
        params.put(paramOrder, getProjectVersion().getOrder());
        
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
    public static HashMap<Long, Long> getFileModifications(ProjectFile pf) {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        HashMap<Long, Long> result = new HashMap<Long, Long>();

        if (pf == null) {
            return result;
        }

        String paramFile = "paramFile";
        String paramDir = "paramDir";
        String paramProject = "paramProject";

        String query = "select pf"
            + " from ProjectVersion pv, ProjectFile pf"
            + " where pf.projectVersion = pv.id "
            + " and pf.name = :" + paramFile
            + " and pf.dir = :" + paramDir
            + " and pv.project = :" + paramProject
            + " order by pv.order desc";
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(paramFile, pf.getName());
        parameters.put(paramDir, pf.getDir());
        parameters.put(paramProject, pf.getProjectVersion().getProject());

        List<ProjectFile> projectFiles = 
            (List<ProjectFile>) dbs.doHQL(query, parameters);

        Iterator<ProjectFile> i = projectFiles.iterator();

        while (i.hasNext()){
            ProjectFile pf1 = i.next();
            result.put(pf1.getProjectVersion().getTimestamp(), pf1.getId());
        }

        return result;
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
            String path, String version) {
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
        
        String query = "select pf " 
            + " from ProjectFile pf, ProjectVersion pv, StoredProject sp ";
        
        if (path != null)
            query += ", Directory d ";

        query += " where pf.projectVersion = pv.id " 
            + " and pf.status <> 'DELETED'"
            + " and pv.project.id = :" + paramProjectId 
            + " and pf.name = :" + paramName;
        
        parameters.put(paramProjectId, projectId);
        parameters.put(paramName, name);
        
        query += " and pf.dir.id = d.id " 
            + " and d.path = :" + paramPath;
        parameters.put(paramPath, path);

        
        parameters.put(paramVersion, version);
        query += " and pv.timestamp <= ( " +
            "select pv1.timestamp " +
            "from ProjectVersion pv1 " +
            "where pv1.revisionId = :" + paramVersion +
            " and pv1.project.id = :" + paramProjectId +")";
        
        query += " order by pv.order desc";

        pfs = (List<ProjectFile>) dbs.doHQL(query, parameters, 1);
        
        if (pfs.isEmpty()) 
            return null;
        
        return pfs.get(0);
    }
    
    public String toString() {
        return "r" + projectVersion.getRevisionId() + ":" + getFileName() + " (" + getStatus() + ")";
    }
}

//vi: ai nosi sw=4 ts=4 expandtab

