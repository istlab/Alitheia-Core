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

import org.apache.commons.collections.list.SynchronizedList;

public class SchedulerStats {
    // the number of jobs currently in the scheduler
    private long totalJobs = 0;
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
    private HashMap<String, Integer> failedJobTypes = new HashMap<String, Integer>();
    //Classname->Num jobs waiting
    private HashMap<String, Integer> waitingJobTypes = new HashMap<String, Integer>();
    //Running jobs
    private List<Job> runJobs = new Vector<Job>();
    
    public synchronized void incTotalJobs() {
        totalJobs++;
    }

    public synchronized void decTotalJobs() {
        totalJobs--;
    }
   
    public synchronized void incFinishedJobs() {
        finishedJobs++;
    }
    
    public synchronized void incWorkerThreads() {
        workerThreads++;
    }
    
    public synchronized void decWorkerThreads() {
        workerThreads--;
    }
    
    public synchronized void incIdleWorkerThreads() {
        idleWorkerThreads++;
    }
    
    public synchronized void decIdleWorkerThreads() {
        idleWorkerThreads--;
    }
    
    public synchronized void addFailedJob(String classname) {
        this.failedJobs++;
        if (failedJobTypes.containsKey(classname))
            failedJobTypes.put(classname, (failedJobTypes.get(classname) + 1));
        else
            failedJobTypes.put(classname, 1);
    }

    public synchronized void addWaitingJob(String classname) {
        this.waitingJobs++;
        if (waitingJobTypes.containsKey(classname))
            waitingJobTypes.put(classname, (waitingJobTypes.get(classname) + 1));
        else
            waitingJobTypes.put(classname, 1);
    }
    
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
 
    public synchronized void addRunJob(Job j) {
        this.runningJobs++;
        this.runJobs.add(j);
    }
    
    public synchronized void removeRunJob(Job j) {
        this.runningJobs--;
        this.runJobs.remove(j);
    }
    
    public long getTotalJobs() {
        return totalJobs;
    }

    public long getWaitingJobs() {
        return waitingJobs;
    }

    public long getFinishedJobs() {
        return finishedJobs;
    }

    public long getRunningJobs() {
        return runningJobs;
    }

    public long getWorkerThreads() {
        return workerThreads;
    }

    public long getIdleWorkerThreads() {
        return idleWorkerThreads;
    }

    public long getFailedJobs() {
        return failedJobs;
    }
    
    public HashMap<String, Integer> getFailedJobTypes() {
        return failedJobTypes;
    }
    
    public HashMap<String, Integer> getWaitingJobTypes() {
        return waitingJobTypes;
    }
    
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
