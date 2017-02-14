package eu.sqooss.test.service.scheduler;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.gemini.blueprint.mock.MockBundleContext;
import org.eclipse.gemini.blueprint.mock.MockServiceReference;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.logging.LoggerImpl;
import eu.sqooss.impl.service.scheduler.SchedulerServiceImpl;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.ResumePoint;
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.service.scheduler.SchedulerStats;

public class TestSchedulerServiceImpl {
    
    
	@BeforeClass
    public static void setUp() {
    	
    	 try{
         	AlitheiaCore.testInstance();
         }catch(Exception e){
        	 //Workarround to making testing work
         }
    	 
    }

    @Test(expected=SchedulerException.class)
    public void testEnqueueAlreadyEnqueuedJob() throws SchedulerException {
    	SchedulerServiceImpl sched = new SchedulerServiceImpl();
    	TestJob j1 = new TestJob(20, "Test");
        sched.enqueue(j1);
        sched.enqueue(j1);
    }
    @Test
    public void testEnqueueMultipleJobs() throws SchedulerException {
    	//Clean scheduler
    	SchedulerServiceImpl sched = new SchedulerServiceImpl();
    	//add jobs
    	TestJob j1 = new TestJob(20, "J1");
    	TestJob j2 = new TestJob(20, "J2");
    	TestJob j3 = new TestJob(20, "J3");
        sched.enqueue(j1);
        sched.enqueue(j2);
        sched.enqueue(j3);
        //
        assertEquals(3, sched.getSchedulerStats().getTotalJobs());
 	  
    }
    
    @Test
    public void testEnqueueMultipleJobsWithPriorities() throws SchedulerException, InterruptedException {
    	//Clean scheduler
    	SchedulerServiceImpl sched = new SchedulerServiceImpl();
    	//add jobs
    	TestJob j1 = new TestJob(20, "J1", 10l);
    	TestJob j2 = new TestJob(20, "J2", 20l);
    	TestJob j3 = new TestJob(20, "J3", 5l);
        sched.enqueue(j1);
        sched.enqueue(j2);
        sched.enqueue(j3);
        assertEquals(j3, sched.takeJob());
        assertEquals(j1, sched.takeJob());
        assertEquals(j2, sched.takeJob());
 	  
    }
    
//    Dont test here because it belongs to Job? And only concerns private fields in SchedulerServiceImpl?
//    @Test 
//    public void testJobDependenciesAfterEnqueue() throws SchedulerException{
//    	//Clean scheduler
//    	SchedulerServiceImpl sched = new SchedulerServiceImpl();
//    	TestJob j1 = new TestJob(20, "J1");
//    	TestJob j2 = new TestJob(20, "J2");
//    	TestJob j3 = new TestJob(20, "J3");
//    }
//   
    
    @Test
    public void testEnqueueBlock() throws SchedulerException {
    	//Clean scheduler
    	SchedulerServiceImpl sched = new SchedulerServiceImpl();
    	sched.startExecute(2);
    	//add jobs
    	List<Job> jobList = new ArrayList<Job>();
    	TestJob j1 = new TestJob(20, "J1");
    	jobList.add(j1);
    	TestJob j2 = new TestJob(20, "J2");
    	jobList.add(j2);
    	TestJob j3 = new TestJob(20, "J3");
    	jobList.add(j3);
    	assertEquals("Test ammount of jobs", 0, sched.getSchedulerStats().getTotalJobs());
    	sched.enqueueBlock(jobList);
    	assertEquals("Test ammount of jobs", 3, sched.getSchedulerStats().getTotalJobs());
    	sched.stopExecute();
    }
    @Test
    public void testDequeue() throws SchedulerException {
    	//Clean scheduler
    	SchedulerServiceImpl sched = new SchedulerServiceImpl();
    	//add jobs
    	TestJob j1 = new TestJob(20, "J1");
        sched.enqueue(j1);
        TestJob j2= new TestJob(20, "J2");
        sched.enqueue(j2);
        assertEquals("Job state is enqueued", Job.State.Queued, j1.state() );
        assertEquals("Job state is enqueued", Job.State.Queued, j2.state() );
        sched.dequeue(j1);
        assertEquals("Job dequeued thus state is created", Job.State.Created, j1.state() );
        assertEquals("Job not dequeued thus state is queued", Job.State.Queued , j2.state());
    }
    
    @Test(expected=SchedulerException.class)
    public void testTakeUnqueuedSpecificJob() throws SchedulerException {
    	SchedulerServiceImpl sched = new SchedulerServiceImpl();
    	//add jobs
    	TestJob j1 = new TestJob(20, "J1");
    	Job job = sched.takeJob(j1);
    }
    
