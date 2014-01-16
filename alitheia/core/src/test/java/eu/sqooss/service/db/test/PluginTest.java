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
import eu.sqooss.service.db.MailMessageMeasurement;
import eu.sqooss.service.db.MailingList;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.NameSpace;
import eu.sqooss.service.db.OhlohDeveloper;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;
import eu.sqooss.service.db.ProjectVersionParent;
import eu.sqooss.service.db.StoredProjectConfig;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DBService.class, AlitheiaCore.class, Branch.class })
public class PluginTest {

	static Plugin testObject;

	@BeforeClass
	public static void setUp() {
		testObject = new Plugin();
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
	public void testDescriptionGetterSetter()
	{
		//set the value via the setter
		testObject.setDescription("testDescription");
		
		//Pull out property in order to compare
		String actualValue = Whitebox.<String>getInternalState(testObject, "description");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,"testDescription");
		
		String getValue=  testObject.getDescription();
		//Compare the expected with the actual result
		assertEquals(getValue,"testDescription");	
	}
	@Test
	public void testVersionGetterSetter() {
		// set the value via the setter
		testObject.setVersion("testVersion");

		// Pull out property in order to compare
		String actualValue = Whitebox.<String> getInternalState(testObject,
				"version");

		// Compare the expected with the actual result
		assertEquals(actualValue, "testVersion");

		String getValue = testObject.getVersion();
		// Compare the expected with the actual result
		assertEquals(getValue, "testVersion");
	}
	
	@Test
	public void testHashCodeGetterSetter() {
		// set the value via the setter
		testObject.setHashcode("testHashCode");

		// Pull out property in order to compare
		String actualValue = Whitebox.<String> getInternalState(testObject,
				"hashcode");

		// Compare the expected with the actual result
		assertEquals(actualValue, "testHashCode");

		String getValue = testObject.getHashcode();
		// Compare the expected with the actual result
		assertEquals(getValue, "testHashCode");
	}

}
