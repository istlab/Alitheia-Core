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

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import eu.sqooss.plugin.util.Entity;
import eu.sqooss.scl.WSException;
import eu.sqooss.scl.WSSession;
import eu.sqooss.scl.accessor.WSAccessor;
import eu.sqooss.scl.accessor.WSMetricAccessor;
import eu.sqooss.scl.accessor.WSProjectAccessor;
import eu.sqooss.ws.client.datatypes.WSFileModification;
import eu.sqooss.ws.client.datatypes.WSMetric;
import eu.sqooss.ws.client.datatypes.WSMetricsRequest;
import eu.sqooss.ws.client.datatypes.WSProjectFile;
import eu.sqooss.ws.client.datatypes.WSProjectVersion;

/**
 * The class represents the project file or directory of the eclipse project.
 */
public class ProjectFileEntity implements Entity {
    
    private WSProjectVersion projectVersion;
    private WSProjectFile projectFile;
    private WSSession session;
    private WSMetric[] metrics;
    Map<Long, WSFileModification> fileModifications;
    
    public ProjectFileEntity(WSProjectVersion projectVersion,
            WSProjectFile projectFile, WSSession session) {
        this.projectVersion = projectVersion;
        this.projectFile = projectFile;
        this.session = session;
    }

    /**
     * @see eu.sqooss.plugin.util.Entity#getMetrics()
     */
    public WSMetric[] getMetrics() {
        if (metrics == null) {
            init((WSMetricAccessor) session.getAccessor(
                    WSAccessor.Type.METRIC));
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
     * @see eu.sqooss.plugin.util.Entity#getVersions()
     */
    public Long[] getVersions() {
        if (this.fileModifications == null) {
            init((WSProjectAccessor) session.getAccessor(
                    WSAccessor.Type.PROJECT));
        }
        if (this.fileModifications == null) return null;
        Set<Long> keySet = fileModifications.keySet();
        Long[] result = new Long[keySet.size()];
        fileModifications.keySet().toArray(result);
        return result;
    }
    
    private void init(WSMetricAccessor metricAccessor) {
        WSMetricsRequest request = new WSMetricsRequest();
        request.setIsProjectFile(true);
        request.setResourcesIds(new long[] {projectFile.getId()});
        try {
            this.metrics = metricAccessor.getMetricsByResourcesIds(request);
        } catch (WSException wse) {
            this.metrics = null;
        }
    }
    
    private void init(WSProjectAccessor projectAccessor) {
        WSFileModification[] modifications;
        WSProjectVersion[] fileProjectVersions;
        try {
            modifications = projectAccessor.getFileModifications(
                    projectVersion.getId(), projectFile.getId());
            fileProjectVersions = projectAccessor.getProjectVersionsByIds(
                    new long[] {projectFile.getProjectVersionId()});
        } catch (WSException wse) {
            modifications = null;
            fileProjectVersions = null;
        }
        if ((modifications == null) ||
                (fileProjectVersions == null) ||
                (fileProjectVersions.length != 1)){
            this.fileModifications = null;
        } else {
            fileModifications = new Hashtable<Long, WSFileModification>();
            WSProjectVersion fileProjectVersion = fileProjectVersions[0];
            for (WSFileModification currentModification : modifications) {
                if (currentModification.getProjectVersionNum() != fileProjectVersion.getVersion()) {
                    fileModifications.put(Long.valueOf(currentModification.getProjectVersionNum()),
                            currentModification);
                }
            }
        }
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
