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
	enum State
	{
		Created,
		Queued,
		Running,
		Finished,
		Error
	}

	/**
	 * @return The current state of the job.
	 */
	abstract State state();
	
	/**
	 * Executes the job.
	 * Makes sure, that all dependencies are met. 
	 * @throws Exception
	 */
	final public void execute() throws Exception
	{
		
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

	/** 
	 * This method is called during queueing, right before the job is added to the work queue.
	 * @param s The scheduler, the job has been enqueued to 
	 */
	public void aboutToBeEnqueued( Scheduler s )
	{
	}
	
	/** 
	 * This method is called right before the job is dequeued without being executed. 
	 * */
	public void aboutToBeDequeued( Scheduler s )
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
