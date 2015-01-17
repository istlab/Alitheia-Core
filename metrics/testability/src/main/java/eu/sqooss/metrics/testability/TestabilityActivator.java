package eu.sqooss.metrics.testability;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import eu.sqooss.metrics.testability.TestabilityImplementation;


public class TestabilityActivator implements BundleActivator {

    private ServiceRegistration registration;

    public void start(BundleContext bc) throws Exception {

        registration = bc.registerService(TestabilityImplementation.class.getName(),
                new TestabilityImplementation(bc), null);
    }

    public void stop(BundleContext context) throws Exception {
        registration.unregister();
    }
}
