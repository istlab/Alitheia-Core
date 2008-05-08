package eu.sqooss.impl.service.corba.alitheia.scheduler;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.metrics.corba.CorbaMetricImpl;
import eu.sqooss.impl.service.CorbaActivator;
import eu.sqooss.impl.service.corba.alitheia.Job;
import eu.sqooss.impl.service.corba.alitheia.JobHelper;
import eu.sqooss.impl.service.corba.alitheia.SchedulerPOA;
import eu.sqooss.impl.service.corba.alitheia.job.CorbaJobImpl;
import eu.sqooss.service.scheduler.Scheduler;

public class SchedulerImpl extends SchedulerPOA {

    BundleContext bc = null;
    Scheduler scheduler = null;
    AlitheiaCore core = null;

    Map< String, CorbaJobImpl > registeredJobs = null;
    Map< String, CorbaMetricImpl > registeredMetrics = null;
    
    public SchedulerImpl(BundleContext bc) {
        this.bc = bc;
        registeredJobs = new HashMap< String, CorbaJobImpl >();
        registeredMetrics = new HashMap< String, CorbaMetricImpl >();

        ServiceReference serviceRef = bc.getServiceReference(AlitheiaCore.class.getName());
        core = (AlitheiaCore)bc.getService(serviceRef);
        scheduler = core.getScheduler();
    }

    public boolean isExecuting() {
        return scheduler.isExecuting();
    }

    public void startExecute(int n) {
        scheduler.startExecute(n);
    }

    public void stopExecute() {
        scheduler.stopExecute();
    }

    /**
     * Registers a job in the Alitheia Scheduler system.
     * @param name The name of the object within the ORB.
     * @return An ID.
     */
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

    /**
     * Unregisters a job previously registered with registerJob.
     * @param name The name of the job within the ORB.
     */
    public void unregisterJob(String name) {
        CorbaJobImpl j = registeredJobs.get(name);
        registeredJobs.remove(name);
        if (j!=null)
        {
            j.invalidate();
        }
    }

    /**
     * Enqueues a job previously registered with registerJob in the scheduler.
     * @param name The name of the job within the ORB.
     */
    public void enqueueJob(String name) {
        registeredJobs.get(name).enqueue();
    }

    /**
     * Marks a job being dependent on another job.
     * @param job The name of the job which is dependent.
     * @param dependency The name ob the job \a job depends on.
     */
    public void addJobDependency(String job, String dependency) {
        try {
            registeredJobs.get(job).addDependency(registeredJobs.get(dependency));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Waits for a job to finish.
     * @param job The name of the job to wait for.
     */
    public void waitForJobFinished(String job) {
        try {
            registeredJobs.get(job).waitForFinished();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
