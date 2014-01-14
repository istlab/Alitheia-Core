package eu.sqooss.impl.service.webadmin;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.xmlmatchers.transform.XmlConverters.the;
import static org.xmlmatchers.xpath.HasXPath.hasXPath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.velocity.VelocityContext;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.xml.sax.SAXException;

import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.service.updater.Updater;
import eu.sqooss.service.updater.UpdaterService.UpdaterStage;

public class ProjectsViewTest {
	private static final String INPUT_REGEX = "<input([\\(\\) a-zA-Z=\\-_\\\"\\\':;0-9\\.\\?\\/\\\\]*[\\(\\) a-zA-Z=\\-_\\\"\\\':;0-9\\.\\?\\\\]+)>";
	private static final String CLUSTER_NODE_NAME = "CLUSTER_NODE1";
	private ProjectsView projectsView;
	private Map<UpdaterStage, Set<Updater>> updaters;

	@Before
	public void setUp() {
		projectsView = new TestableProjectsView(null, null);
		updaters = new HashMap<UpdaterStage, Set<Updater>>();
	}
	
	@Test
	public void shouldShowBasicToolbarIfNoProjectSelected() {
		StringBuilder builder = new StringBuilder();
		
		projectsView.addToolBar(null, builder, 0);
		
		// sanitize html input
		String html = "<root>" + builder.toString() + "</root>";		
		html = html.replaceAll(INPUT_REGEX, "<input$1/>");
		html = html.replaceAll("disabled(\\s*[^=])", "disabled='true'$1");
		
		// the toolbar should have three rows.
		assertThat(the(html), hasXPath("count(/root/tr)", equalTo("3")));
		// the first row should have a button that refreshes the projects page
		String onclick1 = "javascript:window.location='/projects';";
		assertThat(the(html), hasXPath("/root/tr[1]/td[2]/input/@onclick", equalTo(onclick1)));
		// the second row has a button that does the add project action and a button that does a remove project action
		String onclick2 = "javascript:document.getElementById('" + ProjectsView.REQ_PAR_ACTION +"').value='" + ProjectsView.ACT_REQ_ADD_PROJECT + "';" + ProjectsView.SUBMIT;
		assertThat(the(html), hasXPath("/root/tr[2]/td[2]/input[1]/@onclick", equalTo(onclick2)));
		// this action must be disabled
		String onclick3 = "javascript:document.getElementById('" + ProjectsView.REQ_PAR_ACTION +"').value='" + ProjectsView.ACT_REQ_REM_PROJECT + "';" + ProjectsView.SUBMIT;
		assertThat(the(html), hasXPath("/root/tr[2]/td[2]/input[2]/@onclick", equalTo(onclick3)));
		assertThat(the(html), hasXPath("/root/tr[2]/td[2]/input[2]/@disabled"));
		// the third row has a button that runs the updater and one that runs all updaters,
		// both must be disabled
		String onclick4 = "javascript:document.getElementById('" + ProjectsView.REQ_PAR_ACTION +"').value='" + ProjectsView.ACT_CON_UPD + "';" + ProjectsView.SUBMIT;
		assertThat(the(html), hasXPath("/root/tr[3]/td[2]/input[1]/@onclick", equalTo(onclick4)));
		assertThat(the(html), hasXPath("/root/tr[3]/td[2]/input[1]/@disabled"));		
		String onclick5 = "javascript:document.getElementById('" + ProjectsView.REQ_PAR_ACTION +"').value='" + ProjectsView.ACT_CON_UPD_ALL + "';" + ProjectsView.SUBMIT;
		assertThat(the(html), hasXPath("/root/tr[3]/td[2]/input[2]/@onclick", equalTo(onclick5)));
		assertThat(the(html), hasXPath("/root/tr[3]/td[2]/input[2]/@disabled"));
		// the next column has a button to update all on a node
		String onclick6 = "javascript:document.getElementById('" + ProjectsView.REQ_PAR_ACTION +"').value='" + ProjectsView.ACT_CON_UPD_ALL_NODE + "';" + ProjectsView.SUBMIT;
		assertThat(the(html), hasXPath("/root/tr[3]/td[3]/input[1]/@onclick", equalTo(onclick6)));
	}
	
