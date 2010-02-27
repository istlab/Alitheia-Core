package eu.sqooss.rest;

import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.core.Application;

public class RestServiceApp extends Application {

	private static final Set<Class<?>> serviceObjects;
	
	static {
		serviceObjects = new TreeSet<Class<?>>();
		serviceObjects.add(StoredProjectResource.class);
	}
	
	public static void addServiceObject(Class<?> object) {
		if (! serviceObjects.contains(object))
			serviceObjects.add(object);
	}
	
	@Override
	public Set<Class<?>> getClasses() {
		return serviceObjects;
	} 
	
	public Set<Object> getSingletons() {
		return null;
	}
}
