package eu.sqooss.core;

import com.google.inject.AbstractModule;

public class AlitheiaCoreModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AlitheiaCore.class);
    }

}
