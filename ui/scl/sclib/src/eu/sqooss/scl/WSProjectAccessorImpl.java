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

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Map;

import org.apache.axis2.AxisFault;

import eu.sqooss.scl.accessor.WSProjectAccessor;
import eu.sqooss.ws.client.WsStub;
import eu.sqooss.ws.client.datatypes.WSProjectFile;
import eu.sqooss.ws.client.datatypes.WSProjectVersion;
import eu.sqooss.ws.client.datatypes.WSStoredProject;
import eu.sqooss.ws.client.ws.GetEvaluatedProjects;
import eu.sqooss.ws.client.ws.GetEvaluatedProjectsResponse;
import eu.sqooss.ws.client.ws.GetFilesByProjectId;
import eu.sqooss.ws.client.ws.GetFilesByProjectIdResponse;
import eu.sqooss.ws.client.ws.GetFilesByProjectVersionId;
import eu.sqooss.ws.client.ws.GetFilesByProjectVersionIdResponse;
import eu.sqooss.ws.client.ws.GetFilesNumberByProjectVersionId;
import eu.sqooss.ws.client.ws.GetFilesNumberByProjectVersionIdResponse;
import eu.sqooss.ws.client.ws.GetProjectById;
import eu.sqooss.ws.client.ws.GetProjectByIdResponse;
import eu.sqooss.ws.client.ws.GetProjectIdByName;
import eu.sqooss.ws.client.ws.GetProjectIdByNameResponse;
import eu.sqooss.ws.client.ws.GetProjectVersionsByProjectId;
import eu.sqooss.ws.client.ws.GetProjectVersionsByProjectIdResponse;
import eu.sqooss.ws.client.ws.GetStoredProjects;
import eu.sqooss.ws.client.ws.GetStoredProjectsResponse;
import eu.sqooss.ws.client.ws.RequestEvaluation4Project;
import eu.sqooss.ws.client.ws.RequestEvaluation4ProjectResponse;

class WSProjectAccessorImpl extends WSProjectAccessor {

    private static final String METHOD_NAME_GET_EVALUATED_PROJECTS       = "evaluatedProjectsList";

    private static final String METHOD_NAME_GET_STORED_PROJECTS          = "storedProjectsList";

    private static final String METHOD_NAME_GET_FILES_BY_PROJECT_ID      = "retrieveFileList";

    private static final String METHOD_NAME_REQUEST_EVALUATION_4_PROJECT = "requestEvaluation4Project";

    private static final String METHOD_NAME_GET_PROJECT_ID_BY_NAME       = "retrieveProjectId";

    private static final String METHOD_NAME_GET_PROJECT_VERSIONS_BY_ID   = "retrieveStoredProjectVersions";

    private static final String METHOD_NAME_GET_PROJECT_BY_ID            = "retrieveStoredProject";

    private static final String METHOD_NAME_GET_FILES_NUMBER_BY_PROJECT_VERSION_ID   = "getFilesNumber4ProjectVersion";

    private static final String METHOD_NAME_GET_FILES_BY_PROJECT_VERSION_ID          = "getFileList4ProjectVersion";

    private Map<String, Object> parameters;
    private String userName;
    private String password;
    private WsStub wsStub;

    public WSProjectAccessorImpl(String userName, String password, String webServiceUrl) throws WSException {
        this.userName = userName;
        this.password = password;
        parameters = new Hashtable<String, Object>();
        try {
            this.wsStub = new WsStub(webServiceUrl);
        } catch (AxisFault af) {
            throw new WSException(af);
        }
    }

