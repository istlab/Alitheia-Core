package eu.sqooss.impl.service.alitheia.core;

import org.osgi.framework.BundleContext;

import eu.sqooss.impl.service.CorbaActivator;
import eu.sqooss.impl.service.alitheia.CorePOA;

public class CoreImpl extends CorePOA {

	public CoreImpl(BundleContext bc) {
		
	}
	
	public int registerMetric(String name) {
		return 0;
		// TODO Auto-generated method stub
	}

	public void unregisterMetric(int id) {
		CorbaActivator.instance().unregisterExternalCorbaObject(id);
	}

}
