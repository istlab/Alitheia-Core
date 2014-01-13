/**
 * 
 */
package eu.sqooss.test.service.webadmin;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
//import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.velocity.VelocityContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.BundleContext;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.WebAdminRenderer;
import eu.sqooss.service.db.Bug;
import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.MailMessage;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.SchedulerStats;

/**
 * @author elwin
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({WebAdminRenderer.class,Job.class,AlitheiaCore.class,StoredProject.class,ClusterNode.class,ProjectVersion.class,MailMessage.class,Bug.class})
//@PrepareForTest({Job.class,AlitheiaCore.class,StoredProject.class,ClusterNode.class,ProjectVersion.class,MailMessage.class,Bug.class})
public class WebAdminRendererTest extends AbstractViewTestBase {

	WebAdminRenderer webAdminRenderer;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		webAdminRenderer = new WebAdminRenderer(mock(BundleContext.class),velocityContext);
		velocityContext = mock(VelocityContext.class);
		super.setUp(webAdminRenderer);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link eu.sqooss.impl.service.webadmin.WebAdminRenderer#renderFailedJobs()}.
	 */
	@Test
	public void testRenderFailedJobs() {
		String result = webAdminRenderer.renderFailedJobs();
		String expected = "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n\t<thead>\n\t\t<tr>\n\t\t\t<td>Job Type</td>\n\t\t\t<td>Exception type</td>\n\t\t\t<td>Exception text</td>\n\t\t\t<td>Exception backtrace</td>\n\t\t</tr>\n\t</thead>\n\t<tbody>\n<tr><td colspan=\"4\">No failed jobs.</td></tr>\t</tbody>\n</table>";
		assertThat(result,equalTo(expected));
		
		Job[] failedJobs = new Job[1];
		Job job = mock(Job.class);
		failedJobs[0] = job;
		when(scheduler.getFailedQueue()).thenReturn(failedJobs);
		result = webAdminRenderer.renderFailedJobs();
		expected = "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n\t<thead>\n\t\t<tr>\n\t\t\t<td>Job Type</td>\n\t\t\t<td>Exception type</td>\n\t\t\t<td>Exception text</td>\n\t\t\t<td>Exception backtrace</td>\n\t\t</tr>\n\t</thead>\n\t<tbody>\n\t\t<tr>\n\t\t\t<td>job</td>\n\t\t\t<td><b>NA</b></td>\n\t\t\t<td><b>NA<b></td>\n\t\t\t<td><b>NA</b>\t\t\t</td>\n\t\t</tr>\t</tbody>\n</table>";
		assertThat(result,equalTo(expected));
	}
	
	/**
	 * Test method for {@link eu.sqooss.impl.service.webadmin.WebAdminRenderer#renderFailedJobs()}.
	 */
	@Test
	public void testRenderFailedJobs2() {
		String result = webAdminRenderer.renderFailedJobs();
		String expected = "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n\t<thead>\n\t\t<tr>\n\t\t\t<td>Job Type</td>\n\t\t\t<td>Exception type</td>\n\t\t\t<td>Exception text</td>\n\t\t\t<td>Exception backtrace</td>\n\t\t</tr>\n\t</thead>\n\t<tbody>\n<tr><td colspan=\"4\">No failed jobs.</td></tr>\t</tbody>\n</table>";
		assertThat(result,equalTo(expected));
		
		Job[] failedJobs = new Job[1];
		Job job = mock(Job.class);
		failedJobs[0] = job;
		when(scheduler.getFailedQueue()).thenReturn(failedJobs);
		NullPointerException exception = mock(NullPointerException.class);
		when(job.getErrorException()).thenReturn(exception);
		
		result = webAdminRenderer.renderFailedJobs();
		expected = "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n\t<thead>\n\t\t<tr>\n\t\t\t<td>Job Type</td>\n\t\t\t<td>Exception type</td>\n\t\t\t<td>Exception text</td>\n\t\t\t<td>Exception backtrace</td>\n\t\t</tr>\n\t</thead>\n\t<tbody>\n\t\t<tr>\n\t\t\t<td>job</td>\n\t\t\t<td><b>NA<b></td>\n\t\t\t<td>null</td>\n\t\t\t<td><b>NA</b>\t\t\t</td>\n\t\t</tr>\t</tbody>\n</table>";
		assertThat(result,equalTo(expected));
		
		StackTraceElement[] stackTrace = new StackTraceElement[1];
		stackTrace[0] = mock(StackTraceElement.class);
		when(exception.getStackTrace()).thenReturn(stackTrace);
		result = webAdminRenderer.renderFailedJobs();
		//only expeced if WebAdminRenderer is prepared for test, otherwise fails.
		expected = "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n\t<thead>\n\t\t<tr>\n\t\t\t<td>Job Type</td>\n\t\t\t<td>Exception type</td>\n\t\t\t<td>Exception text</td>\n\t\t\t<td>Exception backtrace</td>\n\t\t</tr>\n\t</thead>\n\t<tbody>\n\t\t<tr>\n\t\t\t<td>job</td>\n\t\t\t<td><b>NA<b></td>\n\t\t\t<td>null</td>\n\t\t\t<td>null. null(), (null:0)<br/>\t\t\t</td>\n\t\t</tr>\t</tbody>\n</table>";
		assertThat(result,equalTo(expected));
	}

	/**
	 * Test method for {@link eu.sqooss.impl.service.webadmin.WebAdminRenderer#renderJobFailStats()}.
	 */
	@Test
	public void testRenderJobFailStats() {
		SchedulerStats schedulerStats = mock(SchedulerStats.class);
		when(scheduler.getSchedulerStats()).thenReturn(schedulerStats);
		String result = webAdminRenderer.renderJobFailStats();
		String expected = "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n\t<thead>\n\t\t<tr>\n\t\t\t<td>Job Type</td>\n\t\t\t<td>Num Jobs Failed</td>\n\t\t</tr>\n\t</thead>\n\t<tbody>\n\t\t<tr>\n\t\t\t<td>No failures</td>\n\t\t\t<td>&nbsp;\t\t\t</td>\n\t\t</tr>\t</tbody>\n</table>";
		assertThat(result,equalTo(expected));
		
	}

	/**
	 * Test method for {@link eu.sqooss.impl.service.webadmin.WebAdminRenderer#renderLogs()}.
	 */
	@Test
	public void testRenderLogs() {
		String result = webAdminRenderer.renderLogs();
		String expected = "\t\t\t\t\t<li>&lt;none&gt;</li>\n";
		assertThat(result,equalTo(expected));
		
		String[] names = {"name"};
		when(logManager.getRecentEntries()).thenReturn(names);
		result = webAdminRenderer.renderLogs();
		expected = "\t\t\t\t\t<li>name</li>\n";
		assertThat(result,equalTo(expected));
	}

	/**
	 * Test method for {@link eu.sqooss.impl.service.webadmin.WebAdminRenderer#getUptime()}.
	 * @throws Exception 
	 */
	@Test
	public void testGetUptime() throws Exception {
		Whitebox.setInternalState(webAdminRenderer, long.class, 0);
		Date date = mock(Date.class);
		when(date.getTime()).thenReturn(123456789L);
		whenNew(Date.class).withNoArguments().thenReturn(date);
		String result = webAdminRenderer.getUptime();
		
//		only expeced if WebAdminRenderer is prepared for test, otherwise fails.
		assertThat(result,equalTo("1:10:17:36"));
		
	}

	/**
	 * Test method for {@link eu.sqooss.impl.service.webadmin.WebAdminRenderer#renderJobWaitStats()}.
	 */
	@Test
	public void testRenderJobWaitStats() {
		SchedulerStats schedulerStats = mock(SchedulerStats.class);
		when(scheduler.getSchedulerStats()).thenReturn(schedulerStats);
		String result = webAdminRenderer.renderJobWaitStats();
		String expected = "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n\t<thead>\n\t\t<tr>\n\t\t\t<td>Job Type</td>\n\t\t\t<td>Num Jobs Waiting</td>\n\t\t</tr>\n\t</thead>\n\t<tbody>\n\t\t<tr>\n\t\t\t<td>No failures</td>\n\t\t\t<td>&nbsp;\t\t\t</td>\n\t\t</tr>\t</tbody>\n</table>";
		assertThat(result,equalTo(expected));
	}

	/**
	 * Test method for {@link eu.sqooss.impl.service.webadmin.WebAdminRenderer#renderJobRunStats()}.
	 */
	@Test
	public void testRenderJobRunStats() {
		SchedulerStats schedulerStats = mock(SchedulerStats.class);
		when(scheduler.getSchedulerStats()).thenReturn(schedulerStats);
		String result = webAdminRenderer.renderJobRunStats();
		String expected = "No running jobs";
		assertThat(result,equalTo(expected));
		List<String> rJobs = new ArrayList<String>();
		rJobs.add("Test");
		when(schedulerStats.getRunJobs()).thenReturn(rJobs);
		result = webAdminRenderer.renderJobRunStats();
		expected = "<ul>\n\t<li>Test\t</li>\n</ul>\n";
		assertThat(result, equalTo(expected));
	}

}
