/**
 * 
 */
package eu.sqooss.test.service.webadmin;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;

import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import static org.powermock.api.mockito.PowerMockito.*;
import static org.hamcrest.CoreMatchers.*;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.*;
import org.mockito.Matchers;
import org.apache.velocity.VelocityContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Set;

import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.webadmin.AbstractView;
import eu.sqooss.impl.service.webadmin.PluginsView;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.cluster.ClusterNodeService;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.UpdaterService;

/**
 * @author elwin
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Plugin.class)
public class PluginsViewTest {
	private PluginsView instance;
	private BundleContext bundleContext;
	private VelocityContext velocityContext;
	private AlitheiaCore alitheiaCore;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		bundleContext = mock(BundleContext.class);
		velocityContext = mock(VelocityContext.class);
		alitheiaCore = Mockito.mock(AlitheiaCore.class);
		AbstractView.setSobjObject(alitheiaCore);
		when(alitheiaCore.getDBService()).thenReturn(mock(DBService.class));
		when(alitheiaCore.getScheduler()).thenReturn(mock(Scheduler.class));
		when(alitheiaCore.getMetricActivator()).thenReturn(mock(MetricActivator.class));
		when(alitheiaCore.getTDSService()).thenReturn(mock(TDSService.class));
		when(alitheiaCore.getUpdater()).thenReturn(mock(UpdaterService.class));
		when(alitheiaCore.getClusterNodeService()).thenReturn(mock(ClusterNodeService.class));
		when(alitheiaCore.getSecurityManager()).thenReturn(mock(SecurityManager.class));
		LogManager logManager = mock(LogManager.class);
		Logger logger = Mockito.mock(Logger.class);
		Mockito.when(logManager.createLogger(Logger.NAME_SQOOSS_WEBADMIN)).thenReturn(logger);
		when(alitheiaCore.getLogManager()).thenReturn(logManager);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		bundleContext = null;
		velocityContext = null;
		instance = null;
	}

	/**
	 * Test method for {@link eu.sqooss.impl.service.webadmin.PluginsView#render(javax.servlet.http.HttpServletRequest)}.
	 */
	@Test
	public void testRenderNoPlugin() {
		PluginAdmin admin = mock(PluginAdmin.class);
		Collection<PluginInfo> list = new ArrayList<PluginInfo>();
		when(admin.listPlugins()).thenReturn(list);
		when(alitheiaCore.getPluginAdmin()).thenReturn(admin);
		instance = new PluginsView(bundleContext,velocityContext);
		HttpServletRequest req = mock(HttpServletRequest.class);
		String result = PluginsView.render(req);
		String expectedResult = "\n            <fieldset>\n              <legend>All plug-ins</legend>\n<span>No plug-ins found!&nbsp;<input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Refresh\" onclick=\"javascript:window.location.reload(true);\"></span>            </fieldset>\n";
		assertThat(result,equalTo(expectedResult));
	}
	
	/**
	 * Test method for {@link eu.sqooss.impl.service.webadmin.PluginsView#render(javax.servlet.http.HttpServletRequest)}.
	 */
	@Test
	public void testRenderNullRequestPluginNotInstalled() {
		PluginAdmin admin = mock(PluginAdmin.class);
		Collection<PluginInfo> list = new ArrayList<PluginInfo>();
		PluginInfo mockPlugin = mock(PluginInfo.class);
		when(mockPlugin.getServiceRef()).thenReturn(mock(ServiceReference.class));
		list.add(mockPlugin);
		when(admin.listPlugins()).thenReturn(list);
		when(alitheiaCore.getPluginAdmin()).thenReturn(admin);
		instance = new PluginsView(bundleContext,velocityContext);
		HttpServletRequest req = null;
		String result = PluginsView.render(req);
		String expectedResult = "\n            <form id=\"metrics\" name=\"metrics\" method=\"post\" action=\"/index\">\n              <fieldset>\n                <legend>All plug-ins</legend>\n                <table>\n                  <thead>\n                    <tr class=\"head\">\n                      <td class=\"head\" style=\"width: 80px;\">Status</td>\n                      <td class=\"head\" style=\"width: 30%;\">Name</td>\n                      <td class=\"head\" style=\"width: 40%;\">Class</td>\n                      <td class=\"head\">Version</td>\n                    </tr>\n                  </thead>\n                  <tbody>\n                    <tr class=\"edit\" onclick=\"javascript:document.getElementById('pluginHashcode').value='null';document.metrics.submit();\">\n                      <td class=\"trans\"><img src=\"/edit.png\" alt=\"[Edit]\"/>&nbsp;Registered</td>\n                      <td class=\"trans\">null</td>\n                      <td class=\"trans\">null</td>\n                      <td class=\"trans\">null</td>\n                    </tr>\n                  </tbody>\n                </table>\n                <span>\n                  <input type=\"checkbox\" onclick=\"javascript:document.getElementById('showProperties').value = this.checked;document.getElementById('pluginHashcode').value='';document.metrics.submit();\">Display properties\n                    <input type=\"checkbox\" onclick=\"javascript:document.getElementById('showActivators').value = this.checked;document.getElementById('pluginHashcode').value='';document.metrics.submit();\">Display activators\n                  </span>\n                </fieldset>\n                <input type=\"hidden\" id=\"action\" name=\"action\" value=\"\">\n                <input type=\"hidden\" id=\"pluginHashcode\" name=\"pluginHashcode\" value=\"\">\n                <input type=\"hidden\" id=\"propertyName\" name=\"propertyName\" value=\"\">\n                <input type=\"hidden\" id=\"propertyDescription\" name=\"propertyDescription\" value=\"\">\n                <input type=\"hidden\" id=\"propertyType\" name=\"propertyType\" value=\"\">\n                <input type=\"hidden\" id=\"propertyValue\" name=\"propertyValue\" value=\"\">\n                <input type=\"hidden\" id=\"showProperties\" name=\"showProperties\" value=\"false\">\n                <input type=\"hidden\" id=\"showActivators\" name=\"showActivators\" value=\"false\">\n              </form>\n";
		assertThat(result,equalTo(expectedResult));
	}
	
	/**
	 * Test method for {@link eu.sqooss.impl.service.webadmin.PluginsView#render(javax.servlet.http.HttpServletRequest)}.
	 */
	@Test
	public void testRenderNullRequestPluginInstalled() {
		PluginAdmin admin = mock(PluginAdmin.class);
		Collection<PluginInfo> list = new ArrayList<PluginInfo>();
		PluginInfo mockPlugin = mock(PluginInfo.class);
		mockPlugin.installed = true;
		when(mockPlugin.getServiceRef()).thenReturn(mock(ServiceReference.class));
		list.add(mockPlugin);
		when(admin.listPlugins()).thenReturn(list);
		when(alitheiaCore.getPluginAdmin()).thenReturn(admin);
		instance = new PluginsView(bundleContext,velocityContext);
		HttpServletRequest req = null;
		String result = PluginsView.render(req);
		String expectedResult = "\n            <form id=\"metrics\" name=\"metrics\" method=\"post\" action=\"/index\">\n              <fieldset>\n                <legend>All plug-ins</legend>\n                <table>\n                  <thead>\n                    <tr class=\"head\">\n                      <td class=\"head\" style=\"width: 80px;\">Status</td>\n                      <td class=\"head\" style=\"width: 30%;\">Name</td>\n                      <td class=\"head\" style=\"width: 40%;\">Class</td>\n                      <td class=\"head\">Version</td>\n                    </tr>\n                  </thead>\n                  <tbody>\n                    <tr class=\"edit\" onclick=\"javascript:document.getElementById('pluginHashcode').value='null';document.metrics.submit();\">\n                      <td class=\"trans\"><img src=\"/edit.png\" alt=\"[Edit]\"/>&nbsp;Installed</td>\n                      <td class=\"trans\">null</td>\n                      <td class=\"trans\">null</td>\n                      <td class=\"trans\">null</td>\n                    </tr>\n                  </tbody>\n                </table>\n                <span>\n                  <input type=\"checkbox\" onclick=\"javascript:document.getElementById('showProperties').value = this.checked;document.getElementById('pluginHashcode').value='';document.metrics.submit();\">Display properties\n                    <input type=\"checkbox\" onclick=\"javascript:document.getElementById('showActivators').value = this.checked;document.getElementById('pluginHashcode').value='';document.metrics.submit();\">Display activators\n                  </span>\n                </fieldset>\n                <input type=\"hidden\" id=\"action\" name=\"action\" value=\"\">\n                <input type=\"hidden\" id=\"pluginHashcode\" name=\"pluginHashcode\" value=\"\">\n                <input type=\"hidden\" id=\"propertyName\" name=\"propertyName\" value=\"\">\n                <input type=\"hidden\" id=\"propertyDescription\" name=\"propertyDescription\" value=\"\">\n                <input type=\"hidden\" id=\"propertyType\" name=\"propertyType\" value=\"\">\n                <input type=\"hidden\" id=\"propertyValue\" name=\"propertyValue\" value=\"\">\n                <input type=\"hidden\" id=\"showProperties\" name=\"showProperties\" value=\"false\">\n                <input type=\"hidden\" id=\"showActivators\" name=\"showActivators\" value=\"false\">\n              </form>\n";
		assertThat(result,equalTo(expectedResult));
	}
	
	/**
	 * Test method for {@link eu.sqooss.impl.service.webadmin.PluginsView#render(javax.servlet.http.HttpServletRequest)}.
	 */
	@Test
	public void testRenderNotNullRequestPluginInstalled() {
		PluginAdmin admin = mock(PluginAdmin.class);
		Collection<PluginInfo> list = new ArrayList<PluginInfo>();
		PluginInfo mockPlugin = mock(PluginInfo.class);
		mockPlugin.installed = true;
		when(mockPlugin.getServiceRef()).thenReturn(mock(ServiceReference.class));
		list.add(mockPlugin);
		when(admin.listPlugins()).thenReturn(list);
		when(alitheiaCore.getPluginAdmin()).thenReturn(admin);
		instance = new PluginsView(bundleContext,velocityContext);
		HttpServletRequest req = mock(HttpServletRequest.class);
		String result = PluginsView.render(req);
		String expectedResult = "\n            <form id=\"metrics\" name=\"metrics\" method=\"post\" action=\"/index\">\n              <fieldset>\n                <legend>All plug-ins</legend>\n                <table>\n                  <thead>\n                    <tr class=\"head\">\n                      <td class=\"head\" style=\"width: 80px;\">Status</td>\n                      <td class=\"head\" style=\"width: 30%;\">Name</td>\n                      <td class=\"head\" style=\"width: 40%;\">Class</td>\n                      <td class=\"head\">Version</td>\n                    </tr>\n                  </thead>\n                  <tbody>\n                    <tr class=\"edit\" onclick=\"javascript:document.getElementById('pluginHashcode').value='null';document.metrics.submit();\">\n                      <td class=\"trans\"><img src=\"/edit.png\" alt=\"[Edit]\"/>&nbsp;Installed</td>\n                      <td class=\"trans\">null</td>\n                      <td class=\"trans\">null</td>\n                      <td class=\"trans\">null</td>\n                    </tr>\n                  </tbody>\n                </table>\n                <span>\n                  <input type=\"checkbox\" onclick=\"javascript:document.getElementById('showProperties').value = this.checked;document.getElementById('pluginHashcode').value='';document.metrics.submit();\">Display properties\n                    <input type=\"checkbox\" onclick=\"javascript:document.getElementById('showActivators').value = this.checked;document.getElementById('pluginHashcode').value='';document.metrics.submit();\">Display activators\n                  </span>\n                </fieldset>\n                <input type=\"hidden\" id=\"action\" name=\"action\" value=\"\">\n                <input type=\"hidden\" id=\"pluginHashcode\" name=\"pluginHashcode\" value=\"\">\n                <input type=\"hidden\" id=\"propertyName\" name=\"propertyName\" value=\"\">\n                <input type=\"hidden\" id=\"propertyDescription\" name=\"propertyDescription\" value=\"\">\n                <input type=\"hidden\" id=\"propertyType\" name=\"propertyType\" value=\"\">\n                <input type=\"hidden\" id=\"propertyValue\" name=\"propertyValue\" value=\"\">\n                <input type=\"hidden\" id=\"showProperties\" name=\"showProperties\" value=\"false\">\n                <input type=\"hidden\" id=\"showActivators\" name=\"showActivators\" value=\"false\">\n              </form>\n";
		assertThat(result,equalTo(expectedResult));
	}
	
	/**
	 * Test method for {@link eu.sqooss.impl.service.webadmin.PluginsView#render(javax.servlet.http.HttpServletRequest)}.
	 */
	@Test
	public void testRenderNotNullWithPluginRequest() {
		PluginAdmin admin = mock(PluginAdmin.class);
		Collection<PluginInfo> list = new ArrayList<PluginInfo>();
		PluginInfo mockPlugin = mock(PluginInfo.class);
		mockPlugin.installed = true;
		when(mockPlugin.getServiceRef()).thenReturn(mock(ServiceReference.class));
		list.add(mockPlugin);
		when(admin.listPlugins()).thenReturn(list);
		when(alitheiaCore.getPluginAdmin()).thenReturn(admin);
		instance = new PluginsView(bundleContext,velocityContext);
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getParameter(Matchers.any(String.class))).thenReturn("FakeHashCode");
		String result = PluginsView.render(req);
		String expectedResult = "\n            <form id=\"metrics\" name=\"metrics\" method=\"post\" action=\"/index\">\n              <fieldset>\n                <legend>All plug-ins</legend>\n                <table>\n                  <thead>\n                    <tr class=\"head\">\n                      <td class=\"head\" style=\"width: 80px;\">Status</td>\n                      <td class=\"head\" style=\"width: 30%;\">Name</td>\n                      <td class=\"head\" style=\"width: 40%;\">Class</td>\n                      <td class=\"head\">Version</td>\n                    </tr>\n                  </thead>\n                  <tbody>\n                    <tr class=\"edit\" onclick=\"javascript:document.getElementById('pluginHashcode').value='null';document.metrics.submit();\">\n                      <td class=\"trans\"><img src=\"/edit.png\" alt=\"[Edit]\"/>&nbsp;Installed</td>\n                      <td class=\"trans\">null</td>\n                      <td class=\"trans\">null</td>\n                      <td class=\"trans\">null</td>\n                    </tr>\n                  </tbody>\n                </table>\n                <span>\n                  <input type=\"checkbox\" onclick=\"javascript:document.getElementById('showProperties').value = this.checked;document.getElementById('pluginHashcode').value='';document.metrics.submit();\">Display properties\n                    <input type=\"checkbox\" onclick=\"javascript:document.getElementById('showActivators').value = this.checked;document.getElementById('pluginHashcode').value='';document.metrics.submit();\">Display activators\n                  </span>\n                </fieldset>\n                <input type=\"hidden\" id=\"action\" name=\"action\" value=\"\">\n                <input type=\"hidden\" id=\"pluginHashcode\" name=\"pluginHashcode\" value=\"FakeHashCode\">\n                <input type=\"hidden\" id=\"propertyName\" name=\"propertyName\" value=\"\">\n                <input type=\"hidden\" id=\"propertyDescription\" name=\"propertyDescription\" value=\"\">\n                <input type=\"hidden\" id=\"propertyType\" name=\"propertyType\" value=\"\">\n                <input type=\"hidden\" id=\"propertyValue\" name=\"propertyValue\" value=\"\">\n                <input type=\"hidden\" id=\"showProperties\" name=\"showProperties\" value=\"false\">\n                <input type=\"hidden\" id=\"showActivators\" name=\"showActivators\" value=\"false\">\n              </form>\n";
		assertThat(result,equalTo(expectedResult));
	}
	
	/**
	 * Test method for {@link eu.sqooss.impl.service.webadmin.PluginsView#render(javax.servlet.http.HttpServletRequest)}.
	 */
	@Test
	public void testRenderNotNullWithPluginRequestAndKnowHashCodeNotInstalled() {
		String hashCode = "KnownHashCode";
		PluginAdmin admin = mock(PluginAdmin.class);
		Collection<PluginInfo> list = new ArrayList<PluginInfo>();
		PluginInfo mockPlugin = mock(PluginInfo.class);
		mockPlugin.installed = false;
		when(mockPlugin.getServiceRef()).thenReturn(mock(ServiceReference.class));
		list.add(mockPlugin);
		when(admin.listPlugins()).thenReturn(list);
		when(admin.getPluginInfo(hashCode)).thenReturn(mockPlugin);
		when(alitheiaCore.getPluginAdmin()).thenReturn(admin);
		instance = new PluginsView(bundleContext,velocityContext);
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getParameter(Matchers.any(String.class))).thenReturn(hashCode);
		String result = PluginsView.render(req);
		String expectedResult = "\n            <form id=\"metrics\" name=\"metrics\" method=\"post\" action=\"/index\">\n              <fieldset>\n                <legend>null</legend>\n                <table>\n                  <thead>\n                    <tr class=\"head\">\n                      <td class=\"head\" style=\"width: 80px;\">Status</td>\n                      <td class=\"head\" style=\"width: 30%;\">Name</td>\n                      <td class=\"head\" style=\"width: 40%;\">Class</td>\n                      <td class=\"head\">Version</td>\n                    </tr>\n                  </thead>\n                  <tbody>\n                  <tr>\n                      <td>Registered</td>\n                      <td>null</td>\n                      <td>null</td>\n                      <td>null</td>\n                    </tr>\n                    <tr>\n                      <td colspan=\"4\">\n                        <input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Plug-ins list\" onclick=\"javascript:document.getElementById('pluginHashcode').value='';document.metrics.submit();\">\n                        <input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Install\" onclick=\"javascript:document.getElementById('action').value='installPlugin';document.getElementById('pluginHashcode').value='null';document.metrics.submit();\">\n                      </td>\n                    </tr>\n                  </tbody>\n                </table>\n              </fieldset>\n              <input type=\"hidden\" id=\"action\" name=\"action\" value=\"\">\n              <input type=\"hidden\" id=\"pluginHashcode\" name=\"pluginHashcode\" value=\"KnownHashCode\">\n              <input type=\"hidden\" id=\"propertyName\" name=\"propertyName\" value=\"\">\n              <input type=\"hidden\" id=\"propertyDescription\" name=\"propertyDescription\" value=\"\">\n              <input type=\"hidden\" id=\"propertyType\" name=\"propertyType\" value=\"\">\n              <input type=\"hidden\" id=\"propertyValue\" name=\"propertyValue\" value=\"\">\n              <input type=\"hidden\" id=\"showProperties\" name=\"showProperties\" value=\"false\">\n              <input type=\"hidden\" id=\"showActivators\" name=\"showActivators\" value=\"false\">\n            </form>\n";
		assertThat(result,equalTo(expectedResult));
	}
	
	/**
	 * Test method for {@link eu.sqooss.impl.service.webadmin.PluginsView#render(javax.servlet.http.HttpServletRequest)}.
	 */
	@Test
	public void testRenderNotNullWithPluginRequestAndKnowHashCodeInstalledNoMetrics() {
		String hashCode = "KnownHashCode";
		Plugin plugin = mock(Plugin.class);
		mockStatic(Plugin.class);
		when(Plugin.getPluginByHashcode(Matchers.anyString())).thenReturn(plugin);
		PluginAdmin admin = mock(PluginAdmin.class);
		Collection<PluginInfo> list = new ArrayList<PluginInfo>();
		PluginInfo pluginInfo = mock(PluginInfo.class);
		pluginInfo.installed = true;
		when(pluginInfo.getServiceRef()).thenReturn(mock(ServiceReference.class));
		list.add(pluginInfo);
		when(admin.listPlugins()).thenReturn(list);
		when(admin.getPluginInfo(hashCode)).thenReturn(pluginInfo);
		AlitheiaPlugin alitheiaPlugin = mock(AlitheiaPlugin.class);
		when(alitheiaPlugin.getAllSupportedMetrics()).thenReturn(new ArrayList<Metric>());
		when(admin.getPlugin(pluginInfo)).thenReturn(alitheiaPlugin);
		when(alitheiaCore.getPluginAdmin()).thenReturn(admin);
		instance = new PluginsView(bundleContext,velocityContext);
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getParameter(Matchers.any(String.class))).thenReturn(hashCode);
		String result = PluginsView.render(req);
		String expectedResult = "\n            <form id=\"metrics\" name=\"metrics\" method=\"post\" action=\"/index\">\n              <fieldset>\n                <legend>null</legend>\n                <table>\n                  <thead>\n                    <tr class=\"head\">\n                      <td class=\"head\" style=\"width: 80px;\">Status</td>\n                      <td class=\"head\" style=\"width: 30%;\">Name</td>\n                      <td class=\"head\" style=\"width: 40%;\">Class</td>\n                      <td class=\"head\">Version</td>\n                    </tr>\n                  </thead>\n                  <tbody>\n                  <tr>\n                      <td>Installed</td>\n                      <td>null</td>\n                      <td>null</td>\n                      <td>null</td>\n                    </tr>\n                    <tr>\n                      <td colspan=\"4\">\n                        <input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Plug-ins list\" onclick=\"javascript:document.getElementById('pluginHashcode').value='';document.metrics.submit();\">\n                        <input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Uninstall\" onclick=\"javascript:document.getElementById('action').value='uninstallPlugin';document.getElementById('pluginHashcode').value='null';document.metrics.submit();\">\n                        <input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Synchronise\" onclick=\"javascript:document.getElementById('action').value='syncPlugin';document.getElementById('pluginHashcode').value='null';document.metrics.submit();\">\n                      </td>\n                    </tr>\n                  </tbody>\n                </table>\n                  <fieldset>\n                    <legend>Supported metrics</legend>\n                    <table>\n                      <thead>\n                        <tr class=\"head\">\n                          <td class=\"head\" style=\"width: 10%;\">Id</td>\n                          <td class=\"head\" style=\"width: 25%;\">Name</td>\n                          <td class=\"head\" style=\"width: 25%;\">Type</td>\n                          <td class=\"head\" style=\"width: 40%;\">Description</td>\n                        </tr>\n                      </thead>\n                      <tbody>\n                        <tr>                          <td colspan=\"4\" class=\"noattr\">This plug-in does not support metrics.</td>\n                        </tr>\n                      </tbody>\n                    </table>\n                  </fieldset>\n                    <fieldset>\n                      <legend>Configuration properties</legend>\n                      <table>\n                        <thead>\n                          <tr class=\"head\">\n                            <td class=\"head\" style=\"width: 30%;\">Name</td>\n                            <td class=\"head\" style=\"width: 20%;\">Type</td>\n                            <td class=\"head\" style=\"width: 50%;\">Value</td>\n                          </tr>\n                        </thead>\n                        <tbody>\n                          <tr>                            <td colspan=\"3\" class=\"noattr\">This plug-in has no configuration properties.</td>\n                          </tr>\n                          <tr>\n                            <td colspan=\"3\">\n                              <input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Add property\" onclick=\"javascript:document.getElementById('action').value='createProperty';document.metrics.submit();\">\n                            </td>\n                          </tr>\n                        </tbody>\n                      </table>\n                    </fieldset>\n                  </fieldset>\n                  <input type=\"hidden\" id=\"action\" name=\"action\" value=\"\">\n                  <input type=\"hidden\" id=\"pluginHashcode\" name=\"pluginHashcode\" value=\"KnownHashCode\">\n                  <input type=\"hidden\" id=\"propertyName\" name=\"propertyName\" value=\"\">\n                  <input type=\"hidden\" id=\"propertyDescription\" name=\"propertyDescription\" value=\"\">\n                  <input type=\"hidden\" id=\"propertyType\" name=\"propertyType\" value=\"\">\n                  <input type=\"hidden\" id=\"propertyValue\" name=\"propertyValue\" value=\"\">\n                  <input type=\"hidden\" id=\"showProperties\" name=\"showProperties\" value=\"false\">\n                  <input type=\"hidden\" id=\"showActivators\" name=\"showActivators\" value=\"false\">\n                </form>\n";
		assertThat(result,equalTo(expectedResult));
	}
	
	/**
	 * Test method for {@link eu.sqooss.impl.service.webadmin.PluginsView#render(javax.servlet.http.HttpServletRequest)}.
	 */
	@Test
	public void testRenderNotNullWithPluginRequestAndKnowHashCodeInstalledWithMetrics() {
		String hashCode = "KnownHashCode";
		Plugin plugin = mock(Plugin.class);
		mockStatic(Plugin.class);
		when(Plugin.getPluginByHashcode(Matchers.anyString())).thenReturn(plugin);
		PluginAdmin admin = mock(PluginAdmin.class);
		Collection<PluginInfo> list = new ArrayList<PluginInfo>();
		PluginInfo pluginInfo = mock(PluginInfo.class);
		pluginInfo.installed = true;
		when(pluginInfo.getServiceRef()).thenReturn(mock(ServiceReference.class));
		list.add(pluginInfo);
		when(admin.listPlugins()).thenReturn(list);
		when(admin.getPluginInfo(hashCode)).thenReturn(pluginInfo);
		AlitheiaPlugin alitheiaPlugin = mock(AlitheiaPlugin.class);
		List<Metric> metricList = new ArrayList<Metric>();
		Metric metric = mock(Metric.class);
		when(metric.getMetricType()).thenReturn(mock(MetricType.class));
		metricList.add(metric);
		when(alitheiaPlugin.getAllSupportedMetrics()).thenReturn(metricList);
		when(admin.getPlugin(pluginInfo)).thenReturn(alitheiaPlugin);
		when(alitheiaCore.getPluginAdmin()).thenReturn(admin);
		instance = new PluginsView(bundleContext,velocityContext);
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getParameter(Matchers.any(String.class))).thenReturn(hashCode);
		String result = PluginsView.render(req);
		String expectedResult = "\n            <form id=\"metrics\" name=\"metrics\" method=\"post\" action=\"/index\">\n              <fieldset>\n                <legend>null</legend>\n                <table>\n                  <thead>\n                    <tr class=\"head\">\n                      <td class=\"head\" style=\"width: 80px;\">Status</td>\n                      <td class=\"head\" style=\"width: 30%;\">Name</td>\n                      <td class=\"head\" style=\"width: 40%;\">Class</td>\n                      <td class=\"head\">Version</td>\n                    </tr>\n                  </thead>\n                  <tbody>\n                  <tr>\n                      <td>Installed</td>\n                      <td>null</td>\n                      <td>null</td>\n                      <td>null</td>\n                    </tr>\n                    <tr>\n                      <td colspan=\"4\">\n                        <input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Plug-ins list\" onclick=\"javascript:document.getElementById('pluginHashcode').value='';document.metrics.submit();\">\n                        <input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Uninstall\" onclick=\"javascript:document.getElementById('action').value='uninstallPlugin';document.getElementById('pluginHashcode').value='null';document.metrics.submit();\">\n                        <input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Synchronise\" onclick=\"javascript:document.getElementById('action').value='syncPlugin';document.getElementById('pluginHashcode').value='null';document.metrics.submit();\">\n                      </td>\n                    </tr>\n                  </tbody>\n                </table>\n                  <fieldset>\n                    <legend>Supported metrics</legend>\n                    <table>\n                      <thead>\n                        <tr class=\"head\">\n                          <td class=\"head\" style=\"width: 10%;\">Id</td>\n                          <td class=\"head\" style=\"width: 25%;\">Name</td>\n                          <td class=\"head\" style=\"width: 25%;\">Type</td>\n                          <td class=\"head\" style=\"width: 40%;\">Description</td>\n                        </tr>\n                      </thead>\n                      <tbody>\n                        <tr>\n                          <td>0</td>\n                          <td>null</td>\n                          <td>null</td>\n                          <td>null</td>\n                        </tr>\n                      </tbody>\n                    </table>\n                  </fieldset>\n                    <fieldset>\n                      <legend>Configuration properties</legend>\n                      <table>\n                        <thead>\n                          <tr class=\"head\">\n                            <td class=\"head\" style=\"width: 30%;\">Name</td>\n                            <td class=\"head\" style=\"width: 20%;\">Type</td>\n                            <td class=\"head\" style=\"width: 50%;\">Value</td>\n                          </tr>\n                        </thead>\n                        <tbody>\n                          <tr>                            <td colspan=\"3\" class=\"noattr\">This plug-in has no configuration properties.</td>\n                          </tr>\n                          <tr>\n                            <td colspan=\"3\">\n                              <input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Add property\" onclick=\"javascript:document.getElementById('action').value='createProperty';document.metrics.submit();\">\n                            </td>\n                          </tr>\n                        </tbody>\n                      </table>\n                    </fieldset>\n                  </fieldset>\n                  <input type=\"hidden\" id=\"action\" name=\"action\" value=\"\">\n                  <input type=\"hidden\" id=\"pluginHashcode\" name=\"pluginHashcode\" value=\"KnownHashCode\">\n                  <input type=\"hidden\" id=\"propertyName\" name=\"propertyName\" value=\"\">\n                  <input type=\"hidden\" id=\"propertyDescription\" name=\"propertyDescription\" value=\"\">\n                  <input type=\"hidden\" id=\"propertyType\" name=\"propertyType\" value=\"\">\n                  <input type=\"hidden\" id=\"propertyValue\" name=\"propertyValue\" value=\"\">\n                  <input type=\"hidden\" id=\"showProperties\" name=\"showProperties\" value=\"false\">\n                  <input type=\"hidden\" id=\"showActivators\" name=\"showActivators\" value=\"false\">\n                </form>\n";
		assertThat(result,equalTo(expectedResult));
	}
	
	/**
	 * Test method for {@link eu.sqooss.impl.service.webadmin.PluginsView#render(javax.servlet.http.HttpServletRequest)}.
	 */
	@Test
	public void testRenderNotNullWithPluginRequestAndKnowHashCodeInstalledWithConfiguration() {
		String hashCode = "KnownHashCode";
		Plugin plugin = mock(Plugin.class);
		Set<PluginConfiguration> pluginConfigurationSet = new HashSet<PluginConfiguration>();
		PluginConfiguration pluginConfiguration = mock(PluginConfiguration.class);
		pluginConfigurationSet.add(pluginConfiguration);
		when(plugin.getConfigurations()).thenReturn(pluginConfigurationSet);
		mockStatic(Plugin.class);
		when(Plugin.getPluginByHashcode(Matchers.anyString())).thenReturn(plugin);
		PluginAdmin admin = mock(PluginAdmin.class);
		Collection<PluginInfo> list = new ArrayList<PluginInfo>();
		PluginInfo pluginInfo = mock(PluginInfo.class);
		pluginInfo.installed = true;
		when(pluginInfo.getServiceRef()).thenReturn(mock(ServiceReference.class));
		list.add(pluginInfo);
		when(admin.listPlugins()).thenReturn(list);
		when(admin.getPluginInfo(hashCode)).thenReturn(pluginInfo);
		AlitheiaPlugin alitheiaPlugin = mock(AlitheiaPlugin.class);
		when(alitheiaPlugin.getAllSupportedMetrics()).thenReturn(null);
		when(admin.getPlugin(pluginInfo)).thenReturn(alitheiaPlugin);
		when(alitheiaCore.getPluginAdmin()).thenReturn(admin);
		instance = new PluginsView(bundleContext,velocityContext);
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getParameter(Matchers.any(String.class))).thenReturn(hashCode);
		String result = PluginsView.render(req);
		String expectedResult = "\n            <form id=\"metrics\" name=\"metrics\" method=\"post\" action=\"/index\">\n              <fieldset>\n                <legend>null</legend>\n                <table>\n                  <thead>\n                    <tr class=\"head\">\n                      <td class=\"head\" style=\"width: 80px;\">Status</td>\n                      <td class=\"head\" style=\"width: 30%;\">Name</td>\n                      <td class=\"head\" style=\"width: 40%;\">Class</td>\n                      <td class=\"head\">Version</td>\n                    </tr>\n                  </thead>\n                  <tbody>\n                  <tr>\n                      <td>Installed</td>\n                      <td>null</td>\n                      <td>null</td>\n                      <td>null</td>\n                    </tr>\n                    <tr>\n                      <td colspan=\"4\">\n                        <input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Plug-ins list\" onclick=\"javascript:document.getElementById('pluginHashcode').value='';document.metrics.submit();\">\n                        <input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Uninstall\" onclick=\"javascript:document.getElementById('action').value='uninstallPlugin';document.getElementById('pluginHashcode').value='null';document.metrics.submit();\">\n                        <input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Synchronise\" onclick=\"javascript:document.getElementById('action').value='syncPlugin';document.getElementById('pluginHashcode').value='null';document.metrics.submit();\">\n                      </td>\n                    </tr>\n                  </tbody>\n                </table>\n                  <fieldset>\n                    <legend>Supported metrics</legend>\n                    <table>\n                      <thead>\n                        <tr class=\"head\">\n                          <td class=\"head\" style=\"width: 10%;\">Id</td>\n                          <td class=\"head\" style=\"width: 25%;\">Name</td>\n                          <td class=\"head\" style=\"width: 25%;\">Type</td>\n                          <td class=\"head\" style=\"width: 40%;\">Description</td>\n                        </tr>\n                      </thead>\n                      <tbody>\n                        <tr>                          <td colspan=\"4\" class=\"noattr\">This plug-in does not support metrics.</td>\n                        </tr>\n                      </tbody>\n                    </table>\n                  </fieldset>\n                    <fieldset>\n                      <legend>Configuration properties</legend>\n                      <table>\n                        <thead>\n                          <tr class=\"head\">\n                            <td class=\"head\" style=\"width: 30%;\">Name</td>\n                            <td class=\"head\" style=\"width: 20%;\">Type</td>\n                            <td class=\"head\" style=\"width: 50%;\">Value</td>\n                          </tr>\n                        </thead>\n                        <tbody>\n                          <tr class=\"edit\" onclick=\"javascript:document.getElementById('action').value='updateProperty';document.getElementById('propertyName').value='null';document.getElementById('propertyType').value='null';document.getElementById('propertyDescription').value='null';document.getElementById('propertyValue').value='null';document.metrics.submit();\">\n                            <td class=\"trans\" title=\"No description available.\"><img src=\"/edit.png\" alt=\"[Edit]\"/>&nbsp;null</td>\n                            <td class=\"trans\">null</td>\n                            <td class=\"trans\">null</td>\n                          </tr>\n                          <tr>\n                            <td colspan=\"3\">\n                              <input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Add property\" onclick=\"javascript:document.getElementById('action').value='createProperty';document.metrics.submit();\">\n                            </td>\n                          </tr>\n                        </tbody>\n                      </table>\n                    </fieldset>\n                  </fieldset>\n                  <input type=\"hidden\" id=\"action\" name=\"action\" value=\"\">\n                  <input type=\"hidden\" id=\"pluginHashcode\" name=\"pluginHashcode\" value=\"KnownHashCode\">\n                  <input type=\"hidden\" id=\"propertyName\" name=\"propertyName\" value=\"\">\n                  <input type=\"hidden\" id=\"propertyDescription\" name=\"propertyDescription\" value=\"\">\n                  <input type=\"hidden\" id=\"propertyType\" name=\"propertyType\" value=\"\">\n                  <input type=\"hidden\" id=\"propertyValue\" name=\"propertyValue\" value=\"\">\n                  <input type=\"hidden\" id=\"showProperties\" name=\"showProperties\" value=\"false\">\n                  <input type=\"hidden\" id=\"showActivators\" name=\"showActivators\" value=\"false\">\n                </form>\n";
		assertThat(result,equalTo(expectedResult));
	}
	
	/**
	 * Test method for {@link eu.sqooss.impl.service.webadmin.PluginsView#render(javax.servlet.http.HttpServletRequest)}.
	 */
	@Test
	public void testRenderNotNullWithPluginRequestAndInstallPluginHashcodeNoInstall() {
		String hashCode = "installPlugin";
		Plugin plugin = mock(Plugin.class);
		Set<PluginConfiguration> pluginConfigurationSet = new HashSet<PluginConfiguration>();
		PluginConfiguration pluginConfiguration = mock(PluginConfiguration.class);
		pluginConfigurationSet.add(pluginConfiguration);
		when(plugin.getConfigurations()).thenReturn(pluginConfigurationSet);
		mockStatic(Plugin.class);
		when(Plugin.getPluginByHashcode(Matchers.anyString())).thenReturn(plugin);
		PluginAdmin admin = mock(PluginAdmin.class);
		Collection<PluginInfo> list = new ArrayList<PluginInfo>();
		PluginInfo pluginInfo = mock(PluginInfo.class);
		pluginInfo.installed = true;
		when(pluginInfo.getServiceRef()).thenReturn(mock(ServiceReference.class));
		list.add(pluginInfo);
		when(admin.listPlugins()).thenReturn(list);
		when(admin.getPluginInfo(hashCode)).thenReturn(pluginInfo);
		AlitheiaPlugin alitheiaPlugin = mock(AlitheiaPlugin.class);
		when(alitheiaPlugin.getAllSupportedMetrics()).thenReturn(null);
		when(admin.getPlugin(pluginInfo)).thenReturn(alitheiaPlugin);
		when(alitheiaCore.getPluginAdmin()).thenReturn(admin);
		instance = new PluginsView(bundleContext,velocityContext);
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getParameter(Matchers.any(String.class))).thenReturn(hashCode);
		String result = PluginsView.render(req);
		String expectedResult = "\n            <form id=\"metrics\" name=\"metrics\" method=\"post\" action=\"/index\">\n              <fieldset>\n                <legend>Errors</legend>\nPlug-in can not be installed! Check log for details.              </fieldset>\n              <fieldset>\n                <legend>null</legend>\n                <table>\n                  <thead>\n                    <tr class=\"head\">\n                      <td class=\"head\" style=\"width: 80px;\">Status</td>\n                      <td class=\"head\" style=\"width: 30%;\">Name</td>\n                      <td class=\"head\" style=\"width: 40%;\">Class</td>\n                      <td class=\"head\">Version</td>\n                    </tr>\n                  </thead>\n                  <tbody>\n                  <tr>\n                      <td>Installed</td>\n                      <td>null</td>\n                      <td>null</td>\n                      <td>null</td>\n                    </tr>\n                    <tr>\n                      <td colspan=\"4\">\n                        <input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Plug-ins list\" onclick=\"javascript:document.getElementById('pluginHashcode').value='';document.metrics.submit();\">\n                        <input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Uninstall\" onclick=\"javascript:document.getElementById('action').value='uninstallPlugin';document.getElementById('pluginHashcode').value='null';document.metrics.submit();\">\n                        <input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Synchronise\" onclick=\"javascript:document.getElementById('action').value='syncPlugin';document.getElementById('pluginHashcode').value='null';document.metrics.submit();\">\n                      </td>\n                    </tr>\n                  </tbody>\n                </table>\n                  <fieldset>\n                    <legend>Supported metrics</legend>\n                    <table>\n                      <thead>\n                        <tr class=\"head\">\n                          <td class=\"head\" style=\"width: 10%;\">Id</td>\n                          <td class=\"head\" style=\"width: 25%;\">Name</td>\n                          <td class=\"head\" style=\"width: 25%;\">Type</td>\n                          <td class=\"head\" style=\"width: 40%;\">Description</td>\n                        </tr>\n                      </thead>\n                      <tbody>\n                        <tr>                          <td colspan=\"4\" class=\"noattr\">This plug-in does not support metrics.</td>\n                        </tr>\n                      </tbody>\n                    </table>\n                  </fieldset>\n                    <fieldset>\n                      <legend>Configuration properties</legend>\n                      <table>\n                        <thead>\n                          <tr class=\"head\">\n                            <td class=\"head\" style=\"width: 30%;\">Name</td>\n                            <td class=\"head\" style=\"width: 20%;\">Type</td>\n                            <td class=\"head\" style=\"width: 50%;\">Value</td>\n                          </tr>\n                        </thead>\n                        <tbody>\n                          <tr class=\"edit\" onclick=\"javascript:document.getElementById('action').value='updateProperty';document.getElementById('propertyName').value='null';document.getElementById('propertyType').value='null';document.getElementById('propertyDescription').value='null';document.getElementById('propertyValue').value='null';document.metrics.submit();\">\n                            <td class=\"trans\" title=\"No description available.\"><img src=\"/edit.png\" alt=\"[Edit]\"/>&nbsp;null</td>\n                            <td class=\"trans\">null</td>\n                            <td class=\"trans\">null</td>\n                          </tr>\n                          <tr>\n                            <td colspan=\"3\">\n                              <input type=\"button\" class=\"install\" style=\"width: 100px;\" value=\"Add property\" onclick=\"javascript:document.getElementById('action').value='createProperty';document.metrics.submit();\">\n                            </td>\n                          </tr>\n                        </tbody>\n                      </table>\n                    </fieldset>\n                  </fieldset>\n                  <input type=\"hidden\" id=\"action\" name=\"action\" value=\"\">\n                  <input type=\"hidden\" id=\"pluginHashcode\" name=\"pluginHashcode\" value=\"installPlugin\">\n                  <input type=\"hidden\" id=\"propertyName\" name=\"propertyName\" value=\"\">\n                  <input type=\"hidden\" id=\"propertyDescription\" name=\"propertyDescription\" value=\"\">\n                  <input type=\"hidden\" id=\"propertyType\" name=\"propertyType\" value=\"\">\n                  <input type=\"hidden\" id=\"propertyValue\" name=\"propertyValue\" value=\"\">\n                  <input type=\"hidden\" id=\"showProperties\" name=\"showProperties\" value=\"false\">\n                  <input type=\"hidden\" id=\"showActivators\" name=\"showActivators\" value=\"false\">\n                </form>\n";
		assertThat(result,equalTo(expectedResult));
	}



}
