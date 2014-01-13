package eu.sqooss.impl.service.updater;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.updater.MetadataUpdater;

public class UpdaterModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().implement(Job.class,
                UpdaterJob.class).build(UpdaterJobFactory.class));
    }

}

interface UpdaterJobFactory {
    UpdaterJob create(MetadataUpdater updater);
}
