package eu.sqooss.test.impl.service.scheduler;

import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.sqooss.impl.service.scheduler.SchedulerServiceImpl;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.service.scheduler.SchedulerStatsView;

@RunWith(MockitoJUnitRunner.class)
public class SchedulerServiceImplTest {
	
	private SchedulerServiceImpl scheduler;
	private SchedulerStatsView stats;
	@Mock private Job j1;
	@Mock private Job j2;
	@Mock private Logger logger;

	@Before
	public void setUp() {
		// NOTE no need to mock getClass, which is used on Job by
		// SchedulerServiceImpl
		this.scheduler = new SchedulerServiceImpl();
		this.scheduler.setInitParams(null, this.logger);
		this.scheduler.startUp();
		this.stats = this.scheduler.getSchedulerStats();
	}

	@After
	public void tearDown() {
		this.scheduler.shutDown();
	}

	@Test
	public void testEnqueue() throws SchedulerException {
		assertEquals(0, this.stats.getWaitingJobs());
		assertEquals(0, this.stats.getTotalJobs());
		assertTrue(this.stats.getWaitingJobTypes().isEmpty());
		
		this.scheduler.enqueue(j1);
		
		assertEquals(1, this.stats.getWaitingJobs());
		assertEquals(1, this.stats.getTotalJobs());
		assertEquals(new Integer(1), this.stats.getWaitingJobTypes().get(j1.getClass().toString()));
	}

	@Test
	public void testEnqueueNoDependencies() throws SchedulerException {
		assertEquals(0, this.stats.getWaitingJobs());
		assertEquals(0, this.stats.getTotalJobs());
		
		this.scheduler.enqueueNoDependencies(new HashSet<>(Arrays.asList(j1, j2)));
		
		assertEquals(2, this.stats.getWaitingJobs());
		assertEquals(2, this.stats.getTotalJobs());
	}

	@Test
	public void testEnqueueBlock() throws SchedulerException {
		assertEquals(0, this.stats.getWaitingJobs());
		assertEquals(0, this.stats.getTotalJobs());
		
		this.scheduler.enqueueBlock(Arrays.asList(j1, j2));
		
		assertEquals(2, this.stats.getWaitingJobs());
		assertEquals(2, this.stats.getTotalJobs());
	}

	@Test
	public void testDequeue() throws SchedulerException {
		this.scheduler.enqueue(j1);
		assertEquals(1, this.stats.getWaitingJobs());
		assertEquals(1, this.stats.getTotalJobs());
		
		this.scheduler.dequeue(j1);
		
		// NOTE we didn't expect this behaviour where the stats stay the same,
		// but without documentation of the original code, we expect this to
		// happen.
		assertEquals(1, this.stats.getWaitingJobs());
		assertEquals(1, this.stats.getTotalJobs());
	}

	@Test
	public void testJobStateChangedRunning() throws SchedulerException {
		this.scheduler.enqueue(j1);
		
		assertEquals(1, this.stats.getWaitingJobs());
		assertEquals(0, this.stats.getRunningJobs());
		assertEquals(1, this.stats.getTotalJobs());
		
		this.scheduler.jobStateChanged(j1, Job.State.Running);
		
		assertEquals(0, this.stats.getWaitingJobs());
		assertEquals(1, this.stats.getRunningJobs());
		assertEquals(1, this.stats.getTotalJobs());
	}

	@Test
	public void testJobStateChangedFinished() throws SchedulerException {
		this.scheduler.enqueue(j1);
		this.scheduler.jobStateChanged(j1, Job.State.Running);
		
		assertEquals(1, this.stats.getRunningJobs());
		assertEquals(0, this.stats.getFinishedJobs());
		assertEquals(1, this.stats.getTotalJobs());
		
		this.scheduler.jobStateChanged(j1, Job.State.Finished);
		
		assertEquals(0, this.stats.getRunningJobs());
		assertEquals(1, this.stats.getFinishedJobs());
		assertEquals(1, this.stats.getTotalJobs());
	}

	@Test
	public void testJobStateChangedYielded() throws SchedulerException {
		this.scheduler.enqueue(j1);
		this.scheduler.jobStateChanged(j1, Job.State.Running);
		
		assertEquals(0, this.stats.getWaitingJobs());
		assertEquals(1, this.stats.getRunningJobs());
		assertEquals(1, this.stats.getTotalJobs());
		
		this.scheduler.jobStateChanged(j1, Job.State.Yielded);
		
		assertEquals(1, this.stats.getWaitingJobs());
		assertEquals(0, this.stats.getRunningJobs());
		assertEquals(1, this.stats.getTotalJobs());
	}

	@Test
	public void testJobStateChangedError() throws SchedulerException {
		this.scheduler.enqueue(j1);
		this.scheduler.jobStateChanged(j1, Job.State.Running);
		
		assertEquals(1, this.stats.getRunningJobs());
		assertEquals(0, this.stats.getFailedJobs());
		assertEquals(1, this.stats.getTotalJobs());
		assertTrue(this.stats.getFailedJobTypes().isEmpty());
		
		this.scheduler.jobStateChanged(j1, Job.State.Error);
		
		assertEquals(0, this.stats.getRunningJobs());
		assertEquals(1, this.stats.getFailedJobs());
		assertEquals(1, this.stats.getTotalJobs());
		assertEquals(new Integer(1), this.stats.getFailedJobTypes().get(j1.getClass().toString()));
	}

	@Test
	public void testStartExecute() throws SchedulerException {
		long workThreadsAtStart = this.stats.getWorkerThreads();
		this.scheduler.startExecute(2);
		
		assertEquals(workThreadsAtStart+2, this.stats.getWorkerThreads());
	}

	@Test
	public void testStopExecute() throws SchedulerException {
		this.scheduler.startExecute(2);
		this.scheduler.stopExecute();
		
		assertEquals(0, this.stats.getWorkerThreads());
	}
}
