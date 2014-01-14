package eu.sqooss.impl.service.logging;

import com.google.inject.AbstractModule;

import eu.sqooss.service.logging.LogManager;

public class LogManagerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(LogManager.class).to(LogManagerImpl.class);
    }

}
