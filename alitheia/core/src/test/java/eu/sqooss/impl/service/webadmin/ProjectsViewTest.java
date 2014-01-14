package eu.sqooss.impl.service.webadmin;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.xmlmatchers.transform.XmlConverters.the;
import static org.xmlmatchers.xpath.HasXPath.hasXPath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.velocity.VelocityContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.BundleContext;
import org.xml.sax.SAXException;

import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.admin.AdminAction;
import eu.sqooss.service.admin.AdminService;
import eu.sqooss.service.admin.actions.AddProject;
import eu.sqooss.service.admin.actions.UpdateProject;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.service.updater.Updater;
import eu.sqooss.service.updater.UpdaterService.UpdaterStage;

@RunWith(MockitoJUnitRunner.class)
public class ProjectsViewTest {
	private static final String INPUT_REGEX = "<input([\\(\\) a-zA-Z=\\-_\\\"\\\':;0-9\\.\\?\\/\\\\]*[\\(\\) a-zA-Z=\\-_\\\"\\\':;0-9\\.\\?\\\\]+)>";
	private static final String CLUSTER_NODE_NAME = "CLUSTER_NODE1";
	private ProjectsView projectsView;
	private Map<UpdaterStage, Set<Updater>> updaters;
	
	@Mock public AdminService adminService;
	@Mock public AlitheiaPlugin somePlugin;
	@Mock public Logger logger;
	@Mock public MetricActivator metricActivator;
	@Mock public PluginAdmin pluginAdmin;
	@Mock public Scheduler scheduler;
	@Mock public VelocityContext velocityContext;
	public StoredProject project;

	@Before
	public void setUp() {
		projectsView = new TestableProjectsView(null, null);
		updaters = new HashMap<UpdaterStage, Set<Updater>>();
		project = new StoredProject();
		project.setId(1234l);
		project.setName("project1234");
	}
	
	@Test
	public void shouldExecuteAddActionAndPutResults() {
		AdminAction action = mock(AdminAction.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(ProjectsView.REQ_PAR_PRJ_CODE)).thenReturn("scm");
		when(request.getParameter(ProjectsView.REQ_PAR_PRJ_NAME)).thenReturn("project1234");
		when(request.getParameter(ProjectsView.REQ_PAR_PRJ_BUG)).thenReturn("bug");
		when(request.getParameter(ProjectsView.REQ_PAR_PRJ_MAIL)).thenReturn("test@test.tst");
		when(request.getParameter(ProjectsView.REQ_PAR_PRJ_WEB)).thenReturn("web.com");
		when(adminService.create(AddProject.MNEMONIC)).thenReturn(action);
		Map<String, Object> results = new HashMap<String, Object>();
		when(action.results()).thenReturn(results);

		StoredProject added = projectsView.addProject(null, request, 0);

		verify(adminService).create(AddProject.MNEMONIC);
		verify(action).addArg("scm", "scm");
		verify(action).addArg("name", "project1234");
		verify(action).addArg("bts", "bug");
		verify(action).addArg("mail", "test@test.tst");
		verify(action).addArg("web", "web.com");
		verify(adminService).execute(action);
		verify(velocityContext).put("RESULTS", results);
		
		assertEquals(project, added);
	}
	
	@Test
	public void shouldExecuteAddActionAndPutErrors() {
		AdminAction action = mock(AdminAction.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(ProjectsView.REQ_PAR_PRJ_CODE)).thenReturn("scm");
		when(request.getParameter(ProjectsView.REQ_PAR_PRJ_NAME)).thenReturn("project1234");
		when(request.getParameter(ProjectsView.REQ_PAR_PRJ_BUG)).thenReturn("bug");
		when(request.getParameter(ProjectsView.REQ_PAR_PRJ_MAIL)).thenReturn("test@test.tst");
		when(request.getParameter(ProjectsView.REQ_PAR_PRJ_WEB)).thenReturn("web.com");
		when(adminService.create(AddProject.MNEMONIC)).thenReturn(action);
		Map<String, Object> errors = new HashMap<String, Object>();
		when(action.errors()).thenReturn(errors);
		when(action.hasErrors()).thenReturn(true);

		StoredProject added = projectsView.addProject(null, request, 0);

		verify(adminService).create(AddProject.MNEMONIC);
		verify(action).addArg("scm", "scm");
		verify(action).addArg("name", "project1234");
		verify(action).addArg("bts", "bug");
		verify(action).addArg("mail", "test@test.tst");
		verify(action).addArg("web", "web.com");
		verify(adminService).execute(action);
		verify(velocityContext).put("RESULTS", errors);
		
		assertNull(added);
	}
	
