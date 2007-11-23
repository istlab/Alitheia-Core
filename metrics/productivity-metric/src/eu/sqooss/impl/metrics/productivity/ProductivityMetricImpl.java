package eu.sqooss.impl.metrics.productivity;

import java.util.Date;

import org.osgi.framework.BundleContext;

import eu.sqooss.metrics.abstractmetric.AbstractMetric;
import eu.sqooss.metrics.abstractmetric.MetricResult;
import eu.sqooss.metrics.productivity.ProductivityMetric;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;

public class ProductivityMetricImpl extends AbstractMetric implements
	ProductivityMetric {
    private static final long serialVersionUID = 1L;


    public ProductivityMetricImpl(BundleContext bc) {
	super(bc);
    }

    public boolean install() {
	return false;
    }

    public boolean remove() {
	return false;
    }
    
    public boolean update() {
	return false;
    }

    public boolean delete(StoredProject a) {
	return false;
    }

    public MetricResult getResult(StoredProject a) {
	return null;
    }

    public boolean run(StoredProject a) {
	return false;
    }

    public boolean delete(ProjectVersion a) {
	return false;
    }

    public MetricResult getResult(ProjectVersion a) {
	return null;
    }

    public boolean run(ProjectVersion a, ProjectVersion b) {
	return false;
    }

    public Date getDateInstalled() {
	return null;
    }
}
