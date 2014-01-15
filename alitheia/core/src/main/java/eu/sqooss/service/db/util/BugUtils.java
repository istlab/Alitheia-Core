package eu.sqooss.service.db.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.service.db.Bug;
import eu.sqooss.service.db.BugPriority;
import eu.sqooss.service.db.BugReportMessage;
import eu.sqooss.service.db.BugResolution;
import eu.sqooss.service.db.BugSeverity;
import eu.sqooss.service.db.BugStatus;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;

public class BugUtils {

	private DBService dbs;
	
	public BugUtils(DBService db) {
		this.dbs = db;
	}

	/**
	 * Get the latest entry for the bug with the provided Id.
	 */
	public Bug getBugById(String bugID, StoredProject sp) {    
	    
	    String paramBugID = "paramBugID";
	    String paramStoredProject = "stroredProject";
	    
	    String query = "select b " +
	    	        "from Bug b " +
	    	        "where b.bugID = :" + paramBugID + 
	    	        " and b.project = :" + paramStoredProject +
	    	        " order by b.timestamp desc";
	    
	    Map<String, Object> params = new HashMap<>();
	    params.put(paramBugID, bugID);
	    params.put(paramStoredProject, sp);
	    
	    @SuppressWarnings("unchecked")
		List<Bug> bug = (List<Bug>) dbs.doHQL(query, params, 1);
	    
	    if (bug.isEmpty())
	        return null;
	    else 
	        return bug.get(0);
	}

	/**
	 * Get a list of all bug report comments for this specific bug,
	 * ordered by the time the comment was left (old to new).  
	 */
	@SuppressWarnings("unchecked")
	public List<BugReportMessage> getBugReportComments(Bug bug) {
	    
	    String paramBugID = "paramBugID";
	    String paramStoredProject = "storedProject";
	    
	    String query = "select brm " +
	    		"from Bug b, BugReportMessage brm " +
	    		"where brm.bug = b " +
	    		"and b.bugID = :" + paramBugID +
	    		" and b.project =:" + paramStoredProject +
	    		" order by brm.timestamp asc" ;
	    
	    Map<String, Object> params = new HashMap<>();
	    params.put(paramBugID, bug.getBugID());
	    params.put(paramStoredProject, bug.getProject());
	    
	    return (List<BugReportMessage>) dbs.doHQL(query, params);
	}

	/**
	 * Get the latest entry processed by the bug updater
	 */
	public Bug getLastBugUpdate(StoredProject sp) {
	
	    if (sp == null)
	        return null;
	    
	    String paramStoredProject = "storedProject";
	    
	    String query = " select b " +
	        " from Bug b, StoredProject sp" +
	        " where b.project=sp" +
	        " and sp = :" + paramStoredProject + 
	        " order by b.updateRun desc";
	    
	    Map<String, Object> params = new HashMap<>();
	    params.put(paramStoredProject, sp);
	    
	    @SuppressWarnings("unchecked")
		List<Bug> buglist = (List<Bug>) dbs.doHQL(query, params,1);
	    
	    if (buglist.isEmpty())
	        return null;
	    
	    return buglist.get(0);
	}

	/**
	 * Return or create and return the priority code DB representation 
	 * corresponding to the provided priority code
	 * @param s The priority code to check for
	 * @return A Bugpriority DAO or null if an error occurred while creating
	 * the priority code line to the database
	 */
	public BugPriority getBugPriority(BugPriority.Priority s) {
	    if (s == null)
	        return null;
	    return this.getBugPriority(s.toString(), true);
	}

	/**
	 * Return or create and return the priority code DB representation 
	 * corresponding to the provided String
	 * 
	 * @param priority The bug priority code representation 
	 * @param create If true, create a DB entry for the provided priority 
	 * code
	 * @return A BugPriority DAO or null when the DAO was not found 
	 * and the create field was set to null or when an error occurred
	 * while modifying the DB.
	 */
	public BugPriority getBugPriority(String priority, boolean create) {
	    
	    Map<String,Object> params = new HashMap<>();
	    params.put("priority", priority);
	    
	    List<BugPriority> st = dbs.findObjectsByProperties(BugPriority.class,
	            params);
	    
	    if (!st.isEmpty()) {
	        return st.get(0);
	    }
	    
	    if (!create) {
	        return null;
	    }
	    
	    if (BugPriority.Priority.fromString(priority) == null) {
	        return null;
	    }
	    
	    BugPriority bs = new BugPriority();
	    bs.setpriority(priority);
	    
	    if (!dbs.addRecord(bs))
	        return null;
	    
	    return bs;
	}

