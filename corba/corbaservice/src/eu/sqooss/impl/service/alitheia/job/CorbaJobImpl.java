package eu.sqooss.impl.service.alitheia.job;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.scheduler.Job;

public class CorbaJobImpl extends Job {

	private eu.sqooss.impl.service.alitheia.Job j;
	
	public CorbaJobImpl(BundleContext bc, eu.sqooss.impl.service.alitheia.Job j)
	{
		this.j = j;
		ServiceReference serviceRef = bc.getServiceReference(AlitheiaCore.class.getName());
        AlitheiaCore core = (AlitheiaCore) bc.getService(serviceRef);
        try {
        	if (!core.getScheduler().isExecuting()) {
        		core.getScheduler().startExecute(5);
        	}
			core.getScheduler().enqueue(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

}
