/*
This file is part of the Alitheia system, developed by the SQO-OSS
consortium as part of the IST FP6 SQO-OSS project, number 033331.

Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
Copyright 2007 by KDAB (www.kdab.net)
Author: Christoph Schleifenbaum <christoph@kdab.net>

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

package eu.sqooss.service.scheduler;

import java.util.Iterator;
import java.util.List;

public abstract class Job {

    /**
     * The state of the job.
     * @author christoph
     *
     */
    public enum State
	{
        Created,
        Queued,
        Running,
        Finished,
        Error
    }

    Job()
    {
        m_scheduler = null;
        m_errorException = null;
        setState( State.Created );
    }

    private State m_state;
    private Scheduler m_scheduler;
    private Exception m_errorException;

    /**
     * @return The current state of the job.
     */
    public final State state()
    {
        return m_state;
    }

    /**
     * Sets the jobs state
     */
    protected final void setState( State s )
    {
        if( m_state == s )
            return;
        m_state = s;
        if( m_scheduler != null )
            m_scheduler.jobStateChanged( this, s );
    }

    /**
     * Executes the job.
     * Makes sure, that all dependencies are met. 
     * @throws Exception
     */
    final public void execute() throws Exception
    {
        try
        {
            setState( State.Running );
            run();
            setState( State.Finished );
        }
        catch( Exception e )
        {
            m_errorException = e;
            setState( State.Error );
            throw e;
        }
    }

    /**
     * Run the job.
     * @throws Exception
     */
    abstract protected void run() throws Exception;

    /**
     * @return The priority of the job.   
     */
    abstract int priority();

    public final void callAboutToBeEnqueued( Scheduler s )
    {
        aboutToBeEnqueued( s );
        m_state = State.Queued;
        m_scheduler = s;
    }

    public final void callAboutToBeDequeued( Scheduler s )
    {
        aboutToBeDequeued( s );
        if( m_state == State.Queued )
            m_state = State.Created;
        m_scheduler = null;
    }

    /** 
     * This method is called during queueing, right before the job is added to the work queue.
     * The job is not in state Queued at this time.
     * @param s The scheduler, the job has been enqueued to 
     */
    protected void aboutToBeEnqueued( Scheduler s )
    {
    }

    /** 
     * This method is called right before the job is dequeued without being executed. 
     * 
     */
    protected void aboutToBeDequeued( Scheduler s )
    {
    }

    /**
     * @return All jobs this job depends on.
     */
    abstract List<Job> dependencies();

    /**
     * Checks, wheter all dependencies are met and the job can be executed.
     * @return true, when all dependencies are met.
     */
    boolean canExecute()
    {
        final List<Job> deps = dependencies();
        Iterator<Job> it = deps.iterator(); 
        while( it.hasNext() )
        {
            Job j = it.next();
            if( j.state() != State.Finished )
                return false;
        }
        return true;
    }
}
