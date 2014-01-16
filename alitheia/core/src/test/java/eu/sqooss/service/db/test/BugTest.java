package eu.sqooss.service.db.test;

import static org.junit.Assert.*;

import java.awt.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.Branch;
import eu.sqooss.service.db.Bug;
import eu.sqooss.service.db.BugPriority;
import eu.sqooss.service.db.BugReportMessage;
import eu.sqooss.service.db.BugResolution;
import eu.sqooss.service.db.BugSeverity;
import eu.sqooss.service.db.BugStatus;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.Directory;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DBService.class, AlitheiaCore.class, Branch.class })
public class BugTest {

	static Bug testObject;

	@BeforeClass
	public static void setUp() {
		testObject = new Bug();
	}

	@Test
	public void testIdGetterSetter() {
		// set the value via the setter
		testObject.setId(1900L);

		// Pull out property in order to compare
		long actualValue = Whitebox.<Long> getInternalState(testObject, "id");

		// Compare the expected with the actual result
		assertEquals(actualValue, 1900L);

		long getValue = testObject.getId();
		// Compare the expected with the actual result
		assertEquals(getValue, 1900L);
	}

	@Test
	public void testCreationTSGetterSetter() {
		// set the value via the setter
		Date setValue = new Date();
		testObject.setCreationTS(setValue);

		// Pull out property in order to compare
		Date actualValue = Whitebox.<Date> getInternalState(testObject,
				"creationTS");

		// Compare the expected with the actual result
		assertEquals(actualValue, setValue);

		Date getValue = testObject.getCreationTS();
		// Compare the expected with the actual result
		assertEquals(getValue, setValue);
	}

	@Test
	public void testDeltaTSGetterSetter() {
		// set the value via the setter
		Date setValue = new Date();
		testObject.setDeltaTS(setValue);

		// Pull out property in order to compare
		Date actualValue = Whitebox.<Date> getInternalState(testObject,
				"deltaTS");

		// Compare the expected with the actual result
		assertEquals(actualValue, setValue);

		Date getValue = testObject.getDeltaTS();
		// Compare the expected with the actual result
		assertEquals(getValue, setValue);
	}

	@Test
	public void testUpdateRunGetterSetter() {
		// set the value via the setter
		Date setValue = new Date();
		testObject.setUpdateRun(setValue);

		// Pull out property in order to compare
		Date actualValue = Whitebox.<Date> getInternalState(testObject,
				"updateRun");

		// Compare the expected with the actual result
		assertEquals(actualValue, setValue);

		Date getValue = testObject.getUpdateRun();
		// Compare the expected with the actual result
		assertEquals(getValue, setValue);
	}

	@Test
	public void testShortDescGetterSetter() {
		// set the value via the setter
		testObject.setShortDesc("testDescription");

		// Pull out property in order to compare
		String actualValue = Whitebox.<String> getInternalState(testObject,
				"shortDesc");

		// Compare the expected with the actual result
		assertEquals(actualValue, "testDescription");

		String getValue = testObject.getShortDesc();
		// Compare the expected with the actual result
		assertEquals(getValue, "testDescription");
	}

	@Test
	public void testBugIdGetterSetter() {
		// set the value via the setter
		testObject.setBugID("bugId");

		// Pull out property in order to compare
		String actualValue = Whitebox.<String> getInternalState(testObject,
				"bugID");

		// Compare the expected with the actual result
		assertEquals(actualValue, "bugId");

		String getValue = testObject.getBugID();
		// Compare the expected with the actual result
		assertEquals(getValue, "bugId");
	}

	@Test
	public void testProjectGetterSetter() {

		StoredProject testValue = mock(StoredProject.class);
		// set the value via the setter
		testObject.setProject(testValue);

		// Pull out property in order to compare
		StoredProject actualValue = Whitebox.<StoredProject> getInternalState(
				testObject, "project");

		// Compare the expected with the actual result
		assertEquals(actualValue, testValue);

		StoredProject getValue = testObject.getProject();
		// Compare the expected with the actual result
		assertEquals(getValue, testValue);
	}

	@Test
	public void testReporterGetterSetter() {

		Developer testValue = mock(Developer.class);
		// set the value via the setter
		testObject.setReporter(testValue);

		// Pull out property in order to compare
		Developer actualValue = Whitebox.<Developer> getInternalState(
				testObject, "reporter");

		// Compare the expected with the actual result
		assertEquals(actualValue, testValue);

		Developer getValue = testObject.getReporter();
		// Compare the expected with the actual result
		assertEquals(getValue, testValue);
	}

	@Test
	public void testBugResolutionGetterSetter() {

		BugResolution testValue = mock(BugResolution.class);
		// set the value via the setter
		testObject.setResolution(testValue);

		// Pull out property in order to compare
		BugResolution actualValue = Whitebox.<BugResolution> getInternalState(
				testObject, "resolution");

		// Compare the expected with the actual result
		assertEquals(actualValue, testValue);

		BugResolution getValue = testObject.getResolution();
		// Compare the expected with the actual result
		assertEquals(getValue, testValue);
	}