	@Test
	public void shouldAppendErrorIfNoProjectGiven() {
		StringBuilder error = new StringBuilder();
		
		projectsView.removeProject(error, null, 0);
		
		assertThat(error.toString(), not(equalTo("")));
	}
	
	@Test
	public void shouldEnqueueProjectDeleteJobIfProjectGiven() throws Exception {
		StringBuilder error = new StringBuilder();

		projectsView.removeProject(error, project, 0);
		
		verify(scheduler).enqueue(any(ProjectDeleteJob.class));
		assertThat(error.toString(), equalTo(""));
	}
	
	@Test
	public void shouldPrintErrorIfEnqueueProjectDeleteJobFails() throws Exception {
		StringBuilder error = new StringBuilder();

		doThrow(new SchedulerException("Big failure")).when(scheduler).enqueue(any(ProjectDeleteJob.class));
		
		projectsView.removeProject(error, project, 0);
		
		verify(scheduler).enqueue(any(ProjectDeleteJob.class));
		assertThat(error.toString(), not(equalTo("")));
	}
	
	@Test
	public void shouldExecuteUpdateActionAndPutResults() {
		AdminAction action = mock(AdminAction.class);
		Map<String, Object> results = new HashMap<String, Object>();
		
		when(adminService.create(UpdateProject.MNEMONIC)).thenReturn(action);
		when(action.results()).thenReturn(results);
		
		projectsView.triggerUpdate(null, project, 0, "updater");
		
		verify(adminService).create(UpdateProject.MNEMONIC);
		verify(action).addArg("project", 1234l);
		verify(action).addArg("updater", "updater");
		verify(adminService).execute(action);
		
		verify(velocityContext).put("RESULTS", results);
	}
	
	@Test
	public void shouldExecuteUpdateActionAndPutErrors() {
		AdminAction action = mock(AdminAction.class);
		Map<String, Object> errors = new HashMap<String, Object>();
		
		when(adminService.create(anyString())).thenReturn(action);
		when(action.errors()).thenReturn(errors);
		when(action.hasErrors()).thenReturn(true);
		
		projectsView.triggerUpdate(null, project, 0, "updater");
		
		verify(adminService).create(UpdateProject.MNEMONIC);
		verify(action).addArg("project", 1234l);
		verify(action).addArg("updater", "updater");
		verify(adminService).execute(action);
		
		verify(velocityContext).put("RESULTS", errors);
	}
	
	@Test
	public void syncNothingIfNoPluginSelected() {
		projectsView.syncPlugin(null, null, null);
		
		verifyZeroInteractions(pluginAdmin);
	}
	
	@Test
	public void syncNothingIfNoProjectSelected() {
		projectsView.syncPlugin(null, null, "selected_plugin");
		
		verifyZeroInteractions(pluginAdmin);
	}
	
	@Test
	public void syncNothingIfNoPluginInfo() {
		projectsView.syncPlugin(null, project, "selected_plugin");
		
		verify(pluginAdmin).getPluginInfo("selected_plugin");
		verifyNoMoreInteractions(pluginAdmin);
	}
	
	@Test
	public void syncNothingIfNoPluginObject() {
		PluginInfo info = new PluginInfo();
		when(pluginAdmin.getPluginInfo("selected_plugin")).thenReturn(info);
		
		projectsView.syncPlugin(null, project, "selected_plugin");
		
		verify(pluginAdmin).getPluginInfo("selected_plugin");
		verify(pluginAdmin).getPlugin(info);
		verifyNoMoreInteractions(pluginAdmin);
	}
	
	@Test
	public void syncMetricIfPluginObjectFound() {
		PluginInfo info = new PluginInfo();
		when(pluginAdmin.getPluginInfo("selected_plugin")).thenReturn(info);
		when(pluginAdmin.getPlugin(info)).thenReturn(somePlugin);
		
		projectsView.syncPlugin(null, project, "selected_plugin");
		
		verify(pluginAdmin).getPluginInfo("selected_plugin");
		verify(pluginAdmin).getPlugin(info);
		verifyNoMoreInteractions(pluginAdmin);
		
		verify(metricActivator).syncMetric(somePlugin, project);
	}
	
