package eu.sqooss.test.service.webadmin.projects;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.anyString;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import eu.sqooss.impl.service.webadmin.servlets.ProjectsServlet;
import eu.sqooss.service.admin.AdminAction;
import eu.sqooss.service.admin.AdminService;
import eu.sqooss.service.admin.actions.AddProject;
import eu.sqooss.test.service.webadmin.AbstractWebadminServletTest;

@RunWith(PowerMockRunner.class)
public class AddProjectTest extends AbstractWebadminServletTest {

    private ProjectsServlet testee;
    @Mock private AdminService mockAS;
    @Mock private AdminAction mockAA;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        
        // Mock admin service
        when(mockAC.getAdminService()).thenReturn(mockAS);
        
        // Initialize servlet
        testee = new ProjectsServlet(ve, mockAC);
    }
    
    // Test the adding of a project by its project.properties file
    @Test
    public void testAddProject() throws InstantiationException, IllegalAccessException, ServletException, IOException {
        when(mockReq.getRequestURI()).thenReturn("/projects/action");
        when(mockReq.getMethod()).thenReturn("GET");
        
        // Mock request parameters
        when(mockReq.getParameter("action")).thenReturn("installbyform");
        when(mockReq.getParameter("REQ_PAR_PRJ_CODE")).thenReturn("Scm1");
        when(mockReq.getParameter("REQ_PAR_PRJ_NAME")).thenReturn("Name1");
        when(mockReq.getParameter("REQ_PAR_PRJ_BUG")).thenReturn("Bug1");
        when(mockReq.getParameter("REQ_PAR_PRJ_BTS")).thenReturn("Bts1");
        when(mockReq.getParameter("REQ_PAR_PRJ_MAIL")).thenReturn("Mail1");
        when(mockReq.getParameter("REQ_PAR_PRJ_WEB")).thenReturn("Web1");
        
        // Create an action
        Class<? extends AdminAction> clazz = AddProject.class;
        AdminAction aa = clazz.newInstance();
        when(mockAS.create(anyString())).thenReturn(aa);        
        
        // Do the fake request
        testee.service(mockReq, mockResp);
        
        // Check whether the admin action is executed
        assertEquals(aa.args().get("scm"), "Scm1");
        assertEquals(aa.args().get("name"), "Name1");
        assertEquals(aa.args().get("bug"), "Bug1");
        assertEquals(aa.args().get("bts"), "Bts1");
        assertEquals(aa.args().get("mail"), "Mail1");
        assertEquals(aa.args().get("web"), "Web1");
        verify(mockAS).execute(aa);
    }
    
    // Test the adding of a project by manually entered properties
    @Test
    public void testAddProjectByProperties() throws InstantiationException, IllegalAccessException, ServletException, IOException {
        
        when(mockReq.getRequestURI()).thenReturn("/projects/action");
        when(mockReq.getMethod()).thenReturn("GET");
        
        // Mock request parameters
        when(mockReq.getParameter("action")).thenReturn("installbyproperties");
        when(mockReq.getParameter("properties")).thenReturn("Dir1");
        
        // Create an action
        Class<? extends AdminAction> clazz = AddProject.class;
        AdminAction aa = clazz.newInstance();
        when(mockAS.create(anyString())).thenReturn(aa);        
        
        // Do the fake request
        testee.service(mockReq, mockResp);
        
        // Check whether the admin action is executed
        assertEquals(aa.args().get("dir"), "Dir1");
        verify(mockAS).execute(aa);
    }
}
