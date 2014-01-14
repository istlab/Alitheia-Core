package eu.sqooss.impl.service.rest;

import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import eu.sqooss.service.rest.RestService;

public class RestServiceModule extends AbstractModule {

	@Override
	protected void configure() {
	    bind(RestService.class).to(ResteasyServiceImpl.class);
		install(new FactoryModuleBuilder().implement(HttpServletDispatcher.class,
				ResteasyServlet.class).build(ResteasyServletFactory.class));
	}

}

interface ResteasyServletFactory {
	ResteasyServlet create();
}
