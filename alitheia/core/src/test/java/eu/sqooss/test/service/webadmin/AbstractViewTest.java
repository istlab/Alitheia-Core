package eu.sqooss.test.service.webadmin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
//import org.easymock.Mock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.junit.internal.runners.statements.Fail;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareEverythingForTest;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.tds.TDSServiceImpl;
import eu.sqooss.impl.service.webadmin.AbstractView;
import eu.sqooss.impl.service.webadmin.PluginsView;
import eu.sqooss.impl.service.webadmin.PublicAbstractView;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.TDSService;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
//import static org.easymock.EasyMock.expect;
//import static org.powermock.api.easymock.PowerMock.*;

//@RunWith(PowerMockRunner.class)
public class AbstractViewTest {

	AbstractView abstractView;
	BundleContext bundleContext;
	VelocityContext velocityContext;
	AlitheiaCore alitheiaCore;
	
	@Before
	public void setUp() throws Exception {
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
	}

	@Test
	public void testInitResources() {
		AbstractView.initResources(Locale.ENGLISH);
	}
	
	@Test
	public void testConstructor(){
		abstractView = new PluginsView(bundleContext,velocityContext);
		AbstractView.setSobjObject(alitheiaCore);
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
		
		alitheiaCore = Mockito.mock(AlitheiaCore.class);
		Mockito.when(alitheiaCore.getDBService()).thenReturn(null);
		Mockito.when(alitheiaCore.getPluginAdmin()).thenReturn(null);
		Mockito.when(alitheiaCore.getScheduler()).thenReturn(null);
		Mockito.when(alitheiaCore.getMetricActivator()).thenReturn(null);
		Mockito.when(alitheiaCore.getTDSService()).thenReturn(null);
		Mockito.when(alitheiaCore.getUpdater()).thenReturn(null);
		Mockito.when(alitheiaCore.getClusterNodeService()).thenReturn(null);
		Mockito.when(alitheiaCore.getSecurityManager()).thenReturn(null);
		Mockito.when(alitheiaCore.getLogManager()).thenReturn(null);
		
		AbstractView.setSobjObject(alitheiaCore);
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
		AbstractView.setSobjObject(alitheiaCore);
		assertEquals("test",AbstractView.getLbl("test"));
		AbstractView.initResources(Locale.ENGLISH);
		assertEquals("test",AbstractView.getLbl("test"));

		ResourceBundle resourceBundle = new MsgResourceBundle();
		AbstractView.setResLbl(resourceBundle);
		assertEquals("resLbl string",AbstractView.getLbl("resLbl"));
		
		// resLbl = null
		AbstractView.setResLbl(null);
		assertEquals("resLbl is null", AbstractView.getLbl("resLbl is null"));
		
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
		AbstractView.setSobjObject(alitheiaCore);
		assertEquals("test",AbstractView.getErr("test"));
		AbstractView.initResources(Locale.ENGLISH);
		assertEquals("test",AbstractView.getErr("test"));

		ResourceBundle resourceBundle = new MsgResourceBundle();
		AbstractView.setResErr(resourceBundle);
		assertEquals("resErr string",AbstractView.getErr("resErr"));
		
		// resLbl = null
		AbstractView.setResErr(null);
		assertEquals("resErr is null", AbstractView.getErr("resErr is null"));
	}
	
	@Test
	public void testGetMsg() throws Exception{
		AbstractView.setSobjObject(alitheiaCore);
		assertEquals("test",AbstractView.getMsg("test"));
		AbstractView.initResources(Locale.ENGLISH);
		assertEquals("test",AbstractView.getMsg("test"));

		ResourceBundle resourceBundle = new MsgResourceBundle();
		AbstractView.setResMsg(resourceBundle);
		assertEquals("resMsg string",AbstractView.getMsg("resMsg"));
		
		// resLbl = null
		AbstractView.setResMsg(null);
		assertEquals("resMsg is null", AbstractView.getMsg("resMsg is null"));
	}
	
	@Test
	public void testDebugRequest() {
		fail("TODO");
		// @TODO  
	}
	
	@Test
	public void testSp() {
		assertEquals("", PublicAbstractView.sp(0));
		assertEquals("  ", PublicAbstractView.sp(1));
		assertEquals("    ", PublicAbstractView.sp(2));
	}
	
	@Test
	public void testNormalInputRow() {
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
			PublicAbstractView.normalInputRow("myTitle", "myParName", "myParValue", 0)
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
			PublicAbstractView.normalInputRow("myTitle", "myParName", "myParValue", 1)
		);
		
