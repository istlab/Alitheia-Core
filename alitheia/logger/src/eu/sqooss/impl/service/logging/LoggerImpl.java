package eu.sqooss.impl.service.logging;

import java.util.Date;
import java.util.Vector;

import eu.sqooss.impl.service.logging.utils.LogConfiguration;
import eu.sqooss.impl.service.logging.utils.LogConfigurationConstants;
import eu.sqooss.impl.service.logging.utils.LogUtils;
import eu.sqooss.impl.service.logging.utils.LogWritersManager;
import eu.sqooss.impl.service.logging.utils.LogWriter;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;

/**
 * This class implements a Logger interface.  
 */
public class LoggerImpl implements Logger {

    private Object lockObject = new Object();

    private LogConfiguration logConfig;
    private LogWriter logWriter;
    private String name;
    private String parentName;
    private String fileName;
    private String messageFormat;
    private LogWritersManager logWritersManager;
    private LogManager logManager;

    private int takingsNumber; //used from LogManager

    public LoggerImpl(String name, LogWritersManager logWritersManager, LogConfiguration logConfig, LogManager logManager) {
        this.name = name;
        this.parentName = LogUtils.getParentLoggerName(name);
        this.logConfig = logConfig;
        this.logWritersManager = logWritersManager;
        this.logManager = logManager;
        messageFormat = logConfig.getConfigurationProperty(name, LogConfigurationConstants.KEY_MESSAGE_FORMAT);
        fileName = logConfig.getConfigurationProperty(name, LogConfigurationConstants.KEY_FILE_NAME) + getFileExtension(messageFormat);
        logWriter = logWritersManager.createLogWriter(fileName);
        takingsNumber = 0;
    }

    /**
     * @see eu.sqooss.service.logging.Logger#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * @see eu.sqooss.service.logging.Logger#getConfigurationProperty(String)
     */
    public String getConfigurationProperty(String key) {
        return logConfig.getConfigurationProperty(name, key);
    }

    /**
     * @see eu.sqooss.service.logging.Logger#setConfigurationProperty(String, String)
     */
    public void setConfigurationProperty(String key, String value) {
        setConfigurationProperty(key, value, false);
    }

    /**
     * If the parent changes the configuration then the flag <code>isParentChange</code> is true. 
     * @param key
     * @param value
     * @param isParentChange
     */
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
                logWritersManager.releaseLogWriter(fileName);
                messageFormat = logConfig.getConfigurationProperty(name, LogConfigurationConstants.KEY_MESSAGE_FORMAT);
                fileName = logConfig.getConfigurationProperty(name, LogConfigurationConstants.KEY_FILE_NAME) + getFileExtension(messageFormat);
                logWriter = logWritersManager.createLogWriter(fileName);
            }
        }
    }

    /**
     * @see eu.sqooss.service.logging.Logger#config(String)
     */
    public void config(String message) {
        synchronized (lockObject) {
            logWriter.write(getMessage(message, LoggerConstants.LOGGING_LEVEL_CONFIG));
        }
    }

    /**
     * @see eu.sqooss.service.logging.Logger#info(String)
     */
    public void info(String message) {
        synchronized (lockObject) {
            logWriter.write(getMessage(message, LoggerConstants.LOGGING_LEVEL_INFO));
        }
    }

    /**
     * @see eu.sqooss.service.logging.Logger#severe(String)
     */
    public void severe(String message) {
        severe(message, null);
    }

    /**
     * Logs the message only once to the same file.  
     * @param message
     * @param loggedToFiles contains the names of the files where the message is logged 
     */
    private void severe(String message, Vector<String> loggedToFiles) {
        synchronized (lockObject) {
            if (loggedToFiles != null) {
                for (int i = 0; i < loggedToFiles.size(); i++) {
                    if (fileName.equals(loggedToFiles.get(i))) {
                        logToParent(message, loggedToFiles);
                        return; // the message is logged from the child
                    }
                }
            } else {
                loggedToFiles = new Vector<String>(1);
            }
            logWriter.write(getMessage(message, LoggerConstants.LOGGING_LEVEL_SEVERE));
            loggedToFiles.add(fileName);
            logToParent(message, loggedToFiles);
        }
    }

    /**
     * @see eu.sqooss.service.logging.Logger#warning(String)
     */
    public void warning(String message) {
        synchronized (lockObject) {
            logWriter.write(getMessage(message, LoggerConstants.LOGGING_LEVEL_WARNING));
        }
    }

    /**
     * @see eu.sqooss.service.logging.Logger#getConfigurationKeys()
     */
    public String[] getConfigurationKeys() {
        return LogConfigurationConstants.KEYS;
    }

    /**
     * This method increases the internal counter when the log manager creates or gets the logger.
     */
    protected void get() {
        takingsNumber++;
    }

    /**
     * This method decreases the internal counter when the log manager releases the logger.
     */
    protected int unget() {
        return --takingsNumber;
    }

    /**
     * This method closes the logger.
     */
    protected void close() {
        synchronized (lockObject) {
            logWritersManager.releaseLogWriter(fileName);
        }
    }

    /**
     * The file extension depends on the message's format. 
     * @param messageFormat
     * @return the file extension
     */
    private String getFileExtension(String messageFormat) {
        if (messageFormat.equals(LogConfigurationConstants.MESSAGE_FORMAT_TEXT_PLAIN)) {
            return LogConfigurationConstants.FILE_EXTENSION_TEXT_PLAIN + LogConfigurationConstants.FILE_EXTENSION;
        } else {
            return LogConfigurationConstants.FILE_EXTENSION_TEXT_XML + LogConfigurationConstants.FILE_EXTENSION;
        }
    }

    /**
     * 
     * @param messageBody
     * @param loggingLevel
     * @return the logging message 
     */
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

    /**
     * This method is used by severe logging level.
     * Logs the message to the logger's parent.
     * @param message
     */
    private void logToParent(String message, Vector<String> loggedToFiles) {
        if (!"".equals(parentName)) {
            LoggerImpl parentLogger = (LoggerImpl)logManager.createLogger(parentName);
            parentLogger.severe(message, loggedToFiles);
            logManager.releaseLogger(parentName);
        }
    }

}
