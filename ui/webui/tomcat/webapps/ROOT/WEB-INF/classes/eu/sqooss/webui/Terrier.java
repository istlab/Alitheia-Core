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

import java.util.Iterator;
import java.util.ArrayList;

import eu.sqooss.scl.WSException;
import eu.sqooss.scl.WSSession;
import eu.sqooss.scl.WSConnection;
import eu.sqooss.scl.result.WSResult;
import eu.sqooss.scl.result.WSResultEntry;

import eu.sqooss.webui.User;
import eu.sqooss.webui.File;
import eu.sqooss.webui.Metric;
import eu.sqooss.webui.Project;



class Terrier {

    WSSession session;
    WSConnection connection;
    WSResult result;
    String error;

    public void Terrier () {
        connect();
    
    }
    
    public Project getProject(Long projectId) {
        try {
            result = connection.evaluatedProjectsList();
        } catch (WSException wse) {
            error = "Could not receive a list of projects.";
        } catch (NullPointerException npe) {
            error = "Connection to Alitheia most probably failed.";
        }
        Iterator <ArrayList<WSResultEntry>> itemlist = result.iterator();
        if (!itemlist.hasNext()) {
            error = "No project records found.";
            return null;
        }
        while (itemlist.hasNext()) {
            ArrayList <WSResultEntry> p_item = itemlist.next();
            Iterator <WSResultEntry> oneitemlist = p_item.iterator();
            Project nextProject = new Project(p_item);
            if (nextProject.getId() == projectId) {
                return nextProject;
            }
        }
        return null;
    }
    
    public Metric getMetric(Long metricId) {
        return null;
    }

    public User getUser (Long userId) {
        return null;
    }
    
    public File getFile(Long fileId) {
        return null;
    }
    
    private void connect() {
        // Try to connect
        try {
            session = new WSSession("bla", "foo", "http://localhost:8090/sqooss/services/ws");
            connection = session.getConnection();
        } catch (WSException wse) {
            wse.printStackTrace();
            session = null;
        }
    }
}