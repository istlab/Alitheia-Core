/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

public interface WSConnection {
    
    //5.1.1
    /**
     * This method returns evaluated projects.
     * Each <code>WSResult</code>'s row contains information about the project.
     * The row consists of fields. The fields' description can be found in:
     * {@link eu.sqooss.scl.utils.WSResponseParser#parseStoredProjects(eu.sqooss.scl.axis2.datatypes.WSStoredProject[])}
     * 
     * @return <code>WSResult</code>
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be establish to the SQO-OSS's web services service</li>
     *  <li>if web services service throws a exception</li>
     * <ul>
     */
    public WSResult evaluatedProjectsList() throws WSException;
    
    /**
     * This method returns the metrics for a given project.
     * Each <code>WSResult</code>'s row contains information about the metric.
     * The row consists of fields. The fields' description can be found in:
     * {@link eu.sqooss.scl.utils.WSResponseParser#parseMetrics(eu.sqooss.scl.axis2.datatypes.WSMetric[])}
     * 
     * @param projectId the project's id
     * @return <code>WSResult</code>
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be establish to the SQO-OSS's web services service</li>
     *  <li>if web services service throws a exception</li>
     * <ul>
     */
    public WSResult retrieveMetrics4SelectedProject(String projectId) throws WSException;
    
    /**
     * This method returns the metric with a given id.
     * A <code>WSResult</code>'s row contains information about the metric.
     * The row consists of fields. The fields' description can be found in:
     * {@link eu.sqooss.scl.utils.WSResponseParser#parseMetrics(eu.sqooss.scl.axis2.datatypes.WSMetric[])}
     * 
     * @param projectId the project's id
     * @param metricId the metric's id
     * @return <code>WSResult</code>
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be establish to the SQO-OSS's web services service</li>
     *  <li>if web services service throws a exception</li>
     * <ul>
     */
    public WSResult retrieveSelectedMetric(String projectId, String metricId) throws WSException;
    //5.1.1
    
    //5.1.2
    /**
     * This method returns the project's files.
     * Each <code>WSResult</code>'s row contains information about the project's file.
     * The row consists of fields. The fields' description can be found in:
     * {@link eu.sqooss.scl.utils.WSResponseParser#parseProjectFiles(eu.sqooss.scl.axis2.datatypes.WSProjectFile[])}
     * 
     * @param projectId the project's id
     * @return <code>WSResult</code>
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be establish to the SQO-OSS's web services service</li>
     *  <li>if web services service throws a exception</li>
     * <ul>
     */
    public WSResult retrieveFileList(String projectId) throws WSException;
    
    /**
     * This method returns the metrics for a given files.
     * All files in the folder can be selected with the folder's name.
     * Each <code>WSResult</code>'s row contains information about the metric.
     * The row consists of fields. The fields' description can be found in:
     * {@link eu.sqooss.scl.utils.WSResponseParser#parseMetrics(eu.sqooss.scl.axis2.datatypes.WSMetric[])}
     * 
     * @param projectId the project's id
     * @param folderNames the folders' names i.e. all files in the folders
     * @param fileNames the files' names
     * @return <code>WSResult</code>
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be establish to the SQO-OSS's web services service</li>
     *  <li>if web services service throws a exception</li>
     * <ul>
     */
    public WSResult retrieveMetrics4SelectedFiles(String projectId, String folderNames, String fileNames) throws WSException;
    //5.1.2
    
    //5.1.3
    /**
     * This method makes request for OSS project evaluation.
     * If a project with same name and version is known to the system
     * then the method returns the existent project.
     * A <code>WSResult</code> contains information about the project.
     * The row consists of fields. The fields' description can be found in:
     * {@link eu.sqooss.scl.utils.WSResponseParser#parseStoredProjects(eu.sqooss.scl.axis2.datatypes.WSStoredProject[])}
     *  
     * @param projectName the project's name
     * @param projectVersion the project's version
     * @param srcRepositoryLocation URL for the source repository
     * @param mailingListLocation URL for the mailing list
     * @param BTSLocation URL for the bug tracking system
     * @param userEmailAddress user's e-mail address
     * @param website project's website
     * @return <code>WSResult</code>
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be establish to the SQO-OSS's web services service</li>
     *  <li>if web services service throws a exception</li>
     * <ul>
     */
    public WSResult requestEvaluation4Project(String projectName, String projectVersion,
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
    
    //5.1.7
    public WSResult evaluatedProjectsListScore();
    
    public void submitScores(String projectId, String[] scores, String textOpinion);
    
    public WSResult viewScores(String projectId);
    
    public WSResult viewComments(String projectId);
    //5.1.7
    
    //5.1.8
    public WSResult ratedProjectsList();
    
    public WSResult retrieveProjectRatings(String projectId);
    //5.1.8
    
    //5.1.9
    public WSResult subscriptionsStatus();
    
    public void modifySubscriptions(String newProjectNotification, String newMetricPlugin,
            String projectEvalFinished, String newProjectVersion,
            String newQualityRatings, String statistics);
    //5.1.9
    
    //5.1.10
    public void submitUser(String newAccountUserName, String newAccountSurname,
            String newAccountPassword, String newAccountUserClass);
    //5.1.10
    
    //5.1.11
    public WSResult displayUser();
    
    public void modifyUser(String modifyAccountUserName, String modifyAccountSurname,
            String modifyAccountPassword, String modifyAccountUserClass);
    
    public void deleteUser(String userId);
    //5.1.11
    
}

//vi: ai nosi sw=4 ts=4 expandtab
