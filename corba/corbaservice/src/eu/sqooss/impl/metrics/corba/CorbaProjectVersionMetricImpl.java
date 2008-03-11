package eu.sqooss.impl.metrics.corba;

import org.osgi.framework.BundleContext;

import eu.sqooss.impl.service.corba.alitheia.ProjectVersionMetric;
import eu.sqooss.lib.result.Result;
import eu.sqooss.service.db.ProjectVersion;

public class CorbaProjectVersionMetricImpl extends CorbaMetricImpl implements eu.sqooss.service.abstractmetric.ProjectVersionMetric {

    public CorbaProjectVersionMetricImpl(BundleContext bc, ProjectVersionMetric m) {
        super(bc, m);
    }

	public Result getResult(ProjectVersion a) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean run(ProjectVersion a, ProjectVersion b) {
		// TODO Auto-generated method stub
		return false;
	}

	public void run(ProjectVersion v) {
		// TODO Auto-generated method stub
		
	}

}
