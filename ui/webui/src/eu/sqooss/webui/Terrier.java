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

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import eu.sqooss.scl.WSException;
import eu.sqooss.scl.WSSession;
import eu.sqooss.scl.accessor.WSAccessor;
import eu.sqooss.scl.accessor.WSMetricAccessor;
import eu.sqooss.scl.accessor.WSProjectAccessor;
import eu.sqooss.scl.accessor.WSUserAccessor;
import eu.sqooss.ws.client.datatypes.WSMetric;
import eu.sqooss.ws.client.datatypes.WSProjectFile;
import eu.sqooss.ws.client.datatypes.WSProjectVersion;
import eu.sqooss.ws.client.datatypes.WSStoredProject;
import eu.sqooss.ws.client.datatypes.WSUser;


/**
 * This class is the entry point for retrieving data from the
 * Alitheia core through the webservices. It has a connection
 * to the core and exposes data query methods.
 */
public class Terrier {
    private String error = "";
    private String debug = "";

    // Various configuration parameters
    private static String cfgUnprivUser    = "unprivUser";
    private static String cfgUnprivPass    = "unprivPass";
    private static String cfgFrameworkURL  = "frameworkUrl";

    // Load the WebUI's configuration file (if any)
    private Configurator confParams = new Configurator("webui.cfg");

    private TerrierConnection connection = null;

    /**
     * Simple constructor. Instantiates a new <code>Terrier</code> object.
     */
    public Terrier () {
        initConfig();
        connection = new TerrierConnection(
            confParams.getProperty(cfgFrameworkURL),
            confParams.getProperty(cfgUnprivUser),
            confParams.getProperty(cfgUnprivPass));
    }

    /**
     * This function will create a configuration when one doesn't exist,
     * and/or fill the base configuration parameters (with pre-defined values)
     * when these can't be found in the configuration file.
     */
    private void initConfig() {
        boolean flush = false;
        if ((confParams.getProperty(cfgUnprivUser) == null)) {
            confParams.setProperty(
                    cfgUnprivUser,
                    "alitheia");
            flush = true;
        }
        if ((confParams.getProperty(cfgUnprivPass) == null)) {
            confParams.setProperty(
                    cfgUnprivPass,
                    "alitheia");
            flush = true;
        }
        if ((confParams.getProperty(cfgFrameworkURL) == null)) {
            confParams.setProperty(
                    cfgFrameworkURL,
                    "http://localhost:8088/sqooss/services/ws");
            flush = true;
        }
        // Write the configuration state change into the configuration file
        if (flush) confParams.flush();
    }

    /**
     * Retrieves descriptive information about the selected project from
     * the SQO-OSS framework, and constructs a Project object from it.
     *
     * @param projectId The ID of the selected project.
     * @return A Project object.
     */
    public Project getProject(Long projectId) {
        if (!connection.isConnected()) {
            return null;
        }
        debug += "ok";

        Project prj;
        try {
            // Retrieve information about this project
            prj = new Project(
                    connection.getProjectAccessor().retrieveStoredProject(projectId));

            // Retrieve all project versions
            prj.setVersions(getProjectVersions(projectId));
        } catch (WSException wse) {
            error = "Could not receive a list of projects.";
            return null;
        }
        return prj;
    }

    /**
     * Gets the list of all known project versions. The first field in each
     * version token contains the version number. The second field contains
     * the corresponding version ID.
     *
     * @param projectId The ID of the selected project.
     * @return the list of project versions
     */
    public SortedMap<Long,Long> getProjectVersions(Long projectId) {
        SortedMap<Long, Long> projectVersions = new TreeMap<Long, Long>();
        if (!connection.isConnected()) {
            return projectVersions;
        }
        try {
            WSProjectVersion[] actualProjectVersions =
                connection.getProjectAccessor().retrieveStoredProjectVersions(projectId);
            for (WSProjectVersion nextVersion : actualProjectVersions){
                projectVersions.put(
                        nextVersion.getVersion(),
                        nextVersion.getId());
            };
        } catch (WSException e) {
            error = "Could not receive a list of project versions.";
        }
        return projectVersions;
    }

    public Vector<Project> getEvaluatedProjects() {
        Vector<Project> projects = new Vector<Project>();
        if (!connection.isConnected()) {
            return projects;
        }
        debug += "ok";
        try {
            // TODO: Retrieve only evaluated project later on
            WSStoredProject projectsResult[] =
                connection.getProjectAccessor().storedProjectsList();
            debug += ":gotresults";
            debug += ":projects=" + projectsResult.length;
            for (WSStoredProject wssp : projectsResult) {
                projects.addElement(new Project(wssp));
            }
        } catch (WSException wse) {
            debug+= ":wse";
            error = "Could not receive a list of projects.";
            return projects;
        }
        debug += ":done";
        return projects;
    }

