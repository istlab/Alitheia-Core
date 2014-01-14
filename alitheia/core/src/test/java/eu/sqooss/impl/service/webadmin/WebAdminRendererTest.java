package eu.sqooss.impl.service.webadmin;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.xmlmatchers.transform.XmlConverters.the;
import static org.xmlmatchers.xpath.HasXPath.hasXPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.WebAdminRenderer.TestableWebAdminRenderer;
import eu.sqooss.service.scheduler.Scheduler;

@RunWith(MockitoJUnitRunner.class)
public class WebAdminRendererTest {
	@Mock
	Scheduler scheduler;
	@Mock
	AlitheiaCore core;

	@Test
	public void shouldRenderNoFailedJobs() {
		// Arrange
		WebAdminRenderer web = new WebAdminRenderer(null, null);
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		TestableWebAdminRenderer render = web.new TestableWebAdminRenderer(
				null, null, map, null, null);

		// Act
		String failureString = render.renderJobFailStats();

		// Assert
		String html = "<root>" + failureString.replace("&nbsp;", " ")
				+ "</root>";

		// Check that the thead tr element has to td elements
		assertThat(the(html),
				hasXPath("count(/root/table/thead/tr/td)", equalTo("2")));
		// Check that there are 2 td elements rendered
		assertThat(the(html),
				hasXPath("count(/root/table/tbody/tr/td)", equalTo("2")));
		// Check that the first td element rendered says "No Failures"
		assertThat(the(html),
				hasXPath("/root/table/tbody/tr/td[1]", equalTo("No failures")));

	}

	@Test
	public void shouldRenderOneFailedJobs() {
		// Arrange
		WebAdminRenderer web = new WebAdminRenderer(null, null);
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("test", 1);
		TestableWebAdminRenderer render = web.new TestableWebAdminRenderer(
				null, null, map, null, null);

		// Act
		String failureString = render.renderJobFailStats();

		// Assert
		String html = "<root>" + failureString.replace("&nbsp;", " ")
				+ "</root>";

		// Check that the thead tr element has to td elements
		assertThat(the(html),
				hasXPath("count(/root/table/thead/tr/td)", equalTo("2")));
		// Check that there are 2 td elements rendered
		assertThat(the(html),
				hasXPath("count(/root/table/tbody/tr/td)", equalTo("2")));
		// Check that the first td element rendered says "test"
		assertThat(the(html),
				hasXPath("/root/table/tbody/tr/td[1]", equalTo("test")));
		// Check that the second td element rendered has the integer passed
		assertThat(
				the(html),
				hasXPath("/root/table/tbody/tr/td[2]",
						equalToIgnoringWhiteSpace("1")));
	}

	@Test
	public void shouldRenderTwoFailedJobs() {
		// Arrange
		WebAdminRenderer web = new WebAdminRenderer(null, null);
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("test1", 1);
		map.put("test2", 2);

		TestableWebAdminRenderer render = web.new TestableWebAdminRenderer(
				null, null, map, null, null);

		// Act
		String failureString = render.renderJobFailStats();

		// Assert
		String html = "<root>" + failureString.replace("&nbsp;", " ")
				+ "</root>";

		// Check that the thead tr element has to td elements
		assertThat(the(html),
				hasXPath("count(/root/table/thead/tr/td)", equalTo("2")));
		// Check that there are 2 td elements rendered in tr 1
		assertThat(the(html),
				hasXPath("count(/root/table/tbody/tr[1]/td)", equalTo("2")));
		// Check that there are 2 td elements rendered in tr 2
		assertThat(the(html),
				hasXPath("count(/root/table/tbody/tr[2]/td)", equalTo("2")));
		// Check that the tbody element has two tr tags
		assertThat(the(html),
				hasXPath("count(/root/table/tbody/tr)", equalTo("2")));
		// Check that the first td element rendered says "test1"
		assertThat(the(html),
				hasXPath("/root/table/tbody/tr[1]/td[1]", equalTo("test1")));
		// Check that the second td element rendered has the integer passed
		assertThat(
				the(html),
				hasXPath("/root/table/tbody/tr[1]/td[2]",
						equalToIgnoringWhiteSpace("1")));
		// Check that the first td element rendered says "test2"
		assertThat(the(html),
				hasXPath("/root/table/tbody/tr[2]/td[1]", equalTo("test2")));
		// Check that the second td element rendered has the integer passed
		assertThat(
				the(html),
				hasXPath("/root/table/tbody/tr[2]/td[2]",
						equalToIgnoringWhiteSpace("2")));
	}

