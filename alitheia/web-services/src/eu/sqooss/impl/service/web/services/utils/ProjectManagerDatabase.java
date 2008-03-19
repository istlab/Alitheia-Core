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

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;

public class ProjectManagerDatabase implements ProjectManagerDBQueries {
    
    private DBService db;
    
    public ProjectManagerDatabase(DBService db) {
        this.db = db;
    }
    
    public List<?> evaluatedProjectsList() {
        return db.doHQL(EVALUATED_PROJECTS_LIST);
    }
    
    public List<?> storedProjectsList() {
        return db.doHQL(STORED_PROJECTS_LIST);
    }
    
    public List<StoredProject> getStoredProjects(String projectName) {
        Map<String, Object> properties = new Hashtable<String, Object>(1);
        properties.put("name", projectName);
        return db.findObjectByProperties(StoredProject.class, properties);
    }

    public StoredProject getStoredProject(long projectId) {
        return db.findObjectById(StoredProject.class, projectId);
    }

    public List<?> getStoredProjects(String projectName, long projectVersion) {
        Map<String, Object> queryParameters = new Hashtable<String, Object>(2);
        queryParameters.put(GET_STORED_PROJECTS_PARAM_PR_NAME, projectName);
        queryParameters.put(GET_STORED_PROJECTS_PARAM_PR_VERSION, projectVersion);
        
        return db.doHQL(GET_STORED_PROJECTS, queryParameters);
    }
    
    public List<?> retrieveFileList(long projectId) {
        Map<String, Object> queryParameters = new Hashtable<String, Object>(1);
        queryParameters.put(RETRIEVE_FILE_LIST_PARAM, projectId);

        return db.doHQL(RETRIEVE_FILE_LIST, queryParameters);
    }
    
    public List<?> getFileList4ProjectVersion(long projectVersionId) {
        Map<String, Object> queryParameters = new Hashtable<String, Object>(1);
        queryParameters.put(GET_FILE_LIST_4_PROJECT_VERSION_PARAM, projectVersionId);
        
        return db.doHQL(GET_FILE_LIST_4_PROJECT_VERSION, queryParameters);
    }
    
    public List<?> getFilesNumber4ProjectVersion(long projectVersionId) {
        Map<String, Object> queryParameters = new Hashtable<String, Object>(1);
        queryParameters.put(GET_FILES_NUMBER_4_PROJECT_VERSION_PARAM, projectVersionId);
        
        return db.doHQL(GET_FILES_NUMBER_4_PROJECT_VERSION, queryParameters);
    }
    
    public long createNewProject(StoredProject newProject, ProjectVersion newProjectVersion) {
        Session dbSession = null;
        Transaction transaction = null;
        try {
            dbSession = db.getSession(this);
            transaction = dbSession.beginTransaction();
            //        db.addRecord(dbSession, newStoredProject);
            dbSession.save(newProject);
            long newProjectId = newProject.getId();
            newProjectVersion.setProject(newProject);
            //        db.addRecord(dbSession, newProjectVersion);
            dbSession.save(newProjectVersion);
            transaction.commit();
            db.returnSession(dbSession);
            return newProjectId;
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
            if (dbSession != null) {
                db.returnSession(dbSession);
            }
            throw he;
        }
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
