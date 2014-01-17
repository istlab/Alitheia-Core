package eu.sqooss.service.abstractmetric;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;
import eu.sqooss.service.db.Plugin;
import eu.sqooss.service.db.MetricType.Type;
import eu.sqooss.service.logging.Logger;

public class MetricInstallation {
	protected AbstractMetric metric;
	protected Logger log;
	protected DBService db;
	
	public MetricInstallation(AbstractMetric metric, Logger log, DBService db){
		this.metric = metric;
		this.log = log;
		this.db = db;
	}
	
    /**
     * Register the metric to the DB. Subclasses can run their custom
     * initialization routines (i.e. registering DAOs or tables) after calling
     * super().install()
     */
    public boolean install() {
        //1. check if dependencies are satisfied
        if (!metric.checkDependencies()) {
            log.error("Plug-in installation failed");
            return false;
        }
        
        HashMap<String, Object> h = new HashMap<String, Object>();
        h.put("name", metric.getName());

        List<Plugin> plugins = db.findObjectsByProperties(Plugin.class, h);

        if (!plugins.isEmpty()) {
            log.warn("A plugin with name <" + metric.getName()
                    + "> is already installed, won't re-install.");
            return false;
        }

        //2. Add the plug-in
        Plugin p = new Plugin();
        p.setName(metric.getName());
        p.setInstalldate(new Date(System.currentTimeMillis()));
        p.setVersion(metric.getVersion());
        p.setActive(true);
        p.setHashcode(metric.getUniqueKey());
        boolean result =  db.addRecord(p);
        
        //3. Add the metrics
		 for(Metric m : metric.getMetrics() ) {
	         Type type = Type.fromString(m.getMetricType().getType());
	         MetricType newType = MetricType.getMetricType(type);
	         if (newType == null) {
	        	 newType = new MetricType(type);
	             db.addRecord(newType);
	             m.setMetricType(newType);
	         }
	         m.setMetricType(newType);
	         m.setPlugin(p);
	         db.addRecord(m);
		}
        
        return result;
    }

    /**
     * Remove a plug-in's record from the DB. The DB's referential integrity
     * mechanisms are expected to automatically remove associated records.
     * Subclasses should also clean up any custom tables created.
     */
    public boolean remove() {
        Plugin p = Plugin.getPluginByHashcode(metric.getUniqueKey());
        return db.deleteRecord(p);
    }
    

    /**
     * Retrieve the installation date for this plug-in version
     */
    public final Date getDateInstalled() {
        return Plugin.getPluginByHashcode(metric.getUniqueKey()).getInstalldate();
    }
}
