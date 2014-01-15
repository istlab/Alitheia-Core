package eu.sqoooss.test.rest.api;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.PathParam;

import org.jboss.resteasy.mock.MockHttpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;
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

@PrepareForTest({ AlitheiaCore.class, DAObject.class, ProjectVersion.class,
		StoredProject.class, Directory.class })
@RunWith(PowerMockRunner.class)
public class StoredProjectResourceTest {

	private DBService db;

	/************ Help methods **************/
	private void httpRequestFireAndTestAssertations(String api_path, String r)
			throws URISyntaxException {
		MockHttpResponse response = TestUtils.fireMockHttpRequest(
				StoredProjectResource.class, api_path);
		assertEquals(HttpServletResponse.SC_OK, response.getStatus());
		System.out.println(response.getContentAsString());
		assertEquals(r, response.getContentAsString());
	}

	public void testNullProjectVersion() throws Exception {
		StoredProjectResource api = PowerMockito
				.mock(StoredProjectResource.class);
		Mockito.when(api.getVersion(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(null);
	}

	public void testWithProjectVersion() throws Exception {
		ProjectVersion pv = PowerMockito.mock(ProjectVersion.class);
		List<ProjectFile> l = new ArrayList<ProjectFile>();
		ProjectFile f1 = PowerMockito.mock(ProjectFile.class);
		ProjectFile f2 = PowerMockito.mock(ProjectFile.class);
		l.add(f1);
		l.add(f2);

		PowerMockito.mockStatic(StoredProject.class);
		PowerMockito.mockStatic(ProjectVersion.class);
		Mockito.when(
				ProjectVersion.getVersionByRevision(
						(StoredProject) Mockito.any(), Mockito.anyString()))
				.thenReturn(pv);
		Mockito.when(
				ProjectVersion.getLastProjectVersion(Mockito
						.any(StoredProject.class))).thenReturn(pv);

		PowerMockito.mockStatic(Directory.class);
		Mockito.when(
				pv.getFiles(Mockito.any(Directory.class), Mockito.anyInt()))
				.thenReturn(l);
	}

	public void testGetFirstVersion(ProjectVersion pv, String r)
			throws Exception {
		StoredProjectResource api = PowerMockito
				.mock(StoredProjectResource.class);
		Mockito.when(
				ProjectVersion.getFirstProjectVersion(api.getProject(Mockito
						.anyString()))).thenReturn(pv);
		httpRequestFireAndTestAssertations("api/project/0123/version/first", r);
	}

	public void testGetMiddleVersion(ProjectVersion pv, String r)
			throws Exception {
		Mockito.when(
				ProjectVersion.getVersionByRevision(
						(StoredProject) Mockito.any(), Mockito.anyString()))
				.thenReturn(pv);
		httpRequestFireAndTestAssertations("api/project/0123/version/random", r);
	}

	public void testGetLastVersion(ProjectVersion pv, String r)
			throws Exception {
		StoredProjectResource api = PowerMockito
				.mock(StoredProjectResource.class);
		Mockito.when(
				ProjectVersion.getLastMeasuredVersion(
						Metric.getMetricByMnemonic("TLOC"),
						api.getProject(Mockito.anyString()))).thenReturn(pv);
		httpRequestFireAndTestAssertations("api/project/0123/version/latest", r);
	}

	/************************************/

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

		httpRequestFireAndTestAssertations("api/project/", r);

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

		ProjectVersion pv1 = PowerMockito.mock(ProjectVersion.class);
		ProjectVersion pv2 = PowerMockito.mock(ProjectVersion.class);

		List<ProjectVersion> l = new ArrayList<ProjectVersion>();
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testGetVersions() throws Exception {
		String r1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<collection/>";

		PowerMockito.mockStatic(DAObject.class);
		Mockito.when(
				DAObject.loadDAObyId(Mockito.anyLong(), (Class) Mockito.any()))
				.thenReturn(null);
		httpRequestFireAndTestAssertations("api/project/0123/versions/vid", r1);

		String r2 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<collection><version><id>0</id><timestamp>0</timestamp><sequence>0</sequence></version></collection>";

		StoredProject sp = new StoredProject();
		List<ProjectVersion> l = new ArrayList<ProjectVersion>();
		ProjectVersion pv = new ProjectVersion(sp);
		l.add(pv);

		PowerMockito.mockStatic(DAObject.class);
		Mockito.when(
				DAObject.loadDAObyId(Mockito.anyLong(), (Class) Mockito.any()))
				.thenReturn(sp);

		PowerMockito.mockStatic(ProjectVersion.class);

		String s = "";
		for (int i = 0; i < 65; i++)
			s += "vid,";
		s += "vid";

		testGetVersionsHelper(pv);
		httpRequestFireAndTestAssertations("api/project/0123/versions/" + s, r2);

		testGetVersionsHelper(null);
		httpRequestFireAndTestAssertations("api/project/0123/versions/" + s, r1);
	}

	public void testGetVersionsHelper(ProjectVersion pv) throws Exception {
		Mockito.when(
				ProjectVersion.getVersionByRevision(
						Mockito.any(StoredProject.class), Mockito.anyString()))
				.thenReturn(pv);
	}

	@Test
	public void testGetVersion() throws Exception {
		StoredProject sp = new StoredProject("TestProject");
		ProjectVersion pv = new ProjectVersion(sp);

		String r = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<version><id>0</id><timestamp>0</timestamp><sequence>0</sequence></version>";
		PowerMockito.mockStatic(ProjectVersion.class);
		testGetFirstVersion(pv, r);
		testGetMiddleVersion(pv, r);
		testGetLastVersion(pv, r);
	}

	@Test
	public void testGetAllFiles() throws Exception {
		testNullProjectVersion();
		String r1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<collection/>";
		httpRequestFireAndTestAssertations(
				"api/project/0123/version/random/files/", r1);

		testWithProjectVersion();
		String r2 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<collection><file><id>0</id><isdir>false</isdir></file><file><id>0</id><isdir>false</isdir></file></collection>";
		httpRequestFireAndTestAssertations(
				"api/project/0123/version/random/files/", r2);
	}

	@Test
	public void testGetFiliesInDir() throws Exception {
		testNullProjectVersion();
		String r1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<collection/>";
		httpRequestFireAndTestAssertations(
				"api/project/0123/version/first/files/bla", r1);

		testWithProjectVersion();
		String r2 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<collection><file><id>0</id><isdir>false</isdir></file><file><id>0</id><isdir>false</isdir></file></collection>";
		httpRequestFireAndTestAssertations(
				"api/project/0123/version/random/files/bla", r2);

		httpRequestFireAndTestAssertations(
				"api/project/0123/version/random/files//blah", r2);
	}

	@Test
	public void testGetChangedFiles() throws Exception {
		testNullProjectVersion();
		String r1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<collection/>";
		httpRequestFireAndTestAssertations(
				"api/project/0123/version/first/files/changed", r1);

		ProjectVersion pv = PowerMockito.mock(ProjectVersion.class);
		Set<ProjectFile> s = new HashSet<ProjectFile>();
		ProjectFile f1 = PowerMockito.mock(ProjectFile.class);
		ProjectFile f2 = PowerMockito.mock(ProjectFile.class);
		s.add(f1);
		s.add(f2);
		String r2 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<collection><file><id>0</id><isdir>false</isdir></file><file><id>0</id><isdir>false</isdir></file></collection>";

		PowerMockito.mockStatic(StoredProject.class);
		PowerMockito.mockStatic(ProjectVersion.class);
		Mockito.when(
				ProjectVersion.getVersionByRevision(
						(StoredProject) Mockito.any(), Mockito.anyString()))
				.thenReturn(pv);
		Mockito.when(
				ProjectVersion.getLastProjectVersion(Mockito
						.any(StoredProject.class))).thenReturn(pv);
		Mockito.when(pv.getVersionFiles()).thenReturn(s);
		httpRequestFireAndTestAssertations(
				"api/project/0123/version/random/files/changed", r2);
	}

	@Test
	public void testGetDirs() throws Exception {
		testNullProjectVersion();
		String r1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<collection/>";
		httpRequestFireAndTestAssertations(
				"api/project/0123/version/first/dirs/", r1);

		testWithProjectVersion();
		String r2 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<collection><file><id>0</id><isdir>false</isdir></file><file><id>0</id><isdir>false</isdir></file></collection>";
		httpRequestFireAndTestAssertations(
				"api/project/0123/version/random/dirs/", r2);
	}

	@Test
	public void testGetDirsWithPath() throws Exception {
		testNullProjectVersion();
		String r1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<collection/>";
		httpRequestFireAndTestAssertations(
				"api/project/0123/version/random/dirs/bab", r1);

		testWithProjectVersion();
		String r2 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<collection><file><id>0</id><isdir>false</isdir></file><file><id>0</id><isdir>false</isdir></file></collection>";
		httpRequestFireAndTestAssertations(
				"api/project/0123/version/random/dirs/bab", r2);
		httpRequestFireAndTestAssertations(
				"api/project/0123/version/random/dirs//bab", r2);
	}

	public static void main(String[] args) throws Exception {
		StoredProjectResourceTest t = new StoredProjectResourceTest();
		System.out.println("Hello, World.");
	}

}
