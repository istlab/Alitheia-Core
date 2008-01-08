package paxosk.sqoss.project.dbupdate;

import org.apache.commons.configuration.*;
import org.apache.log4j.*;
import java.io.*;
import java.util.*;
import paxosk.log.loggers.*;
import paxosk.classes.common.*;

/**
 * Parses a CSV file of the following format:
 * 0,KDE,KDE,2007.1.1,www.kde.org,www.kde.org
 */
public class ProjectParser 
{
    //the logger for the projects list
    private Logger logger_=null;
    //the reader to the projects list file
    private BufferedReader br_=null;
    //the current line, that has just been read from the projects list file
    private String line_="";
    
    /**
     * Constructor:
     * reads the properties file and gets the name of the projects list file
     * and the name of the logger, which it initalizes
     * @param propsfile the properties file
     */   
    public ProjectParser(PropertiesConfiguration props,Logger logger) 
    {        
        try {
            br_=new BufferedReader(new FileReader((String)props.getString("PROJECT_FILE")));
        }
        catch (IOException e) {
            logger_.fatal("failed to open projects list file:/n"+MyClassUtils.getStackTraceTabbed(e));
            System.exit(-1);
        }        
    }//ProjectParser
    
    
    /**
     * Checks if there is another line/entry in the projects listing file; Be careful! 
     * the line is read; multiple calls to hasNext without calls to {@link #next()} or {@link #nextProcessed()} 
     * will read the next lines and project entries will remain unprocessed.
     * @return true if there is a next line in the projects file, false otherwise
     */
    public boolean hasNext()
    {
        try {
            line_=br_.readLine();
            
            if (line_==null) {
                return false;
            }            
            
            if (line_.startsWith("#")) {
                hasNext();
            }
        }
        catch (IOException e) {
            logger_.fatal("failed to read next line from the projects list file:/n"+ MyClassUtils.getStackTraceTabbed(e));
            System.exit(-1);
        }       
        
        return true;
    }//hasNext
    
    
    /**
     * Returns the next line of the projects file unprocessed; the line has already been retrived
     * by {@link #hasNext()}.
     * @return the current line of the projects file
     */
    public String next()
    {
        return line_; 
    }//next
    
    
    /**
     * Returns the next line of the projects file processed; the line has already been retrieved
     * by {@link #hasNext()}.
     * @return the string array representation of current line of the projects file
     */
    public String[] nextProcessed()
    {
        //return the array representation of a line of the form
        //"0,KDE,KDE,2007.1.1,www.kde.org,www.kde.org"
        return line_.split(",");
    }//nextProcessed
    
    
//    //sample usage
//    public static void main(String[] args)
//    {
//        ProjectParser pp=new  ProjectParser("C:/Documents and Settings/paxosk/Desktop/projects/SQOSS/src/resources/all.properties");
//        
//        while (pp.hasNext())
//        {
//            System.err.println(Arrays.toString(pp.nextProcessed()));
//        }
//    }
}
