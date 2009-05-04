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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import eu.sqooss.webui.ListView;


public class ProjectsListView extends ListView {

    // Holds the current project
    private Project currentProject;

    // Cache for all project that were retrieved during the last call to
    // the SQO-OSS framework
    //List<Project> projects = new ArrayList<Project>();
    HashMap<Long,Project> projects = new HashMap<Long, Project>();
    
    
    /**
     * Sets the current project.
     * 
     * @param project the new current project
     */
    public void setCurrentProject (Project project ) {
        if (project != null) {
            currentProject = project;
            currentProject.setTerrier(terrier);
        }
        else
            this.currentProject = null;
    }

    /**
     * Sets as current the project with the given Id. If the project does not
     * persist in the local cache, then this method will try to retrieve it
     * from the SQO-OSS framework.
     * 
     * @param projectId the Id of the new current project
     */
    public void setCurrentProject(Long projectId) {
        if (terrier == null)
            return;
        if (projectId != null) {
            // Update the cache if necessary
            if (getProject(projectId) != null) {
                Project project = terrier.getProject(projectId);
                if (project != null)
                    projects.put(project.id, project);
            }
            // Retrieve the project from the cache
            setCurrentProject(getProject(projectId));
        }
        else
            this.currentProject = null;
    }

    /**
     * Gets the current project.
     * 
     * @return The current project, or <code>null<code> when none.
     */
    public Project getCurrentProject () {
        return currentProject;
    }

    /**
     * Gets the Id of the current project.
     * 
     * @return the Id of the current project, or <code>null<code> if none.
     */
    public Long getCurrentProjectId() {
        if (currentProject != null)
            return currentProject.getId();
        else
            return null;
    }

    /**
     * This method will check, if there is at least one project is stored in
     * the local cache.
     *
     * @return <code>true</code>, if one or more projects are stored,
     *   otherwise <code>false</code>.
     */
    public boolean hasProjects() {
        return (!projects.isEmpty());
    }

    /**
     * Retrieves the project with the given Id from the local cache.
     * 
     * @param projectId the project Id
     * 
     * @return The corresponding <code>Project</code> object, when found in
     *   the local cache, otherwise <code>null</code>.
     */
    public Project getProject (long projectId) {
        return projects.get(projectId);
    }

    /**
     * Retrieves all the data that is required by this object from the
     * SQO-OSS framework, unless the cache contains some data already.
     * 
     * @param terrier the <code>Terrier<code> instance
     */
    public void retrieveData (Terrier terrier) {
        this.terrier = terrier;
        if (projects.isEmpty()) {
            List<Project> prs = terrier.getEvaluatedProjects();
            for (Project pr : prs) {
                projects.put(pr.id, pr);
            }
        }   
    }

    /**
     * Flushes all the data that is cached by this object.
     */
    public void flushData () {
        currentProject = null;
        projects = new HashMap<Long, Project>();
    }

    /* (non-Javadoc)
     * @see eu.sqooss.webui.ListView#getHtml(long)
     */
    public String getHtml(long in) {
        StringBuilder html = new StringBuilder();
        if (projects.size() > 0) {
            html.append(sp(in++) + "<ul class=\"projectslist\">\n");
            for (Long id: projects.keySet()) {
                projects.get(id).setServletPath(getServletPath());
                html.append(sp(in) + "<li>" + projects.get(id).link() + "</li>\n");
            }
            html.append(sp(--in) + "</ul>\n");
        }
        return html.toString();
    }

    /**
     * This method selects up to <code>number<code> projects from the projects
     * cache which are somehow "interesting"(<i>TODO</i>) and therefore can be
     * displayed in the tag cloud.
     * 
     * @param number the maximum number of projects that should be pushed in
     *   the results list.
     * 
     * @return The list of cloud projects.
     */
    public Vector<Project> getCloudProjects(int number) {
        Vector<Project> v = new Vector<Project>(number);
        
        List<Project> l = new ArrayList<Project>(); 
        l.addAll(projects.values());
        Collections.shuffle(l);
        Iterator<Project> it = l.iterator();
        
        if (projects.size() <= number) {
            while (it.hasNext())
                v.add(it.next());
        } else {
            for (int i = 0; i < number; i++) {
                v.add(it.next());
            }
        }
        return v;
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
 