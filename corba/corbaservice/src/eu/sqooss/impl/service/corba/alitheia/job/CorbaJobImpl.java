package eu.sqooss.impl.service.corba.alitheia.job;

import org.omg.CORBA.COMM_FAILURE;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.corba.alitheia.JobPackage.JobState;
import eu.sqooss.service.scheduler.Job;

public class CorbaJobImpl extends Job {

	private eu.sqooss.impl.service.corba.alitheia.Job j;
    
	private AlitheiaCore core;
    	
	public CorbaJobImpl(BundleContext bc, eu.sqooss.impl.service.corba.alitheia.Job j)
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
		try{
			if (j==null || j._non_existent()) {
				return 0xffff;
			}
			return j.priority();
		}
		catch( COMM_FAILURE e)
		{
			invalidate();
			return 0xffff;
		}
	}

	@Override
	protected void run() throws Exception {
		if (j==null || j._non_existent()) {
			return;
		}
		try{
			j.run();
		}
		catch(COMM_FAILURE e)
		{
			invalidate();
			throw e;
		}
	}
	
	protected void stateChanged(State state) {
		try
		{
		if (j==null || j._non_existent()) {
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
		catch(Exception e)
		{
			// this should just never fail
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

	public void invalidate() {
		j = null;
	}
}
