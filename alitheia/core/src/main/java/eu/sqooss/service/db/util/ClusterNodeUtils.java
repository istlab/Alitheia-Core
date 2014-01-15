package eu.sqooss.service.db.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.DBService;

public class ClusterNodeUtils {

	private DBService dbs;
	
	public ClusterNodeUtils(DBService db) {
		this.dbs = db;
	}

	public ClusterNode getClusterNodeByName(String name) {
	    
	    Map<String,Object> parameterMap = new HashMap<>();
	    parameterMap.put("name",name);
	    List<ClusterNode> cnList = dbs.findObjectsByProperties(ClusterNode.class, parameterMap);
	    return (cnList == null || cnList.isEmpty()) ? null : cnList.get(0);
	}

}
