package eu.sqooss.impl.service.webadmin;

import static org.junit.Assert.*;

import org.junit.Test;

public class AdminServletTest {
	@Test
	public void addStaticContentTest() {
		// test is JRE version dependant and might break in the future -.-
		assertEquals(AdminServlet.getMimeType("/screen.css"), "text/css");
		assertEquals(AdminServlet.getMimeType("/projects.png"), "image/png");
	}

}
