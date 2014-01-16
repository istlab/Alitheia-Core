package eu.sqooss.service.db.test;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.Branch;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.EncapsulationUnit;
import eu.sqooss.service.db.EncapsulationUnitMeasurement;
import eu.sqooss.service.db.ExecutionUnit;
import eu.sqooss.service.db.MailMessage;
import eu.sqooss.service.db.MailMessageMeasurement;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.NameSpace;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;
import eu.sqooss.service.db.ProjectVersionParent;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.StoredProjectConfig;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DBService.class, AlitheiaCore.class, Branch.class })
public class EncapsulationUnitTest {

	static EncapsulationUnit testObject;

	@BeforeClass
	public static void setUp() {
		testObject = new EncapsulationUnit();
	}

	@Test
	public void testInitialiser() {
		ProjectFile file = new ProjectFile();
		EncapsulationUnit testValue = new EncapsulationUnit(file);
	
		ProjectFile setFile = Whitebox.<ProjectFile> getInternalState(testValue, "file");
		assertEquals(file, setFile);
	}
	@Test
	public void testIdGetterSetter() {
		// set the value via the setter
		testObject.setId(1900L);

		// Pull out property in order to compare
		long actualValue = Whitebox.<Long> getInternalState(testObject,
				"id");

		// Compare the expected with the actual result
		assertEquals(actualValue, 1900L);

		long getValue = testObject.getId();
		// Compare the expected with the actual result
		assertEquals(getValue, 1900L);
	}
	@Test
	public void testNameGetterSetter() {
		// set the value via the setter
		testObject.setName("testName");

		// Pull out property in order to compare
		String actualValue = Whitebox.<String> getInternalState(testObject,
				"name");

		// Compare the expected with the actual result
		assertEquals(actualValue, "testName");

		String getValue = testObject.getName();
		// Compare the expected with the actual result
		assertEquals(getValue, "testName");
	}
	
	@Test
	public void testFileGetterSetter() {

		ProjectFile testValue = mock(ProjectFile.class);
		// set the value via the setter
		testObject.setFile(testValue);

		// Pull out property in order to compare
		ProjectFile actualValue = Whitebox.<ProjectFile> getInternalState(
				testObject, "file");

		// Compare the expected with the actual result
		assertEquals(actualValue, testValue);

		ProjectFile getValue = testObject.getFile();
		// Compare the expected with the actual result
		assertEquals(getValue, testValue);
	}
	
	@Test
	public void testMeasurementsGetterSetter() {
		Set<EncapsulationUnitMeasurement> setValue = new HashSet<EncapsulationUnitMeasurement>();
		// set the value via the setter
		testObject.setMeasurements(setValue);

		// Pull out property in order to compare
		Set<EncapsulationUnitMeasurement> actualValue = Whitebox
				.<Set<EncapsulationUnitMeasurement>> getInternalState(testObject,
						"measurements");

		// Compare the expected with the actual result
		assertEquals(actualValue, setValue);

		Set<EncapsulationUnitMeasurement> getValue = testObject.getMeasurements();
		// BugReportMessage the expected with the actual result
		assertEquals(getValue, setValue);
	}
	
	@Test
	public void testExecutionUnitGetterSetter() {
		Set<ExecutionUnit> setValue = new HashSet<ExecutionUnit>();
		// set the value via the setter
		testObject.setExecUnits(setValue);

		// Pull out property in order to compare
		Set<ExecutionUnit> actualValue = Whitebox
				.<Set<ExecutionUnit>> getInternalState(testObject,
						"execUnits");

		// Compare the expected with the actual result
		assertEquals(actualValue, setValue);

		Set<ExecutionUnit> getValue = testObject.getExecUnits();
		// BugReportMessage the expected with the actual result
		assertEquals(getValue, setValue);
		
		//test the other part of the method:
		testObject.setExecUnits(null);
		getValue = testObject.getExecUnits();
		// BugReportMessage the expected with the actual result
		assertNotNull(getValue);
	}

	@Test
	public void testNameSpaceGetterSetter() {

		NameSpace testValue = mock(NameSpace.class);
		// set the value via the setter
		testObject.setNamespace(testValue);

		// Pull out property in order to compare
		NameSpace actualValue = Whitebox.<NameSpace> getInternalState(
				testObject, "namespace");

		// Compare the expected with the actual result
		assertEquals(actualValue, testValue);

		NameSpace getValue = testObject.getNamespace();
		// Compare the expected with the actual result
		assertEquals(getValue, testValue);
	}
	
	@Test
	public void testToString()
	{
		testObject.setName("testName");
		assertEquals("testName", testObject.toString());
	}
}
