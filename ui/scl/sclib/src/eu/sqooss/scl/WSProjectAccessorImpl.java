/*
 * Copyright 2008 - Organization for Free and Open Source Software,
 *                Athens, Greece.
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

import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.axis2.AxisFault;

import eu.sqooss.scl.accessor.WSProjectAccessor;
import eu.sqooss.ws.client.WsStub;
import eu.sqooss.ws.client.datatypes.WSDeveloper;
import eu.sqooss.ws.client.datatypes.WSDirectory;
import eu.sqooss.ws.client.datatypes.WSFileGroup;
import eu.sqooss.ws.client.datatypes.WSFileModification;
import eu.sqooss.ws.client.datatypes.WSMailMessage;
import eu.sqooss.ws.client.datatypes.WSProjectFile;
import eu.sqooss.ws.client.datatypes.WSProjectVersion;
import eu.sqooss.ws.client.datatypes.WSStoredProject;
import eu.sqooss.ws.client.datatypes.WSShortMailMessage;
import eu.sqooss.ws.client.datatypes.WSShortProjectVersion;
import eu.sqooss.ws.client.datatypes.WSTaggedVersion;
import eu.sqooss.ws.client.datatypes.WSVersionStats;
import eu.sqooss.ws.client.ws.GetDevelopersByIds;
import eu.sqooss.ws.client.ws.GetDevelopersByIdsResponse;
import eu.sqooss.ws.client.ws.GetDirectoriesByIds;
import eu.sqooss.ws.client.ws.GetDirectoriesByIdsResponse;
import eu.sqooss.ws.client.ws.GetEvaluatedProjects;
import eu.sqooss.ws.client.ws.GetEvaluatedProjectsResponse;
import eu.sqooss.ws.client.ws.GetFileGroupsByProjectVersionId;
import eu.sqooss.ws.client.ws.GetFileGroupsByProjectVersionIdResponse;
import eu.sqooss.ws.client.ws.GetFileModifications;
import eu.sqooss.ws.client.ws.GetFileModificationsResponse;
import eu.sqooss.ws.client.ws.GetFilesByRegularExpression;
import eu.sqooss.ws.client.ws.GetFilesByRegularExpressionResponse;
import eu.sqooss.ws.client.ws.GetFilesInDirectory;
import eu.sqooss.ws.client.ws.GetFilesInDirectoryResponse;
import eu.sqooss.ws.client.ws.GetFilesNumberByProjectVersionId;
import eu.sqooss.ws.client.ws.GetFilesNumberByProjectVersionIdResponse;
import eu.sqooss.ws.client.ws.GetFirstProjectVersions;
import eu.sqooss.ws.client.ws.GetFirstProjectVersionsResponse;
import eu.sqooss.ws.client.ws.GetLastProjectVersions;
import eu.sqooss.ws.client.ws.GetLastProjectVersionsResponse;
import eu.sqooss.ws.client.ws.GetPreviousVersionById;
import eu.sqooss.ws.client.ws.GetPreviousVersionByIdResponse;
import eu.sqooss.ws.client.ws.GetMailTimeline;
import eu.sqooss.ws.client.ws.GetMailTimelineResponse;
import eu.sqooss.ws.client.ws.GetNextVersionById;
import eu.sqooss.ws.client.ws.GetNextVersionByIdResponse;
import eu.sqooss.ws.client.ws.GetProjectByName;
import eu.sqooss.ws.client.ws.GetProjectByNameResponse;
import eu.sqooss.ws.client.ws.GetProjectVersionsByIds;
import eu.sqooss.ws.client.ws.GetProjectVersionsByIdsResponse;
import eu.sqooss.ws.client.ws.GetProjectVersionsByTimestamps;
import eu.sqooss.ws.client.ws.GetProjectVersionsByTimestampsResponse;
import eu.sqooss.ws.client.ws.GetProjectVersionsByScmIds;
import eu.sqooss.ws.client.ws.GetProjectVersionsByScmIdsResponse;
import eu.sqooss.ws.client.ws.GetProjectsByIds;
import eu.sqooss.ws.client.ws.GetProjectsByIdsResponse;
import eu.sqooss.ws.client.ws.GetRootDirectory;
import eu.sqooss.ws.client.ws.GetRootDirectoryResponse;
import eu.sqooss.ws.client.ws.GetSCMTimeline;
import eu.sqooss.ws.client.ws.GetSCMTimelineResponse;
import eu.sqooss.ws.client.ws.GetShortMailTimeline;
import eu.sqooss.ws.client.ws.GetShortMailTimelineResponse;
import eu.sqooss.ws.client.ws.GetShortSCMTimeline;
import eu.sqooss.ws.client.ws.GetShortSCMTimelineResponse;
import eu.sqooss.ws.client.ws.GetStoredProjects;
import eu.sqooss.ws.client.ws.GetStoredProjectsResponse;
import eu.sqooss.ws.client.ws.GetTaggedVersionsByProjectId;
import eu.sqooss.ws.client.ws.GetTaggedVersionsByProjectIdResponse;
import eu.sqooss.ws.client.ws.GetVersionsCount;
import eu.sqooss.ws.client.ws.GetVersionsCountResponse;
import eu.sqooss.ws.client.ws.GetVersionsStatistics;
import eu.sqooss.ws.client.ws.GetVersionsStatisticsResponse;

/**
 *
 * @author Evgeni Grigorov, <tt>(ProSyst Software GmbH)</tt>
 * @author Boryan Yotov, <tt>(ProSyst Software GmbH)</tt>
 */
