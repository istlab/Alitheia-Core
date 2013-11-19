package eu.sqooss.impl.service.scheduler;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.service.scheduler.WorkerThread;

/**
 * {@link WorkerThread} executes jobs given by {@link SchedulerServiceImpl}
 * This class BaseWorkerThread provides the most basic implementation of a WorkerThread.
 * @author Joost
 * 
 */
public class BaseWorker  implements WorkerThread, Runnable {
	protected static final String PERF_LOG_PROPERTY = "eu.sqooss.log.perf";
	protected boolean perfLog = false;
	protected Scheduler m_scheduler;
	protected volatile boolean m_processing;
	protected volatile Job m_job = null;

	/**
	 * Constructor to create a new BaseWorker
	 * @param s a {@link SchedulerServiceImpl} which provides the jobs and handles the threadpool
	 */
	public BaseWorker(Scheduler s) {
        m_scheduler = s;
        
        String perfLog = System.getProperty(PERF_LOG_PROPERTY);
        if (perfLog != null && perfLog.equals("true")) {
            this.perfLog = true;
        }
	}

	/**
	 * Stop the loop of this runnable. This means that the runnable stops
	 * after finishing the current task
	 */
	@Override
	public void stopProcessing() {
		m_processing = false;
//		Thread.currentThread().interrupt();
	}

	/**
	 * Returns the job that is currently processing or 
	 * the last process job if the worker is idle
	 */
	@Override
	public Job executedJob() {
		return m_job;
	}

	/**
	 * Take the next job of the scheduler and run it immediately
	 * in a new worker.
	 */
	@Override
	public void takeJob(Job job) throws SchedulerException {
		m_scheduler.startOneShotWorker(job); 
	}

	
	/**
	 * Start a new Runnable that looks for work. 
	 * {@link SchedulerServiceImpl#takeJob()} is blocking and returns the job to be processsed
	 */
	@Override
	public void run() {
	     m_processing = true;
	        while (m_processing) {
	            try {
	            	// get a job from the scheduler
	            	Job job = m_scheduler.takeJob();
	            	
	            	executeJob(job);
	            } catch (InterruptedException e) {
	                // we were interrupted, just try again
	            	m_processing = false;
	            	continue;
	            }
	        }
	}

	/**
	 * Starts or continues the execution of a job and reports
	 * if this fails
	 * @param job the job that is to be executed
	 */
	protected void executeJob(Job j) {
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
		    AlitheiaCore.getInstance().getLogManager().createLogger(
		            Logger.NAME_SQOOSS_SCHEDULING).error("Job " + j + " is not resumable");
		} catch (Exception e) {
			e.printStackTrace();
			// no error handling needed here, the job
			// itself takes care of that.
		} finally { 
		    if (perfLog) {
		        AlitheiaCore.getInstance().getLogManager().
		            createLogger("sqooss.jobtimer").
		            debug(m_job.toString() + ", time: " + time + " ms");
		    }
			m_job = oldJob;
		}
	}


}
