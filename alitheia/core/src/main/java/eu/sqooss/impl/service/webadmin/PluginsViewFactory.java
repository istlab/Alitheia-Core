package eu.sqooss.impl.service.webadmin;

import org.apache.velocity.VelocityContext;
import org.osgi.framework.BundleContext;

public interface PluginsViewFactory {
	PluginsView create(BundleContext bundlecontext, VelocityContext vc);
}
