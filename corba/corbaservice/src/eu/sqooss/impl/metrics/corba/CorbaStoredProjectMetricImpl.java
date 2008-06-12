package eu.sqooss.impl.metrics.corba;

import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.BundleContext;

import eu.sqooss.impl.service.corba.alitheia.StoredProjectMetric;
import eu.sqooss.impl.service.corba.alitheia.db.DAObject;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.StoredProject;

/**
 * Wrapper class to import a StoredProject metric from the Corba ORB.
 * @author Christoph Schleifenbaum, KDAB
 */

public class CorbaStoredProjectMetricImpl extends CorbaMetricImpl implements eu.sqooss.service.abstractmetric.StoredProjectMetric {

    private StoredProjectMetric metric;

	public CorbaStoredProjectMetricImpl(BundleContext bc, StoredProjectMetric m) {
		super(bc, m);
		super.addActivationType(StoredProject.class);
        metric = m;
	}

	/**
	 * {@inheritDoc}
	 */
	public void run(StoredProject a) {
	    metric.doRun(DAObject.toCorbaObject(a));
	}

	/**
	 * {@inheritDoc}
	 */
	public List<ResultEntry> getResult(StoredProject a, Metric m) {
		List<ResultEntry> result = new LinkedList<ResultEntry>();
		metric.doGetResult(DAObject.toCorbaObject(a), DAObject.toCorbaObject(m));
		return result;
	}

}
