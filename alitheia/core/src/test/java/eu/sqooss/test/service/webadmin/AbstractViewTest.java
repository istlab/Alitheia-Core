package eu.sqooss.test.service.webadmin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Enumeration;
import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.AbstractView;
import eu.sqooss.impl.service.webadmin.PluginsView;
import eu.sqooss.impl.service.webadmin.TranslationProxy;
import eu.sqooss.service.db.Bug;
import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.MailMessage;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.tds.TDSService;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Job.class,AlitheiaCore.class,StoredProject.class,ClusterNode.class,ProjectVersion.class,MailMessage.class,Bug.class})
public class AbstractViewTest extends AbstractViewTestBase{

	AbstractView abstractView;
	BundleContext bundleContext;
	TranslationProxy translation;
	
	@Before
	public void setUp() throws Exception {
		// setup translation
		translation = new TranslationProxy(Locale.ENGLISH);
		
		bundleContext = Mockito.mock(BundleContext.class);
		velocityContext = Mockito.mock(VelocityContext.class);
		abstractView = new PluginsView(bundleContext, velocityContext);
		super.setUp(abstractView);
	}

	@After
	public void tearDown() throws Exception {
		bundleContext = null;
		velocityContext = null;
		abstractView = null;
		super.tearDown();
	}

	@Test
	public void testInitResources() {
		abstractView = new PluginsView(bundleContext,velocityContext);
		abstractView.initErrorResources(Locale.ENGLISH);
		
	}
	
	@Test
	public void testConstructor(){
		
		Mockito.when(alitheiaCore.getDBService()).thenReturn(null);
		Mockito.when(alitheiaCore.getPluginAdmin()).thenReturn(null);
		Mockito.when(alitheiaCore.getScheduler()).thenReturn(null);
		Mockito.when(alitheiaCore.getMetricActivator()).thenReturn(null);
		Mockito.when(alitheiaCore.getTDSService()).thenReturn(null);
		Mockito.when(alitheiaCore.getUpdater()).thenReturn(null);
		Mockito.when(alitheiaCore.getClusterNodeService()).thenReturn(null);
		Mockito.when(alitheiaCore.getSecurityManager()).thenReturn(null);
		Mockito.when(alitheiaCore.getLogManager()).thenReturn(logManager);
		when(logManager.createLogger(Logger.NAME_SQOOSS_WEBADMIN)).thenReturn(logger);
		
		abstractView = new PluginsView(bundleContext,velocityContext);

		Mockito.verify(alitheiaCore).getDBService();
		Mockito.verify(alitheiaCore).getPluginAdmin();
		Mockito.verify(alitheiaCore).getScheduler();
		Mockito.verify(alitheiaCore).getMetricActivator();
		Mockito.verify(alitheiaCore).getTDSService();
		Mockito.verify(alitheiaCore).getUpdater();
		Mockito.verify(alitheiaCore).getClusterNodeService();
		Mockito.verify(alitheiaCore).getSecurityManager();
		Mockito.verify(alitheiaCore).getLogManager();
		Mockito.verify(logger,Mockito.atLeast(7)).debug(Mockito.anyString());


	}
	
	@Test
	public void testConstructorLogger(){
		Mockito.when(alitheiaCore.getDBService()).thenReturn(null);
		Mockito.when(alitheiaCore.getPluginAdmin()).thenReturn(null);
		Mockito.when(alitheiaCore.getScheduler()).thenReturn(null);
		Mockito.when(alitheiaCore.getMetricActivator()).thenReturn(null);
		Mockito.when(alitheiaCore.getTDSService()).thenReturn(null);
		Mockito.when(alitheiaCore.getUpdater()).thenReturn(null);
		Mockito.when(alitheiaCore.getClusterNodeService()).thenReturn(null);
		Mockito.when(alitheiaCore.getSecurityManager()).thenReturn(null);
		Mockito.when(alitheiaCore.getLogManager()).thenReturn(null);
		
		abstractView = new PluginsView(bundleContext,velocityContext);
		Mockito.verify(alitheiaCore).getDBService();
		Mockito.verify(alitheiaCore).getPluginAdmin();
		Mockito.verify(alitheiaCore).getScheduler();
		Mockito.verify(alitheiaCore).getMetricActivator();
		Mockito.verify(alitheiaCore).getTDSService();
		Mockito.verify(alitheiaCore).getUpdater();
		Mockito.verify(alitheiaCore).getClusterNodeService();
		Mockito.verify(alitheiaCore).getSecurityManager();
		Mockito.verify(alitheiaCore).getLogManager();

	}
	
	class MsgResourceBundle extends ListResourceBundle {
        private Object[][] contents = new Object[][]{
           {"resLbl","resLbl string"},
           {"resErr","resErr string"},
           {"resMsg","resMsg string"}
        };
        protected Object[][] getContents() {
            return contents;
        }         
	};

	@Test
	public void testGetLbl() throws Exception{
		assertEquals("test",translation.label("test"));
	}
	
	@Test
	public void testGetErr() throws Exception{
		abstractView = new PluginsView(bundleContext,velocityContext);
		assertEquals("test",abstractView.getErr("test"));
		abstractView.initErrorResources(Locale.ENGLISH);
		assertEquals("test",abstractView.getErr("test"));

		ResourceBundle resourceBundle = new MsgResourceBundle();
		Whitebox.setInternalState(abstractView, "resErr", resourceBundle);
		assertEquals("resErr string",abstractView.getErr("resErr"));
		
		// resLbl = null
		Whitebox.setInternalState(abstractView, "resErr", (ResourceBundle)null);
		assertEquals("resErr is null", abstractView.getErr("resErr is null"));
	}
	
