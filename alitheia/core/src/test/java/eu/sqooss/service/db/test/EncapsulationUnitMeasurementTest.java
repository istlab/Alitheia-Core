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
import eu.sqooss.service.db.EncapsulationUnit;
import eu.sqooss.service.db.EncapsulationUnitMeasurement;
import eu.sqooss.service.db.Metric;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DBService.class, AlitheiaCore.class, Branch.class })
public class EncapsulationUnitMeasurementTest {

	static EncapsulationUnitMeasurement testObject;

	@BeforeClass
	public static void setUp() {
		testObject = new EncapsulationUnitMeasurement();
	}

	@Test
	public void testInitialiser() {
		EncapsulationUnit eu = mock(EncapsulationUnit.class);
		Metric metric = mock(Metric.class);
		String result = " testResult";
		EncapsulationUnitMeasurement initObject = new EncapsulationUnitMeasurement(
				eu, metric, result);

		EncapsulationUnit setEU = Whitebox
				.<EncapsulationUnit> getInternalState(initObject,
						"encapsulationUnit");
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
	public void testEncapsulationUnitGetterSetter() {

		EncapsulationUnit setObject = mock(EncapsulationUnit.class);
		// set the value via the setter
		testObject.setEncapsulationUnit(setObject);

		// Pull out property in order to compare
		EncapsulationUnit actualValue = Whitebox
				.<EncapsulationUnit> getInternalState(testObject,
						"encapsulationUnit");

		// Compare the expected with the actual result
		assertEquals(actualValue, setObject);

		EncapsulationUnit getValue = testObject.getEncapsulationUnit();
		// Compare the expected with the actual result
		assertEquals(getValue, setObject);
	}
}
