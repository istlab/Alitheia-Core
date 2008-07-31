package eu.sqooss.impl.service.dsl;

import eu.sqooss.impl.service.dsl.SpPluginProperty;

import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.lang.Exception;
import java.lang.Long;

import eu.sqooss.service.pa.PluginAdmin;
import eu.sqooss.service.pa.PluginInfo;
import eu.sqooss.impl.service.SpecsActivator;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.tds.SCMAccessor;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.db.PluginConfiguration;
import eu.sqooss.service.db.StoredProjectMeasurement;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.Metric;

public class SpMetric implements SpEntity {

    long id = -1;
    
    public String name;
    public boolean selected = false;
    
    public static ArrayList<SpMetric> getMetrics(String projectName) {
      DBService db = SpecsActivator.alitheiaCore.getDBService();
      
      ArrayList<SpMetric> result = new ArrayList<SpMetric>();
      db.startDBSession();
      HashMap<String,Object> properties = new HashMap<String,Object>();
      properties.put("name", projectName);
      List<StoredProject> projects = db.findObjectsByProperties(StoredProject.class, properties);
      StoredProject project = projects.get(0);
      
      Set<StoredProjectMeasurement> metrics = project.getMeasurements();
      TreeSet<Long> ids = new TreeSet<Long>();
        
      System.out.printf("found metrics %s\n", project.getName());
      for (StoredProjectMeasurement m : metrics) {
        System.out.printf("found metric: |%s|\n",m.getMetric().getDescription());
        ids.add(m.getId());
      }
      for (Long id: ids)
      {
        for (StoredProjectMeasurement m : metrics) {
          if (id.equals(m.getId()))
            result.add(new SpMetric(m.getMetric().getDescription(), true));
        }
      }
            
        
      db.commitDBSession();

       return result;
    }
    
    
    private SpMetric(String n, boolean i) {
        name = n;
        selected = i;
    }
    
    public SpMetric(String n) {
      name = n;
      selected  = false;
    }
    
    public void load() {
    }
    
    public void create() {
    }

    public void delete() {
    }
    
}
