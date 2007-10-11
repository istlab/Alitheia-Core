package eu.sqooss.service.logging;

/**
 * The <code>Logger</code> instances are used for logging. They are created with createLogger method of the
 * LogManager service. 
 */
public interface Logger {
  
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
