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

package eu.sqooss.impl.service.web.services.utils;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.hibernate.QueryException;

import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;

public class ProjectManagerDatabase implements ProjectManagerDBQueries {
    
    private DBService db;
    
    public ProjectManagerDatabase(DBService db) {
        this.db = db;
    }
    
    public List<?> getEvaluatedProjects() {
        return db.doHQL(GET_EVALUATED_PROJECTS);
    }
    
    public List<?> getStoredProjects() {
        return db.doHQL(GET_STORED_PROJECTS);
    }
    
    public List<StoredProject> getStoredProjects(String projectName) {
        Map<String, Object> properties = new Hashtable<String, Object>(1);
        properties.put("name", projectName);
        return db.findObjectsByProperties(StoredProject.class, properties);
    }

    public StoredProject getProjectById(long projectId) {
        return db.findObjectById(StoredProject.class, projectId);
    }

    public List<?> getStoredProjects(String projectName, long projectVersion) {
        Map<String, Object> queryParameters = new Hashtable<String, Object>(2);
        queryParameters.put(GET_STORED_PROJECTS_PARAM_PR_NAME, projectName);
        queryParameters.put(GET_STORED_PROJECTS_PARAM_PR_VERSION, projectVersion);
        
        return db.doHQL(GET_STORED_PROJECTS_BY_NAME_VERSION, queryParameters);
    }
    
    public List<?> getFilesByProjectId(long projectId) {
        Map<String, Object> queryParameters = new Hashtable<String, Object>(1);
        queryParameters.put(GET_FILES_BY_PROJECT_ID_PARAM, projectId);

        return db.doHQL(GET_FILES_BY_PROJECT_ID, queryParameters);
    }
    
    public List<?> getFilesByProjectVersionId(long projectVersionId) {
        Map<String, Object> queryParameters = new Hashtable<String, Object>(1);
        queryParameters.put(GET_FILES_BY_PROJECT_VERSION_ID_PARAM, projectVersionId);
        
        try {
            return db.doSQL(GET_FILES_BY_PROJECT_VERSION_ID, queryParameters);
        } catch (SQLException e) {
            return Collections.emptyList();
        }
    }
    
    public List<?> getFilesNumberByProjectVersionId(long projectVersionId) {
        Map<String, Object> queryParameters = new Hashtable<String, Object>(1);
        queryParameters.put(GET_FILES_NUMBER_BY_PROJECT_VERSION_ID_PARAM, projectVersionId);
        
        try {
            return db.doSQL(GET_FILES_NUMBER_BY_PROJECT_VERSION_ID, queryParameters);
        } catch (SQLException e) {
            return Collections.emptyList();
        }
    }
    
    public List<?> getFilesNumberByProjectId(long projectId) {
        Map<String, Object> queryParameters = new Hashtable<String, Object>(1);
        queryParameters.put(GET_FILES_NUMBER_BY_PROJECT_ID_PARAM, projectId);
        
        try {
            return db.doHQL(GET_FILES_NUMBER_BY_PROJECT_ID, queryParameters);
        } catch (QueryException qe) {
            return Collections.emptyList();
        }
        
    }
    
    public long createNewProject(StoredProject newProject, ProjectVersion newProjectVersion) {
            db.addRecord(newProject);
            long newProjectId = newProject.getId();
            newProjectVersion.setProject(newProject);
            db.addRecord(newProjectVersion);
            return newProjectId;
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
