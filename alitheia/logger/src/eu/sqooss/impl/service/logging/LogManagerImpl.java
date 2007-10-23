package eu.sqooss.impl.service.logging;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import org.osgi.framework.BundleContext;

import eu.sqooss.impl.service.logging.utils.LogConfiguration;
import eu.sqooss.impl.service.logging.utils.LogUtils;
import eu.sqooss.impl.service.logging.utils.LogWritersManager;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;

public class LogManagerImpl extends LogManager {

  public static LogManagerImpl logManager = new LogManagerImpl();
  
  private Object lockObject = new Object();
  
  private BundleContext bc;
  
  private LoggerImpl rootLogger;           //stores sqooss - level 0
  private Hashtable rootSiblingLoggers;    //stores sqooss.<service_name> - level 1
  private Hashtable serviceSiblingLoggers; //stores sqooss.service.<plugin_name> - level 2
  
  private LogConfiguration logConfig;
  private LogWritersManager logWritersManager;
  
  public LogManagerImpl() {
    rootSiblingLoggers = new Hashtable();
    serviceSiblingLoggers = new Hashtable();
  }
  
  /**
   * @see eu.sqooss.service.logging.LogManager#createLogger(String)
   */
  public Logger createLogger(String name) {
    LoggerImpl logger = null;
    int nameLevel = LogUtils.getNameLevel(name);
    synchronized (lockObject) {
      switch (nameLevel) {
      case -1:
        throw new IllegalArgumentException("The name is not valid!");
      case 0: 
        if (rootLogger == null) {
          rootLogger = new LoggerImpl(name, logWritersManager, logConfig, this);
          logger = rootLogger;
        } else {
          logger = rootLogger;
        }
        break;
      case 1:
        if (!rootSiblingLoggers.containsKey(name)) {
          logger = new LoggerImpl(name, logWritersManager, logConfig, this);
          rootSiblingLoggers.put(name, logger);
        } else {
          logger = (LoggerImpl)rootSiblingLoggers.get(name);
        }
        break;
      case 2:
        if (!serviceSiblingLoggers.containsKey(name)) {
          logger = new LoggerImpl(name, logWritersManager, logConfig, this);
          serviceSiblingLoggers.put(name, logger);
        } else {
          logger = (LoggerImpl)rootSiblingLoggers.get(name);
        }
        break;
      }
      logger.get();
      return logger;
    }
  }

  /**
   * @see eu.sqooss.service.logging.LogManager#releaseLogger(String)
   */
  public void releaseLogger(String name) {
    LoggerImpl logger;
    int takingsNumber;
    int nameLevel = LogUtils.getNameLevel(name);
    synchronized (lockObject) {
      switch (nameLevel) {
      case -1:
        return;
      case 0:
        if (rootLogger != null) {
          takingsNumber = rootLogger.unget();
          if (takingsNumber == 0) {
            rootLogger.close();
            rootLogger = null;
          }
        }
      case 1:
        logger = (LoggerImpl)rootSiblingLoggers.get(name);
        if (logger != null) {
          takingsNumber = logger.unget();
          if (takingsNumber == 0) {
            logger.close();
            rootSiblingLoggers.remove(name);
          }
        }
      case 2:
        logger = (LoggerImpl)serviceSiblingLoggers.get(name);
        if (logger != null) {
          takingsNumber = logger.unget();
          if (takingsNumber == 0) {
            logger.close();
            serviceSiblingLoggers.remove(name);
          }
        }
      }
    }
  }

  /**
   * This method notifies the logger's children for a configuration change.
   * @param childrenLevel
   * @param key
   * @param value
   */
  public void notifyChildrenForChange(int childrenLevel, String key, String value) {
    switch (childrenLevel) {
    //notify all
    case 1:
      notify(rootSiblingLoggers.values(), key, value);
      notify(serviceSiblingLoggers.values(), key, value);
      break;
    case 2:
      notify(serviceSiblingLoggers.values(), key, value);
      break;
    }
  }
  
  private void notify(Collection loggers, String key, String value) {
    LoggerImpl currentLogger;
    Iterator iter = loggers.iterator();
    while (iter.hasNext()) {
      currentLogger = (LoggerImpl)iter.next();
      currentLogger.setConfigurationProperty(key, value, true);
    }
  }
  
  /**
   * Sets the actual bundle context.
   * @param bc the bundle context
   */
  public void setBundleContext(BundleContext bc) {
    if (this.bc == null) {
      logWritersManager = new LogWritersManager(bc);
      logConfig = new LogConfiguration(bc, this);
    }
    this.bc = bc;
  }
  
  /**
   * @return the bundle context
   */
  public BundleContext getBundleContext() {
    return this.bc;
  }
  
  /**
   * This method closes the log manager and the loggers.
   */
  public void close() {
    if (logConfig != null) {
      logConfig.close();
    }
    if (rootLogger != null) {
      rootLogger.close();
      rootLogger = null;
    }
    Collection elements;
    Iterator iter;
    LoggerImpl logger;
    
    elements = rootSiblingLoggers.values();
    iter = elements.iterator();
    while (iter.hasNext()) {
      logger = (LoggerImpl)iter.next();
      logger.close();
    }
    rootSiblingLoggers.clear();
    
    elements = serviceSiblingLoggers.values();
    iter = elements.iterator();
    while (iter.hasNext()) {
      logger = (LoggerImpl)iter.next();
      logger.close();
    }
    serviceSiblingLoggers.clear();
    
    bc = null;
  }
  
}