    /**
     * Retrieves all metrics that has been evaluated for the selected
     * projects, and generates a proper view for displaying them.
     *
     * @param projectId The ID of selected project
     * @return The corresponding view object
     */
    public MetricsTableView getMetrics4Project(Long projectId) {
        if (!connection.isConnected()) {
            return null;
        }
        MetricsTableView view = new MetricsTableView(projectId);
        try {
            WSMetric[] metrics =
                connection.getMetricAccessor().retrieveMetrics4SelectedProject(projectId);
            for (WSMetric met : metrics) {
                view.addMetric(new Metric(met));
            }
        } catch (WSException e) {
            error = "Can not retrieve the list of metrics for this project.";
            return null;
        }
        return view;
    }

    /**
     * Retrieves all files that exist in the specified project version,
     * and generates a proper view for displaying them.
     *
     * @param versionId The ID of selected project version
     * @return The corresponding view object
     */
    public FileListView getFiles4ProjectVersion(Long versionId) {
        if (!connection.isConnected()) {
            return null;
        }
        FileListView view = new FileListView(versionId);
        try {
            WSProjectFile[] files =
                connection.getProjectAccessor().getFileList4ProjectVersion(versionId);
            for (WSProjectFile file : files) {
                view.addFile(new eu.sqooss.webui.File(file));
            }
        } catch (WSException e) {
            error = "Can not retrieve the list of files for this version.";
            return null;
        }
        return view;
    }

    /**
     * Retrieves the number of all files that exist in the specified project
     * version.
     *
     * @param versionId The ID of selected project version
     * @return The number of files.
     */
    public Long getFilesNumber4ProjectVersion(Long versionId) {
        if (!connection.isConnected()) {
            return null;
        }
        try {
            return connection.getProjectAccessor().getFilesNumber4ProjectVersion(versionId);
        } catch (WSException e) {
            error = "Can not retrieve the number of files for this version.";
        }
        return null;
    }

    public Metric getMetric(Long metricId) {
        // TODO
        return null;
    }

    /**
     * Retrieves information about the specified user from the SQO-OSS
     * framework.
     * 
     * @param userId the user's account Id
     * 
     * @return an User object holding information about the requested user, or
     * <code>null</code> when no information is available
     */
    public User getUserById (Long userId) {
        if (!connection.isConnected()) {
            return null;
        }
        try {
            WSUser user = connection.getUserAccessor().displayUser(userId);
            if (user != null) {
                return new User(user.getId(), user.getUserName(), user.getEmail());
            }
        } catch (WSException e) {
            error = "Can not retrieve information about the selected user.";
        }
        return null;
    }

    /**
     * Retrieves information about the specified user from the SQO-OSS
     * framework.
     * 
     * @param userName the user's name
     * 
     * @return an User object holding information about the requested user, or
     * <code>null</code> when no information is available
     */
    public User getUserByName (String userName) {
        if (!connection.isConnected()) {
            return null;
        }
        try {
            WSUser user = connection.getUserAccessor().getUserByName(userName);
            if (user != null) {
                return new User(user.getId(), user.getUserName(), user.getEmail());
            }
        } catch (WSException e) {
            error = "Can not retrieve information about the selected user.";
        }
        return null;
    }

    public File getFile(Long fileId) {
        // TODO
        return null;
    }

    /**
     * The Alitheia core may have a message-of-the-day stored in it,
     * which is then printed when the user hits the front page.
     */
    public String getUserMessageOfTheDay() {
        try {
            return connection.getUserAccessor().getUserMessageOfTheDay(connection.getUserName());
        } catch (WSException e) {
            return null;
        }
    }

    /**
     * Add a user to the system.
     */
    public boolean registerUser (
            String username,
            String password,
            String email) {
        if (!connection.isConnected()) {
            return false;
        }
        try {
            return connection.getUserAccessor().submitPendingUser(username, password, email);
        } catch (WSException e) {
            error = "An error occured during the registration process!";
            error += " Please try again later.";
            return false;
        }
    }

    /** Forwarding function to TerrierConnection.loginUser */
    public boolean loginUser(String user, String pass) {
        return connection.loginUser(user,pass);
    }

    /** Forwarding function to TerrierConnection.logoutUser */
    public void logoutUser(String user) {
        connection.logoutUser(user);
    }

    public String getError() {
        return error;
    }

    public String getDebug() {
        return debug;
    }
}
