package eu.sqooss.impl.service.scheduler;

import eu.sqooss.service.scheduler.Job;

/**
 * This a test job class.
 * It has the holy purpuse to print a string n times.
 */
class TestJob extends Job
{
    public TestJob( int n, String s )
    {
        this.n = n;
        this.s = s;
    }

    public int priority()
    {
        return 0;
    }
    
    protected void run() throws Exception
    {
        System.out.println( "Testjob running!" );
        for( int i = 0; i < n; ++i )
        {   
            Thread.sleep( 500 );
            System.out.println( s );
        }
        System.out.println( "Testjob finished!" );
    }

    private int n;
    private String s;
}
