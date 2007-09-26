package eu.sqooss.impl.service;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import eu.sqooss.services.updater.UpdaterService;
import eu.sqooss.impl.service.updater.UpdaterServiceImpl;

public class UpdaterActivator implements BundleActivator {
    private UpdaterServiceImpl updaterService;
    private ServiceRegistration registration;

    public void start(BundleContext bc) throws Exception {
        updaterService = new UpdaterServiceImpl(bc);
        registration = bc.registerService(UpdaterService.class.getName(), 
                                          updaterService, null);
        System.out.println("# WebUIActivator::start done.");
    }

    public void stop(BundleContext bc) throws Exception {
        registration.unregister();
    }
}
