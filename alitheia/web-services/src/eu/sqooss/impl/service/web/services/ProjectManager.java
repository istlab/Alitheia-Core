/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

package eu.sqooss.impl.service.web.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import eu.sqooss.impl.service.web.services.datatypes.WSDeveloper;
import eu.sqooss.impl.service.web.services.datatypes.WSDirectory;
import eu.sqooss.impl.service.web.services.datatypes.WSFileModification;
import eu.sqooss.impl.service.web.services.datatypes.WSProjectFile;
import eu.sqooss.impl.service.web.services.datatypes.WSProjectVersion;
import eu.sqooss.impl.service.web.services.datatypes.WSStoredProject;
import eu.sqooss.impl.service.web.services.datatypes.WSVersionStats;
import eu.sqooss.impl.service.web.services.utils.ProjectManagerDatabase;
import eu.sqooss.impl.service.web.services.utils.ProjectSecurityWrapper;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Directory;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.security.SecurityManager;

public class ProjectManager extends AbstractManager {

    private Logger logger;
    private FDSService fds;
    private ProjectManagerDatabase dbWrapper;
    private ProjectSecurityWrapper securityWrapper;

    public ProjectManager(Logger logger, DBService db, SecurityManager security, FDSService fds) {
        super(db);
        this.logger = logger;
        this.fds = fds;
        this.dbWrapper = new ProjectManagerDatabase(db);
        this.securityWrapper = new ProjectSecurityWrapper(security, db, logger);
    }

