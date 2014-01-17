package eu.sqooss.impl.service.tds;

import com.google.inject.AbstractModule;

import eu.sqooss.service.tds.TDSService;

public class TDSServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(TDSService.class).to(TDSServiceImpl.class);
    }

}