class WSProjectAccessorImpl extends WSProjectAccessor {

    private static final String METHOD_NAME_GET_EVALUATED_PROJECTS =
        "getEvaluatedProjects";
    private static final String METHOD_NAME_GET_STORED_PROJECTS =
        "getStoredProjects";
    private static final String METHOD_NAME_GET_PROJECT_BY_NAME =
        "getProjectByName";
    private static final String METHOD_NAME_GET_PROJECT_VERSIONS_BY_IDS =
        "getProjectVersionsByIds";
    private static final String METHOD_NAME_GET_PROJECT_VERSIONS_BY_TIMESTAMPS =
        "getProjectVersionsByTimestamps";
    private static final String METHOD_NAME_GET_PROJECT_VERSIONS_BY_SCM_IDS =
        "getProjectVersionsByScmIds";
    private static final String METHOD_NAME_GET_VERSIONS_COUNT =
        "getVersionsCount";
    private static final String METHOD_NAME_GET_VERSIONS_STATISTICS =
        "getVersionsStatistics";
    private static final String METHOD_NAME_GET_FIRST_PROJECT_VERSIONS =
        "getFirstProjectVersions";
    private static final String METHOD_NAME_GET_LAST_PROJECT_VERSIONS =
        "getLastProjectVersions";
    private static final String METHOD_NAME_GET_PREVIOUS_VERSION_BY_ID =
        "getPreviousVersionById";
    private static final String METHOD_NAME_GET_NEXT_VERSION_BY_ID =
        "getNextVersionById";
    private static final String METHOD_NAME_GET_PROJECTS_BY_IDS =
        "getProjectsByIds";
    private static final String METHOD_NAME_GET_FILES_NUMBER_BY_PROJECT_VERSION_ID =
        "getFilesNumberByProjectVersionId";
    private static final String METHOD_NAME_GET_FILES_BY_REGULAR_EXPRESSION =
        "getFilesByRegularExpression";
    private static final String METHOD_NAME_GET_DIRECTORIES_BY_IDS =
        "getDirectoriesByIds";
    private static final String METHOD_NAME_GET_DEVELOPERS_BY_IDS =
        "getDevelopersByIds";
    private static final String METHOD_NAME_GET_FILE_GROUPS_BY_PROJECT_VERSION_ID =
        "getFileGroupsByProjectVersionId";
    private static final String METHOD_NAME_GET_ROOT_DIRECTORY =
        "getRootDirectory";
    private static final String METHOD_NAME_GET_FILES_IN_DIRECTORY =
        "getFilesInDirectory";
    private static final String METHOD_NAME_GET_FILE_MODIFICATIONS =
        "getFileModifications";
    private static final String METHOD_NAME_GET_TAGGED_VERSIONS_BY_PROJECT_ID =
        "getTaggedVersionsByProjectId";
    private static final String METHOD_NAME_GET_SCM_TIMELINE =
        "getSCMTimeline";
    private static final String METHOD_NAME_GET_SHORT_SCM_TIMELINE =
        "getShortSCMTimeline";
    private static final String METHOD_NAME_GET_MAIL_TIMELINE =
        "getMailTimeline";
    private static final String METHOD_NAME_GET_SHORT_MAIL_TIMELINE =
        "getShortMailTimeline";

