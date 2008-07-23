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

import eu.sqooss.plugin.util.Entity;
import eu.sqooss.ws.client.datatypes.WSMetric;
import eu.sqooss.ws.client.datatypes.WSProjectVersion;
import eu.sqooss.ws.client.datatypes.WSResultEntry;
import eu.sqooss.ws.client.datatypes.WSStoredProject;

/**
 * The class represents the project.
 */
public class ProjectVersionEntity implements Entity {

    private WSStoredProject storedProject;
    private WSProjectVersion projectVersion;
    
    public ProjectVersionEntity(WSStoredProject storedProject,
            WSProjectVersion projectVersion) {
        this.storedProject = storedProject;
        this.projectVersion = projectVersion;
    }
    
    /**
     * @see eu.sqooss.plugin.util.Entity#getMetrics()
     */
    public WSMetric[] getMetrics() {
        // TODO: implement
        return null;
    }

    /**
     * @see eu.sqooss.plugin.util.Entity#getName()
     */
    public String getName() {
        return this.storedProject.getName();
    }

    /**
     * @see eu.sqooss.plugin.util.Entity#getVersions(boolean))
     */
    public Long[] getVersions() {
        // TODO: Implement
        return null;
    }

    public Long getCurrentVersion() {
        // TODO: Implement
        return null;
    }

    public WSResultEntry[] getMetricsResults(WSMetric[] metrics, Long[] versions) {
        // TODO: Implement
        return null;
    }

    public long getVersionById(long id) {
        // TODO: Implement
        return 0;
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
