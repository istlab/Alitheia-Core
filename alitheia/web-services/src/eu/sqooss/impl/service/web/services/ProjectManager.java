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

import java.math.BigInteger;
import java.util.List;

import eu.sqooss.impl.service.web.services.datatypes.WSProjectFile;
import eu.sqooss.impl.service.web.services.datatypes.WSProjectVersion;
import eu.sqooss.impl.service.web.services.datatypes.WSStoredProject;
import eu.sqooss.impl.service.web.services.utils.ProjectManagerDatabase;
import eu.sqooss.impl.service.web.services.utils.SecurityWrapper;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.security.SecurityManager;

public class ProjectManager extends AbstractManager {
    
    private Logger logger;
    private TDSService tds;
    private ProjectManagerDatabase dbWrapper;
    private SecurityWrapper securityWrapper;
    
    public ProjectManager(Logger logger, DBService db, TDSService tds, SecurityManager security) {
        super(db);
        this.logger = logger;
        this.tds = tds;
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
        
        db.startDBSession();
        List<?> projects = dbWrapper.getEvaluatedProjects();
        WSStoredProject[] wsSP = convertToWSStoredProject(projects);
        db.commitDBSession();
        return wsSP;
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getStoredProjects(String, String)
     */
    public WSStoredProject[] getStoredProjects(String userName, String password) {
        logger.info("Gets the stored project list! user: " + userName);

        securityWrapper.checkDBReadAccess(userName, password);
        
        super.updateUserActivity(userName);
    
        db.startDBSession();
        List queryResult = dbWrapper.getStoredProjects();

        List<StoredProject> l = (List<StoredProject>) queryResult;
        if (l==null) {
            logger.warn("Stored project query is broken.");
            db.rollbackDBSession();
            return null;
        } else {
            WSStoredProject[] wsSP = convertToWSStoredProject(l);
            db.commitDBSession();
            return wsSP;
        }
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#requestEvaluation4Project(String, String, String, int, String, String, String, String, String)
     */
    public WSStoredProject requestEvaluation4Project(String userName, String password,
            String projectName, long projectVersion,
            String srcRepositoryLocation, String mailingListLocation,
            String BTSLocation, String userEmailAddress, String website) {
        logger.info("Request evaluation for project! user: " + userName +
                "; project name: " + projectName + "; projectVersion: " + projectVersion +
                ";\n source repository: " + srcRepositoryLocation +
                "; mailing list: " + mailingListLocation +
                ";\n BTS: " + BTSLocation + "; user's e-mail: " + userEmailAddress +
                "; website: " + website);
        
        
        securityWrapper.checkDBWriteAccess(userName, password);
        
        super.updateUserActivity(userName);
        
        db.startDBSession();
        List<?> projects = dbWrapper.getStoredProjects(projectName, projectVersion);
        
        if (projects.size() == 0) {
            StoredProject newStoredProject = new StoredProject();
            newStoredProject.setBugs(BTSLocation);
            newStoredProject.setContact(userEmailAddress);
            newStoredProject.setMail(mailingListLocation);
            newStoredProject.setName(projectName);
            newStoredProject.setRepository(srcRepositoryLocation);
            newStoredProject.setWebsite(website);
            long newStoredProjectId;
            
            ProjectVersion newProjectVersion = new ProjectVersion();
            newProjectVersion.setVersion(projectVersion);
            
            newStoredProjectId = dbWrapper.createNewProject(newStoredProject, newProjectVersion);
            db.commitDBSession();
            if (!tds.projectExists(newStoredProjectId)) {
                tds.addAccessor(newStoredProjectId, projectName, BTSLocation,
                        mailingListLocation, srcRepositoryLocation);
            }
            return new WSStoredProject(newStoredProject);
        } else if (projects.size() == 1) {
            WSStoredProject wsp = convertToWSStoredProject(projects)[0];
            db.commitDBSession();
            return wsp;
        } else {
            String message = "The database contains more than 1 project! name:" + 
            projectName + "; version: " + projectVersion;
            logger.warn(message);
            db.rollbackDBSession();
            throw new RuntimeException(message);
        }
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getProjectIdByName(String, String, String)
     */
    public long getProjectIdByName(String userName, String password, String projectName) {

        logger.info("Retrieve project id! user: " + userName +
                "; project name: " + projectName);

        db.startDBSession();
        List<StoredProject> projects = dbWrapper.getStoredProjects(projectName);
        db.commitDBSession();
                
        if (projects.size() != 0) {
            long projectId = projects.get(0).getId();
            securityWrapper.checkProjectReadAccess(userName, password, projectId);
            super.updateUserActivity(userName);
            return projectId;
        } else {
            throw new IllegalArgumentException("Can't find the project with name: " + projectName);
        }
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getProjectVersionsByProjectId(String, String, long)
     */
    public WSProjectVersion[] getProjectVersionsByProjectId(String userName, String password, long projectId) {

        logger.info("Retrieve stored project versions! user: " + userName +
                "; project's id: " + projectId);

        securityWrapper.checkProjectReadAccess(userName, password, projectId);

        super.updateUserActivity(userName);
        
        db.startDBSession();
        StoredProject storedProject = dbWrapper.getProjectById(projectId);

        if (storedProject != null) {
            List<ProjectVersion> projectVersions = storedProject.getProjectVersions();
            WSProjectVersion[] wspv = convertToWSProjectVersion(projectVersions);
            db.commitDBSession();
            return wspv;
        } else {
            db.rollbackDBSession();
            return null;
        }

    }
    
    /**
     *  @see eu.sqooss.service.web.services.WebServices#getProjectById(String, String, long)
     */
    public WSStoredProject getProjectById(String userName, String password, long projectId) {

        logger.info("Retrieve stored project! user: " + userName +
                "; project's id: " + projectId );

        securityWrapper.checkProjectReadAccess(userName, password, projectId);

        super.updateUserActivity(userName);
        
        db.startDBSession();
        StoredProject storedProject= dbWrapper.getProjectById(projectId);
        
        if (storedProject != null) {
            WSStoredProject wsp = new WSStoredProject(storedProject);
            db.commitDBSession();
            return wsp;
        } else {
            db.rollbackDBSession();
            return null;
        }

    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getFilesByProjectId(String, String, String)
     */
    public WSProjectFile[] getFilesByProjectId(String userName, String password, long projectId) {
        logger.info("Retrieve file list! user: " + userName + "; project id: " + projectId);

        securityWrapper.checkProjectReadAccess(userName, password, projectId);

        super.updateUserActivity(userName);
        
        db.startDBSession();
        List<?> queryResult = dbWrapper.getFilesByProjectId(projectId);

        WSProjectFile[] wspf = convertToWSProjectFiles(queryResult);
        db.commitDBSession();
        return wspf;
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getFilesByProjectVersionId(String, String, long)
     */
    public WSProjectFile[] getFilesByProjectVersionId(String userName, String password, long projectVersionId) {
        logger.info("Get file list for project version! user: " + userName +
                "; project version id: " + projectVersionId);
        
        securityWrapper.checkProjectVersionReadAccess(userName, password, projectVersionId);
        
        super.updateUserActivity(userName);
        
        db.startDBSession();
        List<?> queryResult = dbWrapper.getFilesByProjectVersionId(projectVersionId);
        
        WSProjectFile[] wspf = convertToWSProjectFiles(queryResult);
        db.commitDBSession();
        return wspf;
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getFilesNumberByProjectVersionId(String, String, long)
     */
    public long getFilesNumberByProjectVersionId(String userName, String password, long projectVersionId) {
        logger.info("Get files's number for project version! user: " + userName +
                "; project version id: " + projectVersionId);
        
        securityWrapper.checkProjectVersionReadAccess(userName, password, projectVersionId);
        
        super.updateUserActivity(userName);
        
        db.startDBSession();
        List<?> queryResult = dbWrapper.getFilesNumberByProjectVersionId(projectVersionId);
        db.commitDBSession();
        return ((BigInteger)queryResult.get(0)).longValue();
    }
    
    private WSProjectFile[] convertToWSProjectFiles(List<?> projectFiles) {
        WSProjectFile[] result = null;
        if ((projectFiles != null) && (projectFiles.size() != 0)) {
            result = new WSProjectFile[projectFiles.size()];
            Object currentElem = projectFiles.get(0);
            if (currentElem instanceof ProjectFile) { //parse HQL
                for (int i = 0; i < result.length; i++) {
                    currentElem = projectFiles.get(i);
                    result[i] = new WSProjectFile((ProjectFile) currentElem);
                }
            } else if (currentElem.getClass().isArray()) { //parse SQL
                BigInteger fileId;
                BigInteger projectVersion;
                String fileName;
                String status;
                Boolean isDirectory;
                Object[] currentFile;
                for (int i = 0; i < result.length; i++) {
                    currentFile = (Object[])projectFiles.get(i);
                    fileId = (BigInteger)currentFile[0];
                    fileName = (String)currentFile[1];
                    projectVersion = (BigInteger)currentFile[2];
                    status = (String)currentFile[3];
                    isDirectory = (Boolean)currentFile[4];
                    result[i] = new WSProjectFile(fileId.longValue(), fileName,
                            projectVersion.longValue(), status, isDirectory);
                }
            }
        }
        return result;
    }
    
    private WSProjectVersion[] convertToWSProjectVersion(List<?> projectVersions) {
        WSProjectVersion[] result = null;
        if ((projectVersions != null) && (projectVersions.size() != 0)) {
            result = new WSProjectVersion[projectVersions.size()];
            ProjectVersion currentElem;
            for (int i = 0; i < result.length; i++) {
                currentElem = (ProjectVersion) projectVersions.get(i);
                result[i] = new WSProjectVersion(currentElem);
            }
        }
        return result;
    }
    
    private WSStoredProject[] convertToWSStoredProject(List<?> storedProjects) {
        WSStoredProject[] result = null;
        if ((storedProjects != null) && (storedProjects.size() != 0)) {
            result = new WSStoredProject[storedProjects.size()];
            StoredProject currentElem;
            for (int i = 0; i < result.length; i++) {
                currentElem = (StoredProject) storedProjects.get(i);
                result[i] = new WSStoredProject(currentElem);
            }
        }
        return result;
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
