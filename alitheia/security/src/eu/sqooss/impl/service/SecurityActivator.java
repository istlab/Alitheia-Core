package eu.sqooss.impl.service;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import eu.sqooss.impl.service.security.SecurityManagerImpl;
import eu.sqooss.service.security.SecurityManager;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;

/**
 * The class is used to start and stop the security bundle.
 */
public class SecurityActivator implements BundleActivator, ServiceListener {

    public static final int LOGGING_INFO_LEVEL    = 0;
    public static final int LOGGING_CONFIG_LEVEL  = 1;
    public static final int LOGGING_WARNING_LEVEL = 2;
    public static final int LOGGING_SEVERE_LEVEL  = 3;

    private BundleContext bc;
    private ServiceRegistration securityServiceReg;
    private ServiceReference logManagerServiceRef;
    private LogManager logManager;
    private static Logger logger;

    private static Object lockObject = new Object();

    /**
     * Registers a <code>SecurityManager</code> service.
     */
    public void start(BundleContext bc) throws Exception {
        this.bc = bc;

        initializeLogger();

        String filter = "(" + Constants.OBJECTCLASS + "=" + LogManager.class.getName() + ")";
        bc.addServiceListener(this, filter);

        //registers the security service
        securityServiceReg = bc.registerService(SecurityManager.class.getName(), new SecurityManagerImpl(), null);

        SecurityActivator.log("The security bundle is started!", SecurityActivator.LOGGING_INFO_LEVEL);
    }

    /**
     * Unregisters a <code>SecurityManager</code> service.
     */
    public void stop(BundleContext bc) throws Exception {
        bc.removeServiceListener(this);

        if (securityServiceReg != null) {
            securityServiceReg.unregister();
            SecurityActivator.log("The security service is unregistered!", SecurityActivator.LOGGING_INFO_LEVEL);
        }
        SecurityActivator.log("The security bundle is stopped!", SecurityActivator.LOGGING_INFO_LEVEL);

        removeLogger();
    }

    /**
     * This method logs the messages from the specified logging level.
     * @param message the text
     * @param level the logging level
     */
    public static void log(String message, int level) {
        synchronized (lockObject) {
            if (logger != null) {
                switch (level) {
                case SecurityActivator.LOGGING_INFO_LEVEL: logger.info(message); break;
                case SecurityActivator.LOGGING_CONFIG_LEVEL: logger.config(message); break;
                case SecurityActivator.LOGGING_WARNING_LEVEL: logger.warning(message); break;
                case SecurityActivator.LOGGING_SEVERE_LEVEL: logger.severe(message); break;
                default: logger.info(message); break;
                }
            }
        }
    }

    /**
     * @see org.osgi.framework.ServiceListener#serviceChanged(ServiceEvent)
     */
    public void serviceChanged(ServiceEvent event) {
        int eventType = event.getType();
        if ((ServiceEvent.REGISTERED == eventType) ||
                (ServiceEvent.MODIFIED == eventType)) {
            initializeLogger();
        } else if (ServiceEvent.UNREGISTERING == eventType) {
            removeLogger();
        }
    }

    /**
     * Gets the logger
     */
    private void initializeLogger() {
        synchronized (lockObject) {
            //gets the logger
            logManagerServiceRef = bc.getServiceReference(LogManager.class.getName());
            if (logManagerServiceRef != null) {
                logManager = (LogManager)bc.getService(logManagerServiceRef);
                logger = logManager.createLogger(Logger.NAME_SQOOSS_SECURITY);
            }
        }
    }

    /**
     * Ungets the logger. 
     */
    private void removeLogger() {
        synchronized (lockObject) {
            if (logManagerServiceRef != null) {
                logManager.releaseLogger(logger.getName());
                bc.ungetService(logManagerServiceRef);
                logManagerServiceRef = null;
                logger = null;
            }
        }
    }

}
