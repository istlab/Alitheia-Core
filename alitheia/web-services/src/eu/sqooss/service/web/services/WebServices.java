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
import eu.sqooss.impl.service.web.services.datatypes.WSFileGroup;
import eu.sqooss.impl.service.web.services.datatypes.WSMetric;
import eu.sqooss.impl.service.web.services.datatypes.WSMetricType;
import eu.sqooss.impl.service.web.services.datatypes.WSMetricsResultRequest;
import eu.sqooss.impl.service.web.services.datatypes.WSProjectFile;
import eu.sqooss.impl.service.web.services.datatypes.WSProjectVersion;
import eu.sqooss.impl.service.web.services.datatypes.WSResultEntry;
import eu.sqooss.impl.service.web.services.datatypes.WSStoredProject;
import eu.sqooss.impl.service.web.services.datatypes.WSUser;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.security.SecurityManager;
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
 * 
 * The URL is: http:/.../[web.service.context]/services/[web.service.name]
 * The wsdl file is: http:/.../[web.service.context]/services/[web.service.name]?wsdl
 */

/**
 * The <code>WebServices</code> class defines all methods for accessing the
 * SQO-OSS framework information, that are exported in form of web services,
 * for use by the SQO-OSS user interfaces (<i>or other web service aware
 * applications</i>).
 */
public class WebServices {

    // Instances of the manager classes
    private MetricManager metricManager;
    private ProjectManager projectManager;
    private UserManager userManager;
    private WebadminService webadmin;

    /**
     * Instantiates a new WebServices object.
     * 
     * @param bc - the parent's bundle context
     * @param securityManager - the Security component's instance
     * @param db - the DB component's instance
     * @param tds - the TDS component's instance
     * @param logger - the Logger component's instance
     * @param wa - the WebAdmin component's instance
     */
    public WebServices(
            BundleContext bc,
            SecurityManager securityManager,
            DBService db,
            PluginAdmin pluginAdmin,
            Logger logger,
            WebadminService wa) {
        metricManager = new MetricManager(logger, db, pluginAdmin, securityManager);
        projectManager = new ProjectManager(logger, db, securityManager);
        userManager = new UserManager(securityManager, db);
        webadmin = wa;
    }

    // ===[ ProjectManager methods]===========================================

    /**
     * This method returns an array of all projects accessible from the given
     * user, that the SQO-OSS framework has had evaluated.
     * 
     * @param userName - the user's name used for authentication
     * @param password - the user's password used for authentication
     * @return The array of evaluated projects, or a <code>null</code> array
     *   when none are found.
     */
    public WSStoredProject[] getEvaluatedProjects(
            String userName,
            String password) {
        return projectManager.getEvaluatedProjects(userName, password);
    }

    /**
     * This method returns an array of all projects accessible from the given
     * user, no matter if the SQO-OSS framework had evaluated them or not.
     * 
     * @param userName - the user's name used for authentication
     * @param password - the user's password used for authentication
     * 
     * @return The array of stored projects, or a <code>null</code> array when
     *   none are found.
     */
    public WSStoredProject[] getStoredProjects(
            String userName,
            String password) {
        return projectManager.getStoredProjects(userName, password);
    }

    /**
     * This method returns an array of all files that belongs to the project
     * with the given Id.
     * 
     * @param userName - the user's name used for authentication
     * @param password - the user's password used for authentication
     * @param projectId - the project's identifier
     * 
     * @return The array of project's files, or a <code>null</code> array when
     *   none are found <i>(for example, when the project is not yet not
     *   evaluated)</i>.
     */
    public WSProjectFile[] getFilesByProjectId(
            String userName,
            String password,
            long projectId) {
        return projectManager.getFilesByProjectId(
                userName, password, projectId);
    }

    /**
     * The method returns an array of all files that exists in the specified
     * project version.
     * 
     * @param userName - the user's name used for authentication
     * @param password - the user's password used for authentication
     * @param projectVersionId - the project's version identifier
     * 
     * @return The array of project's files in that project version, or a
     *   <code>null</code> array when none are found.
     */
    public WSProjectFile[] getFilesByProjectVersionId(
            String userName,
            String password,
            long projectVersionId) {
        return projectManager.getFilesByProjectVersionId(
                userName, password, projectVersionId);
    }
    
    /**
     * This method returns an array of all file groups that belongs to the project
     * with the given Id.
     * 
     * @param userName - the user's name used for authentication
     * @param password - the user's password used for authentication
     * @param projectId - the project's identifier
     * 
     * @return The array of project's file groups, or a <code>null</code> array when
     *   none are found <i>(for example, when the project is not yet not
     *   evaluated)</i>.
     */
    public WSFileGroup[] getFileGroupsByProjectId(
            String userName,
            String password,
            long projectId) {
        return projectManager.getFileGroupsByProjectId(
                userName, password, projectId);
    }
    
