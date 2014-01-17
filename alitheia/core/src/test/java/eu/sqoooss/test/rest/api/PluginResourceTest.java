package eu.sqoooss.test.rest.api;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.jboss.resteasy.mock.MockHttpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import eu.sqoooss.test.rest.api.utils.TestUtils;
import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.rest.api.PluginResource;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;

@PrepareForTest({ AlitheiaCore.class })
@RunWith(PowerMockRunner.class)
public class PluginResourceTest {

	private DBService db;
	private PluginAdmin pa;

	/************ Auxiliar methods **************/
	private void httpRequestFireAndTestAssertations(String api_path, String r)
			throws URISyntaxException {
		MockHttpResponse response = TestUtils.fireMockGETHttpRequest(
				PluginResource.class, api_path);
		//System.out.println(response.getContentAsString());
		assertEquals(HttpServletResponse.SC_OK, response.getStatus());
		assertEquals(r, response.getContentAsString());
	}

	@Before
	public void setUp() {
		AlitheiaCore core = PowerMockito.mock(AlitheiaCore.class);

		PowerMockito.mockStatic(AlitheiaCore.class);
		Mockito.when(AlitheiaCore.getInstance()).thenReturn(core);

		db = PowerMockito.mock(DBService.class);
		Mockito.when(core.getDBService()).thenReturn(db);
		pa = PowerMockito.mock(PluginAdmin.class);
		Mockito.when(core.getPluginAdmin()).thenReturn(pa);
		
	}

	@After
	public void tearDown() {
		db = null;
		pa = null;
	}
	
	@Test
	public void testListPlugins() throws Exception {
		String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<collection><plugin_info><installed>false</installed></plugin_info></collection>";
		
		PluginInfo pI = PowerMockito.mock(PluginInfo.class);
		List<PluginInfo> l = new ArrayList<PluginInfo>();
		l.add(pI);
		Mockito.when(pa.listPlugins()).thenReturn(l);
		
		httpRequestFireAndTestAssertations("api/plugin/info/list", r);
	}
	
	@Test
	public void testGetPluginInfoHash() throws Exception {
		PluginInfo pI = new PluginInfo();
		Mockito.when(pa.getPluginInfo(Mockito.anyString())).thenReturn(pI);
	
		MockHttpResponse response = TestUtils.fireMockGETHttpRequest(
				PluginResource.class, "api/plugin/info/123");
		assertEquals(HttpServletResponse.SC_OK, response.getStatus());
	}
	
	@Test
	public void testInstallPlugin() throws Exception {
		PluginInfo pInfo = new PluginInfo();
		//first branch
		Mockito.when(pa.installPlugin(Mockito.anyString())).thenReturn(false);
		MockHttpResponse response = TestUtils.fireMockPUTHttpRequest(
				PluginResource.class, "api/plugin/install/123");
		assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response.getStatus());
		
		//second branch
		Mockito.when(pa.installPlugin(Mockito.anyString())).thenReturn(true);
		Mockito.when(pa.getPluginInfo(Mockito.anyString())).thenReturn(pInfo);
		
		AlitheiaPlugin ap = PowerMockito.mock(AlitheiaPlugin.class);
		Mockito.when(pa.getPlugin(Mockito.any(PluginInfo.class))).thenReturn(ap);
	
		MockHttpResponse response2 = TestUtils.fireMockPUTHttpRequest(
				PluginResource.class, "api/plugin/install/123");
		assertEquals(HttpServletResponse.SC_OK, response2.getStatus());
	}
	
	@Test
	public void testUnistallPlugin() throws Exception {
		//first branch
		Mockito.when(pa.uninstallPlugin(Mockito.anyString())).thenReturn(false);
		MockHttpResponse response = TestUtils.fireMockDELETEHttpRequest(
				PluginResource.class, "api/plugin/uninstall/123");
		assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response.getStatus());
		
		//second branch
		Mockito.when(pa.uninstallPlugin(Mockito.anyString())).thenReturn(true);
		MockHttpResponse response2 = TestUtils.fireMockDELETEHttpRequest(
				PluginResource.class, "api/plugin/uninstall/123");
		assertEquals(HttpServletResponse.SC_OK, response2.getStatus());
	}
}
