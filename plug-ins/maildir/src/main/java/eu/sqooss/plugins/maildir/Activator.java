/*
 * Add a nice copyright message here. Since plug-ins are not linked at
 * compile time to the source, any OSS license (even GPL) will do.
 */

package eu.sqooss.plugins.maildir;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.UpdaterService;
import eu.sqooss.service.updater.UpdaterService.UpdaterStage;

public class Activator implements BundleActivator {

    public void start(BundleContext bc) throws Exception {
        /* 
         * List of data protocols implemented by this plug-in
         * Only one accessor implementation per protocol is permitted.
         */
        String[] protocols = {"maildir"};
        
        /*
         * Register the plug-in accessor to the TDS service 
         */
        TDSService tds = AlitheiaCore.getInstance().getTDSService();
        tds.registerPlugin(protocols, MailDirAccessor.class);
        
        UpdaterStage[] stages = {UpdaterStage.IMPORT};
        UpdaterService us = AlitheiaCore.getInstance().getUpdater();
        
        us.registerUpdaterService(MailDirUpdater.class);
    }

    public void stop(BundleContext context) throws Exception {
        UpdaterService us = AlitheiaCore.getInstance().getUpdater();
        us.unregisterUpdaterService(MailDirUpdater.class);
        
        TDSService tds = AlitheiaCore.getInstance().getTDSService();
        tds.unregisterPlugin(MailDirAccessor.class);
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