	@Test
	public void shouldAddEmptyHiddenFieldsWithoutProject() {
		StringBuilder builder = new StringBuilder();
		
		projectsView.addHiddenFields(null, builder, 0);
		
		String html = sanitizeHTML(builder.toString());
		
		assertThat(the(html), hasXPath("/root/input[@type='hidden' and @id='" + ProjectsView.REQ_PAR_ACTION + "' and @name='" + ProjectsView.REQ_PAR_ACTION + "' and @value='']"));
		assertThat(the(html), hasXPath("/root/input[@type='hidden' and @id='" + ProjectsView.REQ_PAR_PROJECT_ID + "' and @name='" + ProjectsView.REQ_PAR_PROJECT_ID + "' and @value='']"));
		assertThat(the(html), hasXPath("/root/input[@type='hidden' and @id='" + ProjectsView.REQ_PAR_SYNC_PLUGIN + "' and @name='" + ProjectsView.REQ_PAR_SYNC_PLUGIN + "' and @value='']"));
	}
	
	@Test
	public void shouldAddProjectIdWithProject() {
		StringBuilder builder = new StringBuilder();
		projectsView.addHiddenFields(project , builder, 0);
		
		String html = sanitizeHTML(builder.toString());
		
		assertThat(the(html), hasXPath("/root/input[@type='hidden' and @id='" + ProjectsView.REQ_PAR_PROJECT_ID + "' and @name='" + ProjectsView.REQ_PAR_PROJECT_ID + "']/@value", equalTo("1234")));
	}

	protected String sanitizeHTML(String string) {
		String html = "<root>" + string + "</root>";		
		html = html.replaceAll(INPUT_REGEX, "<input$1/>");
		html = html.replaceAll("disabled(\\s*[^=])", "disabled='true'$1");
		return html;
	}
	
	@Test
	public void shouldShowBasicToolbarIfNoProjectSelected() {
		StringBuilder builder = new StringBuilder();
		
		projectsView.addToolBar(null, builder, 0);
		
		String html = sanitizeHTML(builder.toString());
		
		// the toolbar should have three rows.
		assertThat(the(html), hasXPath("count(/root/tr)", equalTo("3")));
		// the first row should have a button that refreshes the projects page
		String onclick1 = "javascript:window.location='/projects';";
		assertThat(the(html), hasXPath("/root/tr[1]/td[2]/input/@onclick", equalTo(onclick1)));
		// the second row has a button that does the add project action and a button that does a remove project action
		String onclick2 = "javascript:document.getElementById('" + ProjectsView.REQ_PAR_ACTION +"').value='" + ProjectsView.ACT_REQ_ADD_PROJECT + "';" + ProjectsView.SUBMIT;
		assertThat(the(html), hasXPath("/root/tr[2]/td[2]/input[1]/@onclick", equalTo(onclick2)));
		// this action must be disabled
		String onclick3 = "javascript:document.getElementById('" + ProjectsView.REQ_PAR_ACTION +"').value='" + ProjectsView.ACT_REQ_REM_PROJECT + "';" + ProjectsView.SUBMIT;
		assertThat(the(html), hasXPath("/root/tr[2]/td[2]/input[2]/@onclick", equalTo(onclick3)));
		assertThat(the(html), hasXPath("/root/tr[2]/td[2]/input[2]/@disabled"));
		// the third row has a button that runs the updater and one that runs all updaters,
		// both must be disabled
		String onclick4 = "javascript:document.getElementById('" + ProjectsView.REQ_PAR_ACTION +"').value='" + ProjectsView.ACT_CON_UPD + "';" + ProjectsView.SUBMIT;
		assertThat(the(html), hasXPath("/root/tr[3]/td[2]/input[1]/@onclick", equalTo(onclick4)));
		assertThat(the(html), hasXPath("/root/tr[3]/td[2]/input[1]/@disabled"));		
		String onclick5 = "javascript:document.getElementById('" + ProjectsView.REQ_PAR_ACTION +"').value='" + ProjectsView.ACT_CON_UPD_ALL + "';" + ProjectsView.SUBMIT;
		assertThat(the(html), hasXPath("/root/tr[3]/td[2]/input[2]/@onclick", equalTo(onclick5)));
		assertThat(the(html), hasXPath("/root/tr[3]/td[2]/input[2]/@disabled"));
		// the next column has a button to update all on a node
		String onclick6 = "javascript:document.getElementById('" + ProjectsView.REQ_PAR_ACTION +"').value='" + ProjectsView.ACT_CON_UPD_ALL_NODE + "';" + ProjectsView.SUBMIT;
		assertThat(the(html), hasXPath("/root/tr[3]/td[3]/input[1]/@onclick", equalTo(onclick6)));
	}
	
