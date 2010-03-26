package eu.sqooss.rest;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.rest.impl.ResteasyServiceImpl;
import eu.sqooss.rest.impl.ResteasyServlet;
import eu.sqooss.service.logging.Logger;

public class Activator implements BundleActivator {
	
    Logger log;
    
	@SuppressWarnings("unchecked")
	public void start(BundleContext bc) throws Exception {
	   
	    Logger log = AlitheiaCore.getInstance().getLogManager().createLogger("sqooss.rest");
		log.info("Starting bundle " + bc.getBundle().getSymbolicName() 
				+ " [" + bc.getBundle() + "]");
		
		HttpService http = null;
		ServiceReference httpRef = bc.getServiceReference(
				HttpService.class.getName());

		if (httpRef != null) {
			http = (HttpService) bc.getService(httpRef);
		} else {
			log.error("Could not find a HTTP service!");
			return;
		}
		
		Dictionary<String, String> params = new Hashtable<String, String>();
		params.put("resteasy.scan", "false");
		params.put("javax.ws.rs.Application", "eu.sqooss.rest.api.RestServiceApp");
		
		ResteasyServlet bridge = new ResteasyServlet();
		try {
			http.registerServlet("/api/*", bridge, params, null);
		} catch (Exception e) {
			log.error("Error registering ResteasyServlet", e);
		}
		
		ResteasyServiceImpl service = new ResteasyServiceImpl(bridge.getServletContext());
		bc.registerService(ResteasyServiceImpl.SERVICE_NAME,service,new Hashtable());
		
		log.info("RESTEasy OSGi service started: " + ResteasyServiceImpl.class.getName());
	}

	public void stop(BundleContext bc) throws Exception {
		log.info("Bundle rest stopped sucessfully");
	}
}