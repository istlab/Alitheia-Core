package eu.sqooss.impl.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import eu.sqooss.impl.service.webui.WebUIServiceImpl;
import eu.sqooss.services.webui.WebUIService;

public class WebUIActivator implements BundleActivator {
    private WebUIServiceImpl webuiService;
    private ServiceRegistration registration;

    public void start(BundleContext bc) throws Exception {
        webuiService = new WebUIServiceImpl(bc);
        registration = bc.registerService(WebUIService.class.getName(), 
                                          webuiService, null);
        System.out.println("# WebUIActivator::start done.");
    }

    public void stop(BundleContext bc) throws Exception {
        registration.unregister();
    }
}
