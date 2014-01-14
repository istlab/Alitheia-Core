package eu.sqooss.service.pa;

import static org.powermock.api.mockito.PowerMockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.mockito.Mockito;
import org.osgi.framework.ServiceReference;

import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.pa.PluginInfo.ConfigurationType;
import junit.framework.TestCase;

/**
 * The class <code>PluginInfoTest</code> contains tests for the class {@link <code>PluginInfo</code>}
 * 
 * @todo The PluginInfo class has a public 'installed' attribute, which is unused in the class itself...
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
		final String name = "testName";
		assertNull(instance.getConfPropId(name, null));
		final String type = "testType";
		assertNull(instance.getConfPropId(type, null));
		assertNull(instance.getConfPropId(name, type));
		
		final PluginConfiguration property = mock(PluginConfiguration.class);
		final Long expected = Long.MAX_VALUE; 
		when(property.getId()).thenReturn(expected);
		when(property.getName()).thenReturn(name);
		when(property.getType()).thenReturn(type);
		config.add(property);
		
		assertNull(instance.getConfPropId("wrongName", "wrongType"));
		assertNull(instance.getConfPropId(name, "wrongType"));
		assertNull(instance.getConfPropId("wrongName", type));
		assertEquals(expected,instance.getConfPropId(name, type));
	}

	/**
	 * Run the boolean hasConfProp(String, String) method test
	 * 
	 * @see testGetConfPropId() This is nearly identical (boolean instead of object)
	 */
	public void testHasConfProp() {
		assertFalse(instance.hasConfProp(null, null));
		final String name = "testName";
		assertFalse(instance.hasConfProp(name, null));
		final String type = "testType";
		assertFalse(instance.hasConfProp(type, null));
		assertFalse(instance.hasConfProp(name, type));
		
		final PluginConfiguration property = mock(PluginConfiguration.class);
		final Long expected = Long.MAX_VALUE; 
		when(property.getId()).thenReturn(expected);
		when(property.getName()).thenReturn(name);
		when(property.getType()).thenReturn(type);
		config.add(property);
		
		assertFalse(instance.hasConfProp("wrongName", "wrongType"));
		assertFalse(instance.hasConfProp(name, "wrongType"));
		assertFalse(instance.hasConfProp("wrongName", type));
		assertTrue(instance.hasConfProp(name, type));
	}

	/**
	 * Run the boolean updateConfigEntry(DBService, String, String) method test
	 * 
	 * If a PluginConfiguration with the given name exists in the current instance,
	 * the DBService will be used to set the newVal for that name, after a type-check.
	 * 
	 * @todo Why general Exceptions (bad on themselves) AND a boolean return status?
	 * @todo Why is the ConfigurationType used here, but not in PluginConfiguration->getType?
	 * @todo When newvall is null, this might result in nullpointer exceptions (not checked)
	 */
	public void testUpdateConfigEntry() {
		final DBService db = mock(DBService.class);
		try {
			instance.updateConfigEntry(db,null,null);
			assertTrue(false);
		} catch (Exception e) {
			assertNotNull(e); // expected
		}
		
		final String name = "testName";
		try {
			assertFalse(instance.updateConfigEntry(db,name,null));
		} catch (Exception e) {
			assertNull(e);
		}
		
		final PluginConfiguration property = mock(PluginConfiguration.class);
		when(db.attachObjectToDBSession(property)).thenReturn(property);
		when(property.getName()).thenReturn(name);
		when(property.getType()).thenReturn("");
		config.add(property);
		try {
			instance.updateConfigEntry(db,name,null);
			assertTrue(false);
		} catch (Exception e) {
			assertNotNull(e); // expected
		}
		
		when(property.getType()).thenReturn(ConfigurationType.BOOLEAN.toString());
		try {
			assertFalse(instance.updateConfigEntry(db,"wrongName",null));
		} catch (Exception e) {
			assertNull(e);
		}
		
		String newVal = "test";
		try {
			instance.updateConfigEntry(db,name,newVal);
			assertTrue(false);
		} catch (Exception e) {
			assertNotNull(e); // expected
		}
		newVal = "true";
		try {
			assertTrue(instance.updateConfigEntry(db,name,newVal));
			verify(property,times(1)).setValue(newVal);
		} catch (Exception e) {
			assertNull(e);
		}
		newVal = "false";
		try {
			assertTrue(instance.updateConfigEntry(db,name,newVal));
			verify(property,times(1)).setValue(newVal);
		} catch (Exception e) {
			assertNull(e);
		}
		
		when(property.getType()).thenReturn(ConfigurationType.INTEGER.toString());
		newVal = "test";
		try {
			instance.updateConfigEntry(db,name,newVal);
			assertTrue(false);
		} catch (Exception e) {
			assertNotNull(e); // expected
		}
		newVal = "1";
		try {
			assertTrue(instance.updateConfigEntry(db,name,newVal));
			verify(property,times(1)).setValue(newVal);
		} catch (Exception e) {
			assertNull(e);
		}
		
		when(property.getType()).thenReturn(ConfigurationType.DOUBLE.toString());
		newVal = "test";
		try {
			instance.updateConfigEntry(db,name,newVal);
			assertTrue(false);
		} catch (Exception e) {
			assertNotNull(e); // expected
		}
		newVal = "1.0";
		try {
			assertTrue(instance.updateConfigEntry(db,name,newVal));
			verify(property,times(1)).setValue(newVal);
		} catch (Exception e) {
			assertNull(e);
		}
		
		when(property.getType()).thenReturn(ConfigurationType.STRING.toString());
		newVal = "test";
		try {
			assertTrue(instance.updateConfigEntry(db,name,newVal));
			verify(property,times(1)).setValue(newVal);
		} catch (Exception e) {
			assertNull(e);
		}
	}

	/**
	 * Run the boolean addConfigEntry(Plugin, String, String, String, String) method test
	 * 
	 * When the given parameters are correct (type-checked and all), a new
	 * PluginConfiguration using those parameters is created and passed to the given Plugin.
	 * 
	 * @note Replaced The first DBService entry (unused!) with a Plugin entry (non-static)
	 * for testing purposes (and it seems better anyway)
	 * 
	 * @todo No DBService is actually used, only through some given Plugin or something? 
	 * This is also inconsistent with the add/delete methods, and gives trouble when calling this...
	 * @todo Way too many parameters, and why is type a string and not a ConfigurationType?
	 * @todo Why general Exceptions (bad on themselves) AND a boolean return status?
	 * @todo No local field is used; this should probably be in the PluginConfiguration class, 
	 * as it creates a new PluginConfiguration (unchecked > untestable)
	 * @todo Code duplication for the type-checking (with updateConfigEntry)
	 * @todo Description is the third parameter but it is optional (and no overload present)
	 */
	@SuppressWarnings("unchecked")
	public void testAddConfigEntry() {
		final Plugin p = mock(Plugin.class);
		try {
			instance.addConfigEntry(p,null,null,null,null);
			assertTrue(false);
		} catch (Exception e) {
			assertNotNull(e); // expected
		}
		
		final String name = "testName";
		try {
			instance.addConfigEntry(p,name,null,null,null);
			assertTrue(false);
		} catch (Exception e) {
			assertNotNull(e); // expected
		}
		
		String type = "";
		try {
			instance.addConfigEntry(p,name,null,type,null);
			assertTrue(false);
		} catch (Exception e) {
			assertNotNull(e); // expected
		}
		type = ConfigurationType.BOOLEAN.toString();
		try {
			instance.addConfigEntry(p,name,null,type,null);
			assertTrue(false);
		} catch (Exception e) {
			assertNotNull(e); // expected
		}
		String newVal = "test";
		try {
			instance.addConfigEntry(p,name,null,type,newVal);
			assertTrue(false);
		} catch (Exception e) {
			assertNotNull(e); // expected
		}
		
		Set mockSet = mock(Set.class);
		when(mockSet.add(Mockito.anyObject())).thenReturn(true);
		when(p.getConfigurations()).thenReturn(mockSet);
		
		newVal = "true";
		try {
			assertTrue(instance.addConfigEntry(p,name,null,type,newVal));
			verify(mockSet,times(1)).add(Mockito.anyObject());
		} catch (Exception e) {
			assertNull(e);
		}
		newVal = "false";
		try {
			assertTrue(instance.addConfigEntry(p,name,null,type,newVal));
			verify(mockSet,times(2)).add(Mockito.anyObject());
		} catch (Exception e) {
			assertNull(e);
		}
		
		type = ConfigurationType.INTEGER.toString();
		newVal = "test";
		try {
			instance.addConfigEntry(p,name,null,type,newVal);
			assertTrue(false);
		} catch (Exception e) {
			assertNotNull(e); // expected
		}
		newVal = "1";
		try {
			assertTrue(instance.addConfigEntry(p,name,null,type,newVal));
			verify(mockSet,times(3)).add(Mockito.anyObject());
		} catch (Exception e) {
			assertNull(e);
		}
		
		type = ConfigurationType.DOUBLE.toString();
		newVal = "test";
		try {
			instance.addConfigEntry(p,name,null,type,newVal);
			assertTrue(false);
		} catch (Exception e) {
			assertNotNull(e); // expected
		}
		newVal = "1.0";
		try {
			assertTrue(instance.addConfigEntry(p,name,null,type,newVal));
			verify(mockSet,times(4)).add(Mockito.anyObject());
		} catch (Exception e) {
			assertNull(e);
		}
		
		type = ConfigurationType.STRING.toString();
		newVal = "test";
		try {
			assertTrue(instance.addConfigEntry(p,name,"description",type,newVal));
			verify(mockSet,times(5)).add(Mockito.anyObject());
		} catch (Exception e) {
			assertNull(e);
		}
	}

	/**
	 * Run the boolean removeConfigEntry(DBService, String, String) method test
	 * 
	 * The given name and type are used to look-up an existing PluginConfiguration entry,
	 * and when found, to delete it using the given DBService.
	 * 
	 * @todo Again, why is type a string?
	 * @todo This uses getConfPropId, which already duplicates some of the parameter-checks done here
	 */
	public void testRemoveConfigEntry() {
		final DBService db = mock(DBService.class);
		try {
			instance.removeConfigEntry(db,null,null);
			assertTrue(false);
		} catch (Exception e) {
			assertNotNull(e); // expected
		}
		
		final String name = "testName";
		try {
			instance.removeConfigEntry(db,name,null);
			assertTrue(false);
		} catch (Exception e) {
			assertNotNull(e); // expected
		}
		String type = "testType";
		try {
			instance.removeConfigEntry(db,name,type);
			assertTrue(false);
		} catch (Exception e) {
			assertNotNull(e); // expected
		}
		
		type = ConfigurationType.STRING.toString(); // doesn't matter
		try {
			assertFalse(instance.removeConfigEntry(db,name,type));
		} catch (Exception e) {
			assertNull(e);
		}
		
		final PluginConfiguration property = mock(PluginConfiguration.class);
		final Long id = Long.MAX_VALUE; 
		when(property.getId()).thenReturn(id);
		when(property.getName()).thenReturn(name);
		when(property.getType()).thenReturn(type);
		config.add(property);
		try {
			assertFalse(instance.removeConfigEntry(db,name,type));
		} catch (Exception e) {
			assertNull(e);
		}
		
		when(db.findObjectById(PluginConfiguration.class, id)).thenReturn(property);
		try {
			assertFalse(instance.removeConfigEntry(db,name,type));
		} catch (Exception e) {
			assertNull(e);
		}
		when(db.deleteRecord(property)).thenReturn(true);
		try {
			assertTrue(instance.removeConfigEntry(db,name,type));
		} catch (Exception e) {
			assertNull(e);
		}
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
	 * @todo Class implements getHashCode and compareTo, but no equals?
	 * @todo This explicitly assumes both of the objects have a hashCode set (which is optional),
	 * possibly leading to nullpointer exceptions.
	 */
	public void testCompareTo() {
		PluginInfo second = new PluginInfo();		
		final String hash = "1";
		second.setHashcode(hash);
		
		instance.setHashcode("");
		assertEquals(-1,instance.compareTo(second));
		assertEquals(1,second.compareTo(instance));
		
		instance.setHashcode(hash);
		assertEquals(0,instance.compareTo(second));
	}
}