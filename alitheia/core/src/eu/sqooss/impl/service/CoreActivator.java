package eu.sqooss.impl.service;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DBService;

public class CoreActivator implements BundleActivator {

    /** Keeps the <code>AlitheaCore</code> instance. */
    private AlitheiaCore core;
    
    /** Keeps the <code>AlitheaCore</code>'s service registration instance. */
    private ServiceRegistration sregCore;

    /* (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext bc) throws Exception {
        // Create an AlitheiaCore instance and register it as a service
        core = new AlitheiaCore(bc);
        bc.registerService(AlitheiaCore.class.getName(), core, null);
        // Initialize the AlitheiaCore instance
        core.init();
    }

    /* (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext bc) throws Exception {
       if (sregCore != null) {
           sregCore.unregister();
       }
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
