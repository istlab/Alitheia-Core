/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2008 by Sebastian Kuegler <sebas@kde.org>
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

package eu.sqooss.webui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Vector;

import eu.sqooss.scl.WSException;
import eu.sqooss.webui.datatype.Developer;
import eu.sqooss.webui.datatype.File;
import eu.sqooss.webui.datatype.TaggedVersion;
import eu.sqooss.webui.datatype.Version;
import eu.sqooss.ws.client.datatypes.WSDeveloper;
import eu.sqooss.ws.client.datatypes.WSDirectory;
import eu.sqooss.ws.client.datatypes.WSFileModification;
import eu.sqooss.ws.client.datatypes.WSMetric;
import eu.sqooss.ws.client.datatypes.WSMetricType;
import eu.sqooss.ws.client.datatypes.WSProjectFile;
import eu.sqooss.ws.client.datatypes.WSProjectVersion;
import eu.sqooss.ws.client.datatypes.WSStoredProject;
import eu.sqooss.ws.client.datatypes.WSTaggedVersion;
import eu.sqooss.ws.client.datatypes.WSUser;
import eu.sqooss.ws.client.datatypes.WSMetricsResultRequest;
import eu.sqooss.ws.client.datatypes.WSResultEntry;
import eu.sqooss.ws.client.datatypes.WSVersionStats;

/**
 * This class is the entry point for retrieving data from the SQO-OSS
 * framework through the Web-services service (WSS). It establishes a
 * connection to the WSS and exposes its data query methods.
 */
public class Terrier {
    // Accumulates the generated error messages
    private Stack<String> errorsStack = new Stack<String>();

    // Metric types cache
    private HashMap<Long, String> metricTypes = new HashMap<Long, String>();

    // Points to the the WebUI's configuration bundle
    private ResourceBundle confParams;

    // Holds an instance of the object that establishes a connection with
    // the SQO-OSS framework
    private TerrierConnection connection = null;

    /**
     * Instantiates a new <code>Terrier</code> object.
     */
    public Terrier () {
    }

    /**
     * Loads the <code>Terrier</code>'s configuration setting from the
     * specified <code>ResourceBundle</code> object. In the exceptional case,
     * when the specified configuration object is invalid, this function will
     * fall back to a set of predefined configuration settings.
     *
     * @param configuration a <code>ResourceBunde</code> configuration object
     */
    public void initConfig(ResourceBundle configuration) {
        this.confParams = configuration;

        String userName = Constants.conUserName;
        String userPass = Constants.conUserPass;
        String connUrl  = Constants.conConnURL;

        if (confParams != null) {
            if (confParams.getString(Constants.cfgUnprivUser) != null) {
                userName = confParams.getString(Constants.cfgUnprivUser);
            }
            if (confParams.getString(Constants.cfgUnprivPass) != null) {
                userPass = confParams.getString(Constants.cfgUnprivPass);
            }
            if (confParams.getString(Constants.cfgFrameworkURL) != null) {
                connUrl = confParams.getString(Constants.cfgFrameworkURL);
            }
        }

        // Instantiate an object for connecting to SQO-OSS
        connection = new TerrierConnection(connUrl, userName, userPass);
    }

    /**
     * Returns the current connection object.
     * 
     * @return The current <code>TerrierConnection</code> instance,
     *   or <code>null</code> in case this object is not yet initialized.
     */
    public TerrierConnection connection() {
        return connection;
    }

    /**
     * Returns the status of the connection with the SQO-OSS framework.
     * 
     * @return <code>true</code>, if a connection is established,
     *   or <code>true</code> otherwise.
     */
    public boolean isConnected() {
        if (connection != null)
            return connection.isConnected();
        return false;
    }

    /**
     * This method will try to establish a session with the SQO-OSS framework
     * by using the specified user credentials.
     * 
     * @param user the username
     * @param pass the password
     * 
     * @return <code>true</code>, if the session is successfully established,
     *   or <code>false</code> otherwise.
     */
    public boolean loginUser(String user, String pass) {
        return connection.loginUser(user,pass);
    }

