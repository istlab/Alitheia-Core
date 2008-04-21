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

package eu.sqooss.service.web.services;

import org.osgi.framework.BundleContext;

import eu.sqooss.impl.service.web.services.MetricManager;
import eu.sqooss.impl.service.web.services.ProjectManager;
import eu.sqooss.impl.service.web.services.UserManager;
import eu.sqooss.impl.service.web.services.datatypes.WSMetric;
import eu.sqooss.impl.service.web.services.datatypes.WSMetricMeasurement;
import eu.sqooss.impl.service.web.services.datatypes.WSProjectFile;
import eu.sqooss.impl.service.web.services.datatypes.WSProjectVersion;
import eu.sqooss.impl.service.web.services.datatypes.WSStoredProject;
import eu.sqooss.impl.service.web.services.datatypes.WSUser;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.security.SecurityManager;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.webadmin.WebadminService;

/* 
 * IMPORTANT NOTES:
 * 
 * 1. The WebServices's implementation and the data types are specially in this form.
 * The Axis2's wsdl generator is the reason.
 * It doesn't work correct with the interfaces, the abstract classes and the inheritance.
 * 
 * 2. java2wsdl doesn't support methods overloading.
 * 
 * 3. The returned arrays must not be empty because the client can't parse them.
 * The method can return null then the client receives the array with null element ([null]).
 */

/**
 * Web services service exports a single interface with all required function calls.
 * The URL is: http:/.../[web.service.context]/services/[web.service.name]
 * The wsdl file is: http:/.../[web.service.context]/services/[web.service.name]?wsdl
 */
public class WebServices {
    
    private MetricManager metricManager;
    private ProjectManager projectManager;
    private UserManager userManager;
    private WebadminService webadmin;
    
    public WebServices(BundleContext bc, SecurityManager securityManager,
            DBService db, TDSService tds, Logger logger, WebadminService wa) {
        metricManager = new MetricManager(logger, db, securityManager);
        projectManager = new ProjectManager(logger, db, tds, securityManager);
        userManager = new UserManager(securityManager, db);
        webadmin = wa;
    }
    
    //5.1.1
    /**
     * This method returns evaluated projects.
     * The user's name and password must be valid. 
     * @param userName
     * @param password
     * @return
     */
    public WSStoredProject[] evaluatedProjectsList(String userName, String password) {
        return projectManager.evaluatedProjectsList(userName, password);
    }
    
    public WSStoredProject[] storedProjectsList(String userName, String password) {
        return projectManager.storedProjectsList(userName, password);
    }
    
    /**
     * This method returns the metrics for a given project.
     * The user's name and password must be valid.
     * @param userName
     * @param password
     * @param projectId
     * @return
     */
    public WSMetric[] retrieveMetrics4SelectedProject(String userName,
            String password, long projectId) {
        return metricManager.retrieveMetrics4SelectedProject(userName, password, projectId);
    }
    
    /**
     * This method returns the metric with a given id.
     * The user's name and password must be valid.
     * @param userName
     * @param password
     * @param projectId
     * @param metricId
     * @return
     */
    public WSMetric retrieveSelectedMetric(String userName, String password,
            long projectId, long metricId) {
        return metricManager.retrieveSelectedMetric(userName, password, projectId, metricId);
    }
    
    /**
     * This method returns all installed metrics.
     * @return
     */
    public WSMetric[] getMetrics(String userName, String password) {
        return metricManager.getMetrics(userName, password);
    }
    //5.1.1
    
    //5.1.2
    /**
     * This method returns the project's files.
     * The user's name and password must be valid.
     * @param userName
     * @param password
     * @param projectId
     * @return
     */
    public WSProjectFile[] retrieveFileList(String userName, String password, long projectId) {
        return projectManager.retrieveFileList(userName, password, projectId);
    }
    
    /**
     * This method returns the metrics for a given files.
     * All files in the folder can be selected with the folder's name.
     * The user's name and password must be valid.
     * @param userName
     * @param password
     * @param projectId
     * @param folders
     * @param fileNames
     * @return
     */
    public WSMetric[] retrieveMetrics4SelectedFiles(String userName, String password,
            long projectId, String[] folders, String[] fileNames) {
        return metricManager.retrieveMetrics4SelectedFiles(userName, password, projectId, folders, fileNames);
    }
    //5.1.2
    
