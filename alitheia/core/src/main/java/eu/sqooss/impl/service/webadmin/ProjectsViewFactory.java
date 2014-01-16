package eu.sqooss.impl.service.webadmin;

import org.apache.velocity.VelocityContext;
import org.osgi.framework.BundleContext;

public interface ProjectsViewFactory {
	ProjectsView create(BundleContext bc, VelocityContext vc);
}
