package eu.sqooss.core;

import com.google.inject.AbstractModule;

import eu.sqooss.impl.service.admin.AdminServiceImpl;
import eu.sqooss.impl.service.cluster.ClusterNodeServiceImpl;
import eu.sqooss.impl.service.db.DBServiceImpl;
import eu.sqooss.impl.service.fds.FDSServiceImpl;
import eu.sqooss.impl.service.logging.LogManagerImpl;
import eu.sqooss.impl.service.metricactivator.MetricActivatorImpl;
import eu.sqooss.impl.service.pa.PAServiceImpl;
import eu.sqooss.impl.service.rest.ResteasyServiceImpl;
import eu.sqooss.impl.service.scheduler.SchedulerServiceImpl;
import eu.sqooss.impl.service.tds.TDSServiceImpl;
import eu.sqooss.impl.service.updater.UpdaterServiceImpl;
import eu.sqooss.impl.service.webadmin.WebadminServiceImpl;
import eu.sqooss.service.admin.AdminService;
import eu.sqooss.service.cluster.ClusterNodeService;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.fds.FDSService;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.rest.RestService;
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.UpdaterService;
import eu.sqooss.service.webadmin.WebadminService;

public class AlitheiaCoreModule extends AbstractModule {

	@Override
	protected void configure() {
		// note: the service is exported to the registry at injection time
		bind(LogManager.class).to(LogManagerImpl.class);
		bind(DBService.class).to(DBServiceImpl.class);
		bind(PluginAdmin.class).to(PAServiceImpl.class);
		bind(Scheduler.class).to(SchedulerServiceImpl.class);
		bind(TDSService.class).to(TDSServiceImpl.class);
		bind(ClusterNodeService.class).to(ClusterNodeServiceImpl.class);
		bind(FDSService.class).to(FDSServiceImpl.class);
		bind(MetricActivator.class).to(MetricActivatorImpl.class);
		bind(UpdaterService.class).to(UpdaterServiceImpl.class);
		bind(WebadminService.class).to(WebadminServiceImpl.class);
		bind(RestService.class).to(ResteasyServiceImpl.class);
		bind(AdminService.class).to(AdminServiceImpl.class);
	}

}

