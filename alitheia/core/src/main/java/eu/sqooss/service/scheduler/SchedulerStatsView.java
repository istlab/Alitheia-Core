package eu.sqooss.service.scheduler;

import java.util.HashMap;
import java.util.List;

public interface SchedulerStatsView {

	public abstract long getTotalJobs();

	public abstract long getWaitingJobs();

	public abstract long getFinishedJobs();

	public abstract long getRunningJobs();

	public abstract long getWorkerThreads();

	public abstract long getIdleWorkerThreads();

	public abstract long getFailedJobs();

	public abstract HashMap<String, Integer> getFailedJobTypes();

	public abstract HashMap<String, Integer> getWaitingJobTypes();
}