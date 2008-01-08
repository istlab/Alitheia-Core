package paxosk.sqoss.portalproject.dbupdate;

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
 * Inserts the repository data to a TableInsert table -created specifically and corresponding to the Repository 
 * table of the db- and uses this table to do an INSERT in the db; configuration details in resources/all.properties
 */
public class PortalProjectInserter 
{
    //the table representation of the SQL insert
    private TableInsert tableInsert_=null;
    //the developer accounts file parser
    private PortalProjectParser reposParser_=null;
    //the SQL connection
    private Connection con_=null;    
    //the name of the table
    private static final String TABNAME="Portal_Project";
    //the id of the currently processing project -an integer
    private String projectId_="";
    //the logger
    private Logger logger_=null;    
    
        
    /**
     * Class constructor used only for reading properties, that refer to the SQL connection 
     * and the logging facilities
     */
    public PortalProjectInserter()
    {
        //parse the SQL source code and get the table representation for the SQL insert
        tableInsert_=new SourceParser().createTableInsert(TABNAME);
        //parse the properties file
        PropertiesConfiguration props_=null;
        try {
            props_=new PropertiesConfiguration("resources/properties/current.properties");            
        }
        catch(Exception e) {
            logger_.fatal("failed to parse properties file... exiting\n"+
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
        logger_=logInst.initFileAppenderLogger((String)props_.getString("LOG_PORTALPROJECT_FILE"));
        //logInst.addConsole2FileAppender(logger_);                
        //init the repository parser
        reposParser_=new PortalProjectParser(props_,logger_);        
    }//RepositoryInserter
    
    
   /**
     * Examines each repositories entries one-by-one, fills in the TableInsert and uses
     * the latter to update the Repository table
     */
    public void start()
    {        
        //while the parser reports more unprocessed repository entries
        while (reposParser_.hasNext())
        {                      
            String[] repos=reposParser_.nextProcessed();
            //String repoName=repos[0];
            String portalId=repos[0];
            String projId=repos[1];
            String mlistUrl=repos[2];
            String svnUrl=repos[3];
            String cvsUrl=repos[4];
            String forumUrl=repos[5];
            String wikiUrl=repos[6];
            
            assert repos.length==7;
                    
            try                    
            {
                //tableInsert_.updateField("pk_name",repoName);
                tableInsert_.updateField("pk_fk_portalId",portalId);
                tableInsert_.updateField("pk_fk_projId",projId);
                tableInsert_.updateField("mlistUrl",mlistUrl);
                tableInsert_.updateField("svnUrl",svnUrl);
                tableInsert_.updateField("cvsUrl",cvsUrl);
                tableInsert_.updateField("forumUrl",forumUrl);                
                tableInsert_.updateField("wikiUrl",wikiUrl);
            }
            catch(FieldNotDeclaredException e)
            {
                logger_.fatal(MyClassUtils.getStackTrace(e));
                System.exit(-1);
            }
            
            //validate the current to be INSERTed table and break
            //if not successful
            if (!tableInsert_.validate()) {
                logger_.error("failed to validate TableInsert:\n"+
                        "===============================================================\n"+
                        tableInsert_+"\nproceeding to next...\n");
            }

            //get the INSERT statement and update the DB
            String sStmt=SQLUtils.createInsertStatement(tableInsert_);
            logger_.debug(Arrays.toString(repos)+": "+ sStmt);        
            
            try 
            {                                
                con_.createStatement().executeUpdate(sStmt);
            } 
            catch (SQLException ex) {
                logger_.fatal(Arrays.toString(repos)+": "+ sStmt+"\n"+MyClassUtils.getStackTraceTabbed(ex));
                System.exit(-1);
            }

            //reset the values to "" string, so that there is no "old value"
            //in the next run
            tableInsert_.resetTableValues();                        
        }//for all accounts            
    }//start        
}