    /**
     * The method returns an array representing all evaluated versions of the
     * given project.
     * 
     * @param userName - the user's name used for authentication
     * @param password - the user's password used for authentication
     * @param projectId - the project's identifier
     * 
     * @return The array with all evaluated project versions, or a
     *   <code>null</code> array when none are found.
     */
    public WSProjectVersion[] getProjectVersionsByProjectId(
            String userName,
            String password,
            long projectId) {
        return projectManager.getProjectVersionsByProjectId(
                userName, password, projectId);
    }

    /**
     * The method returns all information, that the SQO-OSS framework has
     * collected about the specified projects.
     * 
     * @param userName - the user's name used for authentication
     * @param password - the user's password used for authentication
     * @param projectsIds - the projects' identifiers
     * 
     * @return The <code>WSStoredProject</code> array that describes the
     * projects, or <code>null</code> when such projects do not exist.
     */
    public WSStoredProject[] getProjectsByIds(
            String userName,
            String password,
            long[] projectsIds) {
        return projectManager.getProjectsByIds(userName, password, projectsIds);
    }

    /**
     * The method returns all information, that the SQO-OSS framework has
     * collected about the specified project.
     * 
     * @param userName - the user's name used for authentication
     * @param password - the user's password used for authentication
     * @param projectName - the project's name
     * 
     * @return The <code>WSStoredProject</code> object that describes the
     * project, or <code>null</code> when such project does not exist.
     */
    public WSStoredProject getProjectByName(
            String userName,
            String password,
            String projectName) {
        return projectManager.getProjectByName(
                userName, password, projectName);
    }
    
    /**
     * The method returns the total number of files, that exists in the given
     * project version.
     * 
     * @param userName - the user's name used for authentication
     * @param password - the user's password used for authentication
     * @param projectVersionId - the project's version identifier
     * 
     * @return The number of project's files in that project version.
     */
    public long getFilesNumberByProjectVersionId(
            String userName,
            String password,
            long projectVersionId) {
        return projectManager.getFilesNumberByProjectVersionId(
                userName, password, projectVersionId);
    }

    /**
     * The method returns the total of the files, that exist in the given project.
     * 
     * @param userName - the user's name used for authentication
     * @param password - the user's password used for authentication
     * @param projectId - the project's identifier
     * 
     * @return The number of the eproject's files.
     */
    public long getFilesNumberByProjectId(
            String userName,
            String password,
            long projectId) {
        return projectManager.getFilesNumberByProjectId(
                userName, password, projectId);
    }

    // ===[ MetricManager methods]============================================

    /**
     * This method returns an array with all metrics, that have been evaluated
     * for the given project.
     * 
     * @param userName - the user's name used for authentication
     * @param password - the user's password used for authentication
     * @param projectId - the project's identifier
     * 
     * @return The array with all evaluated metrics, or a <code>null</code>
     *   array when none are found.
     */
    public WSMetric[] getMetricsByProjectId(
            String userName,
            String password,
            long projectId) {
        return metricManager.getMetricsByProjectId(
                userName, password, projectId);
    }

    /**
     * The method returns all information, that the SQO-OSS framework has
     * collected about the specified metric type.
     * 
     * @param userName - the user's name used for authentication
     * @param password - the user's password used for authentication
     * @param metricTypeId - the metric type's identifier
     * 
     * @return The <code>WSMetricType</code> object that describes the
     * metric type, or <code>null</code> when such metric type does not exist.
     */
    public WSMetricType getMetricTypeById(
            String userName,
            String password,
            long metricTypeId) {
        return metricManager.getMetricTypeById(
                userName, password, metricTypeId);
    }
    
    /**
     * This method returns an array with all metrics, that are currently
     * installed in the SQO-OSS framework.
     * 
     * @param userName - the user's name used for authentication
     * @param password - the user's password used for authentication
     * 
     * @return The array with all installed metrics, or a <code>null</code>
     *   array when none are found.
     */
    public WSMetric[] getMetrics(String userName, String password) {
        return metricManager.getMetrics(userName, password);
    }

    /**
     * This method returns an array of all metrics that have been evaluated on
     * the selected set of project files.
     * <br/>
     * Separate files can be selected by including them in the
     * <code>fileNames</code> array.
     * <br/>
     * For selecting all files in a specific folder, the folder name must be 
     * included in the <code>folders</code> array.
     * 
     * @param userName - the user's name used for authentication
     * @param password - the user's password used for authentication
     * @param projectId - the project's identifier
     * @param folders - the array with folder names
     * @param fileNames - the array with file names
     * 
     * @return The array with all evaluated metrics, or a <code>null</code>
     *   array when none are found.
     */
    public WSMetric[] getMetricsByFileNames(
            String userName,
            String password,
            long projectId,
            String[] folders,
            String[] fileNames) {
        return metricManager.getMetricsByFileNames(
                userName, password, projectId, folders, fileNames);
    }

