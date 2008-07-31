package eu.sqooss.impl.service.dsl;

import eu.sqooss.impl.service.dsl.SpPluginProperty;

import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.lang.Exception;

import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.impl.service.SpecsActivator;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.tds.SCMAccessor;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.abstractmetric.AlitheiaPlugin;
import eu.sqooss.service.metricactivator.MetricActivator;
import eu.sqooss.impl.service.metricactivator.MetricActivatorImpl;
import eu.sqooss.service.db.MetricType.Type;

public class SpPlugin implements SpEntity {
    private DBService db = SpecsActivator.alitheiaCore.getDBService();

    long id = -1;
    boolean persistent = false;
    
    public String name;
    public boolean installed = false;
    public String hash;
    PluginInfo info;
    
    public static ArrayList<SpPlugin> allPlugins() {
      PluginAdmin pa = SpecsActivator.alitheiaCore.getPluginAdmin();
        ArrayList<SpPlugin> result = new ArrayList<SpPlugin>();
        
        Collection<PluginInfo> plugins = pa.listPlugins();
        TreeSet<String> names = new TreeSet<String>();
        
        for (PluginInfo plugin : plugins) {
            names.add(plugin.getPluginName());
        }
        for (String n: names)
        {
          for (PluginInfo plugin : plugins) {
            if (plugin.getPluginName().equals(n))
              result.add(new SpPlugin(plugin.getPluginName(), plugin.installed, plugin.getHashcode(), plugin));
          }
        }
        
        return result;
    }
    
    
    private SpPlugin(String n, boolean i, String h, PluginInfo inf) {
        name = n;
        installed = i;
        hash = h;
        info = inf;
        persistent = true;
    }
    
    public SpPlugin(String n) {
      name = n;
      persistent = true;
      load();
    }
    
    public void load() {
      //System.out.printf("search plugin: |%s|\n",name);
      ArrayList<SpPlugin> plugins = allPlugins();
      for (SpPlugin plugin: plugins)
      {
        //System.out.printf("found plugin: |%s|\n",plugin.name);
        if (plugin.name.equals(name))
        {
          installed = plugin.installed;
          hash = plugin.hash;
          //System.out.printf("found plugin hash: %s %s\n",name,hash);
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
    
    public ArrayList<SpPluginProperty> properties()
    {
      PluginAdmin pa = SpecsActivator.alitheiaCore.getPluginAdmin();
      ArrayList<SpPluginProperty> result = new ArrayList<SpPluginProperty>();
      
      TreeSet<String> propertyNames = new TreeSet<String>();
            
      Collection<PluginInfo> plugins = pa.listPlugins();
        
      for (PluginInfo plugin : plugins) {
        if (plugin.getPluginName().equals(name))
        {
          Set<PluginConfiguration>  configlist = plugin.getConfiguration();
          for (PluginConfiguration config: configlist)
          {
            propertyNames.add(config.getName());
          }
          for (String pName: propertyNames)
          {
            for (PluginConfiguration config: configlist)
            {
              if (config.getName().equals(pName))
              {
                result.add(new SpPluginProperty(config.getName(), config.getType(), config.getValue()));      
              }
            }            
          }
        }
      }
      return result;
    }
    
    public void changeProperty(String propertyName, String propertyValue)
    {
      PluginAdmin pa = SpecsActivator.alitheiaCore.getPluginAdmin();
      ArrayList<SpPluginProperty> result = new ArrayList<SpPluginProperty>();
      
      Collection<PluginInfo> plugins = pa.listPlugins();
        
      for (PluginInfo plugin : plugins) {
        if (plugin.getPluginName().equals(name))
        {          
          Set<PluginConfiguration>  configlist = plugin.getConfiguration();
          for (PluginConfiguration config: configlist)
          {
            if (config.getName().equals(propertyName))
            {
              DBService db = SpecsActivator.alitheiaCore.getDBService();
              db.startDBSession();
              //config.setValue(propertyValue); 
                //Fails, but I don't know why... Now the new value is not stored in the database./
              
               try 
               {
                 plugin.updateConfigEntry(db, propertyName, propertyValue);
               }
               catch (Exception e)
               {
                 System.out.printf("Error in modifying a property! %s\n", e.getMessage());
               }
              pa.pluginUpdated(pa.getPlugin(plugin));
              db.commitDBSession();
             return;
            }
          }
        }
      }
    }
    
    public void addProperty(String propertyName, String propertyType, String propertyValue)
    {
      PluginAdmin pa = SpecsActivator.alitheiaCore.getPluginAdmin();
      ArrayList<SpPluginProperty> result = new ArrayList<SpPluginProperty>();
      
      Collection<PluginInfo> plugins = pa.listPlugins();
        
      for (PluginInfo plugin : plugins) {
        if (plugin.getPluginName().equals(name))
        {          
          DBService db = SpecsActivator.alitheiaCore.getDBService();
          db.startDBSession();
          try 
          {
            System.out.printf("Adding a new property: %s %s %s\n", propertyName, propertyType, propertyValue);
            plugin.addConfigEntry(db, propertyName, "User added property", propertyType, propertyValue);
            pa.pluginUpdated(pa.getPlugin(plugin));
          } 
          catch (Exception e)
          {
            System.out.printf("Error in adding a new property! %s\n", e.getMessage());
          }
          db.commitDBSession();
          return;
        }
      }
    }
    
    public void synchPlugin()
    {
      PluginAdmin pa = SpecsActivator.alitheiaCore.getPluginAdmin();
      AlitheiaPlugin p = pa.getPlugin(info);
      MetricActivator ma = SpecsActivator.alitheiaCore.getMetricActivator();
      ma.initRules();
      ma.syncMetrics(p);
    }
}
