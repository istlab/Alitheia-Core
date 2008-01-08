package paxosk.sqoss.db.queries;

import java.sql.*;
import java.io.*;
import java.sql.*;
import org.apache.commons.configuration.*;
import org.apache.log4j.*;
import paxosk.log.loggers.*;
import paxosk.classes.common.*;

public class DBQueries 
{
    private PreparedStatement COMMITER_ID_FROM_NAME=null;
    private Connection con_=null;
    private PropertiesConfiguration props_=null;
    private Logger logger_=null;
    
    public DBQueries(Connection con, Logger logger,PropertiesConfiguration props)
    { 
        try
        {
            con_=con;
            props_=props;
            logger_=logger;
            
            COMMITER_ID_FROM_NAME=con_.prepareStatement((String)props_.getString("COMMITER_ID_FROM_NAME"));                       
        }       
        catch(SQLException e) 
        {
            System.err.println("failed to prepare SQL statement... exiting");
            e.printStackTrace();
            System.exit(-1);            
        }               
    }//VariousQueries
    
    
    public long getCommiterIDFromName(String author)// throws SQLException
    {   
        long id=0;
        
        try
        {
            COMMITER_ID_FROM_NAME.setString(1,author);
            ResultSet rs=COMMITER_ID_FROM_NAME.executeQuery();            

            //rs is supposed to be of size 1, since the author name 
            //is unique in the db
            while (rs.next()) 
            {
                id=rs.getLong(1);

                if (rs.wasNull()) {          
                   logger_.debug("there is no such commiter name in the table: "+author);
                   throw new SQLException();             
                }
            }                    
        }
        catch(SQLException e)
        {
            logger_.fatal(MyClassUtils.getStackTrace(e));
            System.exit(-1);
        }
        
        return id;
    }//getCommiterIDFromName
    
    
    
}
