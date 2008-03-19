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

interface ProjectManagerDBQueries {

    public static final String EVALUATED_PROJECTS_LIST = "select sp " +
                                                         "from StoredProject sp, EvaluationMark em " +
                                                         "where sp.id=em.storedProject ";
    
    
    public static final String STORED_PROJECTS_LIST    = "from StoredProject";

    
    public static final String RETRIEVE_FILE_LIST_PARAM = "project_id";
    
    public static final String RETRIEVE_FILE_LIST = "select distinct pf " +
                                                    "from ProjectVersion pv, ProjectFile pf " +
                                                    "where pf.projectVersion=pv.id " +
                                                    " and pv.project.id=:" +
                                                    RETRIEVE_FILE_LIST_PARAM;
    
    
    public static final String GET_FILE_LIST_4_PROJECT_VERSION_PARAM = "project_ver";
    
    public static final String GET_FILE_LIST_4_PROJECT_VERSION = "select pf " +
                                                                 "from ProjectFile pf " +
                                                                 "where pf.projectVersion.id=:" +
                                                                 GET_FILE_LIST_4_PROJECT_VERSION_PARAM;
    
    
    public static final String GET_FILES_NUMBER_4_PROJECT_VERSION_PARAM = "project_ver";
    
    public static final String GET_FILES_NUMBER_4_PROJECT_VERSION = "select count(pf)" +
    		                                                        "from ProjectFile pf " +
    		                                                        "where pf.projectVersion.id=:" +
    		                                                        GET_FILES_NUMBER_4_PROJECT_VERSION_PARAM;
    
    
    public static final String GET_STORED_PROJECTS_PARAM_PR_NAME    = "project_name";
    
    public static final String GET_STORED_PROJECTS_PARAM_PR_VERSION = "project_ver";
    
    public static final String GET_STORED_PROJECTS = "select sp " +
                                                     "from StoredProject sp, ProjectVersion pv " +
                                                     "where sp.id=pv.project " +
                                                     " and sp.name=:" +
                                                     GET_STORED_PROJECTS_PARAM_PR_NAME + " " +
                                                     " and pv.version=:" +
                                                     GET_STORED_PROJECTS_PARAM_PR_VERSION;
    
}

//vi: ai nosi sw=4 ts=4 expandtab
