package eu.sqooss.service.scheduler;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.sqooss.impl.service.scheduler.SchedulerServiceImpl;
import eu.sqooss.service.scheduler.Job.State;
import eu.sqooss.service.scheduler.SchedulerException;

public class SchedulerTests {

	static SchedulerServiceImpl sched;

	@BeforeClass
	public static void setUp() {
		sched = new SchedulerServiceImpl();
		sched.startExecute(2);
	}

	@Test
	public void testJobYield() throws SchedulerException, InterruptedException {

		TestJob j1 = new TestJob(20, "Test");
		sched.enqueue(j1);
		TestJob j2 = new TestJob(20, "Test");
		sched.enqueue(j2);
		TestJob j3 = new TestJob(20, "Test");
		sched.enqueue(j3);
		TestJob j4 = new TestJob(20, "Test");
		sched.enqueue(j4);

		assertTrue(sched.isExecuting());

		assertEquals(0, sched.getSchedulerStats().getFailedJobs());
		assertEquals(0, sched.getSchedulerStats().getFinishedJobs());
		assertEquals(4, sched.getSchedulerStats().getWaitingJobs());
		
		sched.jobStateChanged(j1, State.Running);
		sched.jobStateChanged(j2, State.Running);
		sched.jobStateChanged(j3, State.Running);
		sched.jobStateChanged(j4, State.Running);
		
	}

	@AfterClass
	public static void tearDown() {
		while (sched.getSchedulerStats().getWaitingJobs() > 0) {
			try {
				Thread.sleep(500);
				System.out.println("jobs in schedule: "
						+ sched.getSchedulerStats().getWaitingJobs());
			} catch (InterruptedException e) {
			}
		}
		sched.stopExecute();
	}
}