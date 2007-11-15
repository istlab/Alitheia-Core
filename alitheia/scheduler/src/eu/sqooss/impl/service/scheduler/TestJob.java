package eu.sqooss.impl.service.scheduler;

import eu.sqooss.service.scheduler.Job;

class TestJob extends Job
{
    public int priority()
    {
        return 0;
    }
    
    protected void run() throws Exception
    {
        System.out.println( "Testjob running!" );
    }
}
