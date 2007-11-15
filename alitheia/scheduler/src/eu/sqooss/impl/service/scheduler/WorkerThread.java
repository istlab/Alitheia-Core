package eu.sqooss.impl.service.scheduler;

import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.Job;

import java.lang.InterruptedException;

class WorkerThread extends Thread
{
    public WorkerThread( Scheduler s )
    {
        m_scheduler = s;
    }

    public void run()
    {
        m_processing = true;
        while( m_processing )
        {
            Job j = null;
            try
            {
                j = m_scheduler.takeJob();
            }
            catch( InterruptedException e )
            {
                continue;
            }
            try
            {
                j.execute();
            }
            catch( Exception e )
            {
                // TODO: Error handling?
            }
        }
    }

    /**
     * Stops processing of jobs, after the current job has finished.
     */
    public void stopProcessing()
    {
        m_processing = false;
        interrupt();
    }

    private Scheduler m_scheduler;
    private boolean m_processing;
}
