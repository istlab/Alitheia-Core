package eu.sqooss.test.service.scheduler;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.fail;
import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.scheduler.SchedulerServiceImpl;
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.service.tds.InvalidAccessorException;

public class TestScheduler {
    
    static SchedulerServiceImpl sched;
    
    @BeforeClass
    public static void setUp() {
    	try {
    		AlitheiaCore.testInstance();
    	} catch (Exception e) {
    		// Yeah this blows. But is works
    	}
    	sched = new SchedulerServiceImpl();
    }

    @Test(expected=SchedulerException.class)
    public void TestCircularDependency1() throws Exception {
    	TestJobObject j1 = new TestJobObject(0, "circular1");
    	j1.addDependency(j1);
    }
    
    @Test(expected=SchedulerException.class)
    public void TestCircularDependency2() throws Exception {
    	TestJobObject j1 = new TestJobObject(0, "circular1");
    	TestJobObject j2 = new TestJobObject(3, "circular2");
    	j1.addDependency(j2);
    	j2.addDependency(j1);
    }
    
    @Test(expected=SchedulerException.class)
    public void TestCircularDependency3() throws Exception {
    	TestJobObject j1 = new TestJobObject(4, "circular1");
    	TestJobObject j2 = new TestJobObject(5, "circular2");
    	TestJobObject j3 = new TestJobObject(6, "circular3");
    	j1.addDependency(j2);
    	j2.addDependency(j3);
    	j3.addDependency(j1);
    }
    
    @Test
    public void TestCircularDependency4() throws Exception {
    	TestJobObject j1 = new TestJobObject(4, "circular1");
    	TestJobObject j2 = new TestJobObject(5, "circular2");
    	TestJobObject j3 = new TestJobObject(6, "circular3");
    	j1.addDependency(j2);
    	j2.addDependency(j3);
    	assertEquals(true,j2.dependsOn(j3));
    }
    
    @Test
    public void TestJobYield() throws Exception {
    	assertEquals(0, sched.getSchedulerStats().getTotalJobs());
        TestJobObject j1 = new TestJobObject(10, "Test");
        sched.enqueue(j1);
        assertEquals(1, sched.getSchedulerStats().getTotalJobs());
        TestJobObject j2 = new TestJobObject(10, "Test");
        sched.enqueue(j2);
        assertEquals(2, sched.getSchedulerStats().getTotalJobs());
        TestJobObject j3 = new TestJobObject(10, "Test");
        sched.enqueue(j3);
        assertEquals(3, sched.getSchedulerStats().getTotalJobs());
        TestJobObject j4 = new TestJobObject(10, "Test");
        sched.enqueue(j4);
        assertEquals(4, sched.getSchedulerStats().getTotalJobs());
        TestJobObject j5 = new TestJobObject(10, "Test");
        sched.enqueue(j5);
        assertEquals(5, sched.getSchedulerStats().getTotalJobs());
        sched.shutDown();
    }
    
    @Test
    public void TestScheduleEnqueue() throws Exception {
    	sched = new SchedulerServiceImpl();
    	sched.startExecute(1);
    	assertEquals(0,sched.getSchedulerStats().getTotalJobs());
    	TestJobObject tb1 = new TestJobObject(0, null);
    	HashSet<Job> alj = new HashSet<Job>(1);
    	alj.add(tb1);
    	sched.enqueue(alj);
    	assertEquals(1, sched.getSchedulerStats().getTotalJobs());
    	sched.shutDown();
    }
    
    @AfterClass
    public static void tearDown() {
        while (sched.getSchedulerStats().getFinishedJobs() < 4)
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}
            
        sched.stopExecute();
    }
}