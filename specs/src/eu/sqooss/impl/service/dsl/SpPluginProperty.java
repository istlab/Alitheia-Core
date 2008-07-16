package eu.sqooss.impl.service.dsl;

import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.impl.service.SpecsActivator;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.tds.SCMAccessor;
import eu.sqooss.service.tds.TDSService;

public class SpPluginProperty {
  public String name;
  public String type;
  public String value;
  
  public SpPluginProperty(String n, String t, String v)
  {
    name = n;
    type = t;
    value = v;
  }
}
