package eu.sqooss.impl.service.scheduler;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import eu.sqooss.service.scheduler.WorkerThread;

public class SchedulerServiceModule extends AbstractModule {
	
	@Override
	protected void configure() {
		install(new FactoryModuleBuilder().
				implement(WorkerThread.class, WorkerThreadImpl.class).
				build(WorkerThreadFactory.class));
	}
}