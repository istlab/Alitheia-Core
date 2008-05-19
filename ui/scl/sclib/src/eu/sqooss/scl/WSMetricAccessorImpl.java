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

package eu.sqooss.scl;

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.axis2.AxisFault;

import eu.sqooss.scl.accessor.WSMetricAccessor;
import eu.sqooss.ws.client.WsStub;
import eu.sqooss.ws.client.datatypes.WSMetric;
import eu.sqooss.ws.client.datatypes.WSMetricType;
import eu.sqooss.ws.client.datatypes.WSMetricsResultRequest;
import eu.sqooss.ws.client.datatypes.WSResultEntry;
import eu.sqooss.ws.client.ws.GetMetricTypesByIds;
import eu.sqooss.ws.client.ws.GetMetricTypesByIdsResponse;
import eu.sqooss.ws.client.ws.GetMetrics;
import eu.sqooss.ws.client.ws.GetMetricsByFileNames;
import eu.sqooss.ws.client.ws.GetMetricsByFileNamesResponse;
import eu.sqooss.ws.client.ws.GetMetricsByProjectId;
import eu.sqooss.ws.client.ws.GetMetricsByProjectIdResponse;
import eu.sqooss.ws.client.ws.GetMetricsResponse;
import eu.sqooss.ws.client.ws.GetMetricsResult;
import eu.sqooss.ws.client.ws.GetMetricsResultResponse;

class WSMetricAccessorImpl extends WSMetricAccessor {

    private static final String METHOD_NAME_GET_METRICS_BY_PROJECT_ID  = "getMetricsByProjectId";
    
    private static final String METHOD_NAME_GET_METRIC_TYPES_BY_IDS    = "getMetricTypesByIds";

    private static final String METHOD_NAME_GET_METRICS_BY_FILE_NAMES  = "getMetricsByFileNames";
    
    private static final String METHOD_NAME_GET_METRICS                = "getMetrics";

    private static final String METHOD_NAME_GET_METRICS_RESULT         = "getMetricsResult";

    private static final WSMetricType[] EMPTY_ARRAY_METRIC_TYPES = new WSMetricType[0];
    
    private Map<String, Object> parameters;
    private String userName;
    private String password;
    private WsStub wsStub;

    public WSMetricAccessorImpl(String userName, String password, String webServiceUrl) throws WSException {
        this.userName = userName;
        this.password = password;
        parameters = new Hashtable<String, Object>();
        try {
            this.wsStub = new WsStub(webServiceUrl);
        } catch (AxisFault af) {
            throw new WSException(af);
        }
    }