    private static final WSStoredProject[] EMPTY_ARRAY_STORED_PROJECTS =
        new WSStoredProject[0];
    private static final WSProjectVersion[] EMPTY_ARRAY_PROJECT_VERSIONS =
        new WSProjectVersion[0];
    private static final WSDirectory[] EMPTY_ARRAY_DIRECTORIES =
        new WSDirectory[0];
    private static final WSDeveloper[] EMPTY_ARRAY_DEVELOPERS =
        new WSDeveloper[0];
    
    private Map<String, Object> parameters;
    private String userName;
    private String password;
    private WsStub wsStub;

    public WSProjectAccessorImpl(
            String userName,
            String password,
            String webServiceUrl) throws WSException {
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
     * @see eu.sqooss.scl.accessor.WSProjectAccessor#getFileGroupsByProjectVersionId(long)
     */
    @Override
    public WSFileGroup[] getFileGroupsByProjectVersionId(long projectVersionId) throws WSException {
        GetFileGroupsByProjectVersionIdResponse response;
        GetFileGroupsByProjectVersionId params;
        if (!parameters.containsKey(METHOD_NAME_GET_FILE_GROUPS_BY_PROJECT_VERSION_ID)) {
            params = new GetFileGroupsByProjectVersionId();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_FILE_GROUPS_BY_PROJECT_VERSION_ID, params);
        } else {
            params = (GetFileGroupsByProjectVersionId) parameters.get(
                    METHOD_NAME_GET_FILE_GROUPS_BY_PROJECT_VERSION_ID);
        }
        synchronized (params) {
            params.setProjectVersionId(projectVersionId);
            try {
                response = wsStub.getFileGroupsByProjectVersionId(params);
            } catch (Exception e) {
                throw new WSException(e);
            }
        }
        return response.get_return();
    }

