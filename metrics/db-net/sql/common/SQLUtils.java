package paxosk.sql.common;

import java.sql.*;
import paxosk.sql.common.exceptions.*;

public class SQLUtils 
{
    private static Connection con_=null;
    private static String port_;
    private static String dbname_;
    private static String user_;
    private static String pass_;
    
    /**
     * Initializes and returns and SQL connection to the requested DB.
     * @param port the number of the port
     * @dbname the name of the db
     * @user the username
     * @pass the password
     * @return an SQL connection
     */
    public static Connection initSQLConnection(String port,String dbname,String user,String pass)
    {    
        port_=port;
        dbname_=dbname;
        user_=user;
        pass_=pass;
        
        try 
        {
            Class.forName("com.mysql.jdbc.Driver");
            con_=DriverManager.getConnection("jdbc:mysql://localhost:"+port+"/"+dbname,user,pass);
        } 
        catch(SQLException ex)
        {
            ex.printStackTrace();
        }
        catch (ClassNotFoundException ex) 
        {
            ex.printStackTrace();
        }      
        
        return con_;
    }//initSQLConnection        
    
    
    
    /**
     * Reinitializes the existing SQL connection; used for various purposes, such as
     * to bypass the problem of havng the DB hanging from muliple queries.
     * @param con the connecion to be reinitialized
     * @param lDelay sleep for lDelay millis
     * @return the new connection
     *
     * TODO: redesign the class in an object-oriented way; main problem is this 
     * class
     */
    public static Connection reinitSQLConnection(Connection con,long lDelay)
    {
        try {
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        try {            
            Thread.currentThread().sleep(lDelay);
        } catch (InterruptedException ex) {            
            ex.printStackTrace();
        }
        
        return initSQLConnection(port_,dbname_,user_,pass_);
    }//reinitSQLConnection
    
    
    
    /**
     * Uses the TableInsert to create and return an SQL
     * INSERT statement.
     */
    public static String createInsertStatement(TableInsert ti)
    {
        String stmt="INSERT INTO "+ti.getName()+" ";
        String cols=ti.getColumnsString();
        String vals=" VALUES";
        
        try {            
            vals=ti.getValuesString();
        } 
        catch (ColumnsNotRetrievedException ex) {
            ex.printStackTrace();
        }
        
        stmt+=cols+" VALUES "+vals+";";
        
        return stmt;
    }//createInsertStatement
    
    
    /**
     * Uses the TableInsert to create and return an SQL
     * INSERT statement. If a duplicate key is found, it updates a counter variable
     * @ti param the table representing the insert statetment
     * @var param the counter variable
     */
    public static String createInsertStatementUpdateKey(TableInsert ti, String var)
    {
        String stmt="INSERT INTO "+ti.getName()+" ";
        String cols=ti.getColumnsString();
        String vals=" VALUES";
        
        try {            
            vals=ti.getValuesString();
        } 
        catch (ColumnsNotRetrievedException ex) {
            ex.printStackTrace();
        }
        
        stmt+=cols+" VALUES "+vals+" ON DUPLICATE KEY UPDATE "+var+"="+var+"+1;";
        
        return stmt;
    }//createInsertStatement
    
    
    
    
    /**
     * Sanitizes the string, that are to be stored in the DB. Removes characters or
     * sequesnces of characters, that could be misinterpreted by the DB engine.
     * @param s the unsanitized string
     * @return the sanitized one
     */
    public static String sanitizeInputString(String s)
    {
       String snew=s;
       snew=snew.replace((CharSequence)"--",(CharSequence)" ");
       snew=snew.replace((CharSequence)"\"",(CharSequence)" ");
       
       return snew;
    }//sanitizeInputString
    
    
    /**
     * Wraper method for closing the connection
     */
    public static void closeConnection()
    {
        try {
            con_.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }//closeConnection
    
    
//    //sample usage
//    public static void main(String[] args)
//    {
//        System.out.println(sanitizeInputString("afasf---asdf"));
//    }//main    
}
