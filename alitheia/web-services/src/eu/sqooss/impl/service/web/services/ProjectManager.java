/*
 * Copyright 2008 - Organization for Free and Open Source Software,
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

package eu.sqooss.impl.service.web.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import eu.sqooss.impl.service.web.services.datatypes.WSDeveloper;
import eu.sqooss.impl.service.web.services.datatypes.WSDirectory;
import eu.sqooss.impl.service.web.services.datatypes.WSFileGroup;
import eu.sqooss.impl.service.web.services.datatypes.WSFileModification;
import eu.sqooss.impl.service.web.services.datatypes.WSMailMessage;
import eu.sqooss.impl.service.web.services.datatypes.WSProjectFile;
import eu.sqooss.impl.service.web.services.datatypes.WSProjectVersion;
import eu.sqooss.impl.service.web.services.datatypes.WSShortBug;
import eu.sqooss.impl.service.web.services.datatypes.WSShortMailMessage;
import eu.sqooss.impl.service.web.services.datatypes.WSShortProjectVersion;
import eu.sqooss.impl.service.web.services.datatypes.WSStoredProject;
import eu.sqooss.impl.service.web.services.datatypes.WSTaggedVersion;
import eu.sqooss.impl.service.web.services.datatypes.WSVersionStats;
import eu.sqooss.impl.service.web.services.utils.ProjectManagerDatabase;
import eu.sqooss.impl.service.web.services.utils.ProjectSecurityWrapper;
import eu.sqooss.service.db.Bug;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Directory;
import eu.sqooss.service.db.MailMessage;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileState;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.BugStatus.Status;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.fds.ProjectEvent;
import eu.sqooss.service.fds.Timeline;
import eu.sqooss.service.fds.Timeline.ResourceType;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.security.SecurityManager;

/**
 *
 * @author Evgeni Grigorov, <tt>(ProSyst Software GmbH)</tt>
 * @author Boryan Yotov, <tt>(ProSyst Software GmbH)</tt>
 */
public class ProjectManager extends AbstractManager {

    private Logger logger;
    private ProjectManagerDatabase dbWrapper;
    private ProjectSecurityWrapper securityWrapper;
    private FDSService fds;

    private static final String SEC_VIOLATION =
        "Security violation in method: ";

    public ProjectManager(Logger logger, DBService db, SecurityManager security, FDSService fds) {
        super(db);
        this.logger = logger;
        this.dbWrapper = new ProjectManagerDatabase(db);
        this.securityWrapper = new ProjectSecurityWrapper(security, db, logger);
        this.fds = fds;
    }

    //========================================================================
    // PROJECT RELATED METHODS
    //========================================================================

