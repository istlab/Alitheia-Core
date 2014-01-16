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
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.MailMessageMeasurement;
import eu.sqooss.service.db.MailingList;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.NameSpace;
import eu.sqooss.service.db.OhlohDeveloper;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileState;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;
import eu.sqooss.service.db.ProjectVersionParent;
import eu.sqooss.service.db.StoredProjectConfig;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DBService.class, AlitheiaCore.class, Branch.class })
public class ProjectFileStateTest {

	static ProjectFileState testObject;

	@BeforeClass
	public static void setUp() {
		testObject = new ProjectFileState();
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
	public void testFilesGetterSetter() {
		Set<ProjectFile> setValue = new HashSet<ProjectFile>();
		// set the value via the setter
		testObject.setFiles(setValue);

		// Pull out property in order to compare
		Set<ProjectFile> actualValue = Whitebox
				.<Set<ProjectFile>> getInternalState(testObject,
						"files");

		// Compare the expected with the actual result
		assertEquals(actualValue, setValue);

		Set<ProjectFile> getValue = testObject.getFiles();
		// BugReportMessage the expected with the actual result
		assertEquals(getValue, setValue);
	}
}
