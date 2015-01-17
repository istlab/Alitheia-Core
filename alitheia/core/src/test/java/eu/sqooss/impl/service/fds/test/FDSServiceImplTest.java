package eu.sqooss.impl.service.fds.test;


import static org.junit.Assert.*;

import org.junit.*;

import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import eu.sqooss.impl.service.fds.*;
import eu.sqooss.service.db.*;
import eu.sqooss.service.tds.*;

public class FDSServiceImplTest {
	
	static FDSServiceImpl fdsMock;
	static ProjectFile pf;
	static String revisionId;
	static int projectId;
	static ProjectFileState pfs;
	static TDSService tds;
	static ProjectVersion pv;
	static ProjectAccessor accessor;
	static SCMAccessor scm;
	static Revision newRevision;
	static StoredProject project;
	
	@BeforeClass
	public static void init() throws InvalidAccessorException{
		fdsMock = new FDSServiceImpl();
		tds = Mockito.mock(TDSService.class);
		pfs = Mockito.mock(ProjectFileState.class);
		accessor = Mockito.mock(ProjectAccessor.class);
		scm = Mockito.mock(SCMAccessor.class);
		newRevision = Mockito.mock(Revision.class);

		revisionId = "test";
		projectId = 0;
		
		Mockito.stub(tds.getAccessor(projectId)).toReturn(accessor);
		Mockito.when(accessor.getSCMAccessor()).thenReturn(scm);
		Mockito.when(scm.newRevision(revisionId)).thenReturn(newRevision);
		
		fdsMock.setTds(tds);

		pf = new ProjectFile();
		pf.setState(pfs);

		project = new StoredProject();
		project.setId(projectId);
		
		pv = new ProjectVersion();
		pv.setRevisionId(revisionId);
		pv.setProject(project);
		
		pf.setProjectVersion(pv);
	}
	
	@Test
	public void testProjectFileRevisionFirstIf() throws Exception {	
		Mockito.when(pfs.toString()).thenReturn("DELETED");
		Revision result = Whitebox.<Revision> invokeMethod(fdsMock, "projectFileRevision", pf);	
		assertTrue(result == null);
	}
	
	@Test
	public void testProjectFileRevision() throws Exception {
		Mockito.when(pfs.toString()).thenReturn("ADDED");
		Revision result = Whitebox.<Revision> invokeMethod(fdsMock, "projectFileRevision", pf);	
		assertTrue(
				result.equals(
						tds.getAccessor(projectId)
							.getSCMAccessor()
							.newRevision(pv.getRevisionId())
						)
                );
	}
	
	@Test
	public void testPfGetProjectId(){
		assertEquals(pf.getProjectId(), pf.getProjectVersion().getProject().getId());
	}
	
	@Test
	public void testPfGetRevisionId(){
		assertEquals(pf.getRevisionId(), pf.getProjectVersion().getRevisionId());
	}
	
	@Test
	public void testPfGetProjectName(){
		assertEquals(pf.getProjectName(), pf.getProjectVersion().getProject().getName());
	}
}
