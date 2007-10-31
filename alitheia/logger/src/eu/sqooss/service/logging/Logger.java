package eu.sqooss.service.logging;

import eu.sqooss.impl.service.logging.LogManagerConstants;

/**
 * The <code>Logger</code> instances are used for logging.
 * They are created and released with <code>createLogger</code> method
 * and <code>releaseLogger</code> method of the <code>LogManager</code> class.
 * The loggers are represent with their names.
 * Some of them are "well known" and they are added as constants.
 * For example:
 * <p>
 * <code>Logger.NAME_SQOOSS</code> represents a SQO-OSS system logger <br>
 * <code>Logger.NAME_SQOOSS_SERVICE</code> represents a service system logger <br>
 * </p>
 */
public interface Logger {
    /**
     * Represents the logger name delimiter.
     * The metric plug-in loggers can use the delimiter. 
     */
    public static final String LOGGER_NAME_DELIMITER = String.valueOf(LogManagerConstants.NAME_DELIMITER);

    /**
     * Represents SQO-OSS system logger name.
     */
    public static final String NAME_SQOOSS = LogManagerConstants.NAME_ROOT_LOGGER;

    /**
     * Represents service system logger name.
     */
    public static final String NAME_SQOOSS_SERVICE      = LogManagerConstants.NAME_ROOT_LOGGER +
                                                          LogManagerConstants.NAME_DELIMITER + 
                                                          LogManagerConstants.SIBLING_SERVICE_SYSTEM;

    /**
     * Represents database connectivity logger name.
     */
    public static final String NAME_SQOOSS_DATABASE     = LogManagerConstants.NAME_ROOT_LOGGER +
                                                          LogManagerConstants.NAME_DELIMITER + 
                                                          LogManagerConstants.SIBLING_DATABASE;

    /**
     * Represents security logger name.
     */
    public static final String NAME_SQOOSS_SECURITY     = LogManagerConstants.NAME_ROOT_LOGGER +
                                                          LogManagerConstants.NAME_DELIMITER + 
                                                          LogManagerConstants.SIBLING_SECURITY;

    /**
     * Represents messaging logger name.
     */
    public static final String NAME_SQOOSS_MESSAGING    = LogManagerConstants.NAME_ROOT_LOGGER +
                                                          LogManagerConstants.NAME_DELIMITER + 
                                                          LogManagerConstants.SIBLING_MESSAGING;

    /**
     * Represents web services logger name.
     */
    public static final String NAME_SQOOSS_WEB_SERVICES = LogManagerConstants.NAME_ROOT_LOGGER +
                                                          LogManagerConstants.NAME_DELIMITER + 
                                                          LogManagerConstants.SIBLING_WEB_SERVICES;

    /**
     * Represents scheduling logger name.
     */
    public static final String NAME_SQOOSS_SCHEDULING   = LogManagerConstants.NAME_ROOT_LOGGER +
                                                          LogManagerConstants.NAME_DELIMITER + 
                                                          LogManagerConstants.SIBLING_SCHEDULING;

    /**
     * Represents updater logger name.
     */
    public static final String NAME_SQOOSS_UPDATER      = LogManagerConstants.NAME_ROOT_LOGGER +
                                                          LogManagerConstants.NAME_DELIMITER + 
                                                          LogManagerConstants.SIBLING_UPDATER;

    /**
     * Represents web UI logger name.
     */
    public static final String NAME_SQOOSS_WEBUI      =   LogManagerConstants.NAME_ROOT_LOGGER +
                                                          LogManagerConstants.NAME_DELIMITER + 
                                                          LogManagerConstants.SIBLING_WEBUI;
    /**
     * Represents web TDS logger name.
     */
    public static final String NAME_SQOOSS_TDS        =   LogManagerConstants.NAME_ROOT_LOGGER +
                                                          LogManagerConstants.NAME_DELIMITER + 
                                                          LogManagerConstants.SIBLING_TDS;

    /**
     * Represents web FDS logger name.
     */
    public static final String NAME_SQOOSS_FDS        =   LogManagerConstants.NAME_ROOT_LOGGER +
                                                          LogManagerConstants.NAME_DELIMITER + 
                                                          LogManagerConstants.SIBLING_FDS;

    /**
     * Logs a message with a info(lowest) logging level.
     * @param message a log message
     */
    public void info(String message);

    /**
     * Logs a message with a config logging level.
     * @param message a log message
     */
    public void config(String message);

    /**
     * Logs a message with a warning logging level.
     * @param message a log message
     */
    public void warning(String message);

    /**
     * Logs a message with a severe(highest) logging level.
     * The messages from severe logging level are distributed to the logger's parent too.
     * @param message a log message
     */
    public void severe(String message);

    /**
     * Returns a name of the logger. The naming scheme is similar to domain names.
     * @return the logger's name
     */
    public String getName();

    /**
     * Sets the logger property indicated by the specified key.
     * The <code>getConfigurationKeys</code> method returns the available keys.
     * <p>
     * The configuration changes are reflect to all the logger's children.
     * For example, if SQO-OSS system logger sets your log file name then all loggers change your file names.
     * </p>
     * The configuration settings are read from the configuration files (configuration files are properties files see java.util.Properties).
     * There are three configuration files: logging.root.properties, logging.root.sibling.properties and logging.service.sibling.properties.
     * <ul>
     * <li> logging.root.properties - contains a SQO-OSS system logger configurations, if exist
     * <li> logging.root.sibling.properties - contains the configurations of the SQO-OSS system logger's children, if exist
     * <li> logging.service.sibling.properties - contains the configurations of the service system logger's children, if exist
     * </ul>
     * If they don't exist then default configuration settings are used:
     * <ul>
     * <li> file.name - the name of the logger
     * <li> message.format - text/xml
     * <li> rotation.file.number - 0
     * <li> rotation.file.length - -1 (no rotation)
     * </ul>
     * 
     * @param key the name of the logger property
     * The available keys are:
     * <ul>
     * <li> file.name - sets the logger file name, the logger adds a file extension and a extension for message format;  
     * <li> message.format - sets the logger message format, available formats are: "text/plain" and "text/xml";
     * <li> rotation.file.number - sets the number of the files used for a log rotation
     * <li> rotation.file.length - this property specifies the maximum file size before the log rotation 
     * </ul>
     * 
     * @param value the value of the logger property
     * The correct values are:
     * <ul>
     * <li> file.name - a valid file name; <br>
     * for example: <code>logger.setConfigurationProperty("file.name", "logfile");</code>
     * <li> message.format - "text/plain" or "text/xml"; <br>
     * for example: <code>logger.setConfigurationProperty("message.format", "text/plain");</code>
     * <li> rotation.file.number - a valid number
     * <li> rotation.file.length - a valid number
     * </ul>
     */
    public void setConfigurationProperty(String key, String value);

    /**
     * Gets the logger property indicated by the key.
     * @param key the name of the logger property
     * @return the value of the service property; <code>null</code> if there is no property with that key.
     */
    public String getConfigurationProperty(String key);

    /**
     * Returns a string array of the configuration keys.
     * @return the configuration keys
     */
    public String[] getConfigurationKeys();
}
