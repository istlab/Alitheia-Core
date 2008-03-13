package eu.sqooss.impl.metrics.corba;

import org.osgi.framework.BundleContext;

import eu.sqooss.impl.service.corba.alitheia.FileGroupMetric;
import eu.sqooss.lib.result.Result;
import eu.sqooss.service.db.FileGroup;

public class CorbaFileGroupMetricImpl extends CorbaMetricImpl implements eu.sqooss.service.abstractmetric.FileGroupMetric {

    private FileGroupMetric metric;

	public CorbaFileGroupMetricImpl(BundleContext bc, FileGroupMetric m) {
		super(bc, m);
        metric = m;
	}

	public Result getResult(FileGroup a) {
		// TODO Auto-generated method stub
		return null;
	}

	public void run(FileGroup a) {
        metric.run(fromDBObject(a));
    }


}
