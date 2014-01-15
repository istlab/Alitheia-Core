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

import eu.sqooss.service.db.Bug;
import eu.sqooss.service.db.BugPriority;
import eu.sqooss.service.db.BugPriority.Priority;
import eu.sqooss.service.db.BugReportMessage;
import eu.sqooss.service.db.BugResolution;
import eu.sqooss.service.db.BugResolution.Resolution;
import eu.sqooss.service.db.BugSeverity;
import eu.sqooss.service.db.BugSeverity.Severity;
import eu.sqooss.service.db.BugStatus.Status;
import eu.sqooss.service.db.BugStatus;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.util.BugUtils;

@RunWith(MockitoJUnitRunner.class)
public class BugUtilsTest {
	@Mock private DBService dbService;
	@Mock private StoredProject sp;
	private String bugID = "TESTNAME";
	private Bug expectedBug;
	private BugPriority lowPriority;
	private BugResolution unknownResolution;
	private BugSeverity normalSeverity;
	private BugStatus resolvedStatus;
	private BugUtils bu;

	@Before
	public void setUp() {
		 this.expectedBug = new Bug();
		 this.expectedBug.setProject(this.sp);
		 this.expectedBug.setBugID(this.bugID);
		 
		 this.lowPriority = new BugPriority();
		 this.lowPriority.setBugPriority(Priority.LOW);
		 
		 this.unknownResolution = new BugResolution();
		 this.unknownResolution.setBugResolution(Resolution.UNKNOWN);
		 
		 this.normalSeverity = new BugSeverity();
		 this.normalSeverity.setBugSeverity(Severity.NORMAL);
		 
		 this.resolvedStatus = new BugStatus();
		 this.resolvedStatus.setBugStatus(Status.RESOLVED);

		 this.bu = new BugUtils(this.dbService);
	}

