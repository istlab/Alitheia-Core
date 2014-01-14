package eu.sqooss.rest.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.rest.api.wrappers.JaxbString;
import eu.sqooss.rest.api.wrappers.JaxbMapEntry;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerStats;
import eu.sqooss.service.scheduler.WorkerThread;

@Path("/api/scheduler/")
public class SchedulerResource {
	
	private Scheduler scheduler;
	
	public SchedulerResource() {
		scheduler = AlitheiaCore.getInstance().getScheduler();
	}
	
	@GET
    @Produces({"application/xml", "application/json"})
	@Path("stats")
	public SchedulerStats getSchedulerStats() {
		return scheduler.getSchedulerStats();
	}

	@GET
    @Produces({"application/xml", "application/json"})
	@Path("stats/jobtypes/failed")
	public List<JaxbMapEntry<String, Integer>> getFailedJobTypes() {
		
		List<JaxbMapEntry<String, Integer>> l = new ArrayList<JaxbMapEntry<String, Integer>>();

		Map<String, Integer> map = scheduler.getSchedulerStats().getFailedJobTypes();
		Set<String> keySet = map.keySet();
		
		for ( String k :  keySet )
			l.add(new JaxbMapEntry<String, Integer>(k, map.get(k)));
		
        //TODO remove these
		l.add(new JaxbMapEntry<String, Integer>("test1", 1));
		l.add(new JaxbMapEntry<String, Integer>("test2", 2));
		
		return l;
		
	}

	@GET
    @Produces({"application/xml", "application/json"})
	@Path("stats/jobtypes/waiting")
	public List<JaxbMapEntry<String, Integer>> getWaitingJobTypes() {
		List<JaxbMapEntry<String, Integer>> l = new ArrayList<JaxbMapEntry<String, Integer>>();

		Map<String, Integer> map = scheduler.getSchedulerStats().getWaitingJobTypes();
		Set<String> keySet = map.keySet();
		
		for ( String k :  keySet )
			l.add(new JaxbMapEntry<String, Integer>(k, map.get(k)));
		
        //TODO remove these
		l.add(new JaxbMapEntry<String, Integer>("test1", 1));
		l.add(new JaxbMapEntry<String, Integer>("test2", 2));
		
		return l;
		
	}
	
	@GET
    @Produces({"application/xml", "application/json"})
	@Path("stats/jobs/run")
	public List<JaxbString> getRunJobs() {
		
		List<JaxbString> l = new ArrayList<JaxbString>();
        List<String> rjobs = scheduler.getSchedulerStats().getRunJobs();
        
        for (String s : rjobs)
        	l.add(new JaxbString(s));
        
		l.add(new JaxbString("test1"));
		l.add(new JaxbString("test2"));
		
		return l;
		
	}
	
	@GET
    @Produces({"application/xml", "application/json"})
	@Path("failed_queue")
	public List<Job> getFailedQueue() {
		
		Job[] jobs = scheduler.getFailedQueue();

		List<Job> l = new ArrayList<Job>();
		for (Job j : jobs)
			if(j != null)
				l.add(j);
		
		return l;
		
	}
	
}