    /**
     * This method will terminate the current user session, and will then
     * establish a session by using the non-privileged user credentials.
     */
    public void logoutUser() {
        connection.logoutUser();
    }

    /**
     * Checks, if any errors were accumulated during the execution of the
     * methods of this object.
     * 
     * @return <code>true<code>, if the errors buffer contain at least one
     *   message, or <code>false</code> otherwise.
     */
    public boolean hasErrors () {
        return (errorsStack.isEmpty());
    }

    /**
     * Gets the list of currently accumulated error messages.
     * 
     * @return The list of error messages, or an empty list when none were
     *   found.
     */
    public Stack<String> getErrors() {
        return errorsStack;
    }

    /**
     * Adds a error single error message to the current list of error messages.
     * 
     * @param message the message string
     */
    public void addError(String message) {
        if (errorsStack.contains(message) == false)
            errorsStack.push(message);
    }

    /**
     * Flushes the list of currently accumulate error messages.
     */
    public void flushErrors() {
        errorsStack.clear();
    }

    //========================================================================
    // PROJECT RELATED SCL WRAPPER METHODS
    //========================================================================

    /**
     * Retrieves descriptive information about the selected project from
     * the SQO-OSS framework, and constructs a Project object from it.
     *
     * @param projectId the project Id
     *
     * @return The project's object, or <code> null when such project does
     *   not exist or when a failure has occurred.
     */
    public Project getProject(long projectId) {
        if (isConnected()) {
            try {
                // Retrieve information about this project
                WSStoredProject[] storedProjects =
                    connection.getProjectAccessor().getProjectsByIds(
                            new long[] {projectId});
                if (storedProjects.length > 0)
                    return new Project(storedProjects[0]);
                else
                    addError("This project does not exist!");
            }
            catch (WSException wse) {
                addError("Can not retrieve this project!");
            }
        }
        else
            addError(connection.getError());
        return null;
    }

    /**
     * Gets the list of all project that were evaluated from the attached
     * SQO-OSS framework.
     *
     * @return The list of evaluated projects, or an empty list when none
     *   are found.
     */
    public Vector<Project> getEvaluatedProjects() {
        Vector<Project> result = new Vector<Project>();
        if (isConnected()) {
            try {
                // Retrieve evaluated projects only
                WSStoredProject projectsResult[] =
                    connection.getProjectAccessor().getEvaluatedProjects();
                for (WSStoredProject nextProject : projectsResult)
                    result.addElement(new Project(nextProject));
            }
            catch (WSException wse) {
                addError("Can not retrieve the list of evaluated projects!");
            }
        }
        else
            addError(connection.getError());
        return result;
    }

    //========================================================================
    // VERSION RELATED SCL WRAPPER METHODS
    //========================================================================

    /**
     * Retrieves a project's version by project and version Id.
     *
     * @param projectId the project Id
     * @param versionId the project version's Id
     *
     * @return The version object that matches the selected project and
     *   version Id, or <code>null<code> when such version does not exist.
     */
    public Version getVersion(long projectId, long versionId) {
        if (isConnected()) {
            try {
                // Search for a matching project
                WSProjectVersion[] versionsResult =
                    connection.getProjectAccessor().getProjectVersionsByIds(
                            new long[]{versionId});
                if (versionsResult.length > 0)
                    return new Version(versionsResult[0], this);
            }
            catch (WSException wse) {
                addError("Can not retrieve version by Id!");
            }
        }
        else
            addError(connection.getError());
        return null;
    }

