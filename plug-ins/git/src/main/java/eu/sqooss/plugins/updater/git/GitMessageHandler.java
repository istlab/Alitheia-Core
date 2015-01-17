package eu.sqooss.plugins.updater.git;

import eu.sqooss.service.logging.Logger;

public class GitMessageHandler {
	
    private String projectName;
    private Logger log;
	
	public GitMessageHandler(String projectName, Logger log){
		this.projectName = projectName;
		this.log = log;
	}
	
    /** Convenience method to write warning messages per project */
    protected void warn(String message) {
            log.warn("Git:" + projectName + ":" + message);
    }
    
    /** Convenience method to write error messages per project */
    protected void err(String message) {
            log.error("Git:" + projectName + ":" + message);
    }
    
    /** Convenience method to write info messages per project */
    protected void info(String message) {
            log.info("Git:" + projectName + ":" + message);
    }
    
    /** Convenience method to write debug messages per project */
    protected void debug(String message) {
        if (log != null)
            log.debug("Git:" + projectName + ":" + message);
        else
            System.err.println(message);
    }
}
