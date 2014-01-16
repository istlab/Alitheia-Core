package eu.sqooss.impl.service.webadmin;

import org.apache.velocity.VelocityContext;
import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;

//Test class to test Abstractview
class TestAbstractView extends AbstractView {
	
	AlitheiaCore instance;

	public TestAbstractView(BundleContext bundlecontext, VelocityContext vc, AlitheiaCore instance) {
		super(bundlecontext, vc);
		this.instance = instance;
	}
	
	@Override
	protected AlitheiaCore getInstance() {
		return this.instance;
	}

}
