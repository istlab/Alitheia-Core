package eu.sqooss.test.service.scheduler;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.scheduler.*;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Job.State;
import eu.sqooss.service.scheduler.ResumePoint;
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.service.scheduler.WorkerThread;
public class WorkerTreadImplTests {

	@BeforeClass
	public static void setUp() throws Exception {
	    	 try{
	         	AlitheiaCore.testInstance();
	         }catch(Exception e){
	        	 //Workarround to making testing work
	         }
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testRun() throws SchedulerException {
		SchedulerServiceImpl sched = new SchedulerServiceImpl();
		Job j1 = new TestJob(20, "J1");
		Job j2 = new TestJob(20, "J2");
		sched.startExecute(1);
		sched.enqueue(j1);
		sched.enqueue(j2);
		WorkerThread t1 = sched.getWorkerThreads()[0];
		while(sched.getSchedulerStats().getFinishedJobs()<1)
			try {
				Thread.sleep(100);
				if(j1.state() == State.Running){
					assertEquals("Check if t1 is running j1", j1, t1.executedJob());
				}
				if(j2.state() == State.Running){
					assertEquals("Check if t1 is running j2", j2, t1.executedJob());
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		sched.stopExecute();
	}
	@Test
	public final void testRunWithOneShotWorkerThread() throws SchedulerException {
		SchedulerServiceImpl sched = new SchedulerServiceImpl();
		Job j1 = new TestJob(20, "J1");
		Job j2 = new TestJob(20, "J2");
		sched.startExecute(0);
		sched.enqueue(j1);
		sched.enqueue(j2);
		assertEquals("No threads should exist yet", 0, sched.getWorkerThreads().length);
		sched.startOneShotWorkerThread();
		
		int timeout=1000;
		//This test ensures that multiple jobs CAN run, but only one SHOULD run.
		while(sched.getSchedulerStats().getWaitingJobs()>1 || timeout > 0){ 
			try {
				Thread.sleep(100);
				if(!(j1.state() == Job.State.Running || j2.state() == Job.State.Running)){ // Cause a timout when nothing is being processed anymore
					timeout -= 100;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		sched.stopExecute();
		sched.shutDown();
		assertEquals("Only one job should be done", 1, sched.getSchedulerStats().getFinishedJobs());
	}
	
	@Test
	public final void testWorkerThreadImplSchedulerInt() {
		//This is tested in testRun basicly...?
		//because it makes the difference between normal worker and oneshotworker
	}

	@Test
	public final void testWorkerThreadImplSchedulerBoolean() {
		//This is tested in testRunWithOneShotWorker
//		because it makes the difference between normal worker and oneshotworker
	}

	@Test
	public final void testStopProcessing() throws SchedulerException {
		SchedulerServiceImpl sched = new SchedulerServiceImpl();
		Job jj1 = new TestJob(20, "JJ1",0l);
		Job jj2 = new TestJob(20, "JJ2", 10l);
		sched.startExecute(1);
		sched.enqueue(jj1);
		sched.enqueue(jj2);
		WorkerThread t1 = sched.getWorkerThreads()[0];
		int timeout=1000;
		while(sched.getSchedulerStats().getWaitingJobs()>1 || timeout > 0) 
			try {
				Thread.sleep(10);
				if(!(jj1.state() == Job.State.Running || jj2.state() == Job.State.Running)){ // Cause a timout when nothing is being processed anymore
					timeout -= 100;
				}
				if(jj1.state() == Job.State.Finished){ //Once one job has finished shutdown the process
					t1.stopProcessing();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		sched.stopExecute();
		assertEquals("Only one job should be completed", 1, sched.getSchedulerStats().getFinishedJobs());
	}

	@Test
	public final void testExecutedJob() throws SchedulerException {
		SchedulerServiceImpl sched = new SchedulerServiceImpl();
		Job j1 = new TestJob(20, "J1");
		sched.startExecute(1);
		sched.enqueue(j1);
		WorkerThread t1 = sched.getWorkerThreads()[0];
		while(sched.getSchedulerStats().getFinishedJobs()<1)
			try {
				Thread.sleep(100);
				if(j1.state() == State.Running){
					assertEquals("Check if t1 is running j1", j1, t1.executedJob());
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		sched.stopExecute();
	}

	@Test
	//This is probably not correcet
	public final void testExecuteJob() throws SchedulerException {
		//This should test job yielding?
		// The rest is coverd in previous tests
		SchedulerServiceImpl sched = new SchedulerServiceImpl();
		Job j1 = new TestJob(20, "J1", 10l);
		Job j2 = new TestJob(20, "J2", 50l);
		sched.startExecute(0);
		sched.enqueue(j1);
		sched.enqueue(j2);
		assertEquals("No threads should exist yet", 0, sched.getWorkerThreads().length);
		sched.startOneShotWorkerThread();
		
		int timeout=1000;
		ResumePoint p = new ResumePoint() {
			
			@Override
			public void resume() {
				System.out.println("What should resume do....?");
				
			}
		};
		while(sched.getSchedulerStats().getFinishedJobs()<2){ 
			try {
				Thread.sleep(100);
				System.out.println("Finished: " + sched.getSchedulerStats().getFinishedJobs());
				System.out.println("State j1: "+j1.state());
				System.out.println("State j2: "+j2.state());
				if(j1.state()==Job.State.Running && j2.state()!=Job.State.Finished){
					sched.yield(j1, p);
				}
				if(j2.state()==Job.State.Running){
					assertEquals("While j2 is running j1 should be yielded", Job.State.Yielded, j1.state());
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		sched.stopExecute();
		sched.shutDown();
		assertEquals("Finaly both jobs should be done", 2, sched.getSchedulerStats().getFinishedJobs());
		
	}

	@Test
	public final void testTakeJob() {
		fail("Not yet implemented"); // TODO
	}

}
