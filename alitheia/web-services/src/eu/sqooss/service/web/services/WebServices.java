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
    
    /**
     * This method returns evaluated projects.
     * The user's name and password must be valid. 
     * @param userName
     * @param password
     * @return
     */
    public WSStoredProject[] getEvaluatedProjects(String userName, String password) {
        return projectManager.getEvaluatedProjects(userName, password);
    }
    
    /**
     * This method returns stored projects.
     * They are all projects in the system.
     * @param userName
     * @param password
     * @return the list of stored projects,
     * if there are not stored projects then
     * the method returns an array with null element ([null]).
     */
    public WSStoredProject[] getStoredProjects(String userName, String password) {
        return projectManager.getStoredProjects(userName, password);
    }
    
    /**
     * This method returns the project's files.
     * The user's name and password must be valid.
     * @return the list of files, 
     * if there are not files for the project then
     * the method returns an array with null element ([null]).
     * For example, if the project is stored but not evaluated.
     */
    public WSProjectFile[] getFilesByProjectId(String userName, String password,
            long projectId) {
        return projectManager.getFilesByProjectId(userName, password, projectId);
    }
    
    /**
     * The method returns the files of the specific project version.
     * @return the list of project version files 
     * if there are not files for the project version then
     * the method returns an array with null element ([null]).
     */
    public WSProjectFile[] getFilesByProjectVersionId(
            String userName, String password, long projectVersionId) {
        return projectManager.getFilesByProjectVersionId(
                userName, password, projectVersionId);
    }
    
    /**
     * This method returns the project id. 
     * @param userName
     * @param passwrod
     * @param projectName - the name of the project as stored in the SQO-OSS.
     * @return the identifier of the project
     * @throws IllegalArgumentException -
     * if the system cannot find a project
     */
    public long getProjectIdByName(String userName,
            String password, String projectName) {
        return projectManager.getProjectIdByName(
                userName, password, projectName);
    }
    
    /**
     * The method returns the versions of the project.
     * The project is represented with its identifier.
     * @return the list of project versions 
     * if there are not versions for the project then
     * the method returns an array with null element ([null]).
     */
    public WSProjectVersion[] getProjectVersionsByProjectId(
            String userName, String password, long projectId) {
        return projectManager.getProjectVersionsByProjectId(
                userName, password, projectId);
    }
    
    /**
     * The method returns information about specific project.
     * The project is represented with its identifier. 
     */
    public WSStoredProject getProjectById(
            String userName, String password, long projectId) {
        return projectManager.getProjectById(userName, password, projectId);
    }
    
    /**
     * The method returns total number of the project version files.
     */
    public long getFilesNumberByProjectVersionId(String userName,
            String password, long projectVersionId) {
        return projectManager.getFilesNumberByProjectVersionId(
                userName, password, projectVersionId);
    }
    
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
     * 
     * @deprecated the method is unused
     */
    @Deprecated
    public WSStoredProject requestEvaluation4Project(String userName, String password,
            String projectName, long projectVersion,
            String srcRepositoryLocation, String mailingListLocation,
            String BTSLocation, String userEmailAddress,String website) {
        return projectManager.requestEvaluation4Project(userName, password,
                projectName,projectVersion, srcRepositoryLocation,
                mailingListLocation, BTSLocation, userEmailAddress, website);
    }
    
    /**
     * This method returns the metrics for a given project.
     * @param userName
     * @param password
     * @param projectId
     * @return the list of metrics, 
     * if there are not metrics for the project then
     * the method returns an array with null element ([null]).
     */
    public WSMetric[] getMetricsByProjectId(String userName,
            String password, long projectId) {
        return metricManager.getMetricsByProjectId(userName, password, projectId);
    }
    
    /**
     * This method returns all installed metrics.
     * @return
     */
    public WSMetric[] getMetrics(String userName, String password) {
        return metricManager.getMetrics(userName, password);
    }
    
    /**
     * This method returns the metrics for a given files.
     * All files in the folder can be selected with the folder's name.
     * The user's name and password must be valid.
     * @return the list of metrics, 
     * if there are not metrics for the files then
     * the method returns an array with null element ([null]).
     */
    public WSMetric[] getMetricsByFileNames(String userName, String password,
            long projectId, String[] folders, String[] fileNames) {
        return metricManager.getMetricsByFileNames(userName, password,
                projectId, folders, fileNames);
    }
    
    public WSMetricMeasurement[] getProjectFileMetricMeasurement(String userName, String password,
            long metricId, long projectFileId) {
        return metricManager.getProjectFileMetricMeasurement(userName, password, metricId, projectFileId);
    }
    
    public WSMetricMeasurement[] getProjectVersionMetricMeasurement(String userName, String password,
            long metricId, long projectVersionId) {
        return metricManager.getProjectVersionMetricMeasurement(userName, password, metricId, projectVersionId);
    }
    
    /**
     * This method creates a new user.
     */
    public WSUser createUser(String userNameForAccess, String passwordForAccess,
            String newUserName, String newPassword, String email) {
        return userManager.createUser(userNameForAccess, passwordForAccess,
                newUserName, newPassword, email);
    }
    
    /**
     * This method makes a user request for a new account.
     * The request sends the confirmation e-mail to the user.
     * If the user doesn't confirm the request then the system
     * removes automatically the request.
     */
    public boolean createPendingUser(String userNameForAccess, String passwordForAccess,
            String newUserName, String newPassword, String email) {
        return userManager.createPendingUser(userNameForAccess, passwordForAccess,
                newUserName, newPassword, email);
    }
    
    /**
     * This method returns information about user with given user id.
     * The information does not contain the password hash. 
     */
    public WSUser getUserById(String userNameForAccess,
            String passwordForAccess, long userId) {
        return userManager.getUserById(
                userNameForAccess, passwordForAccess, userId);
    }
    
    /**
     * This method returns information about the user with a given user name.
     * The information does not contain the password hash.
     */
    public WSUser getUserByName(String userNameForAccess,
            String passwordForAccess, String userName) {
        return userManager.getUserByName(
                userNameForAccess, passwordForAccess, userName);
    }
    
    /**
     * This method modifies the existent user with a given user name.
     * The method can change the user's password and e-mail.
     */
    public boolean modifyUser(String userNameForAccess, String passwordForAccess,
            String userName, String newPassword, String newEmail) {
        return userManager.modifyUser(userNameForAccess, passwordForAccess,
                userName, newPassword, newEmail);
    }
    
    /**
     * This method deletes the user with a given id.
     */
    public boolean deleteUserById(String userNameForAccess,
            String passwordForAccess, long userId) {
        return userManager.deleteUserById(userNameForAccess,
                passwordForAccess, userId);
    }
    
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
