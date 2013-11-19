package eu.sqooss.impl.service.scheduler;

import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerException;

public class OneShotWorker extends BaseWorker {

	private Job job;

	/**
	 * Creates a new OneShotWorker that takes
	 * the first executable job from the the 
	 * scheduler, executes it, and terminates.
	 * @param s - {@link Scheduler}
	 */
	public OneShotWorker(Scheduler s) {
		super(s);
	}

	/**
	 * Creates a new OneShotWorker that executes the
	 * given {@link Job} j that has to be in scheduler s.
	 * It terminates after the job is finished
	 * @param s - {@link Scheduler}
	 * @param j - {@link Job}
	 */
	public OneShotWorker(Scheduler s, Job j) {
		super(s);
		this.job = j;
	}

	/**
	 * The job that is either given at the creation of {@link OneShotWorker}
	 * or is taken from the scheduler in this method is executed.
	 * The {@link OneShotWorker} is terminated afterwards.
	 */
	@Override
	public void run() {
		try {
			if (this.job == null) {
				this.job = m_scheduler.takeJob();
			} else {
				if(this.job.state() == Job.State.Yielded){
				}
				this.job = m_scheduler.takeJob(this.job);
				while(!DependencyManager.getInstance().canExecute(this.job)){//wait untill the dependencies are met
					Thread.sleep(100);
				}
			}
			// get a job from the scheduler
			this.executeJob(this.job);
		} catch (InterruptedException e) {
			this.m_scheduler.deallocateFromThreadpool(this);
			Thread.currentThread().interrupt();
			// we were interrupted so this workes finishes
		} catch (SchedulerException e) {
			//No valid job was received so this worker is killed.
			this.m_scheduler.deallocateFromThreadpool(this);
			Thread.currentThread().interrupt();
		} finally {
			this.m_scheduler.deallocateFromThreadpool(this);
		}
	}
}
