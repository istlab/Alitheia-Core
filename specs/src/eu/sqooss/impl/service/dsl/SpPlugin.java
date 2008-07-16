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
    public String hash;
    
    public static ArrayList<SpPlugin> allPlugins() {
      PluginAdmin pa = SpecsActivator.alitheiaCore.getPluginAdmin();
        ArrayList<SpPlugin> result = new ArrayList<SpPlugin>();
        
        Collection<PluginInfo> plugins = pa.listPlugins();
        
          for (PluginInfo plugin : plugins) {
            result.add(new SpPlugin(plugin.getPluginName(), plugin.installed, plugin.getHashcode()));
        }
        
        return result;
    }
    
    
    private SpPlugin(String n, boolean i, String h) {
        name = n;
        installed = i;
        hash = h;
        persistent = true;
    }
    
    public SpPlugin(String n) {
      name = n;
      persistent = true;
      load();
    }
    
    public void load() {
      System.out.printf("search plugin: |%s|\n",name);
      ArrayList<SpPlugin> plugins = allPlugins();
      for (SpPlugin plugin: plugins)
      {
        System.out.printf("found plugin: |%s|\n",plugin.name);
        if (plugin.name.equals(name))
        {
          installed = plugin.installed;
          hash = plugin.hash;
          System.out.printf("found plugin hash: %s %s\n",name,hash);
          return;
        }
      }      
    }
    
    public void install()
    {
      DBService db = SpecsActivator.alitheiaCore.getDBService();
      db.startDBSession();
      PluginAdmin pa = SpecsActivator.alitheiaCore.getPluginAdmin();
      pa.installPlugin(hash);
      db.commitDBSession();
    }

    public void uninstall()
    {
      DBService db = SpecsActivator.alitheiaCore.getDBService();
      db.startDBSession();
      PluginAdmin pa = SpecsActivator.alitheiaCore.getPluginAdmin();
      pa.uninstallPlugin(hash);
      db.commitDBSession();
    }
    
    public void create() {
    }

    public void delete() {
    }
    
}
