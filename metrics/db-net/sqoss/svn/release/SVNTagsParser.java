package paxosk.sqoss.svn.release;

import java.util.*;
import java.io.*;
import java.sql.*;
import org.apache.commons.configuration.*;
import org.apache.log4j.*;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.*;
import paxosk.sqoss.svn.*;
import paxosk.date.common.*;
import paxosk.classes.common.*;
import paxosk.log.loggers.*;
import paxosk.sqoss.svn.common.*;

/**
 * Class, which extracts the releases -(Version,Stage) tuple- and
 * their corresponding dates. The repository dir structure must follow a 
 * standard structure; "/trunk, /branches, /tags". We will search inside
 * "base_url/tags/X" dir in order to exract the info. For example:
 * "svn://anonsvn.kde.org/home/kde/tags/KDE"
 *
 * Conventions:
 * "base url": url to the 0-level of the SVN repository
 * "version": string of the form "x.y.z", where x,y,z integers
 * "stage": the application internal namings, like "Alpha","RC1", etc
 * {@link initStageTable() initStageTable} for details
 *
 * Note:
 * If there is an already opened connection prefer to use it by using 
 * the {@link SVNTagsParser(String, SVNRepository) SVNTagsParser} constructor
 */
public class SVNTagsParser 
{
    //the repository object 
    private SVNRepository repository_=null;
    //the base url
    private String BASE_URL="";
    //the path to the tags 
    private String TAGS_DIR="";
    //the logger
    private Logger logger_=null;
    //the collection of Releases
    private ReleasePool pool_=new ReleasePool();    
    
    /**
     * Constructor:
     * gets props, connects us to the given base url anonymously
     */ 
    public SVNTagsParser()
    {            
        getPropsInitLogger();                
    	SVNConnection con=new SVNConnection();
    	repository_=con.openConnection(BASE_URL);        
        start(TAGS_DIR);  //debug
    }//SVNLogParser	
    
    public void init()
    {
        
    }//init
       
    ////////////////////////////////////////////////////HELPERS/////////////////////////////////////////////////
    /**
     * Reads the tag directory and returns an UNORDERED array;
     * col1: version, col2: stage, col3:timestamp
     * @param path the path to the tags dir, not including the base URL
     * @return String a 2D array with columns (1)version, (2)stage, (3)timestamp
     */
    private void start(String path) 
    {  
        logger_.debug("Will now START reading releases from tags directory: "+path);    	
    	try
    	{    		
            Vector<SVNDirEntry> vEntries=new Vector<SVNDirEntry>();
            Collection<SVNDirEntry> entries = repository_.getDir(path,-1, null,vEntries);
            for (SVNDirEntry entry: vEntries)
            {
                //if (entry.getName().equals("2.2")) //debug
                new Release(entry.getName(),
                        new Timestamp(entry.getDate().getTime()),
                            pool_
                                );
            }
       	}
    	catch (SVNException e)
    	{
            System.err.println("error while reading repo dir: " + e.getMessage());
            System.exit(-1);    		
    	}
        
        logger_.debug("FINISHED reading releases from tags directory: "+path);
    }//listEntries       
    
    
    /**
     * Reads the properties required
     */
    private void getPropsInitLogger()
    {
        PropertiesConfiguration props_=null;
        try {
            props_=new PropertiesConfiguration("resources/properties/current.properties");
        }
        catch(Exception e) {
            System.err.println("failed to parser properties file... exiting\n"+
                    MyClassUtils.getStackTraceTabbed(e));
            System.exit(-1);
        }              
       
        //get the logging properties and initialize a logger
        Log4jInstantiator logInst=new Log4jInstantiator();
        logger_=logInst.initFileAppenderLogger((String)props_.getString("LOG_SVN_FILE"));            
        
        TAGS_DIR=(String)props_.getString("SVN_TAGS_DIR");        
        BASE_URL=(String)props_.getString("SVN_BASE_URL");
    }//getProps
    
    
    public List<String> getSOfficialReleaseList()
    {
        Vector<String> vReleasesOfficial=new Vector<String>();
                
        Iterator<Release> i=pool_.iterator();
        while (i.hasNext())
        {
            vReleasesOfficial.add(i.next().toString());
        }
        
        return vReleasesOfficial;
    }//getSOfficialReleaseList
    
    
    public List<String> getSUnofficialReleaseList()
    {
        Vector<String> vReleasesUnofficial=new Vector<String>();
                
        Iterator<Release> i=pool_.iterator();
        while (i.hasNext())
        {
            Release r=i.next();
            vReleasesUnofficial.add(r.getVersionOrig()+r.getStageOrig());
        }
        
        return vReleasesUnofficial;        
    }//getSUnofficialReleaseList
    
    
    public List<Release> getReleaseList()
    {
        return pool_.getList();
    }//getReleaseList
    ////////////////////////////////////////////////////HELPERS/////////////////////////////////////////////////    

//    //sample usage
//    public static void main(String[] args)
//    {
//        SVNTagsParser p=new SVNTagsParser();
//        p.init();
//        System.out.println("Printing the official names release list\n"+p.getSOfficialReleaseList());
//        System.out.println("Printing the unofficial names release list\n"+p.getSUnofficialReleaseList());        
//    }//main
}
