package eu.sqooss.plugins.svn;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.updater.UpdaterService;
import eu.sqooss.service.updater.UpdaterService.UpdaterStage;

public class Activator implements BundleActivator {
    
    public void start(BundleContext bc) throws Exception {
        String[] protocols = {"svn", "svn-http", "svn-https"};
        UpdaterStage[] stages = {UpdaterStage.IMPORT};
        UpdaterService us = AlitheiaCore.getInstance().getUpdater();
        
        us.registerUpdaterService(protocols, stages, SVNUpdaterImpl.class);
    }
  
    public void stop(BundleContext bc) throws Exception {
        UpdaterService us = AlitheiaCore.getInstance().getUpdater();
        us.unregisterUpdaterService(SVNUpdaterImpl.class);
    }
}