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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import eu.sqooss.impl.service.web.services.datatypes.WSMetricsRequest;
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
    
    public List<?> getMetricsByProjectId(long projectId) {
        Map<String, Object> queryParameters = new Hashtable<String, Object>(1);
        queryParameters.put(GET_METRICS_BY_PROJECT_ID_PARAM, projectId);
        
        return db.doHQL(GET_METRICS_BY_PROJECT_ID, queryParameters);
    }
    
    public List<?> getMetricTypesByIds(long[] metricTypesIds) {
        Map<String, Collection> queryParameters = new Hashtable<String, Collection>();
        Collection idsCollection = new ArrayList();
        for (long id : metricTypesIds) {
            idsCollection.add(id);
        }
        queryParameters.put(GET_METRIC_TYPES_BY_IDS_PARAM, idsCollection);
        return db.doHQL(GET_METRIC_TYPES_BY_IDS, null, queryParameters);
    }
    
    public List<?> getMetricsByResourcesIds(WSMetricsRequest request) {
        long[] ids = request.getResourcesIds();
        Collection idsCollection = new ArrayList();
        for (long id : ids) {
            idsCollection.add(id);
        }
        Map<String, Collection> queryParameters = new Hashtable<String, Collection>(1);
        if (request.getIsProjectFile()) {
            queryParameters.put(GET_METRICS_BY_RESOURCES_IDS_PARAM, idsCollection);
            return db.doHQL(GET_METRICS_BY_RESOURCES_IDS_PROJECT_FILES, null, queryParameters);
        } else if (request.getIsProjectVersion()) {
            queryParameters.put(GET_METRICS_BY_RESOURCES_IDS_PARAM, idsCollection);
            return db.doHQL(GET_METRICS_BY_RESOURCES_IDS_PROJECT_VERSIONS, null, queryParameters);
        } else if (request.getIsStoredProject()) {
            queryParameters.put(GET_METRICS_BY_RESOURCES_IDS_PARAM, idsCollection);
            return db.doHQL(GET_METRICS_BY_RESOURCES_IDS_STORED_PROJECTS, null, queryParameters);
        } else if (request.getIsFileGroup()) {
            queryParameters.put(GET_METRICS_BY_RESOURCES_IDS_PARAM, idsCollection);
            return db.doHQL(GET_METRICS_BY_RESOURCES_IDS_FILE_GROUPS, null, queryParameters);
        } else {
            return Collections.emptyList();
        }
    }
    
    public DAObject getMetricsResultDAObject(WSMetricsResultRequest resultRequest) {
        long daObjectId = resultRequest.getDaObjectId();
        DAObject result = null;
        if (resultRequest.isFileGroup()) {
            result = db.findObjectById(FileGroup.class, daObjectId);
        } else if (resultRequest.isProjectFile()) {
            result = db.findObjectById(ProjectFile.class, daObjectId);
        } else if (resultRequest.isProjectVersion()) {
            result = db.findObjectById(ProjectVersion.class, daObjectId);
        } else if (resultRequest.isStoredProject()) {
            result = db.findObjectById(StoredProject.class, daObjectId);
        }
        return result;
    }
    
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
