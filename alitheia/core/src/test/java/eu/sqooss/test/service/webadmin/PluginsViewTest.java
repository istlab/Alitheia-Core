package eu.sqooss.test.service.webadmin;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;

import org.apache.velocity.VelocityContext;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.osgi.framework.BundleContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.AbstractView;
import eu.sqooss.impl.service.webadmin.PluginsView;
import eu.sqooss.impl.service.webadmin.ProjectsView;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.cluster.ClusterNodeService;
import eu.sqooss.service.db.Bug;
import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.MailMessage;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.StoredProjectConfig;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.Updater;
import eu.sqooss.service.updater.UpdaterService;
import eu.sqooss.service.updater.UpdaterService.UpdaterStage;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.sqooss.impl.service.webadmin.ProjectsView;
import eu.sqooss.service.pa.PluginAdmin;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Plugin.class})
public class PluginsViewTest {
	@Mock private PluginAdmin pa;
	@Mock private AlitheiaPlugin alitheiaPlugin;
	@Mock private Plugin plugin;
	
	@Before
    public void setUpTest() 
	{
		Whitebox.setInternalState(PluginsView.class, pa);
    }

	@Test
	public void testIsPluginsListEmpty() {
		Collection<PluginInfo> col = new ArrayList<PluginInfo>();
		when(pa.listPlugins()).thenReturn(col);
		Assert.assertTrue(PluginsView.isPluginsListEmpty());
		col.add(new PluginInfo());
		Assert.assertFalse(PluginsView.isPluginsListEmpty());
	}
	
	@Test
	public void testGetPluginsList() {
		Collection<PluginInfo> col = new ArrayList<PluginInfo>();
		when(pa.listPlugins()).thenReturn(col);
		Assert.assertTrue(PluginsView.getPluginsList().size() == 0);
		col.add(new PluginInfo());
		Assert.assertTrue(PluginsView.getPluginsList().size() == 1);
	}
	
	@Test
	public void testGetErrorMessages() {
		StringBuilder sb = new StringBuilder();
		Whitebox.setInternalState(PluginsView.class, sb);
		
		Assert.assertEquals(null, PluginsView.getErrorMessages());
		sb.append("test");
		Assert.assertEquals("test", PluginsView.getErrorMessages());
	}
	
	@Test
	public void testGetSelectedPlugin() {
		Assert.assertEquals(null, PluginsView.getSelectedPlugin());
		PluginInfo pi = new PluginInfo();
		Whitebox.setInternalState(PluginsView.class, pi);
		Assert.assertEquals(pi, PluginsView.getSelectedPlugin());
	}
	
	@Test
	public void testGetValPropName() {
		Assert.assertEquals("", PluginsView.getValPropName());
		String prop = "test";
		Whitebox.setInternalState(PluginsView.class, "reqValPropName", prop);
		Assert.assertEquals("test", PluginsView.getValPropName());
	}
	
	@Test
	public void testGetValPropType() {
		Assert.assertEquals("", PluginsView.getValPropType());
		String prop = "test";
		Whitebox.setInternalState(PluginsView.class, "reqValPropType", prop);
		Assert.assertEquals("test", PluginsView.getValPropType());
	}
	
	@Test
	public void testGetValPropDescr() {
		Assert.assertEquals("", PluginsView.getValPropDescr());
		String prop = "test";
		Whitebox.setInternalState(PluginsView.class, "reqValPropDescr", prop);
		Assert.assertEquals("test", PluginsView.getValPropDescr());
	}
	
	@Test
	public void testGetValPropValue() {
		Assert.assertEquals("", PluginsView.getValPropValue());
		String prop = "test";
		Whitebox.setInternalState(PluginsView.class, "reqValPropValue", prop);
		Assert.assertEquals("test", PluginsView.getValPropValue());
	}
	
	@Test
	public void testGetValAction() {
		String prop = null;
		Whitebox.setInternalState(PluginsView.class, "reqValAction", prop);
		Assert.assertEquals("", PluginsView.getValAction());
		prop = "test";
		Whitebox.setInternalState(PluginsView.class, "reqValAction", prop);
		Assert.assertEquals("test", PluginsView.getValAction());
	}
	
