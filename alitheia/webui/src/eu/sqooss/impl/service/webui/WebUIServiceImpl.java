package eu.sqooss.impl.service.webui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;

import eu.sqooss.services.webui.WebUIService;
import eu.sqooss.impl.service.webui.WebUIServer;

public class WebUIServiceImpl implements WebUIService {
    WebUIServer ui;

    public WebUIServiceImpl(BundleContext bc) { 
        System.out.println("ItWorks UI 2.0\n");
        ui = new WebUIServer();
    }

    public String[] getConfigurationKeys() {
        return null;
    }

    public String getConfigurationProperty(String key) {
        return key;
    }

    public void setConfigurationProperty(String key, String val) {
    }
}


