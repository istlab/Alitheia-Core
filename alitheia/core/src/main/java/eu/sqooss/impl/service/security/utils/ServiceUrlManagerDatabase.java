/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 - 2010 - Organization for Free and Open Source Software,  
 *                Athens, Greece.
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

package eu.sqooss.impl.service.security.utils;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.ServiceUrl;

public class ServiceUrlManagerDatabase implements ServiceUrlManagerDBQueries {

    private static final String ATTRIBUTE_SERVICE_URL = "url";
    
    private DBService db;
    private Map<String, Object> serviceUrlProps;
    private Object lockObject = new Object();
    
    public ServiceUrlManagerDatabase(DBService db) {
        super();
        this.db = db;
        serviceUrlProps = new Hashtable<String, Object>(1);
    }

    public ServiceUrl getServiceUrl(long serviceUrlId) {
        return db.findObjectById(ServiceUrl.class, serviceUrlId);
    }
    
    public List<ServiceUrl> getServiceUrl(String serviceUrl) {
        synchronized (lockObject) {
            serviceUrlProps.clear();
            serviceUrlProps.put(ATTRIBUTE_SERVICE_URL, serviceUrl);
            return db.findObjectsByProperties(ServiceUrl.class, serviceUrlProps);
        }
    }
    
    public List<?> getServiceUrls() {
        return db.doHQL(GET_SERVICE_URLS);
    }
    
    public boolean deleteServiceUrl(ServiceUrl serviceUrl) {
        return db.deleteRecord(serviceUrl);
    }
    
    public boolean createServiceUrl(ServiceUrl newServiceUrl) {
        return db.addRecord(newServiceUrl);
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
