package eu.sqooss.test.service.webadmin.misc;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import eu.sqooss.impl.service.webadmin.servlets.PropertiesServlet;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.test.service.webadmin.AbstractWebadminServletTest;

@RunWith(PowerMockRunner.class)
public class PropertiesTest extends AbstractWebadminServletTest {

    private PropertiesServlet testee;
    @Mock private PluginAdmin mockPA;
    @Mock private PluginInfo mockSelPlugin;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        
        // Mock pluginadmin
        when(mockAC.getPluginAdmin()).thenReturn(mockPA);
        
        // Initialize servlet
        testee = new PropertiesServlet(ve, mockAC);
    }
    
    // Test the displaying of logs
    @Test
    public void testDisplay() throws ServletException, IOException {
        when(mockReq.getRequestURI()).thenReturn("/properties");
        when(mockReq.getMethod()).thenReturn("GET");
                
        // Create properties that should be displayed
        PluginInfo plugin = new PluginInfo();
        plugin.setPluginName("Plugin1");
        Set<PluginConfiguration> conf = new HashSet<PluginConfiguration>();
        PluginConfiguration c = new PluginConfiguration();
        c.setId(1);
        c.setName("confName1");
        c.setMsg("confMsg1");
        c.setType("INTEGER");
        c.setValue("confValue1");
        conf.add(c);
        plugin.setPluginConfiguration(conf);
        when(mockReq.getParameter("propertyId")).thenReturn("1");
        when(mockReq.getParameter("hash")).thenReturn("thisishashcode");
        when(mockPA.getPluginInfo("thisishashcode")).thenReturn(plugin);

        // Do the fake request
        testee.service(mockReq, mockResp);

        // Get the output
        String output = getResponseOutput();
        
        // Check whether the entries occur in the output
        assertTrue(output.contains("Plugin1"));
        assertTrue(output.contains("confName1"));
        assertTrue(output.contains("confMsg1"));
        assertTrue(output.contains("confValue1"));
        assertTrue(output.contains("selected"));
    }
    
    // Test the creating of properties
    @Test
    public void testCreatingProperties() throws Exception {
        when(mockReq.getRequestURI()).thenReturn("/properties/action");
        when(mockReq.getMethod()).thenReturn("GET");
        
        when(mockReq.getParameter("hash")).thenReturn("thisishashcode");
        when(mockReq.getParameter("action")).thenReturn("create");
        when(mockReq.getParameter("reqParPropName")).thenReturn("Name1");
        when(mockReq.getParameter("reqParPropDescr")).thenReturn("Description1");
        when(mockReq.getParameter("reqParPropType")).thenReturn("Type1");
        when(mockReq.getParameter("reqParPropValue")).thenReturn("Value1");
        when(mockPA.getPluginInfo("thisishashcode")).thenReturn(mockSelPlugin);
        
        // Do the fake request
        testee.service(mockReq, mockResp);
        
        // Check whether the addConfigEntry method is invoked with the correct parameters
        verify(mockSelPlugin).addConfigEntry((DBService) any(), eq("Name1"), eq("Description1"), eq("Type1"), eq("Value1"));
    }
    
    // Test the updating of properties
    @Test
    public void testUpdatingProperties() throws Exception {
        when(mockReq.getRequestURI()).thenReturn("/properties/action");
        when(mockReq.getMethod()).thenReturn("GET");
        
        when(mockReq.getParameter("hash")).thenReturn("thisishashcode");
        when(mockReq.getParameter("action")).thenReturn("update");
        when(mockReq.getParameter("propertyId")).thenReturn("641");
        when(mockReq.getParameter("reqParPropValue")).thenReturn("Value2");
        when(mockPA.getPluginInfo("thisishashcode")).thenReturn(mockSelPlugin);
        
        // Initialize the PluginConfiguration that should be updated
        Set<PluginConfiguration> configurations = new HashSet<PluginConfiguration>();
        PluginConfiguration c = new PluginConfiguration();
        c.setId(641l);
        c.setName("Name2");
        configurations.add(c);
        when(mockSelPlugin.getConfiguration()).thenReturn(configurations);
        
        // Do the fake request
        testee.service(mockReq, mockResp);
        
        // Check whether the updateConfigEntry method is invoked with the correct parameters
        verify(mockSelPlugin).updateConfigEntry((DBService) any(), eq("Name2"), eq("Value2"));
    }
    
}
