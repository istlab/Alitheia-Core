package eu.sqooss.service.webadmin.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Locale;

import org.apache.velocity.VelocityContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.AbstractView;
import eu.sqooss.impl.service.webadmin.PluginsView;

public class TestAbstractView {

	AbstractView abstractView;
	BundleContext bundleContext;
	VelocityContext velocityContext;
	AlitheiaCore alitheiaCore;
	
	@Before
	public void setUp() throws Exception {
		bundleContext = mock(BundleContext.class);
		velocityContext = mock(VelocityContext.class);
		alitheiaCore = mock(AlitheiaCore.class);
		assertNotNull(alitheiaCore);
		abstractView = new PluginsView(bundleContext,velocityContext);
	}

	@After
	public void tearDown() throws Exception {
		bundleContext = null;
		velocityContext = null;
		abstractView = null;
	}

	@Test
	public void testInitResources() {
		Locale locale = mock(Locale.class);
		AbstractView.initResources(locale);
	}

}
