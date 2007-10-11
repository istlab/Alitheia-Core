package eu.sqooss.impl.service.logging.utils;

import java.io.File;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;

public class LogWritersManager {
  
  private Object lockObject = new Object();
  
  private BundleContext bc;
  private Hashtable logWritersStorage; //key - file_name, value LogWriter
  private String LOGS_DIR_NAME = "logs";
  
  public LogWritersManager(BundleContext bc) {
    this.bc = bc;
    File logsDir = bc.getDataFile(LOGS_DIR_NAME);
    if (!logsDir.exists()) {
      logsDir.mkdir();
    }
    logWritersStorage = new Hashtable();
  }
  
  public LogWriter createLogWriter(String fileName) {
    synchronized (lockObject) {
      LogWriter logWriter;
      if (logWritersStorage.containsKey(fileName)) {
        logWriter = (LogWriter)logWritersStorage.get(fileName);
      } else {
        logWriter = new LogWriter(bc.getDataFile(LOGS_DIR_NAME + File.separator + fileName));
        logWritersStorage.put(fileName, logWriter);
      }
      logWriter.get();
      return logWriter;
    }
  }
  
  public void releaseLogWriter(String fileName) {
    synchronized (lockObject) {
      if (logWritersStorage.containsKey(fileName)) {
        LogWriter logWriter = (LogWriter)logWritersStorage.get(fileName);
        int takingsNumber = logWriter.unget();
        if (takingsNumber == 0) {
          logWritersStorage.remove(fileName);
          logWriter.close();
        }
      }
    }
  }
}
