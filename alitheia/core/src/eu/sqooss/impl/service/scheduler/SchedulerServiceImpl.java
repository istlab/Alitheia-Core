/*
This file is part of the Alitheia system, developed by the SQO-OSS
consortium as part of the IST FP6 SQO-OSS project, number 033331.

Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
Copyright 2007-2008 by KDAB (www.kdab.net)
Author: Mirko Boehm <mirko@kdab.net>

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

 * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the following
      disclaimer in the documentation and/or other materials provided
      with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */

package eu.sqooss.impl.service.scheduler;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import org.osgi.framework.BundleContext;

import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.service.scheduler.SchedulerStats;
import eu.sqooss.service.scheduler.WorkerThread;

public class SchedulerServiceImpl implements Scheduler {

    private static final String START_THREADS_PROPERTY = "eu.sqooss.scheduler.numthreads";
    
    private Logger logger = null;

    private SchedulerStats stats = new SchedulerStats();

    // thread safe job queue
    private PriorityQueue<Job> blockedQueue = new PriorityQueue<Job>(1,
            new JobPriorityComparator());
    private BlockingQueue<Job> workQueue = new PriorityBlockingQueue<Job>(1,
            new JobPriorityComparator());

    private BlockingQueue<Job> failedQueue = new ArrayBlockingQueue<Job>(1000);

    private List<WorkerThread> myWorkerThreads = null;

    public SchedulerServiceImpl(BundleContext bc, Logger l) {
        logger = l;
        logger.info("Got scheduling!");
        
        int numThreads = 2 * Runtime.getRuntime().availableProcessors(); 
        String threadsProperty = System.getProperty(START_THREADS_PROPERTY);
        
        if (threadsProperty != null && !threadsProperty.equals("0")) {
            try {
                numThreads = Integer.parseInt(threadsProperty);
            } catch (NumberFormatException nfe) {
                logger.warn("Invalid number of threads to start:" + threadsProperty);
            }
        }
        
        startExecute(numThreads);
    }

