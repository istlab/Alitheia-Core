package eu.sqooss.test.service.db.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Directory;
import eu.sqooss.service.db.ExecutionUnit;
import eu.sqooss.service.db.FileState;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileState;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.util.ProjectFileUtils;
import eu.sqooss.service.logging.Logger;

@RunWith(MockitoJUnitRunner.class)
public class ProjectFileUtilsTest {
	private static final String FILENAME = "FILENAME";
	private static final String PATH = "/";
	@Mock private DBService dbService;
	@Mock private Logger dbLogger;
	@Mock private StoredProject sp;
	@Mock private ProjectFileState pfs;
	private ProjectFileUtils bu;
	private Directory dir;
	private ProjectVersion pv;
	private ProjectFile pf;

	@Before
	public void setUp() {
		when(dbService.logger()).thenReturn(this.dbLogger);
		
		this.bu = new ProjectFileUtils(this.dbService);
		 
		this.dir = new Directory();
		this.dir.setPath(PATH);
		 
		this.pv = new ProjectVersion(this.sp);
		 
		this.pf = new ProjectFile(this.pv);
		this.pf.setIsDirectory(true);
		this.pf.setDir(dir);
		this.pf.setName(FILENAME);
		this.pf.setCopyFrom(pf);
		this.pf.setState(pfs);
	}
	
	@Test
	public void toDirectoryNotADirectoryTest() {
		this.pf.setIsDirectory(false);
		
		Directory actual = bu.toDirectory(pf);
		
		assertNull(actual);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void toDirectoryEmptyListTest() {
		when(	dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
		
		Directory actual = bu.toDirectory(pf);
		
		assertNull(actual);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void toDirectoryNullTest() {
		when(	dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				null);
		
		Directory actual = bu.toDirectory(pf);
		
		assertNull(actual);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void toDirectoryTest() {
		when(	dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList(Arrays.asList(dir)));
		
		Directory actual = bu.toDirectory(pf);
		
		assertEquals(dir, actual);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getPreviousFileVersionJustAddedTest() {
		when(	dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList(Arrays.asList(dir)));
		when(pfs.getFileStatus()).thenReturn(FileState.ADDED);
		
		ProjectFile actual = bu.getPreviousFileVersion(pf);
		
		assertNull(actual);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getPreviousFileVersionNoCopyNullTest1() {
		when(dbService.doHQL(anyString(),
				anyMapOf(String.class, Object.class), eq(1)))
			.thenReturn(new ArrayList());
				
		ProjectFile actual = bu.getPreviousFileVersion(pf);
		
		assertNull(actual);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getPreviousFileVersionNoCopyNullTest2() {
		when(dbService.doHQL(anyString(),
				anyMapOf(String.class, Object.class), eq(1)))
			.thenReturn(new ArrayList());
				
		pf.setCopyFrom(null);
		ProjectFile actual = bu.getPreviousFileVersion(pf);
		
		assertNull(actual);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getPreviousFileVersionTest() {
		when(dbService.doHQL(anyString(),
				anyMapOf(String.class, Object.class), eq(1)))
			.thenReturn(new ArrayList(Arrays.asList(pf)));
				
		ProjectFile actual = bu.getPreviousFileVersion(pf);
		
		assertEquals(pf, actual);
	}
	
	@Test
	public void getDeletionVersionDeletedTest() {
		when(pfs.getFileStatus()).thenReturn(FileState.DELETED);
				
		ProjectVersion actual = bu.getDeletionVersion(pf);
		
		assertEquals(pf.getProjectVersion(), actual);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getDeletionVersionExistentTest() {
		when(dbService.doHQL(anyString(),
				anyMapOf(String.class, Object.class)))
			.thenReturn(new ArrayList(Arrays.asList(pv)));
		when(dbService.isDBSessionActive())
			.thenReturn(true);
		when(dbService.findObjectsByProperties(eq(ProjectFileState.class), anyMapOf(String.class, Object.class)))
			.thenReturn(new ArrayList(Arrays.asList(pfs)));
		when(pfs.getFileStatus()).thenReturn(FileState.ADDED);
				
		ProjectVersion actual = bu.getDeletionVersion(pf);
		
		assertEquals(pf.getProjectVersion(), actual);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getDeletionVersionNonExistentTest() {
		when(dbService.doHQL(anyString(),
				anyMapOf(String.class, Object.class)))
			.thenReturn(new ArrayList());
		when(pfs.getFileStatus()).thenReturn(FileState.ADDED);
				
		ProjectVersion actual = bu.getDeletionVersion(pf);
		
		assertNull(actual);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getEnclosingDirectoryExistentTest() {
		when(dbService.doHQL(anyString(),
				anyMapOf(String.class, Object.class), eq(1)))
			.thenReturn(new ArrayList(Arrays.asList(pf)));
				
		ProjectFile actual = bu.getEnclosingDirectory(pf);
		
		assertEquals(pf, actual);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getEnclosingDirectoryNonExistentTest() {
		when(dbService.doHQL(anyString(),
				anyMapOf(String.class, Object.class), eq(1)))
			.thenReturn(new ArrayList());
				
		ProjectFile actual = bu.getEnclosingDirectory(pf);
		
		assertNull(actual);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getFileModificationsExistentTest() {
		when(dbService.doHQL(anyString(),
				anyMapOf(String.class, Object.class)))
			.thenReturn(new ArrayList(Arrays.asList(pf)));
				
		List<ProjectFile> actual = bu.getFileModifications(pf);
		
		assertEquals(pf, actual.get(0));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getFileModificationsNonExistentTest() {
		when(dbService.doHQL(anyString(),
				anyMapOf(String.class, Object.class)))
			.thenReturn(new ArrayList());
				
		List<ProjectFile> actual = bu.getFileModifications(pf);
		
		assertTrue(actual.isEmpty());
	}
	
	@Test
	public void findFileIdNullTest() {
				
		ProjectFile actual = bu.findFile(null, "", PATH, "", false);
		
		assertNull(actual);
	}
	
	@Test
	public void findFileNameNullTest() {
				
		ProjectFile actual = bu.findFile(0L, null, PATH, "", false);
		
		assertNull(actual);
	}
	
	@Test
	public void findFileNameNullTest2() {
				
		ProjectFile actual = bu.findFile(null, null, PATH, "", false);
		
		assertNull(actual);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void findFileNonExistentTest() {
		when(dbService.doHQL(anyString(),
				anyMapOf(String.class, Object.class), eq(1)))
			.thenReturn(new ArrayList());
				
		ProjectFile actual = bu.findFile(0L, "", PATH, "", true);
		
		assertNull(actual);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void findFileExistentTest() {
		when(dbService.doHQL(anyString(),
				anyMapOf(String.class, Object.class), eq(1)))
			.thenReturn(new ArrayList(Arrays.asList(pf)));
				
		ProjectFile actual = bu.findFile(0L, "", PATH, "");
		
		assertEquals(pf, actual);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getChangedExecutionUnitsExistentTest() {
		when(dbService.doHQL(anyString(),
				anyMapOf(String.class, Object.class)))
			.thenReturn(new ArrayList(Arrays.asList(pf)));
				
		List<ExecutionUnit> actual = bu.getChangedExecutionUnits(pf);
		
		assertEquals(pf, actual.get(0));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getChangedExecutionUnitsNonExistentTest() {
		when(dbService.doHQL(anyString(),
				anyMapOf(String.class, Object.class)))
			.thenReturn(new ArrayList());
				
		List<ExecutionUnit> actual = bu.getChangedExecutionUnits(pf);
		
		assertTrue(actual.isEmpty());
	}
}
