package eu.sqooss.test.service.webadmin.jobs;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import eu.sqooss.impl.service.updater.UpdaterJob;
import eu.sqooss.impl.service.webadmin.servlets.JobsServlet;
import eu.sqooss.service.scheduler.*;
import eu.sqooss.test.service.webadmin.AbstractWebadminServletTest;

@RunWith(PowerMockRunner.class)
public class JobsTest extends AbstractWebadminServletTest {

	private JobsServlet testee;
	@Mock private Scheduler mockSched;
	@Mock private SchedulerStats mockStats;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		// Mock scheduler
		when(mockAC.getScheduler()).thenReturn(mockSched);

		// Initialize servlet
		testee = new JobsServlet(ve, mockAC);
	}

	// Test Job statistics in Job page
	@Test
	public void testJobStatistics() throws ServletException, IOException {
		when(mockReq.getRequestURI()).thenReturn("/jobs");
		when(mockReq.getMethod()).thenReturn("GET");

		// We expect these number of waiting jobs
		when(mockSched.getSchedulerStats()).thenReturn(mockStats);
		when(mockStats.getRunningJobs()).thenReturn(851l);
		when(mockStats.getWaitingJobs()).thenReturn(861l);
		when(mockStats.getFailedJobs()).thenReturn(871l);
		when(mockStats.getTotalJobs()).thenReturn(881l);
		when(mockStats.getWorkerThreads()).thenReturn(891l);

		// Do the fake request
		testee.service(mockReq, mockResp);

		// Get the output
		String output = stripHTMLandWhitespace(getResponseOutput());

		// Verify that the number are equal
		// Warning: this is always true when the number 851, 861, 871, 881
		// and 891 occur in the HTML code itself. In that case change this
		// number to a unique number.
		assertTrue(output.matches(".*851.*"));
		assertTrue(output.matches(".*861.*"));
		assertTrue(output.matches(".*871.*"));
		assertTrue(output.matches(".*881.*"));
		assertTrue(output.matches(".*891.*"));
	}

	// Test if running jobs are displayed correctly
	@Test
	public void testRunningJobs() throws ServletException, IOException {
		when(mockReq.getRequestURI()).thenReturn("/jobs");
		when(mockReq.getMethod()).thenReturn("GET");

		// We expect these jobs
		List<String> runJobs = new ArrayList<String>();
		runJobs.add("opfgqodzmo");
		runJobs.add("abcrvftnfl");
		when(mockSched.getSchedulerStats()).thenReturn(mockStats);
		when(mockStats.getRunJobs()).thenReturn(runJobs);

		// Do the fake request
		testee.service(mockReq, mockResp);

		// Get the output
		String output = stripHTMLandWhitespace(getResponseOutput());

		// Verify the jobs are displayed correctly
		assertTrue(output.matches(".*opfgqodzmo.*abcrvftnfl.*"));
	}

	// Test if waiting jobs are displayed correctly
	@Test
	public void testWaitingJobs() throws ServletException, IOException {
		when(mockReq.getRequestURI()).thenReturn("/jobs");
		when(mockReq.getMethod()).thenReturn("GET");

		// We expect these waiting jobs
		when(mockSched.getSchedulerStats()).thenReturn(mockStats);
		HashMap<String, Integer> waitingJobs = new HashMap<String, Integer>();
		waitingJobs.put("xtttlxgolc", 911);
		waitingJobs.put("ezugyjtwdv", 921);
		when(mockStats.getWaitingJobTypes()).thenReturn(waitingJobs);

		// Do the fake request
		testee.service(mockReq, mockResp);

		// Get the output
		String output = stripHTMLandWhitespace(getResponseOutput());

		// Verify the jobs names and numbers are displayed correctly
		assertTrue(output.matches(".*xtttlxgolc.*911.*ezugyjtwdv.*921.*"));
	}

	// Test if failed jobs are displayed correctly
	@Test
	public void testFailedJobs() throws ServletException, IOException {
		when(mockReq.getRequestURI()).thenReturn("/jobs");
		when(mockReq.getMethod()).thenReturn("GET");

		// We expect these failing jobs
		when(mockSched.getSchedulerStats()).thenReturn(mockStats);
		HashMap<String, Integer> jobs = new HashMap<String, Integer>();
		jobs.put("dcjjzfqjox", 931);
		jobs.put("ligeismung", 941);
		when(mockStats.getFailedJobTypes()).thenReturn(jobs);

		// Do the fake request
		testee.service(mockReq, mockResp);

		// Get the output
		String output = stripHTMLandWhitespace(getResponseOutput());
		System.out.println(output);

		// Verify the jobs names and numbers are displayed correctly
		assertTrue(output.matches(".*dcjjzfqjox.*931.*ligeismung.*941.*"));
	}

	// TODO: Test if failed job error details are displayed correctly
	@Test
	public void testFailedJobsError() throws ServletException, IOException {
		when(mockReq.getRequestURI()).thenReturn("/jobs/failed");
		when(mockReq.getMethod()).thenReturn("GET");

		// We expect these failing jobs
		Job[] failedJobs = new Job[1];
		failedJobs[0] = mock(UpdaterJob.class);
		when(mockSched.getFailedQueue()).thenReturn(failedJobs);
		when(failedJobs[0].toString()).thenReturn("failedJob1");

		// Fake the request
		testee.service(mockReq, mockResp);

		// Get the output
		String output = stripHTMLandWhitespace(getResponseOutput());

		// Verify output
		assertTrue(output.contains("failedJob1"));
	}

}
