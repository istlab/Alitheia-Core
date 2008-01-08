package eu.sqooss.impl.service.alitheia.core;

import org.osgi.framework.BundleContext;

import eu.sqooss.impl.service.CorbaActivator;
import eu.sqooss.impl.service.alitheia.CorePOA;
import eu.sqooss.impl.service.alitheia.Metric;
import eu.sqooss.impl.service.alitheia.MetricHelper;

public class CoreImpl extends CorePOA {

	public CoreImpl(BundleContext bc) {
		
	}
	
	public int registerMetric(String name) {
		org.omg.CORBA.Object o;
		try {
			o = CorbaActivator.instance().getExternalCorbaObject(name);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		Metric m = MetricHelper.narrow(o);
		return CorbaActivator.instance().registerExternalCorbaObject(Metric.class.getName(), m);
	}

	public void unregisterMetric(int id) {
		CorbaActivator.instance().unregisterExternalCorbaObject(id);
	}

}
