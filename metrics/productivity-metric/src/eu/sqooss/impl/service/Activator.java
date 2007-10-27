package eu.sqooss.impl.service;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import eu.sqooss.impl.metrics.productivity.ProductivityServiceImpl;


public class Activator implements BundleActivator {

    private ServiceRegistration registration;
    
	public void start(BundleContext bc) throws Exception {
		
		 registration = bc.registerService(ProductivityServiceImpl.class.getName(),
                 new ProductivityServiceImpl(bc), null);
	}
	
	public void stop(BundleContext context) throws Exception {
	    registration.unregister();
	}

}