	@Test
	public void shouldReturnNoRunningJobs() {
		// Arrange
		WebAdminRenderer web = new WebAdminRenderer(null, null);
		List<String> runJobs = new ArrayList<String>();
		TestableWebAdminRenderer render = web.new TestableWebAdminRenderer(
				null, null, null, null, runJobs);

		// Act
		String runningJobs = render.renderJobRunStats();

		// Assert
		// Check that a string saying no running jobs is returned
		assertEquals("No running jobs", runningJobs);
	}

	@Test
	public void shouldReturnTwoRunningJobs() {
		// Arrange
		WebAdminRenderer web = new WebAdminRenderer(null, null);
		List<String> runJobs = new ArrayList<String>();
		runJobs.add("job1");
		runJobs.add("job2");
		TestableWebAdminRenderer render = web.new TestableWebAdminRenderer(
				null, null, null, null, runJobs);

		// Act
		String runningJobs = render.renderJobRunStats();

		// Assert
		String html = "<root>" + runningJobs.replace("&nbsp;", " ") + "</root>";
		// Check that there are 2 li elements
		assertThat(the(html), hasXPath("count(/root/ul/li)", equalTo("2")));
		// Check that the first li element has the expected string
		assertThat(the(html),
				hasXPath("/root/ul/li[1]", equalToIgnoringWhiteSpace("job1")));
		// Check that the second li element has the expected string
		assertThat(the(html),
				hasXPath("/root/ul/li[2]", equalToIgnoringWhiteSpace("job2")));
	}

	@Test
	public void shouldRenderNoFailureWaitingList() {
		// Arrange
		WebAdminRenderer web = new WebAdminRenderer(null, null);
		HashMap<String, Integer> waitJobs = new HashMap<String, Integer>();
		// runJobs.put("job1", 2);
		// runJobs.put("job2", 1);
		TestableWebAdminRenderer render = web.new TestableWebAdminRenderer(
				null, null, null, waitJobs, null);

		// Act
		String waitingJobs = render.renderJobWaitStats();

		// Assert
		String html = "<root>" + waitingJobs.replace("&nbsp;", " ") + "</root>";

		// Check that the thead tr element has to td elements
		assertThat(the(html),
				hasXPath("count(/root/table/thead/tr/td)", equalTo("2")));
		// Check that there are 2 td elements rendered
		assertThat(the(html),
				hasXPath("count(/root/table/tbody/tr/td)", equalTo("2")));
		// Check that the first td element rendered says "No Failures"
		assertThat(the(html),
				hasXPath("/root/table/tbody/tr/td[1]", equalTo("No failures")));
	}

	@Test
	public void shouldRenderTwoWaitingJobsList() {
		// Arrange
		WebAdminRenderer web = new WebAdminRenderer(null, null);
		HashMap<String, Integer> waitJobs = new HashMap<String, Integer>();
		waitJobs.put("job1", 2);
		waitJobs.put("job2", 1);
		TestableWebAdminRenderer render = web.new TestableWebAdminRenderer(
				null, null, null, waitJobs, null);

		// Act
		String waitingJobs = render.renderJobWaitStats();

		// Assert
		String html = "<root>" + waitingJobs.replace("&nbsp;", " ") + "</root>";

		// Check that the thead tr element has to td elements
		assertThat(the(html),
				hasXPath("count(/root/table/thead/tr/td)", equalTo("2")));
		// Check that there are for tr 1 2 td elements rendered
		assertThat(the(html),
				hasXPath("count(/root/table/tbody/tr[1]/td)", equalTo("2")));
		// Check that the first td element rendered says "job1"
		assertThat(the(html),
				hasXPath("/root/table/tbody/tr[1]/td[1]", equalTo("job1")));
		// Check that for tr 1 td 2 the integer is as set
		assertThat(
				the(html),
				hasXPath("/root/table/tbody/tr[1]/td[2]",
						equalToIgnoringWhiteSpace("2")));
		// Check that there are for tr 2 2 td elements rendered
		assertThat(the(html),
				hasXPath("count(/root/table/tbody/tr[2]/td)", equalTo("2")));
		// Check that the first td element rendered says "job1"
		assertThat(the(html),
				hasXPath("/root/table/tbody/tr[2]/td[1]", equalTo("job2")));
		// Check that for tr 1 td 2 the integer is as set
		assertThat(
				the(html),
				hasXPath("/root/table/tbody/tr[2]/td[2]",
						equalToIgnoringWhiteSpace("1")));
	}

}
