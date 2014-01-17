package eu.sqooss.service.pa;

import static org.powermock.api.mockito.PowerMockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.util.HashSet;
import java.util.Set;

import org.osgi.framework.ServiceReference;

import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.PluginConfiguration;
import junit.framework.TestCase;

/**
 * The class <code>PluginInfoTest</code> contains tests for the class {@link <code>PluginInfo</code>}
 */
public class PluginInfoTest extends TestCase {	
	/**
	 * The object that is being tested.
	 *
	 * @see eu.sqooss.service.pa.PluginInfo
	 */
	private PluginInfo instance;
	/**
	 * An (by default empty) set for (mocked) PluginConfiguration objects,
	 * passed in the default PluginInfo constructor
	 */
	private Set<PluginConfiguration> config;

	/**
	 * Perform pre-test initialization
	 *
	 * @throws Exception
	 *
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		config = new HashSet<PluginConfiguration>();
		instance = new PluginInfo(config);
	}

	/**
	 * Run the Set<PluginConfiguration> getConfiguration() method test
	 * 
	 * This should just return the set configuration
	 */
	public void testGetConfiguration()
	{
		assertEquals(config,instance.getConfiguration());
	}
	/**
	 * Run the Long getConfPropId(String, String) method test
	 * 
	 * This should return null for all possible parameters, except for when
	 * a given name/type combination matches with that of an existing PluginConfiguration 
	 */
	public void testGetConfPropId() {
		assertNull(instance.getConfPropId(null, null));
		assertNull(instance.getConfPropId("", null));
		final String name = "testName";
		assertNull(instance.getConfPropId(name, null));
		final ConfigurationType type = ConfigurationType.STRING;
		assertNull(instance.getConfPropId(null, type));
		assertNull(instance.getConfPropId(name, type));
		
		final PluginConfiguration property = mock(PluginConfiguration.class);
		final Long expected = Long.MAX_VALUE; 
		when(property.getId()).thenReturn(expected);
		when(property.getName()).thenReturn(name);
		when(property.getType()).thenReturn(type);
		assertTrue(config.add(property));
		
		final String otherName = "wrongName";
		final ConfigurationType otherType = ConfigurationType.BOOLEAN; 
		assertNull(instance.getConfPropId(otherName,otherType));
		assertNull(instance.getConfPropId(name, otherType));
		assertNull(instance.getConfPropId(otherName, type));
		assertEquals(expected,instance.getConfPropId(name, type));
	}

	/**
	 * Run the boolean hasConfProp(String, String) method test
	 * 
	 * @see testGetConfPropId() This is nearly identical (boolean instead of object)
	 */
	public void testHasConfProp() {
		assertFalse(instance.hasConfProp(null, null));
		assertFalse(instance.hasConfProp("", null));
		final String name = "testName";
		assertFalse(instance.hasConfProp(name, null));
		final ConfigurationType type = ConfigurationType.STRING;
		assertFalse(instance.hasConfProp(null, type));
		assertFalse(instance.hasConfProp(name, type));
		
		final PluginConfiguration property = mock(PluginConfiguration.class);
		final Long expected = Long.MAX_VALUE; 
		when(property.getId()).thenReturn(expected);
		when(property.getName()).thenReturn(name);
		when(property.getType()).thenReturn(type);
		assertTrue(config.add(property));
		
		final String otherName = "wrongName";
		final ConfigurationType otherType = ConfigurationType.BOOLEAN; 
		assertFalse(instance.hasConfProp(otherName,otherType));
		assertFalse(instance.hasConfProp(name, otherType));
		assertFalse(instance.hasConfProp(otherName, type));
		assertTrue(instance.hasConfProp(name, type));
	}

	/**
	 * Run the boolean updateConfigEntry(DBService, String, String) method test
	 * 
	 * If a PluginConfiguration with the given name exists in the current instance,
	 * the DBService will be used to set the newVal for that name, after a type-check.
	 */
	public void testUpdateConfigEntry() {
		try{
			instance.updateConfigEntry(null,null);
			assertTrue(false);
		} catch( IllegalArgumentException e ){
			assertNotNull(e);
		}
		final DBService db = mock(DBService.class);
		try{
			instance.updateConfigEntry(db,null);
			assertTrue(false);
		} catch( IllegalArgumentException e ){
			assertNotNull(e);
		}
		
		final PluginConfiguration property = mock(PluginConfiguration.class);
		when(db.attachObjectToDBSession(property)).thenReturn(property);		
		final String name = "true";
		when(property.getName()).thenReturn(name);
		ConfigurationType type = ConfigurationType.BOOLEAN;
		when(property.getType()).thenReturn(type);		
		
		assertTrue(config.add(property));
		assertTrue(instance.updateConfigEntry(db,property));
		verify(db,times(1)).attachObjectToDBSession(property);
		
		final String otherName = "1.0";
		final ConfigurationType otherType = ConfigurationType.DOUBLE;		
		final PluginConfiguration second = mock(PluginConfiguration.class);
		when(db.attachObjectToDBSession(second)).thenReturn(second);
		when(second.getName()).thenReturn(otherName);
		when(second.getType()).thenReturn(type);
		assertTrue(config.add(second));
		final PluginConfiguration third = mock(PluginConfiguration.class);
		when(db.attachObjectToDBSession(third)).thenReturn(third);
		when(third.getName()).thenReturn(name);
		when(third.getType()).thenReturn(otherType);
		assertTrue(config.add(third));
		final PluginConfiguration fourth = mock(PluginConfiguration.class);
		when(db.attachObjectToDBSession(fourth)).thenReturn(fourth);
		when(fourth.getName()).thenReturn(otherName);
		when(fourth.getType()).thenReturn(otherType);
		assertTrue(config.add(fourth));
		
		assertTrue(instance.updateConfigEntry(db,second));
		verify(db,times(1)).attachObjectToDBSession(second);
		assertTrue(instance.updateConfigEntry(db,third));
		verify(db,times(1)).attachObjectToDBSession(third);
		assertTrue(instance.updateConfigEntry(db,fourth));
		verify(db,times(1)).attachObjectToDBSession(fourth);
		
		assertTrue(config.remove(property));
		assertFalse(instance.updateConfigEntry(db,property));
	}