    //5.1.3
    /**
     * This method makes request for OSS project evaluation.
     * If a project with same name and version is known to the system
     * then the method returns the existent project.
     *  
     * @param userName for an authentication
     * @param password for an authentication 
     * @param projectName project's name
     * @param projectVersion project's version
     * @param srcRepositoryLocation URL for the source repository
     * @param mailingListLocation URL for the mailing list
     * @param BTSLocation URL for the bug tracking system
     * @param userEmailAddress user's e-mail address
     * @param website project's website
     * @return the project or old one if exist
     */
    public WSStoredProject requestEvaluation4Project(String userName, String password,
            String projectName, long projectVersion,
            String srcRepositoryLocation, String mailingListLocation,
            String BTSLocation, String userEmailAddress,String website) {
        return projectManager.requestEvaluation4Project(userName, password,
                projectName,projectVersion, srcRepositoryLocation,
                mailingListLocation, BTSLocation, userEmailAddress, website);
    }
    //5.1.3
    
    
    //5.1.10
    /**
     * This method creates a new user.
     */
    public WSUser submitUser(String userNameForAccess, String passwordForAccess,
            String newUserName, String newPassword, String email) {
        return userManager.submitUser(userNameForAccess, passwordForAccess,
                newUserName, newPassword, email);
    }
    
    public boolean submitPendingUser(String userNameForAccess, String passwordForAccess,
            String newUserName, String newPassword, String email) {
        return userManager.submitPendingUser(userNameForAccess, passwordForAccess,
                newUserName, newPassword, email);
    }
    //5.1.10
    
    //5.1.11
    /**
     * This method returns information about the user with a given id.
     * 
     * @param userNameForAccess
     * @param passwordForAccess
     * @param userId
     * @return
     */
    public WSUser displayUser(String userNameForAccess, String passwordForAccess,
            long userId) {
        return userManager.displayUser(userNameForAccess, passwordForAccess, userId);
    }
    
    /**
     * This method returns information about the user with a given user name.
     * 
     * @param userNameForAccess
     * @param passwordForAccess
     * @param userId
     * @return
     */
    public WSUser getUserByName(
            String userNameForAccess,
            String passwordForAccess,
            String userName) {
        return userManager.getUserByName(
                userNameForAccess, passwordForAccess, userName);
    }
    
    /**
     * This method modifies the existent user with a given user name.
     */
    public boolean modifyUser(String userNameForAccess, String passwordForAccess,
            String userName, String newPassword, String newEmail) {
        return userManager.modifyUser(userNameForAccess, passwordForAccess,
                userName, newPassword, newEmail);
    }
    
    /**
     * This method deletes the user with a given id.
     * 
     * @param userNameForAccess
     * @param passwordForAccess
     * @param userId
     */
    public boolean deleteUser(String userNameForAccess, String passwordForAccess, long userId) {
        return userManager.deleteUser(userNameForAccess, passwordForAccess, userId);
    }
    //5.1.11
    
    //retrieve methods
    /**
     * This method retrieves the project's identifier.
     * 
     * @param userName
     * @param passwrod
     * @param projectName - the name of the project as stored in the SQO-OSS.
     * @return
     */
    public long retrieveProjectId(String userName, String password, String projectName) {
        return projectManager.retrieveProjectId(userName, password, projectName);
    }
    
    public WSProjectVersion[] retrieveStoredProjectVersions(String userName, String password, long projectId) {
        return projectManager.retrieveStoredProjectVersions(userName, password, projectId);
    }
    
    public WSStoredProject retrieveStoredProject(String userName, String password, long projectId) {
        return projectManager.retrieveStoredProject(userName, password, projectId);
    }
    
    public WSProjectFile[] getFileList4ProjectVersion(String userName, String password, long projectVersionId) {
        return projectManager.getFileList4ProjectVersion(userName, password, projectVersionId);
    }
    
    public long getFilesNumber4ProjectVersion(String userName, String password, long projectVersionId) {
        return projectManager.getFilesNumber4ProjectVersion(userName, password, projectVersionId);
    }
    //retrieve methods
    
    //metric results
    public WSMetricMeasurement[] getProjectFileMetricMeasurement(String userName, String password,
            long metricId, long projectFileId) {
    	return metricManager.getProjectFileMetricMeasurement(userName, password, metricId, projectFileId);
    }
    
    public WSMetricMeasurement[] getProjectVersionMetricMeasurement(String userName, String password,
            long metricId, long projectVersionId) {
    	return metricManager.getProjectVersionMetricMeasurement(userName, password, metricId, projectVersionId);
    }
    //metric results
    
    public String getUserMessageOfTheDay(String userName) {
        String s = null;
        if (webadmin != null) {
            s = webadmin.getMessageOfTheDay();
        } else {
            s = "No connection to MOTD server.";
        }
        if (userName.length() < 3 /* inches ? */) {
            return "Expand your unit, " + userName;
        }
        if (s != null) {
            return s;
        }
        return "Share and enjoy.";
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