	@Test
	public void getBugByIdNonExistentTest() {
		Bug actual = bu.getBugById(bugID, sp);

		assertNull(actual);
		verify(dbService).doHQL(anyString(),
				anyMapOf(String.class, Object.class), eq(1));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getBugByIdExistentTest() {
		when(
				dbService.doHQL(anyString(),
						anyMapOf(String.class, Object.class), eq(1)))
				.thenReturn(new ArrayList(Arrays.asList(expectedBug)));

		Bug actual = bu.getBugById(bugID, sp);

		verify(dbService).doHQL(anyString(),
				anyMapOf(String.class, Object.class), eq(1));
		assertEquals(expectedBug.getBugID(), actual.getBugID());
		assertEquals(expectedBug.getProject(), actual.getProject());
	}
	
	@Test
	public void getBugReportCommentsTest() {
		when(dbService.doHQL(anyString(), anyMapOf(String.class, Object.class)))
				.thenReturn(null);
		List<BugReportMessage> actual = bu.getBugReportComments(expectedBug);

		assertNull(actual);
		verify(dbService).doHQL(anyString(),
				anyMapOf(String.class, Object.class));
	}
	
	@Test
	public void getLastBugUpdateNullTest() {
		assertNull(bu.getLastBugUpdate(null));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getLastBugUpdateNonExistentTest() {
		when(
				dbService.doHQL(anyString(),
						anyMapOf(String.class, Object.class), eq(1)))
				.thenReturn(new ArrayList());

		Bug actual = bu.getLastBugUpdate(sp);

		assertNull(actual);
		verify(dbService).doHQL(anyString(),
				anyMapOf(String.class, Object.class), eq(1));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getLastBugUpdateExistentTest() {
		when(
				dbService.doHQL(anyString(),
						anyMapOf(String.class, Object.class), eq(1)))
				.thenReturn(new ArrayList(Arrays.asList(expectedBug)));

		Bug actual = bu.getLastBugUpdate(sp);

		verify(dbService).doHQL(anyString(),
				anyMapOf(String.class, Object.class), eq(1));
		assertEquals(expectedBug.getBugID(), actual.getBugID());
		assertEquals(expectedBug.getProject(), actual.getProject());
	}
	
	@Test
	public void getBugPriorityNullTest() {
		assertNull(bu.getBugPriority(null));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getBugPriorityNonExistentFailToAddTest() {
		when(
				dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
		when(dbService.addRecord(any(BugPriority.class))).thenReturn(false);
		
		BugPriority actual = bu.getBugPriority(Priority.LOW);

		assertNull(actual);
		verify(dbService).findObjectsByProperties(any(Class.class),
				anyMapOf(String.class, Object.class));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getBugPriorityNoCreateNonExistentTest() {
		when(
				dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
		
		BugPriority actual = bu.getBugPriority("LOW", false);

		assertNull(actual);
		verify(dbService).findObjectsByProperties(any(Class.class),
				anyMapOf(String.class, Object.class));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getBugPriorityNoCreateExistentTest() {
		when(
				dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList(Arrays.asList(lowPriority)));
		
		BugPriority actual = bu.getBugPriority("LOW", false);

		verify(dbService).findObjectsByProperties(any(Class.class),
				anyMapOf(String.class, Object.class));
		assertEquals(lowPriority.getBugPriority(), actual.getBugPriority());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getBugPriorityCreateNullPriorityTest() {
		when(
				dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
		
		BugPriority actual = bu.getBugPriority(null, true);

		assertNull(actual);
		verify(dbService).findObjectsByProperties(any(Class.class),
				anyMapOf(String.class, Object.class));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getBugPriorityCreateNonExistentFailToAddTest() {
		when(
				dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
		when(dbService.addRecord(any(BugPriority.class))).thenReturn(false);
		
		BugPriority actual = bu.getBugPriority("LOW", true);

		assertNull(actual);
		verify(dbService).findObjectsByProperties(any(Class.class),
				anyMapOf(String.class, Object.class));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getBugPriorityCreateNonExistentTest() {
		when(
				dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
		when(dbService.addRecord(any(BugPriority.class))).thenReturn(true);
		
		BugPriority actual = bu.getBugPriority("LOW", true);

		verify(dbService).findObjectsByProperties(any(Class.class),
				anyMapOf(String.class, Object.class));
		assertEquals(lowPriority.getBugPriority(), actual.getBugPriority());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getBugResolutionNoCreateNonExistentTest() {
		when(
				dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
		
		BugResolution actual = bu.getBugResolution("UNKNOWN", false);

		assertNull(actual);
		verify(dbService).findObjectsByProperties(any(Class.class),
				anyMapOf(String.class, Object.class));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getBugResolutionNoCreateExistentTest() {
		when(
				dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList(Arrays.asList(unknownResolution)));
		
		BugResolution actual = bu.getBugResolution("UNKNOWN", false);

		verify(dbService).findObjectsByProperties(any(Class.class),
				anyMapOf(String.class, Object.class));
		assertEquals(unknownResolution.getBugResolution(), actual.getBugResolution());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getBugResolutionCreateNullPriorityTest() {
		when(
				dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
		
		BugResolution actual = bu.getBugResolution(null, true);

		assertNull(actual);
		verify(dbService).findObjectsByProperties(any(Class.class),
				anyMapOf(String.class, Object.class));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getSimpleBugResolutionTest() {
		when(
				dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
		when(dbService.addRecord(any(BugPriority.class))).thenReturn(false);
		
		BugResolution actual = bu.getBugResolution(Resolution.UNKNOWN);

		assertNull(actual);
		verify(dbService).findObjectsByProperties(any(Class.class),
				anyMapOf(String.class, Object.class));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getBugResolutionCreateNonExistentFailToAddTest() {
		when(
				dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
		when(dbService.addRecord(any(BugPriority.class))).thenReturn(false);
		
		BugResolution actual = bu.getBugResolution("UNKNOWN", true);

		assertNull(actual);
		verify(dbService).findObjectsByProperties(any(Class.class),
				anyMapOf(String.class, Object.class));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getBugResolutionCreateNonExistentTest() {
		when(
				dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
		when(dbService.addRecord(any(BugPriority.class))).thenReturn(true);
		
		BugResolution actual = bu.getBugResolution("UNKNOWN", true);

		verify(dbService).findObjectsByProperties(any(Class.class),
				anyMapOf(String.class, Object.class));
		assertEquals(unknownResolution.getBugResolution(), actual.getBugResolution());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getBugSeverityNoCreateNonExistentTest() {
		when(
				dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
		
		BugSeverity actual = bu.getBugSeverity("NORMAL", false);

		assertNull(actual);
		verify(dbService).findObjectsByProperties(any(Class.class),
				anyMapOf(String.class, Object.class));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getBugSeverityNoCreateExistentTest() {
		when(
				dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList(Arrays.asList(normalSeverity)));
		
		BugSeverity actual = bu.getBugSeverity("NORMAL", false);

		verify(dbService).findObjectsByProperties(any(Class.class),
				anyMapOf(String.class, Object.class));
		assertEquals(normalSeverity.getBugSeverity(), actual.getBugSeverity());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getBugSeverityCreateNullPriorityTest() {
		when(
				dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
		
		BugSeverity actual = bu.getBugSeverity(null, true);

		assertNull(actual);
		verify(dbService).findObjectsByProperties(any(Class.class),
				anyMapOf(String.class, Object.class));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getSimpleBugSeverityTest() {
		when(
				dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
		when(dbService.addRecord(any(BugPriority.class))).thenReturn(false);
		
		BugSeverity actual = bu.getBugSeverity(Severity.NORMAL);

		assertNull(actual);
		verify(dbService).findObjectsByProperties(any(Class.class),
				anyMapOf(String.class, Object.class));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getBugSeverityCreateNonExistentFailToAddTest() {
		when(
				dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
		when(dbService.addRecord(any(BugPriority.class))).thenReturn(false);
		
		BugSeverity actual = bu.getBugSeverity("NORMAL", true);

		assertNull(actual);
		verify(dbService).findObjectsByProperties(any(Class.class),
				anyMapOf(String.class, Object.class));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getBugSeverityCreateNonExistentTest() {
		when(
				dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
		when(dbService.addRecord(any(BugPriority.class))).thenReturn(true);
		
		BugSeverity actual = bu.getBugSeverity("NORMAL", true);

		verify(dbService).findObjectsByProperties(any(Class.class),
				anyMapOf(String.class, Object.class));
		assertEquals(normalSeverity.getBugSeverity(), actual.getBugSeverity());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getBugStatusNoCreateNonExistentTest() {
		when(
				dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
		
		BugStatus actual = bu.getBugStatus("RESOLVED", false);

		assertNull(actual);
		verify(dbService).findObjectsByProperties(any(Class.class),
				anyMapOf(String.class, Object.class));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getBugStatusNoCreateExistentTest() {
		when(
				dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList(Arrays.asList(resolvedStatus)));
		
		BugStatus actual = bu.getBugStatus("RESOLVED", false);

		verify(dbService).findObjectsByProperties(any(Class.class),
				anyMapOf(String.class, Object.class));
		assertEquals(resolvedStatus.getBugStatus(), actual.getBugStatus());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getBugStatusCreateNullPriorityTest() {
		when(
				dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
		
		BugStatus actual = bu.getBugStatus(null, true);

		assertNull(actual);
		verify(dbService).findObjectsByProperties(any(Class.class),
				anyMapOf(String.class, Object.class));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getSimpleBugStatusTest() {
		when(
				dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
		when(dbService.addRecord(any(BugPriority.class))).thenReturn(false);
		
		BugStatus actual = bu.getBugStatus(Status.RESOLVED);

		assertNull(actual);
		verify(dbService).findObjectsByProperties(any(Class.class),
				anyMapOf(String.class, Object.class));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getBugStatusCreateNonExistentFailToAddTest() {
		when(
				dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
		when(dbService.addRecord(any(BugPriority.class))).thenReturn(false);
		
		BugStatus actual = bu.getBugStatus("RESOLVED", true);

		assertNull(actual);
		verify(dbService).findObjectsByProperties(any(Class.class),
				anyMapOf(String.class, Object.class));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void getBugStatusCreateNonExistentTest() {
		when(
				dbService.findObjectsByProperties(any(Class.class),
						anyMapOf(String.class, Object.class))).thenReturn(
				new ArrayList());
		when(dbService.addRecord(any(BugPriority.class))).thenReturn(true);
		
		BugStatus actual = bu.getBugStatus("RESOLVED", true);

		verify(dbService).findObjectsByProperties(any(Class.class),
				anyMapOf(String.class, Object.class));
		assertEquals(resolvedStatus.getBugStatus(), actual.getBugStatus());
	}
}
