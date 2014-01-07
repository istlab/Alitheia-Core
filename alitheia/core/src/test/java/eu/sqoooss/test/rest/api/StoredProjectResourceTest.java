package eu.sqoooss.test.rest.api;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.rest.api.StoredProjectResource;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;
import static org.junit.Assert.assertEquals;



@PrepareForTest(AlitheiaCore.class)
@RunWith(PowerMockRunner.class)
public class StoredProjectResourceTest {
	
	private DBService db;
	
	@Before
	public void setUp() {
		PowerMockito.mockStatic(AlitheiaCore.class);
		AlitheiaCore core = PowerMockito.mock(AlitheiaCore.class);
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
		String q = "from StoredProject";
		
		Mockito.when(db.doHQL(Mockito.anyString())).thenReturn(l);
		List<StoredProject> result = new StoredProjectResource().getProjects();
		assertEquals(l, result);
	}
	
}
