package eu.sqooss.service.cache;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;

public class Activator implements BundleActivator {

    public void start(BundleContext bc) throws Exception {
        AlitheiaCore.getInstance().registerService(CacheService.class, CacheServiceImpl.class);
    }
  
    public void stop(BundleContext bc) throws Exception {
        AlitheiaCore.getInstance().unregisterService(CacheService.class);
    }
}
