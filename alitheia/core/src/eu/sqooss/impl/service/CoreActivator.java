package eu.sqooss.impl.service;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DBService;

public class CoreActivator implements BundleActivator {

    private static BundleContext bundleContext = null;
    private static DBService dbService = null;
    
    public void start(BundleContext bc) throws Exception {
        AlitheiaCore core = new AlitheiaCore(bc);
        bc.registerService(core.getClass().getName(), core, null);

        // Run an instance of the WebAdmin
        core.initWebAdmin();
        
        // Run an instance of the PluginAdmin
        core.initPluginAdmin();
        
        bundleContext = bc;
        dbService = core.getDBService();
    }

    public void stop(BundleContext bc) throws Exception {
       
    }
    
    public static BundleContext getBundleContext() {
        return bundleContext; 
    }
    
    public static DBService getDBService() {
        return dbService;
    }
}
