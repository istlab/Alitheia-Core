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

package eu.sqooss.scl;

import eu.sqooss.scl.result.WSResult;
import eu.sqooss.ws.client.datatypes.WSMetric;
import eu.sqooss.ws.client.datatypes.WSProjectFile;
import eu.sqooss.ws.client.datatypes.WSStoredProject;
import eu.sqooss.ws.client.datatypes.WSUser;

public interface WSConnection {
    
    //5.1.1
    /**
     * This method returns evaluated projects.
     * 
     * <p>
     * The method's url is: <br>
     * http://sqo-oss/evaluatedProjectsList
     * </p>
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be establish to the SQO-OSS's web services service</li>
     *  <li>if web services service throws a exception</li>
     * <ul>
     */
    public WSStoredProject[] evaluatedProjectsList() throws WSException;

    /**
     * All the projects in the database. This method has no URL.
     */
    public WSStoredProject[] storedProjectsList() throws WSException;
    
    /**
     * This method returns the metrics for a given project.
     * 
     * <p>
     * The method's url is: <br>
     * http://sqo-oss/retrieveMetrics4SelectedProject?pid={project_id}
     * </p>
     * 
     * @param projectId the project's id
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be establish to the SQO-OSS's web services service</li>
     *  <li>if web services service throws a exception</li>
     * <ul>
     */
    public WSMetric[] retrieveMetrics4SelectedProject(long projectId) throws WSException;
    
    public WSResult retrieveSelectedMetric(long projectId, long metricId) throws WSException;
    //5.1.1
    
    //5.1.2
    /**
     * This method returns the project's files.
     * 
     * <p>
     * The method's url is: <br>
     * http://sqo-oss/retrieveFileList?pid={project_id}
     * </p>
     * 
     * @param projectId the project's id
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be establish to the SQO-OSS's web services service</li>
     *  <li>if web services service throws a exception</li>
     * <ul>
     */
    public WSProjectFile[] retrieveFileList(long projectId) throws WSException;
    
    /**
     * This method returns the metrics for a given files.
     * All files in the folder can be selected with the folder's name.
     * 
     * <p>
     * The method's url is: <br>
     * http://sqo-oss/retrieveMetrics4SelectedFiles?pid={project_id}&
     * folders={list-of-folder-names}&filenames={list-of-file-names}
     * </p>
     * 
     * @param projectId the project's id
     * @param folderNames the folders' names; the folders' delimiter is a comma
     * @param fileNames the files' names, the files' delimiter is a comma
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be establish to the SQO-OSS's web services service</li>
     *  <li>if web services service throws a exception</li>
     * <ul>
     */
    public WSMetric[] retrieveMetrics4SelectedFiles(long projectId, String folderNames, String fileNames) throws WSException;
    //5.1.2
    
    //5.1.3
    /**
     * This method makes request for OSS project evaluation.
     * If a project with same name and version is known to the system
     * then the method returns the existent project.
     *  
     * @param projectName the project's name
     * @param projectVersion the project's version
     * @param srcRepositoryLocation URL for the source repository
     * @param mailingListLocation URL for the mailing list
     * @param BTSLocation URL for the bug tracking system
     * @param userEmailAddress user's e-mail address
     * @param website project's website
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be establish to the SQO-OSS's web services service</li>
     *  <li>if web services service throws a exception</li>
     * <ul>
     */
    public WSStoredProject requestEvaluation4Project(String projectName, long projectVersion,
            String srcRepositoryLocation, String mailingListLocation,
            String BTSLocation, String userEmailAddress, String website) throws WSException;
    //5.1.3
    
    //5.1.4
    public WSResult requestPastEvolEstimProjects();
    
    public WSResult requestProjectEvolutionEstimates(String projectId, String startDate, String endDate);
    
    public WSResult requestProjectEvolutionEstimatesDuration(String projectId, String duration);
    
    public WSResult requestEvolEstimates4Project(String projectName, String projectVersion, String srcRepositoryLocation,
            String srcRepositoryType, String mailingListLocation, String BTSLocation);
    //5.1.4
    
    //5.1.5
    public WSResult requestProjectsWithBTS();
    
    public WSResult requestDefectStatistics(String projectId, String searchQuery, String statisticalScheme);
    //5.1.5
    
    //5.1.6
    public WSResult retrieveDevelopers4SelectedProject(String projectId);
    
    public WSResult retrieveCriteria4SelectedDeveloper(String projectId, String developerId);
    
    public WSResult displayDeveloperInfoTimeDiagram(String projectId, String developerId, String criterioId,
            String tdStart, String tdEnd);
    
    public WSResult displayDeveloperInfo(String projectId, String developerId, String criterioId, String display);
    //5.1.6
    
    //5.1.9
    public WSResult subscriptionsStatus();
    
    public void modifySubscriptions(String newProjectNotification, String newMetricPlugin,
            String projectEvalFinished, String newProjectVersion,
            String newQualityRatings, String statistics);
    //5.1.9
    
    //5.1.10
    /**
     * This method creates a new user.
     * 
     * @param newUserName - user name
     * @param newNames - user's names (optional)
     * @param newPassword - user's password
     * @param newUserClass - user's class (general user, OSS developer, etc.)
     * @param newOtherInfo - (optional)
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be establish to the SQO-OSS's web services service</li>
     *  <li>if web services service throws a exception</li>
     * <ul>
     */
    public WSUser submitUser(String newUserName, String newNames, String newPassword,
            String newUserClass, String newOtherInfo) throws WSException;
    //5.1.10
    
    //5.1.11
    /**
     * This method returns information about the user with a given id.
     *  
     * @param userId - user's id
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be establish to the SQO-OSS's web services service</li>
     *  <li>if web services service throws a exception</li>
     * <ul>
     */
    public WSUser displayUser(long userId) throws WSException;
    
    /**
     * This method modifies the existent user with a given user name.
     * 
     * @param userName - the user name
     * @param newNames - the new user's names (optional)
     * @param newPassword - the new password
     * @param newUserClass - the new user's class (general user, OSS developer, etc.)
     * @param newOtherInfo - (optional)
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be establish to the SQO-OSS's web services service</li>
     *  <li>if web services service throws a exception</li>
     * <ul>
     */
    public void modifyUser(String userName, String newNames, String newPassword,
            String newUserClass, String newOtherInfo) throws WSException;
    
    /**
     * This method deletes the user with a given id.
     * 
     * @param userId - user's id
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be establish to the SQO-OSS's web services service</li>
     *  <li>if web services service throws a exception</li>
     * <ul>
     */
    public void deleteUser(long userId) throws WSException;
    //5.1.11
    
    //retrieve methods
    /**
     * This method retrieves the project's identifier.
     * 
     * @param projectName - the name of the project as stored in the SQO-OSS.
     * @return
     * @throws WSException
     */
    public long retrieveProjectId(String projectName) throws WSException;
    //retrieve methods
    
}

//vi: ai nosi sw=4 ts=4 expandtab
