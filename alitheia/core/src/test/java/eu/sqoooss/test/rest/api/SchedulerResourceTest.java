package eu.sqoooss.test.rest.api;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.jboss.resteasy.mock.MockHttpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import eu.sqoooss.test.rest.api.utils.TestUtils;
import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.rest.api.SchedulerResource;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerStats;

@PrepareForTest({ Scheduler.class, AlitheiaCore.class, Job.class })
@RunWith(PowerMockRunner.class)
public class SchedulerResourceTest {

	private Scheduler s;
	
	private void httpRequestFireAndTestAssertations(String api_path, String r)
			throws URISyntaxException {
		MockHttpResponse response = TestUtils.fireMockHttpRequest(
				SchedulerResource.class, api_path);
		System.out.println(response.getContentAsString());
		assertEquals(HttpServletResponse.SC_OK, response.getStatus());
		assertEquals(r, response.getContentAsString());
	}

	@Before
	public void setUp() {
		AlitheiaCore core = PowerMockito.mock(AlitheiaCore.class);
		PowerMockito.mockStatic(AlitheiaCore.class);
		Mockito.when(AlitheiaCore.getInstance()).thenReturn(core);

		s = PowerMockito.mock(Scheduler.class);
		Mockito.when(core.getScheduler()).thenReturn(s);
	}

	@After
	public void tearDown() {
		s = null;
	}
	     
	@Test
	public void testGetSchedulerStats() throws Exception {
		String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<scheduler-stats><totalJobs>0</totalJobs><finishedJobs>0</finishedJobs><waitingJobs>0</waitingJobs><runningJobs>0</runningJobs><workerThreads>0</workerThreads><idleWorkerThreads>0</idleWorkerThreads><failedJobs>0</failedJobs><failedJobTypes/><waitingJobTypes/></scheduler-stats>";
		
		SchedulerStats ss = new SchedulerStats();
		Mockito.when(s.getSchedulerStats()).thenReturn(ss);
		httpRequestFireAndTestAssertations("api/scheduler/stats", r);
	}

	@Test
	public void testGetFailedJobTypes() throws Exception {
		String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
					+ "<collection><map_entry><key xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xsi:type=\"xs:string\">test1</key>"
					+ "<value xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xsi:type=\"xs:int\">1</value></map_entry><map_entry><key xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xsi:type=\"xs:string\">test1</key>"
					+ "<value xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xsi:type=\"xs:int\">1</value></map_entry><map_entry><key xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xsi:type=\"xs:string\">test2</key>"
					+ "<value xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xsi:type=\"xs:int\">2</value></map_entry></collection>";

		SchedulerStats ss = PowerMockito.mock(SchedulerStats.class);
		Mockito.when(s.getSchedulerStats()).thenReturn(ss);

		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("test1", 1);
		Mockito.when(ss.getFailedJobTypes()).thenReturn(map);

		httpRequestFireAndTestAssertations(
				"api/scheduler/stats/jobtypes/failed", r);
	}

	@Test
	public void testGetWaitingJobTypes() throws Exception {
		String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<collection><map_entry><key xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xsi:type=\"xs:string\">test1</key>"
				+ "<value xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xsi:type=\"xs:int\">1</value></map_entry><map_entry><key xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xsi:type=\"xs:string\">test1</key>"
				+ "<value xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xsi:type=\"xs:int\">1</value></map_entry><map_entry><key xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xsi:type=\"xs:string\">test2</key>"
				+ "<value xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xsi:type=\"xs:int\">2</value></map_entry></collection>";

		SchedulerStats ss = PowerMockito.mock(SchedulerStats.class);
		Mockito.when(s.getSchedulerStats()).thenReturn(ss);

		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("test1", 1);
		Mockito.when(ss.getWaitingJobTypes()).thenReturn(map);

		httpRequestFireAndTestAssertations(
				"api/scheduler/stats/jobtypes/waiting", r);
	}

	@Test
	public void testGetRunJobs() throws Exception {
		String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<collection><string><value>Test</value></string><string><value>test1</value></string><string><value>test2</value></string></collection>";

		SchedulerStats ss = PowerMockito.mock(SchedulerStats.class);
		Mockito.when(s.getSchedulerStats()).thenReturn(ss);

		List<String> rjobs = new ArrayList<String>();
		rjobs.add("Test");
		Mockito.when(ss.getRunJobs()).thenReturn(rjobs);

		httpRequestFireAndTestAssertations("api/scheduler/stats/jobs/run", r);
	}

	//TODO - JAXBMARSHALL EXception type of variable
	@Ignore
	@Test
	public void testGetFailedQueue() throws Exception {
		String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<collection><job><restarts>0</restarts></job></collection>";
		PowerMockito.mockStatic(Job.class, Mockito.CALLS_REAL_METHODS);
		Job j = PowerMockito.mock(Job.class);
		Job[] jobs = {j};
		
		Mockito.when(s.getFailedQueue()).thenReturn(jobs);

		httpRequestFireAndTestAssertations("api/scheduler/failed_queue", r);
	}
	
	
}
