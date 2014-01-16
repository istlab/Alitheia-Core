package eu.sqooss.impl.service.webadmin;

import org.apache.velocity.VelocityContext;
import org.osgi.framework.BundleContext;

public interface WebAdminRendererFactory {
	WebAdminRenderer create(BundleContext bundlecontext, VelocityContext vc);
}
