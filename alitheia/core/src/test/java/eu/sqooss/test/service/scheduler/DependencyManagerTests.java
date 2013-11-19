package eu.sqooss.test.service.scheduler;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.scheduler.DependencyManager;
import eu.sqooss.impl.service.scheduler.SchedulerServiceImpl;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.SchedulerException;

public class DependencyManagerTests {

	 @BeforeClass
	    public static void setUp() {
	    	try {
	    		AlitheiaCore.testInstance();
	    	} catch (Exception e) {
	    		// Yeah this blows. But is works
	    	}
	    }
	 
	@Test
	public final void testGetInstance() {
		DependencyManager dm = DependencyManager.getInstance();
		assertSame("getInstance should return the samen object",
				dm, DependencyManager.getInstance());
		assertSame("getInstance(false) should return the same object", 
				dm ,DependencyManager.getInstance(false));
		assertNotSame("getInstance(true) should return a different object", 
				dm, DependencyManager.getInstance(true));
		
	}

	@Test
	public final void testDependsOn() throws SchedulerException {
		Job j1 = new TestJobObject(1, "j1");
		Job j2 = new TestJobObject(1, "j2");
		Job j3 = new TestJobObject(1, "j3");
		Job j4 = new TestJobObject(1, "j4");
		Job j5 = new TestJobObject(1, "j5");
		DependencyManager d = DependencyManager.getInstance();
		assertFalse("j1 depends on nothing yet", d.dependsOn(j1, j2));
		d.addDependency(j1, j2);
		assertTrue("j1 depends on j2 now", d.dependsOn(j1, j2));
		d.addDependency(j2, j3);
		d.addDependency(j3, j4);
		d.addDependency(j5, j1);
		assertTrue("j1 depends on j4 by proxy", d.dependsOn(j1, j4));
		assertFalse("j1 does not depend on j5", d.dependsOn(j1, j5));
		assertTrue("j5 does depend on j1", d.dependsOn(j5, j1));
		
	}

	@Test
	public final void testCanExecute() throws SchedulerException {
		//First init some things we need.
		Job j1 = new TestJobObject(1, "j1");
		Job j2 = new TestJobObject(1, "j2");
		DependencyManager d = DependencyManager.getInstance();
		
		assertTrue("J1 can run cause it has no dependencies", d.canExecute(j1));
		d.addDependency(j1, j2);
		assertTrue("J1 cant run cause j2 is not finished", d.canExecute(j2));
		SchedulerServiceImpl sched = new SchedulerServiceImpl();
		sched.enqueue(j1);
		sched.enqueue(j2);
		assertTrue("j2 has no dependencies so can execute", d.canExecute(j2));
		sched.startExecute(1);
		j2.waitForFinished();
		synchronized (sched) {
			assertFalse("Check that j1 is not finished yet", Job.State.Finished == j1.state());
			assertEquals("j2 is now finished", Job.State.Finished, j2.state() );
			assertTrue("J1 can run now since j2 is finished", d.canExecute(j1));
		}
		j1.waitForFinished();
		sched.shutDown();
		
	}

	@Test
	public final void testAddDependency() throws SchedulerException {
		Job j1 = new TestJobObject(1, "j1");
		Job j2 = new TestJobObject(1, "j2");
		Job j3 = new TestJobObject(1, "j3");
		DependencyManager d = DependencyManager.getInstance(true);
		d.addDependency(j1, j2);
		d.addDependency(j1, j3);
		assertTrue("J1 should depend on j2 now", d.dependsOn(j1,j2));
		assertTrue("J1 should depend on j3 now", d.dependsOn(j1,j3));
	}

	@Test(expected=SchedulerException.class)
	public final void testAddDependencyOnRunningJob() throws SchedulerException{
		TestJobObject j1 = new TestJobObject(1, "j1");
		TestJobObject j2 = new TestJobObject(1, "j2");
		DependencyManager d = DependencyManager.getInstance(true);
		
		j1.mockState(Job.State.Running);
		d.addDependency(j1, j2); //Should throw exception
		fail("An exception should have been thrown");
	}
	
	@Test
	public final void testAddDependencyOnYieldedJob() throws SchedulerException{
		TestJobObject j1 = new TestJobObject(1, "j1");
		TestJobObject j2 = new TestJobObject(1, "j2");
		DependencyManager d = DependencyManager.getInstance(true);
		
		List<Job> deps = new ArrayList<Job>(1);
		deps.add(j2);
		
		j1.mockState(Job.State.Yielded);
		d.addDependency(j1, j2);
		assertEquals("The dependency was added to the yielded job",deps,d.getDependency(j1));
	}
	
