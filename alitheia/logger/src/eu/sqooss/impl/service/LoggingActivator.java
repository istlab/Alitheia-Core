package eu.sqooss.impl.service;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import eu.sqooss.impl.service.logging.LogManagerImpl;
import eu.sqooss.service.logging.LogManager;

public class LoggingActivator implements BundleActivator {

  private ServiceRegistration sReg;
  
  public void start(BundleContext bc) throws Exception {
    //registers a log manager service
    LogManagerImpl.logManager.setBundleContext(bc);
    sReg = bc.registerService(LogManager.class.getName(), LogManagerImpl.logManager, null);
  }

  public void stop(BundleContext bc) throws Exception {
    //unregisters a log manager service
    if (sReg != null) {
      sReg.unregister();
    }
    LogManagerImpl.logManager.close();
  }
}
