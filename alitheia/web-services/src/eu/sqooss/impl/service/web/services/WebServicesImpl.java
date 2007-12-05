/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.security.SecurityConstants;
import eu.sqooss.service.security.SecurityManager;
import eu.sqooss.service.web.services.WebServicesException;

public class WebServicesImpl {
    
    private BundleContext bc;
    private ServiceTracker securityTracker;
    
    public WebServicesImpl(BundleContext bc, ServiceTracker securityTracker) {
        this.bc = bc;
        this.securityTracker = securityTracker;
    }
    
    /*metric's methods*/
    /**
     * @see eu.sqooss.service.web.services.WebServices#addMetric(String, String, String)
     */
    public String addMetric(String userName, String password, String url) throws WebServicesException {
        String resourceUrl = SecurityConstants.URL_SQOOSS_SERVICE;
        Hashtable<String, String> privileges = new Hashtable<String, String>(1);
        privileges.put(SecurityConstants.URL_PRIVILEGE_ACTION,
                WebServicesConstants.URL_PRIVILEGE_ACTION_ADD_METRIC);
        if (!checkSecurity(userName, password, privileges, resourceUrl)) {
            throw new WebServicesException("No permission to perform (add metric) opeariton!");
        }
        //TODO: adds the metric from the Url
        return null;
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#removeMetric(String, String, String) 
     */
    public void removeMetric(String userName, String password, String metricId) throws WebServicesException {
        String resourceUrl = SecurityConstants.URL_SQOOSS_SERVICE;
        Hashtable<String, String> privileges = new Hashtable<String, String>(1);
        privileges.put(SecurityConstants.URL_PRIVILEGE_ACTION,
                WebServicesConstants.URL_PRIVILEGE_ACTION_REMOVE_METRIC);
        if (!checkSecurity(userName, password, privileges, resourceUrl)) {
            throw new WebServicesException("No permission to perform (remove metric) operation!");
        }
        //TODO: removes the metric with metricId
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getMetricId(String, String, String, String)
     */
    public String getMetricId(String userName, String password, String metricName, String metricVersion) throws WebServicesException {
        String resourceUrl = SecurityConstants.URL_SQOOSS_SERVICE;
        Hashtable<String, String> privileges = new Hashtable<String, String>(1);
        privileges.put(SecurityConstants.URL_PRIVILEGE_ACTION,
                WebServicesConstants.URL_PRIVILEGE_ACTION_GET_METRIC_ID);
        if (!checkSecurity(userName, password, privileges, resourceUrl)) {
            throw new WebServicesException("No permission to perform (get metric id) operation!");
        }
        //TODO: returns the metric's id
        return null;
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getFileGroupMetricResult(String, String, String, ProjectVersion)
     */
    public String getFileGroupMetricResult(String userName, String password,
            String metricId, ProjectVersion projectVersion) {
        //!!!!FileGroupMetric works with ProjectVersion, maybe must be FileGroup
        return null;
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getProjectFileMetricResult(String, String, String, ProjectFile)
     */
    public String getProjectFileMetricResult(String userName, String password,
            String metricId, ProjectFile projectFile) throws WebServicesException {
        String[] urlComponents = {
                SecurityConstants.URL_SQOOSS_SERVICE,
                SecurityConstants.URL_PARAMETER_METRIC_ID, metricId,
                SecurityConstants.URL_PRIVILEGE_ACTION, WebServicesConstants.URL_PRIVILEGE_ACTION_GET_METRIC_RESULT
        };
        String url = makeUrl(urlComponents);
        if (!checkSecurity(userName, password, url)) {
            throw new WebServicesException("No permission to perform this operation: " + url);
        }
        return null;
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getProjectVersionMetricResult(String, String, String, ProjectVersion)
     */
    public String getProjectVersionMetricResult(String userName, String password,
            String metricId, ProjectVersion projectVersion) throws WebServicesException {
        String[] urlComponents = {
                SecurityConstants.URL_SQOOSS_SERVICE,
                SecurityConstants.URL_PARAMETER_METRIC_ID, metricId,
                SecurityConstants.URL_PRIVILEGE_ACTION, WebServicesConstants.URL_PRIVILEGE_ACTION_GET_METRIC_RESULT
        };
        String url = makeUrl(urlComponents);
        if (!checkSecurity(userName, password, url)) {
            throw new WebServicesException("No permission to perform this operation: " + url);
        }
        return null;
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getStoredProjectMetricResult(String, String, String, StoredProject)
     */
    public String getStoredProjectMetricResult(String userName, String password,
            String metricId, StoredProject storedProject) throws WebServicesException {
        String[] urlComponents = {
                SecurityConstants.URL_SQOOSS_SERVICE,
                SecurityConstants.URL_PARAMETER_METRIC_ID, metricId,
                SecurityConstants.URL_PRIVILEGE_ACTION, WebServicesConstants.URL_PRIVILEGE_ACTION_GET_METRIC_RESULT
        };
        String url = makeUrl(urlComponents);
        System.out.println("url: " + url);
        if (!checkSecurity(userName, password, url)) {
            throw new WebServicesException("No permission to perform this operation: " + url);
        }
        return null;
    }
    /*metric's methods*/
    
    /*project's methods*/
    /**
     * @see eu.sqooss.service.web.services.WebServices#startEvaluateProject(String, String, StoredProject)
     */
    public void startEvaluateProject(String userName, String password, StoredProject storedProject) {
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#stopEvaluateProject(String, String, StoredProject)
     */
    public void stopEvaluateProject(String userName, String password, StoredProject storedProject) {
    }
    /*project's methods*/
    
    private boolean checkSecurity(String userName, String password, String Url) {
        SecurityManager security = (SecurityManager)securityTracker.getService();
        if (security != null) {
            return security.checkPermission(Url, userName, password);
        } else {
            return false;
        }
    }
    
    private boolean checkSecurity(String userName, String password, Hashtable<String, String> privileges, String resourceUrl) {
        SecurityManager security = (SecurityManager)securityTracker.getService();
        if (security != null) {
            return security.checkPermission(resourceUrl, privileges, userName, password);
        } else {
            return false;
        }
    }
    
    private String makeUrl(String[] args) {
        int argsLength = args.length;
        if (argsLength >= 1) {
            StringBuffer url = new StringBuffer();
            url.append(args[0]);
            if ((argsLength >= 3) && ((argsLength % 2) == 1)) {
                url.append("?");
                for (int i = 1; i < argsLength; i+=2) {
                    url.append(args[i]);
                    url.append("=");
                    url.append(args[i+1]);
                    url.append("&");
                }
                url.deleteCharAt(url.length()-1); //delete last &
            }
            return url.toString();
        } else {
            return null;
        }
        
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
