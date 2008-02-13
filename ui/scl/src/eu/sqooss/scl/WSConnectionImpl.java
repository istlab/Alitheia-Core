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
import java.util.StringTokenizer;

import org.apache.axis2.AxisFault;

import eu.sqooss.scl.result.WSResult;
import eu.sqooss.ws.client.WsStub;
import eu.sqooss.ws.client.datatypes.WSStoredProject;
import eu.sqooss.ws.client.datatypes.WSUser;
import eu.sqooss.ws.client.ws.DeleteUser;
import eu.sqooss.ws.client.ws.DisplayUser;
import eu.sqooss.ws.client.ws.DisplayUserResponse;
import eu.sqooss.ws.client.ws.EvaluatedProjectsList;
import eu.sqooss.ws.client.ws.EvaluatedProjectsListResponse;
import eu.sqooss.ws.client.ws.ModifyUser;
import eu.sqooss.ws.client.ws.RequestEvaluation4Project;
import eu.sqooss.ws.client.ws.RequestEvaluation4ProjectResponse;
import eu.sqooss.ws.client.ws.RetrieveFileList;
import eu.sqooss.ws.client.ws.RetrieveFileListResponse;
import eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedFiles;
import eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedFilesResponse;
import eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedProject;
import eu.sqooss.ws.client.ws.RetrieveMetrics4SelectedProjectResponse;
import eu.sqooss.ws.client.ws.RetrieveProjectId;
import eu.sqooss.ws.client.ws.RetrieveProjectIdResponse;
import eu.sqooss.ws.client.ws.RetrieveSelectedMetric;
import eu.sqooss.ws.client.ws.SubmitUser;
import eu.sqooss.ws.client.ws.SubmitUserResponse;
import eu.sqooss.ws.client.ws.ValidateAccount;
import eu.sqooss.ws.client.ws.ValidateAccountResponse;

/**
 * The class has package visibility.
 * The SCL's client can create the WSConnection objects only from the WSSession. 
 */
class WSConnectionImpl implements WSConnection {

    private Hashtable<String, Object> parameters;
    private WsStub wsStub;
    private String userName;
    private String password;
    
    public WSConnectionImpl(String userName, String password, String webServiceUrl) throws WSException {
        this.userName = userName;
        this.password = password;
        ValidateAccountResponse response;
        try {
            this.wsStub = new WsStub(webServiceUrl);
            ValidateAccount accountParams = new ValidateAccount();
            accountParams.setPassword(password);
            accountParams.setUserName(userName);
            response = this.wsStub.validateAccount(accountParams);
        } catch (AxisFault e) {
            throw new WSException(e);
        } catch (RemoteException re) {
            throw new WSException(re);
        }
        
        if (!response.get_return()) {
            throw new WSException("The user's account is not valid!");
        }
        
        initParameters();
    }
    
