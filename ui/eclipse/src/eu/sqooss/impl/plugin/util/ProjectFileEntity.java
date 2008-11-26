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

package eu.sqooss.impl.plugin.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.sqooss.plugin.util.Entity;
import eu.sqooss.scl.WSException;
import eu.sqooss.scl.WSSession;
import eu.sqooss.scl.accessor.WSAccessor;
import eu.sqooss.scl.accessor.WSMetricAccessor;
import eu.sqooss.scl.accessor.WSProjectAccessor;
import eu.sqooss.scl.accessor.WSUserAccessor;
import eu.sqooss.ws.client.datatypes.WSConstants;
import eu.sqooss.ws.client.datatypes.WSFileModification;
import eu.sqooss.ws.client.datatypes.WSMetric;
import eu.sqooss.ws.client.datatypes.WSMetricType;
import eu.sqooss.ws.client.datatypes.WSMetricsRequest;
import eu.sqooss.ws.client.datatypes.WSMetricsResultRequest;
import eu.sqooss.ws.client.datatypes.WSProjectFile;
import eu.sqooss.ws.client.datatypes.WSProjectVersion;
import eu.sqooss.ws.client.datatypes.WSResultEntry;

/**
 * The class represents the project file or directory of the eclipse project.
 */
public class ProjectFileEntity implements Entity {
    
    private static WSConstants wsConstants;
    
    private WSProjectVersion projectVersion;
    private WSProjectFile projectFile;
    private WSSession session;
    private WSMetric[] metrics;
    private MetricResultHashtable resultEntries;
    private Map<Long, WSFileModification> fileModifications;
    private Long[] sortedVersions;
    private WSFileModification currentFileModification;
    
    public ProjectFileEntity(WSProjectVersion projectVersion,
            WSProjectFile projectFile, WSSession session) {
        this.projectVersion = projectVersion;
        this.projectFile = projectFile;
        this.session = session;
        resultEntries = new MetricResultHashtable();
    }

    /**
     * @see eu.sqooss.plugin.util.Entity#getMetrics()
     */
    public WSMetric[] getMetrics() {
        if (metrics == null) {
            initMetrics();
        }
        return metrics;
    }

    /**
     * @see eu.sqooss.plugin.util.Entity#getName()
     */
    public String getName() {
        return projectFile.getFileName();
    }

    /**
     * @see eu.sqooss.plugin.util.Entity#getVersions(boolean))
     */
    public Long[] getSortedVersions() {
        if (this.fileModifications == null) {
            initVersions((WSProjectAccessor) session.getAccessor(
                    WSAccessor.Type.PROJECT));
        }
        if (this.fileModifications == null)
            return null;
        else
            return sortedVersions;
    }

    public Long getCurrentVersion() {
        return Long.valueOf(currentFileModification.getProjectVersionNum());
    }
    
    public WSResultEntry[] getMetricsResults(WSMetric[] metrics, Long[] versions) {
        List<WSResultEntry> result = new ArrayList<WSResultEntry>();
        List<WSMetric> missingMetrics = new ArrayList<WSMetric>();
        WSResultEntry currentResultEntry;
        for (WSMetric metric : metrics) {
            for (Long version : versions) {
                currentResultEntry = this.resultEntries.get(version, metric.getMnemonic());
                if (currentResultEntry == null) {
                    missingMetrics.add(metric);
                } else {
                    result.add(currentResultEntry);
                }
            }
        }
        if (!missingMetrics.isEmpty()) {
            initResultEntries((WSMetricAccessor) session.getAccessor(
                    WSAccessor.Type.METRIC), metrics, versions);
            for (WSMetric metric : missingMetrics) {
                for (Long version : versions) {
                    currentResultEntry = this.resultEntries.get(version, metric.getMnemonic());
                    if (currentResultEntry != null) {
                        result.add(currentResultEntry);
                    }
                }
            }
        }
        WSResultEntry[] arrayResult = new WSResultEntry[result.size()];
        return result.toArray(arrayResult);
    }
    
    public long getVersionById(long id) {
        if (id == this.currentFileModification.getProjectFileId()) {
            return new Long(this.currentFileModification.getProjectVersionNum()).longValue();
        }
        Iterator<Long> keys = this.fileModifications.keySet().iterator();
        Long currentKey;
        while (keys.hasNext()) {
            currentKey = keys.next();
            if (id == this.fileModifications.get(currentKey).getProjectFileId()) {
                return new Long(this.fileModifications.get(currentKey).getProjectVersionNum()).longValue();
            }
        }
        throw new IllegalArgumentException("The version is not valid!");
    }

