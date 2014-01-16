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
public class ProjectFileTest {
	
	
static ProjectFile testObject;



	@BeforeClass
	public static void setUp() {
		testObject = new ProjectFile();
		testObject.setName("t");
		assertEquals(9920403,testObject.hashCode());
		testObject.setName(null);
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
	public void testNameGetterSetter()
	{
		//set the value via the setter
		testObject.setName("testName");
		
		//Pull out property in order to compare
		String actualValue = Whitebox.<String>getInternalState(testObject, "name");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,"testName");
		
		String getValue=  testObject.getName();
		//Compare the expected with the actual result
		assertEquals(getValue,"testName");	
	}
	
	@Test
	public void testProjectVersionGetterSetter()
	{
		
		ProjectVersion testProject=  mock(ProjectVersion.class);
		//set the value via the setter
		testObject.setProjectVersion(testProject);
		
		//Pull out property in order to compare
		ProjectVersion actualValue = Whitebox.<ProjectVersion>getInternalState(testObject, "projectVersion");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,testProject);
		
		ProjectVersion getValue=  testObject.getProjectVersion();
		//Compare the expected with the actual result
		assertEquals(getValue,testProject);	
	}
	
	@Test
	public void testValidUntilGetterSetter()
	{
		
		ProjectVersion testProject=  mock(ProjectVersion.class);
		//set the value via the setter
		testObject.setValidUntil(testProject);
		
		//Pull out property in order to compare
		ProjectVersion actualValue = Whitebox.<ProjectVersion>getInternalState(testObject, "validUntil");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,testProject);
		
		ProjectVersion getValue=  testObject.getValidUntil();
		//Compare the expected with the actual result
		assertEquals(getValue,testProject);	
	}
	
	@Test
	public void testValidFromGetterSetter()
	{
		
		ProjectVersion testProject=  mock(ProjectVersion.class);
		//set the value via the setter
		testObject.setValidFrom(testProject);
		
		//Pull out property in order to compare
		ProjectVersion actualValue = Whitebox.<ProjectVersion>getInternalState(testObject, "validFrom");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,testProject);
		
		ProjectVersion getValue=  testObject.getValidFrom();
		//Compare the expected with the actual result
		assertEquals(getValue,testProject);	
	}
	
	@Test
	public void testProjectFileCopyGetterSetter()
	{
		
		ProjectFile testProject=  mock(ProjectFile.class);
		//set the value via the setter
		testObject.setCopyFrom(testProject);
		
		//Pull out property in order to compare
		ProjectFile actualValue = Whitebox.<ProjectFile>getInternalState(testObject, "copyFrom");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,testProject);
		
		ProjectFile getValue=  testObject.getCopyFrom();
		//Compare the expected with the actual result
		assertEquals(getValue,testProject);	
	}
	
}