    /**
     * Retrieves the first recorded version (<i>e.g. SVN revision 1</i>)
     * of the selected project.
     *
     * @param projectId the project Id
     *
     * @return The version object that corresponds to the first version of
     *   the selected project, or <code>null<code> when such version does not
     *   exist.
     */
    public Version getFirstProjectVersion(long projectId) {
        if (isConnected()) {
            try {
                WSProjectVersion[] wsversions =
                    connection.getProjectAccessor().getFirstProjectVersions(
                            new long[]{projectId});
                if (wsversions.length > 0)
                    return new Version(wsversions[0], this);
            }
            catch (WSException e) {
                addError("Can not retrieve last project version.");
            }
        }
        else
            addError(connection.getError());
        return null;
    }

    /**
     * Retrieves the last recorded version (<i>e.g. SVN HEAD revision</i>)
     * of the selected project.
     *
     * @param projectId the project Id
     *
     * @return The version object that corresponds to the last version of
     *   the selected project, or <code>null<code> when such version does not
     *   exist.
     */
    public Version getLastProjectVersion(long projectId) {
        if (isConnected()) {
            try {
                WSProjectVersion[] wsversions =
                    connection.getProjectAccessor().getLastProjectVersions(
                            new long[]{projectId});
                if (wsversions.length > 0)
                    return new Version(wsversions[0], this);
            }
            catch (WSException e) {
                addError("Can not retrieve last project version.");
            }
        }
        else
            addError(connection.getError());
        return null;
    }

    /**
     * Retrieves one or more project versions by project Id and version
     * numbers from the attached SQO-OSS framework.
     *
     * @param projectId the project Id
     * @param numbers the list of project version numbers
     *
     * @return The list of project versions that correspond to the given
     *   version numbers, or an empty list when none are found.
     */
    public List<Version> getVersionsByNumber(long projectId, long[] numbers) {
        List<Version> result = new ArrayList<Version>();
        if (isConnected()) {
            try {
                // Retrieve the corresponding version objects
                WSProjectVersion[] wsversions =
                    connection.getProjectAccessor()
                    .getProjectVersionsByVersionNumbers(
                            projectId, numbers);
                for (WSProjectVersion nextVersion : wsversions)
                    result.add(new Version(nextVersion, this));
            }
            catch (WSException wse) {
                addError("Can not retrieve version(s) by number.");
            }
        }
        else
            addError(connection.getError());
        return result;
    }

    /**
     * Retrieves from the attached SQO-OSS framework the list of tagged
     * versions that are available for the project with the given Id.
     *
     * @param projectId the project Id
     *
     * @return The list of tagged versions in the selected project,
     *   or an empty list when none are found.
     */
    public List<TaggedVersion> getTaggedVersionsByProjectId(long projectId) {
        List<TaggedVersion> result = new ArrayList<TaggedVersion>();
        if (isConnected()) {
            try {
                // Retrieve the corresponding version objects
                WSTaggedVersion[] wsversions =
                    connection.getProjectAccessor()
                    .getTaggedVersionsByProjectId(projectId);
                for (WSTaggedVersion nextVersion : wsversions)
                    result.add(new TaggedVersion(nextVersion, this));
            }
            catch (WSException wse) {
                addError("Can not retrieve tagged versions.");
            }
        }
        else
            addError(connection.getError());
        return result;
    }

    /**
     * Returns the current number of versions for the project with the
     * given Id.
     *
     * @param projectId the project Id
     *
     * @return The current number of versions for that project.
     */
    public Long getVersionsCount(Long projectId) {
        if (isConnected()) {
            try {
                return connection.getProjectAccessor().getVersionsCount(
                        projectId);
            }
            catch (WSException e) {
                addError("Can not retrieve the number of project versions.");
            }
        }
        else
            addError(connection.getError());
        return null;
    }

    /**
     * Returns the file statistic for the given project versions.
     *
     * @param versionsIds the list of project version Ids
     *
     * @return The list of file statistics for the given versions,
     *   or an empty list when none are found.
     */
    public List<WSVersionStats> getVersionsStatistics(long[] versionsIds) {
        List<WSVersionStats> result = new ArrayList<WSVersionStats>();
        if (isConnected()) {
            try {
                WSVersionStats[] wsstats =
                    connection.getProjectAccessor().getVersionsStatistics(
                            versionsIds);
                for (WSVersionStats nextStats : wsstats)
                    result.add(nextStats);
            }
            catch (WSException e) {
                addError("Can not retrieve statistics for project versions.");
            }
        }
        else
            addError(connection.getError());
        return result;
    }

