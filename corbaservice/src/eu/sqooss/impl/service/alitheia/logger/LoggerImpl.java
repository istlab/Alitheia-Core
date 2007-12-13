package eu.sqooss.impl.service.alitheia.logger;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.alitheia.LoggerPOA;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;

public class LoggerImpl extends LoggerPOA {

	private LogManager logManager;
	private Logger logger;
	
	public LoggerImpl(BundleContext bc) {
		ServiceReference serviceRef = bc.getServiceReference(AlitheiaCore.class.getName());
		AlitheiaCore core = (AlitheiaCore) bc.getService(serviceRef);
		if (core == null) {
			System.out.println("CORBA logger could not get the Alitheia core");
			return;
		}
		logManager = core.getLogManager();
		logger = logManager.createLogger(Logger.NAME_SQOOSS);
	}
	
	public String debug(String message) {
		logger.debug(message);
		return null;
	}

	public String error(String message) {
		logger.error(message);
		return null;
	}

	public String info(String message) {
		logger.info(message);
		return null;
	}

	public String warn(String message) {
		logger.warn(message);
		return null;
	}

}
