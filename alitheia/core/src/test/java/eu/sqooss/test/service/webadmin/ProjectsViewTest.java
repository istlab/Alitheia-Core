package eu.sqooss.test.service.webadmin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;

import org.apache.velocity.VelocityContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.osgi.framework.BundleContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.ProjectDeleteJob;
import eu.sqooss.impl.service.webadmin.ProjectsView;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.admin.AdminAction;
import eu.sqooss.service.admin.AdminService;
import eu.sqooss.service.admin.actions.AddProject;
import eu.sqooss.service.admin.actions.UpdateProject;
import eu.sqooss.service.cluster.ClusterNodeService;
import eu.sqooss.service.db.Bug;
import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.MailMessage;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.service.updater.Updater;
import eu.sqooss.service.updater.UpdaterService;
import eu.sqooss.service.updater.UpdaterService.UpdaterStage;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ClusterNode.class, 
		ProjectVersion.class, 
		Bug.class, 
		MailMessage.class, 
		ClusterNode.class,
		StoredProject.class
	})
public class ProjectsViewTest {
	@Mock private DBService sobjDB;
	@Mock private ClusterNodeService sobjClusterNode;
	@Mock private StoredProject projectMock;
	@Mock private UpdaterService sobjUpdater;
	@Mock private AlitheiaPlugin pObj;
	@Mock private HttpServletRequest request;
	
	@Mock private PluginAdmin sobjPA;
	@Mock private MetricActivator compMA;
	@Mock private Logger sobjLogger;
	@Mock private BundleContext bc;
	@Mock private VelocityContext vc;
	@Mock private AlitheiaCore core;
	@Mock private AdminService as;
	@Mock private AdminAction aa;
	@Mock private Scheduler sobjSched;
	
	private ProjectsView pView;
	
	private void initPView(){
		pView = new ProjectsView(bc, vc);
		Whitebox.setInternalState(ProjectsView.class, sobjPA );
		Whitebox.setInternalState(ProjectsView.class, compMA);
		Whitebox.setInternalState(ProjectsView.class, sobjLogger);
		Whitebox.setInternalState(ProjectsView.class, core);
		Whitebox.setInternalState(ProjectsView.class, sobjSched);
		

		when(core.getAdminService()).thenReturn(as);
		when(as.create(UpdateProject.MNEMONIC)).thenReturn(aa);		
		when(as.create(AddProject.MNEMONIC)).thenReturn(aa);		
	}

	@Test
	public void testGetSelProject() {
		initPView();
		StoredProject selProject = new StoredProject();
		Whitebox.setInternalState(pView, selProject);
		
		assertEquals(selProject, pView.getSelProject());
	}
	
	//Only test basic exec functionality
	@Test
	public void testExec() {
		initPView();
		
		pView.exec(request);
		
		Assert.assertNull(pView.getSelProject());
	}
	
	@Test
	public void testaddProject() throws Exception {
		initPView();
		StoredProject project = new StoredProject();
		PowerMockito.mockStatic(StoredProject.class);
	
		when(StoredProject.getProjectByName(any(String.class))).thenReturn(project);
		
		assertEquals(Whitebox.invokeMethod(pView, "addProject", request), project);

		verify(aa, times(5)).addArg(any(String.class), any(Object.class));
		verify(as, times(1)).execute(aa);
		verify(aa, times(1)).results();
	}

	@Test
	public void testaddProjectFails() throws Exception {
		initPView();
		PowerMockito.mockStatic(StoredProject.class);
		when(aa.hasErrors()).thenReturn(true);
		
		Assert.assertNull(Whitebox.invokeMethod(pView, "addProject", request));

		verify(aa, times(5)).addArg(any(String.class), any(Object.class));
		verify(as, times(1)).execute(aa);
		verify(aa, times(1)).errors();
	}
	
	@Test
	public void testSyncPluginNull() throws Exception {
		initPView();
		
		//Test one of the parameters being null, should not perform
		StoredProject selProject = new StoredProject();
		String reqValSyncPlugin = "reqValSyncPlugin";

		Whitebox.invokeMethod(pView, "syncPlugin", (StoredProject)null, (String)null);
		Whitebox.invokeMethod(pView, "syncPlugin", (StoredProject)selProject, (String)null);
		Whitebox.invokeMethod(pView, "syncPlugin", (StoredProject)null, (String)reqValSyncPlugin);
	
		verify(sobjPA, times(0)).getPluginInfo(any(String.class));
	}
	
