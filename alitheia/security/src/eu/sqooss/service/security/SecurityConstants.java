package eu.sqooss.service.security;

/**
 *  These constants are used to describe the resources that require access control.
 *  They can be a part of the security URL.
 *  <p>
 *  For example:
 *  <p><code>
 *  SecurityConstants.URL_SQOOSS_DATABASE and SecurityConstants.PRIVILEGE_ACTION are used in the URL:
 *  </p></code>
 *  svc://sqooss.database?action=DeleteProject
 *  </p>
 */
public interface SecurityConstants {
    
    /**
     * Represents the url of the SQO-OSS system.
     */
    public static final String URL_SQOOSS = "svc://sqooss";
    
    /**
     * Represents the url of the service system.
     */
    public static final String URL_SQOOSS_SERVICE      = SecurityConstants.URL_SQOOSS + ".service";
    
    /**
     * Represents the url of the database connectivity.
     */
    public static final String URL_SQOOSS_DATABASE     = SecurityConstants.URL_SQOOSS + ".database";
    
    /**
     * Represents the url of the security.
     */
    public static final String URL_SQOOSS_SECURITY     = SecurityConstants.URL_SQOOSS + ".security";
    
    /**
     * Represents the url of the messaging.
     */
    public static final String URL_SQOOSS_MESSAGING    = SecurityConstants.URL_SQOOSS + ".messaging";
    
    /**
     * Represents the url of the web services.
     */
    public static final String URL_SQOOSS_WEB_SERVICES = SecurityConstants.URL_SQOOSS + ".webservices";
    
    /**
     * Represents the url of the scheduling.
     */
    public static final String URL_SQOOSS_SCHEDULING   = SecurityConstants.URL_SQOOSS + ".scheduling";
    
    /**
     * Represents the url of the updater.
     */
    public static final String URL_SQOOSS_UPDATER      = SecurityConstants.URL_SQOOSS + ".updater";
    
    /**
     * Represents the metric id parameter. It can be a part of the URL.
     */
    public static final String URL_PARAMETER_METRIC_ID       = "mid";
    
    /**
     * Represent the action privilege. It can be a part of the URL. 
     */
    public static final String URL_PRIVILEGE_ACTION = "action";
    
}