    public void enqueue(Job job) throws SchedulerException {
        synchronized (this) {
            logger.info("SchedulerServiceImpl: queuing job " + job.toString());
            job.callAboutToBeEnqueued(this);
            blockedQueue.add(job);
            stats.addWaitingJob(job.getClass().toString());
            stats.incTotalJobs();
        }
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
            logger.info("SchedulerServiceImpl: job " + job.toString()
                    + " not found in the queue.");
        }
    }

    public Job takeJob() throws java.lang.InterruptedException {
        /*
         * no synchronize needed here, the queue is doing that adding
         * synchronize here would actually dead-lock this, since no new items
         * can be added as long someone is waiting for items
         */
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
            logger.info("Job " + job + " changed to state " + state);
        }

        if (state == Job.State.Finished) {
            stats.removeRunJob(job.toString());
            stats.incFinishedJobs();
        } else if (state == Job.State.Running) {
            stats.removeWaitingJob(job.getClass().toString());
            stats.addRunJob(job.toString());
        } else if (state == Job.State.Error) {

            if (failedQueue.remainingCapacity() == 1)
                failedQueue.remove();
            failedQueue.add(job);
            
            stats.removeRunJob(job.toString());
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
        logger.info("Starting " + n + " worker threads");
        synchronized (this) {
            if (myWorkerThreads == null) {
                myWorkerThreads = new LinkedList<WorkerThread>();
            }

            for (int i = 0; i < n; ++i) {
                WorkerThread t = new WorkerThreadImpl(this);
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

    public Object selfTest() {
        
        /*
        try {
            SchedulerTestSuite.run();
        } catch (Exception e) {
            System.out.println( e.getMessage() );
            e.printStackTrace();
        }
        if (logger != null) {
            logger.info("Starting scheduler selftest...");
        }

        Job firstJob = new TestJob(5, "firstJob");
        Job secondJob = new TestJob(10, "secondJob");
        Job thirdJob = new TestJob(15, "thirdJob");
        Job forthJob = new TestJob(20, "forthJob");
        Job fifthJob = new TestJob(1, "fifthJob");

        try {
            // secondJob depends on firstJob
            secondJob.addDependency(firstJob);
            // thirdJob depends on firstJob
            thirdJob.addDependency(firstJob);
            // forthJob depends on secondJob and thirdJob
            forthJob.addDependency(secondJob);
            forthJob.addDependency(thirdJob);
            fifthJob.addDependency(secondJob);
        } catch (SchedulerException e) {
            return new String("Scheduler test failed: " + e.getMessage());
        }

        try {
            // that must fail, since cyclic dependencies are not allowed
            firstJob.addDependency(forthJob);
            // nothing was thrown? ohh...
            return new String(
            "Scheduler test failed: Adding cyclic dependencies should not be possible");
        } catch (SchedulerException e) {
        }

        // firstJob should not end up with any dependencies
        if (firstJob.dependencies().size() != 0) {
            return new String(
            "Scheduler test failed: firstJob.dependencies().size() != 0");
        }

        // secondJob should end up with exactly one dependency
        List<Job> dependencies = secondJob.dependencies();
        if (dependencies.size() != 1) {
            return new String("Scheduler test failed: dependencies.size() != 0");
        } else if (!dependencies.contains(firstJob)) {
            return new String(
            "Scheduler test failed: !dependencies.contains(firstJob)");
        }

        // removing dependencies works?
        secondJob.removeDependency(firstJob);
        if (secondJob.dependencies().size() != 0) {
            return new String(
            "Scheduler test failed: secondJob.dependencies().size() != 0");
        }
        try {
            // secondJob depends on firstJob
            secondJob.addDependency(firstJob);
        } catch (SchedulerException e) {
            return new String("Scheduler test failed: " + e.getMessage());
        }

        // even thirdJob should end up with exactly one dependency
        dependencies = thirdJob.dependencies();
        if (dependencies.size() != 1) {
            return new String("Scheduler test failed: dependencies.size() != 0");
        } else if (!dependencies.contains(firstJob)) {
            return new String(
            "Scheduler test failed: !dependencies.contains(firstJob)");
        }

        // forthJob should end up having two dependencies: secondJob and
        // thirdJob
        dependencies = forthJob.dependencies();
        if (dependencies.size() != 2) {
            return new String("Scheduler test failed: dependencies.size() != 2");
        } else if (!dependencies.contains(secondJob)) {
            return new String(
            "Scheduler test failed: !dependencies.contains(secondJob)");
        } else if (!dependencies.contains(thirdJob)) {
            return new String(
            "Scheduler test failed: !dependencies.contains(thirdJob)");
        }

        // check whether canExecute() returns true only for firstJob
        if (!firstJob.canExecute()) {
            return new String("Scheduler test failed: !firstJob.canExecute()");
        } else if (secondJob.canExecute()) {
            return new String("Scheduler test failed: secondJob.canExecute()");
        } else if (thirdJob.canExecute()) {
            return new String("Scheduler test failed: thirdJob.canExecute()");
        } else if (forthJob.canExecute()) {
            return new String("Scheduler test failed: forthJob.canExecute()");
        }

        // start execution using four worker threads
        startExecute(4);
        try {
            enqueue(firstJob);
            enqueue(secondJob);
            enqueue(thirdJob);
            enqueue(forthJob);
            enqueue(fifthJob);

            // this is blocking until the forthJob is done or has failed
            fifthJob.waitForFinished();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new String("Scheduler test failed: " + e.getMessage());
        } finally {
            stopExecute();
        }
        */

        /*
         * since the fifth should start and finish before the forth can be
         * started and the execution is stopped after the fifth, forth should
         * still be in Queued state and the third in Running.
         */
        /*
        if (firstJob.state() != Job.State.Finished) {
            return new String(
            "Scheduler test failed: firstJob.state() != Job.State.Finished");
        } else if (secondJob.state() != Job.State.Finished) {
            return new String(
            "Scheduler test failed: secondJob.state() != Job.State.Finished");
        } else if (thirdJob.state() != Job.State.Running) {
            return new String(
            "Scheduler test failed: thirdJob.state() != Job.State.Running");
        } else if (forthJob.state() != Job.State.Queued) {
            return new String(
            "Scheduler test failed: forthJob.state() != Job.State.Queued");
        } else if (fifthJob.state() != Job.State.Finished) {
            return new String(
            "Scheduler test failed: fifthJob.state() != Job.State.Finished");
        }
        */
        return null;
    }

    public SchedulerStats getSchedulerStats() {
        return stats;
    }

    public Job[] getFailedQueue() {
        Job[] failedJobs = new Job[failedQueue.size()];
        return failedQueue.toArray(failedJobs);
    }

    public WorkerThread[] getWorkerThreads() {
        return (WorkerThread[]) this.myWorkerThreads.toArray();
    }

    public void startOneShotWorkerThread() {
        WorkerThread t = new WorkerThreadImpl(this, true);
        t.start();
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