	@Test
	public void testSyncPluginPInfoNull() throws Exception {
		initPView();
		
		StoredProject selProject = new StoredProject();
		String reqValSyncPlugin = "reqValSyncPlugin";
		
		Whitebox.invokeMethod(pView, "syncPlugin", selProject, reqValSyncPlugin);

		verify(sobjPA, times(1)).getPluginInfo(reqValSyncPlugin);
		verify(sobjPA, times(0)).getPlugin(any(PluginInfo.class));
	}

	@Test
	public void testSyncPluginPluginNull() throws Exception {
		initPView();
		
		StoredProject selProject = new StoredProject();
		String reqValSyncPlugin = "reqValSyncPlugin";
		PluginInfo pInfo = new PluginInfo();
		
		when(sobjPA.getPluginInfo(reqValSyncPlugin)).thenReturn(pInfo);
		
		Whitebox.invokeMethod(pView, "syncPlugin", selProject, reqValSyncPlugin);

		verify(sobjPA, times(1)).getPluginInfo(reqValSyncPlugin);
		verify(sobjPA, times(1)).getPlugin(pInfo);
	}
	
	@Test
	public void testSyncPluginPlugin() throws Exception {
		initPView();
		
		StoredProject selProject = new StoredProject();
		String reqValSyncPlugin = "reqValSyncPlugin";
		PluginInfo pInfo = new PluginInfo();

		when(sobjPA.getPluginInfo(reqValSyncPlugin)).thenReturn(pInfo);
		when(sobjPA.getPlugin(pInfo)).thenReturn(pObj);
		
		Whitebox.invokeMethod(pView, "syncPlugin", selProject, reqValSyncPlugin);

		verify(sobjPA, times(1)).getPluginInfo(reqValSyncPlugin);
		verify(sobjPA, times(1)).getPlugin(pInfo);
		verify(compMA, times(1)).syncMetric(pObj, selProject);
	}
	
	@Test
	public void testTriggerAllUpdateNode() throws Exception {
		initPView();
		
		HashSet<StoredProject> projects = new HashSet<StoredProject>();
		projects.add(new StoredProject());
		projects.add(new StoredProject());
		
		ClusterNode node = new ClusterNode();
		node.setProjects(projects);
		PowerMockito.mockStatic(ClusterNode.class);
		when(ClusterNode.thisNode()).thenReturn(node);

		Whitebox.invokeMethod(pView, "triggerAllUpdateNode");
		
		verify(aa, times(2)).addArg("project", 0L);
	}
	
//    private StoredProject removeProject(StoredProject project) {
//    	if (project != null) {
//			// Deleting large projects in the foreground is very slow
//			ProjectDeleteJob pdj = new ProjectDeleteJob(sobjCore, project);
//			try {
//				sobjSched.enqueue(pdj);
//			} catch (SchedulerException e1) {
//				errors.add(getErr("e0034"));
//			}
//			project = null;
//		} else {
//			errors.add(getErr("e0034"));
//		}
//    }
	
	@Test
	public void testRemoveProjectNull() throws Exception {
		initPView();		
		Whitebox.invokeMethod(pView, "removeProject", (StoredProject)null);
		
		assertEquals(pView.getErrors().get(0), "You must select a project first!");
	}
	@Test
	public void testRemoveProjectQueuingFails() throws Exception {
		initPView();
		StoredProject project = new StoredProject();		
		doThrow(new SchedulerException("mock sched exception"))
			.when(sobjSched).enqueue(any(ProjectDeleteJob.class));
		
		Whitebox.invokeMethod(pView, "removeProject", (StoredProject)project);
		assertEquals(pView.getErrors().get(0), "You must select a project first!");
	}
	@Test
	public void testRemoveProject() throws Exception {
		initPView();
		StoredProject project = new StoredProject();		
		
		Whitebox.invokeMethod(pView, "removeProject", (StoredProject)project);
		assertEquals(pView.getErrors().size(), 0);
		verify(sobjSched, times(1)).enqueue(any(Job.class));
	}


	
	@Test
	public void testTriggerUpdateNull() throws Exception {
		initPView();
		
		StoredProject project = new StoredProject();
		Whitebox.invokeMethod(pView, "triggerUpdate", project, null);

		verify(aa, times(1)).addArg("project", project.getId());
		verify(as, times(1)).execute(aa);
		verify(aa, times(1)).results();
	}
	
