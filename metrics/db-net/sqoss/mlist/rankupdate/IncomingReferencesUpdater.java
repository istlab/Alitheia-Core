package paxosk.sqoss.mlist.rankupdate;

import org.apache.log4j.*;
import org.apache.commons.configuration.*;
import java.io.*;
import java.sql.*;
import java.util.concurrent.*;
import java.util.*;
import paxosk.sqoss.mlist.dbupdate.*;
import paxosk.sql.common.exceptions.*;
import paxosk.log.loggers.*;
import paxosk.sql.common.*;
import paxosk.classes.common.*;

/**
 * Class
 */
public class IncomingReferencesUpdater 
{
    //the logger
    private Logger logger_=null;
    //the connection to the database
    private Connection con_=null;
    //the properties
    private PropertiesConfiguration props_=null;
    //update the incoming references and their number to the given message id
    private PreparedStatement psOUT_INCOM_REFERENCES=null;
    //retrieve the msgId and the sender IDs
    private PreparedStatement psIN_REPLY_SENDER_IDS=null; 
    //retrieve the IDs of all leaf messages
    private PreparedStatement psIN_LEAF_MAILS=null;
    //retrieve the set of msg IDs, that answer a specific msg ID
    private PreparedStatement psIN_ANSWERING_IDS=null;
    //set the rank of a given msg id
    private PreparedStatement psOUT_RANK=null;
    //get the rank of a given msg id
    private PreparedStatement psIN_RANK=null;
        
    
    /**
     * Constructor
     * keeps track of the logger, properties and SQL connection. Creates SQL
     * PreparedStatement for later usage.
     */
    public IncomingReferencesUpdater(Connection con, Logger logger, PropertiesConfiguration props) 
    {                
        //the immediate below vars will contain references to the
        //corresponding values of the mail parsing class
        logger_=logger; //init logger
        props_=props; //init properties
        con_=con; //init SQL connection
        
        //get the SQL properties and initialize and SQL connection
        con_=SQLUtils.initSQLConnection(props_.getString("SQL_PORT"),props_.getString("SQL_DB"),
                            props_.getString("SQL_USER"),props_.getString("SQL_PASS"));
        
        try
        {
            psOUT_INCOM_REFERENCES=con_.prepareStatement(props_.getString("OUT_INCOM_REFERENCES"));
            psIN_REPLY_SENDER_IDS=con_.prepareStatement(props_.getString("IN_REPLY_SENDER_IDS"));
            psIN_LEAF_MAILS=con_.prepareStatement(props_.getString("IN_LEAF_MAILS"));
            psIN_ANSWERING_IDS=con_.prepareStatement(props_.getString("IN_ANSWERING_IDS"));
            psIN_RANK=con_.prepareStatement(props_.getString("IN_RANK"));
            psOUT_RANK=con_.prepareStatement(props_.getString("OUT_RANK"));
        }
        catch(SQLException e)
        {
            logger_.fatal("Failed to prepare statements! exiting... "+MyClassUtils.getStackTraceTabbed(e));
            System.exit(-1);
        }
    }//IncomingReferencesUpdater


    
    /**
     *
     */
    public void exec()
    {
        //for all the emails find their incoming references from other mauils
        //and their number; update the DB with these
        updateIncomingReferences();
        //create a queue with all leaf message IDs
        Queue<String> queue=getAllLeafIDs();
        
        //while non-empty 
        //  remove one ID (say idSource) at a time
        //  find the ID (say idDest) of the message idSource is a response to
        while (!queue.isEmpty())
        {            
            String id=queue.poll(); //would be null if queue had been empty, but already checked
            String upperLevelMsgId=getInReplyTo(id);             
            
            //if there is an upper level message
            //  set the rank of the upper level message
            //  if not contained in the queue, add the upper level message in the queue
            if ((upperLevelMsgId!=null) || (!upperLevelMsgId.equals("")))
            {
                setRank(upperLevelMsgId,getRank(upperLevelMsgId)+getRank(id)+1);
                if (!queue.contains(upperLevelMsgId)) {
                    queue.offer(upperLevelMsgId);
                }
            }
        }
    }//exec   
    
    
    ///////////////////////////////////////////HELPERS/////////////////////////////////////////////////    
    /**
     * Set the given rank of the message with given ID.
     * @param String the ID of the message
     * @param int the rank of the message
     */
    private void setRank(String msgid, int rank)
    {
        try
        {
            psIN_RANK.setString(1,msgid);
            psIN_RANK.setInt(2,rank);
        
            psIN_RANK.executeUpdate();
        }
        catch(SQLException e)
        {
            logger_.fatal("Failed to set rank of msg with id: "+msgid+"\n"+MyClassUtils.getStackTraceTabbed(e));
            System.exit(-1);
        }
    }//setRank
    
    
    /**
     * Retrieve the rank of the message with the given ID
     * @param String the msgid
     * @return int the rank
     */
    private int getRank(String msgid)
    {
        int rank=0;
        
        try
        {
            psOUT_RANK.setString(1,msgid);
            ResultSet rs=psOUT_RANK.executeQuery();
                        
            if (rs.next())
            {
                rank=rs.getInt(1);
            }
        }
        catch(SQLException e)
        {
            logger_.fatal("Failed to retrieve rank of msg with id: "+msgid+"\n"+MyClassUtils.getStackTraceTabbed(e));
        }
        
        if (rank==0) {
            logger_.warn("Rank 0 detected for message with id: "+msgid);
        }
        
        return rank;
    }//getRank
    
    
    /**
     * Will find for each message m, the replies(m) and the count(replies(m)) and
     * will update the corresponding fields of the EMail table.
     */
    private void updateIncomingReferences()
    {
        String replierMsgId="";
        String senderMsgId="";                
        
        try
        {        
            logger_.debug("Will retrieve from DB all replying msg IDs and the corresponding " +
                    "sender msg IDs");            
            ResultSet rsReplies=psIN_REPLY_SENDER_IDS.executeQuery();
            while (rsReplies.next())
            {
                replierMsgId=rsReplies.getString(1);
                senderMsgId=rsReplies.getString(2);
                psOUT_INCOM_REFERENCES.setString(1,replierMsgId);
                psOUT_INCOM_REFERENCES.setString(2,senderMsgId);
                logger_.debug("Will now update incoming reference from message id"+replierMsgId+
                        " to message id "+senderMsgId);
                psOUT_INCOM_REFERENCES.executeUpdate();
            }
            
        }
        catch(SQLException e)
        {
            logger_.fatal("Failed to update incoming reference from message id"+replierMsgId+
                        " to message id "+senderMsgId+"...exiting\n"+MyClassUtils.getStackTraceTabbed(e));
            System.exit(-1);
        }
        
    }//updateIncomingReferences
    
    
    
