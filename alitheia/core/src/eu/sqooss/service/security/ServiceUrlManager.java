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
 * <code>ServiceUrlManager</code> provides methods for service URL's
 * management.
 */
public interface ServiceUrlManager {

    /**
     * Returns the service URL referenced by the given identifier.
     * 
     * @param serviceUrlId - the service URL's identifier
     * @return The <code>ServiceUrl</code> object referenced by the given
     *   identifier, or <code>null</code> when such service doesn't exist.
     */
    public ServiceUrl getServiceUrl(long serviceUrlId);

    /**
     * Returns the service URL associated with the given service URL
     * descriptor.
     * 
     * @param serviceUrl - the service URL's descriptor
     * @return The <code>ServiceUrl</code> object associated with the given
     *   descriptor, or <code>null</code> when such service doesn't exist.
     */
    public ServiceUrl getServiceUrl(String serviceUrl);

    /**
     * Returns an array of all service URLs, that are currently defined in the
     * SQO-OSS framework.
     * 
     * @return The array of all service URLs.
     */
    public ServiceUrl[] getServiceUrls();

    /**
     * This method creates a new service URL from the given service URL
     * descriptor.
     * 
     * @param url - the service URL's descriptor
     * @return The new <code>ServiceUrl</code> object, or <code>null</code>
     *   upon failure.
     */
    public ServiceUrl createServiceUrl(String url);

    /**
     * This method deletes service URL referenced by the given identifier.
     * 
     * @param serviceUrlId - the service URL's identifier
     * @return <code>true</code> or the service URL is deleted successfully,
     *   or <code>false</code> upon failure.
     */
    public boolean deleteServiceUrl(long serviceUrlId);

}

//vi: ai nosi sw=4 ts=4 expandtab
