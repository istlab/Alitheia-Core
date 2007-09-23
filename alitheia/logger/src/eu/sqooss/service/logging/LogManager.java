package eu.sqooss.service.logging;

import eu.sqooss.impl.service.logging.LogManagerImpl;

/**
 * The <code>LogManager</code> creates and releases the loggers.
 * The <code>getInstance</code> method returns a instance of this service.    
 */
public abstract class LogManager {
  
  /**
   * Creates a new logger if doesn't exist, otherwise returns a existent logger. 
   * @param name the name of the logger
   * @return logger
   * @exception IllegalArgumentException - if the name is not valid logger name
   */
  public abstract Logger createLogger(String name);
  
  /**
   * Releases the logger. 
   * @param name
   * @exception NullPointerException - if the name is null
   */
  public abstract void releaseLogger(String name);
  
  /**
   * Returns a instance of the LogManager.
   * @return log manager
   */
  public static final LogManager getInstance() {
    if (LogManagerImpl.logManager.getBundleContext() == null) {
      throw new NullPointerException("The logging bundle is not started!");
    }
    return LogManagerImpl.logManager;
  }
}
