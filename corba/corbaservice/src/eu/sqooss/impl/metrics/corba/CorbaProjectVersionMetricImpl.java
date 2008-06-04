package eu.sqooss.impl.metrics.corba;

import java.util.List;

import org.osgi.framework.BundleContext;

import eu.sqooss.impl.service.corba.alitheia.ProjectVersionMetric;
import eu.sqooss.impl.service.corba.alitheia.db.DAObject;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.ProjectVersion;

/**
 * Wrapper class to import a ProjectVersion metric from the Corba ORB.
 * @author Christoph Schleifenbaum, KDAB
 */
public class CorbaProjectVersionMetricImpl extends CorbaMetricImpl implements eu.sqooss.service.abstractmetric.ProjectVersionMetric {

    private ProjectVersionMetric metric;

    public CorbaProjectVersionMetricImpl(BundleContext bc, ProjectVersionMetric m) {
        super(bc, m);
        super.addActivationType(ProjectVersion.class);
        metric = m;
    }

    /**
     * {@inheritDoc}
     */
	public void run(ProjectVersion v) {
	    metric.doRun(DAObject.toCorbaObject(v));	
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ResultEntry> getResult(ProjectVersion a, Metric m) {
		// TODO Auto-generated method stub
		return null;
	}
}
