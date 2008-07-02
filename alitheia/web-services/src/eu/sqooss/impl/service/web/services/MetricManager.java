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

import eu.sqooss.impl.service.web.services.datatypes.WSMetric;
import eu.sqooss.impl.service.web.services.datatypes.WSMetricType;
import eu.sqooss.impl.service.web.services.datatypes.WSMetricsRequest;
import eu.sqooss.impl.service.web.services.datatypes.WSMetricsResultRequest;
import eu.sqooss.impl.service.web.services.datatypes.WSResultEntry;
import eu.sqooss.impl.service.web.services.utils.MetricManagerDatabase;
import eu.sqooss.impl.service.web.services.utils.MetricSecurityWrapper;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.abstractmetric.MetricMismatchException;
import eu.sqooss.service.abstractmetric.Result;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
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
        this.securityWrapper = new MetricSecurityWrapper(security, db, logger);
    }

    /**
     * @see eu.sqooss.service.web.services.WebServices#getProjectEvaluatedMetrics(String, String, long)
     */
    public WSMetric[] getProjectEvaluatedMetrics(String userName,
            String password, long projectId) {

        logger.info("Retrieve metrics for selected project! user: " + userName +
                "; project id:" + projectId);

        db.startDBSession();

        if (!securityWrapper.checkProjectsReadAccess(userName,
                password, new long[] {projectId})) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException("Security violation in the get project evaluated metrics operation!");
        }

        super.updateUserActivity(userName);

        WSMetric[] result = dbWrapper.getProjectEvaluatedMetrics(projectId);
        // Attach the activation type for each of the retrieved metric
        attachActivators(result);

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

        if (!securityWrapper.checkMetricTypesReadAccess(userName, password, metricTypesIds)) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    "Security violation in the get metric types by ids operation!");
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

        if (!securityWrapper.checkMetricsReadAccess(userName, password)) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    "Security violation in the get metrics by resources ids!");
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
        // Attach the activation type for each of the retrieved metric
        attachActivators(result);

        db.commitDBSession();

        return (WSMetric[]) normalizeWSArrayResult(result);
    }

    /**
     * Retrieves the evaluation results of the specified metrics on the
     * selected project resources.
     * 
     * @param userName the user's name used for authentication
     * @param password the user's password used for authentication
     * @param resultRequest the result request object
     * 
     * @return The array of metrics results, or <code>null</code> when the
     *   specified metrics were not evaluated on the given resources.
     */
    @SuppressWarnings("unchecked")
    public WSResultEntry[] getMetricsResult(
            String userName,
            String password,
            WSMetricsResultRequest resultRequest) {
        // Log this call
        logger.info("Get metrics result!"
                + " user: " + userName +
                ";"
                + " request: " + resultRequest.toString());
        // Match against the current security policy
        db.startDBSession();
        if (!securityWrapper.checkMetricsReadAccess(userName, password)) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException(
                    "Security violation: Get metrics results has been denied!");
        }
        super.updateUserActivity(userName);
        // Retrieve the result(s)
        WSResultEntry[] result = null;
        List<WSResultEntry> resultsList = new ArrayList<WSResultEntry>();
        if ((resultRequest.getMnemonics() != null)
                && (resultRequest.getDaObjectId() != null)) {
            // Retrieve the required metric DAOs and sort them by plug-in DAO
            Hashtable<AlitheiaPlugin, List<Metric>> plugins =
                groupMetricsByPlugins(resultRequest.getMnemonics());
            // Retrieve the evaluation results for each of the resource DAOs
            for (long nextDaoId : resultRequest.getDaObjectId()) {
                // Extract the DAO with this Id
                DAObject nextDao = dbWrapper.getMetricsResultDAObject(
                        resultRequest, nextDaoId);
                // Retrieve the metric evaluations for this DAO
                List<WSResultEntry> nextDaoResults =
                    getMetricsResult(plugins, nextDao);
                if (nextDaoResults.size() > 0) {
                    for (WSResultEntry nextResult : nextDaoResults) {
                        nextResult.setDaoId(nextDaoId);
                        resultsList.add(nextResult);
                    }
                }
            }
        }
        // Convert the result list into an array
        if (resultsList.size() > 0) {
            result = new WSResultEntry[resultsList.size()];
            resultsList.toArray(result);
        }
        db.commitDBSession();
        return (WSResultEntry[]) normalizeWSArrayResult(result);
    }

    /**
     * Retrieve the results of one or more metric that were evaluated on
     * the specified resource DAO.
     * 
     * @param plugins the plug-in to metric hash table
     * @param dao the resource DAO
     * 
     * @return The list of metrics results, or an empty list when these
     *   metrics were not evaluated for on this resource DAO.
     */
    private List<WSResultEntry> getMetricsResult(
            Hashtable<AlitheiaPlugin, List<Metric>> plugins, DAObject dao) {
        // Holds the evaluation results for the given resource DAO
        List<WSResultEntry> resultList = new ArrayList<WSResultEntry>();
        if ((plugins == null) || (plugins.size() == 0) || (dao == null))
            return resultList;

        for (AlitheiaPlugin nextPlugin : plugins.keySet()) {
            Result currentResult = null;
            try {
                currentResult = nextPlugin.getResultIfAlreadyCalculated(
                        dao, plugins.get(nextPlugin));
            }
            catch (MetricMismatchException e) {
                currentResult = null;
            }
            if (currentResult != null) {
                while (currentResult.hasNext())
                    for (ResultEntry nextEntry: currentResult.next())
                        resultList.add(WSResultEntry.getInstance(nextEntry));
            }
        }
        return resultList;
    }

    /**
     * Assembles a plug-in DAOs to metric DAOs hash table, based on the
     * provided list of metric mnemonic names.
     * 
     * @param mnemonics the list of mnemonic names
     * 
     * @return The plug-in to metric hash table, or an empty map when the
     *   given mnemonics list is empty or no metrics correspond to these
     *   mnemonics.
     */
    private Hashtable<AlitheiaPlugin, List<Metric>> groupMetricsByPlugins(
            String[] mnemonics) {
        // Holds the plug-ins to metrics mapping
        Hashtable<AlitheiaPlugin, List<Metric>> plugins =
            new Hashtable<AlitheiaPlugin, List<Metric>>();
        
        if ((mnemonics != null) && (mnemonics.length > 0)) {
            AlitheiaPlugin currentPlugin = null;
            for (String mnemonic : mnemonics) {
                currentPlugin = pluginAdmin.getImplementingPlugin(mnemonic);
                if (currentPlugin != null) {
                    if (plugins.containsKey(currentPlugin)) {
                        plugins.get(currentPlugin).add(
                                Metric.getMetricByMnemonic(mnemonic));
                    }
                    else {
                        List<Metric> metricList = new ArrayList<Metric>(1);
                        metricList.add(Metric.getMetricByMnemonic(mnemonic));
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

    /**
     * This method will return the list of all metrics that are currently
     * registered in the SQO-OSS framework.
     * 
     * @param userName - the user's name used for authentication
     * @param password - the user's password used for authentication
     * 
     * @return The array with all metrics, or a <code>null<code> when none
     *   are found.
     */
    public WSMetric[] getAllMetrics(String userName, String password) {
        // Log this call
        logger.info("Get all metrics!"
                + " user: " + userName);
        // Match against the current security policy
        db.startDBSession();
        if (!securityWrapper.checkMetricsReadAccess(userName, password)) {
            if (db.isDBSessionActive())
                db.commitDBSession();
            throw new SecurityException(
                    "Security violation: Get all metrics is denied!");
        }
        super.updateUserActivity(userName);
        // Retrieve the result(s)
        WSMetric[] result = null;
        List<Metric> metrics = db.findObjectsByProperties(
                Metric.class, new Hashtable<String, Object>());
        if ((metrics != null) && (metrics.size() > 0)) {
            result = new WSMetric[metrics.size()];
            int index = 0;
            for (Metric nextMetric : metrics)
                result[index++] = WSMetric.getInstance(nextMetric);
        }
        // Attach the activation type for each of the retrieved metric
        attachActivators(result);

        db.commitDBSession();
        return result;
    }

    /**
     * Attaches the associated activation type to each metric that is
     * specified in the given array.
     * 
     * @param metrics the array of metric object
     */
    private void attachActivators (WSMetric[] metrics) {
        if ((metrics != null) && (metrics.length > 0)) {
            for (WSMetric metric : metrics) {
                try {
                    AlitheiaPlugin plugin = pluginAdmin.getImplementingPlugin(
                            metric.getMnemonic());
                    metric.setActivator(plugin.getMetricActivationType(
                            Metric.getMetricByMnemonic(
                                    metric.getMnemonic())).getSimpleName());
                }
                catch (NullPointerException ex) { /* Do nothing */ }
            }
        }
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
