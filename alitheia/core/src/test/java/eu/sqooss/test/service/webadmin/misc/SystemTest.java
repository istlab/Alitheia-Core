package eu.sqooss.test.service.webadmin.misc;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.powermock.modules.junit4.PowerMockRunner;

import eu.sqooss.impl.service.webadmin.servlets.PluginsServlet;
import eu.sqooss.impl.service.webadmin.servlets.SystemServlet;
import eu.sqooss.test.service.webadmin.AbstractWebadminServletTest;

@RunWith(PowerMockRunner.class)
public class SystemTest extends AbstractWebadminServletTest {

    private SystemServlet testee;
    @Mock private BundleContext mockBC;
    @Mock private Bundle mockBundle;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        testee = new SystemServlet(ve, mockAC, mockBC);
    }
    
    // Test the stopping of the service
    @Test
    public void testStopService() throws ServletException, IOException, BundleException {
        when(mockReq.getRequestURI()).thenReturn("/system/shutdown");
        when(mockReq.getMethod()).thenReturn("GET");
        
        // Mock the bundle that should be stopped
        when(mockBC.getBundle(0)).thenReturn(mockBundle);
        
        // Do the fake request
        testee.service(mockReq, mockResp);
        
        // Verify that the service is stopped
        verify(mockBundle).stop();        
    }
}
