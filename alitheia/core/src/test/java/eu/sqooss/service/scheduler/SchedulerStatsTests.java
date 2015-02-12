package eu.sqooss.service.scheduler;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class SchedulerStatsTests {
	@Test
	public void initTest() {
		SchedulerStats stats = new SchedulerStats();
		assertEquals(0, stats.getFailedJobs());
		assertEquals(0, stats.getWaitingJobs());
		assertEquals(0, stats.getRunJobs().size());
	}

	@Test
	public void addFailedJobTest() {
		SchedulerStats stats = new SchedulerStats();
		stats.addFailedJob("job 1");

		assertEquals(1, stats.getFailedJobs());
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("job 1", 1);
		assertEquals(map, stats.getFailedJobTypes());
	}

	@Test
	public void addMultipleFailedJobsTest() {
		SchedulerStats stats = new SchedulerStats();
		stats.addFailedJob("job 1");
		stats.addFailedJob("job 2");
		stats.addFailedJob("job 3");

		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("job 1", 1);
		map.put("job 2", 1);
		map.put("job 3", 1);

		assertEquals(map, stats.getFailedJobTypes());
		assertEquals(3, stats.getFailedJobs());

		stats.addFailedJob("job 1");
		map.put("job 1", 2);

		assertEquals(map, stats.getFailedJobTypes());
		assertEquals(4, stats.getFailedJobs());
	}

	@Test
	public void addWaitingJobTest() {
		SchedulerStats stats = new SchedulerStats();
		stats.addWaitingJob("job 1");

		assertEquals(1, stats.getWaitingJobs());
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("job 1", 1);
		assertEquals(map, stats.getWaitingJobTypes());
		assertEquals(1, stats.getWaitingJobs());
	}

	@Test
	public void addMultipleWaitingJobsTest() {
		SchedulerStats stats = new SchedulerStats();
		stats.addWaitingJob("a");
		stats.addWaitingJob("b");
		stats.addWaitingJob("c");

		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("a", 1);
		map.put("b", 1);
		map.put("c", 1);

		assertEquals(map, stats.getWaitingJobTypes());
		assertEquals(3, stats.getWaitingJobs());

		stats.addWaitingJob("a");
		map.put("a", 2);

		assertEquals(map, stats.getWaitingJobTypes());
		assertEquals(4, stats.getWaitingJobs());
	}

	@Test
	public void removeWaitingJobTest() {
		SchedulerStats stats = new SchedulerStats();
		stats.addWaitingJob("a");
		stats.addWaitingJob("b");
		stats.addWaitingJob("b");
		stats.addWaitingJob("c");

		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("b", 1);
		map.put("c", 1);

		stats.removeWaitingJob("a");
		stats.removeWaitingJob("b");

		assertEquals(map, stats.getWaitingJobTypes());
		assertEquals(2, stats.getWaitingJobs());
	}

	/**
	 * This test might expect unintended behavior.
	 */
	@Test
	public void removeToMuchJobsTest() {
		SchedulerStats stats = new SchedulerStats();
		stats.removeWaitingJob("a");
		stats.removeWaitingJob("b");

		assertEquals(0, stats.getWaitingJobs());
	}

	@Test
	public void getRunJobsTest() {
		SchedulerStats stats = new SchedulerStats();
		TestJob job1 = new TestJob(2, "job 1");
		stats.addRunJob(job1);
		assertEquals(1, stats.getRunJobs().size());
	}
}
