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
import eu.sqooss.rest.api.StoredProjectResource;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Directory;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;

@PrepareForTest({ AlitheiaCore.class, StoredProject.class, String.class,
		DAObject.class, ProjectVersion.class, StoredProjectResource.class})
@RunWith(PowerMockRunner.class)
public class StoredProjectResourceTest {

	private DBService db;

	private void httpRequestFireAndTestAssertations(String api_path, String r) throws URISyntaxException {
		MockHttpResponse response = TestUtils.fireMockHttpRequest(
				StoredProjectResource.class, api_path);
		assertEquals(HttpServletResponse.SC_OK, response.getStatus());
		//System.out.println(response.getContentAsString());
		assertEquals(r, response.getContentAsString());
	}
	
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
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
		
		httpRequestFireAndTestAssertations("api/project", r);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testGetProjectWithId() throws Exception {

		StoredProject sp = new StoredProject();
		sp.setName("TestProject");
		String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<project><id>0</id><name>TestProject</name></project>";

		PowerMockito.mockStatic(DAObject.class);
		Mockito.when(
				DAObject.loadDAObyId(Mockito.anyLong(), (Class) Mockito.any()))
				.thenReturn(sp);

		httpRequestFireAndTestAssertations("api/project/0123", r);

	}

	@Test
	public void testGetProjectWithName() throws Exception {

		StoredProject sp = new StoredProject();
		sp.setName("TestProject");
		String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<project><id>0</id><name>TestProject</name></project>";

		PowerMockito.mockStatic(StoredProject.class);
		Mockito.when(StoredProject.getProjectByName(Mockito.anyString()))
				.thenReturn(sp);

		httpRequestFireAndTestAssertations("api/project/aaa", r);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testGetAllVersions() throws Exception {
		StoredProject sp = new StoredProject();
		sp.setName("TestProject");
		
		ProjectVersion pv1 = new ProjectVersion(sp);
		ProjectVersion pv2 = new ProjectVersion(sp);
		
		List l = new ArrayList<ProjectVersion>();
		l.add(pv1);
		l.add(pv2);
		
		String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<collection><version><id>0</id><timestamp>0</timestamp><sequence>0</sequence></version><version><id>0</id><timestamp>0</timestamp><sequence>0</sequence></version></collection>";
		StoredProject tmp = PowerMockito.mock(StoredProject.class);
		PowerMockito.mockStatic(DAObject.class);
		Mockito.when(
				DAObject.loadDAObyId(Mockito.anyLong(), (Class) Mockito.any()))
				.thenReturn(tmp);

		Mockito.when(tmp.getProjectVersions()).thenReturn(l);
		
		httpRequestFireAndTestAssertations("api/project/0123/versions", r);
	}
	
	@Test
	public void testGetFirstVersion() throws Exception {
		StoredProject sp = new StoredProject();
		sp.setName("TestProject");
		
		ProjectVersion v = new ProjectVersion(sp);
		
		String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
		+"<version><id>0</id><timestamp>0</timestamp><sequence>0</sequence></version>";
		
		PowerMockito.mockStatic(ProjectVersion.class);
		StoredProjectResource api = PowerMockito.mock(StoredProjectResource.class);
		Mockito.when(ProjectVersion.getFirstProjectVersion(api.getProject(Mockito.anyString()))).thenReturn(v);
		
		httpRequestFireAndTestAssertations("api/project/0123/version/first", r);
		
	}
	
	@Test
	public void testGetMiddleVersion() throws Exception {
		StoredProject sp = new StoredProject();
		sp.setName("TestProject");
		
		ProjectVersion v = new ProjectVersion(sp);
		
		String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
		+"<version><id>0</id><timestamp>0</timestamp><sequence>0</sequence></version>";
		
		PowerMockito.mockStatic(ProjectVersion.class);
		Mockito.when(ProjectVersion.getVersionByRevision((StoredProject) Mockito.any(), Mockito.anyString())).thenReturn(v);
		
		httpRequestFireAndTestAssertations("api/project/0123/version/random", r);
		
	}
	
	@Test
	public void testGetLastVersion() throws Exception {
		StoredProject sp = new StoredProject();
		sp.setName("TestProject");
		
		ProjectVersion v = new ProjectVersion(sp);
		
		String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
		+"<version><id>0</id><timestamp>0</timestamp><sequence>0</sequence></version>";
	
		PowerMockito.mockStatic(ProjectVersion.class);
		StoredProjectResource api = PowerMockito.mock(StoredProjectResource.class);
		Mockito.when(ProjectVersion.getLastMeasuredVersion(Metric.getMetricByMnemonic("TLOC"), api.getProject(Mockito.anyString()))).thenReturn(v);
		
		httpRequestFireAndTestAssertations("api/project/0123/version/latest", r);
	}
	
	@Test
	public void testGetAllFilesNullProject() throws Exception {
		
		ProjectVersion v = null;
		String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
		+ "<collection/>";
				
		StoredProjectResource api = PowerMockito.mock(StoredProjectResource.class);
		Mockito.when(api.getVersion(Mockito.anyString(), Mockito.anyString())).thenReturn(v);
		
		httpRequestFireAndTestAssertations("api/project/0123/version/random/files/", r);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testGetAllFilesWithProject() throws Exception {
				
		ProjectVersion v = PowerMockito.mock(ProjectVersion.class);
		ProjectFile f = new ProjectFile(v);
		
		List l = new ArrayList<ProjectFile>();
		l.add(f);
		
		String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
		+ "<collection/>";
		
		StoredProjectResource api = PowerMockito.mock(StoredProjectResource.class);
		Mockito.when(api.getVersion(Mockito.anyString(), Mockito.anyString())).thenReturn(v);
		
		PowerMockito.mockStatic(ProjectVersion.class);
		PowerMockito.mock(Directory.class);
		
		Mockito.when(v.getFiles((Directory) Mockito.isNull(), Mockito.anyInt())).thenReturn(l);
		
		httpRequestFireAndTestAssertations("api/project/0123/version/random/files/", r);
	}

}
