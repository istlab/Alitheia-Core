/**
 * 
 */
package eu.sqooss.test.service.webadmin.plugins;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import javax.servlet.ServletException;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.osgi.framework.ServiceReference;
import org.powermock.modules.junit4.PowerMockRunner;

import eu.sqooss.impl.service.webadmin.servlets.PluginsServlet;
import eu.sqooss.service.db.*;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.test.service.webadmin.AbstractWebadminServletTest;

@RunWith(PowerMockRunner.class)
public class PluginListTest extends AbstractWebadminServletTest{

	private PluginsServlet testee;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		testee = new PluginsServlet(ve, mockAC);
	}

	/**
	 * A basic test to see if plugin name, version and installed status is displayed correctly
	 */
	@Test
	public void testPluginList() throws ServletException, IOException {
		when(mockReq.getRequestURI()).thenReturn("/plugins");
		when(mockReq.getMethod()).thenReturn("GET");

		ServiceReference mockRef = Mockito.mock(ServiceReference.class);
		// We expect these plugins
		PluginInfo p1 = new PluginInfo();
		p1.setPluginName("TestPlugin1");
		p1.installed = true;
		p1.setPluginVersion("1.0");
		p1.setServiceRef(mockRef);
		PluginInfo p2 = new PluginInfo();
		p2.setPluginName("TestPlugin2");
		p2.installed = false;
		p2.setPluginVersion("2.0");
		p2.setServiceRef(mockRef);

		when(mockPA.listPlugins()).thenReturn(Arrays.asList(new PluginInfo[] {p1,p2}));

		// Do the fake request
		testee.service(mockReq, mockResp);

		// Get the output
		String output = stripHTMLandWhitespace(getResponseOutput());

		// Verify that the 2 plugins are all contained correctly in the output
		assertTrue(output.contains("InstalledTestPlugin1"));
		assertTrue(output.contains("1.0"));
		assertTrue(output.contains("RegisteredTestPlugin2"));
		assertTrue(output.contains("2.0"));
	}

	// TODO: Test if pluginlist lists activators for a plugin
	@Test
	public void testPluginListActivators() throws ServletException, IOException {
		when(mockReq.getRequestURI()).thenReturn("/plugins");
		when(mockReq.getMethod()).thenReturn("GET");
		when(mockReq.getParameter("showActivators")).thenReturn("true");

		// We expect these activators
		HashSet<Class<? extends DAObject>> activators = new HashSet<>();
		activators.add(PluginConfiguration.class);
		activators.add(ProjectFile.class);

		// We expect these plugins
		PluginInfo p1 = new PluginInfo();
		p1.setPluginName("TestPlugin1");
		p1.installed = true;
		p1.setActivationTypes(activators);
		ServiceReference mockRef = Mockito.mock(ServiceReference.class);
		p1.setServiceRef(mockRef);

		when(mockPA.listPlugins()).thenReturn(Arrays.asList(new PluginInfo[] {p1}));

		// Do the fake request
		testee.service(mockReq, mockResp);
		// Get the output
		String output = getResponseOutput();
		System.out.println(output);
		// Assert that all activators are in the output
		for(Class<?> c : activators) {
			assertTrue(output.contains(c.getName()));
		}
	}

}
