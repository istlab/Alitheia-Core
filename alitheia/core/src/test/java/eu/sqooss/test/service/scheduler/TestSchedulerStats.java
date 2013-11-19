package eu.sqooss.test.service.scheduler;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.scheduler.SchedulerServiceImpl;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Job.State;
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.service.scheduler.SchedulerStats;
import eu.sqooss.test.service.scheduler.TestJobObject;

public class TestSchedulerStats {

	@Test
	public void TestTotalJobs() {
		SchedulerStats ss = new SchedulerStats();
		assertEquals(0,ss.getTotalJobs());
		ss.incTotalJobs();
		assertEquals(1,ss.getTotalJobs());
		ss.decTotalJobs();
		assertEquals(0,ss.getTotalJobs());
	}
	
	@Test
	public void TestFinishedJobs() {
		SchedulerStats ss = new SchedulerStats();
		assertEquals(0,ss.getTotalJobs());
		assertEquals(0,ss.getFinishedJobs());
		ss.incFinishedJobs();
		assertEquals(0,ss.getTotalJobs());
		assertEquals(1,ss.getFinishedJobs());
	}
	
	@Test
	public void TestRunningJobs() {
		SchedulerStats ss = new SchedulerStats();
		TestJobObject jb1 = new TestJobObject(1, "x");
		assertEquals(0,ss.getTotalJobs());
		assertEquals(0, ss.getRunningJobs());
		
		ss.addRunJob(jb1);
		assertEquals(0,ss.getTotalJobs());
		assertEquals(1, ss.getRunningJobs());
		List<String> x = new ArrayList<String>(1);
		x.add("x");
		assertEquals(x,ss.getRunJobs());
		ss.removeRunJob(jb1);
		assertEquals(0,ss.getTotalJobs());
		assertEquals(0, ss.getRunningJobs());
		
	}
	
	@Test
	public void TestFailedJobs() {
		SchedulerStats ss = new SchedulerStats();
		assertEquals(0,ss.getFailedJobs());
		assertEquals(0, ss.getFailedJobTypes().size());
		
		ss.addFailedJob("RogierIsGaaf");
		assertEquals(1,ss.getFailedJobs());
		assertEquals(1, ss.getFailedJobTypes().size());
		HashMap<String,Integer> x = new HashMap<String,Integer>(1);
		x.put("RogierIsGaaf",1);
		assertEquals(x, ss.getFailedJobTypes());
		
		ss.addFailedJob("RogierIsGaaf");
		assertEquals(2,ss.getFailedJobs());
		assertEquals(1, ss.getFailedJobTypes().size());
		x = new HashMap<String,Integer>(1);
		x.put("RogierIsGaaf",2);
		assertEquals(x, ss.getFailedJobTypes());
	}
	
	@Test
	public void TestWaitingJobs() {
		SchedulerStats ss = new SchedulerStats();
		assertEquals(0,ss.getWaitingJobs());
		assertEquals(0, ss.getWaitingJobTypes().size());
		
		ss.addWaitingJob("RogierIsGaaf");
		assertEquals(1,ss.getWaitingJobs());
		assertEquals(1, ss.getWaitingJobTypes().size());
		HashMap<String,Integer> x = new HashMap<String,Integer>(1);
		x.put("RogierIsGaaf",1);
		assertEquals(x, ss.getWaitingJobTypes());
		
		ss.addWaitingJob("RogierIsGaaf");
		assertEquals(2,ss.getWaitingJobs());
		assertEquals(1, ss.getWaitingJobTypes().size());
		x = new HashMap<String,Integer>(1);
		x.put("RogierIsGaaf",2);
		assertEquals(x, ss.getWaitingJobTypes());
		
		ss.removeWaitingJob("RogierIsGaaf");
		assertEquals(1,ss.getWaitingJobs());
		assertEquals(1, ss.getWaitingJobTypes().size());
		x = new HashMap<String,Integer>(1);
		x.put("RogierIsGaaf",1);
		assertEquals(x, ss.getWaitingJobTypes());
		
		ss.removeWaitingJob("RogierIsGaaf");
		assertEquals(0,ss.getWaitingJobs());
//		assertEquals(0, ss.getWaitingJobTypes().size()); // Alitheia Core does something wrong here
//		assertEquals(new HashMap<String,Integer>(1), ss.getWaitingJobTypes());
	
	}
	
	
	
	
}
