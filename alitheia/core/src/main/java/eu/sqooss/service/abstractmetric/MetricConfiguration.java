package eu.sqooss.service.abstractmetric;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.osgi.framework.Bundle;

import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.pa.ConfigurationType;
import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;

public class MetricConfiguration {
	/** The Metric this Configuration belongs to */
	protected AbstractMetric parent;
	/** Logger for administrative operations */
    protected Logger log;
    /** Reference to the plugin administrator service, not to be passed to metric jobs */
    protected PluginAdmin pa;
    
    public MetricConfiguration( AbstractMetric parent, Logger log, PluginAdmin pa ){
    	this.parent = parent;
    	this.log = log;
    	this.pa = pa;
    }
    
    public Set<PluginConfiguration> getConfigurationSchema() {
        // Retrieve the plug-in's info object
        PluginInfo pi = pa.getPluginInfo(parent.getUniqueKey());
        if (pi == null) {
            // The plug-in's info object is always null during bundle startup,
            // but if it is not available when the bundle is active, 
        	// something is possibly wrong.
            if (parent.getState() == Bundle.ACTIVE) {
                log.warn("Plugin <" + parent.getName() + "> is loaded but not installed.");
            }
            return Collections.emptySet();
        }
        return pi.getConfiguration();
    }

    /**
     * Add an entry to this plug-in's configuration schema.
     *
     * @param db   A DBService
     * @param name The name of the configuration property
     * @param defValue The default value for the configuration property
     * @param msg The description of the configuration property
     * @param type The type of the configuration property
     */
    public void addConfigEntry(DBService db, String name, String defValue,
            String msg, ConfigurationType type) {
        // Retrieve the plug-in's info object
        PluginInfo pi = pa.getPluginInfo(parent.getUniqueKey());
        // Will happen if called during bundle's startup
        if (pi == null) {
            log.warn("Adding configuration key <" + name +
                "> to plugin <" + parent.getName() + "> failed: " +
                "no PluginInfo.");
            return;
        }
        // Modify the plug-in's configuration
        try {
            // Update property
        	Long propID = pi.getConfPropId(name, type);
            if (propID != null) {
            	PluginConfiguration prop = db.findObjectById(PluginConfiguration.class, propID);
            	prop.setValue(type, defValue);
                if (pi.updateConfigEntry(db, prop)) {
                    // Update the Plug-in Admin's information
                    pa.pluginUpdated(pa.getPlugin(pi));
                }
                else {
                    log.error("Property (" + name +") update has failed!");
                }
            }
            // Create property
            else {
            	PluginConfiguration prop = new PluginConfiguration();
            	prop.setMsg((msg != null) ? msg : "");
            	prop.setName(name);
            	prop.setValue(type,defValue);
                if (pi.addConfigEntry(db, prop)) {
                    // Update the Plug-in Admin's information
                    pa.pluginUpdated(pa.getPlugin(pi));
                }
                else {
                    log.error("Property (" + name +") append has failed!");
                }
            }
        }
        catch (Exception ex){
            log.error("Can not modify property (" + name +") for plugin ("
                    + parent.getName(), ex);
        }
    }

    /**
     * Remove an entry from the plug-in's configuration schema
     *
     * @param db   A DBService
     * @param name The name of the configuration property to remove
     * @param type The type of the configuration property to remove
     */
    public void removeConfigEntry( DBService db, String name, ConfigurationType type) {
        // Retrieve the plug-in's info object
        PluginInfo pi = pa.getPluginInfo(parent.getUniqueKey());
        // Will happen if called during bundle's startup
        if (pi == null) {
            log.warn("Removing configuration key <" + name +
                "> from plugin <" + parent.getName() + "> failed: " +
                "no PluginInfo.");
            return;
        }
        // Modify the plug-in's configuration
        try {
        	Long propID = pi.getConfPropId(name, type);
            if (propID != null) {
            	PluginConfiguration prop = db.findObjectById(PluginConfiguration.class, propID);
                if (pi.removeConfigEntry(db, prop)) {
                    // Update the Plug-in Admin's information
                    pa.pluginUpdated(pa.getPlugin(pi));
                }
                else {
                    log.error("Property (" + name +") remove has failed!");
                }
            }
            else {
                log.error("Property (" + name +") does not exist!");
            }
        }
        catch (Exception ex){
            log.error("Can not remove property (" + name +") from plugin ("
                    + parent.getName() + ")", ex);
        }
    }
    
    /**
     * Get a configuration option for this metric from the plugin configuration
     * store
     * 
     * @param config The configuration option to retrieve
     * @return The configuration entry corresponding the provided description or
     * null if not found in the plug-in's configuration schema
     */
    public PluginConfiguration getConfigurationOption(String config) {
        Set<PluginConfiguration> conf = 
            pa.getPluginInfo(parent.getUniqueKey()).getConfiguration();
        
        Iterator<PluginConfiguration> i = conf.iterator();
        while (i.hasNext()) {
            PluginConfiguration pc = i.next();
            if (pc.getName().equals(config)) {
                return pc;
            }
        }
        
        /* Config option not found */
        return null;
    }
}