	@Test
	public void testGetValHashcode() {
		Assert.assertEquals("", PluginsView.getValHashcode());
		String prop = "test";
		Whitebox.setInternalState(PluginsView.class, "reqValHashcode", prop);
		Assert.assertEquals("test", PluginsView.getValHashcode());
	}
	
	@Test
	public void testGetValShowProp() {
		Assert.assertFalse(PluginsView.getValShowProp());
		Whitebox.setInternalState(PluginsView.class, "reqValShowProp", true);
		Assert.assertTrue(PluginsView.getValShowProp());
	}
	
	@Test
	public void testGetValShowActv() {
		Assert.assertFalse(PluginsView.getValShowActv());
		Whitebox.setInternalState(PluginsView.class, "reqValShowActv", true);
		Assert.assertTrue(PluginsView.getValShowActv());
	}
	
	@Test
	public void testGetConfigurationTypes() {
		Assert.assertTrue(PluginsView.getConfigurationTypes().length == 4);
	}
	
	@Test
	public void testGetPluginMetrics() {
		List<Metric> col = new ArrayList<Metric>();
		PluginInfo pi = new PluginInfo();
		when(pa.getPlugin(pi)).thenReturn(alitheiaPlugin);
		when(alitheiaPlugin.getAllSupportedMetrics()).thenReturn(col);
		Assert.assertTrue(PluginsView.getPluginMetrics(pi).size() == 0);
		col.add(new Metric());
		Assert.assertTrue(PluginsView.getPluginMetrics(pi).size() == 1);
		Assert.assertEquals(null, PluginsView.getPluginMetrics(null));
	}
	
	@Test
	public void testIsPluginMetricsEmpty() {
		List<Metric> col = new ArrayList<Metric>();
		PluginInfo pi = new PluginInfo();
		when(pa.getPlugin(pi)).thenReturn(alitheiaPlugin);
		when(alitheiaPlugin.getAllSupportedMetrics()).thenReturn(null, col);
		Assert.assertTrue(PluginsView.isPluginMetricsEmpty(pi));
		Assert.assertTrue(PluginsView.isPluginMetricsEmpty(pi));
		col.add(new Metric());
		Assert.assertFalse(PluginsView.isPluginMetricsEmpty(pi));
		Assert.assertTrue(PluginsView.isPluginMetricsEmpty(null));
	}
	
	@Test
	public void testGetPluginConfiguration() {
		Set<PluginConfiguration> col = new HashSet<PluginConfiguration>();
		PluginInfo pi = new PluginInfo();
		PowerMockito.mockStatic(Plugin.class);
		when(Plugin.getPluginByHashcode(pi.getHashcode())).thenReturn(plugin);
		when(plugin.getConfigurations()).thenReturn(col);
		Assert.assertTrue(PluginsView.getPluginConfiguration(pi).size() == 0);
		col.add(new PluginConfiguration());
		Assert.assertTrue(PluginsView.getPluginConfiguration(pi).size() == 1);
		Assert.assertEquals(null, PluginsView.getPluginConfiguration(null));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testIsPluginConfigurationEmpty() {
		Set<PluginConfiguration> col = new HashSet<PluginConfiguration>();
		PluginInfo pi = new PluginInfo();
		PowerMockito.mockStatic(Plugin.class);
		when(Plugin.getPluginByHashcode(pi.getHashcode())).thenReturn(plugin);
		when(plugin.getConfigurations()).thenReturn(null, col);
		Assert.assertTrue(PluginsView.isPluginConfigurationEmpty(pi));
		Assert.assertTrue(PluginsView.isPluginConfigurationEmpty(pi));
		col.add(new PluginConfiguration());
		Assert.assertFalse(PluginsView.isPluginConfigurationEmpty(pi));
		Assert.assertTrue(PluginsView.isPluginConfigurationEmpty(null));
	}
	
	@Test
	public void testIsDebugOn() {
		Assert.assertFalse(PluginsView.isDebugOn());
		Whitebox.setInternalState(PluginsView.class, "DEBUG", true);
		Assert.assertTrue(PluginsView.isDebugOn());
	}
}