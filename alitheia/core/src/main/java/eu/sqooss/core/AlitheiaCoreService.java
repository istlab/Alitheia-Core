package eu.sqooss.core;

/**
 * An interface to control Alitheia Core internal services.
 */
public interface AlitheiaCoreService {

	public boolean init();
	
	public void shutDown();
}
