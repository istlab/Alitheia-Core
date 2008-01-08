package paxosk.sqoss.developer.dbupdate;

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
 * Inserts the developer data to a TableInsert table -created specifically and corresponding to the DeveloperCommiter 
 * table of the db- and uses this table to do an INSERT in the db; configuration details in resources/all.properties
 */
public class DeveloperInserter 
{
    //the table representation of the SQL insert
    private TableInsert tableInsert_=null;
    //the developer accounts file parser
    private DeveloperParser develParser_=null;
    //the SQL connection
    private Connection con_=null;    
    //the name of the table
    private static final String TABNAME="DeveloperCommiter";
    //the id of the currently processing project -an integer
    private String projectId_="";
    //the logger
    private Logger logger_=null;    
    
        
    /**
     * Class constructor used only for reading propertie, that refer to the SQL connection 
     * and the logging facilities
     */
    public DeveloperInserter()
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
        
        //get the id of the project; used for the table
        projectId_=(String)props_.getString("PROJECT_ID");
        
        //get the SQL properties and initialize and SQL connection
        con_=SQLUtils.initSQLConnection((String)props_.getString("SQL_PORT"),(String)props_.getString("SQL_DB"),
                            (String)props_.getString("SQL_USER"),(String)props_.getString("SQL_PASS"));
        
        //get the logging properties and initialize a logger
        Log4jInstantiator logInst=new Log4jInstantiator();
        logger_=logInst.initFileAppenderLogger((String)props_.getString("LOG_MAIL_FILE"));
        //logInst.addConsole2FileAppender(logger_);
        
        //init the account parser
        develParser_=new DeveloperParser(props_,logger_);        
        
        excludeFields();
    }//MailInserter
    
    
   /**
     * Examines each account entries one-by-one, fills in the TableInsert and uses
     * the latter to update the DeveloperCommiter table
     */
    public void start()
    {        
        //while the parser reports more unprocessed account entries
        while (develParser_.hasNext())
        {                     
            String[] account=develParser_.nextProcessed();
            String nick=account[0];
            String fullname=account[1];
            String mailAddr=account[2];
                
            try                    
            {
                tableInsert_.updateField("fk_projId",projectId_);
                tableInsert_.updateField("cmterName",fullname);
                tableInsert_.updateField("nickname",nick);
                tableInsert_.updateField("emailAddr",mailAddr);
            }
            catch(FieldNotDeclaredException e)
            {
                logger_.error(MyClassUtils.getStackTrace(e));
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
            logger_.debug(Arrays.toString(account)+": "+ sStmt);        
            
            try 
            {                                
                con_.createStatement().executeUpdate(sStmt);
            } 
            catch (SQLException ex) {
                logger_.fatal(Arrays.toString(account)+": "+ sStmt+"\n"+MyClassUtils.getStackTraceTabbed(ex));
                System.exit(-1);
            }

            //reset the values to "" string, so that there is no "old value"
            //in the next run
            tableInsert_.resetTableValues();                        
        }//for all accounts            
    }//start    
        

    /**
     * Removes some fields/columns from the TableInsert table
     */
    private void excludeFields()
    {
        tableInsert_.excludeField("pk_developerId");
        tableInsert_.excludeField("pgpSig");
        tableInsert_.excludeField("score");
        tableInsert_.excludeField("emailSig");
    }//excludeField
}
