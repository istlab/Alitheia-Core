package eu.sqooss.impl.metrics.corba;

import org.osgi.framework.BundleContext;

import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.MetricMismatchException;
import eu.sqooss.service.db.DAObject;

abstract public class CorbaMetricImpl extends AbstractMetric {

    eu.sqooss.impl.service.corba.alitheia.AbstractMetric m;

    public CorbaMetricImpl(BundleContext bc, eu.sqooss.impl.service.corba.alitheia.AbstractMetric m) {
        super(bc);
        this.m = m;
    }

    public String getAuthor() {
        return m.getAuthor();
    }

    public String getDescription() {
        return m.getDescription();
    }

    public String getName() {
        return m.getName();
    }

    public String getVersion() {
        return m.getVersion();
    }

    public boolean install() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean remove() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean update() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void run(DAObject o) throws MetricMismatchException {
        // TODO Auto-generated method stub
        
    }
}

