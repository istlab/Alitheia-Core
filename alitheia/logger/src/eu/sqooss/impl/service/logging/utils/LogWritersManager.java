package eu.sqooss.impl.service.logging.utils;

import java.io.File;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;

/**
 * The <code>LogWritersManager</code> is the entry point to <code>LogWriter</code>.
 */
public class LogWritersManager {
  
  private Object lockObject = new Object();
  
  private BundleContext bc;
  private Hashtable<String,LogWriter> logWritersStorage; //key - file_name, value LogWriter
  private String LOGS_DIR_NAME = "logs";
  
  public LogWritersManager(BundleContext bc) {
    this.bc = bc;
    File logsDir = bc.getDataFile(LOGS_DIR_NAME);
    if (!logsDir.exists()) {
      logsDir.mkdir();
    }
    logWritersStorage = new Hashtable<String,LogWriter>();
  }
  
  /**
   * Creates a new writer if doesn't exist, otherwise returns a existent writer.
   * @param fileName
   * @return
   */
  public LogWriter createLogWriter(String fileName) {
    synchronized (lockObject) {
      LogWriter logWriter;
      if (logWritersStorage.containsKey(fileName)) {
        logWriter = logWritersStorage.get(fileName);
      } else {
        logWriter = new LogWriter(bc.getDataFile(LOGS_DIR_NAME + File.separator + fileName));
        logWritersStorage.put(fileName, logWriter);
      }
      logWriter.get();
      return logWriter;
    }
  }
  
  /**
   * Releases the writer with specified file name.
   * @param fileName
   */
  public void releaseLogWriter(String fileName) {
    synchronized (lockObject) {
      if (logWritersStorage.containsKey(fileName)) {
        LogWriter logWriter = logWritersStorage.get(fileName);
        int takingsNumber = logWriter.unget();
        if (takingsNumber == 0) {
          logWritersStorage.remove(fileName);
          logWriter.close();
        }
      }
    }
  }
}
