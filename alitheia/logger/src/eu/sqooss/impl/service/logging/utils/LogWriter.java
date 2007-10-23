package eu.sqooss.impl.service.logging.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * The <code>LogWriter</code> instances write the log messages to the log file.
 * They are created and released from <code>LogWritersManager</code>.
 * The <code>LogWritersManager</code> is used as a singleton because different loggers can log to the same file.
 */
public class LogWriter {
  
  private long maxFileLength;
  private int maxLogFilesNumber;
  private int nextLogFileNumber;
  private Object lockObject = new Object();
  private int takingsNumber;
  private BufferedWriter writer;
  private File file;
  private long fileLength;
  
  public LogWriter(File file) {
    takingsNumber = 0;
    maxFileLength = -1;
    maxLogFilesNumber = 0;
    nextLogFileNumber = 1;
    try {
      this.file = file;
      fileLength = file.length();
      writer = new BufferedWriter(new OutputStreamWriter (new FileOutputStream(file, true)));
    } catch (FileNotFoundException fnfe) {
      throw new RuntimeException(fnfe);
    }
  }
  
  /**
   * Writes the text to the specified file.
   * @param text
   */
  public void write(String text) {
    synchronized (lockObject) {
      try {
        if ((maxFileLength != -1) && (fileLength >= maxFileLength)) {
          rotateLogFile();
        }
        writer.write(text);
        writer.flush();
        fileLength += text.getBytes().length;
      } catch (IOException ioe) {
        throw new RuntimeException(ioe);
      }
    }
  }
  
  /**
   * The rotation policy uses this method.
   * @param maxFileLength
   */
  public void setMaxFileLength(long maxFileLength) {
    this.maxFileLength = maxFileLength;
  }

  /**
   * The rotation policy uses this method.
   * @param maxLogFilesNumber
   */
  public void setMaxLogFilesNumber(int maxLogFilesNumber) {
    this.maxLogFilesNumber = maxLogFilesNumber;
  }

  /**
   * This method increases the internal counter when the <code>LogWritersManager</code> creates or gets the log writer.
   */
  protected void get() {
    takingsNumber++;
  }
  
  /**
   * This method decreases the internal counter when the <code>LogWritersManager</code> releases the log writer.
   */
  protected int unget() {
    return --takingsNumber;
  }
  
  /**
   * This method closes the <code>LogWriter</code>.
   */
  protected void close() {
    synchronized (lockObject) {
      try {
        writer.close();
        if (file.length() == 0) {
          file.delete();
        }
      } catch (IOException ioe) {
        throw new RuntimeException(ioe);
      }
    }
  }
  
  private void rotateLogFile() {
    try {
      writer.close();
      fileLength = 0;
      if ((maxLogFilesNumber != 0) && (nextLogFileNumber <= maxLogFilesNumber)) {
        writer = new BufferedWriter(new FileWriter(new File(file.getPath() + "." + nextLogFileNumber)));
        nextLogFileNumber++;
      } else {
        writer = new BufferedWriter(new FileWriter(file));
        nextLogFileNumber = 1;
      }
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }
  
}
