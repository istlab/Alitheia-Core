package eu.sqooss.metrics.mde;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import eu.sqooss.impl.metrics.mde.MDEImplementation;


public class MDEActivator implements BundleActivator {

    private ServiceRegistration registration;

    public void start(BundleContext bc) throws Exception {

        registration = bc.registerService(MDEImplementation.class.getName(),
                new MDEImplementation(bc), null);
    }

    public void stop(BundleContext context) throws Exception {
        registration.unregister();
    }
}