    /**
     * @see eu.sqooss.service.web.services.WebServices#getEvaluatedProjects(String, String)
     */
    public WSStoredProject[] getEvaluatedProjects(
            String userName,
            String password) {
        // Log this call
        logger.info("getEvaluatedProjects!"
                + " user: " + userName);

        // Match against the current security policy
        db.startDBSession();
        if (!securityWrapper.checkProjectsReadAccess(
                userName, password, null)) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    SEC_VIOLATION + "getEvaluatedProjects!");
        }
        super.updateUserActivity(userName);

        // Retrieve the result(s)
        WSStoredProject[] result = null;
        List<StoredProject> projects = db.findObjectsByProperties(
                StoredProject.class, new HashMap<String, Object>());
        if (projects.size() > 0) {
            ArrayList<WSStoredProject> evaluated =
                new ArrayList<WSStoredProject>();
            for (StoredProject project : projects)
                if (project.isEvaluated())
                    evaluated.add(WSStoredProject.getInstance(project));
            if (evaluated.size() > 0) {
                result = evaluated.toArray(
                        new WSStoredProject[evaluated.size()]);
            }
        }

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
     *  @see eu.sqooss.service.web.services.WebServices#getProjectsByIds(String, String, long[])
     */
    public WSStoredProject[] getProjectsByIds(String userName, String password, long[] projectsIds) {
        // Log this call
        logger.info("getProjectsByIds!"
                + " user: " + userName
                + ";"
                + " project Ids: " + Arrays.toString(projectsIds) );

        db.startDBSession();
        if (!securityWrapper.checkProjectsReadAccess(userName, password, projectsIds)) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    SEC_VIOLATION + "getProjectsByIds!");
        }
        super.updateUserActivity(userName);

        // Retrieve the result(s)
        // TODO: Use the DAOs instead
        WSStoredProject[] result = dbWrapper.getProjectsByIds(
                asCollection(projectsIds));

        db.commitDBSession();
        return (WSStoredProject[]) normalizeWSArrayResult(result);
    }

    //========================================================================
    // VERSION RELATED PROJECT METHODS
    //========================================================================

    /**
     * @see eu.sqooss.service.web.services.WebServices#getProjectVersionsByProjectId(String, String, long)
     */
    public WSProjectVersion[] getProjectVersionsByProjectId(
            String userName,
            String password,
            long projectId) {
        // Log this call
        logger.info("getProjectVersionsByProjectId!"
                + " user: " + userName
                + ";"
                + " project id: " + projectId);

        // Match against the current security policy
        db.startDBSession();
        if (!securityWrapper.checkProjectsReadAccess(
                userName, password, new long[] {projectId})) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    SEC_VIOLATION + "getProjectVersionsByProjectId!");
        }
        super.updateUserActivity(userName);

        // Retrieve the result(s)
        // TODO: Use the DAOs instead
        WSProjectVersion[] result = dbWrapper.getProjectVersionsByProjectId(projectId);

        db.commitDBSession();
        return (WSProjectVersion[]) normalizeWSArrayResult(result);
    }

    /**
     * @see eu.sqooss.service.web.services.WebServices#getProjectVersionsByIds(String, String, long[])
     */
    public WSProjectVersion[] getProjectVersionsByIds(
            String userName,
            String password,
            long[] projectVersionsIds) {
        // Log this call
        logger.info("getProjectVersionsByIds!"
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
                    SEC_VIOLATION + "getProjectVersionsByIds!");
        }
        super.updateUserActivity(userName);

        // Retrieve the result(s)
        // TODO: Use the DAOs instead
        WSProjectVersion[] result = dbWrapper.getProjectVersionsByIds(
                asCollection(projectVersionsIds));

        db.commitDBSession();
        return (WSProjectVersion[]) normalizeWSArrayResult(result);
    }

    /**
     * @see eu.sqooss.service.web.services.WebServices#getProjectVersionsByTimestamps(String, String, long, long[])
     */
    public WSProjectVersion[] getProjectVersionsByTimestamps(
            String userName,
            String password,
            long projectId,
            long[] timestamps) {
        // Log this call
        logger.info("getProjectVersionsByTimestamps!"
                + " user: " + userName
                + ";"
                + " project id: " + projectId
                + ";"
                + " version timestamps: " + Arrays.toString(timestamps));

        // Match against the current security policy
        db.startDBSession();
        if (!securityWrapper.checkProjectsReadAccess(
                userName, password, new long[] {projectId})) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    SEC_VIOLATION + "getProjectVersionsByTimestamps!");
        }
        super.updateUserActivity(userName);

        // Retrieve the result(s)
        ArrayList<WSProjectVersion> result = new ArrayList<WSProjectVersion>();
        StoredProject project = db.findObjectById(
                StoredProject.class, projectId);
        if (project != null) {
            for (long timestamp : timestamps) {
                ProjectVersion version =
                    ProjectVersion.getVersionByTimestamp(project, timestamp);
                if (version != null)
                    result.add(WSProjectVersion.getInstance(version));
            }
        }

        db.commitDBSession();
        return (WSProjectVersion[]) normalizeWSArrayResult(
                result.toArray(new WSProjectVersion[result.size()]));
    }

    /**
     * @see eu.sqooss.service.web.services.WebServices#getProjectVersionsByScmIds(String, String, long, String[])
     */
    public WSProjectVersion[] getProjectVersionsByScmIds(
            String userName,
            String password,
            long projectId,
            String[] scmIds) {
        // Log this call
        logger.info("getProjectVersionsByScmIds!"
                + " user: " + userName
                + ";"
                + " project id: " + projectId
                + ";"
                + " version revisions: " + Arrays.toString(scmIds));

        // Match against the current security policy
        db.startDBSession();
        if (!securityWrapper.checkProjectsReadAccess(
                userName, password, new long[] {projectId})) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    SEC_VIOLATION + "getProjectVersionsByScmIds!");
        }
        super.updateUserActivity(userName);

        // Retrieve the result(s)
        ArrayList<WSProjectVersion> result = new ArrayList<WSProjectVersion>();
        StoredProject project = db.findObjectById(
                StoredProject.class, projectId);
        if (project != null) {
            for (String scmId : scmIds) {
                ProjectVersion version =
                    ProjectVersion.getVersionByRevision(project, scmId);
                if (version != null)
                    result.add(WSProjectVersion.getInstance(version));
            }
        }

        db.commitDBSession();
        return (WSProjectVersion[]) normalizeWSArrayResult(
                result.toArray(new WSProjectVersion[result.size()]));
    }

    /**
     * @see eu.sqooss.service.web.services.WebServices#getVersionsCount(String, String, long)
     */
    public long getVersionsCount(
            String userName,
            String password,
            long projectId) {
        // Log this call
        logger.info("getVersionsCount!"
                + " user: "  + userName
                + ";"
                + " project id: " + projectId);

        // Match against the current security policy
        db.startDBSession();
        if (!securityWrapper.checkProjectsReadAccess(
                userName, password, new long[] {projectId})) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    SEC_VIOLATION + "getVersionsCount!");
        }
        super.updateUserActivity(userName);

        // Retrieve the result(s)
        long result = StoredProject.getVersionsCount(projectId);

        db.commitDBSession();
        return result;
    }

    /**
     * @see eu.sqooss.service.web.services.WebServices#getFirstProjectVersions(String, String, long[])
     */
    public WSProjectVersion[] getFirstProjectVersions(
            String userName,
            String password,
            long[] projectsIds) {
        // Log this call
        logger.info("getFirstProjectVersions!"
                + " user: " + userName
                + ";"
                + " project Ids: " + Arrays.toString(projectsIds));

        // Match against the current security policy
        db.startDBSession();
        if (!securityWrapper.checkProjectsReadAccess(userName, password, projectsIds)) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    SEC_VIOLATION + "getFirstProjectVersions!");
        }
        super.updateUserActivity(userName);

        // Retrieve the result(s)
        List<ProjectVersion> projectVersions = new ArrayList<ProjectVersion>();
        for (long projectId : projectsIds) {
            StoredProject project = db.findObjectById(
                    StoredProject.class, projectId);
            if (project != null) {
                ProjectVersion version =
                    ProjectVersion.getFirstProjectVersion(project);
                if (version != null)
                    projectVersions.add(version);
            }
        }
        WSProjectVersion[] result = WSProjectVersion.asArray(projectVersions);

        db.commitDBSession();
        return (WSProjectVersion[]) normalizeWSArrayResult(result);
    }

    /**
     * @see eu.sqooss.service.web.services.WebServices#getPreviousVersionById(String, String, long)
     */
    public WSProjectVersion getPreviousVersionById(
            String userName,
            String password,
            long versionId) {
        // Log this call
        logger.info("getPreviousVersionById!"
                + " user: " + userName
                + ";"
                + " version Id: " + versionId);

        // Match against the current security policy
        db.startDBSession();
        if (!securityWrapper.checkProjectVersionsReadAccess(
                userName, password, new long[] {versionId})) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    SEC_VIOLATION + "getPreviousVersionById!");
        }
        super.updateUserActivity(userName);

        // Retrieve the result(s)
        WSProjectVersion result = null;
        ProjectVersion version =
            db.findObjectById(ProjectVersion.class, versionId);
        if (version != null) {
            result = WSProjectVersion.getInstance(
                    version.getPreviousVersion());
        }

        db.commitDBSession();
        return result;
    }

    /**
     * @see eu.sqooss.service.web.services.WebServices#getNextVersionById(String, String, long)
     */
    public WSProjectVersion getNextVersionById(
            String userName,
            String password,
            long versionId) {
        // Log this call
        logger.info("getNextVersionById!"
                + " user: " + userName + ";"
                + " version Id: " + versionId);

        // Match against the current security policy
        db.startDBSession();
        if (!securityWrapper.checkProjectVersionsReadAccess(
                userName, password, new long[] {versionId})) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    SEC_VIOLATION + "getNextVersionById!");
        }
        super.updateUserActivity(userName);

        // Retrieve the result(s)
        WSProjectVersion result = null;
        ProjectVersion version =
            db.findObjectById(ProjectVersion.class, versionId);
        if (version != null) {
            result = WSProjectVersion.getInstance(
                    version.getNextVersion());
        }

        db.commitDBSession();
        return result;
    }

    /**
     * @see eu.sqooss.service.web.services.WebServices#getLastProjectVersions(String, String, long[])
     */
    public WSProjectVersion[] getLastProjectVersions(
            String userName,
            String password,
            long[] projectsIds) {
        // Log this call
        logger.info("getLastProjectVersions!"
                + " user: " + userName
                + " project Ids: " + Arrays.toString(projectsIds));

        // Match against the current security policy
        db.startDBSession();
        if (!securityWrapper.checkProjectsReadAccess(userName, password, projectsIds)) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    SEC_VIOLATION + "getLastProjectVersions!");
        }
        super.updateUserActivity(userName);

        // Retrieve the result(s)
        List<ProjectVersion> projectVersions = new ArrayList<ProjectVersion>();
        for (long projectId : projectsIds) {
            StoredProject project = db.findObjectById(
                    StoredProject.class, projectId);
            if (project != null) {
                ProjectVersion version =
                    ProjectVersion.getLastProjectVersion(project);
                if (version != null)
                    projectVersions.add(version);
            }
        }
        WSProjectVersion[] result = WSProjectVersion.asArray(projectVersions);

        db.commitDBSession();
        return (WSProjectVersion[]) normalizeWSArrayResult(result);
    }

    /**
     * @see eu.sqooss.service.web.services.WebServices#getTaggedVersionsByProjectId(String, String, long)
     */
    public WSTaggedVersion[] getTaggedVersionsByProjectId(
            String userName,
            String password,
            long projectId) {
        // Log this call
        logger.info("getTaggedVersionsByProjectId!"
                + " user: " + userName
                + ";"
                + " project id: " + projectId);

        // Match against the current security policy
        db.startDBSession();
        if (!securityWrapper.checkProjectsReadAccess(
                userName, password, new long[] {projectId})) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    SEC_VIOLATION + "getTaggedVersionsByProjectId!");
        }
        super.updateUserActivity(userName);

        // Retrieve the result(s)
        WSTaggedVersion[] result = null;
        StoredProject sp = db.findObjectById(StoredProject.class, projectId);
        if (sp != null)
            result = WSTaggedVersion.asArray(sp.getTaggedVersions());

        db.commitDBSession();
        return (WSTaggedVersion[]) normalizeWSArrayResult(result);
    }

    /**
     * @see eu.sqooss.service.web.services.WebServices#getVersionsStatistics(String, String, long[])
     */
    public WSVersionStats[] getVersionsStatistics(
            String userName,
            String password,
            long[] projectVersionsIds) {
        // Log this call
        logger.info("getVersionsStatistics!"
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
                    SEC_VIOLATION + "getVersionsStatistics!");
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
                    stats.setDeletedCount(nextVersion.getFilesCount(ProjectFileState.deleted()));
                    stats.setModifiedCount(nextVersion.getFilesCount(ProjectFileState.modified()));
                    stats.setAddedCount(nextVersion.getFilesCount(ProjectFileState.added()));
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

    //========================================================================
    // FILE RELATED PROJECT METHODS
    //========================================================================

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
     * @see eu.sqooss.service.web.services.WebServices#getFileGroupsByProjectVersionId(String, String, long)
     */
    public WSFileGroup[] getFileGroupsByProjectVersionId(
            String userName,
            String password,
            long projectVersionId) {
        
        logger.info("Get file groups by project version id! user: " +
        		userName + "; project version id: " + projectVersionId);
        
        db.startDBSession();

        if (!securityWrapper.checkProjectVersionsReadAccess(
                userName, password, new long[] {projectVersionId})) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    "Security violation in the get file groups by project version id operation!");
        }
        
        //TODO: waits for the FileGroup support of the FDS
        
        db.commitDBSession();
        return (WSFileGroup[]) normalizeWSArrayResult(null);
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
        result = WSDirectory.getInstance(Directory.getDirectory("/", false));
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
        //====================================================================
        // Log this call
        //====================================================================
        logger.info("Get files in directory!"
                + " user: " + userName
                +";"
                + " version Id: " + projectVersionId
                +";"
                + " directory Id: " + directoryId);

        //====================================================================
        // Match against the current security policy
        //====================================================================
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

        //====================================================================
        // Retrieve the result(s)
        //====================================================================
        WSProjectFile[] result = null;
        ProjectVersion version =
            db.findObjectById(ProjectVersion.class, projectVersionId);
        Directory directory =
            db.findObjectById(Directory.class, directoryId);
        if ((version == null) || (directory == null)) return null;

        // Retrieve the complete list of files in the selected version
        List<ProjectFile> dirFiles = version.getFiles(directory);
        
        // Construct the result
        if (dirFiles.size() > 0) {
            result = new WSProjectFile[dirFiles.size()];
            int index = 0;
            for (ProjectFile nextFile : dirFiles)
                result[index++] = WSProjectFile.getInstance(nextFile);
        }

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
        logger.info("getFileModifications!"
                + " user: " + userName
                + ";"
                + " version Id: " + projectVersionId
                + ";"
                + " file Id: " + projectFileId);

        // Match against the current security policy
        db.startDBSession();
        if (!securityWrapper.checkProjectVersionsReadAccess(
                userName, password, new long[]{projectVersionId})) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    SEC_VIOLATION + "getFileModifications!");
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
                for (Long verTimestamp : mods.keySet()) {
                    String verRevision = ProjectVersion.getVersionByTimestamp(
                            pf.getProjectVersion().getProject(), verTimestamp)
                            .getRevisionId();
                    result[index++] = new WSFileModification(
                            verTimestamp, mods.get(verTimestamp), verRevision);
                }
            }
        }

        db.commitDBSession();
        return result;
    }

    //========================================================================
    // MAIL RELATED PROJECT METHODS
    //========================================================================

    /**
     * @see eu.sqooss.service.web.services.WebServices#getMailsCount(String, String, long)
     */
    public long getMailsCount(
            String userName,
            String password,
            long projectId) {
        // Log this call
        logger.info("getEmailsCount!"
                + " user: " + userName
                + ";"
                + " project id: " + projectId);

        // Match against the current security policy
        db.startDBSession();
        if (!securityWrapper.checkProjectsReadAccess(
                userName, password, new long[] {projectId})) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    SEC_VIOLATION + "getEmailsCount!");
        }
        super.updateUserActivity(userName);

        // Retrieve the result(s)
        long result = StoredProject.getMailsCount(projectId);

        db.commitDBSession();
        return result;
    }

    //========================================================================
    // BUG RELATED PROJECT METHODS
    //========================================================================

    /**
     * @see eu.sqooss.service.web.services.WebServices#getBugsCount(String, String, long)
     */
    public long getBugsCount(
            String userName,
            String password,
            long projectId) {
        // Log this call
        logger.info("getBugsCount!"
                + " user: " + userName
                + ";"
                + " project id: " + projectId);

        // Match against the current security policy
        db.startDBSession();
        if (!securityWrapper.checkProjectsReadAccess(
                userName, password, new long[] {projectId})) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    SEC_VIOLATION + "getBugsCount!");
        }
        super.updateUserActivity(userName);

        // Retrieve the result(s)
        long result = StoredProject.getBugsCount(projectId);

        db.commitDBSession();
        return result;
    }

    //========================================================================
    // TIMELINE RELATED PROJECT METHODS
    //========================================================================

    public WSProjectVersion[] getSCMTimeline(String userName, String password,
            long projectId, long tsmFrom, long tsmTill) {
        // Log this call
        logger.info("getSCMTimeline!"
                + " user: " + userName
                + ";"
                + " project id: " + projectId);

        // Match against the current security policy
        db.startDBSession();
        if (!securityWrapper.checkProjectsReadAccess(
                userName, password, new long[] {projectId})) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    SEC_VIOLATION + "getSCMTimeline!");
        }
        super.updateUserActivity(userName);

        // Retrieve the result(s)
        WSProjectVersion[] result = null;
        StoredProject sp = db.findObjectById(StoredProject.class, projectId);
        if (sp != null) {
            Calendar calFrom = Calendar.getInstance();
            calFrom.setTimeInMillis(tsmFrom);
            Calendar calTill = Calendar.getInstance();
            calTill.setTimeInMillis(tsmTill);
            Timeline prjTimeline = fds.getTimeline(sp);
            // Retrieve the list of events in the given time period
            SortedSet<ProjectEvent> timeline = prjTimeline.getTimeLine(
                    calFrom, calTill, ResourceType.SCM);
            List<ProjectVersion> resources = new ArrayList<ProjectVersion>();
            for (ProjectEvent nextEvent : timeline)
                resources.add((ProjectVersion) nextEvent.getAssociatedDAO());
            result = WSProjectVersion.asArray(resources);
        }

        db.commitDBSession();
        return (WSProjectVersion[]) normalizeWSArrayResult(result);
    }

    public WSShortProjectVersion[] getShortSCMTimeline(String userName,
            String password, long projectId, long tsmFrom, long tsmTill) {
        // Log this call
        logger.info("getShortSCMTimeline!"
                + " user: " + userName
                + ";"
                + " project id: " + projectId);

        // Match against the current security policy
        db.startDBSession();
        if (!securityWrapper.checkProjectsReadAccess(
                userName, password, new long[] {projectId})) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    SEC_VIOLATION + "getShortSCMTimeline!");
        }
        super.updateUserActivity(userName);

        // Retrieve the result(s)
        WSShortProjectVersion[] result = null;
        StoredProject sp = db.findObjectById(StoredProject.class, projectId);
        if (sp != null) {
            Calendar calFrom = Calendar.getInstance();
            calFrom.setTimeInMillis(tsmFrom);
            Calendar calTill = Calendar.getInstance();
            calTill.setTimeInMillis(tsmTill);
            Timeline prjTimeline = fds.getTimeline(sp);
            // Retrieve the list of events in the given time period
            SortedSet<ProjectEvent> timeline = prjTimeline.getTimeLine(
                    calFrom, calTill, ResourceType.SCM);
            List<ProjectVersion> resources = new ArrayList<ProjectVersion>();
            for (ProjectEvent nextEvent : timeline)
                resources.add((ProjectVersion) nextEvent.getAssociatedDAO());
            result = WSShortProjectVersion.asArray(resources);
        }

        db.commitDBSession();
        return (WSShortProjectVersion[]) normalizeWSArrayResult(result);
    }

    public WSMailMessage[] getMailTimeline(String userName, String password,
            long projectId, long tsmFrom, long tsmTill) {
        // Log this call
        logger.info("getMailTimeline!"
                + " user: " + userName
                + ";"
                + " project id: " + projectId);

        // Match against the current security policy
        db.startDBSession();
        if (!securityWrapper.checkProjectsReadAccess(
                userName, password, new long[] {projectId})) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    SEC_VIOLATION + "getMailTimeline!");
        }
        super.updateUserActivity(userName);

        // Retrieve the result(s)
        WSMailMessage[] result = null;
        StoredProject sp = db.findObjectById(StoredProject.class, projectId);
        if (sp != null) {
            Calendar calFrom = Calendar.getInstance();
            calFrom.setTimeInMillis(tsmFrom);
            Calendar calTill = Calendar.getInstance();
            calTill.setTimeInMillis(tsmTill);
            Timeline prjTimeline = fds.getTimeline(sp);
            // Retrieve the list of events in the given time period
            SortedSet<ProjectEvent> timeline = prjTimeline.getTimeLine(
                    calFrom, calTill, ResourceType.MAIL);
            List<MailMessage> resources = new ArrayList<MailMessage>();
            for (ProjectEvent nextEvent : timeline)
                resources.add((MailMessage) nextEvent.getAssociatedDAO());
            result = WSMailMessage.asArray(resources);
        }

        db.commitDBSession();
        return (WSMailMessage[]) normalizeWSArrayResult(result);
    }

    public WSShortMailMessage[] getShortMailTimeline(String userName,
            String password, long projectId, long tsmFrom, long tsmTill) {
        // Log this call
        logger.info("getShortMailTimeline!"
                + " user: " + userName
                + ";"
                + " project id: " + projectId);

        // Match against the current security policy
        db.startDBSession();
        if (!securityWrapper.checkProjectsReadAccess(
                userName, password, new long[] {projectId})) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    SEC_VIOLATION + "getShortMailTimeline!");
        }
        super.updateUserActivity(userName);

        // Retrieve the result(s)
        WSShortMailMessage[] result = null;
        StoredProject sp = db.findObjectById(StoredProject.class, projectId);
        if (sp != null) {
            Calendar calFrom = Calendar.getInstance();
            calFrom.setTimeInMillis(tsmFrom);
            Calendar calTill = Calendar.getInstance();
            calTill.setTimeInMillis(tsmTill);
            Timeline prjTimeline = fds.getTimeline(sp);
            // Retrieve the list of events in the given time period
            SortedSet<ProjectEvent> timeline = prjTimeline.getTimeLine(
                    calFrom, calTill, ResourceType.MAIL);
            List<MailMessage> resources = new ArrayList<MailMessage>();
            for (ProjectEvent nextEvent : timeline)
                resources.add((MailMessage) nextEvent.getAssociatedDAO());
            result = WSShortMailMessage.asArray(resources);
        }

        db.commitDBSession();
        return (WSShortMailMessage[]) normalizeWSArrayResult(result);
    }

    public WSShortBug[] getShortBugTimeline(String userName,
            String password, long projectId, long tsmFrom, long tsmTill) {
        // Log this call
        logger.info("getShortBugTimeline!"
                + " user: " + userName
                + ";"
                + " project id: " + projectId);

        // Match against the current security policy
        db.startDBSession();
        if (!securityWrapper.checkProjectsReadAccess(
                userName, password, new long[] {projectId})) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    SEC_VIOLATION + "getShortBugTimeline!");
        }
        super.updateUserActivity(userName);

        // Retrieve the result(s)
        WSShortBug[] result = null;
        StoredProject sp = db.findObjectById(StoredProject.class, projectId);
        if (sp != null) {
            Calendar calFrom = Calendar.getInstance();
            calFrom.setTimeInMillis(tsmFrom);
            Calendar calTill = Calendar.getInstance();
            calTill.setTimeInMillis(tsmTill);
            Timeline prjTimeline = fds.getTimeline(sp);
            // Retrieve the list of events in the given time period
            SortedSet<ProjectEvent> timeline = prjTimeline.getTimeLine(
                    calFrom, calTill, ResourceType.BTS);
            List<Bug> resources = new ArrayList<Bug>();
            for (ProjectEvent nextEvent : timeline) {
                Bug dao = (Bug) nextEvent.getAssociatedDAO();
                if (dao.getStatus().getBugStatus() == Status.NEW)
                    resources.add(dao);
            }
            result = WSShortBug.asArray(resources);
        }

        db.commitDBSession();
        return (WSShortBug[]) normalizeWSArrayResult(result);
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
