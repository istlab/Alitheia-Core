package eu.sqooss.impl.service;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import eu.sqooss.impl.service.logging.LogManagerImpl;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;

/**
 * The class is used to start and stop the logger bundle.
 */
public class LoggingActivator implements BundleActivator {

    private ServiceRegistration sReg;

    /**
     * Configures and registers a <code>LogManager</code> service.
     */
    public void start(BundleContext bc) throws Exception {
        //registers a log manager service
        LogManagerImpl.logManager.setBundleContext(bc);
        sReg = bc.registerService(LogManager.class.getName(), LogManagerImpl.logManager, null);
        Logger l = LogManagerImpl.logManager.createLogger("sqooss");
        l.setConfigurationProperty("file.name","alitheia.log");
        l.setConfigurationProperty("message.format","text/plain");
        l.info("Logging bundle started.");
    }

    /**
     * Closes and unregisters a <code>LogManager</code> service.
     */
    public void stop(BundleContext bc) throws Exception {
        //unregisters a log manager service
        if (sReg != null) {
            sReg.unregister();
        }
        LogManagerImpl.logManager.close();
    }
}
