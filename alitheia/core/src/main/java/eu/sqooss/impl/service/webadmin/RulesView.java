package eu.sqooss.impl.service.webadmin;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.VelocityContext;
import org.osgi.framework.BundleContext;

public class RulesView extends AbstractView {

	public RulesView(BundleContext bundlecontext, VelocityContext vc) {
		super(bundlecontext, vc);
	}

	@Override
	public void exec(HttpServletRequest req) {
		
	}
}
