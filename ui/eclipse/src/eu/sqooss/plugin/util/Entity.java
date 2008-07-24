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

package eu.sqooss.plugin.util;

import eu.sqooss.ws.client.datatypes.WSMetric;
import eu.sqooss.ws.client.datatypes.WSResultEntry;

/**
 * The classes that implement an <code>Entity</code>
 * interface wrap the eclipse project entities.
 * The <code>ConnectionUtils</code> class is used as a entity factory. 
 */
public interface Entity {
    
    /**
     * @return the entity name
     */
    public String getName();
    
    /**
     * @return the metrics that have a quality result for the entity
     */
    public WSMetric[] getMetrics();
    
    /**
     * The method return the entity's versions without the current.
     * The versions must be sorted into ascending order. 
     * 
     * @return the sorted entity's versions
     */
    public Long[] getSortedVersions();
    
    /**
     * Different versions of the entity have a unique identifier.
     * 
     * @param id - the version identifier
     * 
     * @return the version number
     */
    public long getVersionById(long id);
    
    /**
     * The method returns the entity version.
     * 
     * @return the entity version
     */
    public Long getCurrentVersion();
    
    /**
     * @param metrics - specifies the Alitheia metrics
     * @param versions - specifies the versions
     * @return - the quality result for the entity 
     */
    public WSResultEntry[] getMetricsResults(WSMetric[] metrics, Long[] versions);
    
}

//vi: ai nosi sw=4 ts=4 expandtab
