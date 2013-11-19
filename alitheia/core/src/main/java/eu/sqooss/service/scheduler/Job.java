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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.lang.Comparable;
import java.lang.InterruptedException;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.scheduler.BaseWorker;
import eu.sqooss.impl.service.scheduler.DependencyManager;
import eu.sqooss.impl.service.scheduler.OneShotWorker;
import eu.sqooss.impl.service.scheduler.SchedulerServiceImpl;
import eu.sqooss.service.util.Pair;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.scheduler.SchedulerException;

/**
 * Abstract base class for all jobs running by the scheduler.
 * 
 * @author Christoph Schleifenbam
 */
public abstract class Job implements Comparable<Job> {

	/**
	 * The state of the job.
	 * 
	 * @author christoph
	 * 
	 */
	public enum State {
		Created, Queued, Running, Finished, Error, Yielded
	}

	/**
	 * This list contains the dependencies between the jobs. Each pair defines
	 * that the \a second one's execution depends on completion of the \a first
	 * one.
	 * 
	 * As soon as the \a first job is finished, the pair is removed from the
	 * list.
	 */
	protected List<Pair<Job, Job>> m_dependencies;

	/**
	 * List of jobs which depend on this job
	 * 
	 * @deprecated Dependencies are managed by the {@link DependencyManager}
	 */
	@Deprecated
	private List<Job> m_dependees;

	/**
	 * A list of objects that listen to this job's state changes
	 */
	private List<JobStateListener> listeners;

	private State m_state;

	private Scheduler m_scheduler;

	private Exception m_errorException;

	private int restarts = 0;

	private ResumePoint resumePoint;

	/**
	 * @deprecated {@link WorkerThread} has been replaced with
	 *             {@link BaseWorker} this should not be used anymore.
	 * @param worker
	 */
	@Deprecated
	public void setWorkerThread(WorkerThread worker) {
	}

	/**
	 * @deprecated {@link WorkerThread} has been replaced with
	 *             {@link BaseWorker} this should not be used anymore.
	 * @return null
	 */
	@Deprecated
	public WorkerThread getWorkerThread() {
		return null;
	}

	/**
	 * @return The current state of the job.
	 */
	public final State state() {
		return m_state;
	}

	/**
	 * Returns the Scheduler this Job was enqueued to.
	 * 
	 * @return {@link Scheduler}
	 */
	public Scheduler getScheduler() {
		return m_scheduler;
	}

	/**
	 * Adds a dependency. This job cannot be executed, as long \a other is not
	 * finished.
	 * 
	 * @deprecated This is handled by the {@link DependencyManager} so that
	 *             should be used.
	 */
	@Deprecated
	public final synchronized void addDependency(Job other)
			throws SchedulerException {
		DependencyManager.getInstance().addDependency(this, other);
	}

	/**
	 * Removes a dependency. \sa addDependency
	 * 
	 * @deprecated This is handled by the {@link DependencyManager} so that
	 *             should be used.
	 */
	@Deprecated
	public final void removeDependency(Job other) {
		DependencyManager.getInstance().removeDependency(this, other);
	}

	/**
	 * Look in the {@link DependencyManager} of the {@link SchedulerServiceImpl}
	 * to which this job belongs if this job and another depend on each other.
	 * 
	 * @deprecated This is handled by the {@link DependencyManager} so that
	 *             should be used.
	 * @param other
	 *            the job to check dependency of.
	 * @return true, when the job depends on \a other, otherwise false.
	 */
	@Deprecated
	public final boolean dependsOn(Job other) {
		return DependencyManager.getInstance().dependsOn(this, other);
	}

	/**
	 * @deprecated removing a Dependee is no longer necessary since this is
	 *             handled by the {@link DependencyManager}.
	 * @param other
	 */
	@Deprecated
	private final synchronized void removeDependee(Job other) {
	}

