package eu.sqooss.impl.service.webadmin;

import static eu.sqooss.impl.service.webadmin.HTMLTestUtils.sanitizeHTML;
import static eu.sqooss.impl.service.webadmin.PluginsView.ACT_VAL_CON_PROP;
import static eu.sqooss.impl.service.webadmin.PluginsView.ACT_VAL_CON_REM_PROP;
import static eu.sqooss.impl.service.webadmin.PluginsView.ACT_VAL_INSTALL_PLUGIN;
import static eu.sqooss.impl.service.webadmin.PluginsView.ACT_VAL_REQ_ADD_PROP;
import static eu.sqooss.impl.service.webadmin.PluginsView.ACT_VAL_REQ_UPD_PROP;
import static eu.sqooss.impl.service.webadmin.PluginsView.ACT_VAL_SYNC_PLUGIN;
import static eu.sqooss.impl.service.webadmin.PluginsView.ACT_VAL_UNINSTALL_PLUGIN;
import static eu.sqooss.impl.service.webadmin.PluginsView.REQ_PAR_ACTION;
import static eu.sqooss.impl.service.webadmin.PluginsView.REQ_PAR_HASHCODE;
import static eu.sqooss.impl.service.webadmin.PluginsView.REQ_PAR_PROP_DESC;
import static eu.sqooss.impl.service.webadmin.PluginsView.REQ_PAR_PROP_NAME;
import static eu.sqooss.impl.service.webadmin.PluginsView.REQ_PAR_PROP_TYPE;
import static eu.sqooss.impl.service.webadmin.PluginsView.REQ_PAR_PROP_VALUE;
import static eu.sqooss.impl.service.webadmin.PluginsView.REQ_PAR_SHOW_ACTV;
import static eu.sqooss.impl.service.webadmin.PluginsView.REQ_PAR_SHOW_PROP;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.text.IsEmptyString.isEmptyString;
import static org.hamcrest.text.IsEqualIgnoringWhiteSpace.equalToIgnoringWhiteSpace;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.xmlmatchers.transform.XmlConverters.the;
import static org.xmlmatchers.xpath.HasXPath.hasXPath;
import static org.xmlmatchers.xpath.XpathReturnType.returningANumber;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;

@RunWith(MockitoJUnitRunner.class)
public class PluginsViewTest { 
	@Mock private PluginAdmin pluginAdmin;
	@Mock private MetricActivator metricActivator;
	@Mock private ServiceReference serviceReference1;
	@Mock private ServiceReference serviceReference2;
	private PluginsView pluginsView;
	private PluginInfo pluginInfo1;
	private PluginInfo pluginInfo2;
	@Mock private AlitheiaPlugin plugin1;
	@Mock private Plugin dbPlugin1;
	@Mock private AlitheiaPlugin plugin2;
	@Mock private Plugin dbPlugin2;
	@Mock private Plugin dbRandomPlugin;

	@Before
	public void setUp() {
		pluginsView = new TestablePluginsView(null, null);
		
		pluginInfo1 = new PluginInfo();
		pluginInfo1.setHashcode("plugin1");
		pluginInfo1.setPluginName("plugin1");
		pluginInfo1.setServiceRef(serviceReference1);
		pluginInfo1.setPluginVersion("v1");
		when(serviceReference1.getProperty(Constants.OBJECTCLASS)).thenReturn(new String[] { "a", "b" });
		when(pluginAdmin.getPluginInfo("plugin1")).thenReturn(pluginInfo1);

		pluginInfo2 = new PluginInfo();
		pluginInfo2.installed = true;
		pluginInfo2.setHashcode("plugin2");
		pluginInfo2.setPluginName("plugin2");
		pluginInfo2.setServiceRef(serviceReference2);
		when(serviceReference2.getProperty(Constants.OBJECTCLASS)).thenReturn(new String[] { "c", "d" });
		pluginInfo2.setPluginVersion("v2");
		when(pluginAdmin.listPlugins()).thenReturn(Arrays.asList(pluginInfo1, pluginInfo2));
		when(pluginAdmin.getPluginInfo("plugin2")).thenReturn(pluginInfo2);
		when(pluginAdmin.getPlugin(pluginInfo2)).thenReturn(plugin2);
	}
	
	@Test
	public void shouldRenderMessageWhenNoPluginsFound() throws Exception {
		when(pluginAdmin.listPlugins()).thenReturn(Arrays.asList(new PluginInfo[0]));
		
		String output = pluginsView.render(null);
		
		String html = sanitizeHTML(output);
		
		// There is an error message displayed.
		assertThat(the(html), hasXPath("/root/fieldset[legend/text()='All plug-ins']/span[contains(text(), 'No plug-ins found!')]"));
	}
	
	@Test
	public void shouldRenderFormWhenPluginsFoundButNoRequest() throws Exception {
		String output = pluginsView.render(null);
		
		String html = sanitizeHTML(output);
		
		// the form is created
		assertThat(the(html), hasXPath("//form[@id='metrics' and @name='metrics' and @method='post' and @action='/index']"));
	}
	