	/**
	 * Run the boolean addConfigEntry(Plugin, String, String, String, String) method test
	 * 
	 * When the given parameters are correct (type-checked and all), a new
	 * PluginConfiguration using those parameters is created and passed to the given Plugin.
	 * 
	 * @todo No DBService is actually used, only through some given Plugin or something? 
	 * This is also inconsistent with the add/delete methods, and gives trouble when calling this...
	 * @todo Way too many parameters, and no local field is used
	 */
	@SuppressWarnings("unchecked")
	public void testAddConfigEntry() {
		try{
			instance.addConfigEntry(null,null);
			assertTrue(false);
		} catch( IllegalArgumentException e ){
			assertNotNull(e);
		}
		final DBService db = mock(DBService.class);
		try{
			instance.addConfigEntry(db,null);
			assertTrue(false);
		} catch( IllegalArgumentException e ){
			assertNotNull(e);
		}
		
		final PluginConfiguration property = mock(PluginConfiguration.class);
		when(db.addRecord(property)).thenReturn(false);		
		final String name = "1";
		when(property.getName()).thenReturn(name);
		final ConfigurationType type = ConfigurationType.INTEGER;
		when(property.getType()).thenReturn(type);
		
		assertFalse(instance.addConfigEntry(db,property));
		verify(db,times(1)).addRecord(property);

		when(db.addRecord(property)).thenReturn(true);		
		assertTrue(instance.addConfigEntry(db,property));
		verify(db,times(2)).addRecord(property);
	}

	/**
	 * Run the boolean removeConfigEntry(DBService, String, String) method test
	 * 
	 * The given name and type are used to look-up an existing PluginConfiguration entry,
	 * and when found, to delete it using the given DBService.
	 */
	public void testRemoveConfigEntry() {
		try{
			instance.removeConfigEntry(null,null);
			assertTrue(false);
		} catch( IllegalArgumentException e ){
			assertNotNull(e);
		}
		final DBService db = mock(DBService.class);
		try{
			instance.removeConfigEntry(db,null);
			assertTrue(false);
		} catch( IllegalArgumentException e ){
			assertNotNull(e);
		}
		
		final PluginConfiguration property = mock(PluginConfiguration.class);
		when(db.deleteRecord(property)).thenReturn(true);
		final Long id = Long.MAX_VALUE; 
		when(property.getId()).thenReturn(id);
		final String name = "testName";
		when(property.getName()).thenReturn(name);
		final ConfigurationType type = ConfigurationType.STRING;
		when(property.getType()).thenReturn(type);
		assertFalse(instance.removeConfigEntry(db,property));
		
		assertTrue(config.add(property));
		assertTrue(instance.removeConfigEntry(db,property));
		verify(db,times(1)).deleteRecord(property);
		assertTrue( config.add(property) ); // re-add
		
		final String otherName = "1.0";
		final ConfigurationType otherType = ConfigurationType.DOUBLE;		
		final PluginConfiguration second = mock(PluginConfiguration.class);
		when(db.deleteRecord(second)).thenReturn(true);
		when(second.getName()).thenReturn(otherName);
		when(second.getType()).thenReturn(type);
		assertTrue(config.add(second));
		final PluginConfiguration third = mock(PluginConfiguration.class);
		when(db.deleteRecord(third)).thenReturn(true);
		when(third.getName()).thenReturn(name);
		when(third.getType()).thenReturn(otherType);
		assertTrue(config.add(third));
		final PluginConfiguration fourth = mock(PluginConfiguration.class);
		when(db.deleteRecord(fourth)).thenReturn(true);
		when(fourth.getName()).thenReturn(otherName);
		when(fourth.getType()).thenReturn(otherType);
		assertTrue(config.add(fourth));
		
		assertTrue(instance.removeConfigEntry(db,second));
		verify(db,times(1)).deleteRecord(second);
		assertTrue(instance.removeConfigEntry(db,third));
		verify(db,times(1)).deleteRecord(third);
		assertTrue(instance.removeConfigEntry(db,fourth));
		verify(db,times(1)).deleteRecord(fourth);
	}