	@Test
	public void testTriggerUpdate() throws Exception {
		initPView();
		
		StoredProject project = new StoredProject();
		when(aa.hasErrors()).thenReturn(true);
		
		Whitebox.invokeMethod(pView, "triggerUpdate", project, "");

		verify(aa, times(1)).addArg("project", project.getId());
		verify(aa, times(1)).addArg("updater", "");
		verify(as, times(1)).execute(aa);
		verify(aa, times(1)).errors();
	}
	
	@Test
	public void testTriggerUpdateSingleArg() throws Exception {
		initPView();
		
		StoredProject project = new StoredProject();
		Whitebox.invokeMethod(pView, "triggerUpdate", project);		
		verify(aa, times(1)).addArg("project", project.getId());
	}
	
	@Test
	public void testGetProjects() {
		ClusterNode clusterNode = new ClusterNode();
		
		PowerMockito.mockStatic(ClusterNode.class);
		when(ClusterNode.thisNode()).thenReturn(clusterNode);

		assertEquals(clusterNode.getProjects(), ProjectsView.getProjects());
	}
	

	@Test
	public void testGetLastProjectVersionNull() {
		ProjectsView.initResources(null);
		assertEquals(ProjectsView.getLastProjectVersion(null), "n/a");
	}

	@Test
	public void testGetLastProjectVersionNonExisting() {
		PowerMockito.mockStatic(ProjectVersion.class);
		when(ProjectVersion.getLastProjectVersion(any(StoredProject.class)))
			.thenReturn(null);
		
		StoredProject project = new StoredProject();
		ProjectsView.initResources(null);
		
		assertEquals(ProjectsView.getLastProjectVersion(project), "n/a");
	}
	

	@Test
	public void testGetLastProjectVersion() {
		ProjectVersion pv = new ProjectVersion();
		pv.setRevisionId("1.0.4");
		StoredProject project = new StoredProject();
		
		ProjectsView.initResources(null);
		
		PowerMockito.mockStatic(ProjectVersion.class);
		when(ProjectVersion.getLastProjectVersion(project))
			.thenReturn(pv);
		
		assertEquals("0(1.0.4)", ProjectsView.getLastProjectVersion(project));
	}

	@Test
	public void testGetLastBugNull() {
		ProjectsView.initResources(null);
		assertEquals(ProjectsView.getLastBug(null), "n/a");
	}

	@Test
	public void testGetLastBugNonExisting() {
		StoredProject project = new StoredProject();
		
		ProjectsView.initResources(null);

		PowerMockito.mockStatic(Bug.class);
		when(Bug.getLastUpdate(project)).thenReturn(null);
		
		assertEquals("n/a", ProjectsView.getLastBug(project));
	}
	
	@Test
	public void testGetLastBug() {
		StoredProject project = new StoredProject();
		Bug bug = new Bug();
		bug.setBugID("Mock bug ID");
		
		ProjectsView.initResources(null);

		PowerMockito.mockStatic(Bug.class);
		when(Bug.getLastUpdate(project)).thenReturn(bug);
		
		assertEquals("Mock bug ID", ProjectsView.getLastBug(project));
	}
	

	@Test
	public void testGetLastEmailDateNull() {
		ProjectsView.initResources(null);
		assertEquals("n/a", ProjectsView.getLastEmailDate(null));
	}
	@Test
	public void testGetLastEmailDateNonExisting() {
		StoredProject project = new StoredProject();
		
		ProjectsView.initResources(null);

		PowerMockito.mockStatic(MailMessage.class);
		when(MailMessage.getLatestMailMessage(project)).thenReturn(null);
		
		assertEquals("n/a", ProjectsView.getLastEmailDate(project));		
	}
	
