package eu.sqooss.test.service.webadmin;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;


import eu.sqooss.impl.service.webadmin.JobsView;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerStats;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Job.class)
public class JobsViewTest {

	@Mock
	Scheduler sobjSched;
	@Mock
	LogManager sobjLogManager;
	@Mock
	Job job1;
	@Mock
	Job job2;

	String newline = "\n";

	@BeforeClass
	public static void setUp() {
	}
	
	@Test
	public void testExed() {
		JobsView jv = new JobsView(null, null);
		assertNotNull(jv);
		jv.exec(null);
	}

	@Test
	public void testGetFailedJobsStatsEmpty() {
		Whitebox.setInternalState(JobsView.class, sobjSched);
		SchedulerStats stats = new SchedulerStats();
		when(sobjSched.getSchedulerStats()).thenReturn(stats);
		Assert.assertEquals(stats.getFailedJobTypes(),
				JobsView.getFailedJobStats());
	}

	@Test
	public void testGetFailedJobsStatsMultiple() {
		Whitebox.setInternalState(JobsView.class, sobjSched);

		SchedulerStats stats = new SchedulerStats();
		stats.addFailedJob("Job name 1");
		stats.addFailedJob("Job name 2");

		when(sobjSched.getSchedulerStats()).thenReturn(stats);
		Assert.assertEquals(stats.getFailedJobTypes(),
				JobsView.getFailedJobStats());
		Assert.assertEquals("Job name 1", JobsView.getFailedJobStats()
				.keySet().toArray()[1]);
		Assert.assertEquals(1,
				(int) JobsView.getFailedJobStats().get("Job name 2"));
	}
	@Test
	public void testGetWaitingJobsEmpty() {
		Whitebox.setInternalState(JobsView.class, sobjSched);
		SchedulerStats stats = new SchedulerStats();
		when(sobjSched.getSchedulerStats()).thenReturn(stats);
		Assert.assertEquals(stats.getWaitingJobTypes(),
				JobsView.getWaitingJobs());
	}

	@Test
	public void testGetWaitingJobsMultiple() {
		Whitebox.setInternalState(JobsView.class, sobjSched);

		SchedulerStats stats = new SchedulerStats();
		stats.addWaitingJob("Job name 1");
		stats.addWaitingJob("Job name 2");

		when(sobjSched.getSchedulerStats()).thenReturn(stats);
		Assert.assertEquals(stats.getWaitingJobTypes(),
				JobsView.getWaitingJobs());
		Assert.assertEquals("Job name 1", JobsView.getWaitingJobs()
				.keySet().toArray()[1]);
		Assert.assertEquals(1,
				(int) JobsView.getWaitingJobs().get("Job name 2"));
	}


	@Test
	public void testGetRunningJobsEmpty() {
		Whitebox.setInternalState(JobsView.class, sobjSched);
		SchedulerStats stats = new SchedulerStats();
		when(sobjSched.getSchedulerStats()).thenReturn(stats);

		Assert.assertEquals(stats.getRunJobs(), JobsView.getRunningJobs());
	}
	
	@Test
	public void testGetRunningJobsMultiple() {
		Whitebox.setInternalState(JobsView.class, sobjSched);
		SchedulerStats stats = new SchedulerStats();
		stats.addRunJob(job1);
		stats.addRunJob(job2);
		when(sobjSched.getSchedulerStats()).thenReturn(stats);

		Assert.assertEquals(stats.getRunJobs(), JobsView.getRunningJobs());
	}

	@Test
	public void testGetFailedJobsNone() {
		Whitebox.setInternalState(JobsView.class, sobjSched);
		Job[] jobs = new Job[] {};
		when(sobjSched.getFailedQueue()).thenReturn(jobs);
		Assert.assertEquals(jobs, JobsView.getFailedJobs());
	}

	@Test
	public void testGetFailedJobsMultiple() {
		Whitebox.setInternalState(JobsView.class, sobjSched);
		Job[] jobs = new Job[] {job1, job2, null};
		when(sobjSched.getFailedQueue()).thenReturn(jobs);
		when(job2.getErrorException()).thenReturn(new IllegalArgumentException("Exception text 2"));
		Assert.assertEquals(jobs, JobsView.getFailedJobs());
	}

	@Test
	public void testGetSchedulerStats() {
		Whitebox.setInternalState(JobsView.class, sobjSched);
		JobsView.getSchedulerStats();
		verify(sobjSched, times(1)).getSchedulerStats();
	}
}