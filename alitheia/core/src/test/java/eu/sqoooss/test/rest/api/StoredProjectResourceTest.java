package eu.sqoooss.test.rest.api;

import static org.junit.Assert.assertEquals;

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
import eu.sqooss.rest.api.StoredProjectResource;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;

@PrepareForTest(AlitheiaCore.class)
@RunWith(PowerMockRunner.class)
public class StoredProjectResourceTest {

	private DBService db;

	@Before
	public void setUp() {
		AlitheiaCore core = PowerMockito.mock(AlitheiaCore.class);

		PowerMockito.mockStatic(AlitheiaCore.class);
		Mockito.when(AlitheiaCore.getInstance()).thenReturn(core);
		
		db = PowerMockito.mock(DBService.class);
		Mockito.when(core.getDBService()).thenReturn(db);

	}

	@After
	public void tearDown() {
		db = null;
	}

	@Test
	public void testGetProjects() throws Exception {

		StoredProject p1 = new StoredProject("TestProject1");
		StoredProject p2 = new StoredProject("TestProject2");
		List l = new ArrayList<StoredProject>();
		l.add(p1);
		l.add(p2);
		String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				 + "<collection>"
				 + "<project><id>0</id><name>TestProject1</name></project>"
				 + "<project><id>0</id><name>TestProject2</name></project>"
				 + "</collection>";

		Mockito.when(db.doHQL(Mockito.anyString())).thenReturn(l);


		MockHttpResponse response = TestUtils.fireMockHttpRequest(
				StoredProjectResource.class, "api/project");

		assertEquals(HttpServletResponse.SC_OK, response.getStatus());
		assertEquals(r, response.getContentAsString());
		
	}



}
