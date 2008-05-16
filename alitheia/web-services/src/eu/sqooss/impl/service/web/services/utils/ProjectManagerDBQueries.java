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

    public static final String GET_EVALUATED_PROJECTS = "select sp " +
                                                        "from StoredProject sp, EvaluationMark em " +
                                                        "where sp.id=em.storedProject ";
    
    
    public static final String GET_STORED_PROJECTS = "from StoredProject";

    
    public static final String GET_FILES_BY_PROJECT_ID_PARAM = "project_id";
    
    public static final String GET_FILES_BY_PROJECT_ID = "select distinct pf " +
                                                    "from ProjectVersion pv, ProjectFile pf " +
                                                    "where pf.projectVersion=pv.id " +
                                                    " and pv.project.id=:" +
                                                    GET_FILES_BY_PROJECT_ID_PARAM;
    
    
    public static final String GET_FILES_BY_PROJECT_VERSION_ID_PARAM = "project_ver";
    
    public static final String GET_FILES_BY_PROJECT_VERSION_ID = "select pf.project_file_id, d.directory_id, " + 
                                                                 "       head.fname, head.headrev, pf.file_status, pf.is_directory " +
    		                                                     "from (select pf.directory_id as dir, " +
    		                                                     "             pf.file_name as fname, " +
    		                                                     "             max(pv.project_version_id) as headrev " +
    		                                                     "      from project_file pf, project_version pv " +
    		                                                     "      where pf.project_version_id=pv.project_version_id " +
    		                                                     "            and pv.timestamp<= ( " +
    		                                                     "                select pv2.timestamp " +
    		                                                     "                from project_version pv2 " +
    		                                                     "                where pv2.project_version_id=:" +
    		                                                     GET_FILES_BY_PROJECT_VERSION_ID_PARAM +
    		                                                     "                ) " +       
    		                                                     "      group by pf.directory_id, pf.file_name) head," +
    		                                                     "      project_file pf, directory d " +
    		                                                     "where d.directory_id=pf.directory_id " +
    		                                                     "      and head.dir=pf.directory_id " +
    		                                                     "      and head.fname=pf.file_name " +
    		                                                     "      and pf.project_version_id=head.headrev " +
    		                                                     "      and pf.file_status<>'DELETED' " +
    		                                                     "order by d.path, head.fname";
    
    
    public static final String GET_FILES_NUMBER_BY_PROJECT_VERSION_ID_PARAM = "project_ver";
    
    public static final String GET_FILES_NUMBER_BY_PROJECT_VERSION_ID = "select count(*) " +
                                                                    "from (select pf.directory_id as dir, " +
                                                                    "             pf.file_name as fname, " +
                                                                    "             max(pv.project_version_id) as headrev " +
                                                                    "      from project_file pf, project_version pv " +
                                                                    "      where pf.project_version_id=pv.project_version_id " +
                                                                    "            and pv.timestamp<= ( " +
                                                                    "                select pv2.timestamp " +
                                                                    "                from project_version pv2 " +
                                                                    "                where pv2.project_version_id=:" +
                                                                    GET_FILES_BY_PROJECT_VERSION_ID_PARAM +
                                                                    "                ) " +       
                                                                    "      group by pf.directory_id, pf.file_name) head," +
                                                                    "      project_file pf, directory d " +
                                                                    "where d.directory_id=pf.directory_id " +
                                                                    "      and head.dir=pf.directory_id " +
                                                                    "      and head.fname=pf.file_name " +
                                                                    "      and pf.project_version_id=head.headrev " +
                                                                    "      and pf.file_status<>'DELETED' ";
    
    
    public static final String GET_FILES_NUMBER_BY_PROJECT_ID_PARAM = "project_id";
    
    public static final String GET_FILES_NUMBER_BY_PROJECT_ID = "select count(*) " +
                                                                "from ProjectVersion pv, ProjectFile pf " +
                                                                "where pv.id=pf.projectVersion " +
                                                                " and pv.project.id=:" +
                                                                GET_FILES_NUMBER_BY_PROJECT_ID_PARAM;
    
    
    public static final String GET_STORED_PROJECTS_PARAM_PR_NAME    = "project_name";
    
    public static final String GET_STORED_PROJECTS_PARAM_PR_VERSION = "project_ver";
    
    public static final String GET_STORED_PROJECTS_BY_NAME_VERSION = "select sp " +
                                                     "from StoredProject sp, ProjectVersion pv " +
                                                     "where sp.id=pv.project " +
                                                     " and sp.name=:" +
                                                     GET_STORED_PROJECTS_PARAM_PR_NAME + " " +
                                                     " and pv.version=:" +
                                                     GET_STORED_PROJECTS_PARAM_PR_VERSION;
    
}

//vi: ai nosi sw=4 ts=4 expandtab