    //========================================================================
    // DEVELOPER RELATED SCL WRAPPER METHODS
    //========================================================================

    /**
     * Returns a list of developer's meta-data objects for the developers
     * with the selected Ids.
     *
     * @param developersIds the list of developers Ids
     *
     * @return The list of developer's meta-data objects, or an empty list
     *   when none are found.
     */
    public List<Developer> getDevelopers(long[] developersIds) {
        List<Developer> result = new ArrayList<Developer>();
        if (isConnected()) {
            try {
                WSDeveloper[] wsdevelopers =
                    connection.getProjectAccessor().getDevelopersByIds(
                            developersIds);
                for (WSDeveloper nextDeveloper : wsdevelopers)
                    result.add(new Developer(nextDeveloper));
            }
            catch (WSException e) {
                addError("Can not retrieve the list of project developers.");
            }
        }
        else
            addError(connection.getError());
        return result;
    }

    //========================================================================
    // SOURCE FILE RELATED SCL WRAPPER METHODS
    //========================================================================

    /**
     * This method returns the root directory of the specified project's
     * source tree.
     *
     * @param projectId the project Id
     *
     * @return The root directory's object, or <code>null</code> if not found.
     */
    public WSDirectory getRootDirectory(long projectId) {
        if (isConnected()) {
            try {
                // Retrieve the corresponding directory object
                return connection.getProjectAccessor().getRootDirectory(
                        projectId);
            }
            catch (WSException wse) {
                addError("Can not retrieve the project's source tree.");
            }
        }
        else
            addError(connection.getError());
        return null;
    }

    /**
     * This method returns a list of all files located in the selected
     * directory, that exists in the specified project version.
     *
     * @param versionId the project version's Id
     * @param directoryId the directory Id
     *
     * @return The list of project's files in that directory and that project
     * version, or a empty list when none are found.
     */
    public List<File> getFilesInDirectory(long versionId, long directoryId) {
        List<File> result = new ArrayList<File>();
        if (isConnected()) {
            try {
                WSProjectFile[] wsfiles =
                    connection.getProjectAccessor().getFilesInDirectory(
                            versionId, directoryId);
                for (WSProjectFile nextFile : wsfiles)
                    result.add(new File(nextFile));
            }
            catch (WSException wse) {
                addError("Can not retrieve the list of files"
                        + " in this directory!");
            }
        }
        else
            addError(connection.getError());
        return result;
    }

    /**
     * Retrieves the number of all files that exist in the specified project
     * version.
     *
     * @param versionId the project version's Id
     *
     * @return The number of files, or <code>null<code> when such version does
     *   not exist.
     */
    public Long getFilesCount(long versionId) {
        if (isConnected()) {
            try {
                return connection.getProjectAccessor()
                    .getFilesNumberByProjectVersionId(versionId);
            }
            catch (WSException e) {
                addError("Can not retrieve the number of files"
                        + " for this version.");
            }
        }
        else
            addError(connection.getError());
        return null;
    }

    /**
     * Retrieves the list of modification that were performed on the file
     * with the given Id. A single modification is presented with a 
     * <code>[Long, Long]</code> map token where the version number is used
     * as a key, and the file Id in that version as a value.
     * 
     * @param versionId the project version's Id
     * @param fileId the file id
     * 
     * @return The sorted (by version number) map of modification that were
     *   performed on this file, or an empty list when none are found.
     */
    public SortedMap<Long, Long> getFileModification(
            long versionId, long fileId) {
        SortedMap<Long, Long> result = new TreeMap<Long, Long>();
        if (isConnected()) {
            try {
                WSFileModification[] wsmods =
                    connection.getProjectAccessor().getFileModifications(
                            versionId, fileId);
                if (wsmods != null)
                    for (WSFileModification nextMod : wsmods)
                        result.put(
                                nextMod.getProjectVersionNum(),
                                nextMod.getProjectFileId());
            }
            catch (WSException e) {
                addError("Can not retrieve the list of file modifications.");
            }
        }
        else
            addError(connection.getError());
        return result;
    }

