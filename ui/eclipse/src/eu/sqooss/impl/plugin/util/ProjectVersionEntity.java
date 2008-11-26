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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import eu.sqooss.plugin.util.Entity;
import eu.sqooss.scl.WSException;
import eu.sqooss.scl.WSSession;
import eu.sqooss.scl.accessor.WSAccessor;
import eu.sqooss.scl.accessor.WSMetricAccessor;
import eu.sqooss.scl.accessor.WSProjectAccessor;
import eu.sqooss.ws.client.datatypes.WSMetric;
import eu.sqooss.ws.client.datatypes.WSMetricsRequest;
import eu.sqooss.ws.client.datatypes.WSMetricsResultRequest;
import eu.sqooss.ws.client.datatypes.WSProjectVersion;
import eu.sqooss.ws.client.datatypes.WSResultEntry;
import eu.sqooss.ws.client.datatypes.WSStoredProject;

/**
 * The class represents the project.
 */
public class ProjectVersionEntity implements Entity {

    private WSStoredProject storedProject;
    private long[] projectId;
    private WSProjectVersion projectVersion;
    private WSProjectAccessor projectAccessor;
    private WSMetricAccessor metricAccessor;
    private WSMetric[] metrics;
    private SortedMap<Long, WSProjectVersion> versions;
    private WSProjectVersion firstVersion;
    private WSProjectVersion lastVersion;
    private MetricResultHashtable resultEntries;

    public ProjectVersionEntity(WSStoredProject storedProject,
            WSProjectVersion projectVersion, WSSession session) {
        this.storedProject = storedProject;
        this.projectId = new long[] {storedProject.getId()};
        this.projectVersion = projectVersion;
        this.projectAccessor = (WSProjectAccessor) session.getAccessor(
                WSAccessor.Type.PROJECT);
        this.metricAccessor = (WSMetricAccessor) session.getAccessor(
                WSAccessor.Type.METRIC);
        resultEntries = new MetricResultHashtable();
    }

    /**
     * @see eu.sqooss.plugin.util.Entity#getMetrics()
     */
    public WSMetric[] getMetrics() {
        initMetrics();
        return metrics;
    }

    /**
     * @see eu.sqooss.plugin.util.Entity#getName()
     */
    public String getName() {
        return this.storedProject.getName()
            + " (ver. " + this.projectVersion.getVersion() + ")";
    }

    /**
     * @see eu.sqooss.plugin.util.Entity#getVersions(boolean))
     */
    public Long[] getSortedVersions() {
        initVersions(null);
        if (this.versions == null)
            return null;
        else
            return this.versions.keySet().toArray(
                    new Long[this.versions.size()]);
    }

    public Long getCurrentVersion() {
        return (new Long(this.projectVersion.getVersion()).longValue());
    }

