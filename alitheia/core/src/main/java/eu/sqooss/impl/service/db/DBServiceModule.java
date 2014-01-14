package eu.sqooss.impl.service.db;

import com.google.inject.AbstractModule;

import eu.sqooss.service.db.DBService;

public class DBServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(DBService.class).to(DBServiceImpl.class);
    }

}
