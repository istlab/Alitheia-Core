package eu.sqooss.service.logging;

import eu.sqooss.impl.service.logging.LogManagerImpl;

/**
 * The log manager is the entry point to the SQO-OSS loggers. It creates and releases all the loggers.
 * The log manager creates and releases the loggers with
 * the <code>createLogger</code> and <code>releaseLogger</code> method respectively.
 * The manager is accessible as OSGi service and from the <code>LogManager.getInstance()</code> method.
 * The both methods return a same log manager. The following code prints <code>true</code>:
 * <p>
 * <code>
 * &nbsp ServiceReference logManagerServiceRef = bundleContext.getServiceReference(LogManager.class.getName()); <br>
 * &nbsp LogManager logManager = (LogManager)bundleContext.getService(logManagerServiceRef); <br>
 * &nbsp System.out.println(logManager == LogManager.getInstance()); <br>
 * </code>
 * </p>
 */
public abstract class LogManager {

    /**
     * Creates a new logger if doesn't exist, otherwise returns a existent logger.
     * For example the following code prints <code>true</code>.
     * <p>
     * <code>
     * &nbsp Logger firstLogger = logManager.createLogger(Logger.NAME_SQOOSS); <br>
     * &nbsp Logger secondLogger = logManager.createLogger(Logger.NAME_SQOOSS); <br>
     * &nbsp System.out.println(firstLogger == secondLogger); <br>
     * &nbsp logManager.releaseLogger(firstLogger.getName()); <br>
     * &nbsp logManager.releaseLogger(secondLogger.getName()); <br>
     * </code>
     * </p>
     *  
     * @param name The name of the logger. Some loggers have a "well known" names.
     * They are added as constants in the <code>Logger</code> class (see the example above).
     * 
     * @return a logger
     * 
     * @exception IllegalArgumentException - If the name is not valid logger name.
     * The valid names are constants from the <code>Logger</code> class and the logger names for the metric plug-ins.
     */
    public abstract Logger createLogger(String name);

    /**
     * Releases the logger with specified name.
     * If the logger is no longer used then the logger must be released.
     * (see the example from the <code>createLogger</code> method)    
     * @param name The logger name.
     */
    public abstract void releaseLogger(String name);

    /**
     * The log manager is registered as OSGi service.
     * The another way to get a log manager is <code>getInstance</code> method.
     * The both methods are equivalent. (see the example from the class)
     * If the logger bundle is not started then a NullPointerException exception is thrown with a appropriate message.
     * 
     * @return the log manager
     * 
     * @exception NullPointerException If the logger bundle is not started. 
     */
    public static final LogManager getInstance() {
        if (LogManagerImpl.logManager.getBundleContext() == null) {
            throw new NullPointerException("The logging bundle is not started!");
        }
        return LogManagerImpl.logManager;
    }
}
