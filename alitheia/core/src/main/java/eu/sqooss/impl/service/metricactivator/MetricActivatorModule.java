package eu.sqooss.impl.service.metricactivator;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import eu.sqooss.service.scheduler.Job;

public class MetricActivatorModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new FactoryModuleBuilder().implement(Job.class,
				MetricActivatorJob.class).build(MetricActivatorJobFactory.class));
	}

}
