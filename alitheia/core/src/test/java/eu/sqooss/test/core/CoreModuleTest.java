package eu.sqooss.test.core;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import eu.sqooss.core.CoreModule;
import eu.sqooss.impl.service.fds.FDSServiceModule;
import eu.sqooss.impl.service.metricactivator.MetricActivatorModule;
import eu.sqooss.impl.service.rest.RestServiceModule;
import eu.sqooss.impl.service.scheduler.SchedulerServiceModule;
import eu.sqooss.impl.service.webadmin.WebAdminServiceModule;
import eu.sqooss.service.admin.AdminService;
import eu.sqooss.service.cluster.ClusterNodeService;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.UpdaterService;
import eu.sqooss.service.webadmin.WebadminService;

public class CoreModuleTest 
{
	private Injector injector;
	
	@Before
	public void setUp()
	{
		injector = Guice.createInjector(	new CoreModule(), 
													new SchedulerServiceModule(),
													new RestServiceModule(),
													new FDSServiceModule(),
													new MetricActivatorModule(),
													new WebAdminServiceModule());
	}
	
	@Test
	public void testAdminService() 
	{		
		injector.getProvider(AdminService.class);
	}
	
	@Test
	public void testClusterNodeService() 
	{		
		injector.getProvider(ClusterNodeService.class);
	}
	
	@Test
	public void testDBService() 
	{		
		injector.getProvider(DBService.class);
	}
	
	@Test
	public void testFDSService() 
	{		
		injector.getProvider(FDSService.class);
	}
	
	@Test
	public void LogManager() 
	{		
		injector.getProvider(LogManager.class);
	}
	
	@Test
	public void testMetricActivator() 
	{
		injector.getProvider(MetricActivator.class);
	}
	
	@Test
	public void testPAService() 
	{
		injector.getProvider(PluginAdmin.class);
	}
	
	@Test
	public void testScheduler() 
	{
		injector.getProvider(Scheduler.class);
	}
	
	@Test
	public void testTDSService() 
	{
		injector.getProvider(TDSService.class);
	}
	
	@Test
	public void testUpdaterService() 
	{
		injector.getProvider(UpdaterService.class);
	}
	
	@Test
	public void testWebadminService() 
	{
		injector.getProvider(WebadminService.class);
	}
}
