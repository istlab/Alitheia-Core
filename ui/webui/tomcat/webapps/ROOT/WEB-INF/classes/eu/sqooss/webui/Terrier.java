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

import java.util.Vector;
import java.util.Iterator;
import java.util.ArrayList;

import eu.sqooss.scl.WSException;
import eu.sqooss.scl.WSSession;
import eu.sqooss.scl.WSConnection;
import eu.sqooss.scl.result.WSResult;
import eu.sqooss.scl.result.WSResultEntry;
import eu.sqooss.ws.client.datatypes.WSStoredProject;
import eu.sqooss.ws.client.datatypes.WSMetric;

import eu.sqooss.webui.User;
import eu.sqooss.webui.File;
import eu.sqooss.webui.Metric;
import eu.sqooss.webui.Project;


public class Terrier {

    WSSession session;
    WSConnection connection;
    WSResult result;

    String error = "No problems.";
    String debug = "...";

    public Terrier () {
        connect();
    }
    
    public boolean isConnected () {
        if (connection == null) {
            connect();
        }
        if (connection == null) {
            debug = "noconnection ";
            error = "Connection to Alitheia failed.";
            return false;
        }
        return true;
    }

    public Project getProject(Long projectId) {
        if (!isConnected()) return null;
        debug += "ok";

        Project prj;
        try {
            prj = new Project(
                    connection.retrieveStoredProject(projectId));
        } catch (WSException wse) {
            error = "Could not receive a list of projects.";
            return null;
        }
        return prj;
    }

    public boolean projectHasVersion(Long projectId) {
        if (!isConnected()) return false; // Todo: throw an exception instead?
        try {
            WSStoredProject project =
                connection.retrieveStoredProject(projectId);
            // Todo: Uncomment once implemented
            //if (project.getProjectVersions() != null) return true;
        } catch (WSException e) {
            return false;
        }
        return false;
    }

    public Vector<Project> getEvaluatedProjects() {
        Vector<Project> projects = new Vector<Project>();
        if (!isConnected()) return projects;
        debug += "ok";
        try {
            WSStoredProject projectsResult[] =
                connection.storedProjectsList(); // TODO: Use evaluated instead
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

    public MetricsTableView getMetrics4Project(Long projectId) {
        if (!isConnected()) return null;
        MetricsTableView view = new MetricsTableView();
        try {
            WSMetric[] metrics =
                connection.retrieveMetrics4SelectedProject(projectId);
            debug += ":metrics=" + metrics.length;
            for (WSMetric met : metrics) {
                view.addMetric(new Metric(met));
            }
        } catch (WSException e) {
            error = "Could not receive a list of metrics for this project.";
        }
        return view;
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
            session = new WSSession("bla", "foo", "http://localhost:8088/sqooss/services/ws"); // WTF?
            error = "connected";
        } catch (WSException wse) {
            error = "Couldn't start Alitheia session.";
            debug += "nosession";
            wse.printStackTrace();
            session = null;
            connection = null;
            return;
        }
        try {
            connection = session.getConnection();
        } catch (WSException wse) {
            debug += "noconnection";
            error = "Couldn't connect to Alitheia's webservice.";
            wse.printStackTrace();
            connection = null;
        }
    }
}
