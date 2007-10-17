package eu.sqooss.service.logging;

import eu.sqooss.impl.service.logging.LogManagerConstants;

/**
 * The <code>Logger</code> instances are used for logging. They are created with createLogger method of the
 * LogManager service. 
 */
public interface Logger {
  
  /**
   * Represents the logger name delimiter.
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
   * Logs a message with a info(lowest) logging level
   * @param message a log message
   */
  public void info(String message);
  
  /**
   * Logs a message with a config logging level
   * @param message a log message
   */
  public void config(String message);
  
  /**
   * Logs a message with a warning logging level
   * @param message a log message
   */
  public void warning(String message);
  
  /**
   * Logs a message with a severe(highest) logging level
   * @param message a log message
   */
  public void severe(String message);
  
  /**
   * Returns a name of the logger.
   * @return the logger's name
   */
  public String getName();
  
  /**
   * Sets the logger property indicated by the specified key.
   * @param key the name of the logger property
   * <p> The available keys are:
   * <ul>
   * <li> <b>file.name</b> 
   * <li> <b>message.format</b> - <b>text/plain</b> or <b>text/xml</b>
   * <li> <b>rotation.file.number</b>
   * <li> <b>rotation.file.length</b>
   * </ul>
   * @param value the value of the logger property
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
