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
import eu.sqooss.service.db.NameSpace;
import eu.sqooss.service.db.NameSpaceMeasurement;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DBService.class, AlitheiaCore.class, Branch.class })
public class NameSpaceMeasurementTest {

	static NameSpaceMeasurement testObject;

	@BeforeClass
	public static void setUp() {
		testObject = new NameSpaceMeasurement();
	}

	@Test
	public void testIdGetterSetter() {
		// set the value via the setter
		testObject.setId(1900L);

		// Pull out property in order to compare
		long actualValue = Whitebox.<Long> getInternalState(
				testObject, "id");

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
		String actualValue = Whitebox.<String> getInternalState(
				testObject, "result");

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
		Metric actualValue = Whitebox.<Metric> getInternalState(
				testObject, "metric");

		// Compare the expected with the actual result
		assertEquals(actualValue, testValue);

		Metric getValue = testObject.getMetric();
		// Compare the expected with the actual result
		assertEquals(getValue, testValue);
	}

	@Test
	public void testNamespaceGetterSetter() {

		NameSpace testNameSpace = mock(NameSpace.class);
		// set the value via the setter
		testObject.setNamespace(testNameSpace);

		// Pull out property in order to compare
		NameSpace actualValue = Whitebox.<NameSpace> getInternalState(
				testObject, "namespace");

		// Compare the expected with the actual result
		assertEquals(actualValue, testNameSpace);

		NameSpace getValue = testObject.getNamespace();
		// Compare the expected with the actual result
		assertEquals(getValue, testNameSpace);
	}
}
