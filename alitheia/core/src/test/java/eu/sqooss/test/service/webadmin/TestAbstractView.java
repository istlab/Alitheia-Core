package eu.sqooss.test.service.webadmin;

import static org.junit.Assert.*;

import org.mockito.Mockito;
import org.mockito.internal.MockitoCore;

//import static org.powermock.api.mockito.PowerMockito.*;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.*;


import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.velocity.VelocityContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.BundleContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.AbstractView;
import eu.sqooss.impl.service.webadmin.PluginsView;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
@RunWith(PowerMockRunner.class)
//@PrepareForTest(AbstractView.class)
public class TestAbstractView {

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
	@Test
	public void testGetLbl() throws Exception{
		AbstractView.setSobjObject(alitheiaCore);
		assertEquals("test",AbstractView.getLbl("test"));
		AbstractView.initResources(Locale.ENGLISH);
		assertEquals("test",AbstractView.getLbl("test"));
		
//		code below fails. when resourceBundle.getString() is called it actually tries to 
//		execute it. This is not expected behavior.
/*		ResourceBundle resourceBundle = Mockito.mock(ResourceBundle.class);
		whenNew(ResourceBundle.class).withAnyArguments().thenReturn(resourceBundle);
		Mockito.when(resourceBundle.getString("error")).thenThrow(new NullPointerException("Null test"));
		Mockito.when(resourceBundle.getString("works")).thenReturn("it works");
		AbstractView.setResLbl(resourceBundle);
		assertEquals("Undefined parameter name!",AbstractView.getLbl("error"));
		Mockito.verify(resourceBundle.getString("error"));
		assertEquals("it works",AbstractView.getLbl("works"));
		Mockito.verify(resourceBundle.getString("works"));*/
		
		
		
		
	}

}
