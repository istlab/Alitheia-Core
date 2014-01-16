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
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.EncapsulationUnit;
import eu.sqooss.service.db.EncapsulationUnitMeasurement;
import eu.sqooss.service.db.ExecutionUnit;
import eu.sqooss.service.db.ExecutionUnitMeasurement;
import eu.sqooss.service.db.MailingListThread;
import eu.sqooss.service.db.MailingListThreadMeasurement;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.NameSpace;
import eu.sqooss.service.db.NameSpaceMeasurement;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileMeasurement;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;
import eu.sqooss.service.db.ProjectVersionParent;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.StoredProjectMeasurement;
import eu.sqooss.service.db.Tag;
import static org.mockito.Mockito.*;


@RunWith(PowerMockRunner.class)
@PrepareForTest({DBService.class, AlitheiaCore.class, Branch.class})
public class ProjectVersionTest {
	
	
static ProjectVersion testObject;



	@BeforeClass
	public static void setUp() {
		testObject = new ProjectVersion();
		assertEquals(208537,testObject.hashCode());
	}

	@Test
	public void testIdGetterSetter()
	{
		//set the value via the setter
		testObject.setId(1900L);
		
		//Pull out property in order to compare
		long actualValue = Whitebox.<Long>getInternalState(testObject, "id");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,1900L);
		
