package paxosk.sqoss.svn.release;

import java.net.URL;
import java.util.*;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.*;
import paxosk.sqoss.svn.*;
import paxosk.date.common.*;
import paxosk.classes.common.*;
import java.io.*;
import org.apache.commons.configuration.*;
import org.apache.log4j.*;
import paxosk.log.loggers.*;
import paxosk.sqoss.svn.common.*;
import paxosk.sqoss.svn.release.*;
import java.sql.*;


/**
 *
 */
public class DBUpdateRelease 
{    
    private Connection con_=null;
    private Logger logger_=null;
    private PropertiesConfiguration props_=null;
    private String UPDATE_RELEASE_TABLE,PROJECT_ID="";
    private PreparedStatement ps_=null;
    
    
    public DBUpdateRelease(Connection con, Logger logger, PropertiesConfiguration props) 
    {
        con_=con;
        logger_=logger;    
        props_=props;
        UPDATE_RELEASE_TABLE=(String)props_.getString("UPDATE_RELEASE_TABLE");
        PROJECT_ID=(String)props_.getString("PROJECT_ID");
        
        try{
            ps_=con_.prepareStatement(UPDATE_RELEASE_TABLE);
        } catch (SQLException e) {
            logger_.fatal(MyClassUtils.getStackTraceTabbed(e));
            System.exit(-1);
        }
    }//SVNTagsHandler
    
    
    /**
     *
     */
    public void handleTagEntry(Release vs)
    {    
        logger_.debug(vs.toStringProcessed()+ ": will be stored in the database");        
        try
        {
            ps_.setString(1,PROJECT_ID);
            ps_.setString(2,vs.getVersion());
            ps_.setString(3,vs.getStage());
            ps_.setInt(4,0); //in the working days field a default value will be set
            ps_.setTimestamp(5,vs.getTimestamp());
        }
        catch(SQLException e)
        {
            logger_.fatal("Failed to update UPDATE_RELEASE_TABLE prepared statement:\n"+MyClassUtils.getStackTraceTabbed(e));        
            System.exit(-1);
        }                
        //DateUtils.getFromDateTimestamp(entry.getDate()); //extract the timestamp    			    		
            
        flushDB(vs);
    }//handleTagEntry
       
    
    /////////////////////////////////////////HELPERS/////////////////////////////////////////////
    private void flushDB(Release vs)
    {
        try
        {
            ps_.executeUpdate();          
            logger_.debug(vs.toStringProcessed()+": has been stored in the database");
        }
        catch(SQLException e)
        {
            logger_.fatal("Failed to flush Release entry to DB:\n"+MyClassUtils.getStackTraceTabbed(e));
            System.exit(-1);
        }
    }//flushDB
    /////////////////////////////////////////HELPERS/////////////////////////////////////////////    
}
