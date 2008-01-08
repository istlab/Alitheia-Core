package paxosk.sqoss.svn;

import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.*;
import paxosk.sqoss.svn.activity.dbupdate.*;
import paxosk.log.loggers.*;
import paxosk.sql.common.*;
import org.apache.log4j.*;
import org.apache.commons.configuration.*;
import paxosk.sqoss.svn.release.*;
import paxosk.sqoss.svn.logs.*;
import paxosk.sqoss.svn.common.*;
import paxosk.classes.common.*;
import java.io.*;
import java.util.*;
import java.sql.*;

/**
 * Performs all SVN processing; the main loop reads all the tag/release directories.
 * For each one of the releases
 *      -calls the handler for updating the DB with the release
 *      -enters the corresponding tags dir; lists logs
 *          -calls the handler for updating the DB with each log
 *      -calls the handler for updating the DB with the statistics 
 *       extracted from the logs made for the release
 *
 * See description of the corresponding handlers
 */
public class SVNParser
{    
    //the SVN repository
    private SVNRepository repository_=null;
    //the head revision
    private long headRevision_=-1;
    //the handler for each log entry
    private DBUpdateLogEntryHandler logHandler_=null;
    //the handler for each release tag entry
    private DBUpdateRelease tagHandler_=null;
    //parses the releases
    private SVNTagsParser tagsParser_=null;
    //used for calculating the activity for a given release
    private ReleaseActivity releaseActivity_=null;
    //sql connection
    private Connection con_=null;
    //base url to SVN repo
    private String BASE_URL="";
    //url to tags dir
    private String TAGS_DIR="";
    //sql connection properties
    private String SQL_PORT, SQL_DB, SQL_USER, SQL_PASS="";
    //private int PROJECT_ID
    private int PROJECT_ID=0;
    //properties
    private PropertiesConfiguration props_=null;
    //the logger
    private Logger logger_=null;    
    
    
    ////////////////////////////////CONSTRUCTORS/////////////////////////////////////	
    /**
     * Constructor:
     * reads propertis, inits logger, initializes a new anonymous connection to 
     * the repository and gets the  head revision
     */
    public SVNParser()
    {   
        //init logger and properties
        getPropsInitLogger();        
        
        //read properties
        BASE_URL=props_.getString("SVN_BASE_URL");
        TAGS_DIR=props_.getString("SVN_TAGS_DIR");
        SQL_PORT=props_.getString("SQL_PORT");
        SQL_DB=props_.getString("SQL_DB");
        SQL_USER=props_.getString("SQL_USER");
        SQL_PASS=props_.getString("SQL_PASS");
        PROJECT_ID=props_.getInt("PROJECT_ID");
        
        //initialize SVN connection
    	SVNConnection con=new SVNConnection();
    	repository_=con.openConnection(BASE_URL);  //open connection to base SVN url
    	headRevision_=con.getHeadRevision(); //read HEAD                
        
        //parse the tags        
        tagsParser_=new SVNTagsParser();        
        
        //initialize SQL connection
        con_=SQLUtils.initSQLConnection(SQL_PORT,SQL_DB, SQL_USER,SQL_PASS);

        //initialize the handlers
        logHandler_=new DBUpdateLogEntryHandler(con_,logger_,props_);
        tagHandler_=new DBUpdateRelease(con_,logger_,props_);
    }//SVNLogParser	
    ////////////////////////////////CONSTRUCTORS/////////////////////////////////////	
       
    
    /**
     * Execute  all beginning and ending operations
     */
    public void exec()
    {
        start();
        end();
    }//exec
    
    
    /**
     * Process all the releases (update the corresponding table, etc)
     * and recursively process all the logs made for the specific releases
     * (updateh the corresponding table, etc)
     */
    public void start()
    {
//        //for each release update the database with the release
//        for (Release r:tagsParser_.getReleaseList()) {                                           
//            tagHandler_.handleTagEntry(r); 
//        }
        
        HashSet<String> sTags=new HashSet<String>();
        sTags.add("1.1"); sTags.add("1.1.1"); sTags.add("1.1.2"); sTags.add("2"); sTags.add("2.0");
        sTags.add("2.0.1"); sTags.add("2.1"); sTags.add("2.1.1"); sTags.add("2.1.2");
        sTags.add("2.2"); sTags.add("2.2.1"); sTags.add("2.2.2"); sTags.add("3"); sTags.add("3.0");
        sTags.add("3.0.1"); sTags.add("3.0.2"); sTags.add("3.0.3"); sTags.add("3.0.4");
        sTags.add("3.0.5A"); sTags.add("3.1");
        
        //for each release
        //set the log handler release identifier to the current release and 
        //recurse into the subdirectories of the current release/tag dir        
        for (Release r:tagsParser_.getReleaseList())
        {                                     
            if (!sTags.contains(r.toStringOrig()))
            {
            logHandler_.setCurrentRelease(r);                         
            start(TAGS_DIR+r.toStringOrig()); 
            }
        }
    }//start
    
    
    /**
     *
     */
    private void start(String dir)
    {        
        try 
        {                
            System.err.println("SVNParser: "+dir);
            repository_.log(new String[]{(dir)},-1, 0, false, false, 0, logHandler_);                  
             
            //read the subdirs of the current directory
            Collection<SVNDirEntry> entries = repository_.getDir(dir, -1, null,(Collection) null);
            
            for (SVNDirEntry entry: entries)
            {
                if (entry.getKind()==SVNNodeKind.DIR)
                {
                    String nextLevelDir=dir.concat("/").concat(entry.getName());                    
                    logger_.debug("Reading dir: "+nextLevelDir);				    	
                    start(nextLevelDir);
                }
            }            
        }
        catch(SVNException e)
        {
                System.err.println(e.getMessage());
        }			        
    }//start

    
    private void end()
    {
        releaseActivity_=new ReleaseActivity(con_,logger_,props_);
        releaseActivity_.calcActivity();
    }//end
    
    
    ////////////////////////////////////////////////HELPERS//////////////////////////////////////////////////	        
    /**
     * Reads the properties and initializes the logger; used by the
     * constructors    
     */
    private void getPropsInitLogger()
    {        
        try {
            props_=new PropertiesConfiguration("resources/properties/current.properties");
        }
        catch(Exception e) {
            System.err.println("failed to parse properties file... exiting\n"+
                    MyClassUtils.getStackTraceTabbed(e));
            System.exit(-1);
        }        

        //get the logging properties and initialize a logger
        Log4jInstantiator logInst=new Log4jInstantiator();
        logger_=logInst.initFileAppenderLogger((String)props_.getString("LOG_SVN_FILE"));          
    }//getPropsInitLogger
    ////////////////////////////////////////////////HELPERS//////////////////////////////////////////////////
		
	
//    //sample usage
//    public static void main(String[] args)
//    {
//        SVNParser sd=new SVNParser();
//        sd.end();
//    }//main
}
