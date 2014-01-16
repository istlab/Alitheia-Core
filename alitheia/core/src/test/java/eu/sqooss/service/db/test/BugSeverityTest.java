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
import eu.sqooss.service.db.Bug;
import eu.sqooss.service.db.BugResolution;
import eu.sqooss.service.db.BugSeverity;
import eu.sqooss.service.db.BugStatus;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.MailMessageMeasurement;
import eu.sqooss.service.db.MailingList;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.NameSpace;
import eu.sqooss.service.db.OhlohDeveloper;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.ProjectFileState;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;
import eu.sqooss.service.db.ProjectVersionParent;
import eu.sqooss.service.db.StoredProjectConfig;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DBService.class, AlitheiaCore.class, Branch.class })
public class BugSeverityTest {

	static BugSeverity testObject;

	@BeforeClass
	public static void setUp() {
		testObject = new BugSeverity();
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
	public void testBugsGetterSetter() {
		Set<Bug> setValue = new HashSet<Bug>();
		// set the value via the setter
		testObject.setBugs(setValue);

		// Pull out property in order to compare
		Set<Bug> actualValue = Whitebox
				.<Set<Bug>> getInternalState(testObject,
						"bugs");

		// Compare the expected with the actual result
		assertEquals(actualValue, setValue);

		Set<Bug> getValue = testObject.getBugs();
		// BugReportMessage the expected with the actual result
		assertEquals(getValue, setValue);
	}
	
	@Test
	public void testSeverityGetterSetter()
	{
		//set the value via the setter
		testObject.setSeverity("testSeverity");
		
		//Pull out property in order to compare
		String actualValue = Whitebox.<String>getInternalState(testObject, "severity");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,"testSeverity");
		
		String getValue=  testObject.getSeverity();
		//Compare the expected with the actual result
		assertEquals(getValue,"testSeverity");	
	}
}