	@Test
	public void shouldShowAdvancedToolbarIfProjectSelected() {
		StoredProject project = new StoredProject();
		StringBuilder builder = new StringBuilder();
		
		projectsView.addToolBar(project, builder, 0);

		// sanitize html input
		String html = "<root>" + builder.toString() + "</root>";		
		html = html.replaceAll(INPUT_REGEX, "<input$1/>");
		html = html.replaceAll("disabled(\\s*[^=])", "disabled='true'$1");

		System.out.println(html);
		
		// the first row should have a button that goes to the project page
		String onclick1 = "javascript:window.location='/projects?" + ProjectsView.REQ_PAR_PROJECT_ID + "=" + project.getId() + "';";
		assertThat(the(html), hasXPath("/root/tr[1]/td[2]/input/@onclick", equalTo(onclick1)));

		// the remove action is not disabled this time
		assertThat(the(html), hasXPath("//input[contains(@onclick, '" + ProjectsView.ACT_REQ_REM_PROJECT + "') and not(@disabled)]"));
		// test the select:
		// lol
		// the update buttons are not disabled this time
		assertThat(the(html), hasXPath("//input[contains(@onclick, '" + ProjectsView.ACT_CON_UPD + "') and not(@disabled)]"));
		assertThat(the(html), hasXPath("//input[contains(@onclick, '" + ProjectsView.ACT_CON_UPD_ALL + "') and not(@disabled)]"));
	}
	
	@Test
	public void shouldShowNoLastAppliedVersionForEmptyList() {
		StringBuilder html = new StringBuilder();

		projectsView.showLastAppliedVersion(null, new ArrayList<PluginInfo>(),
				html);

		assertEquals("", html.toString());
	}

	@Test
	public void shouldNotShowNotInstalledPlugins() {
		StringBuilder html = new StringBuilder();
		PluginInfo pi = new PluginInfo();
		pi.installed = false;
		Collection<PluginInfo> metrics = Arrays.asList(pi);

		projectsView.showLastAppliedVersion(null, metrics, html);

		assertEquals("", html.toString());
	}
	
	@Test
	public void shouldShowInstalledPlugins()
			throws ParserConfigurationException, SAXException, IOException,
			XPathExpressionException {
		// Arrange
		StringBuilder builder = new StringBuilder();
		String hash1 = "p1hash";
		String name1 = "p1 plugin";
		String hash2 = "p2hash";
		String name2 = "p2 plugin";

		PluginInfo p1 = new PluginInfo();
		p1.installed = true;
		p1.setHashcode(hash1);
		p1.setPluginName(name1);
		PluginInfo p2 = new PluginInfo();
		p2.installed = true;
		p2.setHashcode(hash2);
		p2.setPluginName(name2);
		Collection<PluginInfo> metrics = Arrays.asList(p1, p2);

		// Act
		projectsView.showLastAppliedVersion(null, metrics, builder);

		// Assert
		String html = "<root>" + builder.toString().replace("&nbsp;", " ") + "</root>";
		
		String onclick1 = "javascript:document.getElementById('" + ProjectsView.REQ_PAR_SYNC_PLUGIN + "').value='" + hash1 +"';" + ProjectsView.SUBMIT;
		String onclick2 = "javascript:document.getElementById('" + ProjectsView.REQ_PAR_SYNC_PLUGIN + "').value='" + hash2 +"';" + ProjectsView.SUBMIT;

		// there must be two rows.
		assertThat(the(html), hasXPath("count(/root/tr)", equalTo("2")));
		// the first row has one column.
		
		assertThat(the(html), hasXPath("count(/root/tr[1]/td)", equalTo("1")));
		// that column has an input of type button that does 'onclick1' on click.
		assertThat(the(html), hasXPath("/root/tr[1]/td/input[@type='button']/@onclick", equalTo(onclick1)));
		// that column's text should contain the plugin name
		assertThat(the(html), hasXPath("string-join(/root/tr[1]/td/text(), '')", containsString(name1)));
		
		// the second row has one column.
		assertThat(the(html), hasXPath("count(/root/tr[2]/td)", equalTo("1")));
		// that column has an input of type button that does 'onclick2' on click.
		assertThat(the(html), hasXPath("/root/tr[2]/td/input[@type='button']/@onclick", equalTo(onclick2)));
		// that column's text should contain the plugin name
		assertThat(the(html), hasXPath("string-join(/root/tr[2]/td/text(), '')", containsString(name2)));
	}

	@Test
	public void shouldCreateTableHeaderRow() {
		StringBuilder builder = new StringBuilder();

		projectsView.addHeaderRow(builder, 0);

		String html = builder.toString() + "</table>";
		
		// the table should contain a thead with a tr with 7 td's
		assertThat(the(html), hasXPath("count(/table/thead/tr/td)", equalTo("7")));
		// all td's have class='head'
		assertThat(the(html), hasXPath("count(//td[not(@class='head')])", equalTo("0")));
	}
	
	public class TestableProjectsView extends ProjectsView {
		public TestableProjectsView(BundleContext bundlecontext,
				VelocityContext vc) {
			super(bundlecontext, vc);
		}

		@Override
		protected String getClusterNodeName() {
			return CLUSTER_NODE_NAME;
		}

		@Override
		protected Set<Updater> getUpdaters(StoredProject selProject,
				UpdaterStage importStage) {
			Set<Updater> s = updaters.get(importStage);
			if (s == null) {
				return new HashSet<Updater>();
			} else {
				return s;
			}
		}
	}	
}
