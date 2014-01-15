package eu.sqooss.service.db.util;

import java.util.HashMap;
import java.util.List;

import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.NameSpace;
import eu.sqooss.service.db.ProjectVersion;

public class NameSpaceUtils {
	private DBService dbs;
	
	public NameSpaceUtils(DBService db) {
		this.dbs = db;
	}
	
	public NameSpace getNameSpaceByVersionName(ProjectVersion pv, String name) {
	    HashMap<String, Object> params = new HashMap<>();
	    params.put("pv", pv);
	    params.put("name", name);
	    
	    @SuppressWarnings("unchecked")
		List<NameSpace> ns = (List<NameSpace>) dbs.doHQL("from NameSpace ns where ns.changeVersion = :pv and ns.name = :name", params);
	    
	    if (ns.isEmpty())
	        return null;
	    return ns.get(0);
	}

}