    @Test
    public void testTakeQueuedSpecificJob() throws SchedulerException {
    	SchedulerServiceImpl sched = new SchedulerServiceImpl();
    	//add jobs
    	TestJob j1 = new TestJob(20, "J1");
        sched.enqueue(j1);
        TestJob j2 = new TestJob(20, "J2");
        sched.enqueue(j2);
        TestJob j3 = new TestJob(20, "J3");
        sched.enqueue(j3);
    	Job job = sched.takeJob(j1);
    	assertEquals("Correct job from the queue", job ,j1);
    	job = sched.takeJob(j2);
    	assertEquals("Correct job from the queue", job ,j2);
    	job = sched.takeJob(j3);
    	assertEquals("Correct job from the queue", job ,j3);
    	
    }
    
    @Test
    public void testExecutingJobs() {
    	SchedulerServiceImpl sched = new SchedulerServiceImpl();
    	assertEquals("Check not running", false,  sched.isExecuting());
    	sched.startExecute(2);
    	assertEquals("Check running", true,  sched.isExecuting());
    	sched.stopExecute();
    	assertEquals("Check not running", false,  sched.isExecuting());
    }

    @Test
    public void testStoppingNotExecutingJobs() {
    	SchedulerServiceImpl sched = new SchedulerServiceImpl();
    	sched.stopExecute();
    	assertEquals("Check not running", false,  sched.isExecuting());
    	
    }
    
    @Test
    public void testGetSchedulerStats(){
    	SchedulerServiceImpl sched = new SchedulerServiceImpl();
    	SchedulerStats stats  = sched.getSchedulerStats();
    	assertSame(SchedulerStats.class, stats.getClass());
    }
    
    @Test
    public void testFailed() throws SchedulerException{
    	SchedulerServiceImpl sched = new SchedulerServiceImpl();
    	sched.startExecute(2);
    	FailingJob job1 = new FailingJob("J1");
    	FailingJob job2 = new FailingJob("J2");
    	FailingJob job3 = new FailingJob("J3");
    	sched.enqueue(job1);
    	sched.enqueue(job2);
    	sched.enqueue(job3);
    	
    	assertEquals("No failing jobs yet", 0, sched.getSchedulerStats().getFailedJobs());
    	
    	while (sched.getSchedulerStats().getFailedJobs() < 3)
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}
            
