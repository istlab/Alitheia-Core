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

import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import eu.sqooss.impl.service.web.services.datatypes.WSMetric;
import eu.sqooss.impl.service.web.services.datatypes.WSMetricType;
import eu.sqooss.impl.service.web.services.datatypes.WSMetricsResultRequest;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.FileGroup;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;

public class MetricManagerDatabase implements MetricManagerDBQueries {
    
    private DBService db;
    
    public MetricManagerDatabase(DBService db) {
        this.db = db;
    }
    
    public WSMetric[] getProjectEvaluatedMetrics(long projectId) {
        Map<String, Object> queryParameters = new Hashtable<String, Object>(1);
        queryParameters.put(GET_PROJECT_EVALUATED_METRICS_PARAM, projectId);
        
        List<?> metrics = db.doHQL(GET_PROJECT_EVALUATED_METRICS, queryParameters);
        WSMetric[] result = WSMetric.asArray(metrics);
        return result;
    }
    
    @SuppressWarnings("unchecked")
    public WSMetricType[] getMetricTypesByIds(Collection<Long> metricTypesIds) {
        Map<String, Collection> queryParameters = new Hashtable<String, Collection>();
        queryParameters.put(GET_METRIC_TYPES_BY_IDS_PARAM, metricTypesIds);
        List<?> metricTypes = db.doHQL(GET_METRIC_TYPES_BY_IDS, null, queryParameters);
        WSMetricType[] result = WSMetricType.asArray(metricTypes);
        return result;
    }
    
    @SuppressWarnings("unchecked")
    public WSMetric[] getMetricsByResourcesIds(Collection<Long> ids,
            boolean isProjectFile, boolean isProjectVersion,
            boolean isStoredProject, boolean isFileGroup) {
        Map<String, Collection> queryParameters = new Hashtable<String, Collection>(1);
        List<?> metrics;
        if (isProjectFile) {
            queryParameters.put(GET_METRICS_BY_RESOURCES_IDS_PARAM, ids);
            metrics = db.doHQL(GET_METRICS_BY_RESOURCES_IDS_PROJECT_FILES, null, queryParameters);
        } else if (isProjectVersion) {
            queryParameters.put(GET_METRICS_BY_RESOURCES_IDS_PARAM, ids);
            metrics = db.doHQL(GET_METRICS_BY_RESOURCES_IDS_PROJECT_VERSIONS, null, queryParameters);
        } else if (isStoredProject) {
            queryParameters.put(GET_METRICS_BY_RESOURCES_IDS_PARAM, ids);
            metrics = db.doHQL(GET_METRICS_BY_RESOURCES_IDS_STORED_PROJECTS, null, queryParameters);
        } else if (isFileGroup) {
            queryParameters.put(GET_METRICS_BY_RESOURCES_IDS_PARAM, ids);
            metrics = db.doHQL(GET_METRICS_BY_RESOURCES_IDS_FILE_GROUPS, null, queryParameters);
        } else {
            metrics = null;
        }
        return WSMetric.asArray(metrics);
    }
    
    public DAObject getMetricsResultDAObject(
            WSMetricsResultRequest resultRequest, long daoId) {
        DAObject result = null;
        if (resultRequest.isFileGroup()) {
            result = db.findObjectById(FileGroup.class, daoId);
        } else if (resultRequest.isProjectFile()) {
            result = db.findObjectById(ProjectFile.class, daoId);
        } else if (resultRequest.isProjectVersion()) {
            result = db.findObjectById(ProjectVersion.class, daoId);
        } else if (resultRequest.isStoredProject()) {
            result = db.findObjectById(StoredProject.class, daoId);
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
    public List<?> getMetricsResultMetricsList(WSMetricsResultRequest resultRequest) {
        String[] mnemonics = resultRequest.getMnemonics();
        if ((mnemonics != null) && (mnemonics.length != 0)) {
            Collection<String> mnemonicsCollection = Arrays.asList(mnemonics);
            Map<String, Collection> params = new Hashtable<String, Collection>(1);
            params.put(GET_METRICS_RESULT_METRICS_LIST_PARAM, mnemonicsCollection);
            return db.doHQL(GET_METRICS_RESULT_METRICS_LIST, null, params);
        } else {
            return null;
        }
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
