package eu.sqooss.impl.metrics.corba;

import java.util.List;

import org.osgi.framework.BundleContext;

import eu.sqooss.impl.service.corba.alitheia.ProjectFile;
import eu.sqooss.impl.service.corba.alitheia.ProjectFileMetric;
import eu.sqooss.impl.service.corba.alitheia.db.DAObject;
import eu.sqooss.service.abstractmetric.ResultEntry;
import eu.sqooss.service.db.Metric;

/**
 * Wrapper class to import a ProjectFile metric from the Corba ORB.
 * @author Christoph Schleifenbaum, KDAB
 */
public class CorbaProjectFileMetricImpl extends CorbaMetricImpl implements eu.sqooss.service.abstractmetric.ProjectFileMetric {

    private ProjectFileMetric metric;

    public CorbaProjectFileMetricImpl(BundleContext bc, ProjectFileMetric m) {
        super(bc, m);
        super.addActivationType(eu.sqooss.service.db.ProjectFile.class);
        metric = m;
    }

    /**
     * {@inheritDoc}
     */
    public void run(eu.sqooss.service.db.ProjectFile a) {
        ProjectFile file = DAObject.toCorbaObject(a);
        metric.run(file);
    }

    /**
     * {@inheritDoc}
     */
	public List<ResultEntry> getResult(eu.sqooss.service.db.ProjectFile a,
			Metric m) {
		// TODO Auto-generated method stub
		return null;
	}
}
