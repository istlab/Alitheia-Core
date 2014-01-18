/**
 *
 */
package eu.sqooss.test.service.webadmin;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
//import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
//import static org.powermock.api.mockito.PowerMockito.do;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.BundleContext;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.AdminServlet;
import eu.sqooss.impl.service.webadmin.ProjectsView;
import eu.sqooss.impl.service.webadmin.TranslationProxy;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.admin.AdminAction;
import eu.sqooss.service.admin.actions.UpdateProject;
import eu.sqooss.service.db.Bug;
import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.MailMessage;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.SchedulerException;

/**
 * @author Ellen
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AlitheiaCore.class,StoredProject.class,ClusterNode.class,ProjectVersion.class,MailMessage.class,Bug.class})
public class ProjectsViewTest extends AbstractViewTestBase {

	ProjectsView projectsView;
	BundleContext bundleContext;
	TranslationProxy tr;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		bundleContext = mock(BundleContext.class);
		tr = new TranslationProxy(Locale.ENGLISH);
		velocityContext = new VelocityContext();
		velocityContext.put("tr",tr);
		projectsView = new ProjectsView(bundleContext, velocityContext);
		super.setUp(projectsView);
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
	public void testAddProject() throws Exception {
		HttpServletRequest r = mock(HttpServletRequest.class);
		//call private method
		StoredProject proj = Whitebox.<StoredProject>invokeMethod(projectsView, "addProject",r);
		assertThat(proj,equalTo(storedProject));

		//set errors to true
		when(adminAction.hasErrors()).thenReturn(true);
		//call private method addProject with arguments builder,r,0
		proj = Whitebox.<StoredProject>invokeMethod(projectsView, "addProject",r);
		assertThat(proj, nullValue());
	}

	/**
	 * Test method for {@link eu.sqooss.impl.service.webadmin.ProjectsView#ProjectsView(org.osgi.framework.BundleContext, org.apache.velocity.VelocityContext)}.
	 * @throws Exception
	 */
	@Test
	public void testRemoveProject() throws Exception {
		StoredProject p = mock(StoredProject.class);
		//call private method
		StoredProject proj = Whitebox.<StoredProject>invokeMethod(projectsView, "removeProject",p);
		assertThat(proj,nullValue());

		doThrow(new SchedulerException("Test error")).when(scheduler).enqueue(any(Job.class));;
		proj = Whitebox.<StoredProject>invokeMethod(projectsView, "removeProject",p);

		//call private method addProject with arguments builder,r,0
		proj = Whitebox.<StoredProject>invokeMethod(projectsView, "removeProject",null);
		assertThat(proj, nullValue());
	}

	@Test
	public void testTriggerUpdate() throws Exception {
		StoredProject p = mock(StoredProject.class);
		//call private method
		Whitebox.<StoredProject>invokeMethod(projectsView, "triggerUpdate",p,"mnem");
		verify(adminService).create(UpdateProject.MNEMONIC);

		//set errors to true
		when(adminAction.hasErrors()).thenReturn(true);
		Whitebox.invokeMethod(projectsView, "triggerUpdate",p,"mnem");
		verify(adminService,times(2)).create(UpdateProject.MNEMONIC);
		verify(adminService,times(2)).execute(adminAction);
	}

	@Test
	public void testAllUpdate() throws Exception {
		StoredProject p = mock(StoredProject.class);
		//call private method
		Whitebox.<StoredProject>invokeMethod(projectsView, "triggerAllUpdate",p);

		verify(adminService).create(UpdateProject.MNEMONIC);

		//set errors to true
		when(adminAction.hasErrors()).thenReturn(true);
		Whitebox.invokeMethod(projectsView, "triggerAllUpdate",p);
		verify(adminService,times(2)).create(UpdateProject.MNEMONIC);
		verify(adminService,times(2)).execute(adminAction);
	}

	@Test
	public void testTriggerAllUpdateNode() throws Exception {
		StoredProject p = mock(StoredProject.class);
		//call private method
		Set<StoredProject> set = new HashSet<StoredProject>();
		set.add(p);
		when(clusterNode.getProjects()).thenReturn(set);
		Whitebox.<StoredProject>invokeMethod(projectsView, "triggerAllUpdateNode",p);
		verify(adminService).execute(any(AdminAction.class));

	}

	@Test
	public void testSyncPlugin() throws Exception {
		StoredProject p = mock(StoredProject.class);
		PluginInfo pluginInfo = mock(PluginInfo.class);
		AlitheiaPlugin alitheiaPlugin = mock(AlitheiaPlugin.class);
		when(pluginAdmin.getPluginInfo("hash")).thenReturn(pluginInfo);
		when(pluginAdmin.getPlugin(pluginInfo)).thenReturn(alitheiaPlugin);
		//call private method
		Whitebox.<StoredProject>invokeMethod(projectsView, "syncPlugin",p,"hash");

		verify(pluginAdmin).getPluginInfo("hash");
		verify(metricActivator).syncMetric(alitheiaPlugin, p);

	}

	@Test
	public void testCreateFrom() throws Exception {
		VelocityEngine ve = null;
		try {
        	ve = new VelocityEngine();
            ve.setProperty("runtime.log.logsystem.class",
                           "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
            ve.setProperty("runtime.log.logsystem.log4j.category",
                           Logger.NAME_SQOOSS_WEBADMIN);
            String resourceLoader = "classpath";
            ve.setProperty(RuntimeConstants.RESOURCE_LOADER, resourceLoader);
            ve.setProperty(resourceLoader + "." + RuntimeConstants.RESOURCE_LOADER + ".class",
            "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        }
        catch (Exception e) {
            fail("Failed with exception");
        }
		StringBuilder builder = new StringBuilder();
		StoredProject storedProject = mock(StoredProject.class);
		Whitebox.setInternalState(AdminServlet.class, VelocityEngine.class, ve);
		//call private method
		Whitebox.<StoredProject>invokeMethod(projectsView, "createForm",builder,storedProject,"action");
		String expected = "<h2>Projects Management</h2><div id=\"table\"><form id=\"projects\" name=\"projects\" method=\"post\" action=\"/projects\"><table><thead><tr class=\"head\"><td class='head' style='width: 10%;'>Project Id</td><td class='head' style='width: 35%;'>Project Name</td><td class='head' style='width: 15%;'>Last Version</td><td class='head' style='width: 15%;'>Last Email</td><td class='head' style='width: 15%;'>Last Bug</td><td class='head' style='width: 10%;'>Evaluated</td><td class='head' style='width: 10%;'>Host</td></tr></thead><tr><td colspan=\"6\" class=\"noattr\">No projects found.</td></tr><tr class=\"subhead\"><td>View</td><td colspan=\"6\"><input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Refresh\" onclick=\"javascript:window.location='/projects?projectId=0';\"></td></tr><tr class=\"subhead\"><td>Manage</td><td colspan='6'><input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Add project\" onclick=\"javascript:document.getElementById('reqAction').value='reqAddProject';document.projects.submit();\"><input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Delete project\" onclick=\"javascript:document.getElementById('reqAction').value='reqRemProject';document.projects.submit();\"></td></tr><tr class='subhead'><td>Update</td><td colspan='4'><select name=\"reqUpd\" id=\"reqUpd\" ><optgroup label=\"Import Stage\"></optgroup><optgroup label=\"Parse Stage\"></optgroup><optgroup label=\"Inference Stage\"></optgroup><optgroup label=\"Default Stage\"></optgroup></select><input type=\"button\" class=\"install\" value=\"Run Updater\" onclick=\"javascript:document.getElementById('reqAction').value='conUpdate';document.projects.submit();\"><input type=\"button\" class=\"install\" value=\"Run All Updaters\" onclick=\"javascript:document.getElementById('reqAction').value='conUpdateAll';document.projects.submit();\"></td><td colspan=\"2\" align=\"right\"><input type=\"button\" class=\"install\" value=\"Update all on ClusterNodeName\" onclick=\"javascript:document.getElementById('reqAction').value='conUpdateAllOnNode';document.projects.submit();\"></td></tr></tbody></table><input type='hidden' id='reqAction' name='reqAction' value=''><input type='hidden' id='projectId' name='projectId' value='0'><input type='hidden' id='reqParSyncPlugin' name='reqParSyncPlugin' value=''></form></div><h2>Install New Project</h2><form id=\"addprojectdir\" method=\"post\" action=\"diraddproject\"> project.properties file location <input name=\"properties\" type=\"text\" alt=\"Enter the path to the project.properties file of the project you want to install\" class=\"form\" size=\"40\"/><input type=\"submit\" value=\"Install Project\"/></form>";
		assertEquals(expected.replaceAll("\\t|\\n","").replaceAll(" +"," ").replaceAll("> <","><").trim(),builder.toString().replaceAll("\\t|\\n","").replaceAll(" +"," ").replaceAll("> <","><").trim());
		
		builder = new StringBuilder();
		String ACT_REQ_SHOW_PROJECT = Whitebox.<String>getInternalState(projectsView,"ACT_REQ_SHOW_PROJECT", ProjectsView.class);
		Whitebox.<StoredProject>invokeMethod(projectsView, "createForm",builder,storedProject,ACT_REQ_SHOW_PROJECT);
		expected = "<h2>Projects Management</h2><div id=\"table\"><form id=\"projects\" name=\"projects\" method=\"post\" action=\"/projects\"><fieldset><legend>Project information</legend><table class=\"borderless\"><tr><td class=\"borderless\" style=\"width:100px;\"><b>Project name</b></td><td class=\"borderless\"></td></tr><tr><td class=\"borderless\" style=\"width:100px;\"><b>Homepage</b></td><td class=\"borderless\"></td></tr><tr><td class=\"borderless\" style=\"width:100px;\"><b>Contact e-mail</b></td><td class=\"borderless\"></td></tr><tr><td class=\"borderless\" style=\"width:100px;\"><b>Bug database</b></td><td class=\"borderless\"></td></tr><tr><td class=\"borderless\" style=\"width:100px;\"><b>Mailing list</b></td><td class=\"borderless\"></td></tr><tr><td class=\"borderless\" style=\"width:100px;\"><b>Source code</b></td><td class=\"borderless\"></td></tr><tr><td colspan=\"2\" class=\"borderless\"><input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Back\" onclick=\"javascript:document.projects.submit();\"></td></tr></table></fieldset><input type='hidden' id='reqAction' name='reqAction' value=''><input type='hidden' id='projectId' name='projectId' value='0'><input type='hidden' id='reqParSyncPlugin' name='reqParSyncPlugin' value=''></form></div><h2>Install New Project</h2><form id=\"addprojectdir\" method=\"post\" action=\"diraddproject\"> project.properties file location <input name=\"properties\" type=\"text\" alt=\"Enter the path to the project.properties file of the project you want to install\" class=\"form\" size=\"40\"/><input type=\"submit\" value=\"Install Project\"/></form>";
		assertEquals(expected.replaceAll("\\t|\\n","").replaceAll(" +"," ").replaceAll("> <","><").trim(),builder.toString().replaceAll("\\t|\\n","").replaceAll(" +"," ").replaceAll("> <","><").trim());
		
		builder = new StringBuilder();
		String ACT_REQ_ADD_PROJECT = Whitebox.<String>getInternalState(projectsView,"ACT_REQ_ADD_PROJECT", ProjectsView.class);
		Whitebox.<StoredProject>invokeMethod(projectsView, "createForm",builder,storedProject,ACT_REQ_ADD_PROJECT);
		expected = "<h2>Projects Management</h2><div id=\"table\"><form id=\"projects\" name=\"projects\" method=\"post\" action=\"/projects\"><table class=\"borderless\" width='100%'><tr><td class=\"borderless\" style=\"width:100px;\"><b>Project name</b></td><td class=\"borderless\"><input type=\"text\" class=\"form\" id=\"projectName\" name=\"projectName\" value=\"\" size=\"60\"></td></tr><tr><td class=\"borderless\" style=\"width:100px;\"><b>Homepage</b></td><td class=\"borderless\"><input type=\"text\" class=\"form\" id=\"projectHomepage\" name=\"projectHomepage\" value=\"\" size=\"60\"></td></tr><tr><td class=\"borderless\" style=\"width:100px;\"><b>Contact e-mail</b></td><td class=\"borderless\"><input type=\"text\" class=\"form\" id=\"projectContact\" name=\"projectContact\" value=\"\" size=\"60\"></td></tr><tr><td class=\"borderless\" style=\"width:100px;\"><b>Bug database</b></td><td class=\"borderless\"><input type=\"text\" class=\"form\" id=\"projectBL\" name=\"projectBL\" value=\"\" size=\"60\"></td></tr><tr><td class=\"borderless\" style=\"width:100px;\"><b>Mailing list</b></td><td class=\"borderless\"><input type=\"text\" class=\"form\" id=\"projectML\" name=\"projectML\" value=\"\" size=\"60\"></td></tr><tr><td class=\"borderless\" style=\"width:100px;\"><b>Source code</b></td><td class=\"borderless\"><input type=\"text\" class=\"form\" id=\"projectSCM\" name=\"projectSCM\" value=\"\" size=\"60\"></td></tr><tr><td colspan=\"2\" class=\"borderless\"><input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"project_add\" onclick=\"javascript:document.getElementById('reqAction').value='conAddProject';document.projects.submit();\"><input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"cancel\" onclick=\"javascript:document.projects.submit();\"></td></tr></table><input type='hidden' id='reqAction' name='reqAction' value=''><input type='hidden' id='projectId' name='projectId' value='0'><input type='hidden' id='reqParSyncPlugin' name='reqParSyncPlugin' value=''></form></div><h2>Install New Project</h2><form id=\"addprojectdir\" method=\"post\" action=\"diraddproject\"> project.properties file location <input name=\"properties\" type=\"text\" alt=\"Enter the path to the project.properties file of the project you want to install\" class=\"form\" size=\"40\"/><input type=\"submit\" value=\"Install Project\"/></form>";
		assertEquals(expected.replaceAll("\\t|\\n","").replaceAll(" +"," ").replaceAll("> <","><").trim(),builder.toString().replaceAll("\\t|\\n","").replaceAll(" +"," ").replaceAll("> <","><").trim());
		builder = new StringBuilder();

		String ACT_REQ_REM_PROJECT = Whitebox.<String>getInternalState(projectsView,"ACT_REQ_REM_PROJECT", ProjectsView.class);
		Whitebox.<StoredProject>invokeMethod(projectsView, "createForm",builder,storedProject,ACT_REQ_REM_PROJECT);
		expected = "<h2>Projects Management</h2><div id=\"table\"><form id=\"projects\" name=\"projects\" method=\"post\" action=\"/projects\"><fieldset><legend>Delete project : null</legend><table class=\"borderless\"><tr><td class=\"borderless\"><b>Are you sure that you want to completely remove this project?</b></td></tr><tr><td class=\"borderless\"><input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Yes\" onclick=\"javascript:document.getElementById('reqAction').value='conRemProject';document.projects.submit();\"><input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Cancel\" onclick=\"javascript:document.projects.submit();\"></td></tr></table></fieldset><input type='hidden' id='reqAction' name='reqAction' value=''><input type='hidden' id='projectId' name='projectId' value='0'><input type='hidden' id='reqParSyncPlugin' name='reqParSyncPlugin' value=''></form></div><h2>Install New Project</h2><form id=\"addprojectdir\" method=\"post\" action=\"diraddproject\"> project.properties file location <input name=\"properties\" type=\"text\" alt=\"Enter the path to the project.properties file of the project you want to install\" class=\"form\" size=\"40\"/><input type=\"submit\" value=\"Install Project\"/></form>";
		assertEquals(expected.replaceAll("\\t|\\n","").replaceAll(" +"," ").replaceAll("> <","><").trim(),builder.toString().replaceAll("\\t|\\n","").replaceAll(" +"," ").replaceAll("> <","><").trim());
		builder = new StringBuilder();

		Set<StoredProject> storedProjects = new HashSet<StoredProject>();
		storedProjects.add(storedProject);
		when(clusterNode.getProjects()).thenReturn(storedProjects);
		ProjectVersion projectVersion = mock(ProjectVersion.class);
		when(ProjectVersion.getLastProjectVersion(storedProject)).thenReturn(projectVersion);
		Whitebox.<StoredProject>invokeMethod(projectsView, "createForm",builder,storedProject,"action");
		expected = "<h2>Projects Management</h2><div id=\"table\"><form id=\"projects\" name=\"projects\" method=\"post\" action=\"/projects\"><table><thead><tr class=\"head\"><td class='head' style='width: 10%;'>Project Id</td><td class='head' style='width: 35%;'>Project Name</td><td class='head' style='width: 15%;'>Last Version</td><td class='head' style='width: 15%;'>Last Email</td><td class='head' style='width: 15%;'>Last Bug</td><td class='head' style='width: 10%;'>Evaluated</td><td class='head' style='width: 10%;'>Host</td></tr></thead><tbody><tr class=\"selected\" onclick=\"javascript:document.getElementById('projectId').value='';document.projects.submit();\"><td class=\"trans\">0</td><td class=\"trans\"><input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Info\" onclick=\"javascript:document.getElementById('reqAction').value='conShowProject';document.projects.submit();\"> &nbsp;null </td><td class=\"trans\"> 0(null) </td><td class=\"trans\"> null </td><td class=\"trans\"> n/a </td><td class=\"trans\"> Yes </td><td class=\"trans\">(local)</td></tr><tr class=\"subhead\"><td>View</td><td colspan=\"6\"><input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Refresh\" onclick=\"javascript:window.location='/projects?projectId=0';\"></td></tr><tr class=\"subhead\"><td>Manage</td><td colspan='6'><input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Add project\" onclick=\"javascript:document.getElementById('reqAction').value='reqAddProject';document.projects.submit();\"><input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Delete project\" onclick=\"javascript:document.getElementById('reqAction').value='reqRemProject';document.projects.submit();\"></td></tr><tr class='subhead'><td>Update</td><td colspan='4'><select name=\"reqUpd\" id=\"reqUpd\" ><optgroup label=\"Import Stage\"></optgroup><optgroup label=\"Parse Stage\"></optgroup><optgroup label=\"Inference Stage\"></optgroup><optgroup label=\"Default Stage\"></optgroup></select><input type=\"button\" class=\"install\" value=\"Run Updater\" onclick=\"javascript:document.getElementById('reqAction').value='conUpdate';document.projects.submit();\"><input type=\"button\" class=\"install\" value=\"Run All Updaters\" onclick=\"javascript:document.getElementById('reqAction').value='conUpdateAll';document.projects.submit();\"></td><td colspan=\"2\" align=\"right\"><input type=\"button\" class=\"install\" value=\"Update all on ClusterNodeName\" onclick=\"javascript:document.getElementById('reqAction').value='conUpdateAllOnNode';document.projects.submit();\"></td></tr></tbody></table><input type='hidden' id='reqAction' name='reqAction' value=''><input type='hidden' id='projectId' name='projectId' value='0'><input type='hidden' id='reqParSyncPlugin' name='reqParSyncPlugin' value=''></form></div><h2>Install New Project</h2><form id=\"addprojectdir\" method=\"post\" action=\"diraddproject\"> project.properties file location <input name=\"properties\" type=\"text\" alt=\"Enter the path to the project.properties file of the project you want to install\" class=\"form\" size=\"40\"/><input type=\"submit\" value=\"Install Project\"/></form>";
		assertEquals(expected.replaceAll("\\t|\\n","").replaceAll(" +"," ").replaceAll("> <","><").trim(),builder.toString().replaceAll("\\t|\\n","").replaceAll(" +"," ").replaceAll("> <","><").trim());
		builder = new StringBuilder();

		when(storedProject.isEvaluated()).thenReturn(true);
		when(storedProject.getClusternode()).thenReturn(clusterNode);
		Whitebox.<StoredProject>invokeMethod(projectsView, "createForm",builder,storedProject,"action");
		expected = "<h2>Projects Management</h2><div id=\"table\"><form id=\"projects\" name=\"projects\" method=\"post\" action=\"/projects\"><table><thead><tr class=\"head\"><td class='head' style='width: 10%;'>Project Id</td><td class='head' style='width: 35%;'>Project Name</td><td class='head' style='width: 15%;'>Last Version</td><td class='head' style='width: 15%;'>Last Email</td><td class='head' style='width: 15%;'>Last Bug</td><td class='head' style='width: 10%;'>Evaluated</td><td class='head' style='width: 10%;'>Host</td></tr></thead><tbody><tr class=\"selected\" onclick=\"javascript:document.getElementById('projectId').value='';document.projects.submit();\"><td class=\"trans\">0</td><td class=\"trans\"><input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Info\" onclick=\"javascript:document.getElementById('reqAction').value='conShowProject';document.projects.submit();\"> &nbsp;null </td><td class=\"trans\"> 0(null) </td><td class=\"trans\"> null </td><td class=\"trans\"> n/a </td><td class=\"trans\"> Yes </td><td class=\"trans\">null</td></tr><tr class=\"subhead\"><td>View</td><td colspan=\"6\"><input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Refresh\" onclick=\"javascript:window.location='/projects?projectId=0';\"></td></tr><tr class=\"subhead\"><td>Manage</td><td colspan='6'><input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Add project\" onclick=\"javascript:document.getElementById('reqAction').value='reqAddProject';document.projects.submit();\"><input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Delete project\" onclick=\"javascript:document.getElementById('reqAction').value='reqRemProject';document.projects.submit();\"></td></tr><tr class='subhead'><td>Update</td><td colspan='4'><select name=\"reqUpd\" id=\"reqUpd\" ><optgroup label=\"Import Stage\"></optgroup><optgroup label=\"Parse Stage\"></optgroup><optgroup label=\"Inference Stage\"></optgroup><optgroup label=\"Default Stage\"></optgroup></select><input type=\"button\" class=\"install\" value=\"Run Updater\" onclick=\"javascript:document.getElementById('reqAction').value='conUpdate';document.projects.submit();\"><input type=\"button\" class=\"install\" value=\"Run All Updaters\" onclick=\"javascript:document.getElementById('reqAction').value='conUpdateAll';document.projects.submit();\"></td><td colspan=\"2\" align=\"right\"><input type=\"button\" class=\"install\" value=\"Update all on ClusterNodeName\" onclick=\"javascript:document.getElementById('reqAction').value='conUpdateAllOnNode';document.projects.submit();\"></td></tr></tbody></table><input type='hidden' id='reqAction' name='reqAction' value=''><input type='hidden' id='projectId' name='projectId' value='0'><input type='hidden' id='reqParSyncPlugin' name='reqParSyncPlugin' value=''></form></div><h2>Install New Project</h2><form id=\"addprojectdir\" method=\"post\" action=\"diraddproject\"> project.properties file location <input name=\"properties\" type=\"text\" alt=\"Enter the path to the project.properties file of the project you want to install\" class=\"form\" size=\"40\"/><input type=\"submit\" value=\"Install Project\"/></form>";
		assertEquals(expected.replaceAll("\\t|\\n","").replaceAll(" +"," ").replaceAll("> <","><").trim(),builder.toString().replaceAll("\\t|\\n","").replaceAll(" +"," ").replaceAll("> <","><").trim());
		builder = new StringBuilder();

	}

	/**
	 * Test method for {@link eu.sqooss.impl.service.webadmin.ProjectsView#setupVelocityContext(javax.servlet.http.HttpServletRequest)}.
	 */
	@Test
	public void testRender() {
		VelocityEngine ve = null;
		try {
        	ve = new VelocityEngine();
            ve.setProperty("runtime.log.logsystem.class",
                           "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
            ve.setProperty("runtime.log.logsystem.log4j.category",
                           Logger.NAME_SQOOSS_WEBADMIN);
            String resourceLoader = "classpath";
            ve.setProperty(RuntimeConstants.RESOURCE_LOADER, resourceLoader);
            ve.setProperty(resourceLoader + "." + RuntimeConstants.RESOURCE_LOADER + ".class",
            "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        }
        catch (Exception e) {
            fail("Failed with exception");
        }
		Whitebox.setInternalState(AdminServlet.class, VelocityEngine.class, ve);
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getLocale()).thenReturn(Locale.ENGLISH);


		String result = projectsView.setupVelocityContext(req);

		String expected = "<h2>Projects Management</h2><div id=\"table\"><form id=\"projects\" name=\"projects\" method=\"post\" action=\"/projects\"><table><thead><tr class=\"head\"><td class='head' style='width: 10%;'>Project Id</td><td class='head' style='width: 35%;'>Project Name</td><td class='head' style='width: 15%;'>Last Version</td><td class='head' style='width: 15%;'>Last Email</td><td class='head' style='width: 15%;'>Last Bug</td><td class='head' style='width: 10%;'>Evaluated</td><td class='head' style='width: 10%;'>Host</td></tr></thead><tr><td colspan=\"6\" class=\"noattr\">No projects found.</td></tr><tr class=\"subhead\"><td>View</td><td colspan=\"6\"><input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Refresh\" onclick=\"javascript:window.location='/projects';\"></td></tr><tr class=\"subhead\"><td>Manage</td><td colspan='6'><input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Add project\" onclick=\"javascript:document.getElementById('reqAction').value='reqAddProject';document.projects.submit();\"><input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Delete project\" onclick=\"javascript:document.getElementById('reqAction').value='reqRemProject';document.projects.submit();\" disabled></td></tr><tr class='subhead'><td>Update</td><td colspan='4'><input type=\"button\" class=\"install\" value=\"Run Updater\" onclick=\"javascript:document.getElementById('reqAction').value='conUpdate';document.projects.submit();\" disabled><input type=\"button\" class=\"install\" value=\"Run All Updaters\" onclick=\"javascript:document.getElementById('reqAction').value='conUpdateAll';document.projects.submit();\" disabled></td><td colspan=\"2\" align=\"right\"><input type=\"button\" class=\"install\" value=\"Update all on ClusterNodeName\" onclick=\"javascript:document.getElementById('reqAction').value='conUpdateAllOnNode';document.projects.submit();\"></td></tr></tbody></table><input type='hidden' id='reqAction' name='reqAction' value=''><input type='hidden' id='projectId' name='projectId' value=''><input type='hidden' id='reqParSyncPlugin' name='reqParSyncPlugin' value=''></form></div><h2>Install New Project</h2><form id=\"addprojectdir\" method=\"post\" action=\"diraddproject\"> project.properties file location <input name=\"properties\" type=\"text\" alt=\"Enter the path to the project.properties file of the project you want to install\" class=\"form\" size=\"40\"/><input type=\"submit\" value=\"Install Project\"/></form>";
		assertEquals(expected.replaceAll("\\t|\\n","").replaceAll(" +"," ").replaceAll("> <","><").trim(),result.replaceAll("\\t|\\n","").replaceAll(" +"," ").replaceAll("> <","><").trim());
	}

}