	/**
	 * Run the String getPluginName() method test
	 * Automatically tests setPluginName as well
	 * 
	 * Simple holder (set through setAlitheiaPlugin)
	 */
	public void testGetPluginName() {
		AlitheiaPlugin p = mock(AlitheiaPlugin.class);
		final String name = "testName";
		when(p.getName()).thenReturn(name);
		instance.setAlitheiaPlugin(p);
		assertEquals(name,instance.getPluginName());
	}

	/**
	 * Run the String getPluginVersion() method test
	 * Automatically tests setPluginVersion as well
	 * 
	 * Simple holder (set through setAlitheiaPlugin)
	 */
	public void testGetPluginVersion() {
		AlitheiaPlugin p = mock(AlitheiaPlugin.class);
		final String version = "testVersion";
		when(p.getVersion()).thenReturn(version);
		instance.setAlitheiaPlugin(p);
		assertEquals(version,instance.getPluginVersion());
	}

	/**
	 * Run the Set<Class<? extends DAObject>> getActivationTypes() method test
	 * Automatically tests setActivationTypes as well
	 * 
	 * Simple holder (set through setAlitheiaPlugin)
	 */
	public void testGetActivationTypes()
	{
		AlitheiaPlugin p = mock(AlitheiaPlugin.class);
		final Set<Class<? extends DAObject>> types = new HashSet<Class<? extends DAObject>>();
		when(p.getActivationTypes()).thenReturn(types);
		instance.setAlitheiaPlugin(p);
		assertEquals(types,instance.getActivationTypes());
	}
	
	/**
	 * Run the boolean isActivationType(Class<? extends DAObject>) method test
	 * Automatically tests addActivationType as well
	 * 
	 * Just passes through all activation types in the set and checks if they're equal...
	 */
	public void testIsActivationType()
	{
		assertFalse(instance.isActivationType(null));
		assertFalse(instance.isActivationType(PluginConfiguration.class));
		instance.addActivationType(Plugin.class);
		assertFalse(instance.isActivationType(PluginConfiguration.class));
		instance.addActivationType(PluginConfiguration.class);
		assertTrue(instance.isActivationType(PluginConfiguration.class));
	}

	/**
	 * Run the ServiceReference getServiceRef() method test
	 * Automatically tests setServiceRef as well
	 * 
	 * serviceRef is a very simple holder; there's just a get and a set (2 lines of code)
	 */
	public void testGetServiceRef() {
		final ServiceReference refMock = mock(ServiceReference.class);
		assertNull(instance.getServiceRef());
		instance.setServiceRef(refMock);
		assertEquals(refMock,instance.getServiceRef());
	}

	/**
	 * Run the String getHashcode() method test
	 * 
	 * Automatically tests setHashcode as well
	 * 
	 * hashCode is also a very simple holder; there's just a get and a set (2 lines of code)
	 */
	public void testGetHashcode() {
		final String hash = "testHash";
		assertNull(instance.getHashcode());
		instance.setHashcode(hash);
		assertEquals(hash,instance.getHashcode());
	}

	/**
	 * Run the int compareTo(PluginInfo) method test
	 * 
	 * Tests equals/hashcode as well
	 */
	public void testCompareTo() {
		assertFalse(instance.equals(null));
		assertFalse(instance.equals(""));
		assertTrue(instance.equals(instance));
		assertEquals(0,instance.hashCode());
		
		PluginInfo second = new PluginInfo();		
		final String hash = "1";
		second.setHashcode(hash);
		
		instance.setHashcode("");
		assertFalse(instance.equals(second));
		assertEquals(-1,instance.compareTo(second));
		assertEquals(1,second.compareTo(instance));
		
		instance.setHashcode(hash);
		assertTrue(instance.equals(second));
		assertEquals(instance.hashCode(),second.hashCode());
		assertEquals(0,instance.compareTo(second));
		
		instance.setHashcode(null);
		assertFalse(instance.equals(second));
		assertEquals(-1,instance.compareTo(second));
		assertEquals(1,second.compareTo(instance));
		second.setHashcode(null);
		assertTrue(instance.equals(second));
		assertEquals(instance.hashCode(),second.hashCode());
		assertEquals(0,instance.compareTo(second));
	}
	
	/**
	 * Run the boolean isInstalled() method test
	 * 
	 * Automatically tests install/uninstall as well
	 * 
	 * installed is also a very simple flag; there's just a get and two set (3 lines of code)
	 */
	public void testIsInstalled() {
		assertFalse(instance.isInstalled());
		instance.install();
		assertTrue(instance.isInstalled());
		instance.uninstall();
		assertFalse(instance.isInstalled());
	}
}