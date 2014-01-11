package eu.sqooss.rest.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.resteasy.plugins.providers.jaxb.JaxbMap;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.rest.api.wrappers.JaxbString;
import eu.sqooss.rest.api.wrappers.XMLMapEntry;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Scheduler;

@Path("/api/scheduler/")
public class SchedulerResource {
	
	private Scheduler scheduler;
	
	public SchedulerResource() {
		scheduler = AlitheiaCore.getInstance().getScheduler();
	}

	@GET
    @Produces({"application/xml", "application/json"})
	@Path("stats/jobtypes/failed")
	public List<XMLMapEntry<String, Integer>> getFailedJobTypes() {
		
		List<XMLMapEntry<String, Integer>> l = new ArrayList<XMLMapEntry<String, Integer>>();

		Map<String, Integer> map = scheduler.getSchedulerStats().getFailedJobTypes();
		Set<String> keySet = map.keySet();
		
		for ( String k :  keySet )
			l.add(new XMLMapEntry<String, Integer>(k, map.get(k)));
		
		return l;
		
	}

	@GET
    @Produces({"application/xml", "application/json"})
	@Path("stats/jobtypes/waiting")
	public JaxbMap getWaitingJobTypes() {
		JaxbMap jm = new JaxbMap("133", "Can", "");
		jm.addEntry("test", "val");
		
		List<XMLMapEntry<String, Integer>> l = new ArrayList<XMLMapEntry<String, Integer>>();

		Map<String, Integer> map = scheduler.getSchedulerStats().getWaitingJobTypes();
		Set<String> keySet = map.keySet();
		
		for ( String k :  keySet )
			l.add(new XMLMapEntry<String, Integer>(k, map.get(k)));
		
		return jm ;
		
	}
	
	@GET
    @Produces({"application/xml", "application/json"})
	@Path("stats/jobs/run")
	public List<JaxbString> getRunJobs() {
		
		List<JaxbString> l = new ArrayList<JaxbString>();
        List<String> rjobs = scheduler.getSchedulerStats().getRunJobs();
        
        for (String s : rjobs)
        	l.add(new JaxbString(s));
		
		return l;
		
	}
	
	@GET
    @Produces({"application/xml", "application/json"})
	@Path("queue/failed")
	public Job[] getFailedQueue() {
		
		Job[] jobs = scheduler.getFailedQueue();
		return jobs;
		
	}

}
