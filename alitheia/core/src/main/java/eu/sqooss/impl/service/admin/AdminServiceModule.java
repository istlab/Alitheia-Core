package eu.sqooss.impl.service.admin;

import com.google.inject.AbstractModule;

import eu.sqooss.service.admin.AdminService;

public class AdminServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AdminService.class).to(AdminServiceImpl.class);
    }
    
}
