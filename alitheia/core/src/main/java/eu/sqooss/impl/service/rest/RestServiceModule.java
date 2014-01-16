package eu.sqooss.impl.service.rest;

import javax.servlet.http.HttpServlet;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class RestServiceModule extends AbstractModule 
{
	@Override
	protected void configure() 
	{
		install(new FactoryModuleBuilder().implement(HttpServlet.class,
				ResteasyServlet.class).build(ResteasyServletFactory.class));
	}

}