    /**
     * @see eu.sqooss.service.web.services.WebServices#getEvaluatedProjects(String, String)
     */
    public WSStoredProject[] getEvaluatedProjects(String userName, String password) {
        logger.info("Gets the evaluated project list! user: " + userName);

        db.startDBSession();

        if (!securityWrapper.checkProjectsReadAccess(userName, password, null)) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    "Security violation in the get evalueated projects operation!");
        }
        
        super.updateUserActivity(userName);

        WSStoredProject[] result = dbWrapper.getEvaluatedProjects();

        db.commitDBSession();

        return (WSStoredProject[]) normalizeWSArrayResult(result);
    }

    /**
     * @see eu.sqooss.service.web.services.WebServices#getStoredProjects(String, String)
     */
    public WSStoredProject[] getStoredProjects(String userName, String password) {
        logger.info("Gets the stored project list! user: " + userName);

        db.startDBSession();

        if (!securityWrapper.checkProjectsReadAccess(userName, password, null)) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    "Security violation in the get stored projects operation!");
        }

        super.updateUserActivity(userName);

        WSStoredProject[] result = dbWrapper.getStoredProjects(new Hashtable<String, Object>());

        db.commitDBSession();

        return (WSStoredProject[]) normalizeWSArrayResult(result);
    }

    /**
     * @see eu.sqooss.service.web.services.WebServices#getProjectByName(String, String, String)
     */
    public WSStoredProject getProjectByName(String userName, String password, String projectName) {

        logger.info("Retrieve project! user: " + userName +
                "; project name: " + projectName);

        db.startDBSession();

        super.updateUserActivity(userName);

        Map<String, Object> properties = new Hashtable<String, Object>(1);
        properties.put("name", projectName);
        WSStoredProject[] projects = dbWrapper.getStoredProjects(properties);
        db.commitDBSession();
        if ((projects == null) || (projects.length == 0) ||
                (!securityWrapper.checkProjectsReadAccess(userName, password,
                        new long[] {projects[0].getId()}))) {
            throw new SecurityException(
                    "Security violation in the get project by name operation!");
        }
        return projects[0];
    }

    /**
     * @see eu.sqooss.service.web.services.WebServices#getProjectVersionsByProjectId(String, String, long)
     */
    public WSProjectVersion[] getProjectVersionsByProjectId(String userName, String password, long projectId) {

        logger.info("Retrieve stored project versions! user: " + userName +
                "; project's id: " + projectId);

        db.startDBSession();

        if (!securityWrapper.checkProjectsReadAccess(
                userName, password, new long[] {projectId})) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    "Security violation in the get project versions by project id operation!");
        }

        super.updateUserActivity(userName);

        WSProjectVersion[] result = dbWrapper.getProjectVersionsByProjectId(projectId);

        db.commitDBSession();

        return (WSProjectVersion[]) normalizeWSArrayResult(result);
    }

    /**
     * @see eu.sqooss.service.web.services.WebServices#getProjectVersionsByIds(String, String, long[])
     */
    public WSProjectVersion[] getProjectVersionsByIds(String userName,
            String password, long[] projectVersionsIds) {

        logger.info("Retrieve project versions! user: " + userName +
                "; project versions' ids: " + Arrays.toString(projectVersionsIds));

        db.startDBSession();

        if (!securityWrapper.checkProjectVersionsReadAccess(
                userName, password, projectVersionsIds)) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException("Security violation in the get project versions by ids operation!");
        }

        super.updateUserActivity(userName);

        WSProjectVersion[] result = dbWrapper.getProjectVersionsByIds(
                asCollection(projectVersionsIds));

        db.commitDBSession();

        return (WSProjectVersion[]) normalizeWSArrayResult(result);
    }

    /**
     * @see eu.sqooss.service.web.services.WebServices#getProjectVersionsByVersionNumbers(String, String, long, long[])
     */
    public WSProjectVersion[] getProjectVersionsByVersionNumbers(
            String userName,
            String password,
            long projectId,
            long[] versionNumbers) {

        logger.info("Retrieve project versions by project id and version numbers! user: " +
        		userName + "; project id: " + projectId + "; version numbers: " + Arrays.toString(versionNumbers));

        db.startDBSession();

        if (!securityWrapper.checkProjectsReadAccess(
                userName, password, new long[] {projectId})) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    "Security violation in the get project versions by version numbers operation!");
        }

        super.updateUserActivity(userName);

        WSProjectVersion[] result = dbWrapper.getProjectVersionsByVersionNumbers(
                projectId, asCollection(versionNumbers));

        db.commitDBSession();

        return (WSProjectVersion[]) normalizeWSArrayResult(result);
    }

    public long getVersionsCount(
            String userName, String password, long projectId) {
        
        logger.info("Retrieve total number of versions! user: "  + userName +
                "; project id: " + Long.toString(projectId));

        db.startDBSession();

        if (!securityWrapper.checkProjectsReadAccess(
                userName, password, new long[] {projectId})) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    "Security violation in the get versions count operation!");
        }
        
        super.updateUserActivity(userName);

        long result = StoredProject.getVersionsCount(projectId);

        db.commitDBSession();

        return result;
    }

    /**
     * @see eu.sqooss.service.web.services.WebServices#getFirstProjectVersions(String, String, long[])
     */
    @SuppressWarnings("unchecked")
    public WSProjectVersion[] getFirstProjectVersions(String userName,
            String password, long[] projectsIds) {
        db.startDBSession();

        StringBuilder b = new StringBuilder();
        for (long l : projectsIds) {
            b.append(l);
            b.append(",");
        }
        logger.info("Retrieving first projectVersions " + b.toString());
        
        if (!securityWrapper.checkProjectsReadAccess(userName, password, projectsIds)) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    "Security violation in the get first project versions operation!");
        }

        super.updateUserActivity(userName);

        String paramProjectIds = "project_ids";
        String query = 
               "select pv " +
               "from ProjectVersion pv " +
               "where pv.project.id in (:" + paramProjectIds + ")" +
               " and pv.version= " +
               "      (select min(pv1.version) " +
               "      from ProjectVersion pv1 " +
               "      where pv1.project in (:" + paramProjectIds + ")) ";
    
        Map<String, Collection> queryParameters = new Hashtable<String, Collection>(1);
        queryParameters.put(paramProjectIds, asCollection(projectsIds));
        List<ProjectVersion> projectVersions = (List<ProjectVersion>) db.doHQL(query, null, queryParameters);
        b = new StringBuilder();
        for (ProjectVersion v : projectVersions) {
            b.append(v.getVersion());
            b.append(",");
        }
        logger.debug("Got versions " + b.toString());
        
        WSProjectVersion[] result = WSProjectVersion.asArray(projectVersions);

        db.commitDBSession();

        return (WSProjectVersion[]) normalizeWSArrayResult(result);
    }

    /**
     * @see eu.sqooss.service.web.services.WebServices#getLastProjectVersions(String, String, long[])
     */
    public WSProjectVersion[] getLastProjectVersions(String userName,
            String password, long[] projectsIds) {

        logger.info("Retrieve last project version! user: "  + userName +
                "; project id: " + Arrays.toString(projectsIds));

        db.startDBSession();

        if (!securityWrapper.checkProjectsReadAccess(userName, password, projectsIds)) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    "Security violation in the get last project versions operation!");
        }

        super.updateUserActivity(userName);

        WSProjectVersion[] result = dbWrapper.getLastProjectVersions(
                asCollection(projectsIds));

        db.commitDBSession();

        return (WSProjectVersion[]) normalizeWSArrayResult(result);
    }

    /**
     *  @see eu.sqooss.service.web.services.WebServices#getProjectsByIds(String, String, long[])
     */
    public WSStoredProject[] getProjectsByIds(String userName, String password, long[] projectsIds) {

        logger.info("Retrieve stored projects! user: " + userName +
                "; projects' ids: " + Arrays.toString(projectsIds) );

        db.startDBSession();

        if (!securityWrapper.checkProjectsReadAccess(userName, password, projectsIds)) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    "Security violation in the get projects by ids operation!");
        }

        super.updateUserActivity(userName);

        WSStoredProject[] result = dbWrapper.getProjectsByIds(asCollection(projectsIds));

        db.commitDBSession();

        return (WSStoredProject[]) normalizeWSArrayResult(result);
    }

    /**
     * @see eu.sqooss.service.web.services.WebServices#getFilesByProjectVersionId(String, String, long)
     */
    public WSProjectFile[] getFilesByProjectVersionId(String userName, String password, long projectVersionId) {
        logger.info("Get file list for project version ID "
            + projectVersionId);

        db.startDBSession();

        if (!securityWrapper.checkProjectVersionsReadAccess(
                userName, password, new long[] {projectVersionId})) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException("Security violation in the get files by project version id operation!");
        }
        
        super.updateUserActivity(userName);

        ProjectVersion v = db.findObjectById(ProjectVersion.class, projectVersionId);
        List<ProjectFile> files = ProjectFile.getFilesForVersion(v);
        WSProjectFile[] result = new WSProjectFile[files.size()];
        int i = 0;
        for (ProjectFile f : files) {
            result[i++]=WSProjectFile.getInstance(f);
        }

        db.commitDBSession();

        return (WSProjectFile[]) normalizeWSArrayResult(result);
    }

    /**
     * @see eu.sqooss.service.web.services.WebServices#getFilesByRegularExpression(String, String, long, String)
     */
    public WSProjectFile[] getFilesByRegularExpression(
            String userName,
            String password,
            long projectVersionId,
            String regExpr) {
        logger.info("Get files by prefix. project version id: "
                + projectVersionId + "; prefix: " + regExpr);
        
        db.startDBSession();
        
        if (!securityWrapper.checkProjectVersionsReadAccess(
                userName, password, new long[] {projectVersionId})) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException("Security violation in the get files by prefix operation!");
        }
        
        super.updateUserActivity(userName);
        
        WSProjectFile[] result = dbWrapper.getFilesByRegularExpression(
                projectVersionId, regExpr);
        
        db.commitDBSession();
        
        return (WSProjectFile[]) normalizeWSArrayResult(result);
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getFilesNumberByProjectVersionId(String, String, long)
     */
    public long getFilesNumberByProjectVersionId(String userName, String password, long projectVersionId) {
        logger.info("Get files's number for project version! user: " + userName +
                "; project version id: " + projectVersionId);

        db.startDBSession();

        if (!securityWrapper.checkProjectVersionsReadAccess(
                userName, password, new long[] {projectVersionId})) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    "Security violation in the get fiels number by project version id operation!");
        }
        
        super.updateUserActivity(userName);

        long result = dbWrapper.getFilesNumberByProjectVersionId(projectVersionId);

        db.commitDBSession();

        return result;
    }

    /**
     * @see eu.sqooss.service.web.services.WebServices#getDirectoriesByIds(String, String, long[])
     */
    public WSDirectory[] getDirectoriesByIds(String userName, String password,
            long[] directoriesIds) {
        logger.info("Get directories by ids! user: " + userName +
                "; directories' ids: " + Arrays.toString(directoriesIds));

        db.startDBSession();

        if (!securityWrapper.checkDirectoriesReadAccess(userName, password, directoriesIds)) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException("Security violation in the get directories by ids operation");
        }

        super.updateUserActivity(userName);

        WSDirectory[] result = dbWrapper.getDirectoriesByIds(
                asCollection(directoriesIds));

        db.commitDBSession();

        return (WSDirectory[]) normalizeWSArrayResult(result);
    }

    /**
     * @see eu.sqooss.service.web.services.WebServices#getDevelopersByIds(String, String, long[])
     */
    public WSDeveloper[] getDevelopersByIds(String userName, String password,
            long[] developersIds) {
        logger.info("Get developers by ids! useR: " + userName +
                "; developers' ids: " + Arrays.toString(developersIds));

        db.startDBSession();

        if (!securityWrapper.checkDevelopersReadAccess(userName, password, developersIds)) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    "Security violation in the get developers by ids operation!");
        }

        super.updateUserActivity(userName);

        WSDeveloper[] result = dbWrapper.getDevelopersByIds(
                asCollection(developersIds));

        db.commitDBSession();

        return (WSDeveloper[]) normalizeWSArrayResult(result);
    }

    /**
     * @see eu.sqooss.service.web.services.WebServices#getVersionsStatistics(String, String, long[])
     */
    public WSVersionStats[] getVersionsStatistics(
            String userName,
            String password,
            long[] projectVersionsIds) {
        // Log this call
        logger.info("Get versions statistics!"
                + " user: " + userName
                + ";"
                + " version Ids: " + Arrays.toString(projectVersionsIds));
        // Match against the current security policy
        db.startDBSession();
        
        if (!securityWrapper.checkProjectVersionsReadAccess(
                userName, password, projectVersionsIds)) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    "Security violation in the get versions statistics operation!");
        }
        
        super.updateUserActivity(userName);
        // Retrieve the result(s)
        if (projectVersionsIds != null) {
            List<WSVersionStats> result = new ArrayList<WSVersionStats>();
            for (Long nextId : projectVersionsIds) {
                ProjectVersion nextVersion =
                    db.findObjectById(ProjectVersion.class, nextId);
                if (nextVersion != null) {
                    WSVersionStats stats = new WSVersionStats();
                    stats.setVersionId(nextVersion.getId());
                    stats.setDeletedCount(ProjectVersion.getFilesCount(
                            nextVersion, ProjectFile.STATE_DELETED));
                    stats.setModifiedCount(ProjectVersion.getFilesCount(
                            nextVersion, ProjectFile.STATE_MODIFIED));
                    stats.setAddedCount(ProjectVersion.getFilesCount(
                            nextVersion, ProjectFile.STATE_ADDED));
                    result.add(stats);
                }
            }
            if (result.size() > 0) {
                db.commitDBSession();
                return result.toArray(new WSVersionStats[result.size()]);
            }
        }
        db.commitDBSession();
        return null;
    }

    /**
     * @see eu.sqooss.service.web.services.WebServices#getRootFolder(String, String, long)
     */
    public WSDirectory getRootDirectory(
            String userName,
            String password,
            long projectId) {
        // Log this call
        logger.info("Get root directory!"
                + " user: " + userName
                +";"
                + " project Id: " + projectId);
        // Match against the current security policy
        db.startDBSession();
        if (!securityWrapper.checkProjectsReadAccess(
                userName, password, new long[] {projectId})) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    "Security violation in the get root directory operation!");
        }
        super.updateUserActivity(userName);
        // Retrieve the result(s)
        WSDirectory result = null;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("path", "/");
        List<Directory> directories = db.findObjectsByProperties(
                Directory.class, params);
        if ((directories != null) && (directories.size() > 0))
            result = WSDirectory.getInstance(directories.get(0));
        db.commitDBSession();
        return result;
    }

    /**
     * @see eu.sqooss.service.web.services.WebServices#getFilesInDirectory(String, String, long, long)
     */
    public WSProjectFile[] getFilesInDirectory(
            String userName,
            String password,
            long projectVersionId,
            long directoryId) {
        // Log this call
        logger.info("Get files in directory!"
                + " user: " + userName
                +";"
                + " directory Id: " + directoryId);
        // Match against the current security policy
        db.startDBSession();
        if (!securityWrapper.checkDirectoriesReadAccess(userName,
                password, new long[] {directoryId})) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    "Security violation in the get files in directory operation!");
        }
        super.updateUserActivity(userName);
        // Retrieve the result(s)
        WSProjectFile[] result = null;
        ProjectVersion version =
            db.findObjectById(ProjectVersion.class, projectVersionId);
        Directory directory =
            db.findObjectById(Directory.class, directoryId);
        if ((version == null) || (directory == null)) return null;
        List<ProjectFile> files = new ArrayList<ProjectFile>();

        /*********************************************************************
         * Retrieve the list of file by using the "svn list" approach, which
         * is provided from the SVNKit library via the FDS service 
         ********************************************************************/
        HashMap<String, Long> dirEntries = fds.scmDirList(version, directory);

        // Construct a query for retrieving the corresponding ProjectFile DAOs
        String query = null;
        for (String fileName : dirEntries.keySet()) {
            if (query != null) {
                query += " or ("
                    + " pf.projectVersion = pv.id"
                    + " and pv.version = " + dirEntries.get(fileName)
                    + " and pf.name = '" + fileName + "'"
                    + " )";
            }
            else {
                query = "select pf"
                    + " from ProjectVersion pv, ProjectFile pf"
                    + " where"
                    + " pf.dir = " + directoryId
                    + " and (";
                query += " ("
                    + " pf.projectVersion = pv.id"
                    + " and pv.version = " + dirEntries.get(fileName)
                    + " and pf.name = '" + fileName + "'"
                    + ")";
            }
        }

        // Execute the query and store all DAOs in the dedicated buffer
        if (query != null) {
            query += ")";
            List<?> daoList = db.doHQL(query);
            if (daoList != null)
                for (Object nextDAO : daoList) {
                    ProjectFile fileDAO = (ProjectFile) nextDAO;
                    dirEntries.remove(fileDAO.getName());
                    files.add(fileDAO);
                }
        }

        /*
         * Any files left, fall in the special case (see the note bellow) and
         * must be retrieved in another way.
         * 
         * NOTE: SVNKit sometimes returns a "wrong" revision number for
         * directories, in case some entities inside the target directory
         * were changed. In such case, it will return the revision number,
         * that corresponds to the most recent entity change.
         */
        for (String fileName : dirEntries.keySet()) {
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.put("name", fileName);
            props.put("isDirectory", true);
            props.put("dir", directory);
            List<ProjectFile> daoList =
                db.findObjectsByProperties(ProjectFile.class, props);
            if ((daoList == null) || (daoList.isEmpty())) continue;
            ProjectFile bestMatch = daoList.get(0);
            for (ProjectFile nextDir : daoList) {
                if ((nextDir.getProjectVersion().getVersion()
                        > bestMatch.getProjectVersion().getVersion())
                        && (nextDir.getProjectVersion().getVersion()
                                <= version.getVersion()))
                    bestMatch = nextDir;
            }
            files.add(bestMatch);
        }

        // Wrap the retrieved file DAOs and construct the results array
        if (files.size() > 0) {
            result = new WSProjectFile[files.size()];
            int index = 0;
            for (ProjectFile nextFile : files)
                result[index++] = WSProjectFile.getInstance(nextFile);
        }

        /*********************************************************************
         * Alternative way of retrieving the list of files, which is based on
         * the ProjectFile.getFilesForVersion(ProjectVersion, Directory)
         * method. 
         ********************************************************************/
