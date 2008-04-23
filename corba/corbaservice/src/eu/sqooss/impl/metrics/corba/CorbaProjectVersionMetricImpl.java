package eu.sqooss.impl.metrics.corba;

import java.util.List;

import org.osgi.framework.BundleContext;

import eu.sqooss.impl.service.corba.alitheia.ProjectVersionMetric;
import eu.sqooss.impl.service.corba.alitheia.db.DAObject;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.ProjectVersion;

public class CorbaProjectVersionMetricImpl extends CorbaMetricImpl implements eu.sqooss.service.abstractmetric.ProjectVersionMetric {

    private ProjectVersionMetric metric;

    public CorbaProjectVersionMetricImpl(BundleContext bc, ProjectVersionMetric m) {
        super(bc, m);
        super.addActivationType(ProjectVersion.class);
        metric = m;
    }

	public boolean run(ProjectVersion a, ProjectVersion b) {
	    return metric.run2nd(DAObject.toCorbaObject(a), DAObject.toCorbaObject(b));
    }

	public void run(ProjectVersion v) {
	    metric.run(DAObject.toCorbaObject(v));	
	}

	public List<ResultEntry> getResult(ProjectVersion a, Metric m) {
		// TODO Auto-generated method stub
		return null;
	}
}
