package eu.sqooss.impl.service.webadmin;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.xmlmatchers.transform.XmlConverters.the;
import static org.xmlmatchers.xpath.HasXPath.hasXPath;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import java.util.HashMap;

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
		//Arrange
		WebAdminRenderer web = new WebAdminRenderer(null, null);		
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		TestableWebAdminRenderer render = web.new TestableWebAdminRenderer(null, null, map);
		
		//Act
		String failureString = render.renderJobFailStats();
		
		//Assert
		String html = "<root>" + failureString.replace("&nbsp;", " ") + "</root>";
		
		//Check that the thead tr element has to td elements
		assertThat(the(html), hasXPath("count(/root/table/thead/tr/td)", equalTo("2")));
		//Check that there are 2 td elements rendered
		assertThat(the(html), hasXPath("count(/root/table/tbody/tr/td)", equalTo("2")));
		//Check that the first td element rendered says "No Failures"
		assertThat(the(html), hasXPath("/root/table/tbody/tr/td[1]", equalTo("No failures")));

	}
	
	@Test
	public void shouldRenderOneFailedJobs() {
		//Arrange
		WebAdminRenderer web = new WebAdminRenderer(null, null);		
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("test", 1);
		TestableWebAdminRenderer render = web.new TestableWebAdminRenderer(null, null, map);
		
		//Act
		String failureString = render.renderJobFailStats();
		
		//Assert
		String html = "<root>" + failureString.replace("&nbsp;", " ") + "</root>";
		
		//Check that the thead tr element has to td elements
		assertThat(the(html), hasXPath("count(/root/table/thead/tr/td)", equalTo("2")));
		//Check that there are 2 td elements rendered
		assertThat(the(html), hasXPath("count(/root/table/tbody/tr/td)", equalTo("2")));
		//Check that the first td element rendered says "test"
		assertThat(the(html), hasXPath("/root/table/tbody/tr/td[1]", equalTo("test")));
		//Check that the second td element rendered has the integer passed
		assertThat(the(html), hasXPath("/root/table/tbody/tr/td[2]", equalToIgnoringWhiteSpace("1")));
	}
	
	@Test
	public void shouldRenderTwoFailedJobs() {
		//Arrange
		WebAdminRenderer web = new WebAdminRenderer(null, null);		
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("test1", 1);
		map.put("test2", 2);
		
		TestableWebAdminRenderer render = web.new TestableWebAdminRenderer(null, null, map);
		
		//Act
		String failureString = render.renderJobFailStats();
		
		//Assert
		String html = "<root>" + failureString.replace("&nbsp;", " ") + "</root>";
		
		//Check that the thead tr element has to td elements
		assertThat(the(html), hasXPath("count(/root/table/thead/tr/td)", equalTo("2")));
		//Check that there are 2 td elements rendered in tr 1
		assertThat(the(html), hasXPath("count(/root/table/tbody/tr[1]/td)", equalTo("2")));
		//Check that there are 2 td elements rendered in tr 2
		assertThat(the(html), hasXPath("count(/root/table/tbody/tr[2]/td)", equalTo("2")));
		//Check that the tbody element has two tr tags
		assertThat(the(html), hasXPath("count(/root/table/tbody/tr)", equalTo("2")));
		//Check that the first td element rendered says "test1"
		assertThat(the(html), hasXPath("/root/table/tbody/tr[1]/td[1]", equalTo("test1")));
		//Check that the second td element rendered has the integer passed
		assertThat(the(html), hasXPath("/root/table/tbody/tr[1]/td[2]", equalToIgnoringWhiteSpace("1")));
		//Check that the first td element rendered says "test2"
		assertThat(the(html), hasXPath("/root/table/tbody/tr[2]/td[1]", equalTo("test2")));
		//Check that the second td element rendered has the integer passed
		assertThat(the(html), hasXPath("/root/table/tbody/tr[2]/td[2]", equalToIgnoringWhiteSpace("2")));
	}
	
}