    /**
     * @see eu.sqooss.scl.accessor.WSProjectAccessor#getFileGroupsByProjectVersionId(long)
     */
    @Override
    public WSTaggedVersion[] getTaggedVersionsByProjectId(
            long projectId) throws WSException {
        GetTaggedVersionsByProjectIdResponse response;
        GetTaggedVersionsByProjectId params;
        if (!parameters.containsKey(METHOD_NAME_GET_TAGGED_VERSIONS_BY_PROJECT_ID)) {
            params = new GetTaggedVersionsByProjectId();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_TAGGED_VERSIONS_BY_PROJECT_ID, params);
        } else {
            params = (GetTaggedVersionsByProjectId) parameters.get(
                    METHOD_NAME_GET_TAGGED_VERSIONS_BY_PROJECT_ID);
        }
        synchronized (params) {
            params.setProjectId(projectId);
            try {
                response = wsStub.getTaggedVersionsByProjectId(params);
            } catch (Exception e) {
                throw new WSException(e);
            }
        }
        return response.get_return();
    }

    /**
     * @see eu.sqooss.scl.accessor.WSProjectAccessor#getRootDirectory(long)
     */
    @Override
    public WSDirectory getRootDirectory(long projectId) throws WSException {
        GetRootDirectoryResponse response;
        GetRootDirectory params;
        if (!parameters.containsKey(METHOD_NAME_GET_ROOT_DIRECTORY)) {
            params = new GetRootDirectory();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_ROOT_DIRECTORY, params);
        } else {
            params = (GetRootDirectory) parameters.get(
                    METHOD_NAME_GET_ROOT_DIRECTORY);
        }
        synchronized (params) {
            params.setProjectId(projectId);
            try {
                response = wsStub.getRootDirectory(params);
            } catch (Exception e) {
                throw new WSException(e);
            }
        }
        return response.get_return();
    }

    /**
     * @see eu.sqooss.scl.accessor.WSProjectAccessor#getFilesInDirectory(long, long)
     */
    @Override
    public WSProjectFile[] getFilesInDirectory(
            long projectVersionId,
            long directoryId) throws WSException {
        GetFilesInDirectoryResponse response;
        GetFilesInDirectory params;
        if (!parameters.containsKey(METHOD_NAME_GET_FILES_IN_DIRECTORY)) {
            params = new GetFilesInDirectory();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_FILES_IN_DIRECTORY, params);
        } else {
            params = (GetFilesInDirectory) parameters.get(
                    METHOD_NAME_GET_FILES_IN_DIRECTORY);
        }
        synchronized (params) {
            params.setProjectVersionId(projectVersionId);
            params.setDirectoryId(directoryId);
            try {
                response = wsStub.getFilesInDirectory(params);
            } catch (Exception e) {
                throw new WSException(e);
            }
        }
        return (WSProjectFile[]) normalizeWSArrayResult(response.get_return());
    }

    /**
     * @see eu.sqooss.scl.accessor.WSProjectAccessor#getFileModifications(long, long)
     */
    @Override
    public WSFileModification[] getFileModifications(
            long projectVersionId,
            long projectFileId) throws WSException {
        GetFileModificationsResponse response;
        GetFileModifications params;
        if (!parameters.containsKey(METHOD_NAME_GET_FILE_MODIFICATIONS)) {
            params = new GetFileModifications();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_FILE_MODIFICATIONS, params);
        } else {
            params = (GetFileModifications) parameters.get(
                    METHOD_NAME_GET_FILE_MODIFICATIONS);
        }
        synchronized (params) {
            params.setProjectVersionId(projectVersionId);
            params.setProjectFileId(projectFileId);
            try {
                response = wsStub.getFileModifications(params);
            } catch (Exception e) {
                throw new WSException(e);
            }
        }
        return (WSFileModification[]) normalizeWSArrayResult(
                response.get_return());
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
            } catch (Exception e) {
                throw new WSException(e);
            }
        }
        return (WSStoredProject[]) normalizeWSArrayResult(response.get_return());
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
            } catch (Exception e) {
                throw new WSException(e);
            }
        }
        return (WSStoredProject[]) normalizeWSArrayResult(response.get_return());
    }

    /**
     * @see eu.sqooss.scl.accessor.WSProjectAccessor#getVersionsStatistics(long[])
     */
    @Override
    public WSVersionStats[] getVersionsStatistics(long[] projectVersionsIds)
    throws WSException {
        GetVersionsStatisticsResponse response;
        GetVersionsStatistics params;
        if (!parameters.containsKey(METHOD_NAME_GET_VERSIONS_STATISTICS)) {
            params = new GetVersionsStatistics();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_VERSIONS_STATISTICS, params);
        } else {
            params = (GetVersionsStatistics) parameters.get(
                    METHOD_NAME_GET_VERSIONS_STATISTICS);
        }
        synchronized (params) {
            params.setProjectVersionsIds(projectVersionsIds);
            try {
                response = wsStub.getVersionsStatistics(params);
            } catch (Exception e) {
                throw new WSException(e);
            }
        }

        return response.get_return();
    }

    @Override
    public long getVersionsCount(long projectId) throws WSException {
        GetVersionsCountResponse response;
        GetVersionsCount params;
        if (!parameters.containsKey(METHOD_NAME_GET_VERSIONS_COUNT)) {
            params = new GetVersionsCount();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_VERSIONS_COUNT, params);
        } else {
            params = (GetVersionsCount) parameters.get(
                    METHOD_NAME_GET_VERSIONS_COUNT);
        }
        synchronized (params) {
            params.setProjectId(projectId);
            try {
                response = wsStub.getVersionsCount(params);
            } catch (Exception e) {
                throw new WSException(e);
            }
        }

        return response.get_return();
    }

    /**
     * @see eu.sqooss.scl.accessor.WSProjectAccessor#getFilesByRegularExpression(long, java.lang.String)
     */
    @Override
    public WSProjectFile[] getFilesByRegularExpression(long projectVersionId,
            String regExpr) throws WSException {
        try {
            Pattern.compile(regExpr);
        } catch (PatternSyntaxException pse) {
            throw new WSException(pse);
        }
        GetFilesByRegularExpressionResponse response;
        GetFilesByRegularExpression params;
        if (!parameters.containsKey(METHOD_NAME_GET_FILES_BY_REGULAR_EXPRESSION)) {
            params = new GetFilesByRegularExpression();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_FILES_BY_REGULAR_EXPRESSION, params);
        } else {
            params = (GetFilesByRegularExpression) parameters.get(
                    METHOD_NAME_GET_FILES_BY_REGULAR_EXPRESSION);
        }
        synchronized (params) {
            params.setProjectVersionId(projectVersionId);
            params.setRegExpr(regExpr);
            try {
                response = wsStub.getFilesByRegularExpression(params);
            } catch (Exception e) {
                throw new WSException(e);
            }
        }
        return (WSProjectFile[]) normalizeWSArrayResult(response.get_return());
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
            } catch (Exception e) {
                throw new WSException(e);
            }
        }

        return response.get_return();
    }

    /**
     * @see eu.sqooss.scl.accessor.WSProjectAccessor#getDirectoriesByIds(long[])
     */
    @Override
    public WSDirectory[] getDirectoriesByIds(long[] directoriesIds)
            throws WSException {
        if (!isNormalizedWSArrayParameter(directoriesIds)) return EMPTY_ARRAY_DIRECTORIES;
        GetDirectoriesByIdsResponse response;
        GetDirectoriesByIds params;
        if (!parameters.containsKey(METHOD_NAME_GET_DIRECTORIES_BY_IDS)) {
            params = new GetDirectoriesByIds();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_DIRECTORIES_BY_IDS, params);
        } else {
            params = (GetDirectoriesByIds) parameters.get(
                    METHOD_NAME_GET_DIRECTORIES_BY_IDS);
        }
        synchronized (params) {
            params.setDirectoriesIds(directoriesIds);
            try {
                response = wsStub.getDirectoriesByIds(params);
            } catch (Exception e) {
                throw new WSException(e);
            }
        }
        return (WSDirectory[]) normalizeWSArrayResult(response.get_return());
    }

    /**
     * @see eu.sqooss.scl.accessor.WSProjectAccessor#getDevelopersByIds(long[])
     */
    @Override
    public WSDeveloper[] getDevelopersByIds(long[] developersIds)
            throws WSException {
        if (!isNormalizedWSArrayParameter(developersIds)) return EMPTY_ARRAY_DEVELOPERS;
        GetDevelopersByIdsResponse response;
        GetDevelopersByIds params;
        if (!parameters.containsKey(METHOD_NAME_GET_DEVELOPERS_BY_IDS)) {
            params = new GetDevelopersByIds();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_DEVELOPERS_BY_IDS, params);
        } else {
            params = (GetDevelopersByIds) parameters.get(
                    METHOD_NAME_GET_DEVELOPERS_BY_IDS);
        }
        synchronized (params) {
            params.setDevelopersIds(developersIds);
            try {
                response = wsStub.getDevelopersByIds(params);
            } catch (Exception e) {
                throw new WSException(e);
            }
        }
        return (WSDeveloper[]) normalizeWSArrayResult(response.get_return());
    }

    /**
     * @see eu.sqooss.scl.accessor.WSProjectAccessor#getProjectByName(java.lang.String)
     */
    @Override
    public WSStoredProject getProjectByName(String projectName) throws WSException {
        GetProjectByNameResponse response;
        GetProjectByName params;
        if (!parameters.containsKey(METHOD_NAME_GET_PROJECT_BY_NAME)) {
            params = new GetProjectByName();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_PROJECT_BY_NAME, params);
        } else {
            params = (GetProjectByName) parameters.get(
                    METHOD_NAME_GET_PROJECT_BY_NAME);
        }
        synchronized (params) {
            params.setProjectName(projectName);
            try {
                response = wsStub.getProjectByName(params);
            } catch (Exception e) {
                throw new WSException(e);
            }
        }

        return response.get_return();
    }

    /**
     * @see eu.sqooss.scl.accessor.WSProjectAccessor#getProjectVersionsByIds(long[])
     */
    @Override
    public WSProjectVersion[] getProjectVersionsByIds(long[] projectVersionsIds)
            throws WSException {
        if (!isNormalizedWSArrayParameter(projectVersionsIds)) return EMPTY_ARRAY_PROJECT_VERSIONS;
        GetProjectVersionsByIdsResponse response;
        GetProjectVersionsByIds params;
        if (!parameters.containsKey(METHOD_NAME_GET_PROJECT_VERSIONS_BY_IDS)) {
            params = new GetProjectVersionsByIds();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_PROJECT_VERSIONS_BY_IDS, params);
        } else {
            params = (GetProjectVersionsByIds) parameters.get(
                    METHOD_NAME_GET_PROJECT_VERSIONS_BY_IDS);
        }
        synchronized (params) {
            params.setProjectVersionsIds(projectVersionsIds);
            try {
                response = wsStub.getProjectVersionsByIds(params);
            } catch (Exception e) {
                throw new WSException(e);
            }
        }
        
        return (WSProjectVersion[]) normalizeWSArrayResult(response.get_return());
    }

    /**
     * @see eu.sqooss.scl.accessor.WSProjectAccessor#getProjectVersionsByTimestamps(long,
     *      long[])
     */
    @Override
    public WSProjectVersion[] getProjectVersionsByTimestamps(long projectId,
            long[] timestamps) throws WSException {
        if (!isNormalizedWSArrayParameter(timestamps))
            return EMPTY_ARRAY_PROJECT_VERSIONS;

        GetProjectVersionsByTimestampsResponse response;
        GetProjectVersionsByTimestamps params;

        if (!parameters
                .containsKey(METHOD_NAME_GET_PROJECT_VERSIONS_BY_TIMESTAMPS)) {
            params = new GetProjectVersionsByTimestamps();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_PROJECT_VERSIONS_BY_TIMESTAMPS,
                    params);
        } else {
            params = (GetProjectVersionsByTimestamps) parameters
                    .get(METHOD_NAME_GET_PROJECT_VERSIONS_BY_TIMESTAMPS);
        }

        synchronized (params) {
            params.setProjectId(projectId);
            params.setTimestamps(timestamps);
            try {
                response = wsStub.getProjectVersionsByTimestamps(params);
            } catch (Exception e) {
                throw new WSException(e);
            }
        }

        return (WSProjectVersion[]) normalizeWSArrayResult(response
                .get_return());
    }

    /**
     * @see eu.sqooss.scl.accessor.WSProjectAccessor#getProjectVersionsByScmIds(long,
     *      String[])
     */
    @Override
    public WSProjectVersion[] getProjectVersionsByScmIds(long projectId,
            String[] scmIds) throws WSException {
        if (!isNormalizedWSArrayParameter(scmIds))
            return EMPTY_ARRAY_PROJECT_VERSIONS;

        GetProjectVersionsByScmIdsResponse response;
        GetProjectVersionsByScmIds params;

        if (!parameters
                .containsKey(METHOD_NAME_GET_PROJECT_VERSIONS_BY_SCM_IDS)) {
            params = new GetProjectVersionsByScmIds();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_PROJECT_VERSIONS_BY_SCM_IDS,
                    params);
        } else {
            params = (GetProjectVersionsByScmIds) parameters
                    .get(METHOD_NAME_GET_PROJECT_VERSIONS_BY_SCM_IDS);
        }

        synchronized (params) {
            params.setProjectId(projectId);
            params.setScmIds(scmIds);
            try {
                response = wsStub.getProjectVersionsByScmIds(params);
            } catch (Exception e) {
                throw new WSException(e);
            }
        }

        return (WSProjectVersion[]) normalizeWSArrayResult(response
                .get_return());
    }

    /**
     * @see eu.sqooss.scl.accessor.WSProjectAccessor#getFirstProjectVersions(long[])
     */
    @Override
    public WSProjectVersion[] getFirstProjectVersions(long[] projectsIds) throws WSException {
        if (!isNormalizedWSArrayParameter(projectsIds)) {
            return EMPTY_ARRAY_PROJECT_VERSIONS;
        }
        GetFirstProjectVersionsResponse response;
        GetFirstProjectVersions params;
        if (!parameters.containsKey(METHOD_NAME_GET_FIRST_PROJECT_VERSIONS)) {
            params = new GetFirstProjectVersions();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_FIRST_PROJECT_VERSIONS, params);
        } else {
            params = (GetFirstProjectVersions) parameters.get(
                    METHOD_NAME_GET_FIRST_PROJECT_VERSIONS);
        }
        synchronized (params) {
            params.setProjectsIds(projectsIds);
            try {
                response = wsStub.getFirstProjectVersions(params);
            } catch (Exception e) {
                throw new WSException(e);
            }
        }
        
        return (WSProjectVersion[]) normalizeWSArrayResult(response.get_return());
    }

    /**
     * @see eu.sqooss.scl.accessor.WSProjectAccessor#getLastProjectVersions(long[])
     */
    @Override
    public WSProjectVersion[] getLastProjectVersions(long[] projectsIds) throws WSException {
        if (!isNormalizedWSArrayParameter(projectsIds)) {
            return EMPTY_ARRAY_PROJECT_VERSIONS;
        }
        GetLastProjectVersionsResponse response;
        GetLastProjectVersions params;
        if (!parameters.containsKey(METHOD_NAME_GET_LAST_PROJECT_VERSIONS)) {
            params = new GetLastProjectVersions();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_LAST_PROJECT_VERSIONS, params);
        } else {
            params = (GetLastProjectVersions) parameters.get(
                    METHOD_NAME_GET_LAST_PROJECT_VERSIONS);
        }
        synchronized (params) {
            params.setProjectsIds(projectsIds);
            try {
                response = wsStub.getLastProjectVersions(params);
            } catch (Exception e) {
                throw new WSException(e);
            }
        }
        
        return (WSProjectVersion[]) normalizeWSArrayResult(response.get_return());
    }

    /**
     * @see eu.sqooss.scl.accessor.WSProjectAccessor#getPreviousVersionById(long)
     */
    @Override
    public WSProjectVersion getPreviousVersionById(long versionId)
            throws WSException {
        GetPreviousVersionByIdResponse response;
        GetPreviousVersionById params;

        if (!parameters.containsKey(METHOD_NAME_GET_PREVIOUS_VERSION_BY_ID)) {
            params = new GetPreviousVersionById();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_PREVIOUS_VERSION_BY_ID, params);
        } else {
            params = (GetPreviousVersionById) parameters.get(
                    METHOD_NAME_GET_PREVIOUS_VERSION_BY_ID);
        }

        synchronized (params) {
            params.setVersionId(versionId);
            try {
                response = wsStub.getPreviousVersionById(params);
            } catch (Exception e) {
                throw new WSException(e);
            }
        }

        return response.get_return();
    }

    /**
     * @see eu.sqooss.scl.accessor.WSProjectAccessor#getNextVersionById(long)
     */
    @Override
    public WSProjectVersion getNextVersionById(long versionId)
            throws WSException {
        GetNextVersionByIdResponse response;
        GetNextVersionById params;

        if (!parameters.containsKey(METHOD_NAME_GET_NEXT_VERSION_BY_ID)) {
            params = new GetNextVersionById();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_NEXT_VERSION_BY_ID, params);
        } else {
            params = (GetNextVersionById) parameters.get(
                    METHOD_NAME_GET_NEXT_VERSION_BY_ID);
        }

        synchronized (params) {
            params.setVersionId(versionId);
            try {
                response = wsStub.getNextVersionById(params);
            } catch (Exception e) {
                throw new WSException(e);
            }
        }

        return response.get_return();
    }

    /**
     * @see eu.sqooss.scl.accessor.WSProjectAccessor#getProjectsByIds(long[])
     */
    @Override
    public WSStoredProject[] getProjectsByIds(long[] projectsIds) throws WSException {
        if (!isNormalizedWSArrayParameter(projectsIds)) return EMPTY_ARRAY_STORED_PROJECTS; 
        GetProjectsByIdsResponse response;
        GetProjectsByIds params;
        if (!parameters.containsKey(METHOD_NAME_GET_PROJECTS_BY_IDS)) {
            params = new GetProjectsByIds();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_PROJECTS_BY_IDS, params);
        } else {
            params = (GetProjectsByIds) parameters.get(
                    METHOD_NAME_GET_PROJECTS_BY_IDS);
        }
        synchronized (params) {
            params.setProjectsIds(projectsIds);
            try {
                response = wsStub.getProjectsByIds(params);
            } catch (Exception e) {
                throw new WSException(e);
            }
        }

        return (WSStoredProject[]) normalizeWSArrayResult(response.get_return());

    }

    //========================================================================
    // TIMELINE RELATED PROJECT METHODS
    //========================================================================

    /**
     * @see eu.sqooss.scl.accessor.WSProjectAccessor#getSCMTimeline(long,
     *      long, long)
     */
    @Override
    public WSProjectVersion[] getSCMTimeline(long projectId, long tsmFrom,
            long tsmTill) throws WSException {
        GetSCMTimelineResponse response;
        GetSCMTimeline params;
        if (!parameters.containsKey(METHOD_NAME_GET_SCM_TIMELINE)) {
            params = new GetSCMTimeline();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_SCM_TIMELINE, params);
        } else {
            params = (GetSCMTimeline) parameters
                    .get(METHOD_NAME_GET_SCM_TIMELINE);
        }
        synchronized (params) {
            params.setProjectId(projectId);
            params.setTsmFrom(tsmFrom);
            params.setTsmTill(tsmTill);
            try {
                response = wsStub.getSCMTimeline(params);
            } catch (Exception e) {
                throw new WSException(e);
            }
        }
        return response.get_return();
    }

    /**
     * @see eu.sqooss.scl.accessor.WSProjectAccessor#getShortSCMTimeline(long,
     *      long, long)
     */
    @Override
    public WSShortProjectVersion[] getShortSCMTimeline(long projectId,
            long tsmFrom, long tsmTill) throws WSException {
        GetShortSCMTimelineResponse response;
        GetShortSCMTimeline params;
        if (!parameters.containsKey(METHOD_NAME_GET_SHORT_SCM_TIMELINE)) {
            params = new GetShortSCMTimeline();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_SHORT_SCM_TIMELINE, params);
        } else {
            params = (GetShortSCMTimeline) parameters
                    .get(METHOD_NAME_GET_SHORT_SCM_TIMELINE);
        }
        synchronized (params) {
            params.setProjectId(projectId);
            params.setTsmFrom(tsmFrom);
            params.setTsmTill(tsmTill);
            try {
                response = wsStub.getShortSCMTimeline(params);
            } catch (Exception e) {
                throw new WSException(e);
            }
        }
        return response.get_return();
    }

    /**
     * @see eu.sqooss.scl.accessor.WSProjectAccessor#getMailTimeline(long,
     *      long, long)
     */
    @Override
    public WSMailMessage[] getMailTimeline(long projectId, long tsmFrom,
            long tsmTill) throws WSException {
        GetMailTimelineResponse response;
        GetMailTimeline params;
        if (!parameters.containsKey(METHOD_NAME_GET_MAIL_TIMELINE)) {
            params = new GetMailTimeline();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_MAIL_TIMELINE, params);
        } else {
            params = (GetMailTimeline) parameters
                    .get(METHOD_NAME_GET_MAIL_TIMELINE);
        }
        synchronized (params) {
            params.setProjectId(projectId);
            params.setTsmFrom(tsmFrom);
            params.setTsmTill(tsmTill);
            try {
                response = wsStub.getMailTimeline(params);
            } catch (Exception e) {
                throw new WSException(e);
            }
        }
        return response.get_return();
    }

    /**
     * @see eu.sqooss.scl.accessor.WSProjectAccessor#getShortMailTimeline(
     *      long, long, long)
     */
    @Override
    public WSShortMailMessage[] getShortMailTimeline(long projectId,
            long tsmFrom, long tsmTill) throws WSException {
        GetShortMailTimelineResponse response;
        GetShortMailTimeline params;
        if (!parameters.containsKey(METHOD_NAME_GET_SHORT_MAIL_TIMELINE)) {
            params = new GetShortMailTimeline();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_SHORT_MAIL_TIMELINE, params);
        } else {
            params = (GetShortMailTimeline) parameters
                    .get(METHOD_NAME_GET_SHORT_MAIL_TIMELINE);
        }
        synchronized (params) {
            params.setProjectId(projectId);
            params.setTsmFrom(tsmFrom);
            params.setTsmTill(tsmTill);
            try {
                response = wsStub.getShortMailTimeline(params);
            } catch (Exception e) {
                throw new WSException(e);
            }
        }
        return response.get_return();
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