    /**
     * Returns the array of results from the evaluation of the specified
     * metrics on the given data access object.
     * 
     * @param userName - the user's name used for authentication
     * @param password - the user's password used for authentication
     * @param resultRequest - the request object,
     * the object contains the request information
     * 
     * @return The array of all metric evaluation results on that request,
     * or a <code>null</code> array when none are found.
     */
    public WSResultEntry[] getMetricsResult(String userName, String password,
            WSMetricsResultRequest resultRequest) {
        return metricManager.getMetricsResult(userName, password, resultRequest);
    }
    
    // ===[ UserManager methods]==============================================

    /**
     * This method creates a new pending user entry, and sends an email to the
     * given user address with a request for confirmation. After successful
     * confirmation, the pending user entry is converted into a SQO-OSS user.
     * <br/>
     * Note: If the user doesn't confirm the request in time, then the pending
     * user entry is automatically removed from the system, after its
     * expiration.
     * 
     * @param userNameForAccess - the SQO-OSS unprivileged user's name
     * @param passwordForAccess - the SQO-OSS unprivileged user's password
     * @param newUserName - name for the new user
     * @param newPassword - password of the new user
     * @param email - email address of the new user
     * 
     * @return <code>true</code> upon success, or <code>false</code> when a
     *   user with the same name already exists.
     */
    public boolean createPendingUser(
            String userNameForAccess,
            String passwordForAccess,
            String newUserName,
            String newPassword,
            String email) {
        return userManager.createPendingUser(userNameForAccess, passwordForAccess,
                newUserName, newPassword, email);
    }

    /**
     * This method returns all known information about the user referenced by
     * the given identifier.
     * <br/>
     * <i>The information does not include the user's password hash.<i>
     * 
     * @param userNameForAccess - the user's name used for authentication
     * @param passwordForAccess - the user's password used for authentication
     * @param userId - the identifier of the requested user
     * 
     * @return The <code>WSUser</code> object describing the requested user.
     */
    public WSUser getUserById(
            String userNameForAccess,
            String passwordForAccess,
            long userId) {
        return userManager.getUserById(
                userNameForAccess, passwordForAccess, userId);
    }

    /**
     * This method returns all known information about the user associated
     * with the given user name.
     * <br/>
     * <i>The information does not include the user's password hash.<i>
     * 
     * @param userNameForAccess - the user's name used for authentication
     * @param passwordForAccess - the user's password used for authentication
     * @param userName - the name of the requested user
     * 
     * @return The <code>WSUser</code> object describing the requested user.
     */
    public WSUser getUserByName(
            String userNameForAccess,
            String passwordForAccess,
            String userName) {
        return userManager.getUserByName(
                userNameForAccess, passwordForAccess, userName);
    }

    /**
     * This method modifies the information of the existing user associated
     * with the given user name.
     * <br/>
     * <i>This method can change the user's password and email address
     *   only.</i>
     * 
     * @param userNameForAccess - the user's name used for authentication
     * @param passwordForAccess - the user's password used for authentication
     * @param userName - the name of the requested user
     * @param newPassword - the new password
     * @param newEmail - the new email address
     * 
     * @return <code>true</code> upon successful modification,
     *   or <code>false</code> in case of failure.
     */
    public boolean modifyUser(
            String userNameForAccess,
            String passwordForAccess,
            String userName,
            String newPassword,
            String newEmail) {
        return userManager.modifyUser(
                userNameForAccess,
                passwordForAccess,
                userName,
                newPassword,
                newEmail);
    }

    /**
     * This method deletes the user referenced by the given identifier
     * 
     * @param userNameForAccess - the user's name used for authentication
     * @param passwordForAccess - the user's password used for authentication
     * @param userId - the identifier of the requested user
     * 
     * @return <code>true</code> upon successful removal,
     *   or <code>false</code> in case of failure.
     */
    public boolean deleteUserById(
            String userNameForAccess,
            String passwordForAccess,
            long userId) {
        return userManager.deleteUserById(
                userNameForAccess, passwordForAccess, userId);
    }

    // ===[ Miscellaneous methods]============================================

    /**
     * Returns the user's message of the day. MOTD's are usually created by
     * the SQO-OSS system administrator or the SQO-OSS framework itself,
     * upon occurrence of specific events (like addition of a new project).
     * 
     * @param userName - the user's name
     * 
     * @return The message of the day, which is valid for that user.
     */
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
