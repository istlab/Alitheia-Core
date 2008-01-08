package paxosk.log.loggers;

import java.io.IOException;
import java.util.*;
import java.io.*;
import org.apache.log4j.*;
import org.apache.commons.configuration.*;
import paxosk.classes.common.MyClassUtils;

/**
 * Wraper class around the log4j logging system; contains file and console logger
 * instantiation methods, which instantite loggers with default logging of level DEBUG
 */
public class Log4jInstantiator 
{    
    //the pattern1 for the logging output
    private String pattern2="%r %t %C %t %d(ISO8601) %t %l %t %m %n";
    //the pattern2 for the logging output
    private String pattern1="%p %C{1} %M: \t";
    //the logging props
    private PropertiesConfiguration props_=new PropertiesConfiguration();
    //the master table, that references all the created loggers; key the logger 
    //name and value the logger itself
    private Hashtable<String,Logger> hLoggers_=new Hashtable<String,Logger>();
    //the default logging level of all the loggers, that are to be instantiated
    private org.apache.log4j.Level defaultLevel_=org.apache.log4j.Level.WARN;
    
    
    /**
     * Constructor; loads the logging properties
     */
    public Log4jInstantiator()
    {
//        try {
//            props_.load(new FileReader("C:/Documents and Settings/paxosk/Desktop/projects/SQOSS/src/paxosk/sqoss/resources/all.properties"));
//        } catch (FileNotFoundException ex) {
//            System.err.println("LoggerInstantiator: failed to load props\n"+MyClassUtils.getStackTraceTabbed(ex));
//        } catch (IOException ex) {
//            System.err.println("LoggerInstantiator: failed to load props\n"+MyClassUtils.getStackTraceTabbed(ex));
//        } catch (ConfigurationException ex) {
//            System.err.println("LoggerInstantiator: props configuration exception\n"+MyClassUtils.getStackTraceTabbed(ex));
//        }
//        
//        
//        defaultLevel_=Level.toLevel((String) props_.getProperty("LOG_DEFAULT_LEVEL"));
    }//Log4jInstantiator
    
    
    /**
     * Instantiates a new console logger, named after the calling class name,
     * adds it to the master table
     * @return the initialized console logger
     */
    public Logger initConsoleAppenderLogger()
    {
        String lName=getCallerClassName();        
        Logger l=initConsoleAppenderLogger(lName);
        
        return  l;
    }//initConsoleAppenderLogger
    
    
    /**
     * Instantiates a new console logger, adds it to the master table
     * @param loggerName the name of the logger
     * @return the initialized console logger
     */   
    public Logger initConsoleAppenderLogger(String loggerName)
    {
        Logger logger=Logger.getLogger(loggerName);        
        PatternLayout layout=new PatternLayout(pattern1);
        ConsoleAppender conAppender=new ConsoleAppender(layout);
        logger.addAppender(conAppender); 
        logger.setLevel(defaultLevel_);
        hLoggers_.put(loggerName,logger);
        
        return logger;
    }//initConsoleAppenderLogger
    
    
    /**
     * Instantiates a new file logger, named after calling class name, adds it to
     * the master table
     * @param filename the name of the file the logging data will be appended
     * @return the initialized file logger
     */
    public Logger initFileAppenderLogger(String filename)
    {                
        String lName=getCallerClassName();        
        return initFileAppenderLogger(lName,filename);
    }//initFileAppender
    
    
    /**
     * Instantiates a new file logger, adds it to the master table
     * @param loggerName the name of the logger
     * @param filename the name of the logger file
     * @return the initialized file logger
     */   
    public Logger initFileAppenderLogger(String loggerName,String filename)
    {
        Logger logger=Logger.getLogger(loggerName);        
        PatternLayout layout=new PatternLayout(pattern1);
        FileAppender fileAppender=null;
        
        //if the file doesn't already exist, create it
        File f=new File(filename);
        try {
            f.createNewFile();       
        } catch (IOException ex) {
            System.err.println("Failed to create logfile: "+filename+'\n'+
                    paxosk.classes.common.MyClassUtils.getStackTrace(ex));
        }             
        
        try {
            fileAppender = new FileAppender(layout, filename);
        } catch (IOException ex) {
            System.err.println("Failed to open logfile for append: "+filename+'\n'+
                    MyClassUtils.getStackTrace(ex));
        }
        
  
        
        logger.addAppender(fileAppender); 
        logger.setLevel(defaultLevel_);
        hLoggers_.put(loggerName,logger);
    
        return logger;        
    }//initFileAppender
    
    
 
    /**
     * Appends a console logger to the given file logger
     * @param fileLogger the file logger the console will be appended to
     */
    public void addConsole2FileAppender(Logger fileLogger)
    {
        PatternLayout layout=new PatternLayout(pattern1);
        ConsoleAppender conAppender=new ConsoleAppender(layout);        
        fileLogger.addAppender(conAppender);
    }//addConsole2FileAppender
    
    
    
    /**
     * Appends a file logger to the given console logger
     * @param conLogger the console logger the file will be appended to
     * @param filename the name of the file logger, that will appended to 
     *      the console
     */
    public void addFile2ConsoleAppender(Logger conLogger,String filename)
    {
        PatternLayout layout=new PatternLayout(pattern1);
        ConsoleAppender conAppender=new ConsoleAppender(layout,filename);            
        conLogger.addAppender(conAppender);
    }//addFile2ConsoleAppender
    
    
    
    /**
     * @return the master logger referencing table
     */
    public Hashtable<String,Logger> getLoggerTable()
    {
        return hLoggers_;
    }//getLoggerTable        
    
    ////////////////////////////////////HELPERS/////////////////////////////////////////
    public String getCallerClassName()
    {
        return new Exception().getStackTrace()[2].getClassName();        
    }//getCallerClassName    
    ////////////////////////////////////HELPERS/////////////////////////////////////////
}
