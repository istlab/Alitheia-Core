package eu.sqooss.service.db.util;

import java.util.HashMap;
import java.util.List;

import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.PluginConfiguration;

public class PluginUtils {
	private DBService db;
	
	public PluginUtils(DBService db) {
		this.db = db;
	}

	public List<Plugin> getPluginByName(String name) {
	    HashMap<String, Object> s = new HashMap<>();
	    s.put("name", name);
	    return db.findObjectsByProperties(Plugin.class, s);
	}

	/**
	 * Get Plugin by hashcode
	 * 
	 * @param hashcode
	 *                The object's hashcode for the plugin class that implements
	 *                the
	 *                {@link eu.sqooss.service.abstractmetric.AlitheiaPlugin}
	 *                interface
	 * @return A Plugin object if the hashcode was found in the DB; null
	 *         otherwise
	 */
	public Plugin getPluginByHashcode(String hashcode) {
	    HashMap<String, Object> s = new HashMap<>();
	    s.put("hashcode", hashcode);
	    List<Plugin> l = db.findObjectsByProperties(Plugin.class, s); 
	    if (!l.isEmpty())
	        return l.get(0);
	    
	    return null;
	}

	/**
	 * Get a PluginConfiguration entry DAO or null in 
	 */
	public PluginConfiguration getConfigurationEntry(Plugin p, HashMap<String, Object> names) {
	    
	    names.put("plugin", p);
	            
	    List<PluginConfiguration> l = db.findObjectsByProperties(PluginConfiguration.class, names);
	    
	    if(l.isEmpty()) {
	        return null;
	    }
	    
	    return l.get(0);
	}

	/**
	 * Update a configuration entry. If the entry is found and updated 
	 * successfully true will be returned. If not found or the update 
	 * fails, false will be returned.
	 */
	public boolean updConfigurationEntry(Plugin p, HashMap<String, Object> names) {
	    PluginConfiguration pc = getConfigurationEntry(p, names);
	    
	    if (pc == null) {
	        return false;
	    }
	    
	    HashMap<String, Object> s = new HashMap<>();
	    
	    names.put("plugin", p);
	    
	    List<PluginConfiguration> l = db.findObjectsByProperties(PluginConfiguration.class, s);
	    
	    if (l.isEmpty()) {
	        return false;
	    }
	    
	    return true;
	}

}
