/*
This file is part of the Alitheia system, developed by the SQO-OSS
consortium as part of the IST FP6 SQO-OSS project, number 033331.

Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
Copyright 2007 by KDAB (www.kdab.net)
Author: Mirko Boehm <mirko@kdab.net>

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

package eu.sqooss.impl.service.scheduler;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.logging.LogManager;

public class SchedulerServiceImpl implements Scheduler {

    private ServiceReference serviceRef = null;    

    private HttpService httpService = null;    

    private LogManager logService = null;
    
    private Logger logger = null;
    
    private List<Job> jobQueue;

    public SchedulerServiceImpl(BundleContext bc) throws NamespaceException {
        
        serviceRef = bc.getServiceReference("eu.sqooss.service.logging.LogManager");
        logService = (LogManager) bc.getService(serviceRef);
        if (logService != null) {
            logger = logService.createLogger("sqooss.scheduler");
            if(logger != null) {
            	logger.info("Got logging!");
            }
        }
        if (logger != null) {
            logger.info("Got scheduling!");
        } else {
            System.out.println("! Got scheduler but no logging.");
        }
    }

	public void enqueue( Job job )
    {
		logger.info("SchedulerServiceImpl: queuing job " + job.toString() );
		job.callAboutToBeEnqueued( this );
		jobQueue.add(job);
	}
	
	public void dequeue( Job job )
    {
		if( !jobQueue.contains( job ) )
		{
            if( logger != null )
    			logger.info("SchedulerServiceImpl: job " + job.toString() + " not found in the queue.");
			return;
		}
		job.callAboutToBeDequeued( this );
		jobQueue.remove( job );
        if( logger != null )
    		logger.info("SchedulerServiceImpl: job " + job.toString() + " not found in the queue." );
	}

    public void jobStateChanged( Job job, Job.State state )
    {
        if( logger != null )
            logger.info( "Job" + job + "changed to state" + state );
    }

    public Object selfTest() {
        if( logger != null )
            logger.info( "Starting scheduler selftest..." );

        Job firstJob = new TestJob();
        Job secondJob = new TestJob();

        // firstJob depends on secondJob
        firstJob.addDependency( secondJob );

        List< Job > dependencies = firstJob.dependencies();
        if( dependencies.size() != 1 )
            return new String( "Scheduler test failed: dependencies.size() != 1" );
        if( !dependencies.contains( secondJob ) )
            return new String( "Scheduler test failed: !dependencies.contains( secondJob )" );
        dependencies = secondJob.dependencies();
        if( dependencies.size() != 0 )
            return new String( "Scheduler test failed: dependencies.size() != 0" );
        if( !secondJob.canExecute() )
            return new String( "Scheduler test failed: !secondJob.canExecute()" );
        if( firstJob.canExecute() )
            return new String( "Scheduler test failed: firstJob.canExecute()" );

        return null;
    }
}