    public WSResultEntry[] getMetricsResults(WSMetric[] metrics, Long[] versions) {
        List<WSResultEntry> result = new ArrayList<WSResultEntry>();
        List<WSMetric> missingMetrics = new ArrayList<WSMetric>();
        WSResultEntry currentResultEntry;
        for (WSMetric metric : metrics) {
            for (Long version : versions) {
                currentResultEntry = this.resultEntries.get(
                        version, metric.getMnemonic());
                if (currentResultEntry == null) {
                    missingMetrics.add(metric);
                } else {
                    result.add(currentResultEntry);
                }
            }
        }
        if (!missingMetrics.isEmpty()) {
            initResultEntries(metrics, versions);
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
        if (id == this.projectVersion.getId()) {
            return (new Long(this.projectVersion.getVersion()).longValue());
        }
        Iterator<Long> keys = this.versions.keySet().iterator();
        WSProjectVersion currentVersion;
        //checks the local cache
        while (keys.hasNext()) {
            currentVersion = this.versions.get(keys.next());
            if ((currentVersion != null) &&
                    (currentVersion.getId() == id)) {
                return (new Long(currentVersion.getVersion()).longValue());
            }
        }
        //gets from the core
        try {
            WSProjectVersion[] result = projectAccessor.getProjectVersionsByIds(new long[] {id});
            if (result.length == 1) {
                long rev = new Long(result[0].getVersion()).longValue();
                this.versions.put(rev, result[0]);
                return rev;
            }
        } catch (WSException wse) {
            //do nothing
        }
        throw new IllegalArgumentException("The version is not valid!");
    }
    
    private void initResultEntries(WSMetric[] metrics, Long[] versions) {
        WSMetricsResultRequest resultRequest = new WSMetricsResultRequest();
        Hashtable<Long, Long> daoVersions = new Hashtable<Long, Long>();
        long[] daoIds = new long[versions.length];
        String[] revisions = new String[versions.length];
        for (int i = 0; i < versions.length ; i++)
            revisions[i] = versions[i].toString();
        initVersions(revisions);
        WSProjectVersion projectVer;
        for (int i = 0; i < versions.length; i++) {
            long currentScmRevision =
                new Long(projectVersion.getVersion()).longValue();
            if (currentScmRevision == versions[i].longValue()) {
                daoIds[i] = projectVersion.getId();
                daoVersions.put(Long.valueOf(projectVersion.getId()),
                        Long.valueOf(projectVersion.getVersion()));
                        
            } else {
                projectVer = this.versions.get(versions[i]);
                if (projectVer == null) continue;
                daoIds[i] = projectVer.getId();
                daoVersions.put(Long.valueOf(projectVer.getId()),
                        Long.valueOf(projectVer.getVersion()));
            }
        }
        resultRequest.setDaObjectId(daoIds);
        resultRequest.setProjectVersion(true);
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
        if (metrics == null) {
            WSMetricsRequest request = new WSMetricsRequest();
            request.setIsProjectVersion(true);
            request.setResourcesIds(new long[] {projectVersion.getId()});
            try {
                this.metrics = metricAccessor.getMetricsByResourcesIds(request);
            } catch (WSException wse) {
                this.metrics = null;
            }
        }
    }

    private void initVersions(String[] vers) {
        if (this.versions == null) {
            try {
                WSProjectVersion[] mainVersions;
                // Retrieve and store locally the first project version
                mainVersions = projectAccessor.getFirstProjectVersions(projectId);
                if ((mainVersions != null) && (mainVersions.length == 1))
                    this.firstVersion = mainVersions[0];
                // Retrieve and store locally the latest project version
                mainVersions = projectAccessor.getLastProjectVersions(projectId);
                if ((mainVersions != null) && (mainVersions.length == 1))
                    this.lastVersion = mainVersions[0];
            } catch (WSException wse) {
                /* Just ignore */
            }
            // Create placeholders for the versions in between first and latest
            if ((this.firstVersion != null) && (this.lastVersion != null)) {
                this.versions = new TreeMap<Long, WSProjectVersion>();

                long firstScmRev =
                    new Long(firstVersion.getVersion()).longValue();
                long latestScmRev =
                    new Long(lastVersion.getVersion()).longValue();
                long currentScmRevision =
                    new Long(projectVersion.getVersion()).longValue();

                this.versions.put(firstScmRev, firstVersion);
                if (latestScmRev != currentScmRevision) {
                    this.versions.put(latestScmRev, lastVersion);
                }
                for (long ver = firstScmRev + 1; ver < latestScmRev; ver++) {
                    if (ver != currentScmRevision)
                        this.versions.put(ver, null);
                }
            }
        }

        if ((this.versions != null) && (vers != null)) {
            // Sort out all versions which aren't retrieved yet
            List<String> missing = new ArrayList<String>();
            for (String version : vers)
                if (this.versions.get(Long.valueOf(version)) == null)
                    missing.add(version);

            // Pull all missing versions out of SQO-OSS
            if (!missing.isEmpty()) {
                try {
                    WSProjectVersion[] result =
                        projectAccessor.getProjectVersionsByScmIds(
                            storedProject.getId(),
                            missing.toArray(new String[missing.size()]));
                    for (WSProjectVersion version : result) {
                        this.versions.put(
                                Long.valueOf(version.getVersion()),
                                version);
                    }
                } catch (WSException wse) {
                    /* Just ignore */
                }
            }
        }
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
