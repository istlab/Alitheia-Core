package eu.sqooss.impl.service.logging;

import java.util.Date;

import eu.sqooss.impl.service.logging.utils.LogConfiguration;
import eu.sqooss.impl.service.logging.utils.LogConfigurationConstants;
import eu.sqooss.impl.service.logging.utils.LogWritersManager;
import eu.sqooss.impl.service.logging.utils.LogWriter;
import eu.sqooss.service.logging.Logger;

public class LoggerImpl implements Logger {

  private Object lockObject = new Object();

  private LogConfiguration logConfig;
  private LogWritersManager logWritersManager;
  private LogWriter logWriter;
  private String name;
  private String fileName;
  private String messageFormat;
  private LogWritersManager logStorage;

  private int takingsNumber; //used from LogManager

  public LoggerImpl(String name, LogWritersManager logStorage, LogConfiguration logConfig) {
    this.name = name;
    this.logWritersManager = logStorage;
    this.logConfig = logConfig;
    this.logStorage = logStorage;
    messageFormat = logConfig.getConfigurationProperty(name, LogConfigurationConstants.KEY_MESSAGE_FORMAT);
    fileName = logConfig.getConfigurationProperty(name, LogConfigurationConstants.KEY_FILE_NAME) + getFileExtension(messageFormat);
    logWriter = logStorage.createLogWriter(fileName);
    takingsNumber = 0;
  }

  public String getName() {
    return name;
  }

  public String getConfigurationProperty(String key) {
    return logConfig.getConfigurationProperty(name, key);
  }

  public void setConfigurationProperty(String key, String value) {
    setConfigurationProperty(key, value, false);
  }

  public void setConfigurationProperty(String key, String value, boolean isParentChange) {
    if (!isParentChange) {
      logConfig.setConfigurationProperty(name, key, value);
      if (LogConfigurationConstants.KEY_ROTATION_FILE_LENGTH.equals(key)) {
        try {
          long maxLogFileLength = Long.parseLong(value);
          logWriter.setMaxFileLength(maxLogFileLength);
        } catch (NumberFormatException nfe) {
          throw new RuntimeException(nfe);
        }
      } else if (LogConfigurationConstants.KEY_ROTATION_FILE_NUMBER.equals(key)) {
        try {
          int maxLogFileNumber = Integer.parseInt(value);
          logWriter.setMaxLogFilesNumber(maxLogFileNumber);
        } catch (NumberFormatException nfe) {
          throw new RuntimeException(nfe);
        }
      }
    }
    if ((LogConfigurationConstants.KEY_MESSAGE_FORMAT.equals(key)) ||
        (LogConfigurationConstants.KEY_FILE_NAME.equals(key))) {
      synchronized (lockObject) {
        logStorage.releaseLogWriter(fileName);
        messageFormat = logConfig.getConfigurationProperty(name, LogConfigurationConstants.KEY_MESSAGE_FORMAT);
        fileName = logConfig.getConfigurationProperty(name, LogConfigurationConstants.KEY_FILE_NAME) + getFileExtension(messageFormat);
        logWriter = logStorage.createLogWriter(fileName);
      }
    }
  }

  public void config(String message) {
    synchronized (lockObject) {
      logWriter.write(getMessage(message, LoggerConstants.LOGGING_LEVEL_CONFIG));
    }
  }

  public void info(String message) {
    synchronized (lockObject) {
      logWriter.write(getMessage(message, LoggerConstants.LOGGING_LEVEL_INFO));
    }
  }

  public void severe(String message) {
    synchronized (lockObject) {
      logWriter.write(getMessage(message, LoggerConstants.LOGGING_LEVEL_SEVERE));
    }
  }

  public void warning(String message) {
    synchronized (lockObject) {
      logWriter.write(getMessage(message, LoggerConstants.LOGGING_LEVEL_WARNING));
    }
  }

  public String[] getConfigurationKeys() {
    return LogConfigurationConstants.KEYS;
  }

  protected void get() {
    takingsNumber++;
  }

  protected int unget() {
    return --takingsNumber;
  }

  protected void close() {
    logWritersManager.releaseLogWriter(fileName);
  }

  private String getFileExtension(String messageFormat) {
    if (messageFormat.equals(LogConfigurationConstants.MESSAGE_FORMAT_TEXT_PLAIN)) {
      return LogConfigurationConstants.FILE_EXTENSION_TEXT_PLAIN + LogConfigurationConstants.FILE_EXTENSION;
    } else {
      return LogConfigurationConstants.FILE_EXTENSION_TEXT_XML + LogConfigurationConstants.FILE_EXTENSION;
    }
  }

  private String getMessage(String messageBody, String loggingLevel) {
    String text ;
    Date timestamp = new Date();
    if (LogConfigurationConstants.MESSAGE_FORMAT_TEXT_PLAIN.equals(messageFormat)) {
      text = loggingLevel + ", " + timestamp.toString() + ", " + this.name + ", "  +messageBody + "\n";
    } else {
      text = "<log-message>\n" +
                "\t<level>" + loggingLevel + "</level>\n" +
                "\t<message>" + messageBody + "</message>\n" +
                "\t<timestamp>" + timestamp.toString() + "</timestamp>\n" +
                "\t<service>" + this.name + "</service>\n" +
             "</log-message>\n";
    }
    return text;
  }

}
