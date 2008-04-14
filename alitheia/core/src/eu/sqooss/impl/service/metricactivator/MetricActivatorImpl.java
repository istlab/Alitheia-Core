package eu.sqooss.impl.service.metricactivator;

import java.util.Iterator;
import java.util.SortedSet;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.abstractmetric.MetricMismatchException;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.metricactivator.MetricActivator;

public class MetricActivatorImpl implements MetricActivator {

    private BundleContext bc;
    private AlitheiaCore core;
    private DBService dbs;
    private Logger logger;
    
    public MetricActivatorImpl(BundleContext bc, Logger logger) {
        this.bc = bc;
        ServiceReference serviceRef = null;
        serviceRef = bc.getServiceReference(AlitheiaCore.class.getName());
        core = (AlitheiaCore) bc.getService(serviceRef);
        
        this.logger = logger;
        this.dbs = core.getDBService();
    }
    
    public <T extends DAObject> void runMetrics(Class<T> clazz,
            SortedSet<Long> objectIDs) {
        ServiceReference[] metrics = null;
        
        metrics = core.getPluginManager().listMetricProviders(clazz);
        
        Iterator<Long> i = objectIDs.iterator();

        while (i.hasNext()) {
            long currentVersion = i.next().longValue();
            for (ServiceReference r : metrics) {
                eu.sqooss.service.abstractmetric.Metric m = 
                    (eu.sqooss.service.abstractmetric.Metric) core.getService(r);
                if (m != null) {
                    try {
                        m.run(dbs.findObjectById(clazz, currentVersion));
                    } catch (MetricMismatchException e) {
                        logger.warn("Metric " + m.getName() + " failed");
                    }
                }
            }
        }
    }

    public void syncMetric(Metric m, StoredProject sp) {

    }

    public <T extends DAObject> void syncMetrics(Class<T> clazz,
            StoredProject sp) {
    }

}