	@Test
	public void shouldShowAdvancedToolbarIfProjectSelected() {
		StringBuilder builder = new StringBuilder();
		
		Updater iu1 = createUpdater("iu1", "import_updater_1", UpdaterStage.IMPORT);
		Updater iu2 = createUpdater("iu2", "import_updater_2", UpdaterStage.IMPORT);
		updaters.put(UpdaterStage.IMPORT, new HashSet<Updater>(Arrays.asList(iu1, iu2)));
		
		Updater pu1 = createUpdater("pu1", "parse_updater_1", UpdaterStage.PARSE);
		Updater pu2 = createUpdater("pu2", "parse_updater_2", UpdaterStage.PARSE);
		updaters.put(UpdaterStage.PARSE, new HashSet<Updater>(Arrays.asList(pu1, pu2)));
		
		Updater inu1 = createUpdater("inu1", "inference_updater_1", UpdaterStage.INFERENCE);
		Updater inu2 = createUpdater("inu2", "inference_updater_2", UpdaterStage.INFERENCE);
		updaters.put(UpdaterStage.INFERENCE, new HashSet<Updater>(Arrays.asList(inu1, inu2)));
		
		Updater defu1 = createUpdater("defu1", "default_updater_1", UpdaterStage.DEFAULT);
		Updater defu2 = createUpdater("defu2", "default_updater_2", UpdaterStage.DEFAULT);
		updaters.put(UpdaterStage.DEFAULT, new HashSet<Updater>(Arrays.asList(defu1, defu2)));
		
		projectsView.addToolBar(project, builder, 0);

		// sanitize html input
		String html = sanitizeHTML(builder.toString());

		// the first row should have a button that goes to the project page
		String onclick1 = "javascript:window.location='/projects?" + ProjectsView.REQ_PAR_PROJECT_ID + "=" + project.getId() + "';";
		assertThat(the(html), hasXPath("/root/tr[1]/td[2]/input/@onclick", equalTo(onclick1)));

		// the remove action is not disabled this time
		assertThat(the(html), hasXPath("//input[contains(@onclick, '" + ProjectsView.ACT_REQ_REM_PROJECT + "') and not(@disabled)]"));
		// test the select:
		assertThat(the(html), hasXPath("/root/tr/td/select"));
		assertThat(the(html), hasXPath("/root/tr/td/select[@id='" + ProjectsView.REQ_PAR_UPD + "']"));
		assertThat(the(html), hasXPath("count(/root/tr/td/select/optgroup)", equalTo("4")));
		
		// test each pair for each group
		assertThat(the(html), hasXPath("/root/tr/td/select/optgroup[@label='Import Stage']/option[@value='iu1']/text()", equalTo("import_updater_1")));
		assertThat(the(html), hasXPath("/root/tr/td/select/optgroup[@label='Import Stage']/option[@value='iu2']/text()", equalTo("import_updater_2")));
		assertThat(the(html), hasXPath("/root/tr/td/select/optgroup[@label='Parse Stage']/option[@value='pu1']/text()", equalTo("parse_updater_1")));
		assertThat(the(html), hasXPath("/root/tr/td/select/optgroup[@label='Parse Stage']/option[@value='pu2']/text()", equalTo("parse_updater_2")));
		assertThat(the(html), hasXPath("/root/tr/td/select/optgroup[@label='Inference Stage']/option[@value='inu1']/text()", equalTo("inference_updater_1")));
		assertThat(the(html), hasXPath("/root/tr/td/select/optgroup[@label='Inference Stage']/option[@value='inu2']/text()", equalTo("inference_updater_2")));
		assertThat(the(html), hasXPath("/root/tr/td/select/optgroup[@label='Default Stage']/option[@value='defu1']/text()", equalTo("default_updater_1")));
		assertThat(the(html), hasXPath("/root/tr/td/select/optgroup[@label='Default Stage']/option[@value='defu2']/text()", equalTo("default_updater_2")));
		
		// the update buttons are not disabled this time
		assertThat(the(html), hasXPath("//input[contains(@onclick, '" + ProjectsView.ACT_CON_UPD + "') and not(@disabled)]"));
		assertThat(the(html), hasXPath("//input[contains(@onclick, '" + ProjectsView.ACT_CON_UPD_ALL + "') and not(@disabled)]"));
	}

	protected Updater createUpdater(String mnemonic, String description, UpdaterStage stage) {
		Updater updater = mock(Updater.class);
		when(updater.mnem()).thenReturn(mnemonic);
		when(updater.descr()).thenReturn(description);
		when(updater.stage()).thenReturn(stage);
		return updater;
	}
	
