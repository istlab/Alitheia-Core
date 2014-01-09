/**
 * 
 */
package eu.sqooss.test.service.webadmin;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.AbstractView;
import eu.sqooss.impl.service.webadmin.ProjectsView;
import eu.sqooss.service.admin.AdminAction;
import eu.sqooss.service.admin.AdminService;
import eu.sqooss.service.admin.actions.AddProject;
import eu.sqooss.service.db.StoredProject;

/**
 * @author elwin
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ProjectsView.class,AlitheiaCore.class,StoredProject.class})
public class ProjectsViewTest {

	private AlitheiaCore alitheiaCore;
	private AdminService adminService;
	private AdminAction adminAction;
	private VelocityContext veclocityContext;
	private StoredProject storedProject;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		//create mocks
		mockStatic(AlitheiaCore.class);
		mockStatic(StoredProject.class);
		alitheiaCore = mock(AlitheiaCore.class);
		adminService = mock(AdminService.class);
		adminAction = mock(AdminAction.class);
		veclocityContext = mock(VelocityContext.class);
		storedProject = mock(StoredProject.class);
		
		//set private static fields
		Whitebox.setInternalState(AbstractView.class, VelocityContext.class, veclocityContext);
		
		//define behavior public static method calls
		when(AlitheiaCore.getInstance()).thenReturn(alitheiaCore);
		when(StoredProject.getProjectByName(anyString())).thenReturn(storedProject);
		
		//define behavior public method calls
		when(alitheiaCore.getAdminService()).thenReturn(adminService);
		when(adminService.create(AddProject.MNEMONIC)).thenReturn(adminAction);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link eu.sqooss.impl.service.webadmin.ProjectsView#ProjectsView(org.osgi.framework.BundleContext, org.apache.velocity.VelocityContext)}.
	 * @throws Exception 
	 */
	@Test
	public void testProjectsView() throws Exception {
		StringBuilder builder = new StringBuilder();
		HttpServletRequest r = mock(HttpServletRequest.class);
		//call private method
		StoredProject proj = Whitebox.<StoredProject>invokeMethod(ProjectsView.class, "addProject",builder,r,0);
		assertThat(proj,equalTo(storedProject));
		
		//set errors to true
		when(adminAction.hasErrors()).thenReturn(true);
		//call private method
		proj = Whitebox.<StoredProject>invokeMethod(ProjectsView.class, "addProject",builder,r,0);
		assertThat(proj, nullValue());
	}

	/**
	 * Test method for {@link eu.sqooss.impl.service.webadmin.ProjectsView#render(javax.servlet.http.HttpServletRequest)}.
	 */
	@Test
	public void testRender() {
		fail("Not yet implemented"); // TODO
	}

}
