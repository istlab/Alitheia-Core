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

package eu.sqooss.service.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

// TODO: Auto-generated Javadoc
/**
 * The Class SchedulerStats.
 */
@XmlRootElement(name="schedulerStats")
public class SchedulerStats {
    /** The number of jobs currently in the scheduler. */
    @XmlElement
    private long totalJobs = 0;
    
    /** The number of jobs which were finished. */
    @XmlElement
    private long finishedJobs = 0;
    
    /** The number of jobs currently waiting. */
    @XmlElement
    private long waitingJobs = 0;

    /** The number of jobs currently running. */
    @XmlElement
    private long runningJobs = 0;
    
    /** The total number of threads available for scheduling. */
    @XmlElement
    private long workerThreads = 0;

    /** The number of threads being idle at the moment. */
    @XmlElement
    private long idleWorkerThreads = 0;

    /** The number of jobs which failed. */
    @XmlElement
    private long failedJobs = 0;

    /** The failed job types; Classname -> Failed Jobs. */
    @XmlElement
    private HashMap<String, Integer> failedJobTypes = new HashMap<String, Integer>();

    /** The waiting job types; Classname -> # Jobs waiting. */
    @XmlElement
    private HashMap<String, Integer> waitingJobTypes = new HashMap<String, Integer>();

    /** The runnning jobs. */
    private List<Job> runJobs = new Vector<Job>();

    /**
     * Increment total jobs.
     */
    public synchronized void incTotalJobs() {
        totalJobs++;
    }

    /**
     * Decrement total jobs.
     */
    public synchronized void decTotalJobs() {
        totalJobs--;
    }

    /**
     * Increment finished jobs.
     */
    public synchronized void incFinishedJobs() {
        finishedJobs++;
    }

    /**
     * Increment worker threads.
     */
    public synchronized void incWorkerThreads() {
        workerThreads++;
    }

    /**
     * Decrement worker threads.
     */
    public synchronized void decWorkerThreads() {
        workerThreads--;
    }

    /**
     * Increment idle worker threads.
     */
    public synchronized void incIdleWorkerThreads() {
        idleWorkerThreads++;
    }

    /**
     * Decrement idle worker threads.
     */
    public synchronized void decIdleWorkerThreads() {
        idleWorkerThreads--;
    }

    /**
     * Adds the failed job.
     *
     * @param classname the classname
     */
    public synchronized void addFailedJob(String classname) {
        this.failedJobs++;
        if (failedJobTypes.containsKey(classname))
            failedJobTypes.put(classname, (failedJobTypes.get(classname) + 1));
        else
            failedJobTypes.put(classname, 1);
    }

    /**
     * Adds the waiting job.
     *
     * @param classname the classname
     */
    public synchronized void addWaitingJob(String classname) {
        this.waitingJobs++;
        if (waitingJobTypes.containsKey(classname))
            waitingJobTypes.put(classname, (waitingJobTypes.get(classname) + 1));
        else
            waitingJobTypes.put(classname, 1);
    }

    /**
     * Removes the waiting job.
     *
     * @param classname the classname
     */
    public synchronized void removeWaitingJob(String classname) {
        this.waitingJobs --;
        if (waitingJobTypes.containsKey(classname)) {
            int jobs = waitingJobTypes.get(classname) - 1;
            if (jobs == 0) {
                waitingJobTypes.remove(classname);
            }

            waitingJobTypes.put(classname, jobs);
        }
    }

    /**
     * Adds the run job.
     *
     * @param j the j
     */
    public synchronized void addRunJob(Job j) {
        this.runningJobs++;
        this.runJobs.add(j);
    }

    /**
     * Removes the run job.
     *
     * @param j the job
     */
    public synchronized void removeRunJob(Job j) {
        this.runningJobs--;
        this.runJobs.remove(j);
    }

    /**
     * Gets the total jobs.
     *
     * @return the total jobs
     */
    public long getTotalJobs() {
        return totalJobs;
    }

    /**
     * Gets the waiting jobs.
     *
     * @return the waiting jobs
     */
    public long getWaitingJobs() {
        return waitingJobs;
    }

    /**
     * Gets the finished jobs.
     *
     * @return the finished jobs
     */
    public long getFinishedJobs() {
        return finishedJobs;
    }

    /**
     * Gets the running jobs.
     *
     * @return the running jobs
     */
    public long getRunningJobs() {
        return runningJobs;
    }

    /**
     * Gets the worker threads.
     *
     * @return the worker threads
     */
    public long getWorkerThreads() {
        return workerThreads;
    }

    /**
     * Gets the idle worker threads.
     *
     * @return the idle worker threads
     */
    public long getIdleWorkerThreads() {
        return idleWorkerThreads;
    }

    /**
     * Gets the failed jobs.
     *
     * @return the failed jobs
     */
    public long getFailedJobs() {
        return failedJobs;
    }

    /**
     * Gets the failed job types.
     *
     * @return the failed job types
     */
    public HashMap<String, Integer> getFailedJobTypes() {
        return failedJobTypes;
    }

    /**
     * Gets the waiting job types.
     *
     * @return the waiting job types
     */
    public HashMap<String, Integer> getWaitingJobTypes() {
        return waitingJobTypes;
    }

    /**
     * Gets the run jobs.
     *
     * @return the run jobs
     */
    public synchronized List<String> getRunJobs() {
        Job[] jobs = new Job[runJobs.size()];
        runJobs.toArray(jobs);
        List<String> jobDescr = new ArrayList<String>();
        for (Job j : jobs) {
            jobDescr.add(j.toString());
        }
        return jobDescr;
    }
}
