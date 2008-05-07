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

interface MetricManagerDBQueries {
    
    public static final String GET_METRICS_BY_PROJECT_ID_PARAM = "project_id";
    
    public static final String GET_METRICS_BY_PROJECT_ID = "select distinct metric " +
                                                           "from EvaluationMark em, Metric metric " +
                                                           "where metric.id=em.metric " +
                                                           " and em.storedProject.id=:" +
                                                           GET_METRICS_BY_PROJECT_ID_PARAM;
    
    
    public static final String GET_METRICS_BY_FILE_NAMES_PARAM_LIST = "list_of_filenames";
    
    public static final String GET_METRICS_BY_FILE_NAMES_PARAM_PR = "project_id";
    
    public static final String GET_METRICS_BY_FILE_NAMES = "select distinct metric " +
                                                                   "from ProjectFile pf, ProjectVersion pv, " +
                                                                   "     ProjectFileMeasurement pfm, Metric metric " +
                                                                   "where pv.id=pf.projectVersion " +
                                                                   " and pf.id=pfm.projectFile " +
                                                                   " and metric.id=pfm.metric " +
                                                                   " and pf.name in (:" +
                                                                   GET_METRICS_BY_FILE_NAMES_PARAM_LIST + ") " +
                                                                   " and pv.project.id=:" +
                                                                   GET_METRICS_BY_FILE_NAMES_PARAM_PR;
                                                 
    
    public static final String RETRIEVE_METRICS_4_SELECTED_FILES_DIRS_PARAM = "dir_name";
    
    public static final String RETRIEVE_METRICS_4_SELECTED_FILES_DIRS = "select pf.name " +
                                                                        "from ProjectFile pf, ProjectVersion pv " +
                                                                        "where pf.name like :" +
                                                                        RETRIEVE_METRICS_4_SELECTED_FILES_DIRS_PARAM + " " +
                                                                        " and pv.project.id=:" +
                                                                        GET_METRICS_BY_FILE_NAMES_PARAM_PR;
    
    public static final String GET_METRICS = "from Metric";
    
    
    public static final String GET_METRICS_RESULT_METRICS_LIST_PARAM = "metrics";
    
    public static final String GET_METRICS_RESULT_METRICS_LIST       = "from Metric m "+
                                                                       "where m.mnemonic in (:" +
                                                                       GET_METRICS_RESULT_METRICS_LIST_PARAM +")";
    
}

//vi: ai nosi sw=4 ts=4 expandtab
