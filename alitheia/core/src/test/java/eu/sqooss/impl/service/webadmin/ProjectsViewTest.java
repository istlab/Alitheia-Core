package eu.sqooss.impl.service.webadmin;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.xmlmatchers.transform.XmlConverters.the;
import static org.xmlmatchers.xpath.HasXPath.hasXPath;
import static org.xmlmatchers.xpath.XpathReturnType.returningANumber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
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
import eu.sqooss.service.db.Bug;
import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.MailMessage;
import eu.sqooss.service.db.ProjectVersion;
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
	private static final String CLUSTER_NODE = "CLUSTER_NODE";
	private static final long PROJECT_ID = 1234l;
	private static final String PROJECT_NAME = "project1234";
	private static final String PROJECT_WEBSITE = "www.project.com";
	private static final String PROJECT_CONTACT = "info@project.com";
	private static final String PROJECT_BTS_URL = "bts.project.com";
	private static final String PROJECT_MAIL_URL = "mailinglist.project.com";
	private static final String PROJECT_SCM_URL = "svn.project.com";
	private static final Date EPOCH_DATE = new Date(0);
	private static final String BUG_ID = "nasty_bug";

	private static final String CLUSTER_NODE_NAME = "CLUSTER_NODE1";
	
	private static final String INPUT_REGEX = "<input([\\(\\) a-zA-Z=\\-_\\\"\\\':;0-9\\.\\?\\/\\\\]*[\\(\\) a-zA-Z=\\-_\\\"\\\':;0-9\\.\\?\\\\]+)>";

	private ProjectsView projectsView;
	private Map<UpdaterStage, Set<Updater>> updaters;
	
	@Mock private AdminService adminService;
	@Mock private ClusterNode clusterNode;
	@Mock private Logger logger;
	@Mock private MetricActivator metricActivator;
	@Mock private PluginAdmin pluginAdmin;
	@Mock private StoredProject project1;
	@Mock private StoredProject project2;
	@Mock private Scheduler scheduler;
	@Mock private AlitheiaPlugin somePlugin;
	@Mock private VelocityContext velocityContext;
	private Set<StoredProject> projectSet;
	private Map<StoredProject, MailMessage> mailMessages;

	@Before
	public void setUp() {
		projectsView = new TestableProjectsView(null, null);
		updaters = new HashMap<UpdaterStage, Set<Updater>>();
		mailMessages = new HashMap<StoredProject, MailMessage>();

		when(project1.getId()).thenReturn(PROJECT_ID);
		when(project1.getName()).thenReturn(PROJECT_NAME);
		when(project1.getWebsiteUrl()).thenReturn(PROJECT_WEBSITE);
		when(project1.getContactUrl()).thenReturn(PROJECT_CONTACT);
		when(project1.getBtsUrl()).thenReturn(PROJECT_BTS_URL);
		when(project1.getMailUrl()).thenReturn(PROJECT_MAIL_URL);
		when(project1.getScmUrl()).thenReturn(PROJECT_SCM_URL);
		
		when(project2.getId()).thenReturn(PROJECT_ID + 1);
		when(project2.getName()).thenReturn(PROJECT_NAME + "_2");
		when(project2.getWebsiteUrl()).thenReturn(PROJECT_WEBSITE + "_2");
		when(project2.getContactUrl()).thenReturn(PROJECT_CONTACT + "_2");
		when(project2.getBtsUrl()).thenReturn(PROJECT_BTS_URL + "_2");
		when(project2.getMailUrl()).thenReturn(PROJECT_MAIL_URL + "_2");
		when(project2.getScmUrl()).thenReturn(PROJECT_SCM_URL + "_2");
		ProjectVersion version = new ProjectVersion();
		version.setSequence(1);
		version.setRevisionId("a");
		when(project2.getProjectVersions()).thenReturn(Arrays.asList(version));
		MailMessage message = new MailMessage();
		message.setSendDate(EPOCH_DATE);
		mailMessages.put(project2, message);
		Bug bug = new Bug();
		bug.setBugID(BUG_ID);
		when(project2.getBugs()).thenReturn(new HashSet<Bug>(Arrays.asList(bug)));
		when(project2.isEvaluated()).thenReturn(true);
		when(project2.getClusternode()).thenReturn(clusterNode);
		when(clusterNode.getName()).thenReturn(CLUSTER_NODE);
		
		projectSet = new TreeSet<StoredProject>(new Comparator<StoredProject>() {
			@Override
			public int compare(StoredProject o1, StoredProject o2) {
				return Long.compare(o1.getId(), o2.getId());
			}
		});
		projectSet.add(project1);
		projectSet.add(project2);
	}
	
	@Test
	public void shouldRenderProjectListWithoutRequest() {
		String result = projectsView.render(null);
		
		String html = sanitizeHTML(result);
		// RENG: disregard unbalanced fieldset.
		if (StringUtils.countMatches(html, "<fieldset>") != StringUtils.countMatches(html, "</fieldset>")) {
			html = html.replaceAll("<fieldset>", "").replaceAll("</fieldset>", "");
		}

		// verify that it renders all projects
		assertThat(the(html), hasXPath("//form[@id='projects']//td[text()='" + PROJECT_ID + "']"));
		assertThat(the(html), hasXPath("//form[@id='projects']//td[text()='" + (PROJECT_ID + 1) + "']"));
	}
	
	@Test
	public void shouldRenderProjectListWithEmptyRequest() {
		HttpServletRequest request = mock(HttpServletRequest.class);
		
		String result = projectsView.render(request);
		
		String html = sanitizeHTML(result);
		// RENG: disregard unbalanced fieldset.
		if (StringUtils.countMatches(html, "<fieldset>") != StringUtils.countMatches(html, "</fieldset>")) {
			html = html.replaceAll("<fieldset>", "").replaceAll("</fieldset>", "");
		}
		
		// verify that it renders all projects
		assertThat(the(html), hasXPath("//form[@id='projects']//td[text()='" + PROJECT_ID + "']"));
		assertThat(the(html), hasXPath("//form[@id='projects']//td[text()='" + (PROJECT_ID + 1) + "']"));
	}

	@Test
	public void shouldRenderProjectListWithRequestProjectId() {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(ProjectsView.REQ_PAR_PROJECT_ID)).thenReturn(Long.toString(PROJECT_ID));
		
		String result = projectsView.render(request);
		
		String html = sanitizeHTML(result);
		// RENG: disregard unbalanced fieldset.
		if (StringUtils.countMatches(html, "<fieldset>") != StringUtils.countMatches(html, "</fieldset>")) {
			html = html.replaceAll("<fieldset>", "").replaceAll("</fieldset>", "");
		}
		
		// verify that it renders all projects
		assertThat(the(html), hasXPath("//form[@id='projects']//td[text()='" + PROJECT_ID + "']"));
		assertThat(the(html), hasXPath("//form[@id='projects']//td[text()='" + (PROJECT_ID + 1) + "']"));
	}
	
	@Test
	public void shouldRenderProjectWithRequestProjectIdIfConfirmAdd() {		
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(ProjectsView.REQ_PAR_PROJECT_ID)).thenReturn(Long.toString(PROJECT_ID));
		when(request.getParameter(ProjectsView.REQ_PAR_PRJ_NAME)).thenReturn(PROJECT_NAME);
		when(request.getParameter(ProjectsView.REQ_PAR_ACTION)).thenReturn(ProjectsView.ACT_CON_ADD_PROJECT);
		AdminAction addAction = mock(AdminAction.class);
		when(adminService.create(AddProject.MNEMONIC)).thenReturn(addAction);
		
		String result = projectsView.render(request);
		
		verify(adminService).execute(addAction);
		
		String html = sanitizeHTML(result);
		// RENG: disregard unbalanced fieldset.
		if (StringUtils.countMatches(html, "<fieldset>") != StringUtils.countMatches(html, "</fieldset>")) {
			html = html.replaceAll("<fieldset>", "").replaceAll("</fieldset>", "");
		}
		
		// verify that it renders all projects
		assertThat(the(html), hasXPath("//form[@id='projects']//td[text()='" + PROJECT_ID + "']"));
		assertThat(the(html), hasXPath("//form[@id='projects']//td[text()='" + (PROJECT_ID + 1) + "']"));
	}
	
	@Test
	public void shouldRenderProjectsWithRequestProjectIdIfConfirmRemove() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(ProjectsView.REQ_PAR_PROJECT_ID)).thenReturn(Long.toString(PROJECT_ID));
		when(request.getParameter(ProjectsView.REQ_PAR_PRJ_NAME)).thenReturn(PROJECT_NAME);
		when(request.getParameter(ProjectsView.REQ_PAR_ACTION)).thenReturn(ProjectsView.ACT_CON_REM_PROJECT);
		
		String result = projectsView.render(request);
		
		verify(scheduler).enqueue(any(ProjectDeleteJob.class));
		
		String html = sanitizeHTML(result);
		// RENG: disregard unbalanced fieldset.
		if (StringUtils.countMatches(html, "<fieldset>") != StringUtils.countMatches(html, "</fieldset>")) {
			html = html.replaceAll("<fieldset>", "").replaceAll("</fieldset>", "");
		}
		
		// verify that it renders all projects
		assertThat(the(html), hasXPath("//form[@id='projects']//td[text()='" + PROJECT_ID + "']"));
		assertThat(the(html), hasXPath("//form[@id='projects']//td[text()='" + (PROJECT_ID + 1) + "']"));
	}
	
	@Test
	public void shouldTriggerUpdateForSelectedProject() {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(ProjectsView.REQ_PAR_PROJECT_ID)).thenReturn(Long.toString(PROJECT_ID));
		when(request.getParameter(ProjectsView.REQ_PAR_PRJ_NAME)).thenReturn(PROJECT_NAME);
		when(request.getParameter(ProjectsView.REQ_PAR_ACTION)).thenReturn(ProjectsView.ACT_CON_UPD);
		when(request.getParameter(ProjectsView.REQ_PAR_UPD)).thenReturn("updater");
		AdminAction updateAction = mock(AdminAction.class);
		when(adminService.create(UpdateProject.MNEMONIC)).thenReturn(updateAction);
		
		String result = projectsView.render(request);
		
		verify(updateAction).addArg("project", PROJECT_ID);
		verify(updateAction).addArg("updater", "updater");
		verify(adminService).execute(updateAction);
		
		String html = sanitizeHTML(result);
		// RENG: disregard unbalanced fieldset.
		if (StringUtils.countMatches(html, "<fieldset>") != StringUtils.countMatches(html, "</fieldset>")) {
			html = html.replaceAll("<fieldset>", "").replaceAll("</fieldset>", "");
		}

		// verify that it renders all projects
		assertThat(the(html), hasXPath("//form[@id='projects']//td[text()='" + PROJECT_ID + "']"));
		assertThat(the(html), hasXPath("//form[@id='projects']//td[text()='" + (PROJECT_ID + 1) + "']"));
	}
	
	@Test
	public void shouldTriggerUpdateAllForSelectedProject() {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(ProjectsView.REQ_PAR_PROJECT_ID)).thenReturn(Long.toString(PROJECT_ID));
		when(request.getParameter(ProjectsView.REQ_PAR_PRJ_NAME)).thenReturn(PROJECT_NAME);
		when(request.getParameter(ProjectsView.REQ_PAR_ACTION)).thenReturn(ProjectsView.ACT_CON_UPD_ALL);
		AdminAction updateAction = mock(AdminAction.class);
		when(adminService.create(UpdateProject.MNEMONIC)).thenReturn(updateAction);
		
		String result = projectsView.render(request);
		
		verify(updateAction).addArg("project", PROJECT_ID);
		verify(updateAction, times(0)).addArg(eq("updater"), anyString());
		verify(adminService).execute(updateAction);
		
		String html = sanitizeHTML(result);
		// RENG: disregard unbalanced fieldset.
		if (StringUtils.countMatches(html, "<fieldset>") != StringUtils.countMatches(html, "</fieldset>")) {
			html = html.replaceAll("<fieldset>", "").replaceAll("</fieldset>", "");
		}
		
		// verify that it renders all projects
		assertThat(the(html), hasXPath("//form[@id='projects']//td[text()='" + PROJECT_ID + "']"));
		assertThat(the(html), hasXPath("//form[@id='projects']//td[text()='" + (PROJECT_ID + 1) + "']"));
	}
	
	@Test
	public void shouldTriggerUpdateActionForAllProjects() {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(ProjectsView.REQ_PAR_PROJECT_ID)).thenReturn(Long.toString(PROJECT_ID));
		when(request.getParameter(ProjectsView.REQ_PAR_PRJ_NAME)).thenReturn(PROJECT_NAME);
		when(request.getParameter(ProjectsView.REQ_PAR_ACTION)).thenReturn(ProjectsView.ACT_CON_UPD_ALL_NODE);
		AdminAction action1 = mock(AdminAction.class);
		AdminAction action2 = mock(AdminAction.class);
		when(adminService.create(UpdateProject.MNEMONIC)).thenReturn(action1).thenReturn(action2);
		
		String result = projectsView.render(request);
		
		verify(action1).addArg("project", PROJECT_ID);
		verify(action1, times(0)).addArg(eq("updater"), anyString());
	 	verify(action2).addArg("project", PROJECT_ID + 1);
	 	verify(action2, times(0)).addArg(eq("updater"), anyString());
	 	verify(adminService).execute(action1);
	 	verify(adminService).execute(action2);
	 	
	 	String html = sanitizeHTML(result);
		// RENG: disregard unbalanced fieldset.
		if (StringUtils.countMatches(html, "<fieldset>") != StringUtils.countMatches(html, "</fieldset>")) {
			html = html.replaceAll("<fieldset>", "").replaceAll("</fieldset>", "");
		}
		
		// verify that it renders all projects
		assertThat(the(html), hasXPath("//form[@id='projects']//td[text()='" + PROJECT_ID + "']"));
		assertThat(the(html), hasXPath("//form[@id='projects']//td[text()='" + (PROJECT_ID + 1) + "']"));
	}
	
	@Test
	public void shouldTriggerSyncPlugin() {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(ProjectsView.REQ_PAR_PROJECT_ID)).thenReturn(Long.toString(PROJECT_ID));
		when(request.getParameter(ProjectsView.REQ_PAR_PRJ_NAME)).thenReturn(PROJECT_NAME);
		when(request.getParameter(ProjectsView.REQ_PAR_ACTION)).thenReturn("no_action_in_particular");
		
		when(request.getParameter(ProjectsView.REQ_PAR_SYNC_PLUGIN)).thenReturn("plugin1");
		PluginInfo pluginInfo = mock(PluginInfo.class);
		when(pluginAdmin.getPluginInfo("plugin1")).thenReturn(pluginInfo);
		when(pluginAdmin.getPlugin(pluginInfo)).thenReturn(somePlugin);
		
		String result = projectsView.render(request);
		
		verify(metricActivator).syncMetric(somePlugin, project1);
		
		String html = sanitizeHTML(result);
		// RENG: disregard unbalanced fieldset.
		if (StringUtils.countMatches(html, "<fieldset>") != StringUtils.countMatches(html, "</fieldset>")) {
			html = html.replaceAll("<fieldset>", "").replaceAll("</fieldset>", "");
		}
		
		// verify that it renders all projects
		assertThat(the(html), hasXPath("//form[@id='projects']//td[text()='" + PROJECT_ID + "']"));
		assertThat(the(html), hasXPath("//form[@id='projects']//td[text()='" + (PROJECT_ID + 1) + "']"));
	}
	
	@Test
	public void shouldExecuteAddActionAndPutResults() {
		AdminAction action = mock(AdminAction.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(ProjectsView.REQ_PAR_PRJ_CODE)).thenReturn("scm");
		when(request.getParameter(ProjectsView.REQ_PAR_PRJ_NAME)).thenReturn(PROJECT_NAME);
		when(request.getParameter(ProjectsView.REQ_PAR_PRJ_BUG)).thenReturn("bug");
		when(request.getParameter(ProjectsView.REQ_PAR_PRJ_MAIL)).thenReturn("test@test.tst");
		when(request.getParameter(ProjectsView.REQ_PAR_PRJ_WEB)).thenReturn("web.com");
		when(adminService.create(AddProject.MNEMONIC)).thenReturn(action);
		Map<String, Object> results = new HashMap<String, Object>();
		when(action.results()).thenReturn(results);

		StoredProject added = projectsView.addProject(null, request, 0);

		verify(adminService).create(AddProject.MNEMONIC);
		verify(action).addArg("scm", "scm");
		verify(action).addArg("name", PROJECT_NAME);
		verify(action).addArg("bts", "bug");
		verify(action).addArg("mail", "test@test.tst");
		verify(action).addArg("web", "web.com");
		verify(adminService).execute(action);
		verify(velocityContext).put("RESULTS", results);
		
		assertEquals(project1, added);
	}
	
	@Test
	public void shouldExecuteAddActionAndPutErrors() {
		AdminAction action = mock(AdminAction.class);
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(ProjectsView.REQ_PAR_PRJ_CODE)).thenReturn("scm");
		when(request.getParameter(ProjectsView.REQ_PAR_PRJ_NAME)).thenReturn(PROJECT_NAME);
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
		verify(action).addArg("name", PROJECT_NAME);
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

		projectsView.removeProject(error, project1, 0);
		
		verify(scheduler).enqueue(any(ProjectDeleteJob.class));
		assertThat(error.toString(), equalTo(""));
	}
	
	@Test
	public void shouldPrintErrorIfEnqueueProjectDeleteJobFails() throws Exception {
		StringBuilder error = new StringBuilder();

		doThrow(new SchedulerException("Big failure")).when(scheduler).enqueue(any(ProjectDeleteJob.class));
		
		projectsView.removeProject(error, project1, 0);
		
		verify(scheduler).enqueue(any(ProjectDeleteJob.class));
		assertThat(error.toString(), not(equalTo("")));
	}
	
	@Test
	public void shouldExecuteUpdateActionAndPutResults() {
		AdminAction action = mock(AdminAction.class);
		Map<String, Object> results = new HashMap<String, Object>();
		
		when(adminService.create(UpdateProject.MNEMONIC)).thenReturn(action);
		when(action.results()).thenReturn(results);
		
		projectsView.triggerUpdate(null, project1, 0, "updater");
		
		verify(adminService).create(UpdateProject.MNEMONIC);
		verify(action).addArg("project", PROJECT_ID);
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
		
		projectsView.triggerUpdate(null, project1, 0, "updater");
		
		verify(adminService).create(UpdateProject.MNEMONIC);
		verify(action).addArg("project", PROJECT_ID);
		verify(action).addArg("updater", "updater");
		verify(adminService).execute(action);
		
		verify(velocityContext).put("RESULTS", errors);
	}
	
	@Test
	public void shouldExecuteUpdateActionWithoutMnemonicAndPutResults() {
		AdminAction action = mock(AdminAction.class);
		Map<String, Object> errors = new HashMap<String, Object>();
		
		when(adminService.create(UpdateProject.MNEMONIC)).thenReturn(action);
		when(action.errors()).thenReturn(errors);
		when(action.hasErrors()).thenReturn(true);
		
		projectsView.triggerAllUpdate(null, project1, 0);
		
		verify(adminService).create(UpdateProject.MNEMONIC);
		verify(action).addArg("project", PROJECT_ID);
		verify(action, times(0)).addArg(eq("updater"), anyString());
		verify(adminService).execute(action);
		
		verify(velocityContext).put("RESULTS", errors);
	}
	
	@Test
	public void shouldExecuteUpdateActionWithoutMnemonicAndPutErrors() {
		AdminAction action = mock(AdminAction.class);
		Map<String, Object> results = new HashMap<String, Object>();
		
		when(adminService.create(UpdateProject.MNEMONIC)).thenReturn(action);
		when(action.results()).thenReturn(results);
		
		projectsView.triggerAllUpdate(null, project1, 0);
		
		verify(adminService).create(UpdateProject.MNEMONIC);
		verify(action).addArg("project", PROJECT_ID);
		verify(action, times(0)).addArg(eq("updater"), anyString());
		verify(adminService).execute(action);
		
		verify(velocityContext).put("RESULTS", results);
	}
	
	@Test
	public void shouldExecuteUpdateActionForAllProjects() {
		AdminAction action1 = mock(AdminAction.class);
		AdminAction action2 = mock(AdminAction.class);
		when(adminService.create(UpdateProject.MNEMONIC)).thenReturn(action1).thenReturn(action2);
		
		projectsView.triggerAllUpdateNode(null, null, 0);
		
		verify(action1).addArg("project", PROJECT_ID);
		verify(action1, times(0)).addArg(eq("updater"), anyString());
	 	verify(action2).addArg("project", PROJECT_ID + 1);
	 	verify(action2, times(0)).addArg(eq("updater"), anyString());
	 	verify(adminService).execute(action1);
	 	verify(adminService).execute(action2);
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
		projectsView.syncPlugin(null, project1, "selected_plugin");
		
		verify(pluginAdmin).getPluginInfo("selected_plugin");
		verifyNoMoreInteractions(pluginAdmin);
	}
	
	@Test
	public void syncNothingIfNoPluginObject() {
		PluginInfo info = new PluginInfo();
		when(pluginAdmin.getPluginInfo("selected_plugin")).thenReturn(info);
		
		projectsView.syncPlugin(null, project1, "selected_plugin");
		
		verify(pluginAdmin).getPluginInfo("selected_plugin");
		verify(pluginAdmin).getPlugin(info);
		verifyNoMoreInteractions(pluginAdmin);
	}
	
	@Test
	public void syncMetricIfPluginObjectFound() {
		PluginInfo info = new PluginInfo();
		when(pluginAdmin.getPluginInfo("selected_plugin")).thenReturn(info);
		when(pluginAdmin.getPlugin(info)).thenReturn(somePlugin);
		
		projectsView.syncPlugin(null, project1, "selected_plugin");
		
		verify(pluginAdmin).getPluginInfo("selected_plugin");
		verify(pluginAdmin).getPlugin(info);
		verifyNoMoreInteractions(pluginAdmin);
		
		verify(metricActivator).syncMetric(somePlugin, project1);
	}
	
	@Test
	public void shouldCreateForm() {
		StringBuilder builder = new StringBuilder();
		
		projectsView.createForm(builder, null, project1, ProjectsView.ACT_REQ_SHOW_PROJECT, 0);
		
		String html = sanitizeHTML(builder.toString());
		
		// test that the form exists
		assertThat(the(html), hasXPath("/root/form[@id='projects' and @name='projects' and @method='post' and @action='/projects']"));
	}
	
	@Test
	public void shouldPresentErrorsInForm() {
		StringBuilder builder = new StringBuilder();
		StringBuilder errors = new StringBuilder("Fatal_Error");
		
		projectsView.createForm(builder, errors, project1, ProjectsView.ACT_REQ_SHOW_PROJECT, 0);
		
		String html = sanitizeHTML(builder.toString());
		
		// test that the form contains a fieldset that contains the error message.
		assertThat(the(html), hasXPath("string-join(//form[@id='projects']/fieldset[legend[text()='Errors']]/text(), '')", equalToIgnoringWhiteSpace("Fatal_Error")));
	}
	
	@Test
	public void shouldShowProjectInfo() {
		StringBuilder builder = new StringBuilder();
		
		projectsView.createForm(builder, null, project1, ProjectsView.ACT_REQ_SHOW_PROJECT, 0);
		
		String html = sanitizeHTML(builder.toString());

		assertThat(the(html), hasXPath("//form[@id='projects']/fieldset[legend[text()='Project information']]/table/tr[td[1]//*[contains(text(), 'Project name')]]/td[2]", equalToIgnoringWhiteSpace(PROJECT_NAME)));
		assertThat(the(html), hasXPath("//form[@id='projects']/fieldset[legend[text()='Project information']]/table/tr[td[1]//*[contains(text(), 'Homepage')]]/td[2]", equalToIgnoringWhiteSpace(PROJECT_WEBSITE)));
		assertThat(the(html), hasXPath("//form[@id='projects']/fieldset[legend[text()='Project information']]/table/tr[td[1]//*[contains(text(), 'Contact e-mail')]]/td[2]", equalToIgnoringWhiteSpace(PROJECT_CONTACT)));
		assertThat(the(html), hasXPath("//form[@id='projects']/fieldset[legend[text()='Project information']]/table/tr[td[1]//*[contains(text(), 'Bug database')]]/td[2]", equalToIgnoringWhiteSpace(PROJECT_BTS_URL)));
		assertThat(the(html), hasXPath("//form[@id='projects']/fieldset[legend[text()='Project information']]/table/tr[td[1]//*[contains(text(), 'Mailing list')]]/td[2]", equalToIgnoringWhiteSpace(PROJECT_MAIL_URL)));
		assertThat(the(html), hasXPath("//form[@id='projects']/fieldset[legend[text()='Project information']]/table/tr[td[1]//*[contains(text(), 'Source code')]]/td[2]", equalToIgnoringWhiteSpace(PROJECT_SCM_URL)));
		
		assertThat(the(html), hasXPath("//form[@id='projects']/fieldset[legend[text()='Project information']]/table/tr[last()]/td/input[@type='button']/@onclick", equalTo("javascript:" + ProjectsView.SUBMIT)));
		
		assertThat(the(html), hasXPath("//form[@id='projects']/input[@type='hidden' and @id='" + ProjectsView.REQ_PAR_ACTION + "']"));
		assertThat(the(html), hasXPath("//form[@id='projects']/input[@type='hidden' and @id='" + ProjectsView.REQ_PAR_PROJECT_ID + "']"));
		assertThat(the(html), hasXPath("//form[@id='projects']/input[@type='hidden' and @id='" + ProjectsView.REQ_PAR_SYNC_PLUGIN + "']"));
	}
	
	@Test
	public void shouldShowAddProjectForm() {
		StringBuilder builder = new StringBuilder();
		
		projectsView.createForm(builder, null, project1, ProjectsView.ACT_REQ_ADD_PROJECT, 0);
		
		String html = sanitizeHTML(builder.toString());
		
		assertThat(the(html), hasXPath("//form[@id='projects']/table/tr[td[1]//*[contains(text(), 'Project name')]]/td[2]/input[@type='text' and @id='" + ProjectsView.REQ_PAR_PRJ_NAME + "' and @value='']"));
		assertThat(the(html), hasXPath("//form[@id='projects']/table/tr[td[1]//*[contains(text(), 'Homepage')]]/td[2]/input[@type='text' and @id='" + ProjectsView.REQ_PAR_PRJ_WEB + "' and @value='']"));
		assertThat(the(html), hasXPath("//form[@id='projects']/table/tr[td[1]//*[contains(text(), 'Contact e-mail')]]/td[2]/input[@type='text' and @id='" + ProjectsView.REQ_PAR_PRJ_CONT + "' and @value='']"));
		assertThat(the(html), hasXPath("//form[@id='projects']/table/tr[td[1]//*[contains(text(), 'Bug database')]]/td[2]/input[@type='text' and @id='" + ProjectsView.REQ_PAR_PRJ_BUG + "' and @value='']"));
		assertThat(the(html), hasXPath("//form[@id='projects']/table/tr[td[1]//*[contains(text(), 'Mailing list')]]/td[2]/input[@type='text' and @id='" + ProjectsView.REQ_PAR_PRJ_MAIL + "' and @value='']"));
		assertThat(the(html), hasXPath("//form[@id='projects']/table/tr[td[1]//*[contains(text(), 'Source code')]]/td[2]/input[@type='text' and @id='" + ProjectsView.REQ_PAR_PRJ_CODE + "' and @value='']"));

		assertThat(the(html), hasXPath("//form[@id='projects']/table/tr[last()]/td/input[@type='button'][1]/@onclick", equalTo("javascript:document.getElementById('" + ProjectsView.REQ_PAR_ACTION + "').value='" + ProjectsView.ACT_CON_ADD_PROJECT + "';" + ProjectsView.SUBMIT)));
		assertThat(the(html), hasXPath("//form[@id='projects']/table/tr[last()]/td/input[@type='button'][2]/@onclick", equalTo("javascript:" + ProjectsView.SUBMIT)));
		
		assertThat(the(html), hasXPath("//form[@id='projects']/input[@type='hidden' and @id='" + ProjectsView.REQ_PAR_ACTION + "']"));
		assertThat(the(html), hasXPath("//form[@id='projects']/input[@type='hidden' and @id='" + ProjectsView.REQ_PAR_PROJECT_ID + "']"));
		assertThat(the(html), hasXPath("//form[@id='projects']/input[@type='hidden' and @id='" + ProjectsView.REQ_PAR_SYNC_PLUGIN + "']"));
	}
	
	@Test
	public void shouldShowDeleteProjectConfirmation() {
		StringBuilder builder = new StringBuilder();
		
		projectsView.createForm(builder, null, project1, ProjectsView.ACT_REQ_REM_PROJECT, 0);
		
		String html = sanitizeHTML(builder.toString());
		
		assertThat(the(html), hasXPath("//form[@id='projects']/fieldset[legend[contains(text(), '" + PROJECT_NAME + "')]]/table/tr/td/input[@type='button'][1]/@onclick", equalTo("javascript:document.getElementById('" + ProjectsView.REQ_PAR_ACTION + "').value='" + ProjectsView.ACT_CON_REM_PROJECT +"';" + ProjectsView.SUBMIT)));
		assertThat(the(html), hasXPath("//form[@id='projects']/fieldset[legend[contains(text(), '" + PROJECT_NAME + "')]]/table/tr/td/input[@type='button'][2]/@onclick", equalTo("javascript:" + ProjectsView.SUBMIT)));
		
		assertThat(the(html), hasXPath("//form[@id='projects']/input[@type='hidden' and @id='" + ProjectsView.REQ_PAR_ACTION + "']"));
		assertThat(the(html), hasXPath("//form[@id='projects']/input[@type='hidden' and @id='" + ProjectsView.REQ_PAR_PROJECT_ID + "']"));
		assertThat(the(html), hasXPath("//form[@id='projects']/input[@type='hidden' and @id='" + ProjectsView.REQ_PAR_SYNC_PLUGIN + "']"));
	}
	
	@Test
	public void shouldShowMessageIfNoProjects() {
		StringBuilder builder = new StringBuilder();
		
		projectSet.clear();
		projectsView.createForm(builder, null, project1, "some_non_existing_action", 0);
		
		String html = sanitizeHTML(builder.toString());
		
		// RENG: same for fieldset.
		if (StringUtils.countMatches(html, "<fieldset>") != StringUtils.countMatches(html, "</fieldset>")) {
			html = html.replaceAll("<fieldset>", "").replaceAll("</fieldset>", "");
		}
		
		assertThat(the(html), hasXPath("count(//form[@id='projects']/table/thead/tr/td)", returningANumber(), equalTo(7.0)));
		assertThat(the(html), hasXPath("count(//form[@id='projects']/table/tbody/tr[1]/td)", returningANumber(), equalTo(1.0)));
	}
	
	@Test
	public void shouldCreateContentRowPerProject() {
		StringBuilder builder = new StringBuilder();
		
		projectsView.createForm(builder, null, project1, "some_non_existing_action", 0);

		String html = sanitizeHTML(builder.toString());		
		// RENG: Ignore an uneven number of fieldset.
		if (StringUtils.countMatches(html, "<fieldset>") != StringUtils.countMatches(html, "</fieldset>")) {
			html = html.replaceAll("<fieldset>", "").replaceAll("</fieldset>", "");
		}
		
		assertThat(the(html), hasXPath("//form[@id='projects']/table/tbody/tr[1]/@class", equalTo("selected")));
		assertThat(the(html), hasXPath("//form[@id='projects']/table/tbody/tr[1]/@onclick", equalTo("javascript:document.getElementById('" + ProjectsView.REQ_PAR_PROJECT_ID + "').value='';" + ProjectsView.SUBMIT)));
		assertThat(the(html), hasXPath("//form[@id='projects']/table/tbody/tr[1]/td[1]/text()", equalToIgnoringWhiteSpace(Long.toString(PROJECT_ID))));
		assertThat(the(html), hasXPath("string-join(//form[@id='projects']/table/tbody/tr[1]/td[2]/text(), '')", equalToIgnoringWhiteSpace(PROJECT_NAME)));
		assertThat(the(html), hasXPath("//form[@id='projects']/table/tbody/tr[1]/td[2]/input[@type='button']/@onclick", equalTo("javascript:document.getElementById('" + ProjectsView.REQ_PAR_ACTION + "').value='" + ProjectsView.ACT_REQ_SHOW_PROJECT + "';" + ProjectsView.SUBMIT)));
		assertThat(the(html), hasXPath("//form[@id='projects']/table/tbody/tr[1]/td[3]/text()", equalToIgnoringWhiteSpace("l0051")));
		assertThat(the(html), hasXPath("//form[@id='projects']/table/tbody/tr[1]/td[4]/text()", equalToIgnoringWhiteSpace("l0051")));
		assertThat(the(html), hasXPath("//form[@id='projects']/table/tbody/tr[1]/td[5]/text()", equalToIgnoringWhiteSpace("l0051")));
		assertThat(the(html), hasXPath("//form[@id='projects']/table/tbody/tr[1]/td[6]/text()", equalToIgnoringWhiteSpace("project_not_evaluated")));
		assertThat(the(html), hasXPath("//form[@id='projects']/table/tbody/tr[1]/td[7]/text()", equalToIgnoringWhiteSpace("(local)")));
		
		assertThat(the(html), hasXPath("//form[@id='projects']/table/tbody/tr[2]/@class", equalTo("edit")));
		assertThat(the(html), hasXPath("//form[@id='projects']/table/tbody/tr[2]/@onclick", equalTo("javascript:document.getElementById('" + ProjectsView.REQ_PAR_PROJECT_ID + "').value='" + (PROJECT_ID + 1) + "';" + ProjectsView.SUBMIT)));
		assertThat(the(html), hasXPath("//form[@id='projects']/table/tbody/tr[2]/td[1]/text()", equalToIgnoringWhiteSpace(Long.toString(PROJECT_ID + 1))));
		assertThat(the(html), hasXPath("string-join(//form[@id='projects']/table/tbody/tr[2]/td[2]/text(), '')", equalToIgnoringWhiteSpace(PROJECT_NAME + "_2")));
		assertThat(the(html), hasXPath("//form[@id='projects']/table/tbody/tr[2]/td[2]/img"));
		assertThat(the(html), hasXPath("//form[@id='projects']/table/tbody/tr[2]/td[3]/text()", equalToIgnoringWhiteSpace("1(a)")));
		assertThat(the(html), hasXPath("//form[@id='projects']/table/tbody/tr[2]/td[4]/text()", equalToIgnoringWhiteSpace(EPOCH_DATE.toString())));
		assertThat(the(html), hasXPath("//form[@id='projects']/table/tbody/tr[2]/td[5]/text()", equalToIgnoringWhiteSpace(BUG_ID)));
		assertThat(the(html), hasXPath("//form[@id='projects']/table/tbody/tr[2]/td[6]/text()", equalToIgnoringWhiteSpace("project_is_evaluated")));
		assertThat(the(html), hasXPath("//form[@id='projects']/table/tbody/tr[2]/td[7]/text()", equalToIgnoringWhiteSpace(CLUSTER_NODE)));	
	}
	
	@Test
	public void shouldPrintAppliedVersion() {
		StringBuilder builder = new StringBuilder();
		
		String hash1 = "hash1";
		String name1 = "name1";
		String hash2 = "hash2";
		String name2 = "name2";
		
		PluginInfo p1 = new PluginInfo();
		p1.installed = true;
		p1.setHashcode(hash1);
		p1.setPluginName(name1);
		PluginInfo p2 = new PluginInfo();
		p2.installed = true;
		p2.setHashcode(hash2);
		p2.setPluginName(name2);
		Collection<PluginInfo> metrics = Arrays.asList(p1, p2);
		
		when(pluginAdmin.listPlugins()).thenReturn(metrics);
		
		projectsView.createForm(builder, null, project1, "some_non_existing_action", 0);

		String html = sanitizeHTML(builder.toString());		
		// RENG: Ignore an uneven number of fieldset.
		if (StringUtils.countMatches(html, "<fieldset>") != StringUtils.countMatches(html, "</fieldset>")) {
			html = html.replaceAll("<fieldset>", "").replaceAll("</fieldset>", "");
		}
		
		// RENG: very coarse test, just verifies that something is done with these metrics.
		assertThat(the(html), hasXPath("//form[@id='projects']/table//td[contains(string-join(text(), ''), '" + name1 + "') and input[@type='button' and contains(@onclick, '" + hash1 + "')]]"));
		assertThat(the(html), hasXPath("//form[@id='projects']/table//td[contains(string-join(text(), ''), '" + name2 + "') and input[@type='button' and contains(@onclick, '" + hash2 + "')]]"));
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
		projectsView.addHiddenFields(project1 , builder, 0);
		
		String html = sanitizeHTML(builder.toString());
		
		assertThat(the(html), hasXPath("/root/input[@type='hidden' and @id='" + ProjectsView.REQ_PAR_PROJECT_ID + "' and @name='" + ProjectsView.REQ_PAR_PROJECT_ID + "']/@value", equalTo("1234")));
	}

	protected String sanitizeHTML(String string) {
		String html = "<root>" + string + "</root>";		
		html = html.replaceAll(INPUT_REGEX, "<input$1/>");
		html = html.replaceAll("&nbsp;", " ");
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
		
		projectsView.addToolBar(project1, builder, 0);

		// sanitize html input
		String html = sanitizeHTML(builder.toString());

		// the first row should have a button that goes to the project page
		String onclick1 = "javascript:window.location='/projects?" + ProjectsView.REQ_PAR_PROJECT_ID + "=" + project1.getId() + "';";
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
		String html = sanitizeHTML(builder.toString());
		
		String onclick1 = "javascript:document.getElementById('" + ProjectsView.REQ_PAR_SYNC_PLUGIN + "').value='" + hash1 +"';" + ProjectsView.SUBMIT;
		String onclick2 = "javascript:document.getElementById('" + ProjectsView.REQ_PAR_SYNC_PLUGIN + "').value='" + hash2 +"';" + ProjectsView.SUBMIT;

		// there must be two rows.
		assertThat(the(html), hasXPath("count(/root/tr)", returningANumber(), equalTo(2.0)));
		// the first row has one column.
		
		assertThat(the(html), hasXPath("count(/root/tr[1]/td)", returningANumber(), equalTo(1.0)));
		// that column has an input of type button that does 'onclick1' on click.
		assertThat(the(html), hasXPath("/root/tr[1]/td/input[@type='button']/@onclick", equalTo(onclick1)));
		// that column's text should contain the plugin name
		assertThat(the(html), hasXPath("string-join(/root/tr[1]/td/text(), '')", containsString(name1)));
		
		// the second row has one column.
		assertThat(the(html), hasXPath("count(/root/tr[2]/td)", returningANumber(), equalTo(1.0)));
		// that column has an input of type button that does 'onclick2' on click.
		assertThat(the(html), hasXPath("/root/tr[2]/td/input[@type='button']/@onclick", equalTo(onclick2)));
		// that column's text should contain the plugin name
		assertThat(the(html), hasXPath("string-join(/root/tr[2]/td/text(), '')", containsString(name2)));
	}

	@Test
	public void shouldCreateTableHeaderRow() {
		StringBuilder builder = new StringBuilder();
		long in = 0;

		// RENG: This method opens table, but doesn't close it. Move this out?
		builder.append(ProjectsView.sp(in++) + "<table>\n");
		projectsView.addHeaderRow(builder, in);

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
		protected StoredProject getProjectById(long projectId) {
			for (StoredProject project : projectSet) {
				if (project.getId() == projectId) {
					return project;
				}
			}
			return null;
		}

		@Override
		protected StoredProject getProjectByName(String parameter) {
			for (StoredProject project : projectSet) {
				if (project.getName().equals(parameter)) {
					return project;
				}
			}
			return null;
		}

		@Override
		protected void initializeResources(HttpServletRequest req) {
			// do nothing.
		}

		@Override
		protected MailMessage getLastMailMessage(StoredProject project) {
			return mailMessages.get(project);
		}
		
		@Override
		protected Bug getLastBug(StoredProject project) {
			Set<Bug> bugs = project.getBugs();
			if (bugs.isEmpty()) {
				return null;
			} else {
				ArrayList<Bug> bugsList = new ArrayList<Bug>(bugs);
				Collections.sort(bugsList, new Comparator<Bug>() {
					@Override
					public int compare(Bug b1, Bug b2) {
						return b1.getUpdateRun().compareTo(b2.getUpdateRun());
					}
				});
				return bugsList.get(bugsList.size() - 1);
			}
		}

		@Override
		protected ProjectVersion getLastProjectVersion(StoredProject project) {
			List<ProjectVersion> projectVersions = project.getProjectVersions();
			if (projectVersions == null || projectVersions.isEmpty()) {
				return null;
			} else {
				return projectVersions.get(projectVersions.size() - 1);
			}
		}

		@Override
		protected Set<StoredProject> getThisNodeProjects() {
			return projectSet;
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
			Set<Updater> set = updaters.get(importStage);
			return set == null ? new HashSet<Updater>() : set;
		}
	}	
}
