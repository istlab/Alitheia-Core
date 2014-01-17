package eu.sqooss.test.service.webadmin.projects;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.osgi.framework.ServiceReference;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import eu.sqooss.impl.service.webadmin.servlets.ProjectsServlet;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.admin.AdminAction;
import eu.sqooss.service.admin.AdminService;
import eu.sqooss.service.admin.actions.UpdateProject;
import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.test.service.webadmin.AbstractWebadminServletTest;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ClusterNode.class)
public class ProjectActionTest extends AbstractWebadminServletTest {

    private ProjectsServlet testee;
    @Mock private Scheduler mockSched;
    @Mock private MetricActivator mockMA;
    @Mock private PluginAdmin mockPA;
    @Mock private AdminService mockAS;
    @Mock private AdminAction mockAA;
    @Mock private ClusterNode mockCN;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        
        // Mock services
        when(mockAC.getScheduler()).thenReturn(mockSched);
        when(mockAC.getMetricActivator()).thenReturn(mockMA);
        when(mockAC.getPluginAdmin()).thenReturn(mockPA);
        when(mockAC.getAdminService()).thenReturn(mockAS);
        
        // Initialize servlet
        testee = new ProjectsServlet(ve, mockAC);
    }
    
    // Test the deleting of a project
    @Test
    public void testDeleteProject() throws InstantiationException, IllegalAccessException, ServletException, IOException {
        when(mockReq.getRequestURI()).thenReturn("/projects/action");
        when(mockReq.getMethod()).thenReturn("GET");
        
        // Mock request parameters
        when(mockReq.getParameter("action")).thenReturn("delete");
        when(mockReq.getParameter("REQ_PAR_PROJECT_ID")).thenReturn("671");
        StoredProject project = new StoredProject(); 
        when(mockDB.findObjectById(StoredProject.class, 671l)).thenReturn(project);        
        
        // Do the fake request
        testee.service(mockReq, mockResp);
        
        // Get the output
        String output = stripHTMLandWhitespace(getResponseOutput());
        
        // Check whether the output contains 'success' and not contains 'error'
        assertTrue(output.contains("Success"));
        assertFalse(output.contains("Error"));
    }
    
    // Test the running of a specific updater on a project
    @Test
    public void testRunUpdate() throws ServletException, IOException, InstantiationException, IllegalAccessException {
        when(mockReq.getRequestURI()).thenReturn("/projects/action");
        when(mockReq.getMethod()).thenReturn("GET");
        
        // Mock request parameters
        when(mockReq.getParameter("action")).thenReturn("triggerupdate");
        when(mockReq.getParameter("REQ_PAR_PROJECT_ID")).thenReturn("651");
        when(mockReq.getParameter("reqUpd")).thenReturn("Updater1");
        StoredProject project = new StoredProject(); 
        when(mockDB.findObjectById(StoredProject.class, 651l)).thenReturn(project);
        
        // Create an action
        Class<? extends AdminAction> clazz = UpdateProject.class;
        AdminAction aa = clazz.newInstance();
        when(mockAS.create(UpdateProject.MNEMONIC)).thenReturn(aa);        
        
        // Do the fake request
        testee.service(mockReq, mockResp);
        
        // Check whether the admin action is executed
        assertEquals(aa.args().get("updater"), "Updater1");
        verify(mockAS).execute(aa);
    }
    
    // Test the running of all updaters on a project
    @Test
    public void testRunAllUpdate() throws InstantiationException, IllegalAccessException, ServletException, IOException {
        when(mockReq.getRequestURI()).thenReturn("/projects/action");
        when(mockReq.getMethod()).thenReturn("GET");
        
        // Mock request parameters
        when(mockReq.getParameter("action")).thenReturn("triggerallupdate");
        when(mockReq.getParameter("REQ_PAR_PROJECT_ID")).thenReturn("651");
        when(mockReq.getParameter("reqUpd")).thenReturn("Updater1");
        StoredProject project = new StoredProject(); 
        when(mockDB.findObjectById(StoredProject.class, 651l)).thenReturn(project);
        
        // Create an action
        Class<? extends AdminAction> clazz = UpdateProject.class;
        AdminAction aa = clazz.newInstance();
        when(mockAS.create(UpdateProject.MNEMONIC)).thenReturn(aa);        
        
        // Do the fake request
        testee.service(mockReq, mockResp);
        
        // Check whether the admin action is executed
        assertNotSame(aa.args().get("updater"), "Updater1");
        verify(mockAS).execute(aa);
    }
    
    // Test the running of all updaters on all projects
    @Test
    public void testRunAllUpdateNode() throws InstantiationException, IllegalAccessException, ServletException, IOException {
        when(mockReq.getRequestURI()).thenReturn("/projects/action");
        when(mockReq.getMethod()).thenReturn("GET");
        
        // Mock request parameters
        when(mockReq.getParameter("action")).thenReturn("triggerallupdatenode");
        PowerMockito.mockStatic(ClusterNode.class);
        when(ClusterNode.thisNode()).thenReturn(mockCN);
        Set<StoredProject> projects = new HashSet<StoredProject>();
        StoredProject project = new StoredProject();
        project.setId(661l);
        projects.add(project);
        when(mockCN.getProjects()).thenReturn(projects);
        
        // Create an action
        Class<? extends AdminAction> clazz = UpdateProject.class;
        AdminAction aa = clazz.newInstance();
        when(mockAS.create(UpdateProject.MNEMONIC)).thenReturn(aa);        
        
        // Do the fake request
        testee.service(mockReq, mockResp);
        
        // Check whether the admin action is executed
        assertEquals((long) aa.args().get("project"), 661l);
        verify(mockAS).execute(aa);
    }

}
