package eu.sqooss.test.service.webadmin;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import eu.sqooss.core.AlitheiaCore;

import eu.sqooss.impl.service.webadmin.ProjectDeleteJob;
import eu.sqooss.service.db.StoredProject;

//@RunWith(PowerMockRunner.class)
//@PrepareForTest(ProjectDeleteJob.class)
public class ProjectDeleteJobTest {
	ProjectDeleteJob projectDeleteJob;
	StoredProject storedProject;
	AlitheiaCore alitheiaCore;

	@Before
	public void setUp() throws Exception {
		alitheiaCore = mock(AlitheiaCore.class);
		storedProject = mock(StoredProject.class);
		
		testConstructor();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPriority() {
		assertEquals(0xff, projectDeleteJob.priority());
	}

	@Test
	
	public void testRun() throws Exception{
		alitheiaCore = mock(AlitheiaCore.class, Mockito.CALLS_REAL_METHODS);
		storedProject = new StoredProject("testProject");
		projectDeleteJob = ProjectDeleteJob.makeProjectDeleteJob(alitheiaCore, storedProject);
		projectDeleteJob.run();
	}

	@Test
	public void testConstructor() {
		// created an additional method in ProjectDeleteJob to create
		// a ProjectDeleteJob instance, because the constructor is not visible here
		projectDeleteJob = ProjectDeleteJob.makeProjectDeleteJob(alitheiaCore, storedProject);
	}

	@Test
	public void testToString() {
		Mockito.when(storedProject.toString()).thenReturn("a string");
		assertEquals("ProjectDeleteJob - Project:{a string}", projectDeleteJob.toString());
	}

}
