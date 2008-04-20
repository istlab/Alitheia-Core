package eu.sqooss.impl.service.corba.alitheia.core;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.fds.FDSService;

import eu.sqooss.impl.service.CorbaActivator;
import eu.sqooss.impl.service.corba.alitheia.db.DAObject;
import eu.sqooss.impl.service.corba.alitheia.CorePOA;
import eu.sqooss.impl.service.corba.alitheia.Job;
import eu.sqooss.impl.service.corba.alitheia.JobHelper;
import eu.sqooss.impl.service.corba.alitheia.AbstractMetric;
import eu.sqooss.impl.service.corba.alitheia.AbstractMetricHelper;
import eu.sqooss.impl.service.corba.alitheia.ProjectFileMetric;
import eu.sqooss.impl.service.corba.alitheia.ProjectFileMetricHelper;
import eu.sqooss.impl.service.corba.alitheia.ProjectVersionMetric;
import eu.sqooss.impl.service.corba.alitheia.ProjectVersionMetricHelper;
import eu.sqooss.impl.service.corba.alitheia.StoredProjectMetric;
import eu.sqooss.impl.service.corba.alitheia.StoredProjectMetricHelper;
import eu.sqooss.impl.service.corba.alitheia.FileGroupMetric;
import eu.sqooss.impl.service.corba.alitheia.FileGroupMetricHelper;
import eu.sqooss.impl.service.corba.alitheia.Metric;
import eu.sqooss.impl.service.corba.alitheia.MetricTypeType;
import eu.sqooss.impl.service.corba.alitheia.ProjectFile;
import eu.sqooss.impl.service.corba.alitheia.ProjectVersion;
import eu.sqooss.impl.service.corba.alitheia.job.CorbaJobImpl;

import eu.sqooss.impl.metrics.corba.CorbaMetricImpl;
import eu.sqooss.impl.metrics.corba.CorbaProjectFileMetricImpl;
import eu.sqooss.impl.metrics.corba.CorbaProjectVersionMetricImpl;
import eu.sqooss.impl.metrics.corba.CorbaStoredProjectMetricImpl;
import eu.sqooss.impl.metrics.corba.CorbaFileGroupMetricImpl;


public class CoreImpl extends CorePOA {

    BundleContext bc = null;
    FDSService fds = null;

    Map< String, CorbaJobImpl > registeredJobs = null;
    Map< String, CorbaMetricImpl > registeredMetrics = null;
    
    public CoreImpl(BundleContext bc) {
        this.bc = bc;
        registeredJobs = new HashMap< String, CorbaJobImpl >();
        registeredMetrics = new HashMap< String, CorbaMetricImpl >();

        ServiceReference serviceRef = bc.getServiceReference(AlitheiaCore.class.getName());
        fds = ((AlitheiaCore)bc.getService(serviceRef)).getFDSService();
    }

    private static int nextId = 0;

    public synchronized int getUniqueId() {
            return ++nextId;
    }

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

    public void unregisterMetric(int id) {
        CorbaActivator.instance().unregisterExternalCorbaObject(id);
    }

    public int registerJob(String name) {
        org.omg.CORBA.Object o;
        try {
            o = CorbaActivator.instance().getExternalCorbaObject(name);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        Job j = JobHelper.narrow(o);
        CorbaJobImpl impl = new CorbaJobImpl(bc,j);
        registeredJobs.put(name, impl);
        return impl.hashCode();
    }

    public void unregisterJob(int id) {
        // TODO Auto-generated method stub
        
    }

    public void enqueueJob(String name) {
        registeredJobs.get(name).enqueue();
    }

    public void addJobDependency(String job, String dependency) {
        try {
            registeredJobs.get(job).addDependency(registeredJobs.get(dependency));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void waitForJobFinished(String job) {
        try {
            registeredJobs.get(job).waitForFinished();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  

    public int getFileContents(ProjectFile file, org.omg.CORBA.StringHolder contents) {
        byte[] content = null;
        try {
            content = fds.getFileContents(DAObject.fromCorbaObject(file));
        } catch ( Exception e ) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
        if( content == null ) {
            // return an empty string
            contents.value = new String();
            return 0;
        }
        contents.value = new String(content);
        return content.length;
    }

    public Metric[] getSupportedMetrics(String metricname) {
        CorbaMetricImpl metric = registeredMetrics.get(metricname);
        List<eu.sqooss.service.db.Metric> metrics = metric.getSupportedMetrics();
        
        Metric[] result = new Metric[metrics.size()];
        for( int i = 0; i < metrics.size(); ++i ) {
            result[ i ] = DAObject.toCorbaObject(metrics.get(i));
        }

        return result;
    }

    public boolean addSupportedMetrics(String metricname, String description, MetricTypeType type) {
        CorbaMetricImpl metric = registeredMetrics.get(metricname);
        // TODO: Verify if "metricname" can be used as "mnemonic" parameter
        return metric.doAddSupportedMetrics(description, metricname, DAObject.fromCorbaObject(type));
    }
  
    public ProjectFile[] getVersionFiles (ProjectVersion version) {
        List<eu.sqooss.service.db.ProjectFile> files = DAObject.fromCorbaObject(version).getVersionFiles();

        ProjectFile[] result = new ProjectFile[files.size()];
        for( int i = 0; i < files.size(); ++i ) {
            result[ i ] = DAObject.toCorbaObject(files.get(i));
        }

        return result;
    }
}
