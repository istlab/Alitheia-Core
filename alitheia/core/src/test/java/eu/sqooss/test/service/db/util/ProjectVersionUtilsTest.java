package eu.sqooss.test.service.db.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.ProjectFileState;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.Tag;
import eu.sqooss.service.db.util.ProjectFileUtils;
import eu.sqooss.service.db.util.ProjectVersionUtils;

@RunWith(MockitoJUnitRunner.class)
public class ProjectVersionUtilsTest {

	private ProjectVersionUtils utils;
	@Mock private DBService dbs;
	
	@Before
	public void setUp() {
		this.utils = new ProjectVersionUtils(this.dbs, new ProjectFileUtils(this.dbs));
	}
	
	@Test
	public void testGetPreviousVersionListNull() {
		StoredProject sp = new StoredProject();
		sp.setId(5L);
		ProjectVersion pv = new ProjectVersion(sp);
		pv.setSequence(2L);
		
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class), anyInt())).thenReturn(null);
		assertNull(this.utils.getPreviousVersion(pv));
	}
	
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testGetPreviousVersionListEmpty() {
		StoredProject sp = new StoredProject();
		sp.setId(5L);
		ProjectVersion pv = new ProjectVersion(sp);
		pv.setSequence(2L);
		
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class), anyInt())).thenReturn(new ArrayList());
		assertNull(this.utils.getPreviousVersion(pv));
	}
	
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testGetPreviousVersionListNonEmpty() {
		StoredProject sp = new StoredProject();
		sp.setId(5L);
		ProjectVersion pv = new ProjectVersion(sp);
		pv.setSequence(2L);
		
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class), anyInt())).thenReturn(new ArrayList(Arrays.asList(pv)));
		assertEquals(pv, this.utils.getPreviousVersion(pv));
	}
	
	@Test
	public void testGetNextVersionListNull() {
		StoredProject sp = new StoredProject();
		sp.setId(5L);
		ProjectVersion pv = new ProjectVersion(sp);
		pv.setSequence(2L);
		
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class), anyInt())).thenReturn(null);
		assertNull(this.utils.getNextVersion(pv));
	}
	
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testGetNextVersionListEmpty() {
		StoredProject sp = new StoredProject();
		sp.setId(5L);
		ProjectVersion pv = new ProjectVersion(sp);
		pv.setSequence(2L);
		
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class), anyInt())).thenReturn(new ArrayList());
		assertNull(this.utils.getNextVersion(pv));
	}
	
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testGetNextVersionListNonEmpty() {
		StoredProject sp = new StoredProject();
		sp.setId(5L);
		ProjectVersion pv = new ProjectVersion(sp);
		pv.setSequence(2L);
		
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class), anyInt())).thenReturn(new ArrayList(Arrays.asList(pv)));
		assertEquals(pv, this.utils.getNextVersion(pv));
	}
	
	@Test
	public void testGetVersionByRevisionListNull() {
		when(this.dbs.findObjectsByProperties(eq(ProjectVersion.class), anyMapOf(String.class, Object.class))).thenReturn(null);
		assertNull(this.utils.getVersionByRevision(new StoredProject(), "id"));
	}
	
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testGetVersionByRevisionListEmpty() {
		when(this.dbs.findObjectsByProperties(eq(ProjectVersion.class), anyMapOf(String.class, Object.class))).thenReturn(new ArrayList());
		assertNull(this.utils.getVersionByRevision(new StoredProject(), "id"));
	}
	
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testGetVersionByRevisionListNonEmpty() {
		ProjectVersion pv = new ProjectVersion();
		when(this.dbs.findObjectsByProperties(eq(ProjectVersion.class), anyMapOf(String.class, Object.class))).thenReturn(new ArrayList(Arrays.asList(pv)));
		assertEquals(pv, this.utils.getVersionByRevision(new StoredProject(), "id"));
	}
	
	@Test
	public void testGetVersionByTimestampListNull() {
		when(this.dbs.findObjectsByProperties(eq(ProjectVersion.class), anyMapOf(String.class, Object.class))).thenReturn(null);
		assertNull(this.utils.getVersionByTimestamp(new StoredProject(), 12L));
	}
	
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testGetVersionByTimestampListEmpty() {
		when(this.dbs.findObjectsByProperties(eq(ProjectVersion.class), anyMapOf(String.class, Object.class))).thenReturn(new ArrayList());
		assertNull(this.utils.getVersionByTimestamp(new StoredProject(), 12L));
	}
	
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testGetVersionByTimestampListNonEmpty() {
		ProjectVersion pv = new ProjectVersion();
		when(this.dbs.findObjectsByProperties(eq(ProjectVersion.class), anyMapOf(String.class, Object.class))).thenReturn(new ArrayList(Arrays.asList(pv)));
		assertEquals(pv, this.utils.getVersionByTimestamp(new StoredProject(), 12L));
	}
	
	@Test
	public void testGetFirstProjectVersionListNull() {
		StoredProject sp = new StoredProject();
		sp.setId(5L);
		
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class))).thenReturn(null);
		assertNull(this.utils.getFirstProjectVersion(sp));
	}
	
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testGetFirstProjectVersionListEmpty() {
		StoredProject sp = new StoredProject();
		sp.setId(5L);
		
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class), anyInt())).thenReturn(new ArrayList());
		assertNull(this.utils.getFirstProjectVersion(sp));
	}
	
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testGetFirstProjectVersionListNonEmpty() {
		StoredProject sp = new StoredProject();
		sp.setId(5L);
		ProjectVersion pv = new ProjectVersion(sp);
		pv.setSequence(2L);
		
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class))).thenReturn(new ArrayList(Arrays.asList(pv)));
		assertEquals(pv, this.utils.getFirstProjectVersion(sp));
	}
	
	@Test
	public void testGetLastProjectVersionListNull() {
		StoredProject sp = new StoredProject();
		sp.setId(5L);
		
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class))).thenReturn(null);
		assertNull(this.utils.getLastProjectVersion(sp));
	}
	
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testGetLastProjectVersionListEmpty() {
		StoredProject sp = new StoredProject();
		sp.setId(5L);
		
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class))).thenReturn(new ArrayList());
		assertNull(this.utils.getLastProjectVersion(sp));
	}
	
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testGetLastProjectVersionListNonEmpty() {
		StoredProject sp = new StoredProject();
		sp.setId(5L);
		ProjectVersion pv = new ProjectVersion(sp);
		pv.setSequence(2L);
		
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class))).thenReturn(new ArrayList(Arrays.asList(pv)));
		assertEquals(pv, this.utils.getLastProjectVersion(sp));
	}
	
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testGetLastMeasuredVersionListEmpty() {
		StoredProject sp = new StoredProject();
		sp.setId(5L);
		
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class), anyInt())).thenReturn(new ArrayList());
		assertNull(this.utils.getLastMeasuredVersion(sp, new Metric()));
	}
	
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testGetLastMeasuredVersionListNonEmpty() {
		StoredProject sp = new StoredProject();
		sp.setId(5L);
		ProjectVersion pv = new ProjectVersion(sp);
		pv.setSequence(2L);
		
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class), anyInt())).thenReturn(new ArrayList(Arrays.asList(pv)));
		assertEquals(pv, this.utils.getLastMeasuredVersion(sp, new Metric()));
	}
	
	@Test
	public void testGetFilesCountListNull() {
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class))).thenReturn(null);
		assertEquals(0, this.utils.getFilesCount(new ProjectVersion(), new ProjectFileState()));
	}
	
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testGetFilesCountListEmpty() {
		StoredProject sp = new StoredProject();
		sp.setId(5L);
		
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class))).thenReturn(new ArrayList());
		assertEquals(0, this.utils.getFilesCount(new ProjectVersion(), new ProjectFileState()));
	}
	
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testGetFilesCountListNonEmpty() {
		StoredProject sp = new StoredProject();
		sp.setId(5L);
		ProjectVersion pv = new ProjectVersion(sp);
		pv.setSequence(2L);
		
		when(this.dbs.doHQL(anyString(), anyMapOf(String.class, Object.class))).thenReturn(new ArrayList(Arrays.asList(2L)));
		assertEquals(2L, this.utils.getFilesCount(new ProjectVersion(), new ProjectFileState()));
	}
	
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testIsTagListEmpty() {
		when(this.dbs.findObjectsByProperties(eq(Tag.class), anyMapOf(String.class, Object.class))).thenReturn(new ArrayList());
		assertFalse(this.utils.isTag(new ProjectVersion()));
	}
	
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testIsTagListNonEmpty() {
		when(this.dbs.findObjectsByProperties(eq(Tag.class), anyMapOf(String.class, Object.class))).thenReturn(new ArrayList(Arrays.asList(1, 2, 3)));
		assertTrue(this.utils.isTag(new ProjectVersion()));
	}
}
