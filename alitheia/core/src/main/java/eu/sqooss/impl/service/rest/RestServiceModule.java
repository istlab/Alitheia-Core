package eu.sqooss.impl.service.rest;

import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class RestServiceModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new FactoryModuleBuilder().implement(HttpServletDispatcher.class,
				ResteasyServlet.class).build(ResteasyServletFactory.class));
	}

}

interface ResteasyServletFactory {
	ResteasyServlet create();
}
