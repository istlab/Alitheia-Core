package eu.sqooss.impl.service.webadmin;

import static eu.sqooss.impl.service.webadmin.HTMLTestUtils.sanitizeHTML;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.text.IsEmptyString.isEmptyString;
import static org.hamcrest.text.IsEqualIgnoringWhiteSpace.equalToIgnoringWhiteSpace;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.xmlmatchers.transform.XmlConverters.the;
import static org.xmlmatchers.xpath.HasXPath.hasXPath;
import static org.xmlmatchers.xpath.XpathReturnType.returningANumber;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.velocity.VelocityContext;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;

@RunWith(MockitoJUnitRunner.class)
public class PluginsViewTest { 
	@Mock private PluginAdmin pluginAdmin;
	@Mock private ServiceReference serviceReference1;
	@Mock private ServiceReference serviceReference2;
	private PluginsView pluginsView;
	private PluginInfo pluginInfo1;
	private PluginInfo pluginInfo2;

	@Before
	public void setUp() {
		pluginsView = new TestablePluginsView(null, null);
		
		pluginInfo1 = new PluginInfo();
		pluginInfo1.setHashcode("plugin1");
		pluginInfo1.setPluginName("plugin1");
		pluginInfo1.setServiceRef(serviceReference1);
		pluginInfo1.setPluginVersion("v1");
		when(serviceReference1.getProperty(Constants.OBJECTCLASS)).thenReturn(new String[] { "a", "b" });
		pluginInfo2 = new PluginInfo();
		pluginInfo2.installed = true;
		pluginInfo2.setHashcode("plugin2");
		pluginInfo2.setPluginName("plugin2");
		pluginInfo2.setServiceRef(serviceReference2);
		when(serviceReference2.getProperty(Constants.OBJECTCLASS)).thenReturn(new String[] { "c", "d" });
		pluginInfo2.setPluginVersion("v2");
		when(pluginAdmin.listPlugins()).thenReturn(Arrays.asList(pluginInfo1, pluginInfo2));
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
	public void shouldRenderPluginsListWhenPluginsFoundButNoRequest() throws Exception {
		String output = pluginsView.render(null);
		
		String html = sanitizeHTML(output);
		
		// there is a fieldset listing all plugins
		assertThat(the(html), hasXPath("//form/fieldset[legend/text()='All plug-ins']"));
		// the table has a header row
		assertThat(the(html), hasXPath("//form/fieldset[legend/text()='All plug-ins']/table/thead"));
		// the table body
		assertThat(the(html), hasXPath("//form/fieldset[legend/text()='All plug-ins']/table/tbody"));

		assertThat(the(html), hasXPath("//form/fieldset[legend/text()='All plug-ins']/table/tbody/tr[1]/@onclick", equalTo("javascript:document.getElementById('" + PluginsView.REQ_PAR_HASHCODE + "').value='plugin1';document.metrics.submit();")));
		assertThat(the(html), hasXPath("string-join(//form/fieldset[legend/text()='All plug-ins']/table/tbody/tr[1]/td[1]/text(), '')", containsString("Registered")));
		assertThat(the(html), hasXPath("//form/fieldset[legend/text()='All plug-ins']/table/tbody/tr[1]/td[2]/text()", equalTo("plugin1")));
		assertThat(the(html), hasXPath("//form/fieldset[legend/text()='All plug-ins']/table/tbody/tr[1]/td[3]/text()", equalTo("a,b")));
		assertThat(the(html), hasXPath("//form/fieldset[legend/text()='All plug-ins']/table/tbody/tr[1]/td[4]/text()", equalTo("v1")));

		assertThat(the(html), hasXPath("//form/fieldset[legend/text()='All plug-ins']/table/tbody/tr[2]/@onclick", equalTo("javascript:document.getElementById('" + PluginsView.REQ_PAR_HASHCODE + "').value='plugin2';document.metrics.submit();")));
		assertThat(the(html), hasXPath("string-join(//form/fieldset[legend/text()='All plug-ins']/table/tbody/tr[2]/td[1]/text(), '')", containsString("Installed")));
		assertThat(the(html), hasXPath("//form/fieldset[legend/text()='All plug-ins']/table/tbody/tr[2]/td[2]/text()", equalTo("plugin2")));
		assertThat(the(html), hasXPath("//form/fieldset[legend/text()='All plug-ins']/table/tbody/tr[2]/td[3]/text()", equalTo("c,d")));
		assertThat(the(html), hasXPath("//form/fieldset[legend/text()='All plug-ins']/table/tbody/tr[2]/td[4]/text()", equalTo("v2")));
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
	
	public class TestablePluginsView extends PluginsView {
		public TestablePluginsView(BundleContext bundlecontext,
				VelocityContext vc) {
			super(bundlecontext, vc);
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
