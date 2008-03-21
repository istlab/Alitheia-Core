package eu.sqooss.impl.metrics.corba;

import org.osgi.framework.BundleContext;

import eu.sqooss.impl.service.corba.alitheia.db.DAObject;
import eu.sqooss.impl.service.corba.alitheia.StoredProjectMetric;
import eu.sqooss.lib.result.Result;
import eu.sqooss.service.db.StoredProject;

public class CorbaStoredProjectMetricImpl extends CorbaMetricImpl implements eu.sqooss.service.abstractmetric.StoredProjectMetric {

    private StoredProjectMetric metric;

	public CorbaStoredProjectMetricImpl(BundleContext bc, StoredProjectMetric m) {
		super(bc, m);
        metric = m;
	}

	public Result getResult(StoredProject a) {
		// TODO Auto-generated method stub
		return null;
	}

	public void run(StoredProject a) {
	    metric.run(DAObject.toCorbaObject(a));
	}

}
