package eu.sqooss.impl.metrics.corba;

import org.osgi.framework.BundleContext;

import eu.sqooss.impl.service.corba.alitheia.ProjectFileMetric;
import eu.sqooss.lib.result.Result;
import eu.sqooss.service.db.ProjectFile;

public class CorbaProjectFileMetricImpl extends CorbaMetricImpl implements eu.sqooss.service.abstractmetric.ProjectFileMetric {

	public CorbaProjectFileMetricImpl(BundleContext bc, ProjectFileMetric m) {
		super(bc, m);
	}

	public Result getResult(ProjectFile a) {
		// TODO Auto-generated method stub
		return null;
	}

	public void run(ProjectFile a) {
		// TODO Auto-generated method stub
		
	}

}