	@Test
	public void testGetLastEmailDate() {
		StoredProject project = new StoredProject();
		MailMessage mm = new MailMessage();
		Calendar cal = Calendar.getInstance();
		cal.set(2015, 1, 12, 15, 23, 10);		
		mm.setSendDate(cal.getTime());
		
		ProjectsView.initResources(null);

		PowerMockito.mockStatic(MailMessage.class);
		when(MailMessage.getLatestMailMessage(project)).thenReturn(mm);
		
		assertEquals("Thu Feb 12 15:23:10 CET 2015", ProjectsView.getLastEmailDate(project));		
	}

	@Test
	public void testGetEvalStateNull() {
		ProjectsView.initResources(null);
		assertEquals("No", ProjectsView.getEvalState(null));
	}

	@Test
	public void testGetEvalStateNo() {
		when(projectMock.isEvaluated()).thenReturn(false);
		ProjectsView.initResources(null);

		assertEquals("No", ProjectsView.getEvalState(projectMock));	
	}

	@Test
	public void testGetEvalStateYes() {
		when(projectMock.isEvaluated()).thenReturn(true);
		ProjectsView.initResources(null);

		assertEquals("Yes", ProjectsView.getEvalState(projectMock));	
	}
	
	@Test
	public void testGetClusternodeNull() {
		ProjectsView.initResources(null);
		assertEquals("n/a", ProjectsView.getClusternode(null));
	}
	@Test
	public void testGetClusternodeLocal() {
		ProjectsView.initResources(null);
		when(projectMock.getClusternode()).thenReturn(null);
		
		assertEquals("(local)", ProjectsView.getClusternode(projectMock));
	}
	@Test
	public void testGetClusternodeRemote() {
		ProjectsView.initResources(null);
		when(projectMock.getClusternode()).thenReturn(new ClusterNode("Mock node name"));
		
		assertEquals("Mock node name", ProjectsView.getClusternode(projectMock));
	}

	@Test
	public void testGetUpdatersNull() {
		ProjectsView.initResources(null);
		Whitebox.setInternalState(ProjectsView.class, sobjUpdater);
		
		assertTrue(ProjectsView.getUpdaters(null, "default").isEmpty());
	}

	@Test
	public void testGetUpdaters() {
		StoredProject project = new StoredProject();
		ProjectsView.initResources(null);
		Whitebox.setInternalState(ProjectsView.class, sobjUpdater);
		
		HashSet<Updater> defaultSet = new HashSet<Updater>(Arrays.asList((Updater)null));
		HashSet<Updater> inferencetSet = new HashSet<Updater>(Arrays.asList((Updater)null, (Updater)null));
		HashSet<Updater> importSet = new HashSet<Updater>(Arrays.asList((Updater)null, (Updater)null, (Updater)null));
		HashSet<Updater> parseSet = new HashSet<Updater>();
		
		when(sobjUpdater.getUpdaters(project, UpdaterStage.DEFAULT)).thenReturn(defaultSet);
		when(sobjUpdater.getUpdaters(project, UpdaterStage.INFERENCE)).thenReturn(inferencetSet);
		when(sobjUpdater.getUpdaters(project, UpdaterStage.IMPORT)).thenReturn(importSet);
		when(sobjUpdater.getUpdaters(project, UpdaterStage.PARSE)).thenReturn(parseSet);

		assertEquals(defaultSet, ProjectsView.getUpdaters(project, "default"));
		assertEquals(inferencetSet, ProjectsView.getUpdaters(project, "inference"));
		assertEquals(importSet, ProjectsView.getUpdaters(project, "import"));
		assertEquals(parseSet, ProjectsView.getUpdaters(project, "parse"));
	}
	
	
	
	@Test
	public void testGetClusterName() {
		String clusterNodeName = "Mock cluster node name";
		Whitebox.setInternalState(ProjectsView.class, sobjClusterNode);
		when(sobjClusterNode.getClusterNodeName()).thenReturn(clusterNodeName);
    	
		assertEquals(ProjectsView.getClusterName(), clusterNodeName);
    }
	
	@Test
	public void testGetErrors() {
		initPView();
		ArrayList<String> errors = new ArrayList<String>(Arrays.asList("dummy error"));
		Whitebox.setInternalState(pView, errors);
		
		assertEquals(errors, pView.getErrors());
	}
}