    /**
     * @see eu.sqooss.scl.accessor.WSMetricAccessor#getMetricsByProjectId(long)
     */
    @Override
    public WSMetric[] getMetricsByProjectId(long projectId) throws WSException {
        GetMetricsByProjectIdResponse response;
        GetMetricsByProjectId params;
        if (!parameters.containsKey(METHOD_NAME_GET_METRICS_BY_PROJECT_ID)) {
            params = new GetMetricsByProjectId();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_METRICS_BY_PROJECT_ID, params);
        } else {
            params = (GetMetricsByProjectId) parameters.get(
                    METHOD_NAME_GET_METRICS_BY_PROJECT_ID);
        }
        synchronized (params) {
            params.setProjectId(projectId);
            try {
                response = wsStub.getMetricsByProjectId(params);
            } catch (RemoteException re) {
                throw new WSException(re);
            }
        }
        return (WSMetric[]) normaliseWSArrayResult(response.get_return());
    }

    /**
     * @see eu.sqooss.scl.accessor.WSMetricAccessor#getMetricTypesByIds(long[])
     */
    @Override
    public WSMetricType[] getMetricTypesByIds(long[] metricTypesIds) throws WSException {
        if (!isValidArray(metricTypesIds)) return EMPTY_ARRAY_METRIC_TYPES;
        GetMetricTypesByIdsResponse response;
        GetMetricTypesByIds params;
        if (!parameters.containsKey(METHOD_NAME_GET_METRIC_TYPES_BY_IDS)) {
            params = new GetMetricTypesByIds();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_METRIC_TYPES_BY_IDS, params);
        } else {
            params = (GetMetricTypesByIds) parameters.get(
                    METHOD_NAME_GET_METRIC_TYPES_BY_IDS);
        }
        synchronized (params) {
            params.setMetricTypesIds(metricTypesIds);
            try {
                response = wsStub.getMetricTypesByIds(params);
            } catch (RemoteException re) {
                throw new WSException(re);
            }
        }
        return (WSMetricType[]) normaliseWSArrayResult(response.get_return());
    }

    /**
     * Utility function for retrieveMetrics4SelectedFiles, which
     * gets a String representing a comma-separated list of directories
     * or files. This is basically java.lang.String.split() with some
     * additional munging - leading and trailing spaces are not
     * supported (e.g. "foo , bar" is the same as "foo,bar") and empty
     * entries (e.g. "foo,,bar" is the same as "foo,bar") aren't either.
     *
     * Also, this method hides the en_US spelling behind a better name.
     *
     * @param s String to tokenize
     * @return An array of string tokens. This array is never null;
     *      this array is never empty; if it would be empty, then
     *      an array of length 1 with a single null is used instead.
     */
    private String[] tokeniseCommaSeparatedString(String s) {
        // Degenerate case: null strings map to [null]
        if (s == null) {
            return new String[]{null};
        }

        StringTokenizer tokenizer = new StringTokenizer(s, ",");
        int tokenCount = tokenizer.countTokens();
        // If there are no tokens, map to [null]
        if (tokenCount == 0) {
            return new String[]{null};
        }

        String[] tokens = new String[tokenCount];
        for (int i = 0; i < tokenCount; ++i) {
            tokens[i] = tokenizer.nextToken().trim();
        }

        return tokens;
    }

    /**
     * @see eu.sqooss.scl.accessor.WSMetricAccessor#getMetricsByFileNames(long, java.lang.String, java.lang.String)
     */
    @Override
    public WSMetric[] getMetricsByFileNames(long projectId, String folderNames,
            String fileNames) throws WSException {
        String[] folderNamesArray = tokeniseCommaSeparatedString(folderNames);
        String[] fileNamesArray = tokeniseCommaSeparatedString(fileNames);

        GetMetricsByFileNamesResponse response;
        GetMetricsByFileNames params;
        if (!parameters.containsKey(METHOD_NAME_GET_METRICS_BY_FILE_NAMES)) {
            params = new GetMetricsByFileNames();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_METRICS_BY_FILE_NAMES, params);
        } else {
            params = (GetMetricsByFileNames) parameters.get(
                    METHOD_NAME_GET_METRICS_BY_FILE_NAMES);
        }
        synchronized (params) {
            params.setProjectId(projectId);
            params.setFolders(folderNamesArray);
            params.setFileNames(fileNamesArray);
            try {
                response = wsStub.getMetricsByFileNames(params);
            } catch (RemoteException re) {
                throw new WSException(re);
            }
        }
        return (WSMetric[]) normaliseWSArrayResult(response.get_return());
    }

    /**
     * @see eu.sqooss.scl.accessor.WSMetricAccessor#getMetrics()
     */
    @Override
    public WSMetric[] getMetrics() throws WSException {
        GetMetricsResponse response;
        GetMetrics params;
        if (!parameters.containsKey(METHOD_NAME_GET_METRICS)) {
            params = new GetMetrics();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_METRICS, params);
        } else {
            params = (GetMetrics) parameters.get(METHOD_NAME_GET_METRICS);
        }
        synchronized (params) {
            try {
                response = wsStub.getMetrics(params);
            } catch (RemoteException re) {
                throw new WSException(re);
            }
        }
        return (WSMetric[]) normaliseWSArrayResult(response.get_return());
    }

    /**
     * @see eu.sqooss.scl.accessor.WSMetricAccessor#getMetricsResult(eu.sqooss.ws.client.datatypes.WSMetricResultRequest)
     */
    @Override
    public WSResultEntry[] getMetricsResult(WSMetricsResultRequest resultRequest)
            throws WSException {
        GetMetricsResult params;
        GetMetricsResultResponse response;
        if (!parameters.containsKey(METHOD_NAME_GET_METRICS_RESULT)) {
            params = new GetMetricsResult();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_METRICS_RESULT, params);
        } else {
            params = (GetMetricsResult) parameters.get(METHOD_NAME_GET_METRICS_RESULT);
        }
        synchronized (params) {
            params.setResultRequest(resultRequest);
            try {
                response = wsStub.getMetricsResult(params);
            } catch (RemoteException re) {
                throw new WSException(re);
            }
        }
        return (WSResultEntry[]) normaliseWSArrayResult(response.get_return());
    }

    private static boolean isValidArray(long[] arr) {
        return ((arr != null) && (arr.length > 0));
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
