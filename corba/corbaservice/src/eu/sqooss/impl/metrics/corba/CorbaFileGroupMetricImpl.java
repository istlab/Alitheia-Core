package eu.sqooss.impl.metrics.corba;

import java.util.List;

import org.osgi.framework.BundleContext;

import eu.sqooss.impl.service.corba.alitheia.FileGroupMetric;
import eu.sqooss.impl.service.corba.alitheia.db.DAObject;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.FileGroup;
import eu.sqooss.service.db.Metric;

/**
 * Wrapper class to import a FileGroup metric from the Corba ORB.
 * @author Christoph Schleifenbaum, KDAB
 */
public class CorbaFileGroupMetricImpl extends CorbaMetricImpl implements eu.sqooss.service.abstractmetric.FileGroupMetric {

    private FileGroupMetric metric;

	public CorbaFileGroupMetricImpl(BundleContext bc, FileGroupMetric m) {
		super(bc, m);
		super.addActivationType(FileGroup.class);
        metric = m;
	}

	/**
	 * {@inheritDoc}
	 */
	public void run(FileGroup a) {
        metric.doRun(DAObject.toCorbaObject(a));
    }

	/**
	 * {@inheritDoc}
	 */
	public List<ResultEntry> getResult(FileGroup a, Metric m) {
		// TODO Auto-generated method stub
		return null;
	}
}
