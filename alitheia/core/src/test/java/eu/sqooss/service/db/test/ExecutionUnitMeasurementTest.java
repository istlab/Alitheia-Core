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
import eu.sqooss.service.db.ExecutionUnit;
import eu.sqooss.service.db.ExecutionUnitMeasurement;
import eu.sqooss.service.db.Metric;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DBService.class, AlitheiaCore.class, Branch.class })
public class ExecutionUnitMeasurementTest {

	static ExecutionUnitMeasurement testObject;

	@BeforeClass
	public static void setUp() {
		testObject = new ExecutionUnitMeasurement();
	}

	@Test
	public void testInitialiser() {
		ExecutionUnit eu = mock(ExecutionUnit.class);
		Metric metric = mock(Metric.class);
		String result = " testResult";
		ExecutionUnitMeasurement initObject = new ExecutionUnitMeasurement(eu,
				metric, result);

		ExecutionUnit setEU = Whitebox.<ExecutionUnit> getInternalState(
				initObject, "executionUnit");
		Metric setMetric = Whitebox.<Metric> getInternalState(initObject,
				"metric");
		String setResult = Whitebox.<String> getInternalState(initObject,
				"result");

		assertEquals(eu, setEU);
		assertEquals(metric, setMetric);
		assertEquals(result, setResult);
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
	public void testProjectFileGetterSetter() {

		ExecutionUnit testValue = mock(ExecutionUnit.class);
		// set the value via the setter
		testObject.setExecutionUnit(testValue);

		// Pull out property in order to compare
		ExecutionUnit actualValue = Whitebox.<ExecutionUnit> getInternalState(
				testObject, "executionUnit");

		// Compare the expected with the actual result
		assertEquals(actualValue, testValue);

		ExecutionUnit getValue = testObject.getExecutionUnit();
		// Compare the expected with the actual result
		assertEquals(getValue, testValue);
	}
}
