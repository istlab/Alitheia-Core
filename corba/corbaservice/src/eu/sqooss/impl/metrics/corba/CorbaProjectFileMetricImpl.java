package eu.sqooss.impl.metrics.corba;

import org.osgi.framework.BundleContext;

import eu.sqooss.impl.service.corba.alitheia.db.DAObject;
import eu.sqooss.impl.service.corba.alitheia.ProjectFileMetric;
import eu.sqooss.impl.service.corba.alitheia.ProjectFile;
import eu.sqooss.lib.result.Result;

public class CorbaProjectFileMetricImpl extends CorbaMetricImpl implements eu.sqooss.service.abstractmetric.ProjectFileMetric {

    private ProjectFileMetric metric;

    public CorbaProjectFileMetricImpl(BundleContext bc, ProjectFileMetric m) {
        super(bc, m);
        metric = m;
    }

    public Result getResult(eu.sqooss.service.db.ProjectFile a) {
        // TODO Auto-generated method stub
        return null;
    }

    public void run(eu.sqooss.service.db.ProjectFile a) {
        ProjectFile file = DAObject.toCorbaObject(a);
        metric.run(file);
    }
}