    //========================================================================
    // METRIC RELATED SCL WRAPPER METHODS
    //========================================================================

    /**
     * Retrieves the list of all metrics that are currently registered in the
     * attached SQO-OSS framework.
     *
     * @return The list of all registered metric, or an empty list when none
     *   are found.
     */
    public List<Metric> getAllMetrics() {
        List<Metric> result = new ArrayList<Metric>();
        if (isConnected()) {
            try {
                WSMetric[] wsmetrics =
                    connection.getMetricAccessor().getAllMetrics();
                if ((wsmetrics != null) && (wsmetrics.length > 0)) {
                    // Retrieve the metric types
                    long[] typeIds = new long[wsmetrics.length];
                    int index = 0;
                    for (WSMetric nextMetric : wsmetrics)
                        typeIds[index++] = nextMetric.getMetricTypeId();
                    getMetricTypesById(typeIds);
                    // Create the result list
                    for (WSMetric nextMetric : wsmetrics)
                        result.add(new Metric(
                                nextMetric,
                                metricTypes.get(nextMetric.getMetricTypeId())));
                }
            }
            catch (WSException wse) {
                addError("Cannot retrieve the list of all metrics.");
            }
        }
        else
            addError(connection.getError());
        return result;
    }

    /**
     * Retrieves the list of all metrics that has been evaluated on the
     * project with the given Id.
     *
     * @param projectId the project Id
     *
     * @return The list of evaluated metrics, or an empty list when none
     *   are found.
     */
    public List<Metric> getMetricsForProject(long projectId) {
        List<Metric> result = new ArrayList<Metric>();
        if (isConnected()) {
            try {
                WSMetric[] wsmetrics = 
                    connection.getMetricAccessor().getProjectEvaluatedMetrics(
                            projectId);
                if ((wsmetrics != null) && (wsmetrics.length > 0)) {
                    // Retrieve the metric types
                    long[] typeIds = new long[wsmetrics.length];
                    int index = 0;
                    for (WSMetric nextMetric : wsmetrics)
                        typeIds[index++] = nextMetric.getMetricTypeId();
                    getMetricTypesById(typeIds);
                    // Create the result list
                    for (WSMetric nextMetric : wsmetrics)
                        result.add(new Metric(
                                nextMetric,
                                metricTypes.get(nextMetric.getMetricTypeId())));
                }
            }
            catch (WSException wse) {
                addError("Cannot retrieve the list of project metrics.");
            }
        }
        else
            addError(connection.getError());
        return result;
    }

    /**
     * Retrieves metric types by their Ids. In case one or more metric types
     * can not be located in the local cache, then this method will try to
     * retrieve the missing types from the attached SQO-OSS framework.
     * 
     * @param metricTypeIds the list of metric type Ids
     * 
     * @return the map of metric type indexed by their Ids
     */
    private HashMap<Long, String> getMetricTypesById(long[] metricTypeIds) {
        HashMap<Long, String> result = new HashMap<Long, String>();
        if (isConnected()) {
            // Search into the local cache first
            List<Long> missing = new ArrayList<Long>();
            for (long nextId : metricTypeIds) {
                if (metricTypes.containsKey(nextId))
                    result.put(nextId, metricTypes.get(nextId));
                else
                    missing.add(nextId);
            }
            // Retrieve all missing metric types
            if (missing.size() > 0) {
                long[] query = new long[missing.size()];
                int index = 0;
                for (Long nextId : missing)
                    query[index++] = nextId.longValue();
                try {
                    WSMetricType[] missingTypes =
                        connection.getMetricAccessor().getMetricTypesByIds(
                                query);
                    if (missingTypes.length > 0) {
                        // Fill the local cache and the result list
                        for (WSMetricType nextType : missingTypes) {
                            metricTypes.put(
                                    nextType.getId(), nextType.getType());
                            result.put(nextType.getId(), nextType.getType());
                        }
                    } else {
                        addError("One or more metric types can not be found!");
                    }
                } catch (WSException e) {
                    addError("The metric types query has failed!");
                }
            }
        }
        else
            addError(connection.getError());
        return result;
    }

