package eu.sqooss.test.service.scheduler;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.mockito.Mockito.*;

import eu.sqooss.impl.service.scheduler.SchedulerServiceImpl;
import eu.sqooss.impl.service.scheduler.WorkerThreadFactory;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.service.scheduler.WorkerThread;

public class SchedulerTests {
    
    static SchedulerServiceImpl sched;
    
    @BeforeClass
    public static void setUp() {
    	WorkerThreadFactory wtf = mock(WorkerThreadFactory.class);
    	WorkerThread wt = mock(WorkerThread.class);
    	when(wtf.create(any(Scheduler.class), anyInt())).thenReturn(wt);
        sched = new SchedulerServiceImpl(wtf);
        sched.startExecute(2);
    }

    @Test
    public void testJobYield() throws SchedulerException {
        
        TestJob j1 = new TestJob(20, "Test");
        sched.enqueue(j1);
        TestJob j2 = new TestJob(20, "Test");
        sched.enqueue(j2);
        TestJob j3 = new TestJob(20, "Test");
        sched.enqueue(j3);
        TestJob j4 = new TestJob(20, "Test");
        sched.enqueue(j4);
    }
    
    @AfterClass
    public static void tearDown() {
        while (sched.getSchedulerStats().getWaitingJobs() > 0)
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}
            
        sched.stopExecute();
    }
}