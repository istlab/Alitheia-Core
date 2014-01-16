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
import eu.sqooss.service.db.BugPriority;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.ProjectVersion;


@RunWith(PowerMockRunner.class)
@PrepareForTest({DBService.class, AlitheiaCore.class, Branch.class})
public class BugPriorityTest {
	
	
static BugPriority testBugPriority;



	@BeforeClass
	public static void setUp() {
		testBugPriority = new BugPriority();
	}
	
	@Test
	public void testPriorityGetterSetter()
	{
		//set the value via the setter
		testBugPriority.setPriority("testPriority");
		
		//Pull out property in order to compare
		String actualValue = Whitebox.<String>getInternalState(testBugPriority, "priority");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,"testPriority");
		
		String getValue=  testBugPriority.getPriority();
		//Compare the expected with the actual result
		assertEquals(getValue,"testPriority");	
	}

	@Test
	public void testIdGetterSetter()
	{
		//set the value via the setter
		testBugPriority.setId(1900L);
		
		//Pull out property in order to compare
		long actualValue = Whitebox.<Long>getInternalState(testBugPriority, "id");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,1900L);
		
		long getValue=  testBugPriority.getId();
		//Compare the expected with the actual result
		assertEquals(getValue,1900L);	
	}
	
	@Test
	public void testBugsGetterSetter()
	{
		Set<Bug> setValue = new HashSet<Bug>();
		//set the value via the setter
		testBugPriority.setBugs(setValue);
		
		//Pull out property in order to compare
		Set<ProjectVersion> actualValue = Whitebox.<Set<ProjectVersion>>getInternalState(testBugPriority, "bugs");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,setValue);
		
		Set<Bug> getValue=  testBugPriority.getBugs();
		//Compare the expected with the actual result
		assertEquals(getValue,setValue);	
	}
}
