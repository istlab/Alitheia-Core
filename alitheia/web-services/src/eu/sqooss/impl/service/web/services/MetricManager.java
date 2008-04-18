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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.sqooss.impl.service.web.services.datatypes.WSMetric;
import eu.sqooss.impl.service.web.services.datatypes.WSMetricMeasurement;
import eu.sqooss.impl.service.web.services.utils.MetricManagerDatabase;
import eu.sqooss.impl.service.web.services.utils.SecurityWrapper;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.security.SecurityManager;

public class MetricManager {
    
    private Logger logger;
    private MetricManagerDatabase dbWrapper;
    private SecurityWrapper securityWrapper;
    
    public MetricManager(Logger logger, DBService db, SecurityManager security) {
        this.logger = logger;
        this.dbWrapper = new MetricManagerDatabase(db);
        this.securityWrapper = new SecurityWrapper(security);
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#retrieveMetrics4SelectedProject(String, String, String)
     */
    public WSMetric[] retrieveMetrics4SelectedProject(String userName,
            String password, long projectId) {
        
        logger.info("Retrieve metrics for selected project! user: " + userName +
                "; project id:" + projectId);
        
        securityWrapper.checkProjectReadAccess(userName, password, projectId);
        
        List<?> metrics = dbWrapper.retrieveMetrics4SelectedProject(projectId);
        return convertToWSMetrics(metrics);
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#retrieveSelectedMetric(String, String, String, String)
     */
    public WSMetric retrieveSelectedMetric(String userName, String password,
            long projectId, long metricId) {
        
        logger.info("Retrieve selected metric! user: " + userName +
                "; project id: " + projectId + "; metricId: " + metricId);
        
        securityWrapper.checkProjectMetricReadAccess(userName, password, projectId, metricId);
        
        List<?> queryResult = dbWrapper.retrieveSelectedMetric(projectId, metricId);
        
        if (queryResult.size() != 0) {
            return new WSMetric((Metric) queryResult.get(0));
        } else {
            return null;
        }
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#retrieveMetrics4SelectedFiles(String, String, String, String[], String[])
     */
    public WSMetric[] retrieveMetrics4SelectedFiles(String userName, String password,
            long projectId, String[] folders, String[] fileNames) {
        logger.info("Retrieve metrics for selected files! user: " + userName + "; project id: " + projectId);

        securityWrapper.checkProjectReadAccess(userName, password, projectId);

        Set<String> fileNamesSet;
        if ((fileNames.length == 0) || (fileNames[0] == null)) {
            fileNamesSet = new HashSet<String>();
        } else {
            fileNamesSet = new HashSet<String>(Arrays.asList(fileNames));
        }
        
        if ((folders.length != 0) && (folders[0] != null)) {
            Map<String, Object> folderNameParameters = new Hashtable<String, Object>(1);
            List currentFileNames;
            for (String folder : folders) {
                currentFileNames = dbWrapper.getFilesFromFolder(projectId, folder);
                fileNamesSet.addAll(currentFileNames);
            }
        }
        
        List<?> result = null;
        
        if (fileNamesSet.size() != 0) {
            result = dbWrapper.retrieveMetrics4SelectedFiles(projectId, fileNamesSet);
        }
        
        return convertToWSMetrics(result);
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getMetrics(String, String)
     */
    public WSMetric[] getMetrics(String userName, String password) {
        logger.info("Get metrics! user: " + userName);
        
        //TODO: check the security
        
        return convertToWSMetrics(dbWrapper.getMetrics());
    }
    
    public WSMetricMeasurement[] getProjectFileMetricMeasurement(String userName, String password,
            long metricId, long projectFileId) {
        
        logger.info("Get project file metric measurement! user: " + userName +
                "; metric id: " + metricId + "; project file id: " + projectFileId);
        
        securityWrapper.checkMetricReadAccess(userName, password, metricId);
        
        return convertToWSMetricMeasurements(
                dbWrapper.getProjectFileMetricMeasurement(metricId, projectFileId));
    }
    
    public WSMetricMeasurement[] getProjectVersionMetricMeasurement(String userName, String password,
            long metricId, long projectVersionId) {
        
        logger.info("Get project version metric measurement! user: " + userName +
                "; metric is: " + metricId + "; project version id: " + projectVersionId);
        
        securityWrapper.checkMetricReadAccess(userName, password, metricId);
        
    	return convertToWSMetricMeasurements(
    	        dbWrapper.getProjectVersionMetricMeasurement(metricId, projectVersionId));
    }
    
    private WSMetric[] convertToWSMetrics(List<?> metrics) {
        WSMetric[] result = null;
        if ((metrics != null) && (metrics.size() != 0)) {
            result = new WSMetric[metrics.size()];
            for (int i = 0; i < result.length; i++) {
                result[i] = new WSMetric((Metric) metrics.get(i));
            }
        }
        return result;
    }
    
    private WSMetricMeasurement[] convertToWSMetricMeasurements(List<?> measurements) {
        WSMetricMeasurement[] result = null;
        if ((measurements != null) && (measurements.size() != 0)) {
            result = new WSMetricMeasurement[measurements.size()];
            Object currentElem;
            for (int i = 0; i < result.length; i++) {
                currentElem = measurements.get(i);
                result[i] = WSMetricMeasurement.createInstance(currentElem);
            }
        }
        return result;
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
