package eu.sqooss.impl.service.specs.metrics;

import java.util.ArrayList;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import eu.sqooss.impl.service.dsl.SpPlugin;
import eu.sqooss.impl.service.dsl.SpProject;
import eu.sqooss.impl.service.dsl.SpMetric;

@RunWith(ConcordionRunner.class)
public class MetricSelect
{

  public ArrayList<SpPlugin> getPlugins()
  {
    return SpPlugin.allPlugins();
  }
  
  public void addProject(String projectName)
  {
    new SpProject(projectName).create();
  }
  
  public void installPlugin(String pluginName)
  {
    SpPlugin sp = new SpPlugin(pluginName);
    sp.install();
  }
  
  public void synchPlugin(String pluginName, String projectName)
  {
     SpPlugin sp = new SpPlugin(pluginName);
     sp.synchPlugin(projectName);
  }
  
  public ArrayList<SpMetric> getMetrics(String projectName)
  {
    return SpMetric.getMetrics(projectName);
  }
}
