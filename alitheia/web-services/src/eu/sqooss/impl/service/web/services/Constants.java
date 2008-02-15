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

package eu.sqooss.impl.service.web.services;

import eu.sqooss.service.web.services.WebServices;

public class Constants {
    
    /** Represents the location of the configuration file. */
    public static final String FILE_NAME_PROPERTIES = "/OSGI-INF/configuration/web-services.properties";
    
    /**
     * Represents the property key for the name of the web service endpoints.
     * The URL of the web services service is: http:/.../[web.service.context]/services/[web.service.name]
     * The WSDL file is: http:/.../[web.service.context]/services/[web.service.name]?wsdl
     */
    public static final String PROPERTY_KEY_WEB_SERVICES_NAME          = "web.service.name";
    
    /** 
     * Represents the property key for the context to be associated with the web services.
     * The URL of the web services service is: http:/.../[web.service.context]/services/[web.service.name]
     * The WSDL file is: http:/.../[web.service.context]/services/[web.service.name]?wsdl
     */
    public static final String PROPERTY_KEY_WEB_SERVICES_CONTEXT       = "web.service.context";
    
    /**
     * Represents the property key for the name of the interface class that defines the endpoints to be exposed as web services.
     */
    public static final String PROPERTY_KEY_WEB_SERVICES_INTERFACE     = "interface.class";
    
    /** Represents the name of the web service endpoints. */
    public static final String PROPERTY_VALUE_WEB_SERVICES_NAME        = "ws";
    
    /** Represents the context to be associated with the web services. */
    public static final String PROPERTY_VALUE_WEB_SERVICES_CONTEXT     = "sqooss";
    
    /** Represents the name of the interface class that defines the endpoints to be exposed as web services. */
    public static final String PROPERTY_VALUE_WEB_SERVICES_INTERFACE   = WebServices.class.getName();
    
    /** Represents privilege value - add metric. */
    public static final String URL_PRIVILEGE_ACTION_ADD_METRIC        = "add_metric";
    
    /** Represents privilege value - remove metric. */
    public static final String URL_PRIVILEGE_ACTION_REMOVE_METRIC     = "remove_metric";
    
    /** Represents privilege value - get metric result. */
    public static final String URL_PRIVILEGE_ACTION_GET_METRIC_RESULT = "get_metric_result";
    
    /** Represents privilege value - get metric id. */
    public static final String URL_PRIVILEGE_ACTION_GET_METRIC_ID     = "get_metric_id";
    
}

//vi: ai nosi sw=4 ts=4 expandtab
