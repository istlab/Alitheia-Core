package eu.sqooss.impl.service.webadmin;

import javax.servlet.http.HttpServlet;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import eu.sqooss.service.scheduler.Job;

public class WebAdminServiceModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new FactoryModuleBuilder().implement(AbstractView.class,
				ProjectsView.class).build(ProjectsViewFactory.class));
		
		install(new FactoryModuleBuilder().implement(AbstractView.class,
				PluginsView.class).build(PluginsViewFactory.class));
		
		install(new FactoryModuleBuilder().implement(AbstractView.class,
				WebAdminRenderer.class).build(WebAdminRendererFactory.class));
		
		install(new FactoryModuleBuilder().implement(HttpServlet.class,
				AdminServlet.class).build(AdminServletFactory.class));
		
		install(new FactoryModuleBuilder().implement(Job.class,
				ProjectDeleteJob.class).build(ProjectDeleteJobFactory.class));
	}

}
