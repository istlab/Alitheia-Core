package eu.sqooss.test.service.webadmin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.TDSService;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AlitheiaCore.class)
public class AbstractViewTest extends AbstractViewTestBase{

	AbstractView abstractView;
	BundleContext bundleContext;
	VelocityContext velocityContext;
//	AlitheiaCore alitheiaCore;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		bundleContext = Mockito.mock(BundleContext.class);
		velocityContext = Mockito.mock(VelocityContext.class);
		alitheiaCore = Mockito.mock(AlitheiaCore.class);
		assertNotNull(alitheiaCore);
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
		abstractView.initResources(Locale.ENGLISH);
	}
	
	@Test
	public void testConstructor(){
		abstractView = new PluginsView(bundleContext,velocityContext);
		
		LogManager logManager = Mockito.mock(LogManager.class);
		Mockito.when(alitheiaCore.getLogManager()).thenReturn(logManager);
		Logger logger = Mockito.mock(Logger.class);
		Mockito.when(logManager.createLogger(Logger.NAME_SQOOSS_WEBADMIN)).thenReturn(logger);
		abstractView = new PluginsView(bundleContext,velocityContext);
		Mockito.verify(logger,Mockito.atLeast(7)).debug(Mockito.anyString());
		Mockito.verify(alitheiaCore).getDBService();
		Mockito.verify(alitheiaCore).getPluginAdmin();
		Mockito.verify(alitheiaCore).getScheduler();
		Mockito.verify(alitheiaCore).getMetricActivator();
		Mockito.verify(alitheiaCore).getTDSService();
		Mockito.verify(alitheiaCore).getUpdater();
		Mockito.verify(alitheiaCore).getClusterNodeService();
		Mockito.verify(alitheiaCore).getSecurityManager();

	}
	
	@Test
	public void testConstructorLogger(){
		abstractView = new PluginsView(bundleContext,velocityContext);
//		when(AlitheiaCore.getInstance()).thenReturn(alitheiaCore);

		
//		alitheiaCore = Mockito.mock(AlitheiaCore.class);
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
		abstractView = new PluginsView(bundleContext,velocityContext);

		assertEquals("test",abstractView.getLbl("test"));
		abstractView.initResources(Locale.ENGLISH);
		assertEquals("test",abstractView.getLbl("test"));

		ResourceBundle resourceBundle = new MsgResourceBundle();
		Whitebox.setInternalState(abstractView, "resLbl", resourceBundle);
		assertEquals("resLbl string",abstractView.getLbl("resLbl"));
		
		// resLbl = null
		Whitebox.setInternalState(abstractView, "resLbl", (ResourceBundle)null);
		assertEquals("resLbl is null", abstractView.getLbl("resLbl is null"));
		
//		code below fails. when resourceBundle.getString() is called it actually tries to 
//		execute it. This is not expected behavior.
//		Mockito.when(resourceBundle.getString("works")).thenReturn("it works");
//		Mockito.when(Mockito.mock(ResourceBundle.class).getString("works")).thenReturn("it works");
//		final ResourceBundle resourceBundle = Mockito.mock(ResourceBundle.class);
//		Mockito.when(resourceBundle.getString("error")).thenThrow(new NullPointerException("Null test"));
//		Mockito.when(resourceBundle.getString("works")).thenReturn("it works");
//		abstractView.setResLbl(resourceBundle);
//		AbstractView.setResLbl(resourceBundle);
//		assertEquals("Undefined parameter name!",AbstractView.getLbl("error"));
//		Mockito.verify(resourceBundle.getString("error"));
//		assertEquals("it works",AbstractView.getLbl("works"));
//		Mockito.verify(resourceBundle.getString("works"));
		//		abstractView = n;
//		AbstractView abstractView = PowerMockito.spy(new PluginsView(bundleContext,velocityContext));
        // use PowerMockito to set up your expectation
//		verifyPrivate(resourceBundle).invoke("privateMethodName", argument1);
//        PowerMockito.doReturn(value).when(classUnderTest, "methodToMock", "parameter1");
//		whenNew(ResourceBundle.class).withAnyArguments().thenReturn(resourceBundle);
	}
	
	@Test
	public void testGetErr() throws Exception{
		abstractView = new PluginsView(bundleContext,velocityContext);
		assertEquals("test",abstractView.getErr("test"));
		abstractView.initResources(Locale.ENGLISH);
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
		abstractView = new PluginsView(bundleContext,velocityContext);
		assertEquals("test",abstractView.getMsg("test"));
		abstractView.initResources(Locale.ENGLISH);
		assertEquals("test",abstractView.getMsg("test"));

		ResourceBundle resourceBundle = new MsgResourceBundle();
		Whitebox.setInternalState(abstractView, "resMsg", resourceBundle);
		assertEquals("resMsg string",abstractView.getMsg("resMsg"));
		
		// resLbl = null
		Whitebox.setInternalState(abstractView, "resMsg", (ResourceBundle)null);
		assertEquals("resMsg is null", abstractView.getMsg("resMsg is null"));
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
	public void testNormalInputRow() throws Exception {
		abstractView = new PluginsView(bundleContext,velocityContext);
		// start without indentation
		assertEquals(
			"\n"+
			"<tr>\n"+
			"  <td class=\"borderless\" style=\"width:100px;\"><b>myTitle</b></td>\n" +
			"  <td class=\"borderless\">\n" +
			"    <input type=\"text\" class=\"form\" id=\"myParName\" name=\"myParName\" value=\"myParValue\" size=\"60\">\n" +
			"  </td>\n" +
			"</tr>"+
			"\n",
			Whitebox.<String>invokeMethod(abstractView,"normalInputRow","myTitle", "myParName", "myParValue", 0l)
		);
		
		// start with an indentation of 2 spaces
		assertEquals(
			"\n"+
			"  <tr>\n"+
			"    <td class=\"borderless\" style=\"width:100px;\"><b>myTitle</b></td>\n" +
			"    <td class=\"borderless\">\n" +
			"      <input type=\"text\" class=\"form\" id=\"myParName\" name=\"myParName\" value=\"myParValue\" size=\"60\">\n" +
			"    </td>\n" +
			"  </tr>"+
			"\n",
			Whitebox.<String>invokeMethod(abstractView,"normalInputRow","myTitle", "myParName", "myParValue", 1l)
		);
		
		// Called with parName = null generates a newline
		assertEquals("\n", Whitebox.<String>invokeMethod(abstractView,"normalInputRow","myTitle", null, "myParValue", 0l));
	}
	
	@Test
	public void testnormalInfoRow() throws Exception {
		abstractView = new PluginsView(bundleContext,velocityContext);

		// start without indentation
		assertEquals(
			"\n"+
			"<tr>\n"+
			"  <td class=\"borderless\" style=\"width:100px;\"><b>myTitle</b></td>\n" +
			"  <td class=\"borderless\">\n" +
			"    myValue\n" +
			"  </td>\n" +
			"</tr>"+
			"\n",
			Whitebox.<String>invokeMethod(abstractView,"normalInfoRow","myTitle", "myValue", 0l)
		);
		
		// start with an indentation of 2 spaces
		assertEquals(
			"\n"+
			"  <tr>\n"+
			"    <td class=\"borderless\" style=\"width:100px;\"><b>myTitle</b></td>\n" +
			"    <td class=\"borderless\">\n" +
			"      myValue\n" +
			"    </td>\n" +
			"  </tr>"+
			"\n",
			Whitebox.<String>invokeMethod(abstractView,"normalInfoRow","myTitle", "myValue", 1l)
		);
	}
	
	@Test
	public void testNormalFieldSet() throws Exception {
		abstractView = new PluginsView(bundleContext,velocityContext);

		// Called with content = null generates an empty String
		assertEquals("", Whitebox.<String>invokeMethod(abstractView,"normalFieldset","myName", "myCss", null, 0l));
				
		// Called with an empty string StringBuilder generates an empty String
		assertEquals("", Whitebox.<String>invokeMethod(abstractView,"normalFieldset","myName", "myCss", new StringBuilder(), 0l));
				
		// start without indentation
		assertEquals(
			"<fieldset class=\"myCss\">\n"+
			"  <legend>myName</legend>\n"+
			"  <p>myContent</p>\n" +
			"</fieldset>\n",
			Whitebox.<String>invokeMethod(abstractView,"normalFieldset","myName", "myCss", new StringBuilder("<p>myContent</p>"), 0l)
		);
		
		// start with an indentation of 2 spaces
		assertEquals(
			"  <fieldset class=\"myCss\">\n"+
			"    <legend>myName</legend>\n"+
			"    <p>myContent</p>\n" +
			"  </fieldset>\n",
			Whitebox.<String>invokeMethod(abstractView,"normalFieldset","myName", "myCss", new StringBuilder("<p>myContent</p>"), 1l)
		);
	}
	
	@Test
	public void testErrorFieldSet() throws Exception {
		abstractView = new PluginsView(bundleContext,velocityContext);
		assertEquals(
			"<fieldset>\n"+
			"  <legend>Errors</legend>\n"+
			"  <p>Errors</p>\n" +
			"</fieldset>",
			Whitebox.<String>invokeMethod(abstractView,"errorFieldset",new StringBuilder("<p>Errors</p>"), 0l)
		);
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
