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

import java.util.Random;
import java.util.Vector;

import eu.sqooss.webui.ListView;
import eu.sqooss.scl.WSException;
import eu.sqooss.scl.WSSession;


public class ProjectsListView extends ListView {

    private Long projectId = null;
    private Terrier terrier;
    private Project currentProject;

    // Projects cache and number
    Vector<Project> currentProjects = null;
    private long totalProjects = 0;

    public ProjectsListView () {

    }

    public void setCurrentProject (Project project ) {
        currentProject = project;
    }

    public Project getCurrentProject () {
        return currentProject;
    }

    public void setProjectId(String projectId) {
        if ((terrier == null) || (projectId == null)) {
            return;
        }

        if ("none".equals(projectId)) {
            this.projectId = 0L;
            setCurrentProject(null);
        }

        Long pid = null;
        try {
            pid = new Long(projectId);
            this.projectId = pid;
        }
        catch (NumberFormatException ex){
            this.projectId = 0L;
            setCurrentProject(null);
            return;
        }
        setCurrentProject(terrier.getProject(pid));
    }

    public Long getProjectId() {
        return projectId;
    }

    /**
     * Checks if this object stores information for at least one project.
     *
     * @return true, if one or more projects are stored, otherwise false
     */
    public boolean hasProjects() {
        if (totalProjects > 0) {
            return true;
        }
        return false;
    }

    public void retrieveData (Terrier terrier) {
        this.terrier = terrier;
        currentProjects = terrier.getEvaluatedProjects();
        totalProjects = currentProjects.size();
    }

    public String getHtml() {
        StringBuilder html = new StringBuilder();
        if (currentProjects != null) {
            if (currentProjects.size() > 0) {
                html.append("\n<!-- Projects -->");
                html.append("\n<ul class=\"projectslist\">");
                for (Project p: currentProjects) {
                    html.append(
                            "\n\t<li>"
                            + "<a href=\"?pid=" + p.getId() + "\">"
                            + p.getName() + "</a></li>");
                }
                html.append("\n</ul>");
            }
        }
        return html.toString();
    }

    @Override
    public void retrieveData() {
        //TODO: retrieve some data
    }


    /**
     * This method selects up to 13 projects from the list
     * which are somehow "interesting" and which can be displayed
     * in the tag cloud.
     */
    public Vector<Project> getCloudProjects() {
        Vector<Project> v = new Vector<Project>(13);
        Random r = new Random();

        for (int i = 0; (i<13) && (i<totalProjects); ++i) {
            int j = r.nextInt((int)totalProjects);
            if (!v.contains(currentProjects.elementAt(j))) {
                v.add(currentProjects.elementAt(j));
            }
        }

        return v;
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

