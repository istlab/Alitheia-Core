/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 - 2010 - Organization for Free and Open Source Software,  
 *                Athens, Greece.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package eu.sqooss.impl.service.scheduler;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleContext;

import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.ResumePoint;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.service.scheduler.SchedulerStats;
import eu.sqooss.service.scheduler.WorkerThread;

public class SchedulerServiceImpl implements Scheduler {

	private static final String START_THREADS_PROPERTY = "eu.sqooss.scheduler.numthreads";

	private Logger logger;
	private SchedulerStats stats;

	private ExecutorService threadPool;
	private List<BaseWorker> tempThreadPool;

	private PriorityQueue<Job> jobsToBeExecuted;
	private List<Job> failedJobs = new ArrayList<Job>();
	private DependencyManager dependencyManager;

	/**
	 * Initialize a new SchedulerServiceImpl
	 */
	public SchedulerServiceImpl() {
		this.stats = new SchedulerStats();
		this.threadPool = Executors.newFixedThreadPool(1);
		this.jobsToBeExecuted = new PriorityQueue<Job>(10,
				new JobPriorityComparator());
		this.dependencyManager = DependencyManager.getInstance();
		this.tempThreadPool = new ArrayList<BaseWorker>();
	}

	/**
	 * Force the start of a {@link Scheduler} specifying the number of threads
	 * to be used with n.
	 * 
	 * @param n
	 *            - the number of threads to use in the threadpool.
	 */
	@Override
	public void startExecute(int n) {
		this.threadPool = Executors.newFixedThreadPool(n);
		for (int i = 0; i < n; i++) {
			this.threadPool.execute(new BaseWorker(this));
		}
	}

	/**
	 * Start the {@link Scheduler}, the number of threads is based on the
	 * available processors.
	 */
	@Override
	public boolean startUp() {

		int numThreads = 2 * Runtime.getRuntime().availableProcessors();
		String threadsProperty = System.getProperty(START_THREADS_PROPERTY);

		if (threadsProperty != null && !threadsProperty.equals("-1")) {
			try {
				numThreads = Integer.parseInt(threadsProperty);
			} catch (NumberFormatException nfe) {
				logger.warn("Invalid number of threads to start:"
						+ threadsProperty);
			}
		}
		this.startExecute(numThreads);

		return true;
	}

