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

public class WebUIServiceImpl implements WebUIService {
    public WebUIServiceImpl(BundleContext bc) { 
        System.out.println("foo");
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


