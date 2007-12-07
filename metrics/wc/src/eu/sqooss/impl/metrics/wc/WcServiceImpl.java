package eu.sqooss.impl.metrics.wc;

import java.util.Date;

import org.osgi.framework.BundleContext;

import eu.sqooss.service.abstractmetric.AbstractMetric;

public class WcServiceImpl extends AbstractMetric {
    private static final long serialVersionUID = 1L;

    public WcServiceImpl(BundleContext bc)  {
    	super(bc);
    }

	@Override
	public Date getDateInstalled() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean install() {
		// TODO Auto-generated method stub
		return false;
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
