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

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

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
    private static final String PERF_LOG_PROPERTY = "eu.sqooss.log.perf";
    
    private Logger logger = null;
    private boolean perfLog = false;

    private SchedulerStats stats = new SchedulerStats();

    // thread safe job queue
    private PriorityQueue<Job> blockedQueue = new PriorityQueue<Job>(1,
            new JobPriorityComparator());
    private BlockingQueue<Job> workQueue = new PriorityBlockingQueue<Job>(1,
            new JobPriorityComparator());

    private BlockingQueue<Job> failedQueue = new ArrayBlockingQueue<Job>(1000);

    private List<WorkerThread> myWorkerThreads = null;
    
    public SchedulerServiceImpl() { }

    public void enqueue(Job job) throws SchedulerException {
        synchronized (this) {
            if (logger != null)
                logger.debug("SchedulerServiceImpl: queuing job " + job.toString());
            job.callAboutToBeEnqueued(this);
            blockedQueue.add(job);
            stats.addWaitingJob(job.getClass().toString());
            stats.incTotalJobs();
        }
        jobDependenciesChanged(job);
    }
    
    public void enqueueNoDependencies(Set<Job> jobs) throws SchedulerException {
        synchronized (this) {
            for (Job job : jobs) {
                logger.debug("Scheduler ServiceImpl: queuing job "
                        + job.toString());
                job.callAboutToBeEnqueued(this);
                workQueue.add(job);
                stats.addWaitingJob(job.getClass().toString());
                stats.incTotalJobs();
            }
        }
    }
    
    public void enqueueBlock(List<Job> jobs) throws SchedulerException {
        synchronized (this) {
            for (Job job : jobs) {
            	if (logger != null) //Added by Joost
                logger.debug("SchedulerServiceImpl: queuing job " + job.toString());
                job.callAboutToBeEnqueued(this);
                blockedQueue.add(job);
                stats.addWaitingJob(job.getClass().toString());
                stats.incTotalJobs();
            }
        }
        for (Job job : jobs)
            jobDependenciesChanged(job);
    }

    public void dequeue(Job job) {
        synchronized (this) {
            if (!blockedQueue.contains(job) && !workQueue.contains(job)) {
                if (logger != null) {
                    logger.info("SchedulerServiceImpl: job " + job.toString()
                            + " not found in the queue.");
                }
                return;
            }
            job.callAboutToBeDequeued(this);
            blockedQueue.remove(job);
            workQueue.remove(job);
        }
        if (logger != null) {
            logger.warn("SchedulerServiceImpl: job " + job.toString()
                    + " not found in the queue.");
        }
    }

    public Job takeJob() throws java.lang.InterruptedException {
        /*
         * no synchronize needed here, the queue is doing that adding
         * synchronize here would actually dead-lock this, since no new items
         * can be added as long someone is waiting for items
         */
    	for (Iterator iterator = workQueue.iterator(); iterator.hasNext();) {
			Job j = (Job) iterator.next();
		}
        return workQueue.take();
    }

    public Job takeJob(Job job) throws SchedulerException {
        synchronized (workQueue) {
            if (!workQueue.contains(job)) {
                throw new SchedulerException("Can't take job " + job
                        + ": It is not in the scheduler's queue right now.");
            }
            workQueue.remove(job);
            return job;
        }
    }
    
    public void jobStateChanged(Job job, Job.State state) {
        if (logger != null) {
            logger.debug("Job " + job + " changed to state " + state);
        }

        if (state == Job.State.Finished) {
            stats.removeRunJob(job);
            stats.incFinishedJobs();
        } else if (state == Job.State.Running) {
            stats.removeWaitingJob(job.getClass().toString());
            stats.addRunJob(job);
        } else if (state == Job.State.Yielded) {
            stats.removeRunJob(job);
            stats.addWaitingJob(job.getClass().toString());
        } else if (state == Job.State.Error) {

            if (failedQueue.remainingCapacity() == 1)
                failedQueue.remove();
            failedQueue.add(job);
            
            stats.removeRunJob(job);
            stats.addFailedJob(job.getClass().toString());
        }
    }

    public void jobDependenciesChanged(Job job) {
        synchronized (this) {
            if (workQueue.contains(job) && !job.canExecute()) {
                workQueue.remove(job);
                blockedQueue.add(job);
            } else if (job.canExecute()) {
                blockedQueue.remove(job);
                workQueue.add(job);
            }
        }
    }

    public void startExecute(int n) {
        if (logger != null)
            logger.info("Starting " + n + " worker threads");
        synchronized (this) {
            if (myWorkerThreads == null) {
                myWorkerThreads = new LinkedList<WorkerThread>();
            }

            for (int i = 0; i < n; ++i) {
                WorkerThread t = new WorkerThreadImpl(this, i);
                t.start();
                myWorkerThreads.add(t);
                stats.incWorkerThreads();
            }
        }
    }

    public void stopExecute() {
        synchronized (this) {
            if (myWorkerThreads == null) {
                return;
            }

            for (WorkerThread t : myWorkerThreads) {
                t.stopProcessing();
                stats.decWorkerThreads();
            }

            myWorkerThreads.clear();
        }
    }

    synchronized public boolean isExecuting() {
        synchronized (this) {
            if (myWorkerThreads == null) {
                return false;
            } else {
                return !myWorkerThreads.isEmpty();
            }
        }
    }

    public SchedulerStats getSchedulerStats() {
        return stats;
    }

    public Job[] getFailedQueue() {
        Job[] failedJobs = new Job[failedQueue.size()];
        return failedQueue.toArray(failedJobs);
    }

    public WorkerThread[] getWorkerThreads() {
        return (WorkerThread[]) this.myWorkerThreads.toArray(new WorkerThread[0]);
    }

    public void startOneShotWorkerThread() {
        WorkerThread t = new WorkerThreadImpl(this, true);
        t.start();
    }

	@Override
	public void setInitParams(BundleContext bc, Logger l) {
		this.logger = l;
	}

	@Override
	public void shutDown() {
	}

	@Override
	public boolean startUp() {
        
        int numThreads = 2 * Runtime.getRuntime().availableProcessors(); 
        String threadsProperty = System.getProperty(START_THREADS_PROPERTY);
        
        if (threadsProperty != null && !threadsProperty.equals("-1")) {
            try {
                numThreads = Integer.parseInt(threadsProperty);
            } catch (NumberFormatException nfe) {
                logger.warn("Invalid number of threads to start:" + threadsProperty);
            }
        }
        startExecute(numThreads);
        
        String perfLog = System.getProperty(PERF_LOG_PROPERTY);
        if (perfLog != null && perfLog.equals("true")) {
            logger.info("Using performance logging");
            this.perfLog = true;
        }

        return true;
	}

    @Override
    public boolean createAuxQueue(Job j, Deque<Job> jobs, ResumePoint p)
            throws SchedulerException {
        
        if (jobs.isEmpty()) {
            logger.warn("Empty job queue passed to createAuxQueue(). Ignoring request");
            return false;
        }
        
        j.yield(p);
        for (Job job : jobs) {
            j.addDependency(job);
            enqueue(job);
        }
        return true;
    }

    @Override
    public synchronized void yield(Job j, ResumePoint p) throws SchedulerException {
        
        if (j.state() != Job.State.Yielded)
            j.yield(p);
        workQueue.remove(j);
        blockedQueue.add(j);
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
