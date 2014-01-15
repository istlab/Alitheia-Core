package eu.sqooss.service.db.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.service.db.Branch;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;

public class BranchUtils {
	private DBService db;

	public BranchUtils(DBService db) {
		this.db = db;
	}

	public Branch getBranchByName(StoredProject sp, String name, boolean create) {
		Map<String, Object> params = new HashMap<>();
		
		params.put("name", name);
		params.put("project", sp);
		
		@SuppressWarnings("unchecked")
		List<Branch> branches = (List<Branch>)db.doHQL("from Branch b where b.name = :name and b.project = :project", params);
		if (branches.isEmpty()) {
		    if (!create)
		        return null;
		    Branch b = new Branch();
		    b.setProject(sp);
		    b.setName(name);
		    db.addRecord(b);
		    return getBranchByName(sp, name, false);
		}
		
		return branches.get(0);
	}

	public String suggestBranchName(StoredProject sp) {
	
	    Map<String, Object> params = new HashMap<>();
	    params.put("project", sp);
	
	    @SuppressWarnings("unchecked")
		List<Long> ids = (List<Long>) db.doHQL("select count(b) from Branch b where b.project = :project", params);
	    if (ids.isEmpty())
	        return "1";
	    else
	        return String.valueOf(ids.get(0) + 1);
	}

}
