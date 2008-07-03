package eu.sqooss.impl.service.specs.metrics;

import java.util.ArrayList;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import eu.sqooss.impl.service.dsl.SpPlugin;

@RunWith(ConcordionRunner.class)
public class MetricInstall
{

public ArrayList<SpPlugin> getPlugins()
  {
  return SpPlugin.allPlugins();
  }
  }