	/**
	 * Return or create and return the resolution code DB representation 
	 * corresponding to the provided resolution code
	 * @param s The resolution code to check for
	 * @return A BugResolution DAO or null if an error occurred while creating
	 * the resolution code line to the database
	 */
	public BugResolution getBugResolution(BugResolution.Resolution s) {
	    return this.getBugResolution(s.toString(), true);
	}

	/**
	 * Return or create and return the resolution code DB representation 
	 * corresponding to the provided String
	 * 
	 * @param resolution The bug resolution code representation 
	 * @param create If true, create a DB entry for the provided resolution 
	 * code
	 * @return A BugResolution DAO or null when the DAO was not found 
	 * and the create field was set to null or when an error occurred
	 * while modifying the DB.
	 */
	public BugResolution getBugResolution(String resolution, boolean create) {
		
		Map<String,Object> params = new HashMap<>();
		params.put("resolution", resolution);
	    
	    List<BugResolution> st = dbs.findObjectsByProperties(BugResolution.class,
	            params);
	    
	    if (!st.isEmpty()) {
	        return st.get(0);
	    }
	    
	    if (!create) {
	        return null;
	    }
	    
	    if (BugResolution.Resolution.fromString(resolution) == null) {
	        return null;
	    }
	    
	    BugResolution bs = new BugResolution();
	    bs.setResolution(resolution);
	    
	    if (!dbs.addRecord(bs))
	        return null;
	    
	    return bs;
	}

	/**
	 * Return or create and return the severity code DB representation 
	 * corresponding to the provided severity code
	 * @param s The severity code to check for
	 * @return A Bugseverity DAO or null if an error occurred while creating
	 * the severity code line to the database
	 */
	public BugSeverity getBugSeverity(BugSeverity.Severity s) {
	    return this.getBugSeverity(s.toString(), true);
	}

	/**
	 * Return or create and return the severity code DB representation 
	 * corresponding to the provided String
	 * 
	 * @param severity The bug severity code representation 
	 * @param create If true, create a DB entry for the provided severity 
	 * code
	 * @return A BugSeverity DAO or null when the DAO was not found 
	 * and the create field was set to null or when an error occurred
	 * while modifying the DB.
	 */
	public BugSeverity getBugSeverity(String severity, boolean create) {
	    
	    Map<String,Object> params = new HashMap<>();
	    params.put("severity", severity);
	    
	    List<BugSeverity> st = dbs.findObjectsByProperties(BugSeverity.class,
	            params);
	    
	    if (!st.isEmpty()) {
	        return st.get(0);
	    }
	    
	    if (!create) {
	        return null;
	    }
	    
	    if (BugSeverity.Severity.fromString(severity) == null) {
	        return null;
	    }
	    
	    BugSeverity bs = new BugSeverity();
	    bs.setSeverity(severity);
	    
	    if (!dbs.addRecord(bs))
	        return null;
	    
	    return bs;
	}

	/**
	 * Return or create and return the status code DB representation 
	 * corresponding to the provided status code
	 * @param s The status code to check for
	 * @return A BugStatus DAO or null if an error occurred while creating
	 * the status code line to the database
	 */
	public BugStatus getBugStatus(BugStatus.Status s) {
	    return this.getBugStatus(s.toString(), true);
	}

	/**
	 * Return or create and return the status code DB representation 
	 * corresponding to the provided String
	 * @param status The bug status code representation 
	 * @param create If true, create a DB entry for the  
	 * @return A BugStatus DAO or null when the DAO was not found 
	 * and the create field was set to null or when an error occurred
	 * while modifying the DB.
	 */
	public BugStatus getBugStatus(String status, boolean create) {
	    
	    Map<String,Object> params = new HashMap<>();
	    params.put("status", status);
	    
	    List<BugStatus> st = dbs.findObjectsByProperties(BugStatus.class,
	            params);
	    
	    if (!st.isEmpty()) {
	        return st.get(0);
	    }
	    
	    if (!create) {
	        return null;
	    }
	    
	    if (BugStatus.Status.fromString(status) == null) {
	        return null;
	    }
	    
	    BugStatus bs = new BugStatus();
	    bs.setStatus(status);
	    
	    if (!dbs.addRecord(bs))
	        return null;
	    
	    return bs;
	}

}
