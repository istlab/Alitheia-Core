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

import java.rmi.RemoteException;
import java.util.Hashtable;

import org.apache.axis2.AxisFault;

import eu.sqooss.scl.axis2.WsStub;
import eu.sqooss.scl.axis2.ws.EvaluatedProjectsList;
import eu.sqooss.scl.axis2.ws.EvaluatedProjectsListResponse;
import eu.sqooss.scl.axis2.ws.RetrieveMetrics4SelectedProject;
import eu.sqooss.scl.axis2.ws.RetrieveMetrics4SelectedProjectResponse;
import eu.sqooss.scl.axis2.ws.RetrieveSelectedMetric;
import eu.sqooss.scl.axis2.ws.RetrieveSelectedMetricResponse;
import eu.sqooss.scl.axis2.datatypes.WSMetric;
import eu.sqooss.scl.result.WSResult;
import eu.sqooss.scl.utils.WSResponseParser;

/**
 * The class has package visibility.
 * The SCL's client can create the WSConnection objects only from the WSSession. 
 */
class WSConnectionImpl implements WSConnection {

    private Hashtable<String, Object> parameters;
    private WsStub wsStub;
    private String userName;
    private String password;
    private String webServiceUrl;
    
    public WSConnectionImpl(String userName, String password, String webServiceUrl) throws WSException {
        this.userName = userName;
        this.password = password;
        this.webServiceUrl = webServiceUrl;
        try {
            this.wsStub = new WsStub(webServiceUrl);
        } catch (AxisFault e) {
            throw new WSException(e);
        }
        initParameters();
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

    public WSResult evaluatedProjectsList() throws WSException {
        EvaluatedProjectsListResponse response; 
        EvaluatedProjectsList params = (EvaluatedProjectsList) parameters.get(WSConnectionConstants.PARAM_KEY_EVALUATED_PROJECTS_LIST);
        try {
            response = wsStub.evaluatedProjectsList(params);
        } catch (RemoteException e) {
            throw new WSException(e);
        }
        return WSResponseParser.parseStoredProjects(response.get_return());
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

    public WSResult retrieveMetrics4SelectedProject(String projectId) throws WSException {
        RetrieveMetrics4SelectedProject params = (RetrieveMetrics4SelectedProject) parameters.get(WSConnectionConstants.PARAM_KEY_RETRIEVE_METRICS_4_SELECTED_PROJECTS);
        params.setProjectId(projectId);
        RetrieveMetrics4SelectedProjectResponse response;
        try {
            response = wsStub.retrieveMetrics4SelectedProject(params);
        } catch (RemoteException re) {
            throw new WSException(re);
        }
        return WSResponseParser.parseMetrics(response.get_return());
    }

    public WSResult retrieveProjectRatings(String projectId) {
        // TODO Auto-generated method stub
        return new WSResult("Not Implemented yet");
    }

    public WSResult retrieveSelectedMetric(String projectId, String metricId) throws WSException {
        RetrieveSelectedMetric params = (RetrieveSelectedMetric) parameters.get(WSConnectionConstants.PARAM_KEY_RETRIEVE_SELECTED_METRIC);
        params.setProjectId(projectId);
        params.setMetricId(metricId);
        RetrieveSelectedMetricResponse response = null;;
        try {
            response = wsStub.retrieveSelectedMetric(params);
        } catch (RemoteException e) {
            throw new WSException(e);
        }
        return WSResponseParser.parseMetrics(new WSMetric[]{response.get_return()});
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
    
    private void initParameters() {
        parameters = new Hashtable<String, Object>(2);
        
        EvaluatedProjectsList epl = new EvaluatedProjectsList();
        epl.setPassword(password);
        epl.setUserName(userName);
        parameters.put(WSConnectionConstants.PARAM_KEY_EVALUATED_PROJECTS_LIST, epl);
        
        RetrieveMetrics4SelectedProject rm4sp = new RetrieveMetrics4SelectedProject();
        rm4sp.setUserName(userName);
        rm4sp.setPassword(password);
        parameters.put(WSConnectionConstants.PARAM_KEY_RETRIEVE_METRICS_4_SELECTED_PROJECTS, rm4sp);
        
        RetrieveSelectedMetric rsm = new RetrieveSelectedMetric();
        rsm.setUserName(userName);
        rsm.setPassword(password);
        parameters.put(WSConnectionConstants.PARAM_KEY_RETRIEVE_SELECTED_METRIC, rsm);
        
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
