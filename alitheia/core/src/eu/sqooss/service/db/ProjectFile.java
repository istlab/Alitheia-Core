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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.sqooss.impl.service.CoreActivator;

/**
 * Instances of this class represent a file relating to a project as
 * stored in the database
 */
public class ProjectFile extends DAObject{
    // File status constants
    public static final String STATE_ADDED    = "ADDED";
    public static final String STATE_MODIFIED = "MODIFIED";
    public static final String STATE_DELETED  = "DELETED";
    
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
            DBService dbs = CoreActivator.getDBService();
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
        DBService dbs = CoreActivator.getDBService();

        //No need to query if a file was just added
        if (pf.isAdded()) {
            return null;
        }

        String paramFile = "paramFile";
        String paramTimestamp = "paramTimestamp";
        String paramDir = "paramDir";
        String paramProject = "paramProject";

        String query ="select pf" +
        		" from ProjectVersion pv, ProjectFile pf" +
        		" where pf.projectVersion = pv.id " +
        		" and pf.name = :" + paramFile +
        		" and pf.dir = :" + paramDir +        		
        		" and pv.project = :" + paramProject +
        		" and pv.timestamp < :" + paramTimestamp +
        		" order by pv.timestamp desc";
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramFile, pf.getName());
        parameters.put(paramDir, pf.getDir());
        parameters.put(paramProject, pf.getProjectVersion().getProject());
        parameters.put(paramTimestamp, pf.getProjectVersion().getTimestamp());

        List<?> projectFiles = dbs.doHQL(query, parameters);

        if(projectFiles == null || projectFiles.size() == 0) {
            return null;
        }else {
            return (ProjectFile) projectFiles.get(0);
        }
    }

    /**
     * Get the file revision that is current to the provided project version.
     * @param pv The project version against which we want the current version
     * @param path The absolute file path (starting with /)
     * @return The ProjectFile instance or null if the project file was deleted before,
     * has not been added till or not found in the provided project version
     */
    public static ProjectFile getLatestVersion(ProjectVersion pv, String path) {
        DBService dbs = CoreActivator.getDBService();

        String dir = path.substring(0, path.lastIndexOf('/'));
        String fname = path.substring(path.lastIndexOf('/') + 1);

        if (path == null || path.equalsIgnoreCase("")) {
            path = "/";
        }

        Directory d = Directory.getDirectory(dir, false);

        String paramFile = "paramFile";
        String paramTS = "paramTS";
        String paramDir = "paramDir";

        String query = "select pf from ProjectVersion pv, ProjectFile pf " +
                        "where pv.timestamp in (" +
                        "select max(pv2.timestamp) " +
                        "from ProjectVersion pv2, ProjectFile pf2 " +
                        "where pv2.timestamp <= :" + paramTS +
                        " and pf2.projectVersion = pv2.id" +
                        " and pf2.dir = :" + paramDir +
                        " and pf2.name = :" + paramFile +
                        " and pv2.project = pv.project )" +
                        "and pf.projectVersion = pv.id and pf.name = :" + paramFile;

        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramFile, fname);
        parameters.put(paramDir, d);
        parameters.put(paramTS, pv.getTimestamp());

        List<?> projectFiles = dbs.doHQL(query, parameters);

        if(projectFiles == null || projectFiles.size() == 0) {
            return null;
        }else {
            return (ProjectFile) projectFiles.get(0);
        }
    }

    /**
     * Returns all of the files visible in a given project version.
     * Does not return null, but the list may be empty.
     *
     * @param version Project and version to look at
     * @return List of files visible in that version (may be empty, not null)
     */
    @SuppressWarnings("unchecked")
    public static List<ProjectFile> getFilesForVersion(ProjectVersion version) {
        DBService dbs = CoreActivator.getDBService();

        String paramTimestamp = "timestamp";
        String paramProjectId = "project_id";

        String query = "select pf1 " +
    	"from ProjectFile pf1, ProjectVersion pv1 " +
    	"where  pf1.projectVersion = pv1 " +
        " and pf1.status<>'DELETED' " +
        " and pv1.project.id = :" + paramProjectId +
        " and pv1.timestamp = ( " +
        "    select max(pv.timestamp) " + 
        "    from ProjectFile pf, ProjectVersion pv " +
        "    where pf.projectVersion=pv " + 
        "    and pv.timestamp <= :" +  paramTimestamp +
        "    and pf.dir = pf1.dir " + 
        "    and pf.name = pf1.name " +
        "    and pv.project = pv1.project )";
        
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramTimestamp, version.getTimestamp());
        parameters.put(paramProjectId, version.getProject().getId());

        List<ProjectFile> projectFiles = (List<ProjectFile>) dbs.doHQL(query, parameters);
        if (projectFiles==null) {
            // Empty array list with a capacity of 1
            return new ArrayList<ProjectFile>(1);
        } else {
            return projectFiles;
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

        DBService dbs = CoreActivator.getDBService();

        String paramProjectId = "project_id";
        String paramDirectoryId = "directory_id";
        String paramTimestamp = "timestamp";
        String paramIsDirectory = "is_directory";

        String query = "select pf1 " +
        	"from ProjectFile pf1, ProjectVersion pv1 " +
        	"where  pf1.projectVersion.id = pv1.id " +
            " and pf1.status<>'DELETED' " +
            " and pv1.project.id = :" + paramProjectId +
            " and pf1.dir.id = :" + paramDirectoryId +
            " and pv1.timestamp = ( " +
            "    select max(pv.timestamp) " + 
            "    from ProjectFile pf, ProjectVersion pv " +
            "    where pf.projectVersion.id = pv.id " + 
            "    and pv.timestamp <= :" +  paramTimestamp +
            "    and pf.dir.id = pf1.dir.id " + 
            "    and pf.name = pf1.name " +
            "    and pv.project.id = pv1.project.id )"; 
            
        if (mask != MASK_ALL) {
            query += " and pf1.isDirectory = :" + paramIsDirectory;
        }
        
        Map<String,Object> parameters = new HashMap<String,Object>();
        parameters.put(paramTimestamp, version.getTimestamp());
        parameters.put(paramProjectId, version.getProject().getId());
        parameters.put(paramDirectoryId, d.getId() );
        
        if (mask != MASK_ALL) {
            Boolean isDirectory = ((mask == MASK_DIRECTORIES)?true:false);
            parameters.put(paramIsDirectory, isDirectory);
        }
        
        List<ProjectFile> projectFiles = (List<ProjectFile>) dbs.doHQL(query, parameters);
        if (projectFiles == null || projectFiles.size() == 0) {
            // Empty array list with a capacity of 1
            return new ArrayList<ProjectFile>(1);
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
      
        List<ProjectFile> files = getFilesForVersion(version);
        List<ProjectFile> matchedFiles = new ArrayList<ProjectFile>();
        
        if (files == null) {
            // Empty array list with a capacity of 1
            return new ArrayList<ProjectFile>();
        }
        
        Iterator<ProjectFile> i = files.iterator();
        
        while (i.hasNext()) {
            ProjectFile pf = i.next();
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
     * Returns the project version's number where this file was deleted.
     * <br/>
     * This method takes into consideration the deletion of parent folders,
     * thus detecting the situation when a file was deleted indirectly by
     * removing a parent folder.
     * <br/>
     * For a project files in a deleted state, this method will return the
     * project version's number of the same file.
     *
     * @param pf the project's file
     *
     * @return The project version's number where this file was deleted,
     *   or <code>null</code> if this file still exist.
     */
    public static Long getDeletionVersion(ProjectFile pf) {
        DBService db = CoreActivator.getDBService();

        // Skip files which are in a "DELETED" state
        if (pf.isDeleted()) {
            return pf.getProjectVersion().getVersion();
        }

        // Keep the deletion version
        Long deletionVersion = null;

        // Retrieve the version of the given project file
        long fileVersion = pf.getProjectVersion().getVersion();

        // Get all project files in state "DELETED" that match the given
        // file's name and folder
        HashMap<String,Object> props = new HashMap<String,Object>();
        props.put("name", pf.getName());
        props.put("dir", pf.getDir());
        props.put("status", new String("DELETED"));
        List<ProjectFile> deletions =
            db.findObjectsByProperties(ProjectFile.class, props);
        // Check if this file was deleted at all
        if ((deletions != null) && (deletions.size() > 0)) {
            for (ProjectFile nextDeletion : deletions) {
                // Skip deletion matches that are not in the same project
                if (nextDeletion.getProjectVersion().getProject().getId()
                        != pf.getProjectVersion().getProject().getId())
                    continue;
                // Skip deletion matches that are older than the given file
                long nextDeletionVersion =
                    nextDeletion.getProjectVersion().getVersion();
                if (nextDeletionVersion <= fileVersion)
                    continue;
                // Check if this deletion is a closer match
                if ((deletionVersion == null)
                        || (deletionVersion > nextDeletionVersion)) {
                    deletionVersion =
                        nextDeletionVersion;
                }
            }
        }

        // Take into consideration the deletion version of the parent folder
        ProjectFile parentFolder = getParentFolder(pf);
        if (parentFolder != null) {
            Long parentDeletionVersion = getDeletionVersion(parentFolder);
            if (parentDeletionVersion != null) {
                // Check if the parent folder was deleted later on
                if ((deletionVersion != null)
                    && (parentDeletionVersion.longValue()
                            > deletionVersion.longValue())) {
                    return deletionVersion;
                }
                return parentDeletionVersion;
            }
        }

        // Return the project's version where this file was deleted
        return deletionVersion;
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
    public static ProjectFile getParentFolder(ProjectFile pf) {
        DBService db = CoreActivator.getDBService();

        // Get the file's folder
        String filePath = pf.getDir().getPath();

        // Proceed only if this file is not the project's root folder
        if (filePath.matches("^/+$") == false) {
            // Split the folder into folder's name and folder's path
            String dirPath =
                filePath.substring(0, filePath.lastIndexOf('/') + 1);
            if (dirPath.matches(".+/$")) {
                // Remove the trailing path separator from the folder's path
                dirPath = dirPath.substring(0, dirPath.lastIndexOf('/'));
            }
            String dirName =
                filePath.substring(filePath.lastIndexOf('/') + 1);
            // Retrieve the Directory DAO of the extracted folder's path
            HashMap<String,Object> props = new HashMap<String,Object>();
            props.put("path", dirPath);
            List<Directory> dirs =
                db.findObjectsByProperties(Directory.class, props);
            // Retrieve the ProjectFile DAOs of all folders that can be a
            // parent of the given project file.
            props.clear();
            props.put("name", dirName);
            props.put("dir", dirs.get(0));
            props.put("status", new String("ADDED"));
            List<ProjectFile> folders =
                db.findObjectsByProperties(ProjectFile.class, props);
            // Match until the "real" parent folder is found
            if ((folders != null) && (folders.size() > 0)) {
                // Retrieve the version of the given project file
                long fileVersion = pf.getProjectVersion().getVersion();
                // Keep the matched folder's DAO
                ProjectFile fileFolder = null;
                for (ProjectFile nextFolder : folders) {
                    // Skip folder matches that are not in the same project
                    if (nextFolder.getProjectVersion().getProject().getId()
                            != pf.getProjectVersion().getProject().getId())
                        continue;
                    // Skip folder matches that are newer than the given file
                    long nextFolderVersion =
                        nextFolder.getProjectVersion().getVersion();
                    if (nextFolderVersion > fileVersion)
                        continue;
                    // Check if this folder is a closer match
                    if ((fileFolder == null)
                            || (fileFolder.projectVersion.getVersion()
                                    < nextFolderVersion)) {
                        fileFolder = nextFolder;
                    }
                }
                // Return the parent folder's DAO
                return fileFolder;
            }
        }

        return null;
    }

    /**
     * Constructs a hash map of all project version numbers where this
     * particular file was modified, and the file's DAO Id in these versions.
     * The project version number is used as a hash key, while the project
     * file Id in that version as a hash value.
     * 
     * @param pf the project file DAO
     * 
     * @return the modifications hash map
     */
    @SuppressWarnings("unchecked")
    public static HashMap<Long, Long> getFileModifications(ProjectFile pf) {
        DBService dbs = CoreActivator.getDBService();
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
            + " order by pv.timestamp desc";
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(paramFile, pf.getName());
        parameters.put(paramDir, pf.getDir());
        parameters.put(paramProject, pf.getProjectVersion().getProject());

        List<ProjectFile> projectFiles = 
            (List<ProjectFile>) dbs.doHQL(query, parameters);

        Iterator<ProjectFile> i = projectFiles.iterator();

        while (i.hasNext()){
            ProjectFile pf1 = i.next();
            result.put(pf1.getProjectVersion().getVersion(), pf1.getId());
        }

        return result;
    }
    
    /**
     * Return the latest file versions matching the provided arguments
     * 
     * TODO: Move to ProjectFile, where it belongs
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
            String path, Long version) {
        DBService dbs = CoreActivator.getDBService();
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
            "where pv1.version = :" + paramVersion +
            " and pv1.project.id = :" + paramProjectId +")";
        
        query += " order by pv.timestamp desc";

        pfs = (List<ProjectFile>) dbs.doHQL(query, parameters);
        
        if (pfs.isEmpty()) 
            return null;
        
        return pfs.get(0);
    }
}

//vi: ai nosi sw=4 ts=4 expandtab