    /**
     * @see eu.sqooss.scl.accessor.WSProjectAccessor#getEvaluatedProjects()
     */
    @Override
    public WSStoredProject[] getEvaluatedProjects() throws WSException {
        GetEvaluatedProjectsResponse response;
        GetEvaluatedProjects params;
        if (!parameters.containsKey(METHOD_NAME_GET_EVALUATED_PROJECTS)) {
            params = new GetEvaluatedProjects();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_EVALUATED_PROJECTS, params);
        } else {
            params = (GetEvaluatedProjects) parameters.get(
                    METHOD_NAME_GET_EVALUATED_PROJECTS);
        }
        synchronized (params) {
            try {
                response = wsStub.getEvaluatedProjects(params);
            } catch (RemoteException e) {
                throw new WSException(e);
            }
        }
        return (WSStoredProject[]) normaliseWSArrayResult(response.get_return());
    }

    /**
     * @see eu.sqooss.scl.accessor.WSProjectAccessor#getStoredProjects()
     */
    @Override
    public WSStoredProject[] getStoredProjects() throws WSException {
        GetStoredProjectsResponse response;
        GetStoredProjects params;
        if (!parameters.containsKey(METHOD_NAME_GET_STORED_PROJECTS)) {
            params = new GetStoredProjects();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_STORED_PROJECTS, params);
        } else {
            params = (GetStoredProjects) parameters.get(
                    METHOD_NAME_GET_STORED_PROJECTS);
        }
        synchronized (params) {
            try {
                response = wsStub.getStoredProjects(params);
            } catch (RemoteException e) {
                throw new WSException(e);
            }
        }
        return (WSStoredProject[]) normaliseWSArrayResult(response.get_return());
    }

    /**
     * @see eu.sqooss.scl.accessor.WSProjectAccessor#getFilesByProjectId(long)
     */
    @Override
    public WSProjectFile[] getFilesByProjectId(long projectId) throws WSException {
        GetFilesByProjectIdResponse response;
        GetFilesByProjectId params;
        if (!parameters.containsKey(METHOD_NAME_GET_FILES_BY_PROJECT_ID)) {
            params = new GetFilesByProjectId();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_FILES_BY_PROJECT_ID, params);
        } else {
            params = (GetFilesByProjectId) parameters.get(
                    METHOD_NAME_GET_FILES_BY_PROJECT_ID);
        }
        synchronized (params) {
            params.setProjectId(projectId);
            try {
                response = wsStub.getFilesByProjectId(params);
            } catch (RemoteException re) {
                throw new WSException(re);
            }
        }
        return (WSProjectFile[]) normaliseWSArrayResult(response.get_return());
    }

    /**
     * @see eu.sqooss.scl.accessor.WSProjectAccessor#getFilesByProjectVersionId(long)
     */
    @Override
    public WSProjectFile[] getFilesByProjectVersionId(long projectVersionId) throws WSException {
        GetFilesByProjectVersionIdResponse response;
        GetFilesByProjectVersionId params;
        if (!parameters.containsKey(METHOD_NAME_GET_FILES_BY_PROJECT_VERSION_ID)) {
            params = new GetFilesByProjectVersionId();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_FILES_BY_PROJECT_VERSION_ID, params);
        } else {
            params = (GetFilesByProjectVersionId) parameters.get(
                    METHOD_NAME_GET_FILES_BY_PROJECT_VERSION_ID);
        }
        synchronized (params) {
            params.setProjectVersionId(projectVersionId);
            try {
                response = wsStub.getFilesByProjectVersionId(params);
            } catch (RemoteException re) {
                throw new WSException(re);
            }
        }
        return (WSProjectFile[]) normaliseWSArrayResult(response.get_return());
    }

    /**
     * @see eu.sqooss.scl.accessor.WSProjectAccessor#getFilesNumberByProjectVersionId(long)
     */
    @Override
    public long getFilesNumberByProjectVersionId(long projectVersionId) throws WSException {
        GetFilesNumberByProjectVersionIdResponse response;
        GetFilesNumberByProjectVersionId params;
        if (!parameters.containsKey(METHOD_NAME_GET_FILES_NUMBER_BY_PROJECT_VERSION_ID)) {
            params = new GetFilesNumberByProjectVersionId();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_FILES_NUMBER_BY_PROJECT_VERSION_ID, params);
        } else {
            params = (GetFilesNumberByProjectVersionId) parameters.get(
                    METHOD_NAME_GET_FILES_NUMBER_BY_PROJECT_VERSION_ID);
        }
        synchronized (params) {
            params.setProjectVersionId(projectVersionId);
            try {
                response = wsStub.getFilesNumberByProjectVersionId(params);
            } catch (RemoteException re) {
                throw new WSException(re);
            }
        }

        return response.get_return();
    }

    /**
     * @see eu.sqooss.scl.accessor.WSProjectAccessor#requestEvaluation4Project(java.lang.String, long, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public WSStoredProject requestEvaluation4Project(String projectName, long projectVersion,
            String srcRepositoryLocation, String mailingListLocation,
            String BTSLocation, String userEmailAddress, String website) throws WSException {
        RequestEvaluation4ProjectResponse response;
        RequestEvaluation4Project params;
        if (!parameters.containsKey(METHOD_NAME_REQUEST_EVALUATION_4_PROJECT)) {
            params = new RequestEvaluation4Project();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_REQUEST_EVALUATION_4_PROJECT, params);
        } else {
            params = (RequestEvaluation4Project) parameters.get(
                    METHOD_NAME_REQUEST_EVALUATION_4_PROJECT);
        }
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
        return response.get_return();
    }

    /**
     * @see eu.sqooss.scl.accessor.WSProjectAccessor#getProjectIdByName(java.lang.String)
     */
    @Override
    public long getProjectIdByName(String projectName) throws WSException {
        GetProjectIdByNameResponse response;
        GetProjectIdByName params;
        if (!parameters.containsKey(METHOD_NAME_GET_PROJECT_ID_BY_NAME)) {
            params = new GetProjectIdByName();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_PROJECT_ID_BY_NAME, params);
        } else {
            params = (GetProjectIdByName) parameters.get(
                    METHOD_NAME_GET_PROJECT_ID_BY_NAME);
        }
        synchronized (params) {
            params.setProjectName(projectName);
            try {
                response = wsStub.getProjectIdByName(params);
            } catch (RemoteException re) {
                throw new WSException(re);
            }
        }

        return response.get_return();
    }

    /**
     * @see eu.sqooss.scl.accessor.WSProjectAccessor#getProjectVersionsById(long)
     */
    @Override
    public WSProjectVersion[] getProjectVersionsById(long projectId) throws WSException {
        GetProjectVersionsByProjectIdResponse response;
        GetProjectVersionsByProjectId params;
        if (!parameters.containsKey(METHOD_NAME_GET_PROJECT_VERSIONS_BY_ID)) {
            params = new GetProjectVersionsByProjectId();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_PROJECT_VERSIONS_BY_ID, params);
        } else {
            params = (GetProjectVersionsByProjectId) parameters.get(
                    METHOD_NAME_GET_PROJECT_VERSIONS_BY_ID);
        }
        synchronized (params) {
            params.setProjectId(projectId);
            try {
                response = wsStub.getProjectVersionsByProjectId(params);
            } catch (RemoteException re) {
                throw new WSException(re);
            }
        }

        return (WSProjectVersion[]) normaliseWSArrayResult(response.get_return());

    }

    /**
     * @see eu.sqooss.scl.accessor.WSProjectAccessor#getProjectById(long)
     */
    @Override
    public WSStoredProject getProjectById(long projectId) throws WSException {
        GetProjectByIdResponse response;
        GetProjectById params;
        if (!parameters.containsKey(METHOD_NAME_GET_PROJECT_BY_ID)) {
            params = new GetProjectById();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_PROJECT_BY_ID, params);
        } else {
            params = (GetProjectById) parameters.get(
                    METHOD_NAME_GET_PROJECT_BY_ID);
        }
        synchronized (params) {
            params.setProjectId(projectId);
            try {
                response = wsStub.getProjectById(params);
            } catch (RemoteException re) {
                throw new WSException(re);
            }
        }

        return (WSStoredProject) normaliseWSArrayResult(response.get_return());

    }

}

//vi: ai nosi sw=4 ts=4 expandtab
