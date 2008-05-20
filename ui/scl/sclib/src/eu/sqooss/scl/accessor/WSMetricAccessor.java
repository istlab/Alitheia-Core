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
import eu.sqooss.ws.client.datatypes.WSMetricType;
import eu.sqooss.ws.client.datatypes.WSMetricsRequest;
import eu.sqooss.ws.client.datatypes.WSMetricsResultRequest;
import eu.sqooss.ws.client.datatypes.WSResultEntry;

/**
 * This class contains the metrics methods. 
 */
public abstract class WSMetricAccessor extends WSAccessor {
    
    /**
     * This method returns an array with all metrics, that have been evaluated
     * for the given project.
     * 
     * @param projectId - the project's identifier
     * 
     * @return The array with all evaluated metrics,
     * or a empty array when none are found.
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be established to the SQO-OSS's web services service</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract WSMetric[] getMetricsByProjectId(long projectId) throws WSException;
    
    /**
     * The method returns all information, that the SQO-OSS framework has
     * collected about the specified metric types.
     * 
     * @param metricTypesIds - the metric types' identifiers
     * 
     * @return The <code>WSMetricType</code> array that describes the
     * metric types, or an empty array when such metric types do not exist.
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be established to the SQO-OSS's web services service</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract WSMetricType[] getMetricTypesByIds(long[] metricTypesIds) throws WSException;
    
    /**
     * This method returns an array with all metrics, that are currently
     * installed in the SQO-OSS framework.
     * 
     * @return The array with all installed metrics,
     * or a empty array when none are found.
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be established to the SQO-OSS's web services service</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract WSMetric[] getMetrics() throws WSException;
    
    /**
     * The method looks for the metrics. The request object gives the search criteria. 
     * 
     * @param userName - the user's name used for authentication
     * @param password - the user's password used for authentication
     * @param request - the request object,
     * the object contains the request information
     * 
     * @return The array of metrics,
     * or an empty array when none are found.
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be established to the SQO-OSS's web services service</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract WSMetric[] getMetricsByResourcesIds(WSMetricsRequest request) throws WSException;
    
    /**
     * This method returns the array of results from the evaluation of the specified
     * metrics on the given data access object.
     * 
     * @param resultRequest the request object,
     * the object contains the request information
     * 
     * @return The array of all metric evaluation results on that request,
     * or a empty array when none are found.
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be established to the SQO-OSS's web services service</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract WSResultEntry[] getMetricsResult(WSMetricsResultRequest resultRequest) throws WSException;
    
}

//vi: ai nosi sw=4 ts=4 expandtab
