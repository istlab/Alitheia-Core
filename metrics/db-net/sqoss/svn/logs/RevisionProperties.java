package paxosk.sqoss.svn.logs;

import java.util.*;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.*;

/**
 * Every commit/log entry has a corresponding set of changed paths; every changed path is 
 * associated with a set of Properties. This class iterates each set of properties for each 
 * set of changed paths (on a given commit/log entry).
 *
 * log entry-----\----------------\
 *   \            \                \
 * changedPath1, changedPath2, changedPath3
 * {props_set1}  {props_set2}  {props_set2}
 *   |             /                /
 * Iterator-------/----------------/
 */
public class RevisionProperties 
{    
    //////////////////////////CONSTRUCTOR FIELDS/////////////////////////////////////	    
    //the log entry corresponding to this revision
    private SVNLogEntry entry_;
    
    //revision of current entry
    private long revision_=0;
    
    //the repository
    private SVNRepository repos_;
    
    //number of changed paths in this revision
    private int numofChangedPaths_;
    
    //the SVNLogEntryPath to String map; corresponding to the changed paths
    private HashMap<SVNLogEntryPath,String> hChangedPaths_;
    
    //the set of changed paths corresponding to an SVN log entry
    private Set<SVNLogEntryPath> allChangedPaths_;
    
    //iterator on all these changed paths; "SVNLogEntryPath" is a path, that has been 
    //affected by a commit
    private Iterator<SVNLogEntryPath> allChangedPathsIter_;
    //////////////////////////CONSTRUCTOR FIELDS/////////////////////////////////////	    

    /**
     * Constructor:
     * Gets the set of all changed paths for a given log entry and creates an iterator, 
     * a table which maps SVNLogEntryPath objects to the paths
     * @param entry the SVNLogEntry encapsulating the changed paths
     * @repos the SVNRepository
     */
    public RevisionProperties(SVNLogEntry entry, SVNRepository repos) 
    {        
        entry_=entry;     
        revision_=entry_.getRevision();
        repos_=repos;
        numofChangedPaths_=entry_.getChangedPaths().size();
        hChangedPaths_=(HashMap<SVNLogEntryPath,String>)entry_.getChangedPaths();
        allChangedPaths_=hChangedPaths_.keySet();
        allChangedPathsIter_=allChangedPaths_.iterator();
    }//RevisionProperties
    
    
    /**
     * Iterates through the changed paths of a given SVNLogEntry and returns the properties for
     * the next changed path.
     */
    public String getNextProperties()
    {
        Map properties=new HashMap();
        SVNLogEntryPath path = (SVNLogEntryPath) entry_.getChangedPaths().get(allChangedPathsIter_.next());
        String pathname=path.getPath();                        
            
        try 
        {
//            //why that? folders entries have changed paths too
//            //if this not a file return "" as properties
//            if (SVNNodeKind.FILE!=repos_.checkPath(pathname, entry_.getRevision())) {                    
//                return "";
//            }
            repos_.getFile(pathname, entry_.getRevision(), properties, null); 
        } 
        catch (SVNException ex) 
        {
            ex.printStackTrace();
        } 

        String s=properties.toString(); 
        properties.clear();

        return s;
    }//getProperties        
    
    
    /* does the path have more properties?
     */
    public boolean hasNextProperties()
    {
    	if (allChangedPathsIter_.hasNext()) {
    		return true;
    	}
    	
    	return false;    	
    }//hasNextProperties
    
  
    public long getCurrentRevision()
    {
        return revision_;
    }//getCurrentRevision
    
//    //sample usage
//    public static void main(String[] args)
//    {
//        RevisionProperties rp=new RevisionProperties(entry,repos) ;
//        
//        while (rp.hasNextProperties())
//        {
//            String props=rp.getNextProperties();            
//        }
//  }
    
}
