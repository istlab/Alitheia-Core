package paxosk.sqoss.project.dbupdate;

import java.util.*;
import java.sql.*;
import java.io.*;
import javax.mail.internet.*;
import javax.mail.*;
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
public class ProjectInserter 
{
    //the table representation of the SQL insert
    private TableInsert tableInsert_=null;
    //the projects file listing parser parser
    private ProjectParser projectParser_=null;
    //the SQL connection
    private Connection con_=null;    
    //the name of the table
    private static final String TABNAME="Project";
    //the logger
    private Logger logger_=null;    
    
        
    /**
     * Class constructor used only for reading properties, that refer to the SQL connection 
     * and the logging facilities
     */
    public ProjectInserter()
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
        logger_=logInst.initFileAppenderLogger((String)props_.getString("LOG_PROJECT_FILE"));
        //logInst.addConsole2FileAppender(logger_);
        
        //init the mail parser
        projectParser_=new ProjectParser(props_,logger_);
        
        //get the SQL properties and initialize and SQL connection
        con_=SQLUtils.initSQLConnection((String)props_.getString("SQL_PORT"),(String)props_.getString("SQL_DB"),
                            (String)props_.getString("SQL_USER"),(String)props_.getString("SQL_PASS"));        
    }//MailInserter

    
    
    /**
     * Examines each message one-by-one, fills in the TableInsert and uses
     * the latter to update the Project table
     */
    public void start()
    {        
        //while the parser reports more unprocessed project entries
        while (projectParser_.hasNext())
        {      
            String[] prj=projectParser_.nextProcessed();
            String id=prj[0];
            String sName=prj[1];
            String lName=prj[2];
            String date=prj[3];
            String realUrl=prj[4];
            //String repoName=prj[5];

            assert prj.length==5;
            
            try
            {
                tableInsert_.updateField("pk_projId",id);
                tableInsert_.updateField("shortName",sName);
                tableInsert_.updateField("longName",lName);
                tableInsert_.updateField("launchDate",DateUtils.getSimpleTimestamp(date));
                tableInsert_.updateField("realUrl",realUrl);
                //tableInsert_.updateField("fk_repoName",repoName);
            }
            catch(FieldNotDeclaredException e)
            {
                logger_.error(Arrays.toString(prj)+": field of InsertTable not declared:\n"+MyClassUtils.getStackTraceTabbed(e));
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
            logger_.debug(Arrays.toString(prj)+": "+ sStmt);        
            
            try 
            {                                
                con_.createStatement().executeUpdate(sStmt);
            } 
            catch (SQLException ex) {
                logger_.fatal(Arrays.toString(prj)+": "+ sStmt+"\n"+MyClassUtils.getStackTraceTabbed(ex));
                System.exit(-1);
            }

            //reset the values to "" string, so that there is no "old value"
            //in the next run
            tableInsert_.resetTableValues();                                    
        }//while more project entries
    }//start
    
}
