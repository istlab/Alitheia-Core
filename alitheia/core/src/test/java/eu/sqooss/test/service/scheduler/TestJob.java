package eu.sqooss.test.service.scheduler;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.scheduler.SchedulerServiceImpl;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Job.State;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.service.scheduler.WorkerThread;
import eu.sqooss.test.service.scheduler.TestJobObject;

public class TestJob {
    
    static SchedulerServiceImpl sched;
    
    @BeforeClass
    public static void setUp() {
    	try {
    		AlitheiaCore.testInstance();
    	} catch (Exception e) {}
    	sched = new SchedulerServiceImpl();
    }
	
	@Test
	public void TestStateGetter() {
		TestJobObject jb1 = new TestJobObject(1, null);
		assertEquals(Job.State.Created,jb1.state());
	}
	
	@Test
	public void TestDependencies() throws Exception{
		Scheduler sched = new SchedulerServiceImpl();
		TestJobObject jb1 = new TestJobObject(1, null);
		TestJobObject jb2 = new TestJobObject(2, null);
		TestJobObject jb3 = new TestJobObject(3, null);
		
		jb1.addDependency(jb2);
		assertEquals(1,jb1.dependencies().size());
		jb2.addDependency(jb3);
		assertTrue(jb1.dependsOn(jb2));
		assertFalse(jb1.canExecute());
		
		jb1.removeDependency(jb2);
		assertEquals(0,jb1.dependencies().size());
		assertFalse(jb1.dependsOn(jb2));
		assertTrue(jb1.canExecute());
		
		assertEquals(0,jb3.dependencies().size());
		jb3.removeDependency(jb2);
		assertEquals(0,jb3.dependencies().size());
		
		jb1.stateChange(State.Queued);
		assertEquals(Job.State.Queued,jb1.state());
		try {
			jb1.addDependency(jb2);
			fail();
		} catch (SchedulerException e) {}
		
		jb1 = new TestJobObject(1, null);
		jb2 = new TestJobObject(2, null);
//		jb3 = new TestJobObject(3, null);
		jb1.addDependency(jb2);
		assertFalse(jb1.canExecute());
		jb2.stateChange(Job.State.Finished);
		assertTrue(jb1.canExecute());
		jb2.stateChange(Job.State.Finished);
		assertTrue(jb1.canExecute());
	}
	
	@Test
	public void TestPriorities() throws Exception {
		TestJobObject tb1 = new TestJobObject(1, null);
		TestJobObject tb2 = new TestJobObject(1, null);
		
		assertEquals(tb1.priority(),tb2.priority());
	}
	
	@Test
	public void TestEquals() throws Exception {
		TestJobObject tb1 = new TestJobObject(1, null);
		TestJobObject tb2 = new TestJobObject(1, null);
		
		assertTrue(tb1.equals(tb1));
		assertEquals(0,tb2.compareTo(tb2));
		assertFalse(tb1.equals(tb2));
		assertEquals(1,tb1.compareTo(tb2));
	}
	
	@Test
	public void TestState() throws Exception {
		TestJobObject tb1 = new TestJobObject(1, null);
		TestJobObject tb3 = new TestJobObject(1, null);
		tb1.stateChange(State.Created);
		
		assertEquals(Job.State.Created,tb1.state());
		tb1.stateChange(State.Created);
		assertEquals(Job.State.Created,tb1.state());
		
		tb3.addDependency(tb1);
		tb1.addDependency(new TestJobObject(2, null));
		tb1.stateChange(State.Finished);
		assertEquals(Job.State.Finished,tb1.state());
		
		
		
	}
	
	@Test
	public void TestRunning() throws SchedulerException {
		TestJobObject tb1 = new TestJobObject(1, null);
		assertEquals(Job.State.Created, tb1.state());
		
		sched.startExecute(1);
		sched.enqueue(tb1);
		assertEquals(sched,tb1.getScheduler());
		
		tb1.waitForFinished();
		assertEquals(Job.State.Finished, tb1.state());
		
		tb1 = new TestJobObject(1, null);
		assertEquals(Job.State.Created, tb1.state());
		sched.enqueue(tb1);
		while(tb1.state().equals(Job.State.Queued) || tb1.state().equals(Job.State.Running) ) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				fail("Error while testing");
			}
		}
		assertEquals(Job.State.Finished, tb1.state());
		sched.shutDown();
		
//		sched.startExecute(4);
//		tb1 = new TestJobObject(1, null);
//		TestJobObject tb2 = new TestJobObject(1, null);
//		sched.enqueue(tb2);
//		sched.enqueue(tb1);
//		assertEquals(1,sched.getSchedulerStats().getIdleWorkerThreads());
//		tb1.waitForFinished();
//		while(tb2.state().equals(Job.State.Queued) || tb2.state().equals(Job.State.Running) ) {
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				fail("Error while testing");
//			}
//		}
//		assertEquals(Job.State.Finished, tb1.state());
	}
	
	@Test
	public void TestThreads() throws Exception {
		TestJobObject tb1 = new TestJobObject(1, null);
		assertEquals(null,tb1.getWorkerThread());
		
		WorkerThread wt = new WorkerThread() {
			public void takeJob(Job job) throws SchedulerException {}
			public void stopProcessing() {}
			public void start() {}
			public Job executedJob() {return new TestJobObject(0, null);}
		};
		tb1.setWorkerThread(wt);
		assertNull(tb1.getWorkerThread());
		
		tb1.setWorkerThread(null);
		assertEquals(null, tb1.getWorkerThread());
	}
	
	@AfterClass
    public static void tearDown() {}
}
