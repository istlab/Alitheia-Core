package eu.sqooss.service.abstractmetric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.sqooss.service.db.DAObject;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.MetricType.Type;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.pa.PluginAdmin;

public class MetricCollection {
	/** The Metric this Collection belongs to */
	protected AbstractMetric parent;
	/** Logger for administrative operations */
    protected Logger log;
    /** Set of declared metrics indexed by their mnemonic*/
    protected Map<String, Metric> metrics = new HashMap<String, Metric>();    
    /** Metric mnemonics for the metrics required to be present for this metric to operate */
    protected Set<String> dependencies = new HashSet<String>();
    /** The list of this plug-in's activators*/
    protected Set<Class<? extends DAObject>> activators = 
    		new HashSet<Class<? extends DAObject>>();
    /** DOC */
    protected Map<Metric, List<Class<? extends DAObject>>> metricActType =
    		new HashMap<Metric, List<Class<? extends DAObject>>>();
	
	public MetricCollection( AbstractMetric parent, Logger log ){
		this.parent = parent;
		this.log = log;
	}
	
	public void discoverMetrics(MetricDeclarations md){
		if (md != null && md.metrics().length > 0) {
			for (MetricDecl metric : md.metrics()) {
				log.debug("Found metric: " + metric.mnemonic() + " with "
						+ metric.activators().length + " activators");

				if (metrics.containsKey(metric.mnemonic())) {
				    log.error("Duplicate metric mnemonic " + metric.mnemonic());
				    continue;
				}
				
				Metric m = new Metric();
				m.setDescription(metric.descr());
				m.setMnemonic(metric.mnemonic());
				m.setMetricType(new MetricType(MetricType.fromActivator(metric.activators()[0])));
			
				List<Class<? extends DAObject>> activs = new ArrayList<Class<? extends DAObject>>();				
				for (Class<? extends DAObject> o : metric.activators()) {
					activs.add(o);
				}
				
				metricActType.put(m, activs);
				
				activators.addAll(Arrays.asList(metric.activators()));
				
				metrics.put(m.getMnemonic(), m);
				if (metric.dependencies().length > 0)
					dependencies.addAll(Arrays.asList(metric.dependencies()));
			}
		} else {
			log.warn("Plug-in " + parent.getName() + " declares no metrics");
		}
     }
	
	public boolean contains( Metric metric ) {
		return metrics.containsKey(metric.getMnemonic());
	}
	
	public Collection<Metric> getMetrics() {
		return metrics.values();
	}
	
	
    /**
     * Check if the plug-in dependencies are satisfied
     */
    public boolean checkDependencies( PluginAdmin pa ) {
        for (String mnemonic : dependencies) {
        	//Check thyself first
        	if (metrics.containsKey(mnemonic))
        		continue;
        	
            if (pa.getImplementingPlugin(mnemonic) == null) {
                log.error("No plug-in implements metric "  + mnemonic + 
                        " which is required by " + parent.getName());
                return false;
            }
        }
        return true;
    }
    
    public List<Metric> getSupportedMetrics(Class<? extends DAObject> activator) {
        List<Metric> m = new ArrayList<Metric>();

        //Query the database just once
        List<Metric> all = parent.getAllSupportedMetrics();
        
        if (all == null || all.isEmpty())
            return m;
        
        for (Metric metric : all) {
            if (getActivationTypes(metric).contains(activator)) {
                m.add(metric);
            }
        }
        
        return m;
    }
	
	public Set<Class<? extends DAObject>> getActivationTypes() {    
        return activators;
    }
	
	public List<Class<? extends DAObject>> getActivationTypes(Metric m) {
        return metricActType.get(m);
    }
	
	public Set<String> getDependencies() {
		return dependencies;
	}
}
