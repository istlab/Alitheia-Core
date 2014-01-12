package eu.sqooss.impl.service.metricactivator;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.scheduler.Job;

public class MetricActivatorModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new FactoryModuleBuilder().implement(Job.class,
				MetricActivatorJob.class)
				.build(MetricActivatorJobFactory.class));
	}

}

interface MetricActivatorJobFactory {
	MetricActivatorJob create(AbstractMetric m, @Assisted("daoID") Long daoID,
			Logger l, Class<? extends DAObject> daoType,
			@Assisted("priority") long priority, boolean fastSync);
}