    /**
     * @throws WSException 
     * @see eu.sqooss.scl.WSConnection#deleteUser(long)
     */
    public void deleteUser(long userId) throws WSException {
        DeleteUser params = (DeleteUser) parameters.get(
                WSConnectionConstants.METHOD_NAME_DELETE_USER);
        synchronized (params) {
            params.setUserId(userId);
            try {
                wsStub.deleteUser(params);
            } catch (RemoteException re) {
                throw new WSException(re);
            }
        }
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

    /**
     * @see eu.sqooss.scl.WSConnection#displayUser(long)
     */
    public WSResult displayUser(long userId) throws WSException {
        DisplayUserResponse response;
        DisplayUser params = (DisplayUser) parameters.get(
                WSConnectionConstants.METHOD_NAME_DISPLAY_USER);
        synchronized (params) {
            params.setUserId(userId);
            try {
                response = wsStub.displayUser(params);
            } catch (RemoteException re) {
                throw new WSException(re);
            }
        }
        return WSResponseParser.parseUsers(new WSUser[] {response.get_return()});
    }

    /**
     * @see eu.sqooss.scl.WSConnection#evaluatedProjectsList()
     */
    public WSResult evaluatedProjectsList() throws WSException {
        EvaluatedProjectsListResponse response; 
        EvaluatedProjectsList params = (EvaluatedProjectsList) parameters.get(
                WSConnectionConstants.METHOD_NAME_EVALUATED_PROJECTS_LIST);
        synchronized (params) {
            try {
                response = wsStub.evaluatedProjectsList(params);
            } catch (RemoteException e) {
                throw new WSException(e);
            }
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

    /**
     * @see eu.sqooss.scl.WSConnection#modifyUser(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void modifyUser(String userName, String newNames, String newPassword,
            String newUserClass, String newOtherInfo) throws WSException {
        ModifyUser params = (ModifyUser) parameters.get(WSConnectionConstants.METHOD_NAME_MODIFY_USER);
        synchronized (params) {
            params.setUserName(userName);
            params.setNewNames(newNames);
            params.setNewPassword(newPassword);
            params.setNewUserClass(newUserClass);
            params.setNewOtherInfo(newOtherInfo);
            try {
                wsStub.modifyUser(params);
            } catch (RemoteException re) {
                throw new WSException(re);
            }
        }
    }

    public WSResult ratedProjectsList() {
        // TODO Auto-generated method stub
        return new WSResult("Not Implemented yet");
    }

    public WSResult requestDefectStatistics(String projectId, String searchQuery, String statisticalScheme) {
        // TODO Auto-generated method stub
        return new WSResult("Not Implemented yet");
    }

    /**
     * @see eu.sqooss.scl.WSConnection#requestEvaluation4Project(java.lang.String, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public WSResult requestEvaluation4Project(String projectName, long projectVersion,
            String srcRepositoryLocation, String mailingListLocation,
            String BTSLocation, String userEmailAddress, String website) throws WSException {
        RequestEvaluation4ProjectResponse response;
        RequestEvaluation4Project params = (RequestEvaluation4Project) parameters.get(
                WSConnectionConstants.METHOD_NAME_REQUEST_EVALUATION_4_PROJECT);
        synchronized (params) {
            params.setProjectName(projectName);
            params.setProjectVersion(projectVersion);
            params.setSrcRepositoryLocation(srcRepositoryLocation);
            params.setMailingListLocation(mailingListLocation);
            params.setBTSLocation(BTSLocation);
            params.setUserEmailAddress(userEmailAddress);
            params.setWebsite(website);
            try {
                response = wsStub.requestEvaluation4Project(params);
            } catch (RemoteException re) {
                throw new WSException(re);
            }
        }
        return WSResponseParser.parseStoredProjects(new WSStoredProject[]{response.get_return()});
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

    /**
     * @see eu.sqooss.scl.WSConnection#retrieveFileList(long)
     */
    public WSResult retrieveFileList(long projectId) throws WSException {
        RetrieveFileListResponse response;
        RetrieveFileList params = (RetrieveFileList) parameters.get(
                WSConnectionConstants.METHOD_NAME_RETRIEVE_FILE_LIST);
        synchronized (params) {
            params.setProjectId(projectId);
            try {
                response = wsStub.retrieveFileList(params);
            } catch (RemoteException re) {
                throw new WSException(re);
            }
        }
        return WSResponseParser.parseProjectFiles(response.get_return());
    }

   /**
    * @see eu.sqooss.scl.WSConnection#retrieveMetrics4SelectedFiles(long, java.lang.String, java.lang.String)
    */
    public WSResult retrieveMetrics4SelectedFiles(long projectId, String folderNames,
            String fileNames) throws WSException {
        String delimiter = ",";
        
        StringTokenizer folderNamesTokenizer = new StringTokenizer(folderNames, delimiter);
        int folderNamesNumber = folderNamesTokenizer.countTokens();
        String[] folderNamesArray;
        if (folderNamesNumber == 0) {
            folderNamesArray = new String[1];
            folderNamesArray[0] = null;
        } else {
            folderNamesArray = new String[folderNamesNumber];
            for (int i = 0; i < folderNamesArray.length; i++) {
                folderNamesArray[i] = folderNamesTokenizer.nextToken().trim();
            }
        }
        
        StringTokenizer fileNamesTokenizer = new StringTokenizer(fileNames, delimiter);
        int fileNamesNumber = fileNamesTokenizer.countTokens();
        String[] fileNamesArray;
        if (fileNamesNumber == 0) {
            fileNamesArray = new String[1];
            fileNamesArray[0] = null;
        } else {
            fileNamesArray = new String[fileNamesNumber];
            for (int i = 0; i < fileNamesArray.length; i++) {
                fileNamesArray[i] = fileNamesTokenizer.nextToken().trim();
            }
        }
        
        RetrieveMetrics4SelectedFilesResponse response;
        RetrieveMetrics4SelectedFiles params = (RetrieveMetrics4SelectedFiles) parameters.get(
                WSConnectionConstants.METHOD_NAME_RETRIEVE_METRICS_4_SELECTED_FILES);
        synchronized (params) {
            params.setProjectId(projectId);
            params.setFolders(folderNamesArray);
            params.setFileNames(fileNamesArray);
            try {
                response = wsStub.retrieveMetrics4SelectedFiles(params);
            } catch (RemoteException re) {
                throw new WSException(re);
            }
        }
        return WSResponseParser.parseMetrics(response.get_return());
    }

    /**
     * @see eu.sqooss.scl.WSConnection#retrieveMetrics4SelectedProject(long)
     */
    public WSResult retrieveMetrics4SelectedProject(long projectId) throws WSException {
        RetrieveMetrics4SelectedProjectResponse response;
        RetrieveMetrics4SelectedProject params = (RetrieveMetrics4SelectedProject) parameters.get(
                WSConnectionConstants.METHOD_NAME_RETRIEVE_METRICS_4_SELECTED_PROJECT);
        synchronized (params) {
            params.setProjectId(projectId);
            try {
                response = wsStub.retrieveMetrics4SelectedProject(params);
            } catch (RemoteException re) {
                throw new WSException(re);
            }
        }
        return WSResponseParser.parseMetrics(response.get_return());
    }

    public WSResult retrieveProjectRatings(String projectId) {
        // TODO Auto-generated method stub
        return new WSResult("Not Implemented yet");
    }

    /**
     * @see eu.sqooss.scl.WSConnection#retrieveSelectedMetric(long, long)
     */
    public WSResult retrieveSelectedMetric(long projectId, long metricId) throws WSException {
        //TODO: should be replaced
//        RetrieveSelectedMetricResponse response;
//        RetrieveSelectedMetric params = (RetrieveSelectedMetric) parameters.get(
//                WSConnectionConstants.METHOD_NAME_RETRIEVE_SELECTED_METRIC);
//        synchronized (params) {
//            params.setProjectId(projectId);
//            params.setMetricId(metricId);
//            try {
//                response = wsStub.retrieveSelectedMetric(params);
//            } catch (RemoteException e) {
//                throw new WSException(e);
//            }
//        }
//        return WSResponseParser.parseMetrics(new WSMetric[]{response.get_return()});
        return new WSResult("Not Implement yet");
    }

    public void submitScores(String projectId, String[] scores, String textOpinion) {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see eu.sqooss.scl.WSConnection#submitUser(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public WSResult submitUser(String newUserName, String newNames, String newPassword,
            String newUserClass, String newOtherInfo) throws WSException {
        SubmitUserResponse response;
        SubmitUser params = (SubmitUser) parameters.get(
                WSConnectionConstants.METHOD_NAME_SUBMIT_USER);
        synchronized (params) {
            params.setNewUserName(newUserName);
            params.setNewNames(newNames);
            params.setNewPassword(newPassword);
            params.setNewUserClass(newUserClass);
            params.setNewOtherInfo(newOtherInfo);

            try {
                response = wsStub.submitUser(params);
            } catch (RemoteException re) {
                throw new WSException(re);
            }
        }
        return WSResponseParser.parseUsers(new WSUser[] {response.get_return()});
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
    
    public WSResult retrieveProjectId(String projectName) throws WSException {
        RetrieveProjectIdResponse response;
        RetrieveProjectId params = (RetrieveProjectId) parameters.get(
                WSConnectionConstants.METHOD_NAME_RETRIEVE_PROJECT_ID);
        synchronized (params) {
            params.setProjectName(projectName);
            try {
                response = wsStub.retrieveProjectId(params);
            } catch (RemoteException re) {
                throw new WSException(re);
            }
        }
        
        long result = response.get_return();
        
        return new WSResult(result);
        
    }
    
    private void initParameters() {
        parameters = new Hashtable<String, Object>();
        
        EvaluatedProjectsList epl = new EvaluatedProjectsList();
        epl.setPassword(password);
        epl.setUserName(userName);
        parameters.put(WSConnectionConstants.METHOD_NAME_EVALUATED_PROJECTS_LIST, epl);
        
        RetrieveMetrics4SelectedProject rm4sp = new RetrieveMetrics4SelectedProject();
        rm4sp.setUserName(userName);
        rm4sp.setPassword(password);
        parameters.put(WSConnectionConstants.METHOD_NAME_RETRIEVE_METRICS_4_SELECTED_PROJECT, rm4sp);
        
        RetrieveSelectedMetric rsm = new RetrieveSelectedMetric();
        rsm.setUserName(userName);
        rsm.setPassword(password);
        parameters.put(WSConnectionConstants.METHOD_NAME_RETRIEVE_SELECTED_METRIC, rsm);
        
        RetrieveMetrics4SelectedFiles rm4sf = new RetrieveMetrics4SelectedFiles();
        rm4sf.setPassword(password);
        rm4sf.setUserName(userName);
        parameters.put(WSConnectionConstants.METHOD_NAME_RETRIEVE_METRICS_4_SELECTED_FILES, rm4sf);
        
        RetrieveFileList rfl = new RetrieveFileList();
        rfl.setPassword(password);
        rfl.setUserName(userName);
        parameters.put(WSConnectionConstants.METHOD_NAME_RETRIEVE_FILE_LIST, rfl);
        
        RequestEvaluation4Project refp = new RequestEvaluation4Project();
        refp.setPassword(password);
        refp.setUserName(userName);
        parameters.put(WSConnectionConstants.METHOD_NAME_REQUEST_EVALUATION_4_PROJECT, refp);
        
        DisplayUser displayUser = new DisplayUser();
        displayUser.setPasswordForAccess(password);
        displayUser.setUserNameForAccess(userName);
        parameters.put(WSConnectionConstants.METHOD_NAME_DISPLAY_USER, displayUser);
        
        DeleteUser deleteUser = new DeleteUser();
        deleteUser.setPasswordForAccess(password);
        deleteUser.setUserNameForAccess(userName);
        parameters.put(WSConnectionConstants.METHOD_NAME_DELETE_USER, deleteUser);
        
        ModifyUser modifyUser = new ModifyUser();
        modifyUser.setPasswordForAccess(password);
        modifyUser.setUserNameForAccess(userName);
        parameters.put(WSConnectionConstants.METHOD_NAME_MODIFY_USER, modifyUser);
        
        SubmitUser submitUser = new SubmitUser();
        submitUser.setPasswordForAccess(password);
        submitUser.setUserNameForAccess(userName);
        parameters.put(WSConnectionConstants.METHOD_NAME_SUBMIT_USER, submitUser);
        
        RetrieveProjectId retrieveProjectIdParam = new RetrieveProjectId();
        retrieveProjectIdParam.setPasswrod(password);
        retrieveProjectIdParam.setUserName(userName);
        parameters.put(WSConnectionConstants.METHOD_NAME_RETRIEVE_PROJECT_ID, retrieveProjectIdParam);
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
