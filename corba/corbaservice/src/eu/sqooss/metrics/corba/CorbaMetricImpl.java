package eu.sqooss.impl.service.alitheia.metric;

import org.osgi.framework.BundleContext;

import eu.sqooss.impl.service.alitheia.Metric;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.lib.result.Result;
import eu.sqooss.service.db.DAObject;

public class CorbaMetricImpl extends AbstractMetric {

	public CorbaMetricImpl(BundleContext bc, Metric m) {
		super(bc);
		// TODO Auto-generated constructor stub
	}

	public String getAuthor() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result getResult(DAObject o) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update() {
		// TODO Auto-generated method stub
		return false;
	}

}
