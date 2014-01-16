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
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;
import eu.sqooss.service.db.ProjectVersionParentId;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DBService.class, AlitheiaCore.class, Branch.class })
public class ProjectVersionParentIdTest {

	static ProjectVersionParentId testObject;

	@BeforeClass
	public static void setUp() {
		testObject = new ProjectVersionParentId();
	}

	

	@Test
	public void testParentIdGetterSetter() {
		// set the value via the setter
		testObject.setParentid(1900L);

		// Pull out property in order to compare
		long actualValue = Whitebox.<Long> getInternalState(testObject, "parentid");

		// Compare the expected with the actual result
		assertEquals(actualValue, 1900L);

		long getValue = testObject.getParentid();
		// Compare the expected with the actual result
		assertEquals(getValue, 1900L);
	}

	@Test
	public void testChildIdGetterSetter() {
		// set the value via the setter
		testObject.setChildid(2300L);

		// Pull out property in order to compare
		long actualValue = Whitebox.<Long> getInternalState(testObject, "childid");

		// Compare the expected with the actual result
		assertEquals(actualValue, 2300L);

		long getValue = testObject.getChildid();
		// Compare the expected with the actual result
		assertEquals(getValue, 2300L);
	}
	
	
	@Test
	public void testHashCode() {
		// set the value via the setter
		testObject.setChildid(2L);
		testObject.setParentid(2300L);


		long getValue = testObject.hashCode();
		// Compare the expected with the actual result
		assertEquals(72263L, getValue);
	}
	
	@Test
	public void testEqualsMethod() 
	{
	// test on same object
		assertTrue(testObject.equals(testObject));
		//test on object of different type / null
		assertFalse(testObject.equals(null));
		
		//test on object with same property values
		testObject.setChildid(2L);
		testObject.setParentid(2300L);
		
		ProjectVersionParentId testValue = new ProjectVersionParentId();
		testValue.setChildid(2L);
		testValue.setParentid(2100L);
		
		assertEquals(testValue.getChildid(),testObject.getChildid());
		
		boolean result = testValue.equals(testObject);
		assertEquals(false,result);		
	}
}
