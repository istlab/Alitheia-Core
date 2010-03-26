package eu.sqooss.rest;

public interface ResteasyService {
	
	/**
	 * Service name inside OSGi namespace service registration.
	 */
	public static final String SERVICE_NAME = ResteasyService.class.getName();

	/**
	 * Add a SingletonResource.
	 * @param resource
	 */
	public void addSingletonResource(Object resource);
	
	/**
	 * Remove a SingletonResource.
	 * @param clazz
	 */
	//public void removeSingletonResource(Class<?> clazz);

}