		long getValue=  testObject.getId();
		//Compare the expected with the actual result
		assertEquals(getValue,1900L);	
	}
	
	@Test
	public void testTimeStampGetterSetter()
	{
		//set the value via the setter
		testObject.setTimestamp(2000L);
		
		//Pull out property in order to compare
		long actualValue = Whitebox.<Long>getInternalState(testObject, "timestamp");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,2000L);
		
		long getValue=  testObject.getTimestamp();
		//Compare the expected with the actual result
		assertEquals(getValue,2000L);	
	}
	@Test
	public void testSequenceGetterSetter()
	{
		//set the value via the setter
		testObject.setSequence(1500L);
		
		//Pull out property in order to compare
		long actualValue = Whitebox.<Long>getInternalState(testObject, "sequence");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,1500L);
		
		long getValue=  testObject.getSequence();
		//Compare the expected with the actual result
		assertEquals(getValue,1500L);	
	}

	@Test
	public void testCommitMsgGetterSetter()
	{
		//set the value via the setter
		testObject.setCommitMsg("testCommitMessage");
		
		//Pull out property in order to compare
		String actualValue = Whitebox.<String>getInternalState(testObject, "commitMsg");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,"testCommitMessage");
		
		String getValue=  testObject.getCommitMsg();
		//Compare the expected with the actual result
		assertEquals(getValue,"testCommitMessage");	
	}
	
	@Test
	public void testRevisionIdGetterSetter()
	{
		//set the value via the setter
		testObject.setRevisionId("testRevisionId");
		
		//Pull out property in order to compare
		String actualValue = Whitebox.<String>getInternalState(testObject, "revisionId");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,"testRevisionId");
		
		String getValue=  testObject.getRevisionId();
		//Compare the expected with the actual result
		assertEquals(getValue,"testRevisionId");	
	}
	@Test
	public void testProjectGetterSetter()
	{
		
		StoredProject testProject=  mock(StoredProject.class);
		//set the value via the setter
		testObject.setProject(testProject);
		
		//Pull out property in order to compare
		StoredProject actualValue = Whitebox.<StoredProject>getInternalState(testObject, "project");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,testProject);
		
		StoredProject getValue=  testObject.getProject();
		//Compare the expected with the actual result
		assertEquals(getValue,testProject);	
	}
	
	
	@Test
	public void testDeveloperGetterSetter()
	{
		
		Developer testValue=  mock(Developer.class);
		//set the value via the setter
		testObject.setCommitter(testValue);
		
		//Pull out property in order to compare
		Developer actualValue = Whitebox.<Developer>getInternalState(testObject, "committer");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,testValue);
		
		Developer getValue=  testObject.getCommitter();
		//Compare the expected with the actual result
		assertEquals(getValue,testValue);	
	}
	@Test
	public void testBranchIncomingGetterSetter()
	{
		Set<Branch> setValue = new HashSet<Branch>();
		//set the value via the setter
		testObject.setIncomingBranches(setValue);
		
		//Pull out property in order to compare
		Set<Branch> actualValue = Whitebox.<Set<Branch>>getInternalState(testObject, "incomingBranches");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,setValue);
		
		Set<Branch> getValue=  testObject.getIncomingBranches();
		//Compare the expected with the actual result
		assertEquals(getValue,setValue);
		
		//Test the other part of the getter
		testObject.setIncomingBranches(null);
		 getValue=  testObject.getIncomingBranches();
		//Compare the expected with the actual result
		assertNotNull(getValue);
	}
	
	@Test
	public void testBranchOutgoingGetterSetter()
	{
		Set<Branch> setValue = new HashSet<Branch>();
		//set the value via the setter
		testObject.setOutgoingBranches(setValue);
		
		//Pull out property in order to compare
		Set<Branch> actualValue = Whitebox.<Set<Branch>>getInternalState(testObject, "outgoingBranches");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,setValue);
		
		Set<Branch> getValue=  testObject.getOutgoingBranches();
		//Compare the expected with the actual result
		assertEquals(getValue,setValue);	
		//test the other state of the getter:
		testObject.setOutgoingBranches(null);
		 getValue=  testObject.getOutgoingBranches();
		//Compare the expected with the actual result
		 assertNotNull(getValue);
	}
	
	@Test
	public void testNameSpacesGetterSetter()
	{
		Set<NameSpace> setValue = new HashSet<NameSpace>();
		//set the value via the setter
		testObject.setNamespaces(setValue);
		
		//Pull out property in order to compare
		Set<NameSpace> actualValue = Whitebox.<Set<NameSpace>>getInternalState(testObject, "namespaces");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,setValue);
		
		Set<NameSpace> getValue=  testObject.getNamespaces();
		//Compare the expected with the actual result
		assertEquals(getValue,setValue);	
		
		//test the other state of the getter:
		testObject.setNamespaces(null);
		 getValue=  testObject.getNamespaces();
		//Compare the expected with the actual result
		 assertNotNull(getValue);
	}
	
	@Test
	public void testProjectVersionMeasurementGetterSetter()
	{
		Set<ProjectVersionMeasurement> setValue = new HashSet<ProjectVersionMeasurement>();
		//set the value via the setter
		testObject.setMeasurements(setValue);
		
		//Pull out property in order to compare
		Set<ProjectVersionMeasurement> actualValue = Whitebox.<Set<ProjectVersionMeasurement>>getInternalState(testObject, "measurements");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,setValue);
		
		Set<ProjectVersionMeasurement> getValue=  testObject.getMeasurements();
		//Compare the expected with the actual result
		assertEquals(getValue,setValue);	
	}
	
	@Test
	public void testParentsMeasurementGetterSetter()
	{
		Set<ProjectVersionParent> setValue = new HashSet<ProjectVersionParent>();
		//set the value via the setter
		testObject.setParents(setValue);
		
		//Pull out property in order to compare
		Set<ProjectVersionParent> actualValue = Whitebox.<Set<ProjectVersionParent>>getInternalState(testObject, "parents");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,setValue);
		
		Set<ProjectVersionParent> getValue=  testObject.getParents();
		//Compare the expected with the actual result
		assertEquals(getValue,setValue);	
		
		//test the other state of the getter:
		testObject.setParents(null);
		 getValue=  testObject.getParents();
		//Compare the expected with the actual result
		 assertNotNull(getValue);
	}
	
	@Test
	public void testTagsGetterSetter()
	{
		Set<Tag> setValue = new HashSet<Tag>();
		//set the value via the setter
		testObject.setTags(setValue);
		
		//Pull out property in order to compare
		Set<Tag> actualValue = Whitebox.<Set<Tag>>getInternalState(testObject, "tags");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,setValue);
		
		Set<Tag> getValue=  testObject.getTags();
		//Compare the expected with the actual result
		assertEquals(getValue,setValue);	
	}
	
	@Test
	public void testProjectFileGetterSetter()
	{
		Set<ProjectFile> setValue = new HashSet<ProjectFile>();
		//set the value via the setter
		testObject.setVersionFiles(setValue);
		
		//Pull out property in order to compare
		Set<ProjectFile> actualValue = Whitebox.<Set<ProjectFile>>getInternalState(testObject, "versionFiles");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,setValue);
		
		Set<ProjectFile> getValue=  testObject.getVersionFiles();
		//Compare the expected with the actual result
		assertEquals(getValue,setValue);
		
		//test the other state of the getter:
		testObject.setVersionFiles(null);
		 getValue=  testObject.getVersionFiles();
		//Compare the expected with the actual result
		 assertNotNull(getValue);
		
	}
	
	@Test
	public void testDateGetterSetter() {
		// set the value via the setter
		Date setValue = new Date();
		testObject.setDate(setValue);

		// Pull out property in order to compare
		long actualValue = Whitebox.<Long> getInternalState(testObject,
				"timestamp");

		// Compare the expected with the actual result
		assertEquals(setValue.getTime(),actualValue);

		Date getValue = testObject.getDate();
		// Compare the expected with the actual result
		assertEquals(getValue, setValue);
	}
	
}