	@Test
	public void testBugPriorityGetterSetter() {

		BugPriority testValue = mock(BugPriority.class);
		// set the value via the setter
		testObject.setPriority(testValue);

		// Pull out property in order to compare
		BugPriority actualValue = Whitebox.<BugPriority> getInternalState(
				testObject, "priority");

		// Compare the expected with the actual result
		assertEquals(actualValue, testValue);

		BugPriority getValue = testObject.getPriority();
		// Compare the expected with the actual result
		assertEquals(getValue, testValue);
	}

	@Test
	public void testBugSeverityGetterSetter() {

		BugSeverity testValue = mock(BugSeverity.class);
		// set the value via the setter
		testObject.setSeverity(testValue);

		// Pull out property in order to compare
		BugSeverity actualValue = Whitebox.<BugSeverity> getInternalState(
				testObject, "severity");

		// Compare the expected with the actual result
		assertEquals(actualValue, testValue);

		BugSeverity getValue = testObject.getSeverity();
		// Compare the expected with the actual result
		assertEquals(getValue, testValue);
	}
	@Test
	public void testBugStatusGetterSetter() {

		BugStatus testValue = mock(BugStatus.class);
		// set the value via the setter
		testObject.setStatus(testValue);

		// Pull out property in order to compare
		BugStatus actualValue = Whitebox.<BugStatus> getInternalState(
				testObject, "status");

		// Compare the expected with the actual result
		assertEquals(actualValue, testValue);

		BugStatus getValue = testObject.getStatus();
		// Compare the expected with the actual result
		assertEquals(getValue, testValue);
	}
	@Test
	public void testReportMessagesGetterSetter() {
		Set<BugReportMessage> setValue = new HashSet<BugReportMessage>();
		// set the value via the setter
		testObject.setReportMessages(setValue);

		// Pull out property in order to compare
		Set<BugReportMessage> actualValue = Whitebox
				.<Set<BugReportMessage>> getInternalState(testObject,
						"reportMessages");

		// Compare the expected with the actual result
		assertEquals(actualValue, setValue);

		Set<BugReportMessage> getValue = testObject.getReportMessages();
		// BugReportMessage the expected with the actual result
		assertEquals(getValue, setValue);
	}
	
	@Test
	public void testStaticGetBug() {
		// Setup the AlitheiaCore class for testing
		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(AlitheiaCore.class);
		PowerMockito.mockStatic(DBService.class);
		AlitheiaCore core = mock(AlitheiaCore.class);
		DBService dbService = mock(DBService.class);

		when(AlitheiaCore.getInstance()).thenReturn(core);
		when(core.getDBService()).thenReturn(dbService);

		// mock the database call
		doReturn(new ArrayList<Bug>()).when(dbService).doHQL(
				anyString(), Matchers.<Map<String, Object>> any(),anyInt());
		
		//Call the method
		Bug staticTestObject = Bug.getBug("test",mock(StoredProject.class));
		//check if the method was successful
		assertNull(staticTestObject);
		
		//check the other method state
		ArrayList<Bug> list = new ArrayList<Bug>();
		list.add(mock(Bug.class));
		doReturn(list).when(dbService).doHQL(
				anyString(), Matchers.<Map<String, Object>> any(), anyInt());
		
		//Call the method
		  staticTestObject = Bug.getBug("test",mock(StoredProject.class));
		//check if the method was successful
		assertEquals(list.get(0),staticTestObject);

		
	}
	
	@Test
	public void testStaticGetLastBugUpdate() {
		// Setup the AlitheiaCore class for testing
		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(AlitheiaCore.class);
		PowerMockito.mockStatic(DBService.class);
		AlitheiaCore core = mock(AlitheiaCore.class);
		DBService dbService = mock(DBService.class);

		when(AlitheiaCore.getInstance()).thenReturn(core);
		when(core.getDBService()).thenReturn(dbService);

		// mock the database call
		doReturn(new ArrayList<Bug>()).when(dbService).doHQL(
				anyString(), Matchers.<Map<String, Object>> any(),anyInt());
		
		//Call the method
		Bug staticTestObject = Bug.getLastUpdate(mock(StoredProject.class));
		//check if the method was successful
		assertNull(staticTestObject);
		
		//check the second method state
		ArrayList<Bug> list = new ArrayList<Bug>();
		list.add(mock(Bug.class));
		doReturn(list).when(dbService).doHQL(
				anyString(), Matchers.<Map<String, Object>> any(), anyInt());
		
		//Call the method
		  staticTestObject = Bug.getLastUpdate(mock(StoredProject.class));
		//check if the method was successful
		assertEquals(list.get(0),staticTestObject);

		//check the third method state
		staticTestObject = Bug.getLastUpdate(null);
		//check if the method was successful
		assertNull(staticTestObject);
	}
}
