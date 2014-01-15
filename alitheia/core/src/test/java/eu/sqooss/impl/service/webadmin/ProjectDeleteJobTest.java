package eu.sqooss.impl.service.webadmin;

import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionParent;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.StoredProjectConfig;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;

@RunWith(MockitoJUnitRunner.class)
public class ProjectDeleteJobTest {
	private Map<StoredProject, List<StoredProjectConfig>> projectConfigs;
	@Mock private DBService database; 
	@Mock private PluginAdmin pluginAdmin;
	private StoredProject project;
	private ProjectDeleteJob job;

	@Before
	public void setup() {
		projectConfigs = new HashMap<StoredProject, List<StoredProjectConfig>>();
		
		when(database.isDBSessionActive()).thenReturn(true);
		doReturn(new ArrayList<Object>()).when(database).doHQL(anyString());
		when(database.attachObjectToDBSession(any(StoredProject.class))).then(returnsFirstArg());
		
		project = new StoredProject();
		project.setProjectVersions(new ArrayList<ProjectVersion>());
		job = new TestableProjectDeleteJob(null, project);
	}
	
	@Test
	public void shouldHaveFFPriority() {
		assertEquals(0xFF, job.priority());
	}
	
	@Test
	public void shouldFailWhenProjectCanNotBeDeleted() throws Exception {		
		job.run();
		
		verify(database).rollbackDBSession();
	}
	
	@Test
	public void shouldStartSessionIfNotActive() throws Exception {
		when(database.isDBSessionActive()).thenReturn(false);
		
		job.run();
		
		verify(database).startDBSession();
	}
	
	@Test
	public void shouldCommitSessionIfSuccessfull() throws Exception {
		when(database.deleteRecord(project)).thenReturn(true);
		
		job.run();
		
		verify(database).commitDBSession();
	}
	
	@Test
	public void shouldFailWhenConfCanNotBeDeleted() throws Exception {
		ArrayList<StoredProjectConfig> configs = new ArrayList<StoredProjectConfig>();
		configs.add(new StoredProjectConfig());
		projectConfigs.put(project, configs);
		
		job.run();
		
		verify(database).rollbackDBSession();
	}

	@Test
	public void shouldSucceedWhenConfCanBeDeleted() throws Exception {
		ArrayList<StoredProjectConfig> configs = new ArrayList<StoredProjectConfig>();
		configs.add(new StoredProjectConfig());
		projectConfigs.put(project, configs);
		when(database.deleteRecords(configs)).thenReturn(true);
		
		job.run();
		
		verify(database).deleteRecords(configs);
		verify(database).rollbackDBSession();
	}
	
	@Test
	public void shouldCleanupPluginAndSkipNull() throws Exception {
		Plugin p1 = new Plugin();
		p1.setHashcode("plugin1");
		PluginInfo pi1 = new PluginInfo();
		AlitheiaPlugin ap1 = mock(AlitheiaPlugin.class);
		Plugin p2 = new Plugin();
		p2.setHashcode("plugin2");
		PluginInfo pi2 = new PluginInfo();
		AlitheiaPlugin ap3 = mock(AlitheiaPlugin.class);
		Plugin p3 = new Plugin();
		p3.setHashcode("plugin3");
		PluginInfo pi3 = new PluginInfo();
		
		doReturn(Arrays.asList(p1, p2, p3)).when(database).doHQL("from Plugin");
		when(pluginAdmin.getPluginInfo("plugin1")).thenReturn(pi1);
		when(pluginAdmin.getPluginInfo("plugin2")).thenReturn(pi2);
		when(pluginAdmin.getPluginInfo("plugin3")).thenReturn(pi3);
		when(pluginAdmin.getPlugin(pi1)).thenReturn(ap1);
		when(pluginAdmin.getPlugin(pi2)).thenReturn(null);
		when(pluginAdmin.getPlugin(pi3)).thenReturn(ap3);
		
		job.run();
		
		verify(ap1).cleanup(project);
		verify(ap3).cleanup(project);
	}
	
	@Test
	public void shouldClearVersionParents() throws Exception {
		Set<ProjectVersionParent> parents1 = new HashSet<ProjectVersionParent>();
		parents1.add(new ProjectVersionParent());
		Set<ProjectVersionParent> parents2 = new HashSet<ProjectVersionParent>();
		parents2.add(new ProjectVersionParent());
		
		List<ProjectVersion> versions = project.getProjectVersions();
		ProjectVersion version1 = new ProjectVersion();
		version1.setParents(parents1);
		versions.add(version1);
		ProjectVersion version2 = new ProjectVersion();
		version1.setParents(parents2);
		versions.add(version2);
		
		job.run();
		
		assertThat(version1.getParents(), empty());
		assertThat(version2.getParents(), empty());
	}
	
	public class TestableProjectDeleteJob extends ProjectDeleteJob {
		TestableProjectDeleteJob(AlitheiaCore core, StoredProject sp) {
			super(core, sp);
		}

		@Override
		protected List<StoredProjectConfig> getProjectConfigs(
				StoredProject project) {
			if (projectConfigs.containsKey(project)) {
				return projectConfigs.get(project);
			} else {
				return new ArrayList<StoredProjectConfig>();
			}
		}

		@Override
		protected PluginAdmin getPluginAdmin() {
			return pluginAdmin;
		}

		@Override
		protected DBService getDatabaseService() {
			return database;
		}
	}
}
