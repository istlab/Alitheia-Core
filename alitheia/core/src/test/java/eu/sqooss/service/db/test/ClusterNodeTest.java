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
import eu.sqooss.service.db.BugReportMessage;
import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.MailMessageMeasurement;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.OhlohDeveloper;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;
import eu.sqooss.service.db.ProjectVersionParent;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.StoredProjectConfig;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DBService.class, AlitheiaCore.class, Branch.class })
public class ClusterNodeTest {

	static ClusterNode testObject;

	@BeforeClass
	public static void setUp() {
		testObject = new ClusterNode();
	}
	
	@Test
	public void testInitialiser() {
		ClusterNode testValue = new ClusterNode("testServerName");
	
		String setName = Whitebox.<String> getInternalState(testValue, "name");
		assertEquals("testServerName", setName);
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
	public void testProjectsGetterSetter() {
		Set<StoredProject> setValue = new HashSet<StoredProject>();
		// set the value via the setter
		testObject.setProjects(setValue);

		// Pull out property in order to compare
		Set<StoredProject> actualValue = Whitebox
				.<Set<StoredProject>> getInternalState(testObject,
						"projects");

		// Compare the expected with the actual result
		assertEquals(actualValue, setValue);

		Set<StoredProject> getValue = testObject.getProjects();
		// BugReportMessage the expected with the actual result
		assertEquals(getValue, setValue);
	}
	

}