	@Test
	public void testGetMsg() throws Exception{
		assertEquals("message", translation.message("message"));
	}
	
	@Test
	public void testDebugRequest() throws Exception {
		abstractView = new PluginsView(bundleContext,velocityContext);
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		
		Vector<String> v = new Vector<String>();
		v.add("elementOne");
		v.add("elementTwo");
		v.add("elementThree");
		
		Enumeration<?> e = v.elements();
		when(request.getParameterNames()).thenReturn(e);
		when(request.getParameter("elementOne")).thenReturn("elementOneOut");
		when(request.getParameter("elementTwo")).thenReturn("elementTwoOut");
		when(request.getParameter("elementThree")).thenReturn("elementThreeOut");
		
		String out = Whitebox.invokeMethod(abstractView, "debugRequest",request);
		String expected = 
			"elementOne=elementOneOut<br/>\n"+
			"elementTwo=elementTwoOut<br/>\n"+
			"elementThree=elementThreeOut<br/>\n";
		assertEquals(expected, out);
	}
	
	@Test
	public void testSp() throws Exception {
		abstractView = new PluginsView(bundleContext,velocityContext);
		assertEquals("", Whitebox.<String>invokeMethod(abstractView,"sp",0l));
		assertEquals("  ", Whitebox.<String>invokeMethod(abstractView,"sp",1l));
		assertEquals("    ", Whitebox.<String>invokeMethod(abstractView,"sp",2l));
	}
	
	@Test
	public void testFromString() throws Exception {
		abstractView = new PluginsView(bundleContext,velocityContext);
		assertEquals(new Long(0), Whitebox.<String>invokeMethod(abstractView,"fromString","0"));
		assertNull(Whitebox.<String>invokeMethod(abstractView,"fromString","not a number"));
	}
	
	@Test
	public void testCheckName() throws Exception {
		abstractView = new PluginsView(bundleContext,velocityContext);
		assertFalse(Whitebox.<Boolean>invokeMethod(abstractView,"checkName",""));
		assertFalse(Whitebox.<Boolean>invokeMethod(abstractView,"checkName",new String()));
		assertFalse(Whitebox.<Boolean>invokeMethod(abstractView,"checkName",(String)null));
		assertTrue(Whitebox.<Boolean>invokeMethod(abstractView,"checkName","a Valid Name 1"));
		assertTrue(Whitebox.<Boolean>invokeMethod(abstractView,"checkName","aValidName2"));
		assertFalse(Whitebox.<Boolean>invokeMethod(abstractView,"checkName"," name must not start with space"));
		assertFalse(Whitebox.<Boolean>invokeMethod(abstractView,"checkName","name must not end with space "));
	}
	
	@Test
	public void testCheckProjectName() throws Exception {
		abstractView = new PluginsView(bundleContext,velocityContext);
		assertFalse(Whitebox.<Boolean>invokeMethod(abstractView,"checkProjectName",""));
		assertFalse(Whitebox.<Boolean>invokeMethod(abstractView,"checkProjectName",new String()));
		assertFalse(Whitebox.<Boolean>invokeMethod(abstractView,"checkProjectName",(String)null));
		assertTrue(Whitebox.<Boolean>invokeMethod(abstractView,"checkProjectName","a Valid Name 1"));
		assertTrue(Whitebox.<Boolean>invokeMethod(abstractView,"checkProjectName","aValidName2"));
		assertTrue(Whitebox.<Boolean>invokeMethod(abstractView,"checkProjectName","a_Valid_Name_3"));
		assertFalse(Whitebox.<Boolean>invokeMethod(abstractView,"checkProjectName"," name must not start with space"));
		assertFalse(Whitebox.<Boolean>invokeMethod(abstractView,"checkProjectName","name must not end with space "));
		assertFalse(Whitebox.<Boolean>invokeMethod(abstractView,"checkProjectName","_name must not start with underscore"));
		assertFalse(Whitebox.<Boolean>invokeMethod(abstractView,"checkProjectName","name must not end with underscore_"));
	}
	
	@Test
	public void testEmail() throws Exception {
		abstractView = new PluginsView(bundleContext,velocityContext);
		assertFalse(Whitebox.<Boolean>invokeMethod(abstractView,"checkEmail",(String)null));
		assertFalse(Whitebox.<Boolean>invokeMethod(abstractView,"checkEmail",""));
		assertFalse(Whitebox.<Boolean>invokeMethod(abstractView,"checkEmail","test@.nl"));
		assertFalse(Whitebox.<Boolean>invokeMethod(abstractView,"checkEmail","test.nl"));
		assertTrue(Whitebox.<Boolean>invokeMethod(abstractView,"checkEmail","test@myDomain.nl"));
		assertFalse(Whitebox.<Boolean>invokeMethod(abstractView,"checkEmail",".test@myDomain.nl"));
		assertFalse(Whitebox.<Boolean>invokeMethod(abstractView,"checkEmail",".test@.myDomain.nl"));
		assertFalse(Whitebox.<Boolean>invokeMethod(abstractView,"checkEmail","test@myDomain.nl."));
		assertFalse(Whitebox.<Boolean>invokeMethod(abstractView,"checkEmail","..test@myDomain.nl"));
	}
	
	@Test
	public void checkTDSUrl() throws Exception {
		abstractView = new PluginsView(bundleContext,velocityContext);
		TDSService tds = Mockito.mock(TDSService.class);
		Whitebox.setInternalState(abstractView, TDSService.class, tds);
		Whitebox.invokeMethod(abstractView, "checkTDSUrl","myUrl");
		verify(tds,times(1)).isURLSupported("myUrl");
	}
	
	
}
