package eu.sqooss.impl.metrics.corba;

import org.osgi.framework.BundleContext;

import eu.sqooss.impl.service.alitheia.Metric;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.lib.result.Result;
import eu.sqooss.service.db.DAObject;

public class CorbaMetricImpl extends AbstractMetric {

    Metric m;

    public CorbaMetricImpl(BundleContext bc, Metric m) {
        super(bc);
        this.m = m;
    }

    public String getAuthor() {
        return m.getAuthor();
    }

    public String getDescription() {
        return m.getDescription();
    }

    @Override
    public String getName() {
        return m.getName();
    }

    @Override
    public Result getResult(DAObject o) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getVersion() {
        return m.getVersion();
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
