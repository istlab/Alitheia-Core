package eu.sqooss.test.service.db.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.db.util.PluginUtils;

@RunWith(MockitoJUnitRunner.class)
public class PluginUtilsTest {

	private PluginUtils utils;
	@Mock private DBService dbs;
	
	@Before
	public void setUp() {
		this.utils = new PluginUtils(this.dbs);
	}
	
	@Test
	public void testGetPluginByName() {
		Plugin p = new Plugin();
		when(this.dbs.findObjectsByProperties(eq(Plugin.class), anyMapOf(String.class, Object.class))).thenReturn(Arrays.asList(p));
		assertEquals(Arrays.asList(p), this.utils.getPluginByName("foo"));
	}
	
	@Test
	public void testGetPluginByHashCodeListEmpty() {
		when(this.dbs.findObjectsByProperties(eq(Plugin.class), anyMapOf(String.class, Object.class))).thenReturn(new ArrayList<Plugin>());
		assertNull(this.utils.getPluginByHashcode("foo"));
	}
	
	@Test
	public void testGetPluginByHashCodeListNonEmpty() {
		Plugin p = new Plugin();
		when(this.dbs.findObjectsByProperties(eq(Plugin.class), anyMapOf(String.class, Object.class))).thenReturn(Arrays.asList(p));
		assertEquals(p, this.utils.getPluginByHashcode("foo"));
	}
	
	@Test
	public void testGetConfigurationEntryListEmpty() {
		when(this.dbs.findObjectsByProperties(eq(PluginConfiguration.class), anyMapOf(String.class, Object.class))).thenReturn(new ArrayList<PluginConfiguration>());
		assertNull(this.utils.getConfigurationEntry(new Plugin(), new HashMap<String, Object>()));
	}
	
	@Test
	public void testGetConfigurationEntryListNonEmpty() {
		PluginConfiguration pc = new PluginConfiguration();
		when(this.dbs.findObjectsByProperties(eq(PluginConfiguration.class), anyMapOf(String.class, Object.class))).thenReturn(Arrays.asList(pc));
		assertEquals(pc, this.utils.getConfigurationEntry(new Plugin(), new HashMap<String, Object>()));
	}
	
	@Test
	public void testUpdConfigurationEntryPCNull() {
		when(this.dbs.findObjectsByProperties(eq(PluginConfiguration.class), anyMapOf(String.class, Object.class))).thenReturn(new ArrayList<PluginConfiguration>());
		assertFalse(this.utils.updConfigurationEntry(new Plugin(), new HashMap<String, Object>()));
	}
	
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testUpdConfigurationEntryPCNotNullListEmpty() {
		PluginConfiguration pc = new PluginConfiguration();
		when(this.dbs.findObjectsByProperties(eq(PluginConfiguration.class), anyMapOf(String.class, Object.class))).thenReturn(Arrays.asList(pc), new ArrayList());
		assertFalse(this.utils.updConfigurationEntry(new Plugin(), new HashMap<String, Object>()));
	}
	
	@Test
	public void testUpdConfigurationEntryPCNotNullListNonEmpty() {
		PluginConfiguration pc = new PluginConfiguration();
		when(this.dbs.findObjectsByProperties(eq(PluginConfiguration.class), anyMapOf(String.class, Object.class))).thenReturn(Arrays.asList(pc));
		assertTrue(this.utils.updConfigurationEntry(new Plugin(), new HashMap<String, Object>()));
	}
}
