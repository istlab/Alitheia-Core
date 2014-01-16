package eu.sqooss.impl.service.fds;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import eu.sqooss.service.fds.OnDiskCheckout;
import eu.sqooss.service.fds.Timeline;

public class FDSServiceModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new FactoryModuleBuilder().implement(OnDiskCheckout.class,
					OnDiskCheckoutImpl.class).build(OnDiskCheckoutFactory.class));
		install(new FactoryModuleBuilder().implement(Timeline.class, 
					TimelineImpl.class).build(TimelineFactory.class));
	}
}
