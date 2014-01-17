package eu.sqoooss.test.rest.api;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;
import java.util.Collection;

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
import eu.sqooss.rest.api.PluginResource;
import eu.sqooss.rest.api.StoredProjectResource;
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
	
	//FIXME returns error 500 due to PluginConfig bug I believe
	@Test
	@Ignore
	public void testListPlugins() throws Exception {
		String r = "";
		Collection<PluginInfo> c = (Collection<PluginInfo>) PowerMockito.mock(Collection.class);
		Mockito.when(pa.listPlugins()).thenReturn(c);
		
		httpRequestFireAndTestAssertations("api/plugin/info/list", r);
	}
	
	@Test
	public void testGetPluginInfoHash() throws Exception {
		PluginInfo pI = PowerMockito.mock(PluginInfo.class);
		Mockito.when(pa.getPluginInfo(Mockito.anyString())).thenReturn(pI);
	
		MockHttpResponse response = TestUtils.fireMockGETHttpRequest(
				PluginResource.class, "api/plugin/info/bla");
		assertEquals(HttpServletResponse.SC_OK, response.getStatus());
	}
	
	@Ignore
	@Test
	public void testGetPluginInfo() throws Exception {
		
	}
	
	@Test
	public void testInstallPlugin() throws Exception {
		PluginInfo pInfo = PowerMockito.mock(PluginInfo.class);
		Mockito.when(pa.installPlugin(Mockito.anyString())).thenReturn(true);
		Mockito.when(pa.getPluginInfo(Mockito.anyString())).thenReturn(pInfo);
		
		AlitheiaPlugin ap = PowerMockito.mock(AlitheiaPlugin.class);
		Mockito.when(pa.getPlugin(Mockito.any(PluginInfo.class))).thenReturn(ap);
	
		MockHttpResponse response = TestUtils.fireMockGETHttpRequest(
				PluginResource.class, "api/plugin/install/bla");
		assertEquals(HttpServletResponse.SC_OK, response.getStatus());
	}
}