	@Test
	public void shouldSetShowPropToFalseWhenNotPresent() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		
		String output = pluginsView.render(request);
		String html = sanitizeHTML(output);
		
		assertThat(the(html), hasXPath("//form[@id='metrics']/input[@id='" + REQ_PAR_SHOW_PROP + "']/@value", equalTo("false")));
	}

	@Test
	public void shouldSetShowPropToFalseWhenNotTrue() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(REQ_PAR_SHOW_PROP)).thenReturn("some_value");
		
		String output = pluginsView.render(request);
		String html = sanitizeHTML(output);
		
		assertThat(the(html), hasXPath("//form[@id='metrics']/input[@id='" + REQ_PAR_SHOW_PROP + "']/@value", equalTo("false")));
	}

	@Test
	public void shouldSetShowPropToTrueWhenTrue() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(REQ_PAR_SHOW_PROP)).thenReturn("true");
		
		String output = pluginsView.render(request);
		String html = sanitizeHTML(output);
		
		assertThat(the(html), hasXPath("//form[@id='metrics']/input[@id='" + REQ_PAR_SHOW_PROP + "']/@value", equalTo("true")));
	}

	@Test
	public void shouldSetShowActvToFalseWhenNotPresent() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		
		String output = pluginsView.render(request);
		String html = sanitizeHTML(output);
		
		assertThat(the(html), hasXPath("//form[@id='metrics']/input[@id='" + REQ_PAR_SHOW_ACTV + "']/@value", equalTo("false")));
	}
	
	@Test
	public void shouldSetShowActvToFalseWhenNotTrue() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(REQ_PAR_SHOW_ACTV)).thenReturn("some_value");
		
		String output = pluginsView.render(request);
		String html = sanitizeHTML(output);
		
		assertThat(the(html), hasXPath("//form[@id='metrics']/input[@id='" + REQ_PAR_SHOW_ACTV + "']/@value", equalTo("false")));
	}
	
	@Test
	public void shouldSetShowActvToTrueWhenTrue() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(REQ_PAR_SHOW_ACTV)).thenReturn("true");
		
		String output = pluginsView.render(request);
		String html = sanitizeHTML(output);
		
		assertThat(the(html), hasXPath("//form[@id='metrics']/input[@id='" + REQ_PAR_SHOW_ACTV + "']/@value", equalTo("true")));
	}
	
	@Test
	public void shouldRenderPluginsListWhenPluginsFoundButNoRequest() throws Exception {
		String output = pluginsView.render(null);
		
		String html = sanitizeHTML(output);
		
		// there is a fieldset listing all plugins
		assertThat(the(html), hasXPath("//form/fieldset[legend/text()='All plug-ins']"));
		// the table has a header row
		assertThat(the(html), hasXPath("//form/fieldset[legend/text()='All plug-ins']/table/thead"));
		// the table body
		assertThat(the(html), hasXPath("//form/fieldset[legend/text()='All plug-ins']/table/tbody"));

		String submit = "document.metrics.submit();";
		String setHashcode1 = setElementValueString(REQ_PAR_HASHCODE, "plugin1");
		String onclick1 = "javascript:" + setHashcode1 + submit;
		assertThat(the(html), hasXPath("//form/fieldset[legend/text()='All plug-ins']/table/tbody/tr[1]/@onclick", equalTo(onclick1)));
		assertThat(the(html), hasXPath("string-join(//form/fieldset[legend/text()='All plug-ins']/table/tbody/tr[1]/td[1]/text(), '')", containsString("Registered")));
		assertThat(the(html), hasXPath("//form/fieldset[legend/text()='All plug-ins']/table/tbody/tr[1]/td[2]/text()", equalTo("plugin1")));
		assertThat(the(html), hasXPath("//form/fieldset[legend/text()='All plug-ins']/table/tbody/tr[1]/td[3]/text()", equalTo("a,b")));
		assertThat(the(html), hasXPath("//form/fieldset[legend/text()='All plug-ins']/table/tbody/tr[1]/td[4]/text()", equalTo("v1")));

		String onclick2 = "javascript:" + setElementValueString(REQ_PAR_HASHCODE, "plugin2") + submit;
		assertThat(the(html), hasXPath("//form/fieldset[legend/text()='All plug-ins']/table/tbody/tr[2]/@onclick", equalTo(onclick2)));
		assertThat(the(html), hasXPath("string-join(//form/fieldset[legend/text()='All plug-ins']/table/tbody/tr[2]/td[1]/text(), '')", containsString("Installed")));
		assertThat(the(html), hasXPath("//form/fieldset[legend/text()='All plug-ins']/table/tbody/tr[2]/td[2]/text()", equalTo("plugin2")));
		assertThat(the(html), hasXPath("//form/fieldset[legend/text()='All plug-ins']/table/tbody/tr[2]/td[3]/text()", equalTo("c,d")));
		assertThat(the(html), hasXPath("//form/fieldset[legend/text()='All plug-ins']/table/tbody/tr[2]/td[4]/text()", equalTo("v2")));
	}
	
	@Test
	public void shouldRenderOnlyFormWithEmptyRequest() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		
		String output = pluginsView.render(request);
		String html = sanitizeHTML(output);
		
		assertThat(the(html), hasXPath("count(/root/*)", returningANumber(), equalTo(1.0)));
	}
	
	@Test
	public void shouldRenderOnlyFormWithRequestHashCodeButNotInstalled() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(REQ_PAR_HASHCODE)).thenReturn("plugin1");
		
		String output = pluginsView.render(request);
		String html = sanitizeHTML(output);
		
		assertThat(the(html), hasXPath("count(/root/*)", returningANumber(), equalTo(1.0)));
		assertThat(the(html), hasXPath("count(/root/form/fieldset)", returningANumber(), equalTo(1.0)));
		assertThat(the(html), hasXPath("count(/root/form/fieldset/table/tbody/tr)", returningANumber(), equalTo(2.0)));
		assertThat(the(html), hasXPath("/root/form/fieldset/table/tbody/tr[1]/td[1]/text()", equalTo("Registered")));
		assertThat(the(html), not(hasXPath("/root/form/fieldset/fieldset")));
		assertThat(the(html), hasXPath("/root/form/fieldset//input[@value='Install']"));
		assertThat(the(html), not(hasXPath("/root/form/fieldset//input[@value='Uninstall']")));
		assertThat(the(html), not(hasXPath("/root/form/fieldset//input[@value='Synchronise']")));
	}

	@Test
	public void shouldRenderOnlyFormWithRequestHashCodeInstalled() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(REQ_PAR_HASHCODE)).thenReturn("plugin2");
		
		String output = pluginsView.render(request);
		String html = sanitizeHTML(output);
		
		assertThat(the(html), hasXPath("count(/root/*)", returningANumber(), equalTo(1.0)));
		assertThat(the(html), hasXPath("count(/root/form/fieldset)", returningANumber(), equalTo(1.0)));
		assertThat(the(html), hasXPath("count(/root/form/fieldset/table/tbody/tr)", returningANumber(), equalTo(2.0)));
		assertThat(the(html), hasXPath("/root/form/fieldset/table/tbody/tr[1]/td[1]/text()", equalTo("Installed")));
		assertThat(the(html), hasXPath("/root/form/fieldset/fieldset[legend/text()='Supported metrics']"));
		assertThat(the(html), hasXPath("/root/form/fieldset/fieldset[legend/text()='Supported metrics']//*[text()='This plug-in does not support metrics.']"));
		assertThat(the(html), hasXPath("/root/form/fieldset/fieldset[legend/text()='Configuration properties']"));
		assertThat(the(html), hasXPath("/root/form/fieldset/fieldset[legend/text()='Configuration properties']//*[text()='This plug-in has no configuration properties.']"));
		assertThat(the(html), not(hasXPath("/root/form/fieldset//input[@value='Install']")));
		assertThat(the(html), hasXPath("/root/form/fieldset//input[@value='Uninstall']"));
		assertThat(the(html), hasXPath("/root/form/fieldset//input[@value='Synchronise']"));
	}
	
	@Test
	public void shouldRenderMetricRowPerMetric() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(REQ_PAR_HASHCODE)).thenReturn("plugin2");
		
		Metric metric1 = new Metric();
		metric1.setId(1);
		metric1.setMnemonic("m1");
		MetricType metricType1 = mock(MetricType.class);
		when(metricType1.getType()).thenReturn("t1");
		metric1.setMetricType(metricType1);
		metric1.setDescription("d1");
		Metric metric2 = new Metric();
		metric2.setId(2);
		metric2.setMnemonic("m2");
		MetricType metricType2 = mock(MetricType.class);
		when(metricType2.getType()).thenReturn("t2");
		metric2.setMetricType(metricType2);
		metric2.setDescription("d2");
		when(plugin2.getAllSupportedMetrics()).thenReturn(Arrays.asList(metric1, metric2));
		
		String output = pluginsView.render(request);
		String html = sanitizeHTML(output);

		assertThat(the(html), hasXPath("/root/form/fieldset/fieldset[legend/text()='Supported metrics']/table/tbody/tr[1]/td[1]/text()", equalTo("1")));
		assertThat(the(html), hasXPath("/root/form/fieldset/fieldset[legend/text()='Supported metrics']/table/tbody/tr[1]/td[2]/text()", equalTo("m1")));
		assertThat(the(html), hasXPath("/root/form/fieldset/fieldset[legend/text()='Supported metrics']/table/tbody/tr[1]/td[3]/text()", equalTo("t1")));
		assertThat(the(html), hasXPath("/root/form/fieldset/fieldset[legend/text()='Supported metrics']/table/tbody/tr[1]/td[4]/text()", equalTo("d1")));
		assertThat(the(html), hasXPath("/root/form/fieldset/fieldset[legend/text()='Supported metrics']/table/tbody/tr[2]/td[1]/text()", equalTo("2")));
		assertThat(the(html), hasXPath("/root/form/fieldset/fieldset[legend/text()='Supported metrics']/table/tbody/tr[2]/td[2]/text()", equalTo("m2")));
		assertThat(the(html), hasXPath("/root/form/fieldset/fieldset[legend/text()='Supported metrics']/table/tbody/tr[2]/td[3]/text()", equalTo("t2")));
		assertThat(the(html), hasXPath("/root/form/fieldset/fieldset[legend/text()='Supported metrics']/table/tbody/tr[2]/td[4]/text()", equalTo("d2")));
	}

	@Test
	public void shouldRenderConfigurationRowPerConfiguration() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(REQ_PAR_HASHCODE)).thenReturn("plugin2");
		
		PluginConfiguration pc1 = new PluginConfiguration();
		pc1.setName("pc1");
		pc1.setType("pct1");
		pc1.setMsg("pcd1");
		pc1.setValue("pcv1");
		PluginConfiguration pc2 = new PluginConfiguration();
		pc2.setName("pc2");
		pc2.setType("pct2");
		pc2.setValue("pcv2");
		Set<PluginConfiguration> pcSet = new TreeSet<PluginConfiguration>(new Comparator<PluginConfiguration>(){
			@Override
			public int compare(PluginConfiguration o1, PluginConfiguration o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		pcSet.addAll(Arrays.asList(pc1, pc2));
		when(dbPlugin2.getConfigurations()).thenReturn(pcSet);
		
		String output = pluginsView.render(request);
		String html = sanitizeHTML(output);
		
		String submit = "document.metrics.submit();";
		String setAction1 = setElementValueString(REQ_PAR_ACTION, ACT_VAL_REQ_UPD_PROP);
		String setPropName1 = setElementValueString(REQ_PAR_PROP_NAME, "pc1");
		String setPropType1 = setElementValueString(REQ_PAR_PROP_TYPE, "pct1");
		String setPropDesc1 = setElementValueString(REQ_PAR_PROP_DESC, "pcd1");
		String setPropVal1 = setElementValueString(REQ_PAR_PROP_VALUE, "pcv1");
		String onclick1 = "javascript:" + setAction1 + setPropName1 + setPropType1 + setPropDesc1 + setPropVal1 + submit;
		assertThat(the(html), hasXPath("/root/form/fieldset/fieldset[legend/text()='Configuration properties']/table/tbody/tr[1]/@onclick", equalTo(onclick1)));
		assertThat(the(html), hasXPath("/root/form/fieldset/fieldset[legend/text()='Configuration properties']/table/tbody/tr[1]/td[1]/@title", equalTo("pcd1")));
		assertThat(the(html), hasXPath("/root/form/fieldset/fieldset[legend/text()='Configuration properties']/table/tbody/tr[1]/td[1]/text()", containsString("pc1")));
		assertThat(the(html), hasXPath("/root/form/fieldset/fieldset[legend/text()='Configuration properties']/table/tbody/tr[1]/td[2]/text()", equalTo("pct1")));
		assertThat(the(html), hasXPath("/root/form/fieldset/fieldset[legend/text()='Configuration properties']/table/tbody/tr[1]/td[3]/text()", equalTo("pcv1")));
		
		String setAction2 = setElementValueString(REQ_PAR_ACTION, ACT_VAL_REQ_UPD_PROP);
		String setPropName2 = setElementValueString(REQ_PAR_PROP_NAME, "pc2");
		String setPropType2 = setElementValueString(REQ_PAR_PROP_TYPE, "pct2");
		String setPropDesc2 = setElementValueString(REQ_PAR_PROP_DESC, "null");
		String setPropVal2 = setElementValueString(REQ_PAR_PROP_VALUE, "pcv2");
		String onclick2 = "javascript:" + setAction2 + setPropName2 + setPropType2 + setPropDesc2 + setPropVal2 + submit;
		assertThat(the(html), hasXPath("/root/form/fieldset/fieldset[legend/text()='Configuration properties']/table/tbody/tr[2]/@onclick", equalTo(onclick2)));
		assertThat(the(html), hasXPath("/root/form/fieldset/fieldset[legend/text()='Configuration properties']/table/tbody/tr[2]/td[1]/@title", equalTo("No description available.")));
		assertThat(the(html), hasXPath("/root/form/fieldset/fieldset[legend/text()='Configuration properties']/table/tbody/tr[2]/td[1]/text()", containsString("pc2")));
		assertThat(the(html), hasXPath("/root/form/fieldset/fieldset[legend/text()='Configuration properties']/table/tbody/tr[2]/td[2]/text()", equalTo("pct2")));
		assertThat(the(html), hasXPath("/root/form/fieldset/fieldset[legend/text()='Configuration properties']/table/tbody/tr[2]/td[3]/text()", equalTo("pcv2")));
		
		Object onclickAdd = "javascript:" + setElementValueString(REQ_PAR_ACTION, ACT_VAL_REQ_ADD_PROP) + submit;
		assertThat(the(html), hasXPath("/root/form/fieldset/fieldset[legend/text()='Configuration properties']/table/tbody/tr[3]/td/input[@value='Add property']/@onclick", equalTo(onclickAdd)));
	}

	protected static String setElementValueString(String elementId, String value) {
		return "document.getElementById('" + elementId + "').value='" + value + "';";
	}
	
	@Test
	public void shouldNotOutputAnythingIfNotShowingPropertiesAndActivators() {
		String output = PluginsView.renderPluginAttributes(null, false, false, 0);
		
		assertThat(output, isEmptyString());
	}
	
	@Test
	public void shouldOutputNoPropertiesIfNullConfigurations() {
		PluginInfo pluginInfo = new PluginInfo();
		pluginInfo.setPluginConfiguration(null);
		
		String output = PluginsView.renderPluginAttributes(pluginInfo , true, false, 0);
		
		assertThat(output, isEmptyString());
	}
	
	@Test
	public void shouldOutputNoPropertiesIfNoConfigurations() {
		PluginInfo pluginInfo = new PluginInfo();
		
		String output = PluginsView.renderPluginAttributes(pluginInfo , true, false, 0);

		assertThat(output, isEmptyString());
	}
	
	@Test
	public void shouldOutputEachPropertyAsTableRow() {
		PluginInfo pluginInfo = new PluginInfo();
		PluginConfiguration c1 = new PluginConfiguration();
		c1.setName("c1");
		c1.setType("t1");
		c1.setValue("v1");
		PluginConfiguration c2 = new PluginConfiguration();
		c2.setName("c2");
		c2.setType("t2");
		c2.setValue("v2");
		Set<PluginConfiguration> set = new TreeSet<PluginConfiguration>(new Comparator<PluginConfiguration>() {
			@Override
			public int compare(PluginConfiguration o1, PluginConfiguration o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		set.addAll(Arrays.asList(c1, c2));
		pluginInfo.setPluginConfiguration(set);
		
		String html = PluginsView.renderPluginAttributes(pluginInfo , true, false, 0);
		html = "<root>" + html.replaceAll("&nbsp;", " ") + "</root>";
		
		assertThat(the(html), hasXPath("count(/root/tr)", returningANumber(), equalTo(2.0)));
		assertThat(the(html), hasXPath("string-join(/root/tr[1]/td[2]//text(), ' ')", equalToIgnoringWhiteSpace("Property: c1 Type: t1 Value: v1")));
		assertThat(the(html), hasXPath("string-join(/root/tr[2]/td[2]//text(), ' ')", equalToIgnoringWhiteSpace("Property: c2 Type: t2 Value: v2")));
	}
	
	@Test
	public void shouldOutputNoActivatorsIfNullActivators() throws Exception {
		PluginInfo pluginInfo = new PluginInfo();
		pluginInfo.setActivationTypes(null);
		
		String html = PluginsView.renderPluginAttributes(pluginInfo , false, true, 0);

		assertThat(html, isEmptyString());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldOutputEachActivatorAsTableRow() {
		PluginInfo pluginInfo = new PluginInfo();
		Set<Class<? extends DAObject>> set = new TreeSet<Class<? extends DAObject>>(new Comparator<Class<? extends DAObject>>() {
			@Override
			public int compare(Class<? extends DAObject> o1,
					Class<? extends DAObject> o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		set.addAll(Arrays.asList(TestActivator1.class, TestActivator2.class));
		pluginInfo.setActivationTypes(set);
		
		String html = PluginsView.renderPluginAttributes(pluginInfo , false, true, 0);
		html = "<root>" + html.replaceAll("&nbsp;", " ") + "</root>";
		
		assertThat(the(html), hasXPath("count(/root/tr)", returningANumber(), equalTo(2.0)));
		assertThat(the(html), hasXPath("string-join(/root/tr[1]/td[2]//text(), ' ')", equalToIgnoringWhiteSpace("Activator: " + TestActivator1.class.getName())));
		assertThat(the(html), hasXPath("string-join(/root/tr[2]/td[2]//text(), ' ')", equalToIgnoringWhiteSpace("Activator: " + TestActivator2.class.getName())));
	}
	
	@Test
	public void shouldPrintErrorIfInstallPluginFailed() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(REQ_PAR_HASHCODE)).thenReturn("plugin10");
		when(request.getParameter(REQ_PAR_ACTION)).thenReturn(ACT_VAL_INSTALL_PLUGIN);
		
		String output = pluginsView.render(request);
		
		assertThat(output, containsString("Plug-in can not be installed! Check log for details."));
	}

	@Test
	public void shouldUpdatePluginIfInstallPluginSucceeded() throws Exception {
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(REQ_PAR_HASHCODE)).thenReturn("plugin10");
		when(request.getParameter(REQ_PAR_ACTION)).thenReturn(ACT_VAL_INSTALL_PLUGIN);
		
		PluginInfo pluginInfo10 = new PluginInfo();
		pluginInfo10.setServiceRef(serviceReference1);
		AlitheiaPlugin plugin10 = mock(AlitheiaPlugin.class);
		when(pluginAdmin.installPlugin("plugin10")).thenReturn(true);
		when(pluginAdmin.getPluginInfo("plugin10")).thenReturn(pluginInfo10);
		when(pluginAdmin.getPlugin(pluginInfo10)).thenReturn(plugin10);
		
		pluginsView.render(request);

		verify(pluginAdmin).pluginUpdated(plugin10);
	}

	@Test
	public void shouldPrintErrorIfUninstallPluginFailed() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(REQ_PAR_HASHCODE)).thenReturn("plugin10");
		when(request.getParameter(REQ_PAR_ACTION)).thenReturn(ACT_VAL_UNINSTALL_PLUGIN);
		
		String output = pluginsView.render(request);
		
		assertThat(output, containsString("Plug-in can not be uninstalled. Check log for details."));
	}
	
	@Test
	public void shouldUpdatePluginIfUninstallPluginSucceeded() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(REQ_PAR_HASHCODE)).thenReturn("plugin10");
		when(request.getParameter(REQ_PAR_ACTION)).thenReturn(ACT_VAL_UNINSTALL_PLUGIN);
		
		when(pluginAdmin.uninstallPlugin("plugin10")).thenReturn(true);
		
		String output = pluginsView.render(request);
		
		assertThat(output, containsString("A job was scheduled to remove the plug-in"));
	}
	
	@Test
	public void shouldSyncMetricsWhenInstalledPlugin() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(REQ_PAR_HASHCODE)).thenReturn("plugin2");
		when(request.getParameter(REQ_PAR_ACTION)).thenReturn(ACT_VAL_SYNC_PLUGIN);
		
		pluginsView.render(request);
		
		verify(metricActivator).syncMetrics(plugin2);
	}
	
	@Test
	public void shouldNotRemovePropertyWhenPluginDoesNotHaveIt() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(REQ_PAR_HASHCODE)).thenReturn("plugin2");
		when(request.getParameter(REQ_PAR_ACTION)).thenReturn(ACT_VAL_CON_REM_PROP);
		
		PluginConfiguration pc1 = new PluginConfiguration();
		pc1.setName("pc1");
		pc1.setType("pct1");
		pc1.setMsg("pcd1");
		pc1.setValue("pcv1");
		pluginInfo2.setPluginConfiguration(new HashSet<PluginConfiguration>(Arrays.asList(pc1)));
		
		String output = pluginsView.render(request);
		
		assertThat(output, containsString("Unknown configuration property!"));
	}
	
	@Test
	public void shouldPrintErrorWhenPropertyRemovalFails() throws Exception {
		PluginInfo pluginInfo3 = mock(PluginInfo.class);
		when(pluginAdmin.getPluginInfo("plugin3")).thenReturn(pluginInfo3);
		when(pluginInfo3.hasConfProp("pc1", "pct1")).thenReturn(true);
		when(pluginInfo3.getServiceRef()).thenReturn(serviceReference1);
		pluginInfo3.installed = true;
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(REQ_PAR_HASHCODE)).thenReturn("plugin3");
		when(request.getParameter(REQ_PAR_ACTION)).thenReturn(ACT_VAL_CON_REM_PROP);
		when(request.getParameter(REQ_PAR_PROP_NAME)).thenReturn("pc1");
		when(request.getParameter(REQ_PAR_PROP_TYPE)).thenReturn("pct1");
		
		String output = pluginsView.render(request);
		
		assertThat(output, containsString("Property removal has failed! Check log for details."));
	}

	@Test
	public void shouldOutputExceptionMessageWhenRemoveConfigEntryThrows() throws Exception {
		PluginInfo pluginInfo3 = mock(PluginInfo.class);
		AlitheiaPlugin plugin3 = mock(AlitheiaPlugin.class);
		when(pluginAdmin.getPluginInfo("plugin3")).thenReturn(pluginInfo3);
		when(pluginAdmin.getPlugin(pluginInfo3)).thenReturn(plugin3);
		pluginInfo3.installed = true;
		when(pluginInfo3.hasConfProp("pc1", "pct1")).thenReturn(true);
		when(pluginInfo3.removeConfigEntry(any(DBService.class), eq("pc1"), eq("pct1"))).thenThrow(new Exception("EXCEPTION!"));
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(REQ_PAR_HASHCODE)).thenReturn("plugin3");
		when(request.getParameter(REQ_PAR_ACTION)).thenReturn(ACT_VAL_CON_REM_PROP);
		when(request.getParameter(REQ_PAR_PROP_NAME)).thenReturn("pc1");
		when(request.getParameter(REQ_PAR_PROP_TYPE)).thenReturn("pct1");
		
		String output = pluginsView.render(request);
		
		verify(pluginAdmin, times(0)).pluginUpdated(plugin3);
		assertThat(output, containsString("EXCEPTION!"));
	}
	
	@Test
	public void shouldUpdatePluginWhenPropertyRemovalSucceeds() throws Exception {
		PluginInfo pluginInfo3 = mock(PluginInfo.class);
		PluginInfo pluginInfo4 = mock(PluginInfo.class);
		AlitheiaPlugin plugin3 = mock(AlitheiaPlugin.class);
		when(pluginAdmin.getPluginInfo("plugin3")).thenReturn(pluginInfo3).thenReturn(pluginInfo4);
		when(pluginAdmin.getPlugin(pluginInfo3)).thenReturn(plugin3);
		pluginInfo3.installed = true;
		when(pluginInfo3.hasConfProp("pc1", "pct1")).thenReturn(true);
		when(pluginInfo3.removeConfigEntry(any(DBService.class), eq("pc1"), eq("pct1"))).thenReturn(true);
		when(pluginInfo4.getServiceRef()).thenReturn(serviceReference1);
		when(pluginInfo4.getPluginName()).thenReturn("PLUGIN#4");
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter(REQ_PAR_HASHCODE)).thenReturn("plugin3");
		when(request.getParameter(REQ_PAR_ACTION)).thenReturn(ACT_VAL_CON_REM_PROP);
		when(request.getParameter(REQ_PAR_PROP_NAME)).thenReturn("pc1");
		when(request.getParameter(REQ_PAR_PROP_TYPE)).thenReturn("pct1");
		
		String output = pluginsView.render(request);
		
		verify(pluginAdmin).pluginUpdated(plugin3);
		assertThat(output, containsString("PLUGIN#4"));
	}
	
	@Test
	public void shouldPrintWhenUpdateFailedIfPropertyAlreadyExists() throws Exception {
		PluginInfo pluginInfo3 = mock(PluginInfo.class);
		when(pluginAdmin.getPluginInfo("plugin3")).thenReturn(pluginInfo3);
		pluginInfo3.installed = true;
		when(pluginInfo3.hasConfProp("pc1", "pct1")).thenReturn(true);
		
		when(pluginInfo3.updateConfigEntry(any(DBService.class), eq("pc1"), anyString())).thenReturn(false);
		
		HttpServletRequest request = mock(HttpServletRequest.class);		
		when(request.getParameter(REQ_PAR_HASHCODE)).thenReturn("plugin3");
		when(request.getParameter(REQ_PAR_ACTION)).thenReturn(ACT_VAL_CON_PROP);
		when(request.getParameter(REQ_PAR_PROP_NAME)).thenReturn("pc1");
		when(request.getParameter(REQ_PAR_PROP_TYPE)).thenReturn("pct1");
		
		String output = pluginsView.render(request);
		
		assertThat(output, containsString("Property update has failed! Check log for details."));
	}

	@Test
	public void shouldPrintExceptionMessageWhenUpdateThrewIfPropertyAlreadyExists() throws Exception {
		PluginInfo pluginInfo3 = mock(PluginInfo.class);
		when(pluginAdmin.getPluginInfo("plugin3")).thenReturn(pluginInfo3);
		pluginInfo3.installed = true;
		when(pluginInfo3.hasConfProp("pc1", "pct1")).thenReturn(true);
		
		when(pluginInfo3.updateConfigEntry(any(DBService.class), eq("pc1"), anyString())).thenThrow(new Exception("EXCEPTION!"));
		
		HttpServletRequest request = mock(HttpServletRequest.class);		
		when(request.getParameter(REQ_PAR_HASHCODE)).thenReturn("plugin3");
		when(request.getParameter(REQ_PAR_ACTION)).thenReturn(ACT_VAL_CON_PROP);
		when(request.getParameter(REQ_PAR_PROP_NAME)).thenReturn("pc1");
		when(request.getParameter(REQ_PAR_PROP_TYPE)).thenReturn("pct1");
		
		String output = pluginsView.render(request);
		
		assertThat(output, containsString("EXCEPTION!"));
	}
	
	@Test
	public void shouldUpdatePluginWhenUpdateSucceededIfPropertyAlreadyExists() throws Exception {
		PluginInfo pluginInfo3 = mock(PluginInfo.class);
		PluginInfo pluginInfo4 = mock(PluginInfo.class);
		when(pluginAdmin.getPluginInfo("plugin3")).thenReturn(pluginInfo3).thenReturn(pluginInfo4);
		AlitheiaPlugin plugin3 = mock(AlitheiaPlugin.class);
		when(pluginAdmin.getPlugin(pluginInfo3)).thenReturn(plugin3);
		pluginInfo3.installed = true;
		when(pluginInfo3.hasConfProp("pc1", "pct1")).thenReturn(true);
		when(pluginInfo4.getPluginName()).thenReturn("PLUGIN#4");
		when(pluginInfo4.getServiceRef()).thenReturn(serviceReference1);
		
		when(pluginInfo3.updateConfigEntry(any(DBService.class), eq("pc1"), anyString())).thenReturn(true);
		
		HttpServletRequest request = mock(HttpServletRequest.class);		
		when(request.getParameter(REQ_PAR_HASHCODE)).thenReturn("plugin3");
		when(request.getParameter(REQ_PAR_ACTION)).thenReturn(ACT_VAL_CON_PROP);
		when(request.getParameter(REQ_PAR_PROP_NAME)).thenReturn("pc1");
		when(request.getParameter(REQ_PAR_PROP_TYPE)).thenReturn("pct1");
		
		String output = pluginsView.render(request);
		
		verify(pluginAdmin).pluginUpdated(plugin3);
		assertThat(output, containsString("PLUGIN#4"));
	}
	
	@Test
	public void shouldPrintWhenUpdateFailedIfPropertyDoesntExist() throws Exception {
		PluginInfo pluginInfo3 = mock(PluginInfo.class);
		when(pluginAdmin.getPluginInfo("plugin3")).thenReturn(pluginInfo3);
		pluginInfo3.installed = true;
		when(pluginInfo3.hasConfProp("pc1", "pct1")).thenReturn(false);
		
		when(pluginInfo3.addConfigEntry(any(DBService.class), eq("pc1"), anyString(), anyString(), anyString())).thenReturn(false);
		
		HttpServletRequest request = mock(HttpServletRequest.class);		
		when(request.getParameter(REQ_PAR_HASHCODE)).thenReturn("plugin3");
		when(request.getParameter(REQ_PAR_ACTION)).thenReturn(ACT_VAL_CON_PROP);
		when(request.getParameter(REQ_PAR_PROP_NAME)).thenReturn("pc1");
		when(request.getParameter(REQ_PAR_PROP_TYPE)).thenReturn("pct1");
		
		String output = pluginsView.render(request);
		
		assertThat(output, containsString("Property creation has failed! Check log for details."));
	}

	@Test
	public void shouldPrintExceptionMessageWhenUpdateThrewIfPropertyDoesntExist() throws Exception {
		PluginInfo pluginInfo3 = mock(PluginInfo.class);
		when(pluginAdmin.getPluginInfo("plugin3")).thenReturn(pluginInfo3);
		pluginInfo3.installed = true;
		when(pluginInfo3.hasConfProp("pc1", "pct1")).thenReturn(false);
		
		when(pluginInfo3.addConfigEntry(any(DBService.class), eq("pc1"), anyString(), anyString(), anyString())).thenThrow(new Exception("EXCEPTION!"));
		
		HttpServletRequest request = mock(HttpServletRequest.class);		
		when(request.getParameter(REQ_PAR_HASHCODE)).thenReturn("plugin3");
		when(request.getParameter(REQ_PAR_ACTION)).thenReturn(ACT_VAL_CON_PROP);
		when(request.getParameter(REQ_PAR_PROP_NAME)).thenReturn("pc1");
		when(request.getParameter(REQ_PAR_PROP_TYPE)).thenReturn("pct1");
		
		String output = pluginsView.render(request);
		
		assertThat(output, containsString("EXCEPTION!"));
	}
	
	@Test
	public void shouldUpdatePluginWhenUpdateSucceededIfPropertyDoesntExist() throws Exception {
		PluginInfo pluginInfo3 = mock(PluginInfo.class);
		PluginInfo pluginInfo4 = mock(PluginInfo.class);
		when(pluginAdmin.getPluginInfo("plugin3")).thenReturn(pluginInfo3).thenReturn(pluginInfo4);
		AlitheiaPlugin plugin3 = mock(AlitheiaPlugin.class);
		when(pluginAdmin.getPlugin(pluginInfo3)).thenReturn(plugin3);
		pluginInfo3.installed = true;
		when(pluginInfo3.hasConfProp("pc1", "pct1")).thenReturn(false);
		when(pluginInfo4.getPluginName()).thenReturn("PLUGIN#4");
		when(pluginInfo4.getServiceRef()).thenReturn(serviceReference1);
		
		when(pluginInfo3.addConfigEntry(any(DBService.class), eq("pc1"), anyString(), anyString(), anyString())).thenReturn(true);
		
		HttpServletRequest request = mock(HttpServletRequest.class);		
		when(request.getParameter(REQ_PAR_HASHCODE)).thenReturn("plugin3");
		when(request.getParameter(REQ_PAR_ACTION)).thenReturn(ACT_VAL_CON_PROP);
		when(request.getParameter(REQ_PAR_PROP_NAME)).thenReturn("pc1");
		when(request.getParameter(REQ_PAR_PROP_TYPE)).thenReturn("pct1");
		
		String output = pluginsView.render(request);
		
		verify(pluginAdmin).pluginUpdated(plugin3);
		assertThat(output, containsString("PLUGIN#4"));
	}
	
	public class TestablePluginsView extends PluginsView {
		public TestablePluginsView(BundleContext bundlecontext,
				VelocityContext vc) {
			super(bundlecontext, vc);
		}

		@Override
		protected MetricActivator getMetricActivator() {
			return metricActivator;
		}

		@Override
		protected Plugin getPluginByHashcode(String hashcode) {
			if ("plugin1".equals(hashcode)) {
				return dbPlugin1;
			} else if ("plugin2".equals(hashcode)) {
				return dbPlugin2;
			} else {
				return dbRandomPlugin;
			}
		}

		@Override
		protected PluginAdmin getPluginAdmin() {
			return pluginAdmin;
		}
	}	
	
	public class TestActivator2 extends DAObject {
		@Override
		public long getId() {
			return 0;
		}
		@Override
		public void setId(long id) {
		}
	}

	public class TestActivator1 extends DAObject {
		@Override
		public long getId() {
			return 0;
		}
		@Override
		public void setId(long id) {
		}
	}
}
