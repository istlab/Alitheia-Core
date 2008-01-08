package paxosk.sqoss.svn.logs;

import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.*;
import org.apache.log4j.*;
import org.apache.commons.configuration.*;
import paxosk.classes.common.*;
import paxosk.schedule.lru.*;
import paxosk.log.loggers.*;
import paxosk.date.common.*;
import paxosk.string.common.*;
import paxosk.sqoss.db.queries.*;
import paxosk.sqoss.svn.common.*;
import java.util.*;
import java.io.*;
import java.sql.*;


public class DBUpdateLogEntryHandler  implements ISVNLogEntryHandler
{    
    //id of the project; used in the query
    private String PROJECT_ID="";    
    //SQL connection
    private Connection con_=null;    
    //INSERT statement for updating the commit table
    private PreparedStatement psUPDATE_COMMIT_TABLE_=null;    
    //keeps a set of helpful utility queries
    private DBQueries variousQueries_=null;
    //log4j logger
    private Logger logger_=null;            
    //the current release the logsof which we are processing
    private Release vs_=null;
    //the props
    private PropertiesConfiguration props_=null;
    //utility class for extracting stats about the types of paths, files
    private LogPaths logPaths_=null;
    //is this a "scripty" commit; will not be inserted in the DB
    private boolean isScript_=false; 
    //is this a "duplicate" commit; will not be inserted in the DB
    private boolean isDuplicate_=false;
    //a pool, that keeps the IDs of the commits and removes them
    //in a "Last Recently Used" manner    
    private HashSet pool_; //private LastRUPool pool_=new LastRUPool(50000);
    //a thread watching the status of the pool
    private PoolWatcher poolWatcher_;
    
    public DBUpdateLogEntryHandler(Connection con, Logger logger, PropertiesConfiguration props)  
    {
        con_=con;        
        variousQueries_=new DBQueries(con,logger,props);
        logger_=logger;        
        
        //get the props        
        props_=props;
        PROJECT_ID=props_.getString("PROJECT_ID");
                
        //and then prepare the statement
        try {
            psUPDATE_COMMIT_TABLE_=con_.prepareStatement(props_.getString("UPDATE_COMMIT_TABLE"));
            
            //this will be done with the mySQL addon "ON DUPLICATE UPDATE dupCounter=dupCounter+1" directive
            //psUPDATE_DUPCOUNTER_COMMIT_=con_.prepareStatement(props_.getString("UPDATE_DUPCOUNTER_COMMIT"));            
        }
        catch(SQLException e) {
            logger_.fatal(MyClassUtils.getStackTrace(e));
            System.exit(-1);
        }                      
                
        logPaths_=new LogPaths();
        pool_=new MyHashPool(con_);
        poolWatcher_=new PoolWatcher(pool_);
        new Thread(poolWatcher_).start();
    }//MyISVNLogEntryHandler
    
    
    
    public void handleLogEntry(SVNLogEntry logEntry)
    {
        long revision=logEntry.getRevision();
        if (!pool_.contains(revision))
        {
            pool_.add(revision);
        }
        else if (pool_.contains(revision))
        {
            isDuplicate_=true;
            return;
        }
        
        long projid=Long.valueOf(PROJECT_ID);
        
        java.util.Date date=logEntry.getDate();                
        Timestamp tstamp=new Timestamp(date.getYear(),date.getMonth(),date.getDate(),
                date.getHours(),date.getMinutes(),date.getSeconds(),0);           

        long commiterid=variousQueries_.getCommiterIDFromName(logEntry.getAuthor());
        
        logger_.debug("Now handling release revision number: "+revision);
        if (commiterid==0) {
            logger_.debug("\tthis is a script commit");
            isScript_=true;
        }
        
        String version=vs_.getVersion();
        String stage=vs_.getStage();
        String msg=logEntry.getMessage();
        
        logPaths_.exec(logEntry);
        int pathsNum=logPaths_.getAllPathsNumber();
        int devPathsNum=logPaths_.getDevelExtensions();
        int makePathsNum=logPaths_.getMakeExtensions();
        int docPathsNum=logPaths_.getDocExtensions();
        int graphPathsNum=logPaths_.getGraphicsExtensions();
        String paths=logPaths_.getAllPathsString();
                
        try
        {             
            //pk1_revision\, pk2_fk_projId\, cmitTime\, fk_developerId\, fk_version\,fk_stage\, msg\,
            //pathsNum\, devPathsNum\, makePathsNum\, docPathsNum\, graphPathsNum\, paths
            psUPDATE_COMMIT_TABLE_.setLong(1,revision);
            psUPDATE_COMMIT_TABLE_.setLong(2,projid);
            psUPDATE_COMMIT_TABLE_.setTimestamp(3,tstamp);            
            //psUPDATE_COMMIT_TABLE_.setTimestamp(3,tstamp);
            psUPDATE_COMMIT_TABLE_.setLong(4,commiterid);
            psUPDATE_COMMIT_TABLE_.setString(5,version);
            psUPDATE_COMMIT_TABLE_.setString(6,stage);
            psUPDATE_COMMIT_TABLE_.setString(7,msg);
            psUPDATE_COMMIT_TABLE_.setInt(8,pathsNum);
            psUPDATE_COMMIT_TABLE_.setInt(9,devPathsNum);
            psUPDATE_COMMIT_TABLE_.setInt(10,makePathsNum);
            psUPDATE_COMMIT_TABLE_.setInt(11,docPathsNum);
            psUPDATE_COMMIT_TABLE_.setInt(12,graphPathsNum);
            psUPDATE_COMMIT_TABLE_.setString(13,paths);            
        }
        catch(SQLException e)
        {
            logger_.debug("Commit table failed to be updated with the log entry:\n" +
                    "========================================================================\n "+logEntry.toString()+
                    "========================================================================\n"+
                    MyClassUtils.getStackTraceTabbed(e));
        }
        
        flushDB(logEntry);
    }//handleLogEntry	       
    

    public void setCurrentRelease(Release vs)
    {
        vs_=vs;
    }//setRelease4Log  
    
    
    ///////////////////////////////////////HELPERS//////////////////////////////////////////////
    private void flushDB(SVNLogEntry entry)
    {
        //if this is a script commit (the corresponding developer id is 0)
        //reset the script commit flag back to false and return; do not try 
        //to update the database, because there will be a foreign key violation.      
        //
        //if this will be a duplicate commit, reset the duplicate flag back
        //to false and return
        if ((isScript_) || (isDuplicate_))
        {
            isScript_=false; 
            isDuplicate_=false;
            return;
        }
        
        //update the commit table with the new commit row; in the case the primary key already exists,
        //issue an error. The dupCounter of the table will be ++ as well.
        try
        {
            psUPDATE_COMMIT_TABLE_.executeUpdate();
        }
        catch(SQLException e)
        {
            if (e.getMessage().contains((CharSequence)"Duplicate"))
            {
                logger_.warn("release: "+vs_.toString()+" revision: "+entry.getRevision()+" --> duplicate log entry");
            }
             
//            logger_.fatal("Failed to flush Log entry to DB:\n"+MyClassUtils.getStackTraceTabbed(e));
//            //System.exit(-1);
        }
    }//flushDB 
    ///////////////////////////////////////HELPERS//////////////////////////////////////////////    
}
