package eu.sqooss.impl.service.dsl;

import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.impl.service.SpecsActivator;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.tds.SCMAccessor;
import eu.sqooss.service.tds.TDSService;

public class SpPlugin implements SpEntity {
    private DBService db = SpecsActivator.alitheiaCore.getDBService();

    long id = -1;
    boolean persistent = false;
    
    public String name;
    public boolean installed = false;
    
    public static ArrayList<SpPlugin> allPlugins() {
      PluginAdmin pa = SpecsActivator.alitheiaCore.getPluginAdmin();
        ArrayList<SpPlugin> result = new ArrayList<SpPlugin>();
        
        Collection<PluginInfo> plugins = pa.listPlugins();
        
          for (PluginInfo plugin : plugins) {
            result.add(new SpPlugin(plugin.getPluginName(), plugin.installed));
        }
        
        return result;
    }
    
    
    private SpPlugin(String n, boolean i) {
        name = n;
        installed = i;
        persistent = true;
    }
    
    public void load() {
    }

    public void create() {
    }

    public void delete() {
    }
    
}