//        try {
//            List<ProjectFile> files =
//                ProjectFile.getFilesForVersion(version, directory);
//            if (files.size() > 0) {
//                result = new WSProjectFile[files.size()];
//                int index = 0;
//                for (ProjectFile nextFile : files)
//                    result[index++] = WSProjectFile.getInstance(nextFile);
//            }
//        }
//        catch (IllegalArgumentException ex) {}

        db.commitDBSession();
        return result;
    }

    /**
     * @see eu.sqooss.service.web.services.WebServices#getFileModifications(String, String, long, long)
     */
    public WSFileModification[] getFileModifications(
            String userName,
            String password,
            long projectVersionId,
            long projectFileId) {
        // Log this call
        logger.info("Get file modifications!"
                + " user: " + userName
                +";"
                + " file Id: " + projectFileId);
        // Match against the current security policy
        db.startDBSession();
        if (!securityWrapper.checkProjectVersionsReadAccess(
                userName, password, new long[]{projectVersionId})) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
            "Security violation in the get versions statistics operation!");
        }
        super.updateUserActivity(userName);
        // Retrieve the result(s)
        WSFileModification[] result = null;
        ProjectFile pf = db.findObjectById(ProjectFile.class, projectFileId);
        if (pf != null) {
            HashMap<Long, Long> mods = ProjectFile.getFileModifications(pf);
            if (mods.size() > 0) {
                int index = 0;
                result = new WSFileModification[mods.size()];
                for (Long verNum : mods.keySet())
                    result[index++] =
                        new WSFileModification(verNum,mods.get(verNum));
            }
        }
        db.commitDBSession();
        return result;
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
