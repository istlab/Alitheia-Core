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

/**
 * The class has package visibility.
 * The SCL's client can create the WSConnection objects only from the WSSession. 
 */
class WSConnectionImpl implements WSConnection {

    private String userName;
    private String password;
    private String webServiceUrl;
    
    public WSConnectionImpl(String userName, String password, String webServiceUrl) {
        this.userName = userName;
        this.password = password;
        this.webServiceUrl = webServiceUrl;
    }
    
    public void deleteUser(String userId) {
        // TODO Auto-generated method stub
        
    }

    public WSResult displayDeveloperInfo(String projectId, String developerId, String criterioId,
            String display) {
        // TODO Auto-generated method stub
        return new WSResult("Not Implemented yet");
    }

    public WSResult displayDeveloperInfoTimeDiagram(String projectId, String developerId,
            String criterioId, String tdStart, String tdEnd) {
        // TODO Auto-generated method stub
        return new WSResult("Not Implemented yet");
    }

    public WSResult displayUser() {
        // TODO Auto-generated method stub
        return new WSResult("Not Implemented yet");
    }

    public WSResult evaluatedProjectsList() {
        // TODO Auto-generated method stub
        return new WSResult("Not Implemented yet");
    }

    public WSResult evaluatedProjectsListScore() {
        // TODO Auto-generated method stub
        return new WSResult("Not Implemented yet");
    }

    public void modifySubscriptions(String newProjectNotification, String newMetricPlugin,
            String projectEvalFinished, String newProjectVersion,
            String newQualityRatings, String statistics) {
        // TODO Auto-generated method stub
        
    }

    public void modifyUser(String modifyAccountUserName, String modifyAccountSurname,
            String modifyAccountPassword, String modifyAccountUserClass) {
        // TODO Auto-generated method stub
        
    }

    public WSResult ratedProjectsList() {
        // TODO Auto-generated method stub
        return new WSResult("Not Implemented yet");
    }

    public WSResult requestDefectStatistics(String prokectId, String searchQuery, String statisticalScheme) {
        // TODO Auto-generated method stub
        return new WSResult("Not Implemented yet");
    }

    public void requestEvaluatin4Project(String projectName, String projectVersion,
            String srcRepositoryLocation, String srcRepositoryType,
            String mailingListLocation, String BTSLocation) {
        // TODO Auto-generated method stub
        
    }

    public WSResult requestEvolEstimates4Project(String projectName, String projectVersion,
            String srcRepositoryLocation, String srcRepositoryType,
            String mailingListLocation, String BTSLocation) {
        // TODO Auto-generated method stub
        return new WSResult("Not Implemented yet");
    }

    public WSResult requestPastEvolEstimProjects() {
        // TODO Auto-generated method stub
        return new WSResult("Not Implemented yet");
    }

    public WSResult requestProjectEvolutionEstimates(String projectId, String startDate, String endDate) {
        // TODO Auto-generated method stub
        return new WSResult("Not Implemented yet");
    }

    public WSResult requestProjectEvolutionEstimatesDuration(String projectId, String duration) {
        // TODO Auto-generated method stub
        return new WSResult("Not Implemented yet");
    }

    public WSResult requestProjectsWithBTS() {
        // TODO Auto-generated method stub
        return new WSResult("Not Implemented yet");
    }

    public WSResult retrieveCriteria4SelectedDeveloper(String projectId, String developerId) {
        // TODO Auto-generated method stub
        return new WSResult("Not Implemented yet");
    }

    public WSResult retrieveDevelopers4SelectedProject(String projectId) {
        // TODO Auto-generated method stub
        return new WSResult("Not Implemented yet");
    }

    public WSResult retrieveFileList(String projectId) {
        // TODO Auto-generated method stub
        return new WSResult("Not Implemented yet");
    }

    public WSResult retrieveMetrics4SelectedFiles(String projectId, String[] folders,
            String[] fileNames) {
        // TODO Auto-generated method stub
        return new WSResult("Not Implemented yet");
    }

    public WSResult retrieveMetrics4SelectedProject(String projectId) {
        // TODO Auto-generated method stub
        return new WSResult("Not Implemented yet");
    }

    public WSResult retrieveProjectRatings(String projectId) {
        // TODO Auto-generated method stub
        return new WSResult("Not Implemented yet");
    }

    public WSResult retrieveSelectedMetric(String projectId, String metricId) {
        // TODO Auto-generated method stub
        return new WSResult("Not Implemented yet");
    }

    public void submitScores(String projectId, String[] scores, String textOpinion) {
        // TODO Auto-generated method stub
        
    }

    public void submitUser(String newAccountUserName, String newAccountSurname,
            String newAccountPassword, String newAccountUserClass) {
        // TODO Auto-generated method stub
        
    }

    public WSResult subscriptionsStatus() {
        // TODO Auto-generated method stub
        return new WSResult("Not Implemented yet");
    }

    public WSResult viewComments(String projectId) {
        // TODO Auto-generated method stub
        return new WSResult("Not Implemented yet");
    }

    public WSResult viewScores(String projectId) {
        // TODO Auto-generated method stub
        return new WSResult("Not Implemented yet");
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
