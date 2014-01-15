package eu.sqooss.service.scheduler;


public interface SchedulerStats extends SchedulerStatsView {

	public abstract void incFinishedJobs();

	public abstract void incWorkerThreads();

	public abstract void decWorkerThreads();

	public abstract void incIdleWorkerThreads();

	public abstract void decIdleWorkerThreads();

	public abstract void addFailedJob(String classname);

	public abstract void addWaitingJob(String classname);

	public abstract void removeWaitingJob(String classname);

	public abstract void addRunJob(Job j);

	public abstract void removeRunJob(Job j);

}