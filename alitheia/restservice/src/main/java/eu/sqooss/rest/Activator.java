package eu.sqooss.rest;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.rest.impl.ResteasyServiceImpl;
import eu.sqooss.rest.impl.ResteasyServlet;
import eu.sqooss.service.logging.Logger;

public class Activator implements BundleActivator {

	private ServiceTracker osgiServiceTracker;
	
	@SuppressWarnings("unchecked")
	public void start(BundleContext bc) throws Exception {
	   
	    Logger log = AlitheiaCore.getInstance().getLogManager().createLogger("rest");
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
		
		ResteasyServlet bridge = new ResteasyServlet();
		try {
			http.registerServlet("/api", bridge,
					new Hashtable(), null);
		} catch (Exception e) {
			log.error("Error registering ResteasyServlet", e);
		}

		log.info("Bundle started sucessfully");
		
		log.info("Starting RESTEasy OSGi service");
		ResteasyServiceImpl service = new ResteasyServiceImpl(bridge.getServletContext());
		bc.registerService(ResteasyServiceImpl.SERVICE_NAME,service,new Hashtable());
		
		log.info("RESTEasy OSGi service started: " + ResteasyServiceImpl.class.getName());
	}

	public void stop(BundleContext context) throws Exception {
		//log.info("Stopping bundle " + context.getBundle().getSymbolicName() + " [" + context.getBundle() + "]");
		
		osgiServiceTracker.close();
		osgiServiceTracker = null;
		
		//log.info("Bundle stopped sucessfully");
	}
}