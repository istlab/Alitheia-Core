package eu.sqooss.impl.service.updater;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.updater.MetadataUpdater;
import eu.sqooss.service.updater.UpdaterService;

public class UpdaterServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(UpdaterService.class).to(UpdaterServiceImpl.class);
        install(new FactoryModuleBuilder().implement(Job.class,
                UpdaterJob.class).build(UpdaterJobFactory.class));
    }

}

interface UpdaterJobFactory {
    UpdaterJob create(MetadataUpdater updater);
}
