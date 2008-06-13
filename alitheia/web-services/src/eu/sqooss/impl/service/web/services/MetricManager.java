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
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import eu.sqooss.impl.service.web.services.datatypes.WSMetric;
import eu.sqooss.impl.service.web.services.datatypes.WSMetricType;
import eu.sqooss.impl.service.web.services.datatypes.WSMetricsRequest;
import eu.sqooss.impl.service.web.services.datatypes.WSMetricsResultRequest;
import eu.sqooss.impl.service.web.services.datatypes.WSResultEntry;
import eu.sqooss.impl.service.web.services.datatypes.WSStoredProject;
import eu.sqooss.impl.service.web.services.datatypes.WSProjectVersion;
import eu.sqooss.impl.service.web.services.utils.MetricManagerDatabase;
import eu.sqooss.impl.service.web.services.utils.MetricSecurityWrapper;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.abstractmetric.MetricMismatchException;
import eu.sqooss.service.abstractmetric.Result;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.EvaluationMark;
import eu.sqooss.service.db.FileGroup;
import eu.sqooss.service.db.Metric;
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
    private MetricSecurityWrapper securityWrapper;

    public MetricManager(Logger logger, DBService db,
            PluginAdmin pluginAdmin, SecurityManager security) {
        super(db);
        this.logger = logger;
        this.pluginAdmin = pluginAdmin;
        this.dbWrapper = new MetricManagerDatabase(db);
        this.securityWrapper = new MetricSecurityWrapper(security, db);
    }

    /**
     * @see eu.sqooss.service.web.services.WebServices#getProjectEvaluatedMetrics(String, String, long)
     */
    public WSMetric[] getProjectEvaluatedMetrics(String userName,
            String password, long projectId) {

        logger.info("Retrieve metrics for selected project! user: " + userName +
                "; project id:" + projectId);

        db.startDBSession();

        try {
            securityWrapper.checkDBProjectsReadAccess(userName, password, new long[] {projectId}, null);
        } catch (SecurityException se) {
            db.commitDBSession();
            throw se;
        }

        super.updateUserActivity(userName);

        WSMetric[] result = dbWrapper.getProjectEvaluatedMetrics(projectId);

        db.commitDBSession();

        return (WSMetric[]) normalizeWSArrayResult(result);
    }

    /**
     * @see eu.sqooss.service.web.services.WebServices#getMetricTypesByIds(String, String, long[])
     */
    public WSMetricType[] getMetricTypesByIds(String userName,
            String password, long[] metricTypesIds) {

        logger.info("Retrieve metric types with given ids! user: " + userName +
                "; metric types ids: " + Arrays.toString(metricTypesIds));

        db.startDBSession();

        try {
            securityWrapper.checkDBReadAccess(userName, password);
        } catch (SecurityException se) {
            db.commitDBSession();
            throw se;
        }

        super.updateUserActivity(userName);

        if (metricTypesIds == null) {
            return null;
        }

        WSMetricType[] result = dbWrapper.getMetricTypesByIds(
                asCollection(metricTypesIds));

        db.commitDBSession();

        return (WSMetricType[]) normalizeWSArrayResult(result);
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

        db.startDBSession();

        try {
            securityWrapper.checkDBMetricsReadAccess(userName, password, null);
        } catch (SecurityException se) {
            db.commitDBSession();
            throw se;
        }

        super.updateUserActivity(userName);

        WSMetric[] result;
        boolean isProjectFile = request.getIsProjectFile();
        boolean isProjectVersion = request.getIsProjectVersion();
        boolean isStoredProject = request.getIsStoredProject();
        boolean isFileGroup = request.getIsFileGroup();
        if (request.getSkipResourcesIds()) {
            List<Metric> metrics = new ArrayList<Metric>();
            if (isFileGroup) {
                metrics.addAll(getMetrics(pluginAdmin.
                        listPluginProviders(FileGroup.class)));
            }
            if (isProjectFile) {
                metrics.addAll(getMetrics(pluginAdmin.
                        listPluginProviders(ProjectFile.class)));
            }
            if (isProjectVersion) {
                metrics.addAll(getMetrics(pluginAdmin.
                        listPluginProviders(ProjectVersion.class)));
            }
            if (isStoredProject) {
                metrics.addAll(getMetrics(pluginAdmin.
                        listPluginProviders(StoredProject.class)));
            }
            result = WSMetric.asArray(metrics);
        } else {
            Collection<Long> ids = asCollection(request.getResourcesIds());
            result = dbWrapper.getMetricsByResourcesIds(ids, isProjectFile,
                    isProjectVersion, isStoredProject, isFileGroup);
        }

        db.commitDBSession();

        return (WSMetric[]) normalizeWSArrayResult(result);
    }

    public WSResultEntry[] getMeasurements(String userName, String password,
        WSProjectVersion wsv) {
        WSResultEntry[] result = null;
        db.startDBSession();
        // First, get a stored project for this project version
        ProjectVersion v = db.findObjectById(ProjectVersion.class, wsv.getId());
        StoredProject p = v.getProject();
        // Inform logger
        logger.info("Retrieving measurements for project " +
            p.getName() + " " + v.getVersion());
        // Now loop over the metrics that have evaluated this project
        Set<EvaluationMark> marks = p.getEvaluationMarks();
        for (EvaluationMark mark : marks) {
            Metric metric = mark.getMetric();
            AlitheiaPlugin plugin = pluginAdmin.getImplementingPlugin(metric.getMnemonic());
            List<Metric> l = new ArrayList<Metric>();
            l.add(metric);
            try {
                Result r = plugin.getResult(v,l);
                logger.info("Got measurement from " + metric.getMnemonic() + " with " +
                    r.getRowCount());
            } catch (MetricMismatchException e) {
                // So that metric doesn't support project version metrics
                // Ignore it.
            }
        }
        return (WSResultEntry[]) normalizeWSArrayResult(result);
    }

    @SuppressWarnings("unchecked")
    public WSResultEntry[] getMetricsResult(String userName, String password,
            WSMetricsResultRequest resultRequest) {
        logger.info("Get metrics result! user: " + userName +
                "; request: " + resultRequest.toString());

        db.startDBSession();

        try {
            securityWrapper.checkDBMetricsReadAccess(userName, password, resultRequest.getMnemonics());
        } catch (SecurityException se) {
            db.commitDBSession();
            throw se;
        }

        super.updateUserActivity(userName);

        WSResultEntry[] result = null;
        List<WSResultEntry> resultsList = new ArrayList<WSResultEntry>();
        if (resultRequest.getDaObjectId() != null) {
            for (long nextDaoId : resultRequest.getDaObjectId()) {
                // Find the DAO with this Id
                DAObject nextDao = dbWrapper.getMetricsResultDAObject(
                        resultRequest, nextDaoId);
                // Stored the metric evaluation on this DAO
                List<WSResultEntry> nextDaoResults = null;
                if (nextDao != null) {
                    List<Metric> metrics =
                        (List<Metric>) dbWrapper.getMetricsResultMetricsList(
                                resultRequest);
                    nextDaoResults = getMetricsResult(metrics, nextDao);
                }
                if ((nextDaoResults != null) && (nextDaoResults.size() != 0)) {
                    for (WSResultEntry nextResult : nextDaoResults) {
                        nextResult.setDaoId(nextDaoId);
                        resultsList.add(nextResult);
                    }
                }
            }
        }
        
        if (resultsList.size() > 0) {
            result = new WSResultEntry[resultsList.size()];
            resultsList.toArray(result);
        }
        db.commitDBSession();
        return (WSResultEntry[]) normalizeWSArrayResult(result);
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
                currentResult = currentPlugin.getResultIfAlreadyCalculated(daObject, currentPluginMetrics);
            } catch (MetricMismatchException e) {
                currentResult = null;
            }
        }
        if (currentResult != null) {
            List<ResultEntry> currentRow;
            for (int i = 0; i < currentResult.getRowCount(); i++) {
                currentRow = currentResult.getRow(i);
                for (int j = 0; j < currentRow.size(); j++) {
                    resultList.add(WSResultEntry.getInstance(currentRow.get(j)));
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

}

//vi: ai nosi sw=4 ts=4 expandtab
