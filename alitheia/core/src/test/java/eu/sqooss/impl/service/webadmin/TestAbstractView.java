package eu.sqooss.impl.service.webadmin;

import org.apache.velocity.VelocityContext;
import org.osgi.framework.BundleContext;

//Test class to test Abstractview
class TestAbstractView extends AbstractView {

	public TestAbstractView(BundleContext bundlecontext, VelocityContext vc) {
		super(bundlecontext, vc);
	}

}
