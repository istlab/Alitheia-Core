package eu.sqooss.plugin;

import eu.sqooss.db.Plugin;

import java.util.ArrayList;

/**
 * The PluginList class initializes the plugins 
 * from the database
 */
public class PluginList extends ArrayList {
    private static PluginList defaultInstance;
    
    static {
	defaultInstance = new PluginList();
    }
    
    private PluginList() {
	
    }
    
    // TODO: implement this function
    public boolean addPlugin(Plugin p) {
	return false;
    }
    
    public static PluginList getInstance() {
	return defaultInstance; 
    }
}
