package eu.sqooss.test.service.webadmin;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import eu.sqooss.core.AlitheiaCore;

import eu.sqooss.impl.service.webadmin.ProjectDeleteJob;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionParent;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.StoredProjectConfig;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AlitheiaCore.class,StoredProject.class,StoredProjectConfig.class})
public class ProjectDeleteJobTest {
	StoredProject storedProject;
	AlitheiaCore alitheiaCore;
	DBService dbs;

	@Before
	public void setUp() throws Exception {
		alitheiaCore = mock(AlitheiaCore.class, Mockito.CALLS_REAL_METHODS);
		storedProject = mock(StoredProject.class);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPriority() throws Exception {
		ProjectDeleteJob pdj = Whitebox.invokeConstructor(ProjectDeleteJob.class, new Class[]{AlitheiaCore.class,StoredProject.class},new Object[]{alitheiaCore, storedProject});
		assertEquals(0xff, pdj.priority());
	}

	@SuppressWarnings("unchecked")
	@Test

	public void testRun() throws Exception{
		alitheiaCore = mock(AlitheiaCore.class);
		PowerMockito.mockStatic(StoredProjectConfig.class);
//		storedProject = new StoredProject("testProject");
		dbs = Mockito.mock(DBService.class);
		when(alitheiaCore.getDBService()).thenReturn(dbs);

		// simulate inactive db session
		when(dbs.isDBSessionActive()).thenReturn(false);
		
		when(dbs.attachObjectToDBSession(storedProject)).thenReturn(storedProject);
		
		when(storedProject.getProjectVersions()).thenReturn(new ArrayList<ProjectVersion>());
		when(StoredProjectConfig.fromProject(storedProject)).thenReturn(new ArrayList<StoredProjectConfig>());
		
		// invoke constructor and run method
		// delete is unsuccessful
		ProjectDeleteJob pdj = Whitebox.invokeConstructor(ProjectDeleteJob.class, new Class[]{AlitheiaCore.class,StoredProject.class},new Object[]{alitheiaCore, storedProject});
		Whitebox.invokeMethod(pdj, "run");

		verify(dbs,times(1)).startDBSession();
		verify(dbs,times(1)).rollbackDBSession();
		
		
		ArrayList<StoredProjectConfig> confParams = new ArrayList<StoredProjectConfig>();
		confParams.add(new StoredProjectConfig());
		when(StoredProjectConfig.fromProject(storedProject)).thenReturn(confParams);
		
		// simulate succesfull removal of project's config options
		when(dbs.deleteRecords(confParams)).thenReturn(true);
		when(dbs.deleteRecord(storedProject)).thenReturn(true);
		
		// invoke run method again
		Whitebox.invokeMethod(pdj, "run");
		verify(dbs,times(1)).deleteRecords(confParams);
		
		verify(dbs,times(1)).rollbackDBSession();
		verify(dbs,times(1)).commitDBSession();
		
		// simulate version parents
		ArrayList<ProjectVersion> versions = new ArrayList<ProjectVersion>();
		ProjectVersion pv = Mockito.mock(ProjectVersion.class);
		versions.add(pv);
		
		when(storedProject.getProjectVersions()).thenReturn(versions);
		
		@SuppressWarnings("unchecked")
		Set<ProjectVersionParent> mySet = (Set<ProjectVersionParent>) Mockito.mock(Set.class);
		when(pv.getParents()).thenReturn(mySet);
		
		// invoke run method again
		// we have parents on our ProjectVersion simulated,
		// which must be cleared
		Whitebox.invokeMethod(pdj, "run");
		verify(mySet,times(1)).clear();
		
		// cleanup plugin results test
		@SuppressWarnings("rawtypes")
		List pluginList = new ArrayList<Plugin>();
		Plugin myPlugin = Mockito.mock(Plugin.class);
		pluginList.add(myPlugin);
		when(dbs.doHQL("from Plugin")).thenReturn(pluginList);
		
		AlitheiaPlugin ap = Mockito.mock(AlitheiaPlugin.class);
		PluginAdmin myPluginAdmin = Mockito.mock(PluginAdmin.class);
		
		PluginInfo pi = Mockito.mock(PluginInfo.class);
		
		when(myPlugin.getHashcode()).thenReturn("myHash");
		when(alitheiaCore.getPluginAdmin()).thenReturn(myPluginAdmin);
		when(myPluginAdmin.getPluginInfo("myHash")).thenReturn(pi);
		
		when(myPluginAdmin.getPlugin(pi)).thenReturn(ap);
		
		// invoke run method again
		// cleanup must be called on AlitheiaPlugin,
		// with the storedProject as argument
		Whitebox.invokeMethod(pdj, "run");
		verify(ap,times(1)).cleanup(storedProject);		

	}

	@Test
	public void testConstructor() throws Exception {
		storedProject = new StoredProject("testProject");
		ProjectDeleteJob pdj = Whitebox.invokeConstructor(ProjectDeleteJob.class, new Class[]{AlitheiaCore.class,StoredProject.class},new Object[]{alitheiaCore, storedProject});
		AlitheiaCore core = Whitebox.getInternalState(pdj, AlitheiaCore.class);
		StoredProject sp = Whitebox.getInternalState(pdj, StoredProject.class);

		// check for storedProject and Alitheia Core
		assertEquals("testProject", sp.getName());
		assertNotNull(core);
	}

	@Test
	public void testToString() throws Exception {
		storedProject = Mockito.mock(StoredProject.class);
		ProjectDeleteJob pdj = Whitebox.invokeConstructor(ProjectDeleteJob.class, new Class[]{AlitheiaCore.class,StoredProject.class},new Object[]{alitheiaCore, storedProject});
		Mockito.when(storedProject.toString()).thenReturn("a string");
		assertEquals("ProjectDeleteJob - Project:{a string}", pdj.toString());
	}

}
