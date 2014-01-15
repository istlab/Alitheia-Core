package eu.sqooss.test.service.db.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.sqooss.service.db.BugPriority;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Directory;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.util.DirectoryUtils;

@RunWith(MockitoJUnitRunner.class)
public class DirectoryUtilsTest {
	@Mock private DBService dbService;
	@Mock private StoredProject sp;
	private DirectoryUtils du;
	private Directory expectedDirectory;
	private final String path = "/";

	@Before
	public void setUp() {
		 this.expectedDirectory = new Directory();
		 this.expectedDirectory.setPath(path);
		 
		 this.du = new DirectoryUtils(this.dbService);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getDirectoryByPathNonExistentTest() {
		when(
				dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
		
		Directory actual = du.getDirectoryByPath(path, false);

		assertNull(actual);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getDirectoryByPathExistentTest() {
		when(
				dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList(Arrays.asList(expectedDirectory)));
		
		Directory actual = du.getDirectoryByPath(path, false);

		assertEquals(expectedDirectory.getPath(), actual.getPath());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getDirectoryByPathCreateTest() {
		when(
				dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
		when(dbService.addRecord(any(BugPriority.class))).thenReturn(true);
		
		Directory actual = du.getDirectoryByPath(path, true);

		assertEquals(expectedDirectory.getPath(), actual.getPath());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void getDirectoryByPathCreateFailsTest() {
		when(
				dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
		when(dbService.addRecord(any(BugPriority.class))).thenReturn(false);
		
		Directory actual = du.getDirectoryByPath(path, true);

		assertNull(actual);
	}
}
