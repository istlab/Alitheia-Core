package eu.sqooss.impl.service.alitheia.job;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.alitheia.JobPackage.JobState;
import eu.sqooss.service.scheduler.Job;

public class CorbaJobImpl extends Job {

	private eu.sqooss.impl.service.alitheia.Job j;
    
	private AlitheiaCore core;
    	
	public CorbaJobImpl(BundleContext bc, eu.sqooss.impl.service.alitheia.Job j)
	{
		this.j = j;
		stateChanged(state());
		ServiceReference serviceRef = bc.getServiceReference(AlitheiaCore.class.getName());
        core = (AlitheiaCore) bc.getService(serviceRef);
        if (!core.getScheduler().isExecuting()) {
       		core.getScheduler().startExecute(16);
       	}
	}
	
	@Override
	public int priority() {
		return j.priority();
	}

	@Override
	protected void run() throws Exception {
		j.run();
	}
	
	protected void stateChanged(State state) {
		if (j==null) {
			return;
		}
		switch (state) {
		case Created:
			j.setState(JobState.Created);
			break;
		case Error:
			j.setState(JobState.Error);
			break;
		case Finished:
			j.setState(JobState.Finished);
			break;
		case Queued:
			j.setState(JobState.Queued);
			break;
		case Running:
			j.setState(JobState.Running);
			break;
		}
	}
	
	public void enqueue()
	{
		try{
			core.getScheduler().enqueue(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
