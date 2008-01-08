package paxosk.sqoss.developer.dbupdate;

import java.io.*;
import java.util.*;        
import org.apache.commons.collections.ArrayStack;
import org.apache.log4j.*;
import org.apache.commons.configuration.*;
import paxosk.log.loggers.*;
import paxosk.classes.common.*;

/**
 * Parses a text file of the form:
 * nick1    fulName1    mailAddr1
 * nick2    fulName2    mailAddr2
 * ...
 * where the columns are considered to be divided by an arbitrary number of
 * spaces and returns a
 * string array of size (3), where
 * array[1]=nick1
 * array[2]=fullName1
 * array[3]=mailAddr3
 */
public class DeveloperParser 
{
    //the reader to the file containinfg the accounts
    private BufferedReader br_=null;
    //the current line read; line is read each time hasNext is called
    private String line_="";
    //the logger
    private Logger logger_=null;
    
    
    /**
     * Constructor:
     * creates a reader for the given filename and initializes a logger (after the properties file has
     * been read)
     * @param propsfile the name of the properties file
     */
    public DeveloperParser(PropertiesConfiguration props, Logger logger) 
    {                
        logger_=logger;
        
        try 
        {            
            br_=new BufferedReader(new FileReader((String)props.getString("DEVEL_ACCOUNTS_FILE")));  
        } 
        catch (FileNotFoundException ex) 
        {
            logger_.fatal("Failed to open commiter-developer accounts file/n"+MyClassUtils.getStackTraceTabbed(ex));
            System.exit(-1);
        }  
    }//DeveloperParser
    
    
    
    /**
     * Checks whether there is another line in the accounts file after it has been fetched it. Be careful! 
     * the line is read; multiple calls to hasNext without calls to {@link #next()} or {@link #nextProcessed()} 
     * will read the next lines and accounts will remain unprocessed.
     * @return true if there is a next line in the accounts file, false otherwise
     */
    public boolean hasNext()
    {
        try {
            line_=br_.readLine();
        } catch (IOException ex) {
            logger_.fatal("Failed to read from commiter-developer accounts file/n"+MyClassUtils.getStackTraceTabbed(ex));
            System.exit(-1);
        }
                
        if (line_==null) {
            return false;
        }
        
        return true;
    }//hasNext
    
    
    /**
     * Returns the next line of the accounts file unprocessed; the line has already been retrived
     * by {@link #hasNext()}.
     * @return the current line of the accounts file
     */
    public String next()
    {
        return line_; 
    }//next
    
    
    /**
     * Returns the next line of the accounts file processed; the line has already been retrived
     * by {@link #hasNext()}.
     * @return the string array representation of current line of the accounts file
     */
    public String[] nextProcessed()
    {
        String[] sfinal=new String[3];
        String[] stmp=line_.split(" ");    
        sfinal[0]=stmp[0].trim();
        sfinal[1]="";   //appending without initializing to "", would give us "nullXYA";
                        //string intitial value is null, not ""!
        
        for (int i=1;i<stmp.length-1;i++)
        {
            if (!stmp[i].equals("")) 
            {
                sfinal[1]+=stmp[i];
            }                
        }
        
        sfinal[1]=sfinal[1].trim();
        sfinal[2]=stmp[stmp.length-1].trim();        
        
        return sfinal;
    }//nextProcessed
    
    
//    //sample use
//    public static void main(String[] args)
//    {        
//        DeveloperParser dp=new DeveloperParser("C:/Documents and Settings/paxosk/Desktop/projects/SQOSS/src/resources/kde-accounts.txt");
//        
//        while (dp.hasNext())
//        {
//            String[] sar=dp.nextProcessed();            
//            System.out.println(Arrays.toString(sar));
//        }
//    }  
        
}
