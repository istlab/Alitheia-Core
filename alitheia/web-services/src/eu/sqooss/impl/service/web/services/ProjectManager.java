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

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

import eu.sqooss.impl.service.web.services.datatypes.WSDeveloper;
import eu.sqooss.impl.service.web.services.datatypes.WSDirectory;
import eu.sqooss.impl.service.web.services.datatypes.WSFileGroup;
import eu.sqooss.impl.service.web.services.datatypes.WSProjectFile;
import eu.sqooss.impl.service.web.services.datatypes.WSProjectVersion;
import eu.sqooss.impl.service.web.services.datatypes.WSStoredProject;
import eu.sqooss.impl.service.web.services.utils.ProjectManagerDatabase;
import eu.sqooss.impl.service.web.services.utils.SecurityWrapper;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.security.SecurityManager;

public class ProjectManager extends AbstractManager {
    
    private Logger logger;
    private ProjectManagerDatabase dbWrapper;
    private SecurityWrapper securityWrapper;
    
    public ProjectManager(Logger logger, DBService db, SecurityManager security) {
        super(db);
        this.logger = logger;
        this.dbWrapper = new ProjectManagerDatabase(db);
        this.securityWrapper = new SecurityWrapper(security);
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getEvaluatedProjects(String, String)
     */
    public WSStoredProject[] getEvaluatedProjects(String userName, String password) {
        logger.info("Gets the evaluated project list! user: " + userName);

        securityWrapper.checkDBReadAccess(userName, password);
        
        super.updateUserActivity(userName);
        
        WSStoredProject[] result = dbWrapper.getEvaluatedProjects();
        return (WSStoredProject[]) normalizeWSArrayResult(result);
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getStoredProjects(String, String)
     */
    public WSStoredProject[] getStoredProjects(String userName, String password) {
        logger.info("Gets the stored project list! user: " + userName);

        securityWrapper.checkDBReadAccess(userName, password);
        
        super.updateUserActivity(userName);
    
        WSStoredProject[] result = dbWrapper.getStoredProjects(new Hashtable<String, Object>());
        return (WSStoredProject[]) normalizeWSArrayResult(result);
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getProjectByName(String, String, String)
     */
    public WSStoredProject getProjectByName(String userName, String password, String projectName) {

        logger.info("Retrieve project! user: " + userName +
                "; project name: " + projectName);

        //TODO: check the security
        
        super.updateUserActivity(userName);
        
        Map<String, Object> properties = new Hashtable<String, Object>(1);
        properties.put("name", projectName);
        WSStoredProject[] projects = dbWrapper.getStoredProjects(properties);
        if ((projects != null) && (projects.length != 0)) {
            return projects[0];
        } else {
            return null;
        }
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getProjectVersionsByProjectId(String, String, long)
     */
    public WSProjectVersion[] getProjectVersionsByProjectId(String userName, String password, long projectId) {

        logger.info("Retrieve stored project versions! user: " + userName +
                "; project's id: " + projectId);

        securityWrapper.checkProjectsReadAccess(
                userName, password, new long[] {projectId});

        super.updateUserActivity(userName);
        
        WSProjectVersion[] result = dbWrapper.getProjectVersionsByProjectId(projectId);
        return (WSProjectVersion[]) normalizeWSArrayResult(result);
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getProjectVersionsByIds(String, String, long[])
     */
    public WSProjectVersion[] getProjectVersionsByIds(String userName,
            String password, long[] projectVersionsIds) {

        logger.info("Retrieve project versions! user: " + userName +
                "; project versions' ids: " + Arrays.toString(projectVersionsIds));

        securityWrapper.checkProjectVersionsReadAccess(
                userName, password, projectVersionsIds);

        super.updateUserActivity(userName);

        WSProjectVersion[] result = dbWrapper.getProjectVersionsByIds(
                asCollection(projectVersionsIds));
        return (WSProjectVersion[]) normalizeWSArrayResult(result);
    }
    
    /**
     *  @see eu.sqooss.service.web.services.WebServices#getProjectsByIds(String, String, long[])
     */
    public WSStoredProject[] getProjectsByIds(String userName, String password, long[] projectsIds) {

        logger.info("Retrieve stored projects! user: " + userName +
                "; projects' ids: " + Arrays.toString(projectsIds) );

        securityWrapper.checkProjectsReadAccess(userName, password, projectsIds);

        super.updateUserActivity(userName);
        
        WSStoredProject[] result = dbWrapper.getProjectsByIds(asCollection(projectsIds));
        return (WSStoredProject[]) normalizeWSArrayResult(result);
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getFilesByProjectId(String, String, String)
     */
    public WSProjectFile[] getFilesByProjectId(String userName, String password, long projectId) {
        logger.info("Retrieve file list! user: " + userName + "; project id: " + projectId);

        securityWrapper.checkProjectsReadAccess(
                userName, password, new long[] {projectId});

        super.updateUserActivity(userName);
        
        WSProjectFile[] result = dbWrapper.getFilesByProjectId(projectId);
        return (WSProjectFile[]) normalizeWSArrayResult(result);
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getFilesByProjectVersionId(String, String, long)
     */
    public WSProjectFile[] getFilesByProjectVersionId(String userName, String password, long projectVersionId) {
        logger.info("Get file list for project version! user: " + userName +
                "; project version id: " + projectVersionId);
        
        securityWrapper.checkProjectVersionsReadAccess(
                userName, password, new long[] {projectVersionId});
        
        super.updateUserActivity(userName);
        
        WSProjectFile[] result = dbWrapper.getFilesByProjectVersionId(projectVersionId);
        return (WSProjectFile[]) normalizeWSArrayResult(result);
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getFileGroupsByProjectId(String, String, long)
     */
    public WSFileGroup[] getFileGroupsByProjectId(String userName,
            String password, long projectId) {
        logger.info("Get a file group list for the project! user: " + userName +
                "; project id: " + projectId);
        
        securityWrapper.checkProjectsReadAccess(
                userName, password, new long[] {projectId});
        
        super.updateUserActivity(userName);
        
        WSFileGroup[] result = dbWrapper.getFileGroupsByProjectId(projectId);
        return (WSFileGroup[]) normalizeWSArrayResult(result);
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getFilesNumberByProjectVersionId(String, String, long)
     */
    public long getFilesNumberByProjectVersionId(String userName, String password, long projectVersionId) {
        logger.info("Get files's number for project version! user: " + userName +
                "; project version id: " + projectVersionId);
        
        securityWrapper.checkProjectVersionsReadAccess(
                userName, password, new long[] {projectVersionId});
        
        super.updateUserActivity(userName);
        
        return dbWrapper.getFilesNumberByProjectVersionId(projectVersionId);
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getFilesNumberByProjectId(String, String, long)
     */
    public long getFilesNumberByProjectId(String userName, String password, long projectId) {
        logger.info("Get file's number for project! user: " + userName +
                "; project id: " + projectId);
        
        securityWrapper.checkProjectsReadAccess(
                userName, password, new long[] {projectId});
        
        super.updateUserActivity(userName);
        
        return dbWrapper.getFilesNumberByProjectId(projectId);
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getDirectoriesByIds(String, String, long[])
     */
    public WSDirectory[] getDirectoriesByIds(String userName, String password,
            long[] directoriesIds) {
        logger.info("Get directories by ids! user: " + userName +
                "; directories' ids: " + Arrays.toString(directoriesIds));
        
        securityWrapper.checkDBReadAccess(userName, password);
        
        super.updateUserActivity(userName);
        
        WSDirectory[] result = dbWrapper.getDirectoriesByIds(
                asCollection(directoriesIds));
        return (WSDirectory[]) normalizeWSArrayResult(result);
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getDevelopersByIds(String, String, long[])
     */
    public WSDeveloper[] getDevelopersByIds(String userName, String password,
            long[] developersIds) {
        logger.info("Get developers by ids! useR: " + userName +
                "; developers' ids: " + Arrays.toString(developersIds));
        
        securityWrapper.checkDBReadAccess(userName, password);
        
        super.updateUserActivity(userName);
        
        WSDeveloper[] result = dbWrapper.getDevelopersByIds(
                asCollection(developersIds));
        return (WSDeveloper[]) normalizeWSArrayResult(result);
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