    private void initResultEntries(WSMetricAccessor metricAccessor,
            WSMetric[] metrics, Long[] versions) {
        WSMetricsResultRequest resultRequest = new WSMetricsResultRequest();
        Hashtable<Long, Long> daoVersions = new Hashtable<Long, Long>();
        long[] daoIds = new long[versions.length];
        for (int i = 0; i < versions.length; i++) {
            long verRevision = new Long(
                    currentFileModification.getProjectVersionNum());
            if (verRevision == versions[i].longValue()) {
                daoIds[i] = currentFileModification.getProjectFileId();
                daoVersions.put(Long.valueOf(currentFileModification.getProjectFileId()),
                        Long.valueOf(currentFileModification.getProjectVersionNum()));
                        
            } else {
                WSFileModification fileModif = fileModifications.get(versions[i]);
                daoIds[i] = fileModif.getProjectFileId();
                daoVersions.put(Long.valueOf(fileModif.getProjectFileId()),
                        Long.valueOf(fileModif.getProjectVersionNum()));
            }
        }
        resultRequest.setDaObjectId(daoIds);
        resultRequest.setProjectFile(true);
        String[] metricsMnemonics = new String[metrics.length];
        for (int i = 0; i < metricsMnemonics.length; i++) {
            metricsMnemonics[i] = metrics[i].getMnemonic();
        }
        resultRequest.setMnemonics(metricsMnemonics);
        try {
            WSResultEntry[] resultEntries = metricAccessor.getMetricsResult(resultRequest);
            for (WSResultEntry entry : resultEntries) {
                this.resultEntries.put(daoVersions.get(entry.getDaoId()), entry);
            }
        } catch (WSException wse) {
            //nothing to do here
        }
    }
    
    private void initMetrics() {
        WSMetricsRequest request = new WSMetricsRequest();
        request.setIsProjectFile(true);
        request.setResourcesIds(new long[] {projectFile.getId()});
        try {
            if (ProjectFileEntity.wsConstants == null) {
                ProjectFileEntity.wsConstants =
                    ((WSUserAccessor) session.getAccessor(WSAccessor.Type.USER)).getConstants();
            }
            WSMetric[] metrics = ((WSMetricAccessor) session.getAccessor(WSAccessor.Type.METRIC)).
            getMetricsByResourcesIds(request);
            this.metrics = filterMetrics(metrics);
        } catch (WSException wse) {
            this.metrics = null;
        }
    }
    
    private WSMetric[] filterMetrics(WSMetric[] metrics) {
        if ((ProjectFileEntity.wsConstants == null) ||
                (metrics == null) || (metrics.length == 0)) return null;
        long[] metricTypesIds = new long[metrics.length];
        for (int i = 0; i < metricTypesIds.length; i++) {
            metricTypesIds[i] = metrics[i].getMetricTypeId();
        }
        WSMetricType[] metricTypes;
        try {
            metricTypes = 
                ((WSMetricAccessor) session.getAccessor(WSAccessor.Type.METRIC)).
                getMetricTypesByIds(metricTypesIds);
        } catch (WSException wse) {
            metricTypes = null;
        }
        if ((metricTypes == null) ||
                (metricTypes.length == 0)) return null;
        Hashtable<Long, WSMetricType> metricTypesHash = new Hashtable<Long, WSMetricType>();
        for (WSMetricType metricType : metricTypes) {
            metricTypesHash.put(Long.valueOf(metricType.getId()), metricType);
        }
        List<WSMetric> result = new ArrayList<WSMetric>();
        WSMetricType currentMetricType = null;
        Long currentKey;
        for (WSMetric currentMetric : metrics) {
            currentKey = Long.valueOf(currentMetric.getMetricTypeId());
            if (metricTypesHash.containsKey(currentKey)) {
                currentMetricType = metricTypesHash.get(currentKey);
            } else {
                continue;
            }
            if ((projectFile.getDirectory() &&
                    (currentMetricType.getType().equals(ProjectFileEntity.wsConstants.getMetricTypeSourceFolder()))) ||
                (!projectFile.getDirectory() &&
                    (currentMetricType.getType().equals(ProjectFileEntity.wsConstants.getMetricTypeSourceCode())))){
                result.add(currentMetric);
            }
        }
        WSMetric[] arrayResult = new WSMetric[result.size()];
        return result.toArray(arrayResult);
    }

    private void initVersions(WSProjectAccessor projectAccessor) {
        // Retrieve the list of modifications performed on this file
        WSFileModification[] modifications;
        WSProjectVersion fileVersion = null;
        try {
            modifications = projectAccessor.getFileModifications(
                    projectVersion.getId(), projectFile.getId());
            WSProjectVersion[] fileVersions =
                projectAccessor.getProjectVersionsByIds(
                        new long[] {projectFile.getProjectVersionId()});
            if ((fileVersions != null) && (fileVersions.length == 1))
                fileVersion = fileVersions[0];
        } catch (WSException wse) {
            modifications = null;
            fileVersion = null;
        }

        if ((modifications == null) || (fileVersion == null)){
            this.fileModifications = null;
        } else {
            fileModifications = new Hashtable<Long, WSFileModification>();
            for (WSFileModification modification : modifications) {
                String modVersion = modification.getProjectVersionNum();
                if (!modVersion.equals(fileVersion.getVersion())) {
                    fileModifications.put(Long.valueOf(modVersion),modification);
                } else {
                    this.currentFileModification = modification;
                }
            }
            Set<Long> keySet = this.fileModifications.keySet();
            sortedVersions = this.fileModifications.keySet().toArray(new Long[keySet.size()]);
            Arrays.sort(sortedVersions);
        }
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
