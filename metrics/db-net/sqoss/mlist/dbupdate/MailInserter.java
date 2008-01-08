package paxosk.sqoss.mlist.dbupdate;

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
 * Extracts values from MIME mail messages, inserts them to a TableInsert
 * table -created specifically and corresponding to the EMail table of the db-
 * and uses this table to do an INSERT in the db; configuration details in
 * resources/all.properties
 *
 * VARIOUS NOTES:
 * ===================
//   ABOUT THE UNQUENESS OF MESSAGE-IDs: 
//   FROM http://www.landfield.com/usefor/1998/Sep/0012.html :
//   ---------------------------------------------------------------
//   1.3 Uniqueness of Message-IDs and Content-IDs 
//
//   According to [RFC822], Message-IDs "are" unique, i.e. not reused for 
//   other email messages. 
//
//   [RFC1036] RECOMMENDS Message-IDs to be unique for at least two years. 
//
//   [RFC2045] says Content-IDs are world-unique "like Message-ID values". 
//
//   This memo RECOMMENDS that IDs are generated in a way that guarantees 
//   uniqueness for an unlimited period of time. The methods presented 
//   here fulfil this recommendation. 
//
//   None of the specifications above says whether reusing an IDs of one 
//   type as the ID of another type (e.g. using the same ID as a Message- 
//   ID and as a Content-ID) is allowed. As any ID generator must be able 
//   to generate an arbitrary number of unique IDs, reusing IDs of one 
//   type for other types of IDs is PROHIBITED. 
//
//   As a special exception, for messages sent via both email and news, 
//   both copies may use the same Message-ID, provided both copies are 
//   considered the same. 
//
//   NOTE: The exact definition of being "the same" is beyond the scope of 
//   this memo.
 *
 */
public class MailInserter 
{
    //the table representation of the SQL insert
    private TableInsert tableInsert_=null;
    //the MIME mail parser
    private MailIterator mailParser_=null;
    //the SQL connection
    private Connection con_=null;    
    //the insert statement, that will be reused for batch INSERTs
    private Statement insStmt_=null;
    //the name of the table
    private static final String TABNAME="EMail";
    //the logger
    private Logger logger_=null;    
    //the props
    private PropertiesConfiguration props_=null;
    //the mail feature extractor
    private SigContentSeparator featEx_=null;    
    //email number being processed
    private long iLetter=0;
    //max batched commands number    
    private int iBatchCommands_=0;
    //current number of commands that have been batched
    private int iCurrentBatchCommands_=0;
//    //counter variable counting the primary key updates
//    private String cVar="dupCounter";

    /**
     * Class constructor used only for reading propertie, that refer to the SQL connection 
     * and the logging facilities
     */
    public MailInserter()
    {        
        //parse the SQL source code and get the table representation for the SQL insert
        tableInsert_=new SourceParser().createTableInsert(TABNAME);
        //parse the properties file        
        try {
            props_=new PropertiesConfiguration("resources/properties/current.properties");
        }
        catch(Exception e) {
            System.err.println("failed to parser properties file... exiting\n"+
                    e.getMessage());
            System.exit(-1);
        }        
        
        //get the logging properties and initialize a logger
        Log4jInstantiator logInst=new Log4jInstantiator();
        logger_=logInst.initFileAppenderLogger(props_.getString("LOG_MAIL_FILE"));        
        
        //init the mail parser
        mailParser_=new MailIterator(props_,logger_);                
        
        //get the SQL properties and initialize and SQL connection
        con_=SQLUtils.initSQLConnection(props_.getString("SQL_PORT"),props_.getString("SQL_DB"),
                            props_.getString("SQL_USER"),props_.getString("SQL_PASS"));
        try
        {
            con_.setAutoCommit(false); //if auto-commit true batched statements are executed exclusively, ie no batch!
            insStmt_=con_.createStatement(); 
        }
        catch(SQLException e)
        {
            logger_.fatal("Failed to set SQL connection and create statement "+ MyClassUtils.getStackTraceTabbed(e));
            System.exit(-1);
        }       

        //how many commands to execute in batched mode
        iBatchCommands_=Integer.valueOf((String)props_.getProperty("BATCH_CMD_NUM"));
        
//        //set the SQL connection restart properties
//        conStopThreshold_=Long.valueOf((String)props_.getProperty("CON_STOP_THRESHOLD"));
//        conReinitDelay_=Long.valueOf((String)props_.getProperty("CON_REINIT_DELAY"));
//        logger_.debug("MySQL restart threshold: "+conStopThreshold_+" and reinitialization delay: "+conReinitDelay_);
        
        //logInst.addConsole2FileAppender(logger_);
        featEx_=new SigContentSeparator(logger_,props_);
        excludeFields();
    }//MailInserter

    
    
