package eu.sqoooss.test.rest.api;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;

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
import eu.sqooss.rest.api.LogManagerResource;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.logging.LogManager;

@PrepareForTest({ AlitheiaCore.class, DAObject.class })
@RunWith(PowerMockRunner.class)
public class LogManagerResourceTest {

	private DBService db;
	private LogManager logManager;

	/************ Auxiliar methods **************/
	private void httpRequestFireAndTestAssertations(String api_path, String r)
			throws URISyntaxException {
		MockHttpResponse response = TestUtils.fireMockHttpRequest(
				LogManagerResource.class, api_path);
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
		
		logManager = PowerMockito.mock(LogManager.class);
		Mockito.when(core.getLogManager()).thenReturn(logManager);

	}

	@After
	public void tearDown() {
		db = null;
	}
	
	public void testWithEntries() throws Exception {
		String[] names = {"one", "two", "three"};
		String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<collection><string><value>one</value></string><string><value>two</value></string><string><value>three</value></string>"
				+ "<string><value>test1</value></string><string><value>test2</value></string></collection>";

		Mockito.when(logManager.getRecentEntries()).thenReturn(names);

		httpRequestFireAndTestAssertations("api/logmanager/entries/recent", r);
	}
	
	public void testNullEntries() throws Exception {
		String[] names = {};
		String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<collection><string><value>test1</value></string><string><value>test2</value></string></collection>";

		Mockito.when(logManager.getRecentEntries()).thenReturn(names);

		httpRequestFireAndTestAssertations("api/logmanager/entries/recent", r);
	}
	@Test
	public void testGetRecentEntries() throws Exception {
		testNullEntries();
		testWithEntries();
	}

}
