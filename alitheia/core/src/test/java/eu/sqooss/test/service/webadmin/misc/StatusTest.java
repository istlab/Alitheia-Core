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

import eu.sqooss.impl.service.webadmin.servlets.PluginsServlet;
import eu.sqooss.impl.service.webadmin.servlets.StatusServlet;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerStats;
import eu.sqooss.test.service.webadmin.AbstractWebadminServletTest;

@RunWith(PowerMockRunner.class)
public class StatusTest extends AbstractWebadminServletTest {

    private StatusServlet testee;
    @Mock private Scheduler mockSche;
    @Mock private SchedulerStats stats;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        
        // Mock scheduler
        when(mockAC.getScheduler()).thenReturn(mockSche);
        
        // Initialize servlet
        testee = new StatusServlet(ve, mockAC);
    }
    
    // Test the displaying of the uptime
    // Test the displaying of the queue length
    @Test
    public void testDisplay() throws ServletException, IOException {
        when(mockReq.getRequestURI()).thenReturn("/status");
        when(mockReq.getMethod()).thenReturn("GET");
        
        // We expect these number of waiting jobs
        when(mockSche.getSchedulerStats()).thenReturn(stats);
        when(stats.getWaitingJobs()).thenReturn(741l);
        
        // Do the fake request
        testee.service(mockReq, mockResp);

        // Get the output
        String output = stripHTMLandWhitespace(getResponseOutput());

        // Verify that the uptime is displayed
        // Warning: this fails if the test takes longer than a minute
        assertTrue(output.matches(".*0:00:00:[0-9]{2}.*"));
        
        // Verify that there are 741 waiting jobs
        // Warning: this is always true when the number 741 occurs in the HTML code itself
        // In that case change this number to a unique number 
        assertTrue(output.matches(".*741.*"));
    }
    
    //TODO: Test the displaying of the executing jobs
    // Test the displaying of the waiting jobs
    // Test the displaying of the failed jobs
    // Test the displaying of the total jobs
    // Test the displaying of the threads
    @Test
    public void testDisplayJobInformation() throws ServletException, IOException {
        when(mockReq.getRequestURI()).thenReturn("/status");
        when(mockReq.getMethod()).thenReturn("GET");
        
        // We expect these number of waiting jobs
        when(mockSche.getSchedulerStats()).thenReturn(stats);
        when(stats.getRunningJobs()).thenReturn(751l);
        when(stats.getWaitingJobs()).thenReturn(761l);
        when(stats.getFailedJobs()).thenReturn(771l);
        when(stats.getTotalJobs()).thenReturn(781l);
        when(stats.getWorkerThreads()).thenReturn(791l);
        
        // Do the fake request
        testee.service(mockReq, mockResp);

        // Get the output
        String output = stripHTMLandWhitespace(getResponseOutput());
        
        // Verify that the number are equal
        // Warning: this is always true when the number 751, 761, 771, 781 
        // and 791 occur in the HTML code itself. In that case change this 
        // number to a unique number.
        assertTrue(output.matches(".*751.*"));
        assertTrue(output.matches(".*761.*"));
        assertTrue(output.matches(".*771.*"));
        assertTrue(output.matches(".*781.*"));
        assertTrue(output.matches(".*791.*"));
    }
}
