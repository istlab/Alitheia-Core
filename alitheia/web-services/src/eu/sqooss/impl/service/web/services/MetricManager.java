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

package eu.sqooss.impl.service.web.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import eu.sqooss.impl.service.web.services.datatypes.WSMetric;
import eu.sqooss.impl.service.web.services.datatypes.WSMetricType;
import eu.sqooss.impl.service.web.services.datatypes.WSMetricsRequest;
import eu.sqooss.impl.service.web.services.datatypes.WSMetricsResultRequest;
import eu.sqooss.impl.service.web.services.datatypes.WSResultEntry;
import eu.sqooss.impl.service.web.services.utils.MetricManagerDatabase;
import eu.sqooss.impl.service.web.services.utils.SecurityWrapper;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.abstractmetric.MetricMismatchException;
import eu.sqooss.service.abstractmetric.Result;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.FileGroup;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.security.SecurityManager;

public class MetricManager extends AbstractManager {
    
    private Logger logger;
    private PluginAdmin pluginAdmin;
    private MetricManagerDatabase dbWrapper;
    private SecurityWrapper securityWrapper;
    
    public MetricManager(Logger logger, DBService db,
            PluginAdmin pluginAdmin, SecurityManager security) {
        super(db);
        this.logger = logger;
        this.pluginAdmin = pluginAdmin;
        this.dbWrapper = new MetricManagerDatabase(db);
        this.securityWrapper = new SecurityWrapper(security);
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getMetricsByProjectId(String, String, long)
     */
    public WSMetric[] getMetricsByProjectId(String userName,
            String password, long projectId) {
        
        logger.info("Retrieve metrics for selected project! user: " + userName +
                "; project id:" + projectId);
        
        securityWrapper.checkProjectsReadAccess(userName, password, new long[] {projectId});
        
        super.updateUserActivity(userName);
        
        db.startDBSession();
        List<?> metrics = dbWrapper.getMetricsByProjectId(projectId);
        WSMetric[] wsMetrics = convertToWSMetrics(metrics);
        db.commitDBSession();
        return wsMetrics;
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getMetricTypesByIds(String, String, long[])
     */
    public WSMetricType[] getMetricTypesByIds(String userName,
            String password, long[] metricTypesIds) {
        
        logger.info("Retrieve metric types with given ids! user: " + userName +
                "; metric types ids: " + Arrays.toString(metricTypesIds));
        
        securityWrapper.checkDBReadAccess(userName, password);
        
        super.updateUserActivity(userName);
        
        if (metricTypesIds == null) {
            return null;
        }
        
        db.startDBSession();
        List<?> metricTypes = dbWrapper.getMetricTypesByIds(metricTypesIds);
        db.commitDBSession();
        
        return convertToWSMetricTypes(metricTypes);
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getMetrics(String, String)
     */
    public WSMetric[] getMetrics(String userName, String password) {
        logger.info("Get metrics! user: " + userName);
        
        securityWrapper.checkMetricsReadAccess(userName, password, null);
        
        super.updateUserActivity(userName);
        
        db.startDBSession();
        WSMetric[] wsMetrics = convertToWSMetrics(dbWrapper.getMetrics());
        db.commitDBSession();
        return wsMetrics;
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getMetricsByResourcesIds(String, String, WSMetricsRequest)
     */
    public WSMetric[] getMetricsByResourcesIds(
            String userName,
            String password,
            WSMetricsRequest request) {
        logger.info("Get metrics by resources ids! user: " + userName +
                "; request: " + request.toString());
        
        securityWrapper.checkMetricsReadAccess(userName, password, null);
        
        super.updateUserActivity(userName);
        
        if (request.getSkipResourcesIds()) {
            List<Metric> metrics = new ArrayList<Metric>();
            if (request.getIsFileGroup()) {
                metrics.addAll(getMetrics(pluginAdmin.
                        listPluginProviders(FileGroup.class)));
            }
            if (request.getIsProjectFile()) {
                metrics.addAll(getMetrics(pluginAdmin.
                        listPluginProviders(ProjectFile.class)));
            }
            if (request.getIsProjectVersion()) {
                metrics.addAll(getMetrics(pluginAdmin.
                        listPluginProviders(ProjectVersion.class)));
            }
            if (request.getIsStoredProject()) {
                metrics.addAll(getMetrics(pluginAdmin.
                        listPluginProviders(StoredProject.class)));
            }
            return convertToWSMetrics(metrics);
        } else {
            db.startDBSession();
            List<?> metrics = dbWrapper.getMetricsByResourcesIds(request);
            db.commitDBSession();
            return convertToWSMetrics(metrics);
        }
    }
    
    @SuppressWarnings("unchecked")
    public WSResultEntry[] getMetricsResult(String userName, String password,
            WSMetricsResultRequest resultRequest) {
        logger.info("Get metrics result! user: " + userName +
                "; request: " + resultRequest.toString());
        
        securityWrapper.checkMetricsReadAccess(userName, password, resultRequest.getMnemonics());
        
        super.updateUserActivity(userName);
        
        WSResultEntry[] result = null;
        List<WSResultEntry> resultList = null;
        db.startDBSession();
        DAObject daObject = dbWrapper.getMetricsResultDAObject(resultRequest);
        if (daObject != null) {
            List<Metric> metrics = (List<Metric>) dbWrapper.getMetricsResultMetricsList(resultRequest);
            resultList = getMetricsResult(metrics, daObject);
        }
        if ((resultList != null) && (resultList.size() != 0)) {
            result = new WSResultEntry[resultList.size()];
            resultList.toArray(result);
        }
        db.commitDBSession();
        return result;
    }
    
    private List<WSResultEntry> getMetricsResult(List<Metric> metrics, DAObject daObject) {
        if ((metrics == null) || (metrics.size() == 0) || (daObject == null)) {
            return null;
        }
        List<WSResultEntry> resultList = new ArrayList<WSResultEntry>();
        Hashtable<AlitheiaPlugin, List<Metric>> plugins = groupMetricsByPlugins(metrics);
        AlitheiaPlugin currentPlugin;
        Result currentResult = null;
        List<Metric> currentPluginMetrics;
        for (Enumeration<AlitheiaPlugin> keys = plugins.keys(); keys.hasMoreElements(); /*empty*/) {
            currentPlugin = keys.nextElement();
            currentPluginMetrics = plugins.get(currentPlugin);
            try {
                currentResult = currentPlugin.getResult(daObject, currentPluginMetrics);
            } catch (MetricMismatchException e) {
                currentResult = null;
            }
        }
        if (currentResult != null) {
            List<ResultEntry> currentRow;
            for (int i = 0; i < currentResult.getRowCount(); i++) {
                currentRow = currentResult.getRow(i);
                for (int j = 0; j < currentRow.size(); j++) {
                    resultList.add(createWSResultEntry(currentRow.get(j)));
                }
            }
        }
        return resultList;
    }
    
    private Hashtable<AlitheiaPlugin, List<Metric>>  groupMetricsByPlugins(List<Metric> metrics) {
        Hashtable<AlitheiaPlugin, List<Metric>> plugins =
            new Hashtable<AlitheiaPlugin, List<Metric>>();
        if ((metrics != null) && (metrics.size() != 0)) {
            AlitheiaPlugin currentPlugin = null;
            for (Metric metric : metrics) {
                currentPlugin = pluginAdmin.getImplementingPlugin(
                        metric.getMnemonic());
                if (currentPlugin != null) {
                    if (plugins.containsKey(currentPlugin)) {
                        plugins.get(currentPlugin).add(metric); 
                    } else {
                        List<Metric> metricList = new ArrayList<Metric>(1);
                        metricList.add(metric);
                        plugins.put(currentPlugin, metricList);
                    }
                }
            }
        }
        return plugins;
    }
    
    private List<Metric> getMetrics(List<PluginInfo> pluginInfos) {
        List<Metric> result = new ArrayList<Metric>();
        AlitheiaPlugin currentPlugin;
        for (PluginInfo pluginInfo : pluginInfos) {
            currentPlugin = pluginAdmin.getPlugin(pluginInfo);
            if (currentPlugin != null) {
                result.addAll(currentPlugin.getSupportedMetrics());
            }
        }
        return result;
    }
    
    private WSMetric[] convertToWSMetrics(List<?> metrics) {
        WSMetric[] result = null;
        if ((metrics != null) && (!metrics.isEmpty())) {
            result = new WSMetric[metrics.size()];
            for (int i = 0; i < result.length; i++) {
                result[i] = createWSMetric((Metric) metrics.get(i));
            }
        }
        return result;
    }
    
    private WSMetricType[] convertToWSMetricTypes(List<?> metricTypes) {
        WSMetricType[] result = null;
        if ((metricTypes != null) && (!metricTypes.isEmpty())) {
            result = new WSMetricType[metricTypes.size()];
            for (int i = 0; i < result.length; i++) {
                result[i] = createWSMetricType((MetricType) metricTypes.get(i));
            }
        }
        return result;
    }
    
    private static WSMetric createWSMetric(Metric metric) {
        if (metric == null) return null;
        WSMetric wsMetric = new WSMetric();
        wsMetric.setId(metric.getId());
        wsMetric.setDescription(metric.getDescription());
        wsMetric.setMetricTypeId(metric.getMetricType().getId());
        wsMetric.setMnemonic(metric.getMnemonic());
        wsMetric.setPluginId(metric.getPlugin().getId());
        return wsMetric;
    }
    
    private static WSMetricType createWSMetricType(MetricType metricType) {
        if (metricType == null) return null;
        WSMetricType wsMetricType = new WSMetricType();
        wsMetricType.setId(metricType.getId());
        wsMetricType.setType(metricType.getType());
        return wsMetricType; 
    }
    
    private static WSResultEntry createWSResultEntry(ResultEntry resultEntry) {
        if (resultEntry == null) return null;
        WSResultEntry wsResultEntry = new WSResultEntry();
        wsResultEntry.setMimeType(resultEntry.getMimeType());
        wsResultEntry.setMnemonic(resultEntry.getMnemonic());
        wsResultEntry.setResult(resultEntry.toString());
        return wsResultEntry;
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
