package eu.sqooss.impl.service.pa;

import com.google.inject.AbstractModule;

import eu.sqooss.service.pa.PluginAdmin;

public class PluginAdminModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(PluginAdmin.class).to(PAServiceImpl.class);
    }

}
