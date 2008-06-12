package eu.sqooss.impl.service.corba.alitheia.core;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.metrics.corba.CorbaFileGroupMetricImpl;
import eu.sqooss.impl.metrics.corba.CorbaMetricImpl;
import eu.sqooss.impl.metrics.corba.CorbaProjectFileMetricImpl;
import eu.sqooss.impl.metrics.corba.CorbaProjectVersionMetricImpl;
import eu.sqooss.impl.metrics.corba.CorbaStoredProjectMetricImpl;
import eu.sqooss.impl.service.CorbaActivator;
import eu.sqooss.impl.service.corba.alitheia.CorePOA;
import eu.sqooss.impl.service.corba.alitheia.FileGroupMetric;
import eu.sqooss.impl.service.corba.alitheia.FileGroupMetricHelper;
import eu.sqooss.impl.service.corba.alitheia.Metric;
import eu.sqooss.impl.service.corba.alitheia.MetricTypeType;
import eu.sqooss.impl.service.corba.alitheia.ProjectFile;
import eu.sqooss.impl.service.corba.alitheia.ProjectFileMetric;
import eu.sqooss.impl.service.corba.alitheia.ProjectFileMetricHelper;
import eu.sqooss.impl.service.corba.alitheia.ProjectVersion;
import eu.sqooss.impl.service.corba.alitheia.ProjectVersionMetric;
import eu.sqooss.impl.service.corba.alitheia.ProjectVersionMetricHelper;
import eu.sqooss.impl.service.corba.alitheia.StoredProjectMetric;
import eu.sqooss.impl.service.corba.alitheia.StoredProjectMetricHelper;
import eu.sqooss.impl.service.corba.alitheia.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.fds.FDSService;

/**
 * Core of the CorbaService.
 * Handles all methods to register/unregister services within/from the Corba ORB.
 * Provides even some Service dependent misc methods.
 * @author Christoph Schleifenbaum
 */
public class CoreImpl extends CorePOA {

    BundleContext bc = null;
    FDSService fds = null;
    AlitheiaCore core = null;
    DBService db = null;
    
    Map< String, CorbaMetricImpl > registeredMetrics = null;
    
    public CoreImpl(BundleContext bc) {
        this.bc = bc;
        registeredMetrics = new ConcurrentHashMap< String, CorbaMetricImpl >();

        ServiceReference serviceRef = bc.getServiceReference(AlitheiaCore.class.getName());
        core = (AlitheiaCore)bc.getService(serviceRef);
        fds = core.getFDSService();
        db = core.getDBService();
    }

    private static int nextId = 0;

    /**
     * Creates an unique ID.
     * This ID is should be used to create unique names for the Corba ORB.
     */
    public synchronized int getUniqueId() {
    	return ++nextId;
    }

    /**
     * Registers a metric in the ORB.
     * @param name The name of the metric within the ORB.
     * @return A service ID which can be used to unregister the metric later.
     */
    public int registerMetric(String name) {
        org.omg.CORBA.Object o;
        try {
            o = CorbaActivator.instance().getExternalCorbaObject(name);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        CorbaMetricImpl wrapper = null;
        if (o._is_a(ProjectVersionMetricHelper.id()))
        {
            ProjectVersionMetric m = ProjectVersionMetricHelper.narrow(o);
            wrapper = new CorbaProjectVersionMetricImpl(bc,m);
        }
        else if (o._is_a(ProjectFileMetricHelper.id()))
        {
            ProjectFileMetric m = ProjectFileMetricHelper.narrow(o);
            wrapper = new CorbaProjectFileMetricImpl(bc,m);
        }
        else if (o._is_a(StoredProjectMetricHelper.id()))
        {
            StoredProjectMetric m = StoredProjectMetricHelper.narrow(o);
            wrapper = new CorbaStoredProjectMetricImpl(bc,m);
        }
        else if (o._is_a(FileGroupMetricHelper.id()))
        {
            FileGroupMetric m = FileGroupMetricHelper.narrow(o);
            wrapper = new CorbaFileGroupMetricImpl(bc,m);
        }
       	registeredMetrics.put(name, wrapper);

        return CorbaActivator.instance().registerExternalCorbaObject(CorbaMetricImpl.class.getName(), wrapper);
    }

    /**
     * Unregisters a metric previously registered with registerMetric.
     * @param id The id returned by registerMetric.
     */
    public void unregisterMetric(int id) {
        CorbaActivator.instance().unregisterExternalCorbaObject(id);
    }

    /**
     * Get a list of metric types supported by a special metric.
     * @param metricname The name of the metric within the Corba ORB.
     */
    public Metric[] getSupportedMetrics(String metricname) {
        CorbaMetricImpl metric = registeredMetrics.get(metricname);
        db.startDBSession();
        List<eu.sqooss.service.db.Metric> metrics = metric.getSupportedMetrics();
        
        Metric[] result = new Metric[metrics.size()];
        for( int i = 0; i < metrics.size(); ++i ) {
            result[ i ] = DAObject.toCorbaObject(metrics.get(i));
        }

        db.commitDBSession();

        return result;
    }

    /** 
     * Adds metric types supporetd by a special metric to it.
     * @param metricname The name of the metric within the Corba ORB.
     * @param description String description of the metric.
     * @param mnemoic Short description code of the metric type.
     * @param type The metric type of the supported metric.
     */
    public boolean addSupportedMetrics(String metricname, String description, String mnemonic, MetricTypeType type) {
        CorbaMetricImpl metric = registeredMetrics.get(metricname);
        return metric.doAddSupportedMetrics(description, mnemonic, DAObject.fromCorbaObject(type));
    }
  
    /**
     * Get a list of all files added/deleted/changed in \a version.
     */
    public ProjectFile[] getVersionFiles (ProjectVersion version) {
        db.startDBSession();
        Set<eu.sqooss.service.db.ProjectFile> files = DAObject.fromCorbaObject(version).getVersionFiles();
        eu.sqooss.service.db.ProjectFile[] filesArray = (eu.sqooss.service.db.ProjectFile[]) files.toArray();
        
        ProjectFile[] result = new ProjectFile[files.size()];
        for( int i = 0; i < files.size(); ++i ) {
            result[ i ] = DAObject.toCorbaObject(filesArray[ i ]);
        }

        db.commitDBSession();
        return result;
    }
}