		// Called with parName = null generates a newline
		assertEquals("\n", PublicAbstractView.normalInputRow("myTitle", null, "myParValue", 0));
	}
	
	@Test
	public void testnormalInfoRow() {
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
			PublicAbstractView.normalInfoRow("myTitle", "myValue", 0)
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
			PublicAbstractView.normalInfoRow("myTitle", "myValue", 1)
		);
	}
	
	@Test
	public void testNormalFieldSet() {
		// Called with content = null generates an empty String
		assertEquals("", PublicAbstractView.normalFieldset("myName", "myCss", null, 0));
				
		// Called with an empty string StringBuilder generates an empty String
		assertEquals("", PublicAbstractView.normalFieldset("myName", "myCss", new StringBuilder(), 0));
				
		// start without indentation
		assertEquals(
			"<fieldset class=\"myCss\">\n"+
			"  <legend>myName</legend>\n"+
			"  <p>myContent</p>\n" +
			"</fieldset>\n",
			PublicAbstractView.normalFieldset("myName", "myCss", new StringBuilder("<p>myContent</p>"), 0)
		);
		
		// start with an indentation of 2 spaces
		assertEquals(
			"  <fieldset class=\"myCss\">\n"+
			"    <legend>myName</legend>\n"+
			"    <p>myContent</p>\n" +
			"  </fieldset>\n",
			PublicAbstractView.normalFieldset("myName", "myCss", new StringBuilder("<p>myContent</p>"), 1)
		);
	}
	
	@Test
	public void testErrorFieldSet() {
		assertEquals(
			"<fieldset>\n"+
			"  <legend>Errors</legend>\n"+
			"  <p>Errors</p>\n" +
			"</fieldset>",
			PublicAbstractView.errorFieldset(new StringBuilder("<p>Errors</p>"), 0)
		);
	}
	
	@Test
	public void testFromString() {
		assertEquals(new Long(0), PublicAbstractView.fromString("0"));
		assertNull(PublicAbstractView.fromString("not a number"));
	}
	
	@Test
	public void testCheckName() {
		assertFalse(PublicAbstractView.checkName(""));
		assertFalse(PublicAbstractView.checkName(new String()));
		assertFalse(PublicAbstractView.checkName(null));
		assertTrue(PublicAbstractView.checkName("a Valid Name 1"));
		assertTrue(PublicAbstractView.checkName("aValidName2"));
		assertFalse(PublicAbstractView.checkName(" name must not start with space"));
		assertFalse(PublicAbstractView.checkName("name must not end with space "));
	}
	
	@Test
	public void testCheckProjectName() {
		assertFalse(PublicAbstractView.checkProjectName(""));
		assertFalse(PublicAbstractView.checkProjectName(new String()));
		assertFalse(PublicAbstractView.checkProjectName(null));
		assertTrue(PublicAbstractView.checkProjectName("a Valid Name 1"));
		assertTrue(PublicAbstractView.checkProjectName("aValidName2"));
		assertTrue(PublicAbstractView.checkProjectName("a_Valid_Name_3"));
		assertFalse(PublicAbstractView.checkProjectName(" name must not start with space"));
		assertFalse(PublicAbstractView.checkProjectName("name must not end with space "));
		assertFalse(PublicAbstractView.checkProjectName("_name must not start with underscore"));
		assertFalse(PublicAbstractView.checkProjectName("name must not end with underscore_"));
	}
	
	@Test
	//TODO add test cases for uncovered code
	public void testEmail() {
		assertFalse(PublicAbstractView.checkEmail(null));
		assertFalse(PublicAbstractView.checkEmail(""));
		assertFalse(PublicAbstractView.checkEmail("test@.nl"));
		assertFalse(PublicAbstractView.checkEmail("test.nl"));
		assertTrue(PublicAbstractView.checkEmail("test@myDomain.nl"));
		assertFalse(PublicAbstractView.checkEmail(".test@myDomain.nl"));
		assertFalse(PublicAbstractView.checkEmail(".test@.myDomain.nl"));
		assertFalse(PublicAbstractView.checkEmail("test@myDomain.nl."));
		assertFalse(PublicAbstractView.checkEmail("..test@myDomain.nl"));
	}
	
	@Test
	public void checkTDSUrl() {
		// TODO implement test-case
		fail("not implemented yet");
	}
	
	
}
