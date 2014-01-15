package eu.sqooss.service.db.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.MetricType;

public class MetricsUtils {
	private DBService dbs;
	
	public MetricsUtils(DBService db) {
		this.dbs = db;
	}

	/**
	 * Get a metric from its mnemonic name
	 * 
	 * @param mnem
	 *            - The metric mnemonic name to search for
	 * @return A Metric object or null when no metric can be found for the
	 *         provided mnemonic
	 */
	public Metric getMetricByMnemonic(String mnem) {
	
		Map<String, Object> properties = new HashMap<>();
		properties.put("mnemonic", mnem);
	
		List<Metric> result = dbs.findObjectsByProperties(Metric.class,
				properties);
	
		if (result.size() <= 0)
			return null;
	
		return result.get(0);
	}

	/**
	 * Get the corresponding DAO for the provided metric type.
	 * 
	 * @return A MetricType DAO representing the metric type
	 */
	public MetricType getMetricType(MetricType.Type t) {
	    HashMap<String, Object> s = new HashMap<>();
	    s.put("type", t.toString());
	    List<MetricType> result = dbs.findObjectsByProperties(MetricType.class, s);
	    if (result.isEmpty()) {
	        return null;
	    }
	    else {
	        return result.get(0);
	    }
	}

}
