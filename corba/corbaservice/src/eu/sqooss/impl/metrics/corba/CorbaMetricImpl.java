package eu.sqooss.impl.metrics.corba;

import java.util.Date;

import org.osgi.framework.BundleContext;

import eu.sqooss.lib.result.Result;
import eu.sqooss.service.abstractmetric.MetricMismatchException;
import eu.sqooss.service.abstractmetric.ProjectVersionMetric;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.ProjectVersion;

public class CorbaMetricImpl implements ProjectVersionMetric {

    eu.sqooss.impl.service.corba.alitheia.ProjectVersionMetric m;

    public CorbaMetricImpl(BundleContext bc, eu.sqooss.impl.service.corba.alitheia.ProjectVersionMetric m) {
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

    public Result getResult(DAObject o) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getVersion() {
        return m.getVersion();
    }

	public Result getResult(ProjectVersion a) {
		//return m.getResult(a);
		return null;
	}

	public boolean run(ProjectVersion a, ProjectVersion b) {
		// TODO Auto-generated method stub
		return false;
	}

	public void run(ProjectVersion v) {
		// TODO Auto-generated method stub
		
	}

	public Date getDateInstalled() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean install() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean remove() {
		// TODO Auto-generated method stub
		return false;
	}

	public void run(DAObject o) throws MetricMismatchException {
		// TODO Auto-generated method stub
		
	}

	public boolean update() {
		// TODO Auto-generated method stub
		return false;
	}
}

