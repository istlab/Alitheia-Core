package eu.sqooss.service.pa;

import java.util.Set;

import org.osgi.framework.ServiceReference;

import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.PluginConfiguration;
import junit.framework.TestCase;

/**
 * The class <code>PluginInfoTest</code> contains tests for the class {@link
 * <code>PluginInfo</code>}
 */
public class PluginInfoTest extends TestCase {

	/**
	 * The object that is being tested.
	 *
	 * @see eu.sqooss.service.pa.PluginInfo
	 */
	private PluginInfo instance;

	/**
	 * Perform pre-test initialization
	 *
	 * @throws Exception
	 *
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		instance = new PluginInfo();
		// Add additional set up code here
	}

	/**
	 * Run the void setPluginConfiguration(Set<PluginConfiguration>) method
	 * test
	 */
	public void testSetPluginConfiguration()
	{
		fail("Newly generated method - fix or disable");
		// add test code here
		Set<PluginConfiguration> c = null;
		instance.setPluginConfiguration(c);
		assertTrue(false);
	}
	/**
	 * Run the Set<PluginConfiguration> getConfiguration() method test
	 */
	public void testGetConfiguration()
	{
		fail("Newly generated method - fix or disable");
		// add test code here
		Set<PluginConfiguration> result = instance.getConfiguration();
		assertTrue(false);
	}
	/**
	 * Run the Long getConfPropId(String, String) method test
	 */
	public void testGetConfPropId() {
		fail("Newly generated method - fix or disable");
		// add test code here
		String name = null;
		String type = null;
		Long result = instance.getConfPropId(name, type);
		assertTrue(false);
	}

	/**
	 * Run the boolean hasConfProp(String, String) method test
	 */
	public void testHasConfProp() {
		fail("Newly generated method - fix or disable");
		// add test code here
		String name = null;
		String type = null;
		boolean result = instance.hasConfProp(name, type);
		assertTrue(false);
	}

	/**
	 * Run the boolean updateConfigEntry(DBService, String, String) method test
	 */
	public void testUpdateConfigEntry() {
		fail("Newly generated method - fix or disable");
		// add test code here
		DBService db = null;
		String name = null;
		String newVal = null;
		try {
			boolean result = instance.updateConfigEntry(db, name, newVal);
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		assertTrue(false);
	}

	/**
	 * Run the boolean addConfigEntry(DBService, String, String, String,
	 * String) method test
	 */
	public void testAddConfigEntry() {
		fail("Newly generated method - fix or disable");
		// add test code here
		DBService db = null;
		String name = null;
		String description = null;
		String type = null;
		String value = null;
		try {
			boolean result = instance.addConfigEntry(
				db,
				name,
				description,
				type,
				value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		assertTrue(false);
	}

	/**
	 * Run the boolean removeConfigEntry(DBService, String, String) method test
	 */
	public void testRemoveConfigEntry() {
		fail("Newly generated method - fix or disable");
		// add test code here
		DBService db = null;
		String name = null;
		String type = null;
		try {
			boolean result = instance.removeConfigEntry(db, name, type);
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		assertTrue(false);
	}

	/**
	 * Run the void setPluginName(String) method test
	 */
	public void testSetPluginName() {
		fail("Newly generated method - fix or disable");
		// add test code here
		String metricName = null;
		instance.setPluginName(metricName);
		assertTrue(false);
	}

	/**
	 * Run the String getPluginName() method test
	 */
	public void testGetPluginName() {
		fail("Newly generated method - fix or disable");
		// add test code here
		String result = instance.getPluginName();
		assertTrue(false);
	}

	/**
	 * Run the void setPluginVersion(String) method test
	 */
	public void testSetPluginVersion() {
		fail("Newly generated method - fix or disable");
		// add test code here
		String metricVersion = null;
		instance.setPluginVersion(metricVersion);
		assertTrue(false);
	}

	/**
	 * Run the String getPluginVersion() method test
	 */
	public void testGetPluginVersion() {
		fail("Newly generated method - fix or disable");
		// add test code here
		String result = instance.getPluginVersion();
		assertTrue(false);
	}

	/**
	 * Run the void setActivationTypes(Set<Class<? extends DAObject>>) method
	 * test
	 */
	public void testSetActivationTypes()
	{
		fail("Newly generated method - fix or disable");
		// add test code here
		Set<Class<? extends DAObject>> l = null;
		instance.setActivationTypes(l);
		assertTrue(false);
	}
	/**
	 * Run the Set<Class<? extends DAObject>> getActivationTypes() method test
	 */
	public void testGetActivationTypes()
	{
		fail("Newly generated method - fix or disable");
		// add test code here
		Set<Class<? extends DAObject>> result = instance.getActivationTypes();
		assertTrue(false);
	}
	/**
	 * Run the void addActivationType(Class<? extends DAObject>) method test
	 */
	public void testAddActivationType()
	{
		fail("Newly generated method - fix or disable");
		// add test code here
		Class<? extends DAObject> activator = null;
		instance.addActivationType(activator);
		assertTrue(false);
	}
	/**
	 * Run the boolean isActivationType(Class<? extends DAObject>) method test
	 */
	public void testIsActivationType()
	{
		fail("Newly generated method - fix or disable");
		// add test code here
		Class<? extends DAObject> o = null;
		boolean result = instance.isActivationType(o);
		assertTrue(false);
	}
	/**
	 * Run the void setServiceRef(ServiceReference) method test
	 */
	public void testSetServiceRef() {
		fail("Newly generated method - fix or disable");
		// add test code here
		ServiceReference serviceRef = null;
		instance.setServiceRef(serviceRef);
		assertTrue(false);
	}

	/**
	 * Run the ServiceReference getServiceRef() method test
	 */
	public void testGetServiceRef() {
		fail("Newly generated method - fix or disable");
		// add test code here
		ServiceReference result = instance.getServiceRef();
		assertTrue(false);
	}

	/**
	 * Run the void setHashcode(String) method test
	 */
	public void testSetHashcode() {
		fail("Newly generated method - fix or disable");
		// add test code here
		String hashcode = null;
		instance.setHashcode(hashcode);
		assertTrue(false);
	}

	/**
	 * Run the String getHashcode() method test
	 */
	public void testGetHashcode() {
		fail("Newly generated method - fix or disable");
		// add test code here
		String result = instance.getHashcode();
		assertTrue(false);
	}

	/**
	 * Run the String toString() method test
	 */
	public void testToString() {
		fail("Newly generated method - fix or disable");
		// add test code here
		String result = instance.toString();
		assertTrue(false);
	}

	/**
	 * Run the int compareTo(PluginInfo) method test
	 */
	public void testCompareTo() {
		fail("Newly generated method - fix or disable");
		// add test code here
		PluginInfo pi = null;
		int result = instance.compareTo(pi);
		assertTrue(false);
	}
}