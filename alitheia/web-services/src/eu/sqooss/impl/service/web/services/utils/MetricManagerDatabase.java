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

import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import eu.sqooss.service.db.DBService;

public class MetricManagerDatabase implements MetricManagerDBQueries {
    
    private DBService db;
    
    public MetricManagerDatabase(DBService db) {
        this.db = db;
    }
    
    public List<?> getMetricsByProjectId(long projectId) {
        Map<String, Object> queryParameters = new Hashtable<String, Object>(1);
        queryParameters.put(GET_METRICS_BY_PROJECT_ID_PARAM, projectId);
        
        return db.doHQL(GET_METRICS_BY_PROJECT_ID, queryParameters);
    }
    
    public List<?> getMetricsByFileNames(long projectId, Collection fileNames) {
        Map<String, Object> projectIdParameter = new Hashtable<String, Object>(1);
        projectIdParameter.put(GET_METRICS_BY_FILE_NAMES_PARAM_PR,
                projectId);
        Map<String, Collection> fileNamesParameter = new Hashtable<String, Collection>(1);
        fileNamesParameter.put(GET_METRICS_BY_FILE_NAMES_PARAM_LIST,
                fileNames);
        return db.doHQL(GET_METRICS_BY_FILE_NAMES,
                projectIdParameter, fileNamesParameter);
    }
    
    public List<?> getMetrics() {
        return db.doHQL(GET_METRICS);
    }
    
    public List<?> getFilesFromFolder(long projectId, String folder) {
        Map<String, Object> folderNameParameters = new Hashtable<String, Object>(2);
        folderNameParameters.put(GET_METRICS_BY_FILE_NAMES_PARAM_PR,
                projectId);
        folderNameParameters.put(RETRIEVE_METRICS_4_SELECTED_FILES_DIRS_PARAM,
                folder + "%");
        return db.doHQL(RETRIEVE_METRICS_4_SELECTED_FILES_DIRS,
                folderNameParameters);
    }
    
    public List<?> getProjectFileMetricMeasurement(long metricId, long projectFileId) {
        Map<String, Object> params = new Hashtable<String, Object>(2);
        params.put(GET_PROJECT_FILE_METRIC_MEASUREMENT_PARAM_FILE, projectFileId);
        params.put(GET_PROJECT_FILE_METRIC_MEASUREMENT_PARAM_METRIC, metricId);
        return db.doHQL(GET_PROJECT_FILE_METRIC_MEASUREMENT, params);
    }
    
    public List<?> getProjectVersionMetricMeasurement(long metricId, long projectVersionId) {
        Map<String, Object> params = new Hashtable<String, Object>(2);
        params.put(GET_PROJECT_VERSION_METRIC_MEASUREMENT_PARAM_VERSION, projectVersionId);
        params.put(GET_PROJECT_VERSION_METRIC_MEASUREMENT_PARAM_METRIC, metricId);
        return db.doHQL(GET_PROJECT_VERSION_METRIC_MEASUREMENT, params);
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
