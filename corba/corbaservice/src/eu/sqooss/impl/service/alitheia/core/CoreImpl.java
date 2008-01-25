package eu.sqooss.impl.service.alitheia.core;

import org.osgi.framework.BundleContext;

import eu.sqooss.impl.service.CorbaActivator;
import eu.sqooss.impl.service.alitheia.CorePOA;
import eu.sqooss.impl.service.alitheia.Job;
import eu.sqooss.impl.service.alitheia.JobHelper;
import eu.sqooss.impl.service.alitheia.Metric;
import eu.sqooss.impl.service.alitheia.MetricHelper;
import eu.sqooss.impl.service.alitheia.job.CorbaJobImpl;
import eu.sqooss.impl.service.alitheia.metric.CorbaMetricImpl;

public class CoreImpl extends CorePOA {

	BundleContext bc;
	
	public CoreImpl(BundleContext bc) {
		this.bc = bc;
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
		CorbaMetricImpl impl = new CorbaMetricImpl(bc,m);
		return CorbaActivator.instance().registerExternalCorbaObject(CorbaMetricImpl.class.getName(), impl);
	}

	public void unregisterMetric(int id) {
		CorbaActivator.instance().unregisterExternalCorbaObject(id);
	}

	public int registerJob(String name) {
		org.omg.CORBA.Object o;
		try {
			o = CorbaActivator.instance().getExternalCorbaObject(name);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		Job j = JobHelper.narrow(o);
		CorbaJobImpl impl = new CorbaJobImpl(bc,j);
		return impl.hashCode();
	}

	public void unregisterJob(int id) {
		// TODO Auto-generated method stub
		
	}

}
