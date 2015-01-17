package eu.sqooss.plugins.javaparser;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;

import eu.sqooss.service.updater.UpdaterService;

public class Activator implements BundleActivator {

    public void start(BundleContext bc) throws Exception {
        
        /*
         * Register the plug-in to the updater service
         */
        UpdaterService us = AlitheiaCore.getInstance().getUpdater();
        us.registerUpdaterService(JavaUpdater.class);
    }

    public void stop(BundleContext context) throws Exception {
        UpdaterService us = AlitheiaCore.getInstance().getUpdater();
        us.unregisterUpdaterService(JavaUpdater.class);
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