    //========================================================================
    // RESULT RELATED SCL WRAPPER METHODS
    //========================================================================

    /**
     * Retrieves a list of evaluation results from the attached SQO-OSS
     * framework, for all project resources and metrics that are described
     * in the given result request object.
     *
     * @param request the result request object
     *
     * @return the list of results, or an empty list when none are found.
     */
    public List<Result> getResults (WSMetricsResultRequest request) {
        List<Result> result = new ArrayList<Result>();
        if (connection.isConnected()) {
            try {
                // Retrieve the requested results
                WSResultEntry[] wsresults =
                    connection.getMetricAccessor().getMetricsResult(request);
                // Create the result list
                for (WSResultEntry nextResult : wsresults)
                    result.add(new Result(nextResult, this));
            }
            catch (WSException wse) {
                addError("Failed to retrieve ProjectResults.");
            }
        }
        else
            addError(connection.getError());
        return result;
    }

    //========================================================================
    // USER RELATED SCL WRAPPER METHODS
    //========================================================================

    /**
     * Retrieves information about the specified user from the SQO-OSS
     * framework.
     *
     * @param userId the user Id
     *
     * @return an <code>User</code> object holding information about the
     *   requested user, or <code>null</code> when such user does not exist
     *   or its information is not available to the current user.
     */
    public User getUserById (Long userId) {
        if (isConnected()) {
            try {
                WSUser[] users = connection.getUserAccessor().getUsersByIds(
                        new long[]{userId});
                if (users.length > 0)
                    return new User(users[0]);
            }
            catch (WSException e) {
                addError("Can not retrieve information"
                        + " about the selected user.");
            }
        }
        else
            addError(connection.getError());
        return null;
    }

    /**
     * Retrieves information about the specified user from the SQO-OSS
     * framework.
     *
     * @param userName the user name
     *
     * @return an <code>User</code> object holding information about the
     *   requested user, or <code>null</code> when such user does not exist
     *   or its information is not available to the current user.
     */
    public User getUserByName (String userName) {
        if (isConnected()) {
            try {
                WSUser user = connection.getUserAccessor().getUserByName(
                        userName);
                if (user != null)
                    return new User(user);
            }
            catch (WSException e) {
                addError("Can not retrieve information"
                        + " about the selected user.");
            }
        }
        else
            addError(connection.getError());
        return null;
    }

    /**
     * Sends an user registration request to the attached SQO-OSS framework.
     * 
     * @param username the username
     * @param password the password
     * @param email the email
     * 
     * @return <code>true</code>, if the registration process was successful,
     *   or <code>false<code> when a user with the same name already exist.
     */
    public boolean registerUser (
            String username,
            String password,
            String email) {
        if (isConnected()) {
            try {
                return connection.getUserAccessor().createPendingUser(
                        username, password, email);
            }
            catch (WSException e) {
                addError("An error occured during the registration process!");
            }
        }
        else
            addError(connection.getError());
        return false;
    }

    /**
     * Retrieves the current message-of-the-day that is stored in the attached
     * SQO-OSS framework.
     * 
     * @return The message text, or <code>null<code> when a message is not
     *   provided.
     */
    public String getUserMessageOfTheDay() {
        if (isConnected()) {
            try {
                return connection.getUserAccessor().getMessageOfTheDay();
            }
            catch (WSException e) {
                addError("An error occured during the registration process!");
            }
        }
        else
            addError(connection.getError());
        return null;
    }

}
