package eu.sqooss.impl.service.specs.metrics;

import java.util.ArrayList;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import eu.sqooss.impl.service.dsl.SpPlugin;
import eu.sqooss.impl.service.dsl.SpPluginProperty;

@RunWith(ConcordionRunner.class)
public class MetricConfigure
{

  public ArrayList<SpPlugin> getPlugins()
  {
    return SpPlugin.allPlugins();
  }
  
  public void installPlugin(String pluginName)
  {
    SpPlugin sp = new SpPlugin(pluginName);
    sp.install();
  }
  
  public ArrayList<SpPluginProperty> getProperties(String pluginName)
  {
    SpPlugin sp = new SpPlugin(pluginName);
    return sp.properties();
  }
  
  public void changeProperty(String pluginName, String name, String value)
  {
    SpPlugin sp = new SpPlugin(pluginName);
    sp.changeProperty(name, value);
  }
  
  public String getPropertyValue(String pluginName, String name)
  {
    SpPlugin sp = new SpPlugin(pluginName);
    ArrayList<SpPluginProperty> properties = sp.properties();
    for (SpPluginProperty property: properties)
    {
      if (property.name.equals(name))
        return property.value;
    }
    return "";
  }
  
  public void addProperty(String pluginName, String name, String type, String value)
  {
    SpPlugin sp = new SpPlugin(pluginName);
    sp.addProperty(name, type, value);
  }
}
