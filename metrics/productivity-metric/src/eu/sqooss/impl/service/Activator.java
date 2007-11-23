package eu.sqooss.impl.service;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import eu.sqooss.impl.metrics.productivity.ProductivityMetricImpl;

public class Activator implements BundleActivator {

    private ServiceRegistration registration;

    public void start(BundleContext bc) throws Exception {

        registration = bc.registerService(ProductivityMetricImpl.class
                .getName(), new ProductivityMetricImpl(bc), null);
    }

    public void stop(BundleContext context) throws Exception {
        registration.unregister();
    }
}
