package eu.sqooss.impl.service.alitheia.logger;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.alitheia.LoggerPOA;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;

import java.util.HashMap;
import java.util.Map;

public class LoggerImpl extends LoggerPOA {

	private LogManager logManager;
	
	private Map<String,Logger> loggers;

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
	
	public void debug(String name, String message) {
		logger(name).debug(message);
	}

	public void error(String name, String message) {
		logger(name).error(message);
	}

	public void info(String name, String message) {
		logger(name).info(message);
	}

	public void warn(String name, String message) {
		logger(name).warn(message);
	}

}
