package eu.sqooss.core;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

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
import eu.sqooss.service.scheduler.Scheduler;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.UpdaterService;
import eu.sqooss.service.webadmin.WebadminService;


public class CoreModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(AdminServiceImpl.class);
		bind(ClusterNodeServiceImpl.class);
		bind(DBServiceImpl.class);
		bind(FDSServiceImpl.class);
		bind(LogManagerImpl.class);
		bind(MetricActivatorImpl.class);
		bind(PAServiceImpl.class);
		bind(ResteasyServiceImpl.class);
		bind(SchedulerServiceImpl.class);
		bind(TDSServiceImpl.class);
		bind(UpdaterServiceImpl.class);
		bind(WebadminServiceImpl.class);
	}

	@Provides
	LogManager provideLogManager() {
		return AlitheiaCore.getInstance().getLogManager();
	}

	@Provides
	WebadminService provideWebadminService() {
        return AlitheiaCore.getInstance().getWebadminService();
    }

	@Provides
	PluginAdmin providePluginAdmin() {
		return AlitheiaCore.getInstance().getPluginAdmin();
    }

	@Provides
	DBService provideDBService() {
		return AlitheiaCore.getInstance().getDBService();
    }

	@Provides
	FDSService provideFDSService() {
		return AlitheiaCore.getInstance().getFDSService();
    }

	@Provides
	Scheduler provideScheduler() {
		return AlitheiaCore.getInstance().getScheduler();
    }

	@Provides
	SecurityManager provideSecurityManager() {
		return AlitheiaCore.getInstance().getSecurityManager();
    }

	@Provides
	TDSService provideTDSService() {
		return AlitheiaCore.getInstance().getTDSService();
    }

	@Provides
	UpdaterService provideUpdater() {
		return AlitheiaCore.getInstance().getUpdater();
    }

	@Provides
	ClusterNodeService provideClusterNodeService() {
		return AlitheiaCore.getInstance().getClusterNodeService();
    }

	@Provides
	MetricActivator provideMetricActivator() {
		return AlitheiaCore.getInstance().getMetricActivator();
    }

	@Provides
	AdminService provideAdminService() {
		return AlitheiaCore.getInstance().getAdminService();
    }
}