	@Test(expected=SchedulerException.class)
	public final void testAddDependencyOnFinishedJOb() throws SchedulerException{
		TestJobObject j1 = new TestJobObject(1, "j1");
		TestJobObject j2 = new TestJobObject(1, "j2");
		DependencyManager d = DependencyManager.getInstance(true);
		
		j1.mockState(Job.State.Finished);
		d.addDependency(j1, j2); //Should throw exception
		fail("An exception should have been thrown");
	}
	
	@Test(expected=SchedulerException.class)
	public final void testAddDependencyOnFailed() throws SchedulerException{
		TestJobObject j1 = new TestJobObject(1, "j1");
		TestJobObject j2 = new TestJobObject(1, "j2");
		DependencyManager d = DependencyManager.getInstance(true);
		
		j1.mockState(Job.State.Error);
		d.addDependency(j1, j2); //Should throw exception
		fail("An exception should have been thrown");
	}
	
	@Test(expected=SchedulerException.class)
	public final void testAddCyclicDependency() throws SchedulerException{
		TestJobObject j1 = new TestJobObject(1, "j1");
		TestJobObject j2 = new TestJobObject(1, "j2");
		DependencyManager d = DependencyManager.getInstance(true);
		d.addDependency(j1, j2);
		j1.mockState(Job.State.Error);
		d.addDependency(j2, j1); //Should throw exception no cyclic deps
		fail("An exception should have been thrown");
	}
	
	@Test
	public final void testRemoveDependency() throws SchedulerException {
		Job j1 = new TestJobObject(1, "j1");
		Job j2 = new TestJobObject(1, "j2");
		DependencyManager d = DependencyManager.getInstance(true);
		d.removeDependency(j1, j2);
		assertFalse("J1 should not depend on j2 yet", d.dependsOn(j1,j2));
		d.addDependency(j1, j2);
		assertTrue("J1 should depend on j2 now", d.dependsOn(j1,j2));
		d.removeDependency(j1, j2);
		assertFalse("J1 should not depend on j2 anymore", d.dependsOn(j1,j2));
	}

	@Test
	public final void testGetDependency() throws SchedulerException {
		Job j1 = new TestJobObject(1, "j1");
		Job j2 = new TestJobObject(1, "j2");
		Job j3 = new TestJobObject(1, "j3");
		DependencyManager d = DependencyManager.getInstance(true);
		d.addDependency(j1, j2);
		d.addDependency(j1, j3);
		LinkedList<Job> l = new LinkedList<Job>();
		l.add(j2);
		l.add(j3);
		assertEquals("J1 should have j2 and j3 as dependency", l, d.getDependency(j1));
	}

	@Test
	public final void testRemoveJobJob() throws SchedulerException {
		DependencyManager dm = DependencyManager.getInstance(true);
		Job j1 = new TestJobObject(0, "j1");
		Job j2 = new TestJobObject(0, "j2");
		Job j3 = new TestJobObject(0, "j3");
		
		List<Job> deps = new ArrayList<Job>(3);
		deps.add(j2);
		deps.add(j3);
		
		dm.addDependency(j1, j2);
		dm.addDependency(j1, j3);
		assertEquals(deps,dm.getDependency(j1));
		
		dm.removeDependency(j1, j2);
		deps.remove(j2);
		assertEquals(deps,dm.getDependency(j1));
	}

	@Test(expected=SchedulerException.class)
	public final void testDependOnEachOther() throws SchedulerException {
		DependencyManager dm = DependencyManager.getInstance(true);
		Job j1 = new TestJobObject(0, "j1");
		Job j2 = new TestJobObject(0, "j2");
		Job j3 = new TestJobObject(0, "j3");
		Job j4 = new TestJobObject(0, "j4");
		Job j5 = new TestJobObject(0, "j5");
		
		dm.addDependency(j1, j2);
		dm.addDependency(j2, j3);
		dm.addDependency(j3, j4);
		dm.addDependency(j4, j5);
		dm.addDependency(j5, j1);
		
		fail("Exception not thrown, cyclic dependency"); // TODO
	}
	
	@Test(expected=SchedulerException.class)
	public final void testDependOnEachOtherParentIsChild() throws SchedulerException {
		DependencyManager dm = DependencyManager.getInstance(true);
		Job j1 = new TestJobObject(0, "j1");
		assertSame(j1,j1);
		dm.addDependency(j1, j1);
		
		fail("Exception not thrown, cyclic dependency"); // TODO
	}

}
