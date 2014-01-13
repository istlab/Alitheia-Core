package eu.sqooss.impl.service.webadmin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.junit.Test;
import org.xml.sax.SAXException;

import eu.sqooss.service.pa.PluginInfo;

public class ProjectsViewTest {
	@Test
	public void shouldShowNoLastAppliedVersionForEmptyList() {
		StringBuilder html = new StringBuilder();
		
		ProjectsView.showLastAppliedVersion(null, new ArrayList<PluginInfo>(), html);
		
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
	public void shouldShowInstalledPlugins() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		StringBuilder html = new StringBuilder();
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
		
		ProjectsView.showLastAppliedVersion(null, metrics, html);
		
		String submit = Pattern.quote(ProjectsView.SUBMIT);
		
		String sethash1 = "javascript:document.getElementById\\('" + ProjectsView.REQ_PAR_SYNC_PLUGIN + "'\\).value='" + hash1 + "';";
		String onclick1 = "onclick\\s*=\\s*\"" + sethash1 + submit + "\"";
		String input1 = "<input\\s+(.*\\s+)" + onclick1 + "(.*)>\\s*";
		String column1 = "<td(.*)>\\s*" + input1 + "&nbsp;" + name1 + "</td>";
		String expected1 = "<tr>\\s*" + column1 + "\\s*</tr>\\s*";

		String sethash2 = "javascript:document.getElementById\\('" + ProjectsView.REQ_PAR_SYNC_PLUGIN + "'\\).value='" + hash2 + "';";
		String onclick2 = "onclick\\s*=\\s*\"" + sethash2 + submit + "\"";
		String input2 = "<input\\s+(.*\\s+)" + onclick2 + "(.*)>\\s*";
		String column2 = "<td(.*)>\\s*" + input2 + "&nbsp;" + name2 + "</td>";
		String expected2 = "<tr>\\s*" + column2 + "\\s*</tr>\\s*";
		
		assertTrue(html.toString().matches(expected1 + expected2));
	}
	
	@Test
	public void shouldCreateTableHeaderRow() {
		StringBuilder html = new StringBuilder();
		
		ProjectsView.addHeaderRow(html, 0);
		
		String expected = "\\s*<table>\\s*" +
				"<thead>\\s*" +
				"<tr\\s+class\\s*=\\s*['\"]head['\"]>\\s*" +
				"(<td (.*)>\\w+</td>\\s*){7}\\s*" +
				"</tr>\\s*" +
				"</thead>\\s*";

		assertTrue(html.toString().matches(expected));
	}
}
