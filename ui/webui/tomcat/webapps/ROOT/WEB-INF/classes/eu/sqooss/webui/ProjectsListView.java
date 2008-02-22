/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008-2008 by Sebastian Kuegler <sebas@kde.org>
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

import eu.sqooss.webui.ListView;
import eu.sqooss.scl.WSException;
import eu.sqooss.scl.WSSession;
import eu.sqooss.scl.result.WSResult;


public class ProjectsListView extends ListView {

    private Long projectId = null;
    private Terrier terrier;
    private Project currentProject;

    public ProjectsListView () {
        
    }

    public void setCurrentProject (Project project ) {
        currentProject = project;
    }

    public Project getCurrentProject () {
        return currentProject;
    }

    public void setProjectId(String projectId) {
        try {
            if (new Long(projectId) != null) {
                this.projectId = new Long(projectId);
                if (terrier != null) {
                    setCurrentProject(
                            terrier.getProject(this.projectId));
                }
            }
        }
        catch (NumberFormatException ex){
            
        }
    }

    public Long getProjectId() {
        return projectId;
    }

    public void retrieveData (Terrier terrier) {
        this.terrier = terrier;
//        try {
//            result = session.getConnection().evaluatedProjectsList();//.next().get(0).getLong() + "=";
//            setItems(result);
//            //TODO:
//        } catch (WSException wse) {
//            error += "<br />Something went wrong getting evaluatedProjectsList() ... :/";
//        } catch (NullPointerException npe) {
//            error += "<br />We didn't connect ...";
//        }
        //TODO:
    }

    public String getHtml() {
        StringBuilder html = new StringBuilder();
        if (terrier != null) {
            Vector<Project> projects = terrier.getEvaluatedProjects();
            if (projects.size() > 0) {
                html.append("\n<!-- Projects -->");
                html.append("\n<ul>");
                for (Project p: projects) {
                    html.append(
                            "\n\t<li>"
                            + "<a href=\"?id=" + p.getId() + "\">"
                            + p.getName() + "</a></li>");
                }
                html.append("\n</ul>");
            }
        }
        return html.toString();
    }
}