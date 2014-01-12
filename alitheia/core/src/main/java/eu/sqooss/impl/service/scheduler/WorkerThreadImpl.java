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

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.service.scheduler.WorkerThread;

/**
 * Worker thread executing jobs given by a scheduler.
 *
 * @author Christoph Schleifenbaum
 */
class WorkerThreadImpl extends Thread implements WorkerThread {
    private static final String PERF_LOG_PROPERTY = "eu.sqooss.log.perf";
    
    private boolean perfLog = false;

    private Scheduler m_scheduler;

    private volatile boolean m_processing;

    private volatile Job m_job = null;
    
    private boolean m_oneshot = false;
    
    private LogManager logManager;
    
    private WorkerThreadFactory workerThreadFactory;
    
    /**
     * Constructor creating a new WorkerThread
     * @param s the schedule being asked for jobs.
     */
    @AssistedInject
    public WorkerThreadImpl(@Assisted Scheduler s, @Assisted int n, 
            LogManager logManager, WorkerThreadFactory workerThreadFactory) {
        super(null, null, "Worker ");
        m_scheduler = s;
        this.logManager = logManager;
        this.workerThreadFactory = workerThreadFactory;
        String perfLog = System.getProperty(PERF_LOG_PROPERTY);
        if (perfLog != null && perfLog.equals("true")) {
            this.perfLog = true;
        }
    }

    /**
     * Constructor creating a new WorkerThread
     * @param s the schedule being asked for jobs.
     */
    @AssistedInject
    public WorkerThreadImpl(@Assisted Scheduler s, @Assisted boolean oneshot, 
            LogManager logManager, WorkerThreadFactory workerThreadFactory) {
        super(null, null, "OneShot Worker Thread");
        m_scheduler = s;
        this.logManager = logManager;
        this.workerThreadFactory = workerThreadFactory;
    }

    /**
     * Runs the worker thread.
     */
    public void run() {
        m_processing = true;
        while (m_processing) {
            try {
                // get a job from the scheduler
                executeJob(m_scheduler.takeJob());
            } catch (InterruptedException e) {
                // we were interrupted, just try again
                continue;
            }
            if (m_oneshot) {
            	m_processing = false;
            }
        }
    }

    /**
     * Stops processing of jobs, after the current job has finished.
     */
    public void stopProcessing() {
        m_processing = false;
        interrupt();
    }

	public Job executedJob() {
		return m_job;
	}

	public void executeJob(Job j) {
		Job oldJob = m_job;
		long time = -1;
		try {
			m_job = j;
			if (m_job.state() == Job.State.Yielded) {
			    time = m_job.resume();
			} else { 
			    time = m_job.execute();
			}
		} catch (ClassCastException cce) { 
		    logManager.createLogger(
		            Logger.NAME_SQOOSS_SCHEDULING).error("Job " + j + " is not resumable");
		} catch (Exception e) {
			// no error handling needed here, the job
			// itself takes care of that.
		} finally {
		    if (perfLog) {
		        logManager.createLogger("sqooss.jobtimer").
		            debug(m_job.toString() + ", time: " + time + " ms");
		    }
			m_job = oldJob;
		}
	}
	
	/**
	 * Temporary Worker Thread is used to trigger instant
	 * execution of a job.
	 * @author christoph
	 *
	 */
	public class TemporaryWorkerThread implements Runnable {

		private WorkerThread worker;
		private Job job;
		
		public TemporaryWorkerThread(Job job) {
			this.worker = workerThreadFactory.create(null, -1);
		}
		
		TemporaryWorkerThread(WorkerThread worker,Job job) {
			this.worker = worker;
			this.job = job;
		}
		
		public void run() {
			worker.executeJob(job);
		}
		
	}
	
	public void takeJob(Job job) throws SchedulerException {
		Thread thread = new Thread(
				new TemporaryWorkerThread(
						this,
						m_scheduler.takeJob(job)), "Temporary Scheduler Worker Thread");
		thread.start();
	}
}
