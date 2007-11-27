package eu.sqooss.impl.service;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

import eu.sqooss.impl.service.web.services.WebServicesConstants;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.web.services.WebServices;
import eu.sqooss.service.security.SecurityManager;

/**
 * This class is used to start and stop the web services bundle. 
 */
public class WebServicesActivator implements BundleActivator, ServiceListener {
    
    public static final int LOGGING_INFO_LEVEL    = 0;
    public static final int LOGGING_CONFIG_LEVEL  = 1;
    public static final int LOGGING_WARNING_LEVEL = 2;
    public static final int LOGGING_SEVERE_LEVEL  = 3;
    
    private BundleContext bc;
    private ServiceRegistration webServicesReg;
    private ServiceReference logManagerServiceRef;
    private LogManager logManager;
    private ServiceTracker securityTracker;
    
    private static Logger logger;
    
    private static Object lockObject = new Object();
    
    /**
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext bc) throws Exception {
        this.bc = bc;
        
        String filter = "(" + Constants.OBJECTCLASS + "=" + LogManager.class.getName() + ")";
        bc.addServiceListener(this, filter);
        initializeLogger();
        
        securityTracker = new ServiceTracker(bc, SecurityManager.class.getName(), null);
        securityTracker.open();
        Object serviceObject = new WebServices(bc, securityTracker);
        Properties props = initProperties(bc);
        String serviceClass = props.getProperty(WebServicesConstants.PROPERTY_KEY_WEB_SERVICES_INTERFACE); 
        webServicesReg = bc.registerService(serviceClass, serviceObject, props);
        
        WebServicesActivator.log("The web services bundle is started!",
                WebServicesActivator.LOGGING_INFO_LEVEL);
    }

    /**
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext bc) throws Exception {
        if (webServicesReg != null) {
            webServicesReg.unregister();
        }
        
        WebServicesActivator.log("The web services bundle is stoped!",
                WebServicesActivator.LOGGING_INFO_LEVEL);
        
        securityTracker.close();
        
        bc.removeServiceListener(this);
        removeLogger();
    }
    
    /**
     * Loads the properties of the web services from the configuration file.
     * If some of the mandatory properties are missing then the method sets default properties.
     * 
     * @param bc The bundle context is used to access the configuration file.
     * 
     * @return the properties of the web services (i.e. web.service.name,
     * web.service.context and interface.class)
     * 
     */
    private Properties initProperties(BundleContext bc) {
        Bundle bundle = bc.getBundle();
        URL propsUrl = bundle.getEntry(WebServicesConstants.FILE_NAME_PROPERTIES); 
        Properties props = new Properties();
        if (propsUrl != null) {
            try {
                props.load(propsUrl.openStream());
            } catch (IOException e) {
                //uses default properties
                WebServicesActivator.log(e.getMessage(),
                        WebServicesActivator.LOGGING_WARNING_LEVEL);
            }
        }
        setDefaultPropertiesIfNeed(props);
        return props;
    }
    
    /**
     * Checks the mandatory properties.
     * 
     * @param props
     */
    private void setDefaultPropertiesIfNeed(Properties props) {
        if (props.getProperty(WebServicesConstants.PROPERTY_KEY_WEB_SERVICES_CONTEXT) == null) {
        props.setProperty(WebServicesConstants.PROPERTY_KEY_WEB_SERVICES_CONTEXT,
                WebServicesConstants.PROPERTY_VALUE_WEB_SERVICES_CONTEXT);
        }
        if (props.getProperty(WebServicesConstants.PROPERTY_KEY_WEB_SERVICES_INTERFACE) == null) {
        props.setProperty(WebServicesConstants.PROPERTY_KEY_WEB_SERVICES_INTERFACE,
                WebServicesConstants.PROPERTY_VALUE_WEB_SERVICES_INTERFACE);
        }
        if (props.getProperty(WebServicesConstants.PROPERTY_KEY_WEB_SERVICES_NAME) == null) {
        props.setProperty(WebServicesConstants.PROPERTY_KEY_WEB_SERVICES_NAME,
                WebServicesConstants.PROPERTY_VALUE_WEB_SERVICES_NAME);
        }
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
                case WebServicesActivator.LOGGING_INFO_LEVEL: logger.info(message); break;
                case WebServicesActivator.LOGGING_CONFIG_LEVEL: logger.config(message); break;
                case WebServicesActivator.LOGGING_WARNING_LEVEL: logger.warning(message); break;
                case WebServicesActivator.LOGGING_SEVERE_LEVEL: logger.severe(message); break;
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
            logManagerServiceRef = bc.getServiceReference(LogManager.class.getName());
            if (logManagerServiceRef != null) {
                logManager = (LogManager)bc.getService(logManagerServiceRef);
                logger = logManager.createLogger(Logger.NAME_SQOOSS_WEB_SERVICES);
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
