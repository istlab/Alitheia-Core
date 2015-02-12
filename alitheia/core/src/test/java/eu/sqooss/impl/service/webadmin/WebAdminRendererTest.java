package eu.sqooss.impl.service.webadmin;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.velocity.VelocityContext;
import org.junit.Test;
import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;

public class WebAdminRendererTest {

	BundleContext bc;
	AlitheiaCore core;

	public void setUp() {
		bc = mock(BundleContext.class);
		when(bc.getProperty("eu.sqooss.db")).thenReturn("H2");
		when(bc.getProperty("eu.sqooss.db.host")).thenReturn("localhost");
		when(bc.getProperty("eu.sqooss.db.schema")).thenReturn(
				"alitheia;LOCK_MODE=3;MULTI_THREADED=true");
		when(bc.getProperty("eu.sqooss.db.user")).thenReturn("sa");
		when(bc.getProperty("eu.sqooss.db.passwd")).thenReturn("");
		when(bc.getProperty("eu.sqooss.db.conpool")).thenReturn("c3p0");

		core = new AlitheiaCore(bc);
	}

	@Test
	public void renderJobFailedStatsTest1() {
		setUp();
		VelocityContext vc = mock(VelocityContext.class);
		new WebAdminRenderer(bc, vc);

		StringBuilder expected = new StringBuilder();
		expected.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">");
		expected.append("<thead>");
		expected.append("<tr>");
		expected.append("<td>Job Type</td>");
		expected.append("<td>Num Jobs Failed</td>");
		expected.append("</tr>");
		expected.append("</thead>");
		expected.append("<tbody>");
		expected.append("<tr>");
		expected.append("<td>No failures</td>");
		expected.append("<td>&nbsp;</td>");
		expected.append("</tr></tbody>");
		expected.append("</table>");

		String output = WebAdminRenderer.renderJobFailStats();
		output = output.replaceAll("[\\t\\n]", "");
		assertEquals(expected.toString(), output);
	}

	@Test
	public void renderJobWaitFailedTest2() {
		setUp();
		VelocityContext vc = mock(VelocityContext.class);
		new WebAdminRenderer(bc, vc);
		AbstractView.sobjSched.getSchedulerStats().addFailedJob("name");
		
		StringBuilder expected = new StringBuilder();
		expected.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">");
		expected.append("<thead>");
		expected.append("<tr>");
		expected.append("<td>Job Type</td>");
		expected.append("<td>Num Jobs Failed</td>");
		expected.append("</tr>");
		expected.append("</thead>");
		expected.append("<tbody>");
		expected.append("<tr>");
		expected.append("<td>name</td>");
		expected.append("<td>1</td>");
		expected.append("</tr></tbody>");
		expected.append("</table>");
		
		String output = WebAdminRenderer.renderJobFailStats();
		output = output.replaceAll("[\\t\\n]", "");
		assertEquals(expected.toString(), output);
	}
	
	@Test
	public void renderJobWaitStatsTest() {
		setUp();
		VelocityContext vc = mock(VelocityContext.class);
		new WebAdminRenderer(bc, vc);

		StringBuilder expected = new StringBuilder();
		expected.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">");
		expected.append("<thead>");
		expected.append("<tr>");
		expected.append("<td>Job Type</td>");
		expected.append("<td>Num Jobs Waiting</td>");
		expected.append("</tr>");
		expected.append("</thead>");
		expected.append("<tbody>");
		expected.append("<tr>");
		expected.append("<td>No failures</td>");
		expected.append("<td>&nbsp;</td>");
		expected.append("</tr></tbody>");
		expected.append("</table>");

		String output = WebAdminRenderer.renderJobWaitStats();
		output = output.replaceAll("[\\t\\n]", "");
		assertEquals(expected.toString(), output);
	}
	
	@Test
	public void renderJobWaitStatsTest2() {
		setUp();
		VelocityContext vc = mock(VelocityContext.class);
		new WebAdminRenderer(bc, vc);
		AbstractView.sobjSched.getSchedulerStats().addWaitingJob("name");
		
		StringBuilder expected = new StringBuilder();
		expected.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">");
		expected.append("<thead>");
		expected.append("<tr>");
		expected.append("<td>Job Type</td>");
		expected.append("<td>Num Jobs Waiting</td>");
		expected.append("</tr>");
		expected.append("</thead>");
		expected.append("<tbody>");
		expected.append("<tr>");
		expected.append("<td>name</td>");
		expected.append("<td>1</td>");
		expected.append("</tr></tbody>");
		expected.append("</table>");
		
		String output = WebAdminRenderer.renderJobWaitStats();
		output = output.replaceAll("[\\t\\n]", "");
		assertEquals(expected.toString(), output);
	}
}
