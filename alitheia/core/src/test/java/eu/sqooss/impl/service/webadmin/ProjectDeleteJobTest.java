package eu.sqooss.impl.service.webadmin;

import static org.junit.Assert.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.StoredProjectConfig;
import eu.sqooss.service.pa.PluginAdmin;

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
