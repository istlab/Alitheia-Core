package eu.sqooss.service.db.test;

import static org.junit.Assert.*;

import java.util.Date;

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
import eu.sqooss.service.db.DeveloperAlias;
import eu.sqooss.service.db.MailMessageMeasurement;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.OhlohDeveloper;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;
import eu.sqooss.service.db.ProjectVersionParent;
import eu.sqooss.service.db.StoredProjectConfig;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DBService.class, AlitheiaCore.class, Branch.class })
public class OhlohDeveloperTest {

	static OhlohDeveloper testObject;

	@BeforeClass
	public static void setUp() {
		testObject = new OhlohDeveloper();
	}
	
	@Test
	public void testInitialiser() {
		OhlohDeveloper testValue = new OhlohDeveloper("testuName","testHash","testOlohId");
	
		String setUname = Whitebox.<String> getInternalState(testValue, "uname");
		String setHash = Whitebox.<String> getInternalState(testValue, "emailHash");
		String setOhlohid = Whitebox.<String> getInternalState(testValue, "ohlohId");
		Date setTimeStamp = Whitebox.<Date> getInternalState(testValue, "timestamp");

		assertEquals("testuName", setUname);
		assertEquals("testHash", setHash);
		assertEquals("testOlohId", setOhlohid);
		assertNotNull(setTimeStamp);
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
	public void testUnameGetterSetter()
	{
		//set the value via the setter
		testObject.setUname("testUName");
		
		//Pull out property in order to compare
		String actualValue = Whitebox.<String>getInternalState(testObject, "uname");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,"testUName");
		
		String getValue=  testObject.getUname();
		//Compare the expected with the actual result
		assertEquals(getValue,"testUName");	
	}
	
	@Test
	public void testEmailHashGetterSetter()
	{
		//set the value via the setter
		testObject.setEmailHash("testEmailHash");
		
		//Pull out property in order to compare
		String actualValue = Whitebox.<String>getInternalState(testObject, "emailHash");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,"testEmailHash");
		
		String getValue=  testObject.getEmailHash();
		//Compare the expected with the actual result
		assertEquals(getValue,"testEmailHash");	
	}
	
	@Test
	public void testOhlohIdGetterSetter()
	{
		//set the value via the setter
		testObject.setOhlohId("testOhlohId");
		
		//Pull out property in order to compare
		String actualValue = Whitebox.<String>getInternalState(testObject, "ohlohId");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,"testOhlohId");
		
		String getValue=  testObject.getOhlohId();
		//Compare the expected with the actual result
		assertEquals(getValue,"testOhlohId");	
	}
	
}
