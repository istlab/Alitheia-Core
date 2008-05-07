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

package eu.sqooss.scl.accessor;

import eu.sqooss.scl.WSException;
import eu.sqooss.ws.client.datatypes.WSMetric;
import eu.sqooss.ws.client.datatypes.WSMetricsResultRequest;
import eu.sqooss.ws.client.datatypes.WSResultEntry;

public abstract class WSMetricAccessor extends WSAccessor {
    
    /**
     * This method returns the metrics for a given project.
     * 
     * @param projectId the project's id
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be establish to the SQO-OSS's web services service</li>
     *  <li>if web services service throws a exception</li>
     * <ul>
     */
    public abstract WSMetric[] getMetricsByProjectId(long projectId) throws WSException;
    
    /**
     * This method returns the metrics for a given files.
     * All files in the folder can be selected with the folder's name.
     * 
     * @param projectId the project's id
     * @param folderNames the folders' names; the folders' delimiter is a comma
     * @param fileNames the files' names, the files' delimiter is a comma
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be establish to the SQO-OSS's web services service</li>
     *  <li>if web services service throws a exception</li>
     * <ul>
     */
    public abstract WSMetric[] getMetricsByFileNames(long projectId, String folderNames, String fileNames) throws WSException;
    
    /**
     * This method returns all installed metrics.
     * @return the empty array if there aren't metrics
     */
    public abstract WSMetric[] getMetrics() throws WSException;
    
    public abstract WSResultEntry[] getMetricsResult(WSMetricsResultRequest resultRequest) throws WSException;
    
}

//vi: ai nosi sw=4 ts=4 expandtab
