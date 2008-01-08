package paxosk.sqoss.portal.dbupdate;

import java.util.*;
import java.sql.*;
import java.io.*;
import org.apache.commons.configuration.*;
import org.apache.log4j.*;
import paxosk.classes.common.*;
import paxosk.sql.common.*;
import paxosk.sql.common.exceptions.*;
import paxosk.date.common.*;
import paxosk.log.loggers.*;
import paxosk.classes.common.*;

/**
 * Inserts project listings to a TableInsert table -created specifically and corresponding to the Project 
 * table of the db- and uses this table to do an INSERT in the db; configuration details in resources/all.properties
 */
public class PortalInserter 
{
    //the table representation of the SQL insert
    private TableInsert tableInsert_=null;
    //the projects file listing parser parser
    private PortalParser portalParser_=null;
    //the SQL connection
    private Connection con_=null;    
    //the name of the table
    private static final String TABNAME="Portal";
    //the logger
    private Logger logger_=null;    
    
        
    /**
     * Class constructor used only for reading properties, that refer to the SQL connection 
     * and the logging facilities
     */
    public PortalInserter()
    {
        //parse the SQL source code and get the table representation for the SQL insert
        tableInsert_=new SourceParser().createTableInsert(TABNAME);
        //parse the properties file
        PropertiesConfiguration props_=null;
        try {
            props_=new PropertiesConfiguration("resources/properties/current.properties");            
        }
        catch(Exception e) {
            logger_.fatal("failed to parser properties file... exiting\n"+
                    e.getMessage());
            System.exit(-1);
        }               
        
        //get the logging properties and initialize a logger
        Log4jInstantiator logInst=new Log4jInstantiator();
        logger_=logInst.initFileAppenderLogger((String)props_.getString("LOG_PORTAL_FILE"));
        //logInst.addConsole2FileAppender(logger_);
        
        //init the mail parser
        portalParser_=new PortalParser(props_,logger_);
        
        //get the SQL properties and initialize and SQL connection
        con_=SQLUtils.initSQLConnection((String)props_.getString("SQL_PORT"),(String)props_.getString("SQL_DB"),
                            (String)props_.getString("SQL_USER"),(String)props_.getString("SQL_PASS"));        
    }//PortalInserter

    
    
    /**
     * Examines each message one-by-one, fills in the TableInsert and uses
     * the latter to update the Project table
     */
    public void start()
    {        
        //while the parser reports more unprocessed project entries
        while (portalParser_.hasNext())
        {      
            String[] prt=portalParser_.nextProcessed();
            String id=prt[0];
            String name=prt[1];            

            assert prt.length==2;
            
            try
            {
                tableInsert_.updateField("pk_portalId",id);
                tableInsert_.updateField("portalName",name);                
            }
            catch(FieldNotDeclaredException e)
            {
                logger_.fatal(Arrays.toString(prt)+": field of InsertTable not declared:\n"+MyClassUtils.getStackTraceTabbed(e));
                System.exit(-1);
            }            
            
            //validate the current to be INSERTed table and break
            //if not successful
            if (!tableInsert_.validate()) {
                logger_.warn("failed to validate TableInsert:\n"+
                        "===============================================================\n"+
                        tableInsert_+"\nproceeding to next...\n");
            }

            //get the INSERT statement and update the DB
            String sStmt=SQLUtils.createInsertStatement(tableInsert_);
            logger_.debug(Arrays.toString(prt)+": "+ sStmt);        
            
            try 
            {                                
                con_.createStatement().executeUpdate(sStmt);
            } 
            catch (SQLException ex) {
                logger_.fatal(Arrays.toString(prt)+": "+ sStmt+"\n"+MyClassUtils.getStackTraceTabbed(ex));
                System.exit(-1);
            }

            //reset the values to "" string, so that there is no "old value"
            //in the next run
            tableInsert_.resetTableValues();                                    
        }//while more project entries
    }//start
    
}
