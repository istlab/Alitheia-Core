package eu.sqooss.impl.service.messaging;

public class MessagingConstants {

  //persistent storage for message id
  public static final String FILE_NAME_MESSAGE_ID = "id.storage";
  public static final String FILE_NAME_PROPERTIES = "messaging.properties";
  
  //property keys
  public static final String KEY_QUEUERING_TIME     = "queuering.time";
  public static final String KEY_MAX_THREADS_NUMBER = "max.threads.number";
  public static final String KEY_SMTP_HOST          = "smtp.host";
  public static final String KEY_SMTP_PORT          = "smtp.port";
  public static final String KEY_SMTP_USER          = "smtp.user";
  public static final String KEY_SMTP_PASS          = "smtp.pass";
  public static final String KEY_SMTP_REPLY         = "smtp.reply";
  public static final String KEY_SMTP_TIMEOUT       = "smtp.timeout";
  
  //threads factor = number of messages for one thread
  public static final String KEY_THREAD_FACTOR     = "thread.factor";
  
  public static final String[] KEYS = {KEY_QUEUERING_TIME,
                                       KEY_MAX_THREADS_NUMBER,
                                       KEY_SMTP_HOST,
                                       KEY_SMTP_PORT,
                                       KEY_SMTP_USER,
                                       KEY_SMTP_PASS,
                                       KEY_SMTP_REPLY,
                                       KEY_SMTP_TIMEOUT,
                                       KEY_THREAD_FACTOR};
  
}
