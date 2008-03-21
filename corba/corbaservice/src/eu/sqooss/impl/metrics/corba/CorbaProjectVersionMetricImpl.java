package eu.sqooss.impl.metrics.corba;

import org.osgi.framework.BundleContext;

import eu.sqooss.impl.service.corba.alitheia.db.DAObject;
import eu.sqooss.impl.service.corba.alitheia.ProjectVersionMetric;
import eu.sqooss.lib.result.Result;
import eu.sqooss.service.db.ProjectVersion;

public class CorbaProjectVersionMetricImpl extends CorbaMetricImpl implements eu.sqooss.service.abstractmetric.ProjectVersionMetric {

    private ProjectVersionMetric metric;

    public CorbaProjectVersionMetricImpl(BundleContext bc, ProjectVersionMetric m) {
        super(bc, m);
        metric = m;
    }

	public Result getResult(ProjectVersion a) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean run(ProjectVersion a, ProjectVersion b) {
	    return metric.run2nd(DAObject.toCorbaObject(a), DAObject.toCorbaObject(b));
    }

	public void run(ProjectVersion v) {
	    metric.run(DAObject.toCorbaObject(v));	
	}
}