    /**
     * Examines each message one-by-one, fills in the TableInsert and uses
     * the latter to update the EMail table
     */
    public void start()
    {        
        //while the parser reports more unprocessed messages
        while (mailParser_.hasNextMessage())
        {                           
            logger_.debug("Now processing letter number: "+ (++iLetter)+" from path: "+mailParser_.getCurrentPath());
            //if (iLetter>=35600) {   //DEBUG letter id
            Enumeration henum=null; 
            MimeMessage msg=mailParser_.getNextMessage(); //get the next MimeMessage
            String sMsg=mailParser_.getCurrentMessageString(); //get its String representation                        
            
            //enumerate all headers
            try {
                henum = msg.getAllHeaders();
            } catch (MessagingException ex) {
                logger_.error(mailParser_.getCurrentPath()+": failure enumerating all the headers...");
            }

            //for all the headers
            while (henum.hasMoreElements())
            {
                Header header=(Header)henum.nextElement();
                String headerName=header.getName(); //header name
                String headerValue=header.getValue().trim(); //header value

                try
                {
                    //the "From" header has either the from "name surname <hah@jaj.com>"
                    //or the form "somename <hah@jaj.com>"
                    if (headerName.equalsIgnoreCase("From"))
                    {                
                        String[] stmp=splitFromHeader("From",headerValue);
                        //update with either "name surname" or "somename"
                        tableInsert_.updateField("fromName",stmp[0].trim());
                        //update with "hah@jaj.com"
                        tableInsert_.updateField("fromEMailAddr",sanitizeHats(stmp[1].trim()));
                    }
                    //convert the "Date" header representation to timestamp
                    //and update
                    //bear in mind we have encountered double headers -lines of the form:
                    //Date: Date: x y z
                    else if (headerName.equalsIgnoreCase("Date"))
                    {
                        tableInsert_.updateField("dateSent",DateUtils.getMimeMessageTimestamp(headerValue));
                    }
                    //remove the hats from the "Message-Id" and update
                    else if (headerName.equalsIgnoreCase("Message-Id"))
                    {
                        tableInsert_.updateField("pk_msgId",sanitizeHats(headerValue));
                    }
                    //update "Subject"
                    else if (headerName.equalsIgnoreCase("Subject"))
                    {
                        tableInsert_.updateField("mailSubject",headerValue);
                    }
                    //remove the hats from the "In-Reply-To" and update
                    else if (headerName.equalsIgnoreCase("In-Reply-To"))
                    {
                        tableInsert_.updateField("inReplyTo",sanitizeHats(headerValue));
                        tableInsert_.updateField("ref_msgId",sanitizeHats(headerValue));
                    }
                    //remove the hats from the "List-Post" and update
                    else if (headerName.equalsIgnoreCase("List-Post"))
                    {
                        tableInsert_.updateField("listPostUrl",sanitizeHats(headerValue));
                    }
                    //remove the hats from the "References" mail ids and update
                    //see http://www.cs.tut.fi/~jkorpela/rfc/1036.html#2.2.5 for further info on
                    //the "References" list
                    else if (headerName.equalsIgnoreCase("References"))
                    {
                        tableInsert_.updateField("mailReferenceList",sanitizeHatsList(headerValue));
                    }
                    //remove the hats from the "List-Archive" and update
                    else if (headerName.equalsIgnoreCase("List-Archive"))
                    {
                        tableInsert_.updateField("listArchiveUrl",sanitizeHats(headerValue));
                    }
                    //update by using the raw "List-Id" description; for ex
                    //"For discussion about KDE PIM applications <kdepim-users.kde.org>"
                    else if (headerName.equalsIgnoreCase("List-Id"))
                    {
                        tableInsert_.updateField("listId",headerValue);
                    }
                    //the  "To" header is too messy; contains fake mail addresses,
                    //users mail addresses
                    //various KDE mailing address, with namings, that containg more
                    //than 2 space seperated words - for ex "KDE Usability Project", etc                    
                    else if (headerName.equalsIgnoreCase("To"))
                    {
                        //the fields/columns, that can be extracted from the "To" header
                        //have been excluded from the TableInsert. See excludeFields()
                    }                    
                }
                catch(FieldNotDeclaredException e)
                {
                    logger_.error(mailParser_.getCurrentPath()+":\n\t header field has not been declared in TableInsert\n"+
                            MyClassUtils.getStackTrace(e));
                }

            }//for all the headers
            
            
            //these headers will be updated only once- if put inside the above loop
            //will be updaeted multiple times
            try            
            {
                String contentNoAttachments=""; //the content without the attachments, that will be returned
                String content=""; //a string variable, used internally by the extractAllContensNoAttachments method
                try
                {
                    contentNoAttachments=featEx_.extractAllContensNoAttachments((Part)msg,content);
                    //update table using the string contents of the message (including the signature)
                    //tableInsert_.updateField("content",featEx_.extractAllContents(sMsg));                    
                    tableInsert_.updateField("content",contentNoAttachments);
                }
                catch(MessagingException e)
                {
                    logger_.error("Failed to extract contents from message!\n"+MyClassUtils.getStackTraceTabbed(e));
                }
                catch(IOException e)
                {
                    logger_.error("Failed to extract contents from message!\n"+MyClassUtils.getStackTraceTabbed(e));
                }
                
                //TODO: the warning below is not right, since a MIME message has a tree structure and the term body parts cannot be
                //defined; use another warning message pointing out the structure of the tree
                //logger_.warn("There have been found ("+((MimeMultipart)msg.getContent()).getCount()+") body parts number in the "+mailParser_.getCurrentPath());
                
                //extract the signature from the message and update; the input MUST NOT contain any attachments
                tableInsert_.updateField("signature",featEx_.extractSignature(contentNoAttachments));
                //get the path of the message and update
                tableInsert_.updateField("msgUrl",mailParser_.getCurrentPath());
                //SQL TEXT fields cannot be declared with a default value; do it here
                tableInsert_.updateField("incomingReferences","");
            }
            catch(FieldNotDeclaredException e)
            {
                logger_.error(mailParser_.getCurrentPath()+":\n\t header field has not been declared in TableInsert\n"+
                        MyClassUtils.getStackTraceTabbed(e));                
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
            logger_.debug(mailParser_.getCurrentPath()+": "+ sStmt);
            
            try 
            {                 
                logger_.debug("adding batch statement...");
                insStmt_.addBatch(sStmt); //add batch command
                
                //if we have reached the maximum number of batched commands
                if (iCurrentBatchCommands_==iBatchCommands_)
                {
                    logger_.debug("Will now execute batched command set");
                    int[] warns=insStmt_.executeBatch();
                    
                    logger_.debug("Finished batch execution with warnings "+Arrays.toString(warns));                    
                    con_.commit();
                    insStmt_.clearBatch();
                    insStmt_.clearWarnings();
                    iCurrentBatchCommands_=0;
                }
                else
                {
                    iCurrentBatchCommands_++;
                }
            }//try 
            catch (SQLException ex) 
            {
                logger_.warn("SQLException with state: "+ex.getSQLState()+" and error code: "+ ex.getErrorCode());
                ex.printStackTrace();
            }//catch
            
            
            //reset the values to "" string, so that there is no "old value"
            //in the next run
            tableInsert_.resetTableValues();
            //} //DEBUG letter id
        }
        
        //close the connection in the end
        try {            
            con_.close();
        } 
        catch (SQLException ex) {
            logger_.error("Failed to close connection to db after having successfully loaded all messages" +
                    MyClassUtils.getStackTraceTabbed(ex));
        }
    }//start    
    
    
    public Connection getConnection()
    {
        return con_;
    }//getConnection
    
    
    public PropertiesConfiguration getProperties()
    {
        return props_;
    }//getProperties
    
    
    public Logger getLogger()
    {
        return logger_;
    }//getLogger
    
    ///////////////////////////////HELPER////METHODS/////////////////////////////////////////////           
    /**
     * given a string of the form "<xyz...>", returns a sting of the form
     * "xyz..."
     */
    private String sanitizeHats(String s)
    {
        String snew="";
        
        for (char c:s.toCharArray())
        {
            if (!((c=='<') || (c=='>'))) {
                snew+=c;
            }                
        }//for
        
        return snew;
    }//sanitizeHats
    
    
    
    /* Given a string of the form:
     * "<20701150859.49823.gene.heskett@verizon.net> <200701151842.54713.gene.heskett@verizon.net>
     * <20701160908.28804.art.alexion@verizon.net>...", removes the hats and returns:
     * "20701150859.49823.gene.heskett@verizon.net 200701151842.54713.gene.heskett@verizon.net
     *  20701160908.28804.art.alexion@verizon.net"
     */
    private String sanitizeHatsList(String s)
    {
        String snew="";
        
        String[] stmp=s.split(" ");
        for (String s2: stmp)
        {
            snew+=sanitizeHats(s2)+" ";
        }//for
        
        return snew;
    }//sanitizeHatsList
    
    
    
    /**
     * Truncates a given string representaton of an e-mail address; e-mail address may
     * be coming from the "From" header or from the "Reply-To" header or from any other header
     * containing an e-mail address followed by the name of the owner or not
     * For example:
     * given "Art Alexion <art.alexion@verizon.net>"
     * returns array[0]="Art Alexion"
     *          array[1]="art.alexion@verizon.net"
     * @parameter hName the name of the header ("From","Reply-To")
     * @parameter hValue the value of the header
     * @return the header value truncated inside a string array
     */
    private String[] splitFromHeader(String hName, String hValue)
    {        
        String[] sfinal=new String[2];
        String[] stmp=hValue.split(" ");
        //1: name, 2: surname, 3: unsanitized mail address
        if (stmp.length==3)
        {
            String name=stmp[0]+" "+stmp[1];
            String email=sanitizeHats(stmp[2]);
            sfinal[0]=name;
            sfinal[1]=email;
        }
        //1: some_name, 2: unsanitized mail address
        else if (stmp.length==2)
        {
            String name=stmp[0];
            String email=sanitizeHats(stmp[1]);
            sfinal[0]=name;
            sfinal[1]=email;            
        }
        //1: some_name, 2: middle_name, 3: some_name, 4: unsanitized email-address
        else if (stmp.length==4)
        {
            String name=stmp[0]+" "+stmp[1]+" "+stmp[2];
            String email=sanitizeHats(stmp[3]);
            sfinal[0]=name;
            sfinal[1]=email;                        
        }
        //Marcelo, Magno, T., Sales, mailaddr@sth.net
        //1: some_name, 2: middle_name, 3: some_name ,4: some_name, 5: unsanitized email-address
        else if (stmp.length==5)
        {
            String name=stmp[0]+" "+stmp[1]+" "+stmp[2]+" "+stmp[3];
            String email=sanitizeHats(stmp[4]);
            sfinal[0]=name;
            sfinal[1]=email;                        
        }
        //1:email-address
        else if (stmp.length==1)
        {
            String name=" ";
            String email=sanitizeHats(stmp[0]);
            sfinal[0]=name;
            sfinal[1]=email;            
        }        
        else if (stmp.length > 5)
        {
            String name=" ";
            String email=sanitizeHats(stmp[stmp.length-1]);
            sfinal[0]=name;
            sfinal[1]=email;                        
        }
        
        logger_.debug(hName +" header initially truncated to ("+stmp.length+") words. Final representation: "+Arrays.toString(stmp));
        
        return sfinal;
    }//splitFromHeader        
    
    
    /**
     * Removes some fields/columns from the TableInsert table
     */
    private void excludeFields()
    {
        tableInsert_.excludeField("toEmailAddr1"); //for future use
        tableInsert_.excludeField("toEmailAddr2"); //for future use
        tableInsert_.excludeField("toEmailAddr3"); //for future use
        tableInsert_.excludeField("fk1_developerId"); //filled afterwards, after the DeveloperCommiter table has
                                                      //been created - we do not know IDs now  
        tableInsert_.excludeField("isSpam");    //checked and filled afterwards
        tableInsert_.excludeField("dupCounter");
        tableInsert_.excludeField("score");
        tableInsert_.excludeField("rank");
        tableInsert_.excludeField("inRefCounter");
        //tableInsert_.excludeField("incomingReferences");
    }//excludeFields        
    ///////////////////////////////HELPER////METHODS/////////////////////////////////////////////   
}
