package eu.sqooss.test.service.webadmin;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.mockito.Matchers.anyString;


import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.powermock.reflect.Whitebox;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.AbstractView;
import eu.sqooss.service.admin.AdminAction;
import eu.sqooss.service.admin.AdminService;
import eu.sqooss.service.admin.actions.AddProject;
import eu.sqooss.service.admin.actions.UpdateProject;
import eu.sqooss.service.cluster.ClusterNodeService;
import eu.sqooss.service.db.Bug;
import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.MailMessage;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.updater.UpdaterService;

public abstract class AbstractViewTestBase {
	protected AlitheiaCore alitheiaCore;
	protected AdminService adminService;
	protected AdminAction adminAction;
	protected PluginAdmin pluginAdmin;
	protected VelocityContext velocityContext;
	protected StoredProject storedProject;
	protected Scheduler scheduler;
	protected LogManager logManager;
	protected ClusterNode clusterNode;
	protected MetricActivator metricActivator;
	protected Logger logger;
	protected ClusterNodeService clusterNodeService;
	protected UpdaterService updateService;
	protected MailMessage mailMessage;
	protected Bug bug;
	/**
	 * @throws java.lang.Exception
	 */
	public void setUp(AbstractView view) throws Exception {
		if(velocityContext == null){
			fail("VelocityContext should be mocked before calling super.setup(view)");
		}
		//create mocks
		mockStatic(AlitheiaCore.class);
		mockStatic(StoredProject.class);
		mockStatic(ClusterNode.class);
		mockStatic(ProjectVersion.class);
		mockStatic(MailMessage.class);
		mockStatic(Bug.class);
		
		alitheiaCore = mock(AlitheiaCore.class);
		adminService = mock(AdminService.class);
		adminAction = mock(AdminAction.class);
//		veclocityContext = mock(VelocityContext.class);
		storedProject = mock(StoredProject.class);
		scheduler = mock(Scheduler.class);
		clusterNode = mock(ClusterNode.class);
		pluginAdmin = mock(PluginAdmin.class);
		metricActivator = mock(MetricActivator.class);
		logger = mock(Logger.class);
		clusterNodeService = mock(ClusterNodeService.class);
		updateService = mock(UpdaterService.class);
		mailMessage = mock(MailMessage.class);
		bug = mock(Bug.class);
		logManager = mock(LogManager.class);
		
		//set private static fields
		Whitebox.setInternalState(view, VelocityContext.class, velocityContext);
		Whitebox.setInternalState(view, Scheduler.class, scheduler);
		Whitebox.setInternalState(view, PluginAdmin.class, pluginAdmin);
		Whitebox.setInternalState(view, MetricActivator.class, metricActivator);
		Whitebox.setInternalState(view, Logger.class, logger);
		Whitebox.setInternalState(view, ClusterNodeService.class, clusterNodeService);
		Whitebox.setInternalState(view, UpdaterService.class, updateService);
		Whitebox.setInternalState(view, LogManager.class, logManager);

		//define behavior public static method calls
		when(AlitheiaCore.getInstance()).thenReturn(alitheiaCore);
		when(StoredProject.getProjectByName(anyString())).thenReturn(storedProject);
		when(ClusterNode.thisNode()).thenReturn(clusterNode);
		when(MailMessage.getLatestMailMessage(any(StoredProject.class))).thenReturn(mailMessage);
		when(Bug.getLastUpdate(any(StoredProject.class))).thenReturn(bug);

		
		//define behavior public method calls
		when(alitheiaCore.getAdminService()).thenReturn(adminService);
		when(alitheiaCore.getPluginAdmin()).thenReturn(pluginAdmin);
		when(adminService.create(AddProject.MNEMONIC)).thenReturn(adminAction);
		when(adminService.create(UpdateProject.MNEMONIC)).thenReturn(adminAction);
		Map<String,Object> map = new HashMap<String,Object>();
		when(adminAction.results()).thenReturn(map);
		when(adminAction.errors()).thenReturn(null);
		when(clusterNodeService.getClusterNodeName()).thenReturn("ClusterNodeName");
		
		//call constructor
//		new ProjectsView(mock(BundleContext.class), mock(VelocityContext.class));
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	
	public void tearDown() throws Exception {
	}
}