        assertEquals("Test 3 failing jobs", 3, sched.getSchedulerStats().getFailedJobs());
        sched.stopExecute();
    	
    }
    
    @Test
    public void testGetFailedQueued() throws SchedulerException{
    	SchedulerServiceImpl sched = new SchedulerServiceImpl();
    	sched.startExecute(2);
    	FailingJob job1 = new FailingJob("J1");
    	FailingJob job2 = new FailingJob("J2");
    	FailingJob job3 = new FailingJob("J3");
    	sched.enqueue(job1);
    	sched.enqueue(job2);
    	sched.enqueue(job3);
    	
    	assertEquals("No failing jobs yet", 0, sched.getSchedulerStats().getFailedJobs());
    	
    	while (sched.getSchedulerStats().getFailedJobs() < 3)
    		try {
    			Thread.sleep(500);
    		} catch (InterruptedException e) {}
    	
    	Job[] expectedArray = {job1, job2, job3};
    	ArrayList<Job> expList = new ArrayList<Job>(Arrays.asList(expectedArray));
    	ArrayList<Job> actualList = new ArrayList<Job>(Arrays.asList(sched.getFailedQueue()));
    	assertEquals(true, expList.containsAll(actualList));
    	sched.stopExecute();
    }
    
    @Test
    public void testAmountofWorkerThreads() throws SchedulerException{
    	SchedulerServiceImpl sched = new SchedulerServiceImpl();
    	sched.startExecute(2);
    	assertEquals(2, sched.getWorkerThreads().length); //Fails because of getWorkerThreads, cast not properly implemented
    	
    }	
    
    @Test
    public void testSetInitParams(){
    	SchedulerServiceImpl sched = new SchedulerServiceImpl();
    	final MockServiceReference reference = new MockServiceReference();
    	final Object service = null;
    	MockBundleContext bundleContext = new MockBundleContext() {

    		public ServiceReference getServiceReference(String clazz) {
    			return reference;
    		}

    		public ServiceReference[] getServiceReferences(String clazz, String filter) 
    				throws InvalidSyntaxException {
    			return new ServiceReference[] { reference };
    		}
    		
    		public Object getService(ServiceReference ref) {
    		    if (reference == ref)
    		       return service;
    		    return super.getService(ref);
    		}
    	};
    	Logger l = new LoggerImpl("Logger 2"); 
    	sched.setInitParams(bundleContext, l);
    	//There is not really a way of checking that this is properly processed.
    }
    	
    @Test
    public void testStartOneShotWorkerThread() throws SchedulerException{
    	SchedulerServiceImpl sched = new SchedulerServiceImpl();
    	FailingJob job1 = new FailingJob("J1");
    	FailingJob job2 = new FailingJob("J2");
    	FailingJob job3 = new FailingJob("J3");
    	sched.enqueue(job1);
    	sched.enqueue(job2);
    	sched.enqueue(job3);
    	assertEquals("Zero finished jobs", 0, sched.getSchedulerStats().getFinishedJobs());
    	sched.startOneShotWorkerThread();
                try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	assertEquals("One finished job", 1, sched.getSchedulerStats().getFinishedJobs()); //Fails for unknown reason, but at lease the boolean oneshot is incorrect
    }
    

    @Test
    public void testStartUp()  {
    	SchedulerServiceImpl sched = new SchedulerServiceImpl(); 
    	sched.startUp();
    	int numThreads = 2 * Runtime.getRuntime().availableProcessors();
    	assertEquals("Equal amount of threads", numThreads, sched.getWorkerThreads().length); //Fails because getWorkerThreads is not propery implemented
    }
    
    @Test
    public void testCreateAuxQueueWithEmtpyJobList() throws SchedulerException  {
    	SchedulerServiceImpl sched = new SchedulerServiceImpl();
    	sched.startExecute(1);
    	TestJob j1 = new TestJob(100, "J1");
    	TestJob j2 = new TestJob(20, "J2");
        sched.enqueue(j1);
        sched.enqueue(j2);
        Deque<Job> jobs = new ArrayDeque<Job>();
        ResumePoint p = new ResumePoint() { //No implementation available yet
			@Override
			public void resume() {
				//Do nothing
			}
		};
        boolean result = sched.createAuxQueue(j1, jobs, p); //Fails because not properly checked for logger.
        assertEquals("No jobs listed so returns false", false, result);
    }

    @Test
    public void testCreateAuxQueue() throws SchedulerException  {
    	SchedulerServiceImpl sched = new SchedulerServiceImpl(); 
    	TestJob j1 = new TestJob(50, "J1");
    	TestJob j2 = new TestJob(20, "J2");
    	TestJob j3 = new TestJob(20, "J3");
    	sched.enqueue(j1);
    	Deque<Job> jobs = new ArrayDeque<Job>();
    	jobs.add(j2);
    	jobs.add(j3);
    	ResumePoint p = new ResumePoint() { //No implementation available yet
    		@Override
    		public void resume() {
    			//Do nothing
    		}
    	};
    	sched.startExecute(1);
    	while(sched.getSchedulerStats().getRunningJobs()<1){
    		try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	boolean result = sched.createAuxQueue(j1, jobs, p);
    	assertEquals("Jobs given so should return true", true,result);
    	assertEquals("Two jobs added", 3, sched.getSchedulerStats().getTotalJobs());

    	//Check if depencies are added correctly
    	LinkedList<Job> expectedDependencies = new LinkedList<Job>();
    	expectedDependencies.add(j2);
    	expectedDependencies.add(j3);
    	LinkedList<Job> actualDependencies = (LinkedList<Job>) j1.dependencies();
    	assertEquals("Dependencies of job 1 changed", true, actualDependencies.containsAll(expectedDependencies));
    	//Check if job j1 is yielded
    	assertEquals("J1 should yield", Job.State.Yielded, j1.state());
    	sched.stopExecute();
    }
    @Test(expected=SchedulerException.class)
    public void testFailingYield() throws SchedulerException{
    	SchedulerServiceImpl sched = new SchedulerServiceImpl();
    	TestJob j1 = new TestJob(100, "J1");
        sched.enqueue(j1);
        ResumePoint p = new ResumePoint() {
			
			@Override
			public void resume() {
				//Do nothing
			}
		};
        sched.yield(j1, p);
    }

    @Test
    public void testSucceedingYield( ) throws SchedulerException{
    	SchedulerServiceImpl sched = new SchedulerServiceImpl();
    	TestJob j1 = new TestJob(20, "J1");
    	sched.enqueue(j1);
    	ResumePoint p = new ResumePoint() {
    		
    		@Override
    		public void resume() {
    			//Do nothing
    		}
    	};
    	sched.startExecute(1);
    	while(sched.getSchedulerStats().getRunningJobs()<1){
    		try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	sched.yield(j1, p);
    	assertEquals("There is one waiting job after yielding", 1, sched.getSchedulerStats().getWaitingJobs());
    }
    @Test
    public void testRunMultipleJobs() throws SchedulerException {
    	//Clean scheduler
    	SchedulerServiceImpl sched = new SchedulerServiceImpl();
    	sched.startExecute(2);
    	//add jobs
    	TestJob j1 = new TestJob(20, "J1");
    	TestJob j2 = new TestJob(20, "J2");
    	TestJob j3 = new TestJob(20, "J3");
        sched.enqueue(j1);
        sched.enqueue(j2);
        sched.enqueue(j3);
        assertEquals("Test waiting jobs", 3, sched.getSchedulerStats().getWaitingJobs());
        assertEquals("Test total jobs", 3, sched.getSchedulerStats().getTotalJobs());
        System.out.println("teardown"+sched.getSchedulerStats().getFinishedJobs());
    	while (sched.getSchedulerStats().getFinishedJobs() < 3)
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}
            
    	assertEquals("Test waiting jobs", 0, sched.getSchedulerStats().getWaitingJobs());
        assertEquals("Test finished jobs", 3, sched.getSchedulerStats().getFinishedJobs());
        sched.stopExecute();
    }
    @AfterClass
    public static void tearDown() {
    	
    }
}