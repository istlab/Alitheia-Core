package eu.sqooss.impl.service.corba.alitheia.logger;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.corba.alitheia.LoggerPOA;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper class to export the Logger service into the Corba ORB.
 * @author Christoph Schleifenbaum, KDAB
 */
public class LoggerImpl extends LoggerPOA {

	private LogManager logManager;
	
	private Map<String,Logger> loggers;

	/**
	 * Provides access to the Alitheia loggers.
	 * @param name The name of the logger being used.
	 * @return The (perhaps newly created) Logger object.
	 */
	protected Logger logger(String name) {
		if (!loggers.containsKey(name)) {
			loggers.put(name, logManager.createLogger(name));
		}
		return loggers.get(name);
	}
	
	public LoggerImpl(BundleContext bc) {
		ServiceReference serviceRef = bc.getServiceReference(AlitheiaCore.class.getName());
		AlitheiaCore core = (AlitheiaCore) bc.getService(serviceRef);
		if (core == null) {
			System.out.println("CORBA logger could not get the Alitheia core");
			return;
		}
		logManager = core.getLogManager();
		loggers = new HashMap<String,Logger>();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void debug(String name, String message) {
		logger(name).debug(message);
	}

    /**
     * {@inheritDoc}
     */
	public void error(String name, String message) {
		logger(name).error(message);
	}

    /**
     * {@inheritDoc}
     */
	public void info(String name, String message) {
		logger(name).info(message);
	}

    /**
     * {@inheritDoc}
     */
	public void warn(String name, String message) {
		logger(name).warn(message);
	}

}
