package paxosk.sql.common;

import java.util.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import org.apache.commons.configuration.*;
import org.apache.log4j.*;
import paxosk.classes.common.MyClassUtils;
import paxosk.sql.common.*;
import paxosk.log.loggers.*;

/**
 * Utilities for parsing an SQL text file and retrieving/creating hashtables tables
 * for the given declared tables.
 * BE CAREFUL! Make sure, that the field names are not tab separated from their types,
 * but space separated!
 */
public class SourceParser 
{    
    //the string representation of the contents of the SQL source file
    private String SQLfile_="";
    //the logger; this logger will also be used by the TableInsert tables\
    //launched by the SourceParser!
    private Logger logger_=null;
    //the props
    private PropertiesConfiguration props_=null;
    
    /** 
     * Constructor: reads the name of the properties file, gets the name of the SQL source file
     * and loads it into the memory
     * properties required: LOG_SQL_FILE, SQL_TXT
     */
    public SourceParser()
    {       
        PropertiesConfiguration props_=null;
        try
        {
            props_=new PropertiesConfiguration("resources/properties/current.properties");
        }
        catch (Exception e)
        {
            System.err.println("SourceParser constructor: problem parsing properties file... exiting\n"+
                    MyClassUtils.getStackTraceTabbed(e));
            System.exit(-1);
        }
        
        Log4jInstantiator logInst=new Log4jInstantiator();
        logger_=logInst.initFileAppenderLogger((String)props_.getString("LOG_SQL_FILE"));
        //logInst.addConsole2FileAppender(logger_);
        readfile((String)props_.getString("SQL_TXT"));
    }//SourceParser
    
    
    /** 
     * Constructor: given the name of an SQL file, loads it into memory
     * @param logfilename the full path to the log file
     * @param sqlfilename the full path to  the SQL source file
     */
    public SourceParser(String logfilename,String sqlfilename)
    {
        Log4jInstantiator logInst=new Log4jInstantiator();
        Logger logger_=logInst.initFileAppenderLogger(logfilename);
        //logInst.addConsole2FileAppender(logger_);
        readfile(sqlfilename);
    }//SourceParser
    
    
    
    /* for the given table name, returns  a table, which contains
     * the fieldnames and the corresponding (type,value) tuple;
     * BE CAREFUL! before using the TableInsert returned you must
     * call the "validate" method on it.
     */
    public TableInsert createTableInsert(String tabName)
    {
        TableInsert vct=new TableInsert(tabName,logger_);
        
        String s1="CREATE TABLE "+tabName;
        
        int txtStartIndex=SQLfile_.indexOf(s1)+s1.length();
        String s2=SQLfile_.substring(txtStartIndex);
        
        int txtEndIndex=s2.indexOf(");");
        String s3=s2.substring(0,txtEndIndex);  
        
        //s3 has now everything, that is contained inside the outer
        //parenthesis of a table creation declaration
        
        String[] lines=s3.split("\n");
        
        for (String line:lines)
        {
            line=line.trim(); //SQL source file lines may begin with tabs and spaces!
                              //trim these first and then split based upon spaces and tabs!
            if ((!line.contains("FOREIGN")) && (!line.contains("PRIMARY")))
            {                
                String fieldName=line.split(" ")[0].trim();
                if (!(fieldName.equals("") || fieldName.equals("("))) {
                    TableInsert.Type fieldType=TableInsert.getCorrespondingType(line.split(" ")[1].trim());
                    vct.declareField(fieldName,fieldType);
                    logger_.debug("Declaring field name: "+ fieldName+" of type: "+fieldType+" for line: "+ line);                
                }
            }           
        }
        
        logger_.debug("Initialized empty TableInsert for: "+ tabName+": "+vct.toString());
        return vct;   
    }//getFieldsTable

    
    
    
    //////////////////////////////////////////////////////////HELPERS////////////////////////////////////////////////////////
    private void readfile(String filename)
    {
         try
        {
            File f=new File(filename);
            FileChannel fc=new RandomAccessFile(f,"r").getChannel();
            ByteBuffer bb=fc.map(FileChannel.MapMode.READ_ONLY,0,(int)fc.size());
            Charset chset=Charset.forName("ISO-8859-1");
            CharsetDecoder dec=chset.newDecoder();
            SQLfile_=new String(dec.decode(bb).toString());
        }
        catch(Exception e)
        {
            logger_.fatal("readfile: problem reading sql txt file... exiting\n"+
                                            MyClassUtils.getStackTrace(e));
            System.exit(-1);
        }        
    }//readfilename
    //////////////////////////////////////////////////////////HELPERS////////////////////////////////////////////////////////    
    
//    //sample usage
//    public static void main(String[] args)        
//    {
//        SourceParser ut=new SourceParser();
//        TableInsert tb=ut.createColumnValueTable("EMail");
//        tb.validate();
//        System.err.println(tb);
//    }//main
}
