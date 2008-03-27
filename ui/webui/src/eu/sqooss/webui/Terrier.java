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
import eu.sqooss.scl.result.WSResult;
import eu.sqooss.ws.client.datatypes.WSMetric;
import eu.sqooss.ws.client.datatypes.WSProjectFile;
import eu.sqooss.ws.client.datatypes.WSProjectVersion;
import eu.sqooss.ws.client.datatypes.WSStoredProject;


public class Terrier {

    WSSession session;
//    WSConnection connection;
    WSProjectAccessor projectAccessor;
    WSMetricAccessor metricAccessor;
    WSResult result;

    String error = "No problems.";
    String debug = "...";

    public Terrier () {
        connect();
    }
    
    public boolean isConnected () {
        if (session == null) {
            connect();
        }
        if (session == null) {
            debug = "noconnection ";
            error = "Connection to Alitheia failed.";
            return false;
        }
        return true;
    }

    /**
     * Retrieves descriptive information about the selected project from
     * the SQO-OSS framework, and constructs a Project object from it.
     * 
     * @param projectId The ID of the selected project.
     * @return A Project object.
     */
    public Project getProject(Long projectId) {
        if (!isConnected()) return null;
        debug += "ok";

        Project prj;
        try {
            // Retrieve information about this project
            prj = new Project(
                    projectAccessor.retrieveStoredProject(projectId));

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
        if (!isConnected()) return projectVersions;
        try {
            WSProjectVersion[] actualProjectVersions =
                projectAccessor.retrieveStoredProjectVersions(projectId);
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
        if (!isConnected()) return projects;
        debug += "ok";
        try {
            // TODO: Retrieve only evaluated project later on
            WSStoredProject projectsResult[] =
                projectAccessor.storedProjectsList();
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
        if (!isConnected()) return null;
        MetricsTableView view = new MetricsTableView(projectId);
        try {
            WSMetric[] metrics =
                metricAccessor.retrieveMetrics4SelectedProject(projectId);
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
        if (!isConnected()) return null;
        FileListView view = new FileListView(versionId);
        try {
            WSProjectFile[] files =
                projectAccessor.getFileList4ProjectVersion(versionId);
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
        if (!isConnected()) return null;
        try {
            return projectAccessor.getFilesNumber4ProjectVersion(versionId);
        } catch (WSException e) {
            error = "Can not retrieve the number of files for this version.";
        }
        return null;
    }

    public Metric getMetric(Long metricId) {
        // TODO
        return null;
    }

    public User getUser (Long userId) {
        // TODO
        return null;
    }

    public File getFile(Long fileId) {
        // TODO
        return null;
    }

    public String getError() {
        return error;
    }
    
    public String getDebug() {
        return debug;
    }

    private void connect() {
        // Try to connect the SCL to the Alitheia system
        try {
            session = new WSSession("alitheia", "alitheia", "http://localhost:8088/sqooss/services/ws"); // WTF?
            error = "connected";
        } catch (WSException wse) {
            error = "Couldn't start Alitheia session.";
            debug += "nosession";
            wse.printStackTrace();
            session = null;
            return;
        }
        projectAccessor = (WSProjectAccessor) session.getAccessor(WSAccessor.Type.PROJECT);
        metricAccessor = (WSMetricAccessor) session.getAccessor(WSAccessor.Type.METRIC);
//        try {
//            connection = session.getConnection();
//        } catch (WSException wse) {
//            debug += "noconnection";
//            error = "Couldn't connect to Alitheia's webservice.";
//            wse.printStackTrace();
//            connection = null;
//        }
    }
}