	/**
	 * Kill the current {@link Scheduler}, this stops all the threads that are
	 * associated with this {@link Scheduler}
	 */
	@Override
	public void shutDown() {
		for (BaseWorker worker : this.tempThreadPool) {
			worker.stopProcessing();
			worker = null;
		}
		this.threadPool.shutdownNow();
		try {
			this.threadPool.awaitTermination(15, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();

		}
	}

	/**
	 * Specify the logger after initializing the {@link Scheduler}
	 */
	@Override
	public void setInitParams(BundleContext bc, Logger l) {
		this.logger = l;
		this.stats.setInitParams(bc, l);
	}

	/**
	 * Enqueue a {@link Job} in this scheduler. Note: dependencies are handled
	 * by the {@link DependencyManager} now.
	 * 
	 * @param job
	 *            - The {@link Job} to be enqueued.
	 */
	@Override
	public void enqueue(Job job) throws SchedulerException {
		if (job == null) {
			return;
		}
		synchronized (this) {
			job.callAboutToBeEnqueued(this);
			this.jobsToBeExecuted.add(job);
			this.stats.incTotalJobs();
			this.stats.addWaitingJob(job.getClass().toString());
			job.addJobStateListener(this.stats);
		}
	}

	/**
	 * Enqueue a multiple jobs at once, this calls
	 * {@link Scheduler#enqueue(Job)} for each job in the Set.
	 * 
	 * @param jobs
	 *            - A set of jobs.
	 */
	@Override
	public void enqueue(Set<Job> jobs) throws SchedulerException {
		synchronized (this) {
			for (Job j : jobs) {
				this.enqueue(j);
			}
		}
	}

	/**
	 * @deprecated This method exists for backward compatability and should not
	 *             be used, dependencies are handled by
	 *             {@link DependencyManager}
	 */
	@Deprecated
	public void enqueueNoDependencies(Set<Job> jobs) throws SchedulerException {
		this.enqueue(jobs);
	}

	/**
	 * Remove a {@link Job} j from the queue of this {@link Scheduler}
	 * 
	 * @param j
	 *            - the Job to be dequeued
	 */
	public void dequeue(Job j) {
		synchronized (this) {
			j.callAboutToBeDequeued(this);
			this.jobsToBeExecuted.remove(j);
			this.stats.removeWaitingJob(j.getClass().toString());
		}
	}

	/**
	 * @deprecated This method exists for backward compatability and should not
	 *             be used, dependencies are handled by
	 *             {@link DependencyManager}
	 */
	@Override
	@Deprecated
	public void jobDependenciesChanged(Job job) {
		// for backwards compatibility
		// does nothing
	}

	/**
	 * Take the first job that can be executed. Jobs are ordered by priority, a
	 * higher priority means that a job is executed sooner.
	 */
	@Override
	public Job takeJob() throws InterruptedException {
		while (true) {
			synchronized (this) {
				for (Job j : this.jobsToBeExecuted) {
					if (this.dependencyManager.canExecute(j)) {
						this.jobsToBeExecuted.remove(j);
						return j;
					} else {
						if (this.logger != null) {
							this.logger.debug("Unmatched dependencies for "
									+ this.dependencyManager.getDependency(j));

						}
					}
				}
			}
			Thread.sleep(100);
		}
	}

	/**
	 * Take a specific job from the {@link Scheduler}, this {@link Job} has to
	 * be in the scheduler or else a {@link SchedulerException} will be thrown
	 * 
	 * @param job
	 *            - The job to be taken from the scheduler.
	 * @throws SchedulerException
	 *             when the job is not enqueued in this scheduler
	 */
	@Override
	public synchronized Job takeJob(Job job) throws SchedulerException {
		if (job == null || job.state() == Job.State.Finished
				|| !this.jobsToBeExecuted.contains(job)) {
			throw new SchedulerException(String.format(
					"Job %s is not enqueued in scheduler %s", job, this));
		}
		this.jobsToBeExecuted.remove(job);
		return job;
	}

	/**
	 * Stop this {@link Scheduler} killing all the threads associated with it
	 */
	@Override
	public void stopExecute() {
		for (BaseWorker worker : this.tempThreadPool) {
			worker.stopProcessing();
		}
		this.threadPool.shutdownNow();
	}

	/**
	 * Check if threads of this {@link Scheduler} are executing.
	 * 
	 * @return boolean
	 */
	@Override
	// TODO testen
	public boolean isExecuting() {
		return ((ThreadPoolExecutor) this.threadPool).getActiveCount() > 0;
	}

	/**
	 * Return the {@link SchedulerStats} object of this {@link Scheduler}
	 * 
	 * @return {@link SchedulerStats}
	 */
	@Override
	public SchedulerStats getSchedulerStats() {
		return this.stats;
	}

	/**
	 * Get the queue of failed jobs.
	 * 
	 * @return Job[] - array of failed jobs.
	 */
	@Override
	public Job[] getFailedQueue() {
		return this.failedJobs.toArray(new Job[0]);
	}

	@Override
	public boolean createAuxQueue(Job j, Set<Job> jobs, ResumePoint p)
			throws SchedulerException {
		if (jobs.isEmpty() ) {
			if ( logger != null) {
				logger.warn("Empty job queue passed to createAuxQueue(). Ignoring request");
			}
			return false;
		}

		j.yield(p);
		for (Job job : jobs) {
			this.dependencyManager.addDependency(j, job);
			enqueue(job);
		}
		return true;
	}

	@Override
	public void yield(Job j, ResumePoint p) throws SchedulerException {
		if (j.state() != Job.State.Yielded) {
			j.yield(p);
		}
		this.jobsToBeExecuted.remove(j);

	}

	@Override
	public void resume(Job j, ResumePoint p) throws SchedulerException {
		if (j.state() == Job.State.Yielded) {
			this.jobsToBeExecuted.add(j);
		}
	}

	@Override
	public DependencyManager getDependencyManager() {
		return dependencyManager;
	}

	@Override
	public void startOneShotWorker(Job job) {
		OneShotWorker osw = new OneShotWorker(this, job);
		this.tempThreadPool.add(osw);
		osw.run();
	}

	@Override
	public void deallocateFromThreadpool(BaseWorker bw) {
		this.tempThreadPool.remove(bw);
	}

}

// vi: ai nosi sw=4 ts=4 expandtab
