package eu.sqooss.plugin;

import java.util.HashMap;

import eu.sqooss.db.Metric;

/**
 * The interface for all the plugins
 */
public interface Plugin {
    public HashMap<String,String> run(String file);
    
    public Metric[] getMetrics();
    
    public String getDescription();
}