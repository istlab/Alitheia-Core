package eu.sqooss.impl.service.web.services;

import eu.sqooss.service.web.services.WebServices;

public class WebServicesConstants {
    
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
    public static final String PROPERTY_VALUE_WEB_SERVICES_NAME        = "system";
    
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
