package com.mws.squal.impl.service.cache;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import eu.sqooss.core.AlitheiaCore;

public class Activator implements BundleActivator {

    /** Keeps the <code>AlitheaCore</code>'s service registration instance. */
    private ServiceRegistration sregCache;

    public void start(BundleContext bc) throws Exception {
        AlitheiaCore.getInstance().registerService(CacheService.class, CacheServiceImpl.class);
    }
  
    public void stop(BundleContext bc) throws Exception {
        AlitheiaCore.getInstance().unregisterService(CacheService.class);
    }
}
