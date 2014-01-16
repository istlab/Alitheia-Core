package eu.sqooss.test.service.metricactivator;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;

import static org.mockito.Mockito.*;

import eu.sqooss.impl.service.metricactivator.MetricActivatorImpl;
import eu.sqooss.impl.service.metricactivator.MetricActivatorJobFactory;
import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.test.testutils.TestUtils;

public class MetricActivatorImplTest 
{
	private PluginAdmin pa;
	private DBService dbs;
	private Scheduler sched;
	private MetricActivatorJobFactory majf;
	private MetricActivatorImpl ma;
	
	@Before
	public void setUp()
	{
		pa = mock(PluginAdmin.class);
		dbs = mock(DBService.class);
		sched = mock(Scheduler.class);
		majf = mock(MetricActivatorJobFactory.class);
		
		ma = new MetricActivatorImpl(null, TestUtils.provide(pa),
				TestUtils.provide(dbs), TestUtils.provide(sched), majf);
		
		BundleContext bc = mock(BundleContext.class);
		Logger l = mock(Logger.class);
		
		ma.setInitParams(bc, l);
		ma.startUp();
	}
	
	@Test
	public void testPAInjection() 
	{
		ma.syncMetrics((StoredProject) null);
		
		verify(pa).listPlugins();
	}
	
	@Test
	public void testDBInjection()
	{
		ma.syncMetrics((AlitheiaPlugin) null);
		
		verify(dbs).doHQL("from StoredProject");
	}

	@Test
	public void testSchedulerInjection()
	{
		DAObject dao = mock(DAObject.class);
		AbstractMetric am = mock(AbstractMetric.class);
		ma.runMetric(dao, am);
		
		try {
			verify(sched).enqueue(any(Job.class));
		} catch (SchedulerException e) {
			fail("Test failed: " + e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testMetricActivatorJobFactoryInjection() 
	{
		DAObject dao = mock(DAObject.class);
		AbstractMetric am = mock(AbstractMetric.class);
		ma.runMetric(dao, am);
		
		verify(majf).create(any(AbstractMetric.class), any(Long.class),
				any(Logger.class), any(Class.class), any(long.class), anyBoolean());
		
	}
}
