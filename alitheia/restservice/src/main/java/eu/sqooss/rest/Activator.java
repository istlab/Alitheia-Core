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

	private Logger log = AlitheiaCore.getInstance().getLogManager().createLogger("rest");
	
	private ServiceTracker osgiServiceTracker;
	
	private static ResteasyServlet bridge = null;
	
	@SuppressWarnings("unchecked")
	public void start(BundleContext bc) throws Exception {
		log.info("Starting bundle " + bc.getBundle().getSymbolicName() 
				+ " [" + bc.getBundle().getVersion() + "]");
		
		HttpService http = null;
		ServiceReference httpRef = bc.getServiceReference(
				HttpService.class.getName());

		if (httpRef != null) {
			http = (HttpService) bc.getService(httpRef);
		} else {
			log.error("Could not find a HTTP service!");
			return;
		}

		try {
			http.registerServlet("/api", new ResteasyServlet(),
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
		log.info("Stopping bundle " + context.getBundle().getSymbolicName() + " [" + context.getBundle().getVersion() + "]");
		
		osgiServiceTracker.close();
		osgiServiceTracker = null;
		
		log.info("Bundle stopped sucessfully");
	}
}