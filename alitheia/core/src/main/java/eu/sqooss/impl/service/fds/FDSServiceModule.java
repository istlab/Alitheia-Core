package eu.sqooss.impl.service.fds;

import java.io.File;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.fds.OnDiskCheckout;
import eu.sqooss.service.fds.Timeline;
import eu.sqooss.service.tds.SCMAccessor;

public class FDSServiceModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new FactoryModuleBuilder().implement(OnDiskCheckout.class,
				OnDiskCheckoutImpl.class).build(OnDiskCheckoutFactory.class));
		install(new FactoryModuleBuilder().implement(Timeline.class,
				TimelineImpl.class).build(TimelineFactory.class));
	}
}

interface OnDiskCheckoutFactory {
	OnDiskCheckout create(SCMAccessor accessor, String path, ProjectVersion pv,
			File root);
}

interface TimelineFactory {
	Timeline create(StoredProject project);
}