	/**
	 * Executes the job. Makes sure that all dependencies are met.
	 * 
	 * @return The time required to execute the Job in milliseconds.
	 * @throws Exception
	 */
	final public long execute() throws Exception {
		DBService dbs;
		long timer;
		try {// Try to get the DBservice
			dbs = AlitheiaCore.getInstance().getDBService();
			timer = System.currentTimeMillis();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		try {
			setState(State.Running);
			restart();

			/* Idiot/bad programmer proofing */
			assert (!dbs.isDBSessionActive());
			if (dbs.isDBSessionActive()) {
				dbs.rollbackDBSession();
				setState(State.Error); // No uncommitted sessions are tolerated
			} else {
				if (state() != State.Yielded)
					setState(State.Finished);
			}
		} catch (Exception e) {

			if (dbs.isDBSessionActive()) {
				dbs.rollbackDBSession();
			}
			// In case of an exception, state becomes Error
			m_errorException = e;
			setState(State.Error);
			// the Exception itself is forwarded
			throw e;
		}
		return System.currentTimeMillis() - timer;
	}

	/**
	 * Sets the job's state to Queued and informs the job about the new
	 * scheduler. This method should only be called by Scheduler.enqueue.
	 * 
	 * @throws SchedulerException
	 *             If the job is already enqueued.
	 */
	public final void callAboutToBeEnqueued(Scheduler s)
			throws SchedulerException {
		if (m_scheduler != null) {
			throw new SchedulerException(
					"This job is already enqueued in a scheduler.");
		}
		aboutToBeEnqueued(s);
		m_state = State.Queued;
		m_scheduler = s;
	}

	/**
	 * Sets the job's state back from Queued to Created and informs about being
	 * dequeud. This method should only be called by Scheduler.dequeue.
	 */
	public final void callAboutToBeDequeued(Scheduler s) {
		aboutToBeDequeued(s);

		if (m_state == State.Queued) {
			m_state = State.Created;
		}

		m_scheduler = null;
	}

	/**
	 * The priority of the job is the order of job within the scheduler's queue.
	 * That leads to 0 being taking he highest precedence, then the higher
	 * numbers. It is not adviced to change the job's priority after it has been
	 * enqueued. That might lead to undefined behaviour.
	 * 
	 * @return The priority of the job.
	 */
	abstract public long priority();

	/**
	 * @return All unfinished jobs this job depends on.
	 * @deprecated This is handled by the {@link DependencyManager} now.
	 */
	@Deprecated
	public final List<Job> dependencies() {
		return DependencyManager.getInstance().getDependency(this);
	}

	/**
	 * Waits for the job to finish. Note that this method even returns when the
	 * job's state changes to Error.
	 */
	public final void waitForFinished() {
		try {
			synchronized (this) {
				if (this.state() == Job.State.Finished) {
					return;
				} else if (this.state() == Job.State.Queued
						|| this.state() == Job.State.Yielded) {
					this.m_scheduler.startOneShotWorker(this);
				} else if (this.state() == Job.State.Error) {
					throw new Exception(
							"The job is unable to be executed, it either ended in error state.");
				} else if (this.state() == Job.State.Created) {
					throw new Exception(
							"The job is unable to be executed, state was created. It might not be enqueued yet.");
				}

				while (state() != State.Finished) {
					if (state() == State.Error) {
						return;
					}
					try {
						wait();
					} catch (InterruptedException e) {}
				}
			}
		} catch (Exception e) {
			// if something went wrong with taking the job
			// ok - we might be stuck...
			// TODO check this work properly should
			// if (m_scheduler.getSchedulerStats().getIdleWorkerThreads() == 0)
			// {
			// // m_scheduler.startOneShotWorkerThread();
			// }
		}
	}

	/**
	 * Checks, whether all dependencies are met and the job can be executed.
	 * 
	 * @return true, when all dependencies are met.
	 * @deprecated this is handled by {@link DependencyManager}
	 */
	public boolean canExecute() {
		return DependencyManager.getInstance().canExecute(this);
	}

	/**
	 * Return the exception that caused this Job to quit
	 * 
	 * @return An exception object or null if the job has finished normally
	 */
	public final Exception getErrorException() {
		return this.m_errorException;
	}

	/**
	 * XXX bogus method, only used for putting it in into a Pair.
	 * 
	 * @deprecated Since pairs are no longer used this has not necessary.
	 */
	public int compareTo(Job other) {
		return (this == other) ? 0 : 1;
	}

	/**
	 * Protected default constructor.
	 */
	protected Job() {
		m_scheduler = null;
		m_errorException = null;
		setState(State.Created);
	}

	/**
	 * Sets the job's state.
	 * 
	 * @param s
	 *            The new state.
	 */
	protected final void setState(State s) {
		if (m_state == s) {
			return;
		}

		m_state = s;

		stateChanged(m_state);
		fireStateChangedEvent();

		synchronized (this) {
			notifyAll();
		}
	}

	/**
	 * Called, when the state of the job changed to \a state. The default
	 * implementation does nothing.
	 */
	protected void stateChanged(State state) {

	}

	/**
	 * If the job is queued to a scheduler, this methods tells the scheduler,
	 * that the job's dependencies have changed.
	 * 
	 * @deprecated since {@link DependencyManager} handled all dependencies this
	 *             call is not necessary.
	 */
	@Deprecated
	protected final void callDependenciesChanged() {
	}

	/**
	 * Restart a failing job by keeping count of the number of restarts
	 * 
	 * @throws Exception
	 *             to signify that the maximum number of restarts was reached
	 */
	protected void restart() throws Exception {
		restarts++;
		if (restarts >= 5) {
			throw new Exception("Too many restarts - failing job");
		}
		run();
	}

	/**
	 * Run the job.
	 * 
	 * @throws Exception
	 *             If thrown, the job ends up in Error state.
	 */
	abstract protected void run() throws Exception;

	/**
	 * Stop execution of the job until
	 * 
	 * @param p
	 * @throws SchedulerException
	 */
	public void yield(ResumePoint p) throws SchedulerException {
		synchronized (this) {
			if (m_state == State.Running) {
				setState(State.Yielded);
				this.resumePoint = p;
				m_scheduler.yield(this, p);
			} else {
				throw new SchedulerException("Cannot yield non-running job: "
						+ this + " (state was:" + state() + ")");
			}
		}

	}

	public long resume() throws Exception {
		long ts = System.currentTimeMillis();
		DBService dbs = AlitheiaCore.getInstance().getDBService();
		if (state() != State.Yielded)
			throw new SchedulerException("Cannot resume a non-yielded job");

		if (resumePoint == null)
			throw new SchedulerException("Resume point is null");

		try {
			setState(State.Running);
			resumePoint.resume();
			assert (!dbs.isDBSessionActive());
			if (dbs.isDBSessionActive()) {
				dbs.rollbackDBSession();
				setState(State.Error); // No uncommitted sessions are tolerated
			} else {
				setState(State.Finished);
			}
		} catch (Exception e) {

			if (dbs.isDBSessionActive()) {
				dbs.rollbackDBSession();
			}

			// In case of an exception, state becomes Error
			m_errorException = e;
			setState(State.Error);
			// the Exception itself is forwarded
			throw e;
		}

		return System.currentTimeMillis() - ts;
	}

	/**
	 * This method is called during queueing, right before the job is added to
	 * the work queue. The job is not in state Queued at this time.
	 * 
	 * @param s
	 *            The scheduler, the job has been enqueued to.
	 */
	protected void aboutToBeEnqueued(Scheduler s) {
	}

	/**
	 * This method is called right before the job is dequeued without being
	 * executed. The job is still in it's previoues state.
	 * 
	 * @parem s The scheduler, the job is dequeued from.
	 */
	protected void aboutToBeDequeued(Scheduler s) {
	}

	/**
	 * Add a listener from the job's list of state listeners
	 */
	public final synchronized void addJobStateListener(JobStateListener l) {
		if (listeners == null)
			listeners = new ArrayList<JobStateListener>();
		listeners.add(l);
	}

	/**
	 * Remove a listener from the job's list of state listeners
	 * 
	 * @param l
	 *            The listener to remove'
	 */
	public final synchronized void removeJobStateListener(JobStateListener l) {
		if (listeners == null)
			return;
		listeners.remove(l);
	}

	/**
	 * Called when the job's state has changed to notify clients about that.
	 */
	private void fireStateChangedEvent() {
		if (listeners == null)
			return;
		for (JobStateListener l : listeners) {
			l.jobStateChanged(this, m_state);
		}
	}
}