	@Test
	public void shouldShowNoLastAppliedVersionForEmptyList() {
		StringBuilder html = new StringBuilder();

		projectsView.showLastAppliedVersion(null, new ArrayList<PluginInfo>(),
				html);

		assertEquals("", html.toString());
	}

	@Test
	public void shouldNotShowNotInstalledPlugins() {
		StringBuilder html = new StringBuilder();
		PluginInfo pi = new PluginInfo();
		pi.installed = false;
		Collection<PluginInfo> metrics = Arrays.asList(pi);

		projectsView.showLastAppliedVersion(null, metrics, html);

		assertEquals("", html.toString());
	}
	
	@Test
	public void shouldShowInstalledPlugins()
			throws ParserConfigurationException, SAXException, IOException,
			XPathExpressionException {
		// Arrange
		StringBuilder builder = new StringBuilder();
		String hash1 = "p1hash";
		String name1 = "p1 plugin";
		String hash2 = "p2hash";
		String name2 = "p2 plugin";

		PluginInfo p1 = new PluginInfo();
		p1.installed = true;
		p1.setHashcode(hash1);
		p1.setPluginName(name1);
		PluginInfo p2 = new PluginInfo();
		p2.installed = true;
		p2.setHashcode(hash2);
		p2.setPluginName(name2);
		Collection<PluginInfo> metrics = Arrays.asList(p1, p2);

		// Act
		projectsView.showLastAppliedVersion(null, metrics, builder);

		// Assert
		String html = "<root>" + builder.toString().replace("&nbsp;", " ") + "</root>";
		
		String onclick1 = "javascript:document.getElementById('" + ProjectsView.REQ_PAR_SYNC_PLUGIN + "').value='" + hash1 +"';" + ProjectsView.SUBMIT;
		String onclick2 = "javascript:document.getElementById('" + ProjectsView.REQ_PAR_SYNC_PLUGIN + "').value='" + hash2 +"';" + ProjectsView.SUBMIT;

		// there must be two rows.
		assertThat(the(html), hasXPath("count(/root/tr)", equalTo("2")));
		// the first row has one column.
		
		assertThat(the(html), hasXPath("count(/root/tr[1]/td)", equalTo("1")));
		// that column has an input of type button that does 'onclick1' on click.
		assertThat(the(html), hasXPath("/root/tr[1]/td/input[@type='button']/@onclick", equalTo(onclick1)));
		// that column's text should contain the plugin name
		assertThat(the(html), hasXPath("string-join(/root/tr[1]/td/text(), '')", containsString(name1)));
		
		// the second row has one column.
		assertThat(the(html), hasXPath("count(/root/tr[2]/td)", equalTo("1")));
		// that column has an input of type button that does 'onclick2' on click.
		assertThat(the(html), hasXPath("/root/tr[2]/td/input[@type='button']/@onclick", equalTo(onclick2)));
		// that column's text should contain the plugin name
		assertThat(the(html), hasXPath("string-join(/root/tr[2]/td/text(), '')", containsString(name2)));
	}

	@Test
	public void shouldCreateTableHeaderRow() {
		StringBuilder builder = new StringBuilder();

		projectsView.addHeaderRow(builder, 0);

		String html = builder.toString() + "</table>";
		
		// the table should contain a thead with a tr with 7 td's
		assertThat(the(html), hasXPath("count(/table/thead/tr/td)", equalTo("7")));
		// all td's have class='head'
		assertThat(the(html), hasXPath("count(//td[not(@class='head')])", equalTo("0")));
	}
	
	public class TestableProjectsView extends ProjectsView {
		public TestableProjectsView(BundleContext bundlecontext,
				VelocityContext vc) {
			super(bundlecontext, vc);
		}
		
		@Override
		protected StoredProject getProjectByName(String parameter) {
			if (parameter != null && parameter.equals(project.getName())) {
				return project;
			} else {
				return null;
			}
		}

		@Override
		protected Scheduler getScheduler() {
			return scheduler;
		}

		@Override
		protected VelocityContext getVelocityContext() {
			return velocityContext;
		}

		@Override
		protected AdminService getAdminService() {
			return adminService;
		}

		@Override
		protected Logger getLogger() {
			return logger;
		}

		@Override
		protected MetricActivator getMetricActivator() {
			return metricActivator;
		}

		@Override
		protected PluginAdmin getPluginAdmin() {
			return pluginAdmin;
		}

		@Override
		protected String getClusterNodeName() {
			return CLUSTER_NODE_NAME;
		}

		@Override
		protected Set<Updater> getUpdaters(StoredProject selProject,
				UpdaterStage importStage) {
			return updaters.get(importStage);
		}
	}	
}
