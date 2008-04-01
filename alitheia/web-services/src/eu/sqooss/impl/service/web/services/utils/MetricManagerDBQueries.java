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
    
    public static final String RETRIEVE_METRICS_4_SELECTED_PROJECT_PARAM = "project_id";
    
    /* COMMENTED
    public static final String RETRIEVE_METRICS_4_SELECTED_PROJECT = "select distinct metric, metricType " +
                                                                      "from ProjectVersion pv, Measurement measurement, Metric metric, MetricType metricType " +
                                                                      "where pv.id=measurement.projectVersion " +
                                                                      " and metric.id=measurement.metric " +
                                                                      " and metricType.id=metric.metricType " +
                                                                      " and pv.project.id=:" +
                                                                      RETRIEVE_METRICS_4_SELECTED_PROJECT_PARAM;
    */

    public static final String RETRIEVE_METRICS_4_SELECTED_PROJECT =
        "select distinct metric, metricType"
        + " from EvaluationMark em, Metric metric, MetricType metricType"
        + " where"
            + " em.storedProject.id=:" + RETRIEVE_METRICS_4_SELECTED_PROJECT_PARAM
            + " and metric.id=em.metric"
            + " and metricType.id=metric.metricType";
    
    
    public static final String RETRIEVE_SELECTED_METRIC_PARAM_PR = "project_id";
    
    public static final String RETRIEVE_SELECTED_METRIC_PARAM_METRIC = "metric_id";
    
    public static final String RETRIEVE_SELECTED_METRIC = "select distinct metric, metricType " +
                                                          "from EvaluationMark em, Metric metric, MetricType metricType " +
                                                          "where metric.id=em.metric " +
                                                          " and metricType.id=metric.metricType " +
                                                          " and em.storedProject.id=:" +
                                                          RETRIEVE_SELECTED_METRIC_PARAM_PR + " " +
                                                          " and metric.id=:" +
                                                          RETRIEVE_SELECTED_METRIC_PARAM_METRIC;
    
    
    public static final String RETRIEVE_METRICS_4_SELECTED_FILES_PARAM_LIST = "list_of_filenames";
    
    public static final String RETRIEVE_METRICS_4_SELECTED_FILES_PARAM_PR = "project_id";
    
    public static final String RETRIEVE_METRICS_4_SELECTED_FILES = "select distinct metric, metricType " +
                                                                   "from ProjectFile pf, ProjectVersion pv, ProjectFileMeasurement pfm, " +
                                                                   "     Metric metric, MetricType metricType " +
                                                                   "where pv.id=pf.projectVersion " +
                                                                   " and pf.id=pfm.projectFile " +
                                                                   " and metric.id=pfm.metric " +
                                                                   " and metricType.id=metric.metricType " +
                                                                   " and pf.name in (:" +
                                                                   RETRIEVE_METRICS_4_SELECTED_FILES_PARAM_LIST + ") " +
                                                                   " and pv.project.id=:" +
                                                                   RETRIEVE_METRICS_4_SELECTED_FILES_PARAM_PR;
                                                 
    
    public static final String RETRIEVE_METRICS_4_SELECTED_FILES_DIRS_PARAM = "dir_name";
    
    public static final String RETRIEVE_METRICS_4_SELECTED_FILES_DIRS = "select pf.name " +
                                                                        "from ProjectFile pf, ProjectVersion pv " +
                                                                        "where pf.name like :" +
                                                                        RETRIEVE_METRICS_4_SELECTED_FILES_DIRS_PARAM + " " +
                                                                        " and pv.project.id=:" +
                                                                        RETRIEVE_METRICS_4_SELECTED_FILES_PARAM_PR;
    
    
    public static final String GET_PROJECT_FILE_METRIC_MEASUREMENT_PARAM_FILE   = "project_file_id";
    
    public static final String GET_PROJECT_FILE_METRIC_MEASUREMENT_PARAM_METRIC = "metric_id";
    
    public static final String GET_PROJECT_FILE_METRIC_MEASUREMENT = "select m " + 
                                                                     "from ProjectFileMeasurement m " +
                                                                     "where m.projectFile.id=:" +
                                                                     GET_PROJECT_FILE_METRIC_MEASUREMENT_PARAM_FILE + " " +
                                                                     " and m.metric.id=:" +
                                                                     GET_PROJECT_FILE_METRIC_MEASUREMENT_PARAM_METRIC;
    
    
    public static final String GET_PROJECT_VERSION_METRIC_MEASUREMENT_PARAM_VERSION   = "project_file_id";
    
    public static final String GET_PROJECT_VERSION_METRIC_MEASUREMENT_PARAM_METRIC    = "metric_id";
    
    public static final String GET_PROJECT_VERSION_METRIC_MEASUREMENT = "select m " + 
                                                                        "from ProjectVersionMeasurement m " +
                                                                        "where m.projectVersion.id=:" +
                                                                        GET_PROJECT_VERSION_METRIC_MEASUREMENT_PARAM_VERSION + " " +
                                                                        " and m.metric.id=:" +
                                                                        GET_PROJECT_VERSION_METRIC_MEASUREMENT_PARAM_METRIC;
    
    
}

//vi: ai nosi sw=4 ts=4 expandtab
