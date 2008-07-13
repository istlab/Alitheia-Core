package eu.sqooss.metrics.modulemetrics;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import eu.sqooss.impl.metrics.modulemetrics.ModuleMetricsImplementation;


public class ModuleMetricsActivator implements BundleActivator {

    private ServiceRegistration registration;

    public void start(BundleContext bc) throws Exception {

        registration = bc.registerService(ModuleMetricsImplementation.class.getName(),
                new ModuleMetricsImplementation(bc), null);
    }

    public void stop(BundleContext context) throws Exception {
        registration.unregister();
    }
}
