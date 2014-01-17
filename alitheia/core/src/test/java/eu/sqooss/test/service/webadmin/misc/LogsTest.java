package eu.sqooss.test.service.webadmin.misc;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import eu.sqooss.impl.service.webadmin.servlets.LogsServlet;
import eu.sqooss.impl.service.webadmin.servlets.PluginsServlet;
import eu.sqooss.impl.service.webadmin.servlets.StatusServlet;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerStats;
import eu.sqooss.test.service.webadmin.AbstractWebadminServletTest;

@RunWith(PowerMockRunner.class)
public class LogsTest extends AbstractWebadminServletTest {

    private LogsServlet testee;
    @Mock private LogManager mockLM;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        
        // Mock log manager
        when(mockAC.getLogManager()).thenReturn(mockLM);
        
        // Initialize servlet
        testee = new LogsServlet(ve, mockAC);
    }
    
    // Test the displaying of logs
    @Test
    public void testDisplay() throws ServletException, IOException {
        when(mockReq.getRequestURI()).thenReturn("/logs");
        when(mockReq.getMethod()).thenReturn("GET");
        
        // We expect these logs
        String[] recentEntries = new String[2];
        recentEntries[0] = "fzoevtvtow";
        recentEntries[1] = "byisqfovcq";
        when(mockLM.getRecentEntries()).thenReturn(recentEntries);
        
        // Do the fake request
        testee.service(mockReq, mockResp);

        // Get the output
        String output = stripHTMLandWhitespace(getResponseOutput());

        // Check whether the entries occur in the output
        assertTrue(output.matches(".*fzoevtvtow.*byisqfovcq.*"));
    }
    
}
