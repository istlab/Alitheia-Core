package eu.sqooss.impl.service.logging.utils;

/**
 * These constants are of use for logger configuration.
 */
public class LogConfigurationConstants {
  
  public static final String KEY_FILE_NAME       = "file.name";
  public static final String KEY_MESSAGE_FORMAT  = "message.format";
  public static final String KEY_ROTATION_FILE_NUMBER = "rotation.file.number";
  public static final String KEY_ROTATION_FILE_LENGTH = "rotation.file.length";
  
  public static final String[] KEYS = {KEY_MESSAGE_FORMAT,
                                       KEY_FILE_NAME,
                                       KEY_ROTATION_FILE_NUMBER,
                                       KEY_ROTATION_FILE_LENGTH};
  
  public static final String MESSAGE_FORMAT_TEXT_XML   = "text/xml";
  public static final String MESSAGE_FORMAT_TEXT_PLAIN = "text/plain";
  
  public static final String FILE_EXTENSION            = ".log";
  public static final String FILE_EXTENSION_TEXT_PLAIN = ".txt";
  public static final String FILE_EXTENSION_TEXT_XML   = ".xml";
}
