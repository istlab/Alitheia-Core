/*
 * Add a nice copyright message here. Since plug-ins are not linked at
 * compile time to the source, any OSS license (even GPL) will do.
 */

package eu.sqooss.plugins.bugzilla;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.UpdaterService;
import eu.sqooss.service.updater.UpdaterService.UpdaterStage;

/*
 * An Alitheia Core plug-in extends the TDS and updater services
 * to new data formats. A plug-in does not necessarily need to
 * extend both services at the same time. 
 */
public class Activator implements BundleActivator {

    public void start(BundleContext bc) throws Exception {
        /* 
         * List of data protocols implemented by this plug-in
         * Only one accessor implementation per protocol is permitted.
         */
        String[] protocols = {"bugzilla-xml"};
        
        /*
         * Register the plug-in accessor to the TDS service 
         */
        TDSService tds = AlitheiaCore.getInstance().getTDSService();
        tds.registerPlugin(protocols, BugzillaXMLParser.class);
        
        /*
         * Register the plug-in to the updater service
         */
        UpdaterStage[] stages = {UpdaterStage.IMPORT};
        UpdaterService us = AlitheiaCore.getInstance().getUpdater();
        
        us.registerUpdaterService(protocols, stages, BugzillaUpdater.class);
    }

    public void stop(BundleContext context) throws Exception {
        UpdaterService us = AlitheiaCore.getInstance().getUpdater();
        us.unregisterUpdaterService(BugzillaUpdater.class);
        
        TDSService tds = AlitheiaCore.getInstance().getTDSService();
        tds.unregisterPlugin(BugzillaXMLParser.class);
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
