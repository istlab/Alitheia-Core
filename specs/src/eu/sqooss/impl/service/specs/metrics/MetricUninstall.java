package eu.sqooss.impl.service.specs.metrics;

import java.util.ArrayList;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import eu.sqooss.impl.service.dsl.SpPlugin;

@RunWith(ConcordionRunner.class)
public class MetricUninstall
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
  
  public void unInstallPlugin(String pluginName)
  {
    SpPlugin sp = new SpPlugin(pluginName);
    sp.uninstall();
  }
}
