package eu.sqooss.impl.service.webui;

import java.util.Hashtable;
import javax.servlet.*;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.InvalidSyntaxException;

import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import eu.sqooss.services.webui.WebUIService;
import eu.sqooss.impl.service.webui.WebUIServer;

public class WebUIServiceImpl implements WebUIService {
    ServiceReference serviceref = null;
    HttpService httpservice = null;
    WebUIServer httpui = null;

    public WebUIServiceImpl(BundleContext bc) throws Exception { 
        System.out.println("# WebUIServiceImpl()");
        serviceref = bc.getServiceReference("org.osgi.service.http.HttpService");
        if (serviceref != null) {
            System.out.println("#   Got service reference.");
            httpservice = (HttpService) bc.getService(serviceref);
            httpui = new WebUIServer();
            httpservice.registerServlet("/", (Servlet) httpui,
                                        new Hashtable(), null);
        } else {
            System.out.println("! Could not load the HTTP service.");
        }
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


