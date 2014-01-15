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

import java.util.HashMap;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.SchedulerStats;

public class SchedulerStatsImpl implements SchedulerStats {
    // the number of jobs which were finished
    private long finishedJobs = 0;
    // the number of jobs currently waiting
    private long waitingJobs = 0;
    // the number of jobs currently running
    private long runningJobs = 0;
    // the total number of threads available for scheduling
    private long workerThreads = 0;
    // the number of threads being idle at the moment
    private long idleWorkerThreads = 0;
    // the number of jobs which failed
    private long failedJobs = 0;
    //Classname->Failed Jobs 
    private HashMap<String, Integer> failedJobTypes = new HashMap<>();
    //Classname->Num jobs waiting
    private HashMap<String, Integer> waitingJobTypes = new HashMap<>();
   
    /* (non-Javadoc)
	 * @see eu.sqooss.service.scheduler.SchedulerStats#incFinishedJobs()
	 */
    @Override
	public synchronized void incFinishedJobs() {
        finishedJobs++;
    }
    
    /* (non-Javadoc)
	 * @see eu.sqooss.service.scheduler.SchedulerStats#incWorkerThreads()
	 */
    @Override
	public synchronized void incWorkerThreads() {
        workerThreads++;
    }
    
    /* (non-Javadoc)
	 * @see eu.sqooss.service.scheduler.SchedulerStats#decWorkerThreads()
	 */
    @Override
	public synchronized void decWorkerThreads() {
        workerThreads--;
    }
    
    /* (non-Javadoc)
	 * @see eu.sqooss.service.scheduler.SchedulerStats#incIdleWorkerThreads()
	 */
    @Override
	public synchronized void incIdleWorkerThreads() {
        idleWorkerThreads++;
    }
    
    /* (non-Javadoc)
	 * @see eu.sqooss.service.scheduler.SchedulerStats#decIdleWorkerThreads()
	 */
    @Override
	public synchronized void decIdleWorkerThreads() {
        idleWorkerThreads--;
    }
    
    /* (non-Javadoc)
	 * @see eu.sqooss.service.scheduler.SchedulerStats#addFailedJob(java.lang.String)
	 */
    @Override
	public synchronized void addFailedJob(String classname) {
        this.failedJobs++;
        if (failedJobTypes.containsKey(classname))
            failedJobTypes.put(classname, (failedJobTypes.get(classname) + 1));
        else
            failedJobTypes.put(classname, 1);
    }

    /* (non-Javadoc)
	 * @see eu.sqooss.service.scheduler.SchedulerStats#addWaitingJob(java.lang.String)
	 */
    @Override
	public synchronized void addWaitingJob(String classname) {
        this.waitingJobs++;
        if (waitingJobTypes.containsKey(classname))
            waitingJobTypes.put(classname, (waitingJobTypes.get(classname) + 1));
        else
            waitingJobTypes.put(classname, 1);
    }
    
    /* (non-Javadoc)
	 * @see eu.sqooss.service.scheduler.SchedulerStats#removeWaitingJob(java.lang.String)
	 */
    @Override
	public synchronized void removeWaitingJob(String classname) {
        this.waitingJobs --;
        if (waitingJobTypes.containsKey(classname)) {
            int jobs = waitingJobTypes.get(classname) - 1;
            if (jobs == 0) {
                waitingJobTypes.remove(classname);
            } else {
                waitingJobTypes.put(classname, jobs);
            }
        }
    }
 
    /* (non-Javadoc)
	 * @see eu.sqooss.service.scheduler.SchedulerStats#addRunJob(eu.sqooss.service.scheduler.Job)
	 */
    @Override
	public synchronized void addRunJob(Job j) {
        this.runningJobs++;
    }
    
    /* (non-Javadoc)
	 * @see eu.sqooss.service.scheduler.SchedulerStats#removeRunJob(eu.sqooss.service.scheduler.Job)
	 */
    @Override
	public synchronized void removeRunJob(Job j) {
        this.runningJobs--;
    }
    
    /* (non-Javadoc)
	 * @see eu.sqooss.service.scheduler.SchedulerStatsView#getTotalJobs()
	 */
    @Override
	public long getTotalJobs() {
        return failedJobs + finishedJobs + runningJobs + waitingJobs;
    }

    /* (non-Javadoc)
	 * @see eu.sqooss.service.scheduler.SchedulerStatsView#getWaitingJobs()
	 */
    @Override
	public long getWaitingJobs() {
        return waitingJobs;
    }

    /* (non-Javadoc)
	 * @see eu.sqooss.service.scheduler.SchedulerStatsView#getFinishedJobs()
	 */
    @Override
	public long getFinishedJobs() {
        return finishedJobs;
    }

    /* (non-Javadoc)
	 * @see eu.sqooss.service.scheduler.SchedulerStatsView#getRunningJobs()
	 */
    @Override
	public long getRunningJobs() {
        return runningJobs;
    }

    /* (non-Javadoc)
	 * @see eu.sqooss.service.scheduler.SchedulerStatsView#getWorkerThreads()
	 */
    @Override
	public long getWorkerThreads() {
        return workerThreads;
    }

    /* (non-Javadoc)
	 * @see eu.sqooss.service.scheduler.SchedulerStatsView#getIdleWorkerThreads()
	 */
    @Override
	public long getIdleWorkerThreads() {
        return idleWorkerThreads;
    }

    /* (non-Javadoc)
	 * @see eu.sqooss.service.scheduler.SchedulerStatsView#getFailedJobs()
	 */
    @Override
	public long getFailedJobs() {
        return failedJobs;
    }
    
    /* (non-Javadoc)
	 * @see eu.sqooss.service.scheduler.SchedulerStatsView#getFailedJobTypes()
	 */
    @Override
	public HashMap<String, Integer> getFailedJobTypes() {
        return failedJobTypes;
    }
    
    /* (non-Javadoc)
	 * @see eu.sqooss.service.scheduler.SchedulerStatsView#getWaitingJobTypes()
	 */
    @Override
	public HashMap<String, Integer> getWaitingJobTypes() {
        return waitingJobTypes;
    }
}
