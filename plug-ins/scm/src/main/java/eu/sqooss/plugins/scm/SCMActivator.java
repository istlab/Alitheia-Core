package eu.sqooss.plugins.scm;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.plugins.tds.scm.SCMAccessor;
import eu.sqooss.plugins.updater.scm.SCMUpdater;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.UpdaterService;

public abstract class SCMActivator implements BundleActivator {
	
	public final void start(BundleContext bc) {
		String[] protocols = getProtocols();
        UpdaterService us = getAlitheiaCoreInstance().getUpdater();
        
        us.registerUpdaterService(getUpdaterClass());
        
        TDSService tds = getAlitheiaCoreInstance().getTDSService();
        tds.registerPlugin(protocols, getAccessorClass());
	}
	
	public final void stop(BundleContext bc) throws Exception {
        UpdaterService us = getAlitheiaCoreInstance().getUpdater();
        us.unregisterUpdaterService(getUpdaterClass());
        
        TDSService tds = getAlitheiaCoreInstance().getTDSService();
        tds.unregisterPlugin(getAccessorClass());
    }
	
	protected AlitheiaCore getAlitheiaCoreInstance() {
		return AlitheiaCore.getInstance();
	}
	
	protected abstract String[] getProtocols();
	
	protected abstract Class<? extends SCMUpdater> getUpdaterClass();
	
	protected abstract Class<? extends SCMAccessor> getAccessorClass();
}
