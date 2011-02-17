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
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import java.lang.Comparable;
import java.lang.InterruptedException;

import eu.sqooss.core.AlitheiaCore;
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
     * @author christoph
     *
     */
    public enum State {
        Created,
        Queued,
        Running,
        Finished,
        Error
    }

    /**
     * This list contains the dependencies between the jobs.
     * Each pair defines that the \a second one's execution depends on
     * completion of the \a first one.
     *
     * As soon as the \a first job is finished, the pair is removed from
     * the list.
     */
    protected static List<Pair<Job,Job>> s_dependencies = new LinkedList<Pair<Job,Job>>();

    /**
     * A list of objects that listen to this job's state changes  
     */
    private List<JobStateListener> listeners = new ArrayList<JobStateListener>();
    
    private State m_state;

    private Scheduler m_scheduler;

    private Exception m_errorException;
    
    private WorkerThread m_worker;
    
    private int restarts = 0;
    
    public void setWorkerThread(WorkerThread worker) {
    	m_worker = worker;
    }
    
    /**
     * @return The current state of the job.
     */
    public final State state() {
        return m_state;
    }
    
    /**
     * Returns the Scheduler this Job was enqueued to.
     */
    public Scheduler getScheduler() {
    	return m_scheduler;
    }
    
    /**
     * Adds a dependency.
     * This job cannot be executed, as long \a other
     * is not finished.
     */
    public final void addDependency(Job other) throws SchedulerException {
        // Dependencies of jobs can ony be changed before the job is queued.
        // Otherwise, race conditions would occur in which it would be undefined
        // if the dependency is applied or not.
        if ( (state() != State.Created) ) {
        	throw new SchedulerException("Job dependencies cannot be added after the job has been queued.");
        }

        // Don't allow circular dependencies
        if( other.dependsOn(this) || (this==other) ) {
            throw new SchedulerException("Job dependencies are not allowed to be cyclic.");
        }

        synchronized (s_dependencies) {
            Pair<Job,Job> newDependency = new Pair<Job,Job>(other, this);
            s_dependencies.add(newDependency);
        }
        callDependenciesChanged();
    }

    /**
     * Removes a dependency.
     * \sa addDependency
     */
    public final void removeDependency(Job other) {
        synchronized(s_dependencies) {
            List<Pair<Job,Job>> doomed = new LinkedList<Pair<Job,Job>>();
            for (Pair<Job,Job> p: s_dependencies ) {
                if ( (p.first == other) && (p.second == this) ) {
                    doomed.add(p);
                }
            }
            s_dependencies.removeAll(doomed);
        }
        callDependenciesChanged();
    }

    /**
     * Checks recursive whether this job depends on job \a other.
     * @param other the job to check dependency of.
     * @return true, when the job depends on \a other, otherwise false.
     */
    public final boolean dependsOn(Job other) {
        synchronized(s_dependencies) {
            for (Pair<Job,Job> p: s_dependencies ) {
                if ( (p.first == other) && (p.second == this) ) {
                    return true;
                } else if ( (p.second == this) && p.first.dependsOn(other)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Executes the job. Makes sure that all dependencies are met. 
     * 
     * @return The time required to execute the Job in milliseconds.
     * @throws Exception
     */
    final public long execute() throws Exception {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        long timer = System.currentTimeMillis();
        try {
            setState(State.Running);
            restart();
            
            /*Idiot/bad programmer proofing*/
            assert (!dbs.isDBSessionActive());            
            if (dbs.isDBSessionActive()) {
                dbs.rollbackDBSession();
                setState(State.Error); //No uncommitted sessions are tolerated
            } else {
                setState(State.Finished);
            }   
        } catch(Exception e) {
            
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
     * scheduler.
     * This method should only be called by Scheduler.enqueue.
     * @throws SchedulerException If the job is already enqueued.
     */
    public final void callAboutToBeEnqueued(Scheduler s) throws SchedulerException {
        if (m_scheduler != null) {
            throw new SchedulerException("This job is already enqueued in a scheduler.");
        }
        aboutToBeEnqueued(s);
        m_state = State.Queued;
        m_scheduler = s;
    }

    /**
     * Sets the job's state back from Queued to Created and informs about being
     * dequeud.
     * This method should only be called by Scheduler.dequeue.
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
     * That leads to 0 being taking he highest precedence, then the higher numbers.
     * It is not adviced to change the job's priority after it has been enqueued. That
     * might lead to undefined behaviour.
     * @return The priority of the job.
     */
    abstract public long priority();

    /**
     * @return All unfinished jobs this job depends on.
     */
    public final List<Job> dependencies() {
        List<Job> result = new LinkedList<Job>();
        synchronized (s_dependencies) {
            for (Pair<Job,Job> p: s_dependencies) {
                if (p.second == this) {
                    result.add(p.first);
                }
            }
        }
        return result;
    }

    /**
     * Waits for the job to finish.
     * Note that this method even returns when the job's state changes to Error.
     */
    public final void waitForFinished() {
    	try {
            synchronized (this) {
                // if this method is running inside of a WorkerThread
                // we try to pass the job we're waiting for to the thread.
                if (Thread.currentThread() instanceof WorkerThread) {
                    WorkerThread t = (WorkerThread) Thread.currentThread();
                    t.takeJob(this);
                } else {
                    throw new Exception();
                }
            }
        } catch (Exception e) {
            // if something went wrong with taking the job
            // ok - we might be stuck...
            if (m_scheduler.getSchedulerStats().getIdleWorkerThreads() == 0) {
                m_scheduler.startOneShotWorkerThread();
            }
        }
        synchronized (this) {
            while (state() != State.Finished) {
                if (state() == State.Error) {
                    return;
                }
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    /**
     * Checks, whether all dependencies are met and the job can be executed.
     * @return true, when all dependencies are met.
     */
    public boolean canExecute() {
        final List<Job> deps = dependencies();
        Iterator<Job> it = deps.iterator();
        while (it.hasNext()) {
            Job j = it.next();
            if (j.state() != State.Finished) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Return the exception that caused this Job to quit
     * @return An exception object or null if the job has finished normally
     */
    public final Exception getErrorException() {
        return this.m_errorException;
    }

    /**
     * XXX bogus method, only used for putting it in into a Pair.
     */
    public int compareTo(Job other)
    {
        return (this == other) ? 0 : 1;
    }

    /**
     * Protected default constructor.
     */
    protected Job() {
        m_scheduler = null;
        m_errorException = null;
        setState( State.Created );
    }

    /**
     * Sets the job's state.
     * @param s The new state.
     */
    protected final void setState(State s) {
        if (m_state == s) {
            return;
        }

        m_state = s;

        if (m_state == State.Finished) {
            // remove the job from the dependency list
            List<Job> unblockedJobs = new LinkedList<Job>();
            synchronized (s_dependencies) {
                List<Pair<Job,Job>> doomed = new LinkedList<Pair<Job,Job>>();
                for (Pair<Job,Job> p: s_dependencies) {
                    if (p.first == this) {
                        doomed.add(p);
                        unblockedJobs.add(p.second);
                    }
                }
                s_dependencies.removeAll(doomed);
            }
            /* tell all jobs depending on the now finished on to forward that
             * to the scheduler
             */
            for (Job j: unblockedJobs) {
                j.callDependenciesChanged();
            }
        }
        if (m_scheduler != null) {
            m_scheduler.jobStateChanged(this, s);
        }

        stateChanged(m_state);
        fireStateChangedEvent();
        
        synchronized(this) {
        	notifyAll();
        }
    }

    /**
     * Called, when the state of the job changed to \a state.
     * The default implementation does nothing.
     */
    protected void stateChanged(State state) {
        
    }

    /**
     * If the job is queued to a scheduler, this methods tells the scheduler,
     * that the job's dependencies have changed.
     */
    protected final void callDependenciesChanged() {
        if (m_scheduler != null) {
            m_scheduler.jobDependenciesChanged(this);
        }
    }

    /**
     * Restart a failing job by keeping count of the number of restarts
     * @throws Exception to signify that the maximum number of restarts
     * was reached
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
     *                 If thrown, the job ends up in Error state.
     */
    abstract protected void run() throws Exception;

    /**
     * This method is called during queueing, right before the job is added to
     * the work queue.
     * The job is not in state Queued at this time.
     * @param s The scheduler, the job has been enqueued to.
     */
    protected void aboutToBeEnqueued(Scheduler s) {
    }

    /**
     * This method is called right before the job is dequeued without being
     * executed.
     * The job is still in it's previoues state.
     * @parem s The scheduler, the job is dequeued from.
     */
    protected void aboutToBeDequeued(Scheduler s) {
    }
    
    /**
     * Add a listener from the job's list of state listeners
     */
    public final synchronized void addJobStateListener(JobStateListener l) {
        listeners.add(l);
    }
    
    /**
     * Remove a listener from the job's list of state listeners
     * @param l The listener to remove'
     */
    public final synchronized void removeJobStateListener(JobStateListener l) {
        listeners.remove(l);
    }
    
    /**
     * Called when the job's state has changed to notify clients about that.
     */
    private void fireStateChangedEvent() {
        for (JobStateListener l : listeners) {
            l.jobStateChanged(this, m_state);
        }
    }
}
