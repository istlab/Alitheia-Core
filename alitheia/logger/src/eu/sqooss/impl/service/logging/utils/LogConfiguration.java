package eu.sqooss.impl.service.logging.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.osgi.framework.BundleContext;

import eu.sqooss.impl.service.logging.LogManagerConstants;
import eu.sqooss.impl.service.logging.LogManagerImpl;

/**
 * This class is the entry point for the logger configuration.
 */
public class LogConfiguration {
  
  private static final char INTERNAL_KEY_SYMBOL = '.';
  private static final String ROOT_PROPS_FILE_NAME = "logging.root.properties";
  private static final String ROOT_SIBLING_PROPS_FILE_NAME = "logging.root.sibling.properties";
  private static final String SERVICE_SIBLING_PROPS_FILE_NAME = "logging.service.sibling.properties";
  
  private LogManagerImpl logManager;
  private BundleContext bc;
  private Properties rootProperties;
  private Properties rootSiblingProperties;
  private Properties serviceSiblingProperties;
  
  public LogConfiguration(BundleContext bc, LogManagerImpl logManager) {
    this.bc = bc;
    this.logManager = logManager;
    loadLogProperties(bc);
  }
  
  /**
   * Sets the logger property indicated by the specified key.
   * @param loggerName
   * @param key
   * @param value
   */
  public void setConfigurationProperty(String loggerName, String key, String value) {
    String internalKey = loggerName + INTERNAL_KEY_SYMBOL + key;
    int nameLevel = LogUtils.getNameLevel(loggerName);
    switch (nameLevel) {
    case 0:
      rootProperties.put(internalKey, value);
      clearProperties(rootSiblingProperties, key);
      clearProperties(serviceSiblingProperties, key);
      logManager.notifyChildrenForChange(1, key, value);
    case 1:
      rootSiblingProperties.put(internalKey, value);
      String serviceLoggerName = LogManagerConstants.NAME_ROOT_LOGGER + LogManagerConstants.NAME_DELIMITER +
                                 LogManagerConstants.SIBLING_SERVICE_SYSTEM;
      if (serviceLoggerName.equals(loggerName)) {
        clearProperties(serviceSiblingProperties, key);
        logManager.notifyChildrenForChange(2, key, value);
      }
    case 2:
      serviceSiblingProperties.put(internalKey, value);
    }
  }
  
  /**
   * Gets the logger property indicated by the key.
   * @param loggerName
   * @param key
   * @return
   */
  public String getConfigurationProperty(String loggerName, String key) {
    String configProp = null;
    String internalKey = loggerName + INTERNAL_KEY_SYMBOL + key;
    int nameLevel = LogUtils.getNameLevel(loggerName);
    switch (nameLevel) {
    case 0:
      configProp = rootProperties.getProperty(internalKey);;
      break;
    case 1:
      configProp = rootSiblingProperties.getProperty(internalKey);
      if (configProp == null) {
        internalKey = LogManagerConstants.NAME_ROOT_LOGGER + INTERNAL_KEY_SYMBOL + key;
        configProp = rootProperties.getProperty(internalKey);
      }
      break;
    case 2:
      configProp = serviceSiblingProperties.getProperty(internalKey);
      if (configProp == null) {
        internalKey = LogManagerConstants.NAME_ROOT_LOGGER + LogManagerConstants.NAME_DELIMITER +
                      LogManagerConstants.SIBLING_SERVICE_SYSTEM + INTERNAL_KEY_SYMBOL + key;
        configProp = rootSiblingProperties.getProperty(internalKey);
        if (configProp == null) {
          internalKey = LogManagerConstants.NAME_ROOT_LOGGER + INTERNAL_KEY_SYMBOL + key;
          configProp = rootProperties.getProperty(internalKey);
        }
      }
      break;
    }
    if (configProp == null) {
      return getDefaultValue(loggerName, key);
    } else {
      return configProp;
    }
  }
  
  /**
   * The <code>close</code> method saves the configuration properties.
   */
  public void close() {
    saveLogProperties(bc);
  }
  
  /**
   * This method loads the configuration properties. (if the files exist)
   * @param bc
   */
  private void loadLogProperties(BundleContext bc) {
    File rootFile = bc.getDataFile(ROOT_PROPS_FILE_NAME);
    File rootSiblingFile = bc.getDataFile(ROOT_SIBLING_PROPS_FILE_NAME);
    File serviceSiblingFile = bc.getDataFile(SERVICE_SIBLING_PROPS_FILE_NAME);
    rootProperties = new Properties();
    rootSiblingProperties = new Properties();
    serviceSiblingProperties = new Properties();
    try {
      rootProperties.load(new FileInputStream(rootFile));
    } catch (FileNotFoundException fnfe) {
      //set properties manual
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
    try {
      rootSiblingProperties.load(new FileInputStream(rootSiblingFile));
    } catch (FileNotFoundException fnfe) {
      //set properties manual
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
    try {
      serviceSiblingProperties.load(new FileInputStream(serviceSiblingFile));
    } catch (FileNotFoundException fnfe) {
      //set properties manual
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }
  
  /**
   * This method saves the configuration properties.
   * @param bc
   */
  private void saveLogProperties(BundleContext bc) {
    File rootFile = bc.getDataFile(ROOT_PROPS_FILE_NAME);
    File rootSiblingFile = bc.getDataFile(ROOT_SIBLING_PROPS_FILE_NAME);
    File serviceSiblingFile = bc.getDataFile(SERVICE_SIBLING_PROPS_FILE_NAME);
    try {
      rootProperties.store(new FileOutputStream(rootFile), null);
      rootSiblingProperties.store(new FileOutputStream(rootSiblingFile), null);
      serviceSiblingProperties.store(new FileOutputStream(serviceSiblingFile), null);
    } catch (FileNotFoundException fnfe) {
      throw new RuntimeException(fnfe);
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }
  
  /**
   * @param loggerName
   * @param key
   * @return default value for some keys
   */
  private String getDefaultValue(String loggerName, String key) {
    if (LogConfigurationConstants.KEY_MESSAGE_FORMAT.equals(key)) {
      return LogConfigurationConstants.MESSAGE_FORMAT_TEXT_XML;
    }
    if (LogConfigurationConstants.KEY_FILE_NAME.equals(key)) {
      return loggerName;
    }
    return null;
  }
  
  private void clearProperties(Properties props, String suffix) {
    String currentKey;
    Enumeration keys;
    for(keys = props.propertyNames(); keys.hasMoreElements(); ) {
      currentKey = (String)keys.nextElement();
      if (currentKey.endsWith(suffix)) {
        props.remove(currentKey);
      }
    }
  }
  
}
