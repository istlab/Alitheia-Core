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

package eu.sqooss.service.security;

import eu.sqooss.service.db.ServiceUrl;

/**
 * <code>ServiceUrlManager</code> gives access to the service url's management. 
 */
public interface ServiceUrlManager {
    
    /**
     * @param serviceUrlId
     * @return the service url with given identifier,
     * null - if the service url doesn't exist
     */
    public ServiceUrl getServiceUrl(long serviceUrlId);
    
    /**
     * The service url is unique.
     * The method returns the service url object with given url. 
     * @param serviceUrl - the service url
     * @return <code>ServiceUrl</code> object with given url,
     * null - if the service url doesn't exist
     */
    public ServiceUrl getServiceUrl(String serviceUrl);
    
    /**
     * @return all service urls in the system
     */
    public ServiceUrl[] getServiceUrls();
    
    /**
     * This method creates a new service url.
     * @param url the url
     * @return new service url, null - if the service url isn't created
     */
    public ServiceUrl createServiceUrl(String url);
    
    /**
     * This method deletes the service url with given identifier.
     * @param serviceUrlId
     * @return true - if the service url is deleted successfully, false - otherwise
     */
    public boolean deleteServiceUrl(long serviceUrlId);
    
}

//vi: ai nosi sw=4 ts=4 expandtab
