package eu.sqooss.test.service.webadmin;

import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;

import org.apache.velocity.VelocityContext;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.osgi.framework.BundleContext;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import eu.sqooss.impl.service.webadmin.LogsView;
import eu.sqooss.service.logging.LogManager;

@RunWith(PowerMockRunner.class)
public class LogsViewTest {

	@Mock
	LogManager sobjLogManager;
	@Mock 
	BundleContext bc;
	@Mock
	HttpServletRequest req;

	String newline = "\n";

	@BeforeClass
	public static void setUp() {
	}
	
	@Test
	public void testExec() {
		LogsView lv = new LogsView(bc, new VelocityContext());
		Assert.assertNotNull(lv);
		lv.exec(req);
	}
	
	@Test
	public void testGetLogsNone() {
		Whitebox.setInternalState(LogsView.class, sobjLogManager);
		 when(sobjLogManager.getRecentEntries()).thenReturn(new String[0]);
		 Assert.assertTrue(LogsView.getLogs().size() == 0);
		
		 when(sobjLogManager.getRecentEntries()).thenReturn(null);
		 Assert.assertTrue(LogsView.getLogs().size() == 0);
	}
	
	@Test
	public void testGetLogsMultiple() {
		Whitebox.setInternalState(LogsView.class, sobjLogManager);
		 String[] logEntries = {"Log entry 1", "Log entry 2"};
		 when(sobjLogManager.getRecentEntries()).thenReturn(logEntries);
		 Assert.assertTrue(LogsView.getLogs().size() == 2);
		 Assert.assertEquals("Log entry 1", LogsView.getLogs().get(0));
	}

}