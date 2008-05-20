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
    
    
    public static final String GET_METRICS = "from Metric";
    
    
    public static final String GET_METRICS_BY_RESOURCES_IDS_PARAM = "list_of_ids";
    
    public static final String GET_METRICS_BY_RESOURCES_IDS_STORED_PROJECTS = "select spm.metric " +
                                                                              "from StoredProjectMeasurement spm " +
    		                                                                  "where spm.storedProject.id in (:" +
    		                                                                  GET_METRICS_BY_RESOURCES_IDS_PARAM + ") ";
    
    public static final String GET_METRICS_BY_RESOURCES_IDS_PROJECT_VERSIONS = "select pvm.metric " +
    		                                                                   "from ProjectVersionMeasurement pvm " +
    		                                                                   "where pvm.projectVersion.id in (:" +
    		                                                                   GET_METRICS_BY_RESOURCES_IDS_PARAM + ") ";
    
    public static final String GET_METRICS_BY_RESOURCES_IDS_PROJECT_FILES = "select pfm.metric " +
      		                                                                "from ProjectFileMeasurement pfm " +
    		                                                                "where pfm.projectFile.id in (:" +
    		                                                                GET_METRICS_BY_RESOURCES_IDS_PARAM + ") ";
    
    public static final String GET_METRICS_BY_RESOURCES_IDS_FILE_GROUPS = "select fgm.metric " +
    		                                                              "from FileGroupMeasurement fgm " +
    		                                                              "where fgm.fileGroup.id in (:" +
    		                                                              GET_METRICS_BY_RESOURCES_IDS_PARAM + ") ";
    
    
    public static final String GET_METRICS_RESULT_METRICS_LIST_PARAM = "metrics";
    
    public static final String GET_METRICS_RESULT_METRICS_LIST       = "from Metric m "+
                                                                       "where m.mnemonic in (:" +
                                                                       GET_METRICS_RESULT_METRICS_LIST_PARAM +")";
    
    
    public static final String GET_METRIC_TYPES_BY_IDS_PARAM = "list_of_ids";
    
    public static final String GET_METRIC_TYPES_BY_IDS       = "select mt " +
    		                                                   "from MetricType mt " +
    		                                                   "where mt.id in (:" +
    		                                                   GET_METRIC_TYPES_BY_IDS_PARAM + ") ";
}

//vi: ai nosi sw=4 ts=4 expandtab
