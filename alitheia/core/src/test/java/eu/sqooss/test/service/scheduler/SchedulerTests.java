package eu.sqooss.test.service.scheduler;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.scheduler.SchedulerServiceImpl;
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.test.service.scheduler.TestJobObject;

public class SchedulerTests {
    
    static SchedulerServiceImpl sched;
    
    @BeforeClass
    public static void setUp() {
        sched = new SchedulerServiceImpl();
        sched.startExecute(2);
        try{
        	AlitheiaCore.testInstance();
        }catch(Exception e){
        	
        }
    }

    @Test
    public void testJobYield() throws SchedulerException {
        
    	TestJobObject j1 = new TestJobObject(20, "Test");
        sched.enqueue(j1);
        TestJobObject j2 = new TestJobObject(20, "Test");
        sched.enqueue(j2);
        TestJobObject j3 = new TestJobObject(20, "Test");
        sched.enqueue(j3);
        TestJobObject j4 = new TestJobObject(20, "Test");
        sched.enqueue(j4);
        
    }
    
    @AfterClass
    public static void tearDown() {
        while (sched.getSchedulerStats().getFinishedJobs() < 4)
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}
            
        sched.stopExecute();
        assertEquals(4, sched.getSchedulerStats().getFinishedJobs());
    }
}