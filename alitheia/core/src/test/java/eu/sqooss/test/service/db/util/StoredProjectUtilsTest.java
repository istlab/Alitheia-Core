package eu.sqooss.test.service.db.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.sqooss.service.db.ConfigOption;
import eu.sqooss.service.db.ConfigurationOption;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.StoredProjectConfig;
import eu.sqooss.service.db.MetricType.Type;
import eu.sqooss.service.db.util.ConfigurationOptionUtils;
import eu.sqooss.service.db.util.StoredProjectUtils;

@RunWith(MockitoJUnitRunner.class)
public class StoredProjectUtilsTest {

	private StoredProjectUtils utils;
	@Mock private DBService dbs;
	@Mock private ConfigurationOptionUtils cou;

	@Before
	public void setUp() {
		this.utils = new StoredProjectUtils(this.dbs, this.cou);
	}
	
	@Test
	public void testGetProjectVersionForNamedTagListNull() {
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class), anyInt()))
				.thenReturn(null);

		assertNull(this.utils.getProjectVersionForNamedTag(new StoredProject(), "tagName"));
	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testGetProjectVersionForNamedTagListEmpty() {
		List list = new ArrayList();
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class), anyInt()))
				.thenReturn(list);

		assertNull(this.utils.getProjectVersionForNamedTag(new StoredProject(), "tagName"));
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testGetProjectVersionForNamedTag() {
		ProjectVersion projectVersion = new ProjectVersion();
		List list = Arrays.asList(projectVersion);
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class), anyInt()))
				.thenReturn(list);

		assertEquals(projectVersion, this.utils.getProjectVersionForNamedTag(new StoredProject(), "tagName"));
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testUpdateConfigValueConfigOptNull() {
		ConfigurationOption co = new ConfigurationOption();
		List list = Arrays.asList(new StoredProjectConfig(null, "value", null));
		when(this.cou.getConfigurationOptionByKey(anyString())).thenReturn(co);
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class))).thenReturn(list);
		
		this.utils.addConfigValue(new StoredProject(), "key", "value");
		
		verify(this.dbs, times(0)).addRecord((DAObject) anyObject());
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testUpdateConfigValueConfigOptNullCONull() {
		List list = Arrays.asList(new StoredProjectConfig(null, "value2", null));
		when(this.cou.getConfigurationOptionByKey(anyString())).thenReturn(null);
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class))).thenReturn(list);
		
		this.utils.addConfigValue(new StoredProject(), "key", "value");
		
		verify(this.dbs, times(2)).addRecord((DAObject) anyObject());
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testUpdateConfigValueConfigOptNotNull() {
		ConfigurationOption co = new ConfigurationOption();
		List list = Arrays.asList(new StoredProjectConfig(null, "value", null));
		when(this.cou.getConfigurationOptionByKey(anyString())).thenReturn(co);
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class))).thenReturn(list);
		
		this.utils.addConfig(new StoredProject(), ConfigOption.PROJECT_CONTACT, "value");
		
		verify(this.dbs, times(0)).addRecord((DAObject) anyObject());
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testUpdateConfigValueConfigOptNotNullCONull() {
		List list = Arrays.asList(new StoredProjectConfig(null, "value2", null));
		when(this.cou.getConfigurationOptionByKey(anyString())).thenReturn(null);
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class))).thenReturn(list);
		
		ConfigOption projectBtsSource = ConfigOption.PROJECT_BTS_SOURCE;
		this.utils.addConfig(new StoredProject(), projectBtsSource, "value");
		
		verify(this.dbs, times(2)).addRecord((DAObject) anyObject());
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testSetConfigValue() {
		ConfigurationOption co = new ConfigurationOption();
		List list = Arrays.asList(new StoredProjectConfig(null, "value", null));
		when(this.cou.getConfigurationOptionByKey(anyString())).thenReturn(co);
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class))).thenReturn(list);
		
		this.utils.setConfigValue(new StoredProject(), "key", "value");
		
		verify(this.dbs).addRecord((DAObject) anyObject());
		verify(this.dbs).deleteRecords(eq(list));
	}
	
	@Test
	public void testGetProjectByNameListNull() {
		when(this.dbs.findObjectsByProperties(eq(StoredProject.class), anyMapOf(String.class, Object.class)))
				.thenReturn(null);
		
		assertNull(this.utils.getProjectByName("foo"));
	}
	
	@Test
	public void testGetProjectByNameListEmpty() {
		when(this.dbs.findObjectsByProperties(eq(StoredProject.class), anyMapOf(String.class, Object.class)))
				.thenReturn(new ArrayList<StoredProject>());
		
		assertNull(this.utils.getProjectByName("foo"));
	}
	
	@Test
	public void testGetProjectByNameListNonEmpty() {
		StoredProject storedProject = new StoredProject();
		when(this.dbs.findObjectsByProperties(eq(StoredProject.class), anyMapOf(String.class, Object.class)))
				.thenReturn(Arrays.asList(storedProject));
		
		assertEquals(storedProject, this.utils.getProjectByName("foo"));
	}
	
	@Test
	public void testGetProjectCountListNull() {
		when(this.dbs.doHQL(anyString())).thenReturn(null);
		assertEquals(0, this.utils.getProjectCount());
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testGetProjectCountListEmpty() {
		when(this.dbs.doHQL(anyString())).thenReturn(new ArrayList());
		assertEquals(0, this.utils.getProjectCount());
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testGetProjectCountListNonEmpty() {
		when(this.dbs.doHQL(anyString())).thenReturn(new ArrayList(Arrays.asList(5L)));
		assertEquals(5L, this.utils.getProjectCount());
	}
	
	@Test
	public void testGetVersionsCountListNull() {
		when(this.dbs.doHQL(anyString())).thenReturn(null);
		StoredProject sp = new StoredProject();
		sp.setId(2L);
		assertEquals(0, this.utils.getVersionsCount(sp));
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testGetProjectVersionsListEmpty() {
		when(this.dbs.doHQL(anyString())).thenReturn(new ArrayList());
		StoredProject sp = new StoredProject();
		sp.setId(2L);
		assertEquals(0, this.utils.getVersionsCount(sp));
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testGetVersionsCountListNonEmpty() {
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class))).thenReturn(new ArrayList(Arrays.asList(5L)));
		StoredProject sp = new StoredProject();
		sp.setId(2L);
		assertEquals(5L, this.utils.getVersionsCount(sp));
	}

	@Test
	public void testGetMailsCountListNull() {
		when(this.dbs.doHQL(anyString())).thenReturn(null);
		StoredProject sp = new StoredProject();
		sp.setId(2L);
		assertEquals(0, this.utils.getMailsCount(sp));
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testGetProjectMailsListEmpty() {
		when(this.dbs.doHQL(anyString())).thenReturn(new ArrayList());
		StoredProject sp = new StoredProject();
		sp.setId(2L);
		assertEquals(0, this.utils.getMailsCount(sp));
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testGetMailsCountListNonEmpty() {
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class))).thenReturn(new ArrayList(Arrays.asList(5L)));
		StoredProject sp = new StoredProject();
		sp.setId(2L);
		assertEquals(5L, this.utils.getMailsCount(sp));
	}
	
	@Test
	public void testGetBugsCountListNull() {
		when(this.dbs.doHQL(anyString())).thenReturn(null);
		StoredProject sp = new StoredProject();
		sp.setId(2L);
		assertEquals(0, this.utils.getBugsCount(sp));
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testGetProjectBugsListEmpty() {
		when(this.dbs.doHQL(anyString())).thenReturn(new ArrayList());
		StoredProject sp = new StoredProject();
		sp.setId(2L);
		assertEquals(0, this.utils.getBugsCount(sp));
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testGetBugsCountListNonEmpty() {
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class))).thenReturn(new ArrayList(Arrays.asList(5L)));
		StoredProject sp = new StoredProject();
		sp.setId(2L);
		assertEquals(5L, this.utils.getBugsCount(sp));
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testIsEvaluatedListEmpty() {
		when(this.dbs.doHQL(anyString())).thenReturn(new ArrayList());
		assertFalse(this.utils.isEvaluated(new StoredProject()));
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testIsEvaluatedListProjectVersion() {
		Metric metric = new Metric();
		metric.setMetricType(new MetricType(Type.PROJECT_VERSION));
		when(this.dbs.doHQL(anyString())).thenReturn(new ArrayList(Arrays.asList(metric)));
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class), eq(1))).thenReturn(new ArrayList(Arrays.asList(1, 2, 3)));
		assertTrue(this.utils.isEvaluated(new StoredProject()));
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testIsEvaluatedListNonEmptySourceFile() {
		Metric metric = new Metric();
		metric.setMetricType(new MetricType(Type.SOURCE_FILE));
		when(this.dbs.doHQL(anyString())).thenReturn(new ArrayList(Arrays.asList(metric)));
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class), eq(1))).thenReturn(new ArrayList(Arrays.asList(1, 2, 3)));
		assertTrue(this.utils.isEvaluated(new StoredProject()));
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testIsEvaluatedListNonEmptySourceDirectory() {
		Metric metric = new Metric();
		metric.setMetricType(new MetricType(Type.SOURCE_DIRECTORY));
		when(this.dbs.doHQL(anyString())).thenReturn(new ArrayList(Arrays.asList(metric)));
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class), eq(1))).thenReturn(new ArrayList(Arrays.asList(1, 2, 3)));
		assertTrue(this.utils.isEvaluated(new StoredProject()));
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testIsEvaluatedListNonEmptyMailThread() {
		Metric metric = new Metric();
		metric.setMetricType(new MetricType(Type.MAILTHREAD));
		when(this.dbs.doHQL(anyString())).thenReturn(new ArrayList(Arrays.asList(metric)));
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class), eq(1))).thenReturn(new ArrayList(Arrays.asList(1, 2, 3)));
		assertTrue(this.utils.isEvaluated(new StoredProject()));
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testIsEvaluatedListNonEmptyMailMessage() {
		Metric metric = new Metric();
		metric.setMetricType(new MetricType(Type.MAILMESSAGE));
		when(this.dbs.doHQL(anyString())).thenReturn(new ArrayList(Arrays.asList(metric)));
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class), eq(1))).thenReturn(new ArrayList(Arrays.asList(1, 2, 3)));
		assertTrue(this.utils.isEvaluated(new StoredProject()));
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testIsEvaluatedListNonEmptyEncapsUnit() {
		Metric metric = new Metric();
		metric.setMetricType(new MetricType(Type.ENCAPSUNIT));
		when(this.dbs.doHQL(anyString())).thenReturn(new ArrayList(Arrays.asList(metric)));
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class), eq(1))).thenReturn(new ArrayList(Arrays.asList(1, 2, 3)));
		assertTrue(this.utils.isEvaluated(new StoredProject()));
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testIsEvaluatedListNonEmptyExecunit() {
		Metric metric = new Metric();
		metric.setMetricType(new MetricType(Type.EXECUNIT));
		when(this.dbs.doHQL(anyString())).thenReturn(new ArrayList(Arrays.asList(metric)));
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class), eq(1))).thenReturn(new ArrayList(Arrays.asList(1, 2, 3)));
		assertTrue(this.utils.isEvaluated(new StoredProject()));
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testIsEvaluatedListNonEmptyNameSpace() {
		Metric metric = new Metric();
		metric.setMetricType(new MetricType(Type.NAMESPACE));
		when(this.dbs.doHQL(anyString())).thenReturn(new ArrayList(Arrays.asList(metric)));
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class), eq(1))).thenReturn(new ArrayList(Arrays.asList(1, 2, 3)));
		assertTrue(this.utils.isEvaluated(new StoredProject()));
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testIsEvaluatedListNonEmptyBug() {
		Metric metric = new Metric();
		metric.setMetricType(new MetricType(Type.BUG));
		when(this.dbs.doHQL(anyString())).thenReturn(new ArrayList(Arrays.asList(metric)));
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class), eq(1))).thenReturn(new ArrayList(Arrays.asList(1, 2, 3)));
		assertFalse(this.utils.isEvaluated(new StoredProject()));
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testIsEvaluatedListNonEmptyNoResults() {
		Metric metric = new Metric();
		metric.setMetricType(new MetricType(Type.NAMESPACE));
		when(this.dbs.doHQL(anyString())).thenReturn(new ArrayList(Arrays.asList(metric)));
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class), eq(1))).thenReturn(new ArrayList());
		assertFalse(this.utils.isEvaluated(new StoredProject()));
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testFromProject() {
		when(this.dbs.findObjectsByProperties(eq(StoredProjectConfig.class), anyMapOf(String.class, Object.class))).thenReturn(new ArrayList());
		assertEquals(new ArrayList(), this.utils.fromProject(null));
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testGetTaggedVersions() {
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class))).thenReturn(new ArrayList());
		assertEquals(new ArrayList(), this.utils.getTaggedVersions(null));
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testGetValues() {
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class))).thenReturn(new ArrayList());
		assertEquals(new ArrayList(), this.utils.getValues(null, null));
	}
}
