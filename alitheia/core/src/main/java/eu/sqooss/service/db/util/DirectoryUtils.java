package eu.sqooss.service.db.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Directory;

public class DirectoryUtils {
	private DBService dbs;
	
	public DirectoryUtils(DBService db) {
		this.dbs = db;
	}

	/**
	 * Return the entry in the Directory table that corresponds to the
	 * passed argument. If the entry does not exist, it will optionally be 
	 * created and saved, depending on the second parameter
	 *  
	 * @param path The path of the Directory to search for
	 * @param create Whether or not the directory entry will be created if
	 * not found. If true, it will be created.
	 * @return A Directory record for the specified path or null on failure
	 */
	public synchronized Directory getDirectoryByPath(String path, boolean create) {
	    
	    Map<String,Object> parameterMap = new HashMap<>();
	    parameterMap.put("path", path);
	    
	    List<Directory> dirs = dbs.findObjectsByProperties(Directory.class,
	            parameterMap);
	    
	    /* Dir path in table, return it */
	    if ( !dirs.isEmpty() ) {
	        return dirs.get(0);
	    }
	    
	    if (create) {
	        /* Dir path not in table, create it */ 
	        Directory d = new Directory();
	        d.setPath(path);
	        if (!dbs.addRecord(d)) {
	            return null;
	        }
	    
	        return d;
	    }
	    //Dir not found and not created
	    return null;
	}

}
