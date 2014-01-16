package eu.sqooss.service.db.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.Branch;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DBService.class, AlitheiaCore.class, Branch.class })
public class BranchTest {

	static Branch testObject;

	@BeforeClass
	public static void setUp() {
		testObject = new Branch();

	}

	@Test
	public void testInitialiser() {
		StoredProject project = new StoredProject();
		String name = "testName";
		Branch branch = new Branch(project, name);
		String setName = Whitebox.<String> getInternalState(branch, "name");
		StoredProject setProject = Whitebox.<StoredProject> getInternalState(
				branch, "project");
		assertEquals(name, setName);
		assertEquals(project, setProject);
	}

	@Test
	public void testBranchIncomingGetterSetter() {
		Set<ProjectVersion> setValue = new HashSet<ProjectVersion>();
		// set the value via the setter
		testObject.setBranchIncoming(setValue);

		// Pull out property in order to compare
		Set<ProjectVersion> actualValue = Whitebox
				.<Set<ProjectVersion>> getInternalState(testObject,
						"branchIncoming");

		// Compare the expected with the actual result
		assertEquals(actualValue, setValue);

		Set<ProjectVersion> getValue = testObject.getBranchIncoming();
		// Compare the expected with the actual result
		assertEquals(getValue, setValue);
	}

	@Test
	public void testBranchOutgoingGetterSetter() {
		Set<ProjectVersion> setValue = new HashSet<ProjectVersion>();
		// set the value via the setter
		testObject.setBranchOutgoing(setValue);

		// Pull out property in order to compare
		Set<ProjectVersion> actualValue = Whitebox
				.<Set<ProjectVersion>> getInternalState(testObject,
						"branchOutgoing");

		// Compare the expected with the actual result
		assertEquals(actualValue, setValue);

		Set<ProjectVersion> getValue = testObject.getBranchOutgoing();
		// Compare the expected with the actual result
		assertEquals(getValue, setValue);
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
	public void testProjectGetterSetter() {

		StoredProject testValue = mock(StoredProject.class);
		// set the value via the setter
		testObject.setProject(testValue);

		// Pull out property in order to compare
		StoredProject actualValue = Whitebox.<StoredProject> getInternalState(
				testObject, "project");

		// Compare the expected with the actual result
		assertEquals(actualValue, testValue);

		StoredProject getValue = testObject.getProject();
		// Compare the expected with the actual result
		assertEquals(getValue, testValue);
	}

	@Test
	public void testSuggestName() {
		// Setup the AlitheiaCore class for testing
		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(AlitheiaCore.class);
		PowerMockito.mockStatic(DBService.class);
		AlitheiaCore core = mock(AlitheiaCore.class);
		DBService dbService = mock(DBService.class);

		when(AlitheiaCore.getInstance()).thenReturn(core);
		when(core.getDBService()).thenReturn(dbService);

		// get the expected string which will be added in the database call
		String qNextSequence = Whitebox.<String> getInternalState(Branch.class,
				"qNextSequence");

		// mock the database call
		doReturn(new ArrayList<Long>()).when(dbService).doHQL(
				eq(qNextSequence), Matchers.<Map<String, Object>> any());

		// mock a storedproject as param to test
		StoredProject testProject = mock(StoredProject.class);
		// call the method
		String suggestedName = Branch.suggestName(testProject);
		// Compare the results
		assertEquals(suggestedName, "1");

		// Cover the other branch of the method
		ArrayList<Long> list = new ArrayList<Long>();
		list.add(100L);
		doReturn(list).when(dbService).doHQL(eq(qNextSequence),
				Matchers.<Map<String, Object>> any());
		String suggestedName2 = Branch.suggestName(testProject);
		// Compare the results
		assertEquals(suggestedName2, "101");
	}

	@Test
	public void testBranchFromName() {
		// Setup the AlitheiaCore class for testing
		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(AlitheiaCore.class);
		PowerMockito.mockStatic(DBService.class);
		AlitheiaCore core = mock(AlitheiaCore.class);
		DBService dbService = mock(DBService.class);

		when(AlitheiaCore.getInstance()).thenReturn(core);
		when(core.getDBService()).thenReturn(dbService);

		// get the expected string which will be added in the database call
		String qBranchByName = Whitebox.<String> getInternalState(Branch.class,
				"qBranchByName");

		// mock the database call
		doReturn(new ArrayList<Branch>()).when(dbService).doHQL(
				eq(qBranchByName), Matchers.<Map<String, Object>> any());
		Branch staticTestObject = Branch.fromName(mock(StoredProject.class),
				"testName", true);
		assertNull(staticTestObject);

		// test the other state of the method
		ArrayList<Branch> testList = new ArrayList<Branch>();
		testList.add(mock(Branch.class));
		doReturn(testList).when(dbService).doHQL(eq(qBranchByName),
				Matchers.<Map<String, Object>> any());
		staticTestObject = Branch.fromName(mock(StoredProject.class),
				"testName", true);
		assertEquals(staticTestObject, testList.get(0));
	}
}
