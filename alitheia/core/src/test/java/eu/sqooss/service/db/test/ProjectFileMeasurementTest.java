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
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileMeasurement;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DBService.class, AlitheiaCore.class, Branch.class })
public class ProjectFileMeasurementTest {

	static ProjectFileMeasurement testObject;

	@BeforeClass
	public static void setUp() {
		testObject = new ProjectFileMeasurement();
	}

	@Test
	public void testInitialiser() {

		Metric metric = mock(Metric.class);
		ProjectFile pf = mock(ProjectFile.class);
		String value = "testValue";
		ProjectFileMeasurement initObject = new ProjectFileMeasurement(metric,
				pf, value);

		ProjectFile setPF = Whitebox.<ProjectFile> getInternalState(initObject,
				"projectFile");
		Metric setMetric = Whitebox.<Metric> getInternalState(initObject,
				"metric");
		String setResult = Whitebox.<String> getInternalState(initObject,
				"result");

		assertEquals(pf, setPF);
		assertEquals(metric, setMetric);
		assertEquals(value, setResult);
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

		Metric testMetric = mock(Metric.class);
		// set the value via the setter
		testObject.setMetric(testMetric);

		// Pull out property in order to compare
		Metric actualValue = Whitebox.<Metric> getInternalState(testObject,
				"metric");

		// Compare the expected with the actual result
		assertEquals(actualValue, testMetric);

		Metric getValue = testObject.getMetric();
		// Compare the expected with the actual result
		assertEquals(getValue, testMetric);
	}

	@Test
	public void testProjectFileGetterSetter() {

		ProjectFile setObject = mock(ProjectFile.class);
		// set the value via the setter
		testObject.setProjectFile(setObject);

		// Pull out property in order to compare
		ProjectFile actualValue = Whitebox.<ProjectFile> getInternalState(
				testObject, "projectFile");

		// Compare the expected with the actual result
		assertEquals(actualValue, setObject);

		ProjectFile getValue = testObject.getProjectFile();
		// Compare the expected with the actual result
		assertEquals(getValue, setObject);
	}
}