    /**
     * Retrieves all leaf messages IDs from the DB; {@link #updateIncomingReferences
     * updateIncomingReferences} must have been called first.     
     * @return a queue, that contains all leaf message IDs
     */
    private ConcurrentLinkedQueue<String> getAllLeafIDs()
    {
        ConcurrentLinkedQueue<String> queue=new ConcurrentLinkedQueue<String>();        
        
        try 
        {            
            ResultSet rsIDs=psIN_LEAF_MAILS.executeQuery();
            while (rsIDs.next())
            {
                queue.offer(rsIDs.getString(1));
            }
        }
        catch(SQLException e)
        {
            logger_.fatal("Failed to retrieve leaf IDs from DB! exiting... "+
                    MyClassUtils.getStackTraceTabbed(e));
            System.exit(-1);
        }
        
        if (queue.isEmpty()) {
            logger_.warn("No LEAF messages have been detected!");
        }
        
        return queue;
    }//getAllLeafIDs
    
    
    /**
     * Retrieves the message ID, that the parameter is a reply to.
     * @param String msg ID
     * @return the message ID, that msgid replies to
     */
    private String getInReplyTo(String msgid)
    {
        String id="";
        
        try
        {
            psIN_REPLY_SENDER_IDS.setString(1,msgid);
            ResultSet rs=psIN_REPLY_SENDER_IDS.executeQuery();
                        
            if (rs.next())
            {
                id=rs.getString(1);
            }
        }
        catch(SQLException e)
        {
            logger_.fatal("Failed to prepare IN_REPLY_SENDER_IDS statement! exiting ..."+
                    MyClassUtils.getStackTraceTabbed(e));
            System.exit(-1);
        }       
        
        return id;
    }//getInReplyTo
    ///////////////////////////////////////////HELPERS/////////////////////////////////////////////////
    
//    //sample usage
//    public static void main(String[] args)
//    {
//        MailInserter mi=new MailInserter();
//        mi.start();    
//        new IncomingReferencesUpdater(mi.getConnection(),mi.getLogger(),mi.getProperties()).exec();
//    }//main
}
