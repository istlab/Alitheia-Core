package eu.sqooss.core;

import org.osgi.framework.BundleContext;

import eu.sqooss.service.logging.Logger;

/**
 * An interface to control Alitheia Core internal services. All services 
 * implementing this interface should also contain an empty constructor.
 * The service initialisation sequence consists of setting the initialisation
 * parameters with the {@link #setInitParams(BundleContext, Logger)} method
 * and then calling the {@link #startUp()} method.
 */
public interface AlitheiaCoreService {

	/**
	 * Tell an internal service to initialise itself.
	 */
	public boolean startUp();
	
	/**
	 * Tell an internal service to shutdown.
	 */
	public void shutDown();
	
	/**
	 * Create and return a new service instance. This method should create
	 * intances of a service even if the service class has not been initialised
	 * so it should be implemented as a static method.
	 */
	public void setInitParams(BundleContext bc, Logger l);
}
