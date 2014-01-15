package eu.sqooss.service.db.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.service.db.ConfigurationOption;
import eu.sqooss.service.db.DBService;

public class ConfigurationOptionUtils {

	private DBService dbs;

	public ConfigurationOptionUtils(DBService db) {
		this.dbs = db;
	}
	
	public ConfigurationOption getConfigurationOptionByKey(String key) {
		
		String paramKey = "key";
		
		Map<String, Object> params = new HashMap<>();
		params.put(paramKey, key);
		
		List<ConfigurationOption> opts =  dbs.findObjectsByProperties(ConfigurationOption.class, params);
		
		if (opts.isEmpty())
			return null;
		
		return opts.get(0);
	}

}
