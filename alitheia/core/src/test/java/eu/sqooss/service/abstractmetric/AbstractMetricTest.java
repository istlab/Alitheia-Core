package eu.sqooss.service.abstractmetric;

import static org.powermock.api.mockito.PowerMockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.util.Date;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.Bug;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.db.Tag;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;
import junit.framework.TestCase;

/**
 * The class <code>AbstractMetricTest</code> contains tests for the class
 * {@link <code>AbstractMetric</code>}
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(AlitheiaCore.class)
@SuppressWarnings({"unchecked","unused"})
public class AbstractMetricTest extends TestCase {
	private AbstractMetricInstance instance; 
	private AlitheiaCore core;
	private Logger log;
	private DBService db;
	private PluginAdmin pa;
	private BundleContext bc;
	private Bundle bundle;
	private Dictionary headers;
	
	private static class AbstractMetricInstance extends AbstractMetric{
		private LinkedList<Metric> supported;
		
		public AbstractMetricInstance(BundleContext bc){
			super(bc);
			supported = new LinkedList<Metric>();
		}
		
		public Metric getMetric(String mnemonic){
			Iterator<Metric> iterator =  this.metrics.getMetrics().iterator();
			while (iterator.hasNext()){
				Metric next = iterator.next();
				if( next.getMnemonic().equals(mnemonic))
					return next;
			}
			return null;
		}
		
		public List<Result> getResult(Bug b, Metric m){
			return new LinkedList<Result>();
		}
		
		public void run(Bug b){
			
		}
		
		public void addSupportedMetric(Metric metric){
			supported.add(metric);
		}
		
		public List<Metric> getAllSupportedMetrics(){
			return supported;
		}
	}
	
	/**
	 * Perform pre-test initialization
	 *
	 * @throws Exception
	 *
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		mockStatic(AlitheiaCore.class);
		core = mock(AlitheiaCore.class);
		when(AlitheiaCore.getInstance()).thenReturn(core);
		
		final LogManager logM = mock(LogManager.class);
		log = mock(Logger.class);
		when(logM.createLogger(Mockito.anyString())).thenReturn(log);
		when(core.getLogManager()).thenReturn(logM);
		db = mock(DBService.class);
		when(core.getDBService()).thenReturn(db);
		pa = mock(PluginAdmin.class);
		when(core.getPluginAdmin()).thenReturn(pa);
		
		bc = mock(BundleContext.class);
		bundle = mock(Bundle.class);
		headers = new Hashtable<String,String>();
		when(bundle.getHeaders()).thenReturn(headers);
		when(bc.getBundle()).thenReturn(bundle);
		
		instance = new AbstractMetricInstance(bc);
	}
	
	/**
	 * Run the void discoverMetrics() method test
	 */
	public void testDiscoverMetrics() {
		final MetricDeclarations md = mock(MetricDeclarations.class);
		final List<MetricDecl> metrics = new LinkedList<MetricDecl>();
		when(md.metrics()).thenReturn(metrics.toArray(new MetricDecl[metrics.size()]));
		
		instance.metrics.discoverMetrics(md); // null already called through constructor
		verify(log,times(2)).warn(Mockito.anyString());
		
		final MetricDecl metric = mock(MetricDecl.class);
		when(metric.mnemonic()).thenReturn("testMnemonic");
		when(metric.dependencies()).thenReturn(new String[0]);
		final List<Class<? extends DAObject>> activators = 
				new LinkedList<Class<? extends DAObject>>();
		activators.add(Bug.class);
		when(metric.activators()).thenReturn(
				activators.toArray((Class<? extends DAObject>[])new Class[activators.size()]));
		metrics.add(metric);
		when(md.metrics()).thenReturn(metrics.toArray(new MetricDecl[metrics.size()]));
		
		instance.metrics.discoverMetrics(md);
		verify(log,times(0)).error(Mockito.anyString());
		instance.metrics.discoverMetrics(md);
		verify(log,times(1)).error(Mockito.anyString());
		
		when(metric.mnemonic()).thenReturn("otherMnemonic");
		when(metric.dependencies()).thenReturn(new String[]{"testDependency"});
		when(pa.getImplementingPlugin("testDependency")).thenReturn(mock(AlitheiaPlugin.class));
		instance.metrics.discoverMetrics(md);
		verify(log,times(1)).error(Mockito.anyString());
	}

	/**
	 * Run the String getAuthor() method test
	 */
	public void testGetAuthor() {
		assertNull(instance.getAuthor());
		final String author = "testAuthor";
		headers.put(Constants.BUNDLE_CONTACTADDRESS,author);
		assertEquals(author,instance.getAuthor());		
	}

	/**
	 * Run the String getDescription() method test
	 */
	public void testGetDescription() {
		assertNull(instance.getDescription());
		final String description = "testDescription";
		headers.put(Constants.BUNDLE_DESCRIPTION,description);
		assertEquals(description,instance.getDescription());	
	}

	/**
	 * Run the String getName() method test
	 */
	public void testGetName() {
		assertNull(instance.getName());
		final String name = "testName";
		headers.put(Constants.BUNDLE_NAME,name);
		assertEquals(name,instance.getName());	
	}

	/**
	 * Run the String getVersion() method test
	 */
	public void testGetVersion() {
		assertNull(instance.getVersion());
		final String version = "testVersion";
		headers.put(Constants.BUNDLE_VERSION,version);
		assertEquals(version,instance.getVersion());	
	}

	/**
	 * Run the List<Result> getResult(DAObject, List<Metric>) method test
	 * Automatically tests getResultIfAlreadyCalculated as well
	 */
	public void testGetResult() {
		testDiscoverMetrics(); // logs 1 error
		final Metric metric = instance.getMetric("testMnemonic");

		final List<Metric> metrics = new LinkedList<Metric>();
		metrics.add(metric);
		final DAObject daobject = mock(Bug.class,Mockito.RETURNS_DEFAULTS);
		try {
			instance.getResult(daobject,metrics);
			verify(log,times(1)).error(Mockito.anyString());
		} catch (Exception e) {
			assertNull(e);
		}
		try {
			instance.getResult(daobject,metrics);
			verify(log,times(1)).error(Mockito.anyString());
		} catch (Exception e) {
			assertNull(e);
		}
	}

	/**
	 * Run the void run(DAObject) method test
	 */
	public void testRun() {
		final DAObject daobject1 = mock(Bug.class,Mockito.RETURNS_DEFAULTS);
		try {
			instance.run(daobject1);
			verify(log,times(0)).error(Mockito.anyString());
		} catch( Exception e ){
			assertNull(e);
		}
		final DAObject daobject2 = mock(Tag.class,Mockito.RETURNS_DEFAULTS);
		try {
			instance.run(daobject2);
		} catch( Exception e ){
			assertNull(e);
		}
	}

	/**
	 * Run the List<Metric> getSupportedMetrics(Class<? extends DAObject>) method test
	 */
	public void testGetSupportedMetrics() {
		assertTrue(instance.getSupportedMetrics(null).isEmpty());
		
		testDiscoverMetrics(); // logs 1 error
		instance.addSupportedMetric(instance.getMetric("testMnemonic"));
		
		assertTrue(instance.getSupportedMetrics(Tag.class).isEmpty());
		assertFalse(instance.getSupportedMetrics(Bug.class).isEmpty());
	}
	
	/**
	 * Run the boolean install() method test
	 */
	public void testInstall() {
		when(db.findObjectsByProperties(Mockito.eq(Plugin.class), Mockito.anyMapOf(String.class, Object.class))).
			thenReturn(new LinkedList<Plugin>());
		when(db.addRecord(Mockito.any(Plugin.class))).thenReturn(true);
		assertTrue(instance.install());
	}

	/**
	 * Run the boolean cleanup(DAObject) method test
	 */
	public void testCleanup() {
		instance.cleanup(null);
		verify(log,times(2)).warn(Mockito.anyString());
	}

	/**
	 * Run the boolean update() method test
	 */
	public void testUpdate() {
		ServiceReference ref = mock(ServiceReference.class);
		when(bc.getServiceReference(Mockito.anyString())).thenReturn(ref);
		AlitheiaCore service = mock(AlitheiaCore.class);
		when(bc.getService(ref)).thenReturn(service);
		assertFalse(instance.update());
		
		MetricActivator activator = mock(MetricActivator.class);
		when(service.getMetricActivator()).thenReturn(activator);
		assertTrue(instance.update());
		verify(activator,times(1)).syncMetrics(instance);
	}

	/**
	 * Run the String getUniqueKey() method test
	 */
	public void testGetUniqueKey() {
		assertFalse(instance.getUniqueKey().isEmpty());
	}

	/**
	 * Run the Set<PluginConfiguration> getConfigurationSchema() method test
	 */
	public void testGetConfigurationSchema() {
		when(bundle.getState()).thenReturn(Bundle.ACTIVE);
		assertTrue(instance.getConfigurationSchema().isEmpty());
		verify(log,times(2)).warn(Mockito.anyString());
		
		PluginInfo pi = mock(PluginInfo.class);
		when(pa.getPluginInfo(instance.getUniqueKey())).thenReturn(pi);
		assertTrue(instance.getConfigurationSchema().isEmpty());
		verify(log,times(2)).warn(Mockito.anyString());
	}

	/**
	 * Run the PluginConfiguration getConfigurationOption(String) method test
	 */
	public void testGetConfigurationOption() {
		PluginInfo pi = mock(PluginInfo.class);
		when(pa.getPluginInfo(instance.getUniqueKey())).thenReturn(pi);
		Set<PluginConfiguration> conf = new HashSet<PluginConfiguration>();
		when(pi.getConfiguration()).thenReturn(conf);

		final String option = "testOption";
		assertNull(instance.getConfigurationOption(option));
		
		PluginConfiguration pc1 = mock(PluginConfiguration.class);
		when(pc1.getName()).thenReturn("otherOption");
	}
}