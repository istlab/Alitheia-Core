package eu.sqooss.impl.service.webadmin;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.xmlmatchers.transform.XmlConverters.the;
import static org.xmlmatchers.xpath.HasXPath.hasXPath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.junit.Test;
import org.xml.sax.SAXException;

import eu.sqooss.service.pa.PluginInfo;

public class ProjectsViewTest {
	@Test
	public void shouldShowNoLastAppliedVersionForEmptyList() {
		StringBuilder html = new StringBuilder();

		ProjectsView.showLastAppliedVersion(null, new ArrayList<PluginInfo>(),
				html);

		assertEquals("", html.toString());
	}

	@Test
	public void shouldNotShowNotInstalledPlugins() {
		StringBuilder html = new StringBuilder();
		PluginInfo pi = new PluginInfo();
		pi.installed = false;
		Collection<PluginInfo> metrics = Arrays.asList(pi);

		ProjectsView.showLastAppliedVersion(null, metrics, html);

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
		ProjectsView.showLastAppliedVersion(null, metrics, builder);

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
		StringBuilder html = new StringBuilder();

		ProjectsView.addHeaderRow(html, 0);

		String expected = "\\s*<table>\\s*" + "<thead>\\s*"
				+ "<tr\\s+class\\s*=\\s*['\"]head['\"]>\\s*"
				+ "(<td (.*)>\\w+</td>\\s*){7}\\s*" + "</tr>\\s*"
				+ "</thead>\\s*";

		assertTrue(html.toString().matches(expected));
	}
}
