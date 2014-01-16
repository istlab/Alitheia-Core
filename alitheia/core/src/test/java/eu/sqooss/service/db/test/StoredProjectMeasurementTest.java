package eu.sqooss.service.db.test;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.Branch;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.StoredProjectMeasurement;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DBService.class, AlitheiaCore.class, Branch.class })
public class StoredProjectMeasurementTest {

	static StoredProjectMeasurement testObject;

	@BeforeClass
	public static void setUp() {
		testObject = new StoredProjectMeasurement();
	}
	
	@Test
	public void testInitialiser()
	{
		StoredProject project = new StoredProject();
		String name = "testName";
		Branch branch = new Branch(project,name);
		String setName = Whitebox.<String>getInternalState(branch, "name");
		StoredProject setProject = Whitebox.<StoredProject>getInternalState(branch, "project");
		assertEquals(name,setName);
		assertEquals(project,setProject);
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
	public void testResultGetterSetter() {
		// set the value via the setter
		testObject.setResult("testResult");

		// Pull out property in order to compare
		String actualValue = Whitebox.<String> getInternalState(testObject,
				"result");

		// Compare the expected with the actual result
		assertEquals(actualValue, "testResult");

		String getValue = testObject.getResult();
		// Compare the expected with the actual result
		assertEquals(getValue, "testResult");
	}

	@Test
	public void testMetricGetterSetter() {

		Metric testValue = mock(Metric.class);
		// set the value via the setter
		testObject.setMetric(testValue);

		// Pull out property in order to compare
		Metric actualValue = Whitebox.<Metric> getInternalState(testObject,
				"metric");

		// Compare the expected with the actual result
		assertEquals(actualValue, testValue);

		Metric getValue = testObject.getMetric();
		// Compare the expected with the actual result
		assertEquals(getValue, testValue);
	}

	@Test
	public void testProjectGetterSetter() {

		StoredProject testValue = mock(StoredProject.class);
		// set the value via the setter
		testObject.setStoredProject(testValue);

		// Pull out property in order to compare
		StoredProject actualValue = Whitebox.<StoredProject> getInternalState(
				testObject, "storedProject");

		// Compare the expected with the actual result
		assertEquals(actualValue, testValue);

		StoredProject getValue = testObject.getStoredProject();
		// Compare the expected with the actual result
		assertEquals(getValue, testValue);
	}
}
