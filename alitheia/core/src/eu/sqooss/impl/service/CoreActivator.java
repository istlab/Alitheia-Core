package eu.sqooss.impl.service;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleActivator;

import eu.sqooss.core.AlitheiaCore;

public class CoreActivator
    implements BundleActivator{
    
    public void start(BundleContext bc) throws Exception {
        AlitheiaCore core = new AlitheiaCore(bc);
        bc.registerService(core.getClass().getName(), core, null);
        
        // Create a WebAdmin instance
        core.initWebAdmin();
    }

    public void stop(BundleContext bc) throws Exception {
       
    }
}
