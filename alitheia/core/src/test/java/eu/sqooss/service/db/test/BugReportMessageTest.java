package eu.sqooss.service.db.test;

import static org.junit.Assert.*;

import java.util.Date;
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
import eu.sqooss.service.db.Bug;
import eu.sqooss.service.db.BugReportMessage;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.MailMessageMeasurement;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;
import eu.sqooss.service.db.ProjectVersionParent;
import eu.sqooss.service.db.StoredProjectConfig;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DBService.class, AlitheiaCore.class, Branch.class })
public class BugReportMessageTest {

	static BugReportMessage testObject;

	@BeforeClass
	public static void setUp() {
		testObject = new BugReportMessage();
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
	public void testTimeStampGetterSetter()
	{
		Date setValue = new Date();
		//set the value via the setter
		testObject.setTimestamp(setValue);
		
		//Pull out property in order to compare
		Date actualValue = Whitebox.<Date>getInternalState(testObject, "timestamp");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,setValue);
		
		Date getValue=  testObject.getTimestamp();
		//Compare the expected with the actual result
		assertEquals(getValue,setValue);	
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
	public void testBugsGetterSetter() {
		Bug setValue = new Bug();
		// set the value via the setter
		testObject.setBug(setValue);

		// Pull out property in order to compare
		Bug actualValue = Whitebox
				.<Bug> getInternalState(testObject,
						"bug");

		// Compare the expected with the actual result
		assertEquals(actualValue, setValue);

		Bug getValue = testObject.getBug();
		// BugReportMessage the expected with the actual result
		assertEquals(getValue, setValue);
	}
	
	@Test
	public void testTextIdGetterSetter()
	{
		//set the value via the setter
		testObject.setText("testText");
		
		//Pull out property in order to compare
		String actualValue = Whitebox.<String>getInternalState(testObject, "text");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,"testText");
		
		String getValue=  testObject.getText();
		//Compare the expected with the actual result
		assertEquals(getValue,"testText");	
	}
}
