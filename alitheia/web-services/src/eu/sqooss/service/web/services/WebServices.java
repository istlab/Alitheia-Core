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
import eu.sqooss.impl.service.web.services.datatypes.WSProjectFile;
import eu.sqooss.impl.service.web.services.datatypes.WSProjectVersion;
import eu.sqooss.impl.service.web.services.datatypes.WSStoredProject;
import eu.sqooss.impl.service.web.services.datatypes.WSUser;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.security.SecurityManager;
import eu.sqooss.service.tds.TDSService;

/* 
 * NOTES:
 * 
 * 1. The WebServices's implementation and the data types are specially in this form.
 * The Axis2's wsdl generator is the reason.
 * It doesn't work correct with the interfaces, the abstract classes and the inheritance.
 * 
 * 2. java2wsdl doesn't support methods overloading.
 * 
 * 3. The returned arrays must not be empty. You can return null.
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
    
    public WebServices(BundleContext bc, SecurityManager securityManager,
            DBService db, TDSService tds, Logger logger) {
        metricManager = new MetricManager(logger, db);
        projectManager = new ProjectManager(logger, db, tds);
        userManager = new UserManager();
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
    
//    //5.1.4
//    public WSPair[] requestPastEvolEstimProjects(String userName, String password) {
//        return null;
//    }
//    
//    public String[] requestProjectEvolutionEstimates(String userName, String password,
//            String projectId, String startDate, String endDate) {
//        return null;
//    }
//    
//    public String[] requestProjectEvolutionEstimatesDuration(String userName, String password,
//            String projectId, String duration) {
//        return null;
//    }
//    
//    public String[] requestEvolEstimates4Project(String userName, String password,
//            String projectName, String projectVersion, String srcRepositoryLocation,
//            String srcRepositoryType, String mailingListLocation, String BTSLocation) {
//        return null;
//    }
//    //5.1.4
//    
//    //5.1.5
//    public WSPair[] requestProjectsWithBTS(String userName, String password) {
//        return null;
//    }
//    
//    public String[] requestDefectStatistics(String userName, String password,
//            String prokectId, String searchQuery, String statisticalScheme) {
//        return null;
//    }
//    //5.1.5
//    
//    //5.1.6
//    public WSPair[] retrieveDevelopers4SelectedProject(String userName, String password,
//            String projectId) {
//        return null;
//    }
//    
//    public WSPair[] retrieveCriteria4SelectedDeveloper(String userName, String password,
//            String projectId, String developerId) {
//        return null;
//    }
//    
//    public String displayDeveloperInfoTimeDiagram(String userName, String password,
//            String projectId, String developerId, String criterioId,
//            String tdStart, String tdEnd) {
//        return null;
//    }
//    
//    public String displayDeveloperInfo(String userName, String password,
//            String projectId, String developerId, String criterioId, String display) {
//        return null;
//    }
//    //5.1.6
//    
//    //5.1.9
//    public String subscriptionsStatus(String userName, String password) {
//        return null;
//    }
//    
//    public void modifySubscriptions(String userName, String password,
//            String newProjectNotification, String newMetricPlugin,
//            String projectEvalFinished, String newProjectVersion,
//            String newQualityRatings, String statistics) {
//    }
//    //5.1.9
    
    //5.1.10
    /**
     * This method creates a new user.
     * 
     * @param userNameForAccess
     * @param passwordForAccess
     * @param newUserName
     * @param newNames
     * @param newPassword
     * @param newUserClass
     * @param newOtherInfo
     * @return
     */
    public WSUser submitUser(String userNameForAccess, String passwordForAccess,
            String newUserName, String newNames, String newPassword,
            String newUserClass, String newOtherInfo) {
        return userManager.submitUser(userNameForAccess, passwordForAccess,
                newUserName, newNames, newPassword, newUserClass, newOtherInfo);
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
     * This method modifies the existent user with a given user name.
     * 
     * @param userNameForAccess
     * @param passwordForAccess
     * @param userName
     * @param newNames
     * @param newPassword
     * @param newUserClass
     * @param newOtherInfo
     */
    public void modifyUser(String userNameForAccess, String passwordForAccess,
            String userName, String newNames, String newPassword,
            String newUserClass, String newOtherInfo) {
        userManager.modifyUser(userNameForAccess, passwordForAccess,
                userName, newNames, newPassword, newUserClass, newOtherInfo);
    }
    
    /**
     * This method deletes the user with a given id.
     * 
     * @param userNameForAccess
     * @param passwordForAccess
     * @param userId
     */
    public void deleteUser(String userNameForAccess, String passwordForAccess, long userId) {
        userManager.deleteUser(userNameForAccess, passwordForAccess, userId);
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
    public long retrieveProjectId(String userName, String passwrod, String projectName) {
        return projectManager.retrieveProjectId(userName, passwrod, projectName);
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
    
    //validation
    /**
     * This method checks the user's name and the given password.
     * @param userName
     * @param password
     * @return
     */
    public boolean validateAccount(String userName, String password) {
        return userManager.validateAccount(userName, password);
    }
    //validation
    
}

//vi: ai nosi sw=4 ts=4 expandtab
