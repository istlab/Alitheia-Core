package eu.sqooss.impl.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import eu.sqooss.impl.service.messaging.MessagingConstants;
import eu.sqooss.impl.service.messaging.MessagingServiceImpl;
import eu.sqooss.impl.service.messaging.senders.smtp.SMTPSender;
import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.messaging.MessagingService;
import eu.sqooss.service.messaging.sender.MessageSender;

/**
 * The class is used to start and stop the messaging bundle.
 */
public class MessagingActivator implements BundleActivator, ServiceListener {
  
  public static final int LOGGING_INFO_LEVEL    = 0;
  public static final int LOGGING_CONFIG_LEVEL  = 1;
  public static final int LOGGING_WARNING_LEVEL = 2;
  public static final int LOGGING_SEVERE_LEVEL  = 3;
  
  private BundleContext bc;
  private MessagingServiceImpl messagingService;
  private ServiceRegistration sRegMessagingService;
  private ServiceRegistration sRegSMTPSenderService;
  private ServiceReference logManagerServiceRef;
  private LogManager logManager;
  private static Logger logger;
  
  private static Object lockObject = new Object();
  
  /**
   * Configures and registers the messaging service and SMTP sender service.
   */
  public void start(BundleContext bc) throws Exception {
    this.bc = bc;
    
    initializeLogger();
    
    String filter = "(" + Constants.OBJECTCLASS + "=" + LogManager.class.getName() + ")";
    bc.addServiceListener(this, filter);
    
    //registers SMTP SenderService
    SMTPSender smtpSender = new SMTPSender(bc);
    Properties serviceProps = new Properties();
    serviceProps.setProperty(MessageSender.PROTOCOL_PROPERTY, SMTPSender.PROTOCOL_PROPERTY_VALUE);
    sRegSMTPSenderService = bc.registerService(MessageSender.class.getName(), smtpSender, serviceProps);
    
    //registers messaging service
    long id = readId(bc);
    messagingService = new MessagingServiceImpl(id, bc, smtpSender);
    sRegMessagingService = bc.registerService(MessagingService.class.getName(), messagingService, null);
    
    MessagingActivator.log("The messaging bundle is started!", MessagingActivator.LOGGING_INFO_LEVEL);
  }

  /**
   * Writes the last message id and unregisters the services.
   */
  public void stop(BundleContext bc) throws Exception {
    bc.removeServiceListener(this);
    
    writeId(bc, messagingService.stopService());
    
    if (sRegSMTPSenderService != null) {
      sRegSMTPSenderService.unregister();
    }
    
    if (sRegMessagingService != null) {
      sRegMessagingService.unregister();
    }
    
    MessagingActivator.log("The messaging bundle is stopped!", MessagingActivator.LOGGING_INFO_LEVEL);
    
    removeLogger();
  }

  /**
   * Reads the last message id from the file.
   */
  private long readId(BundleContext bc) throws IOException {
    File idFile = bc.getDataFile(MessagingConstants.FILE_NAME_MESSAGE_ID);
    DataInputStream in = null;
    if (idFile.exists()) {
      try {
        in = new DataInputStream(new FileInputStream(idFile));
        return in.readLong();
      } finally {
        if (in != null) {
          in.close();
        }
      }
    } else {
      return 1;
    }
  }
  
  /**
   * Writes the last message id to the file.
   * @param bc
   * @param id
   * @throws IOException
   */
  private void writeId(BundleContext bc, long id) throws IOException {
    File idFile = bc.getDataFile(MessagingConstants.FILE_NAME_MESSAGE_ID);
    DataOutputStream out = null;
    try{
      out = new DataOutputStream(new FileOutputStream(idFile));
      out.writeLong(id);
    } finally {
      if (out != null) {
        out.close();
      }
    }
  }

  /**
   * This method logs the messages from the specified logging level.
   * @param message the text
   * @param level the logging level
   */
  public static void log(String message, int level) {
    synchronized (lockObject) {
      if (logger != null) {
        switch (level) {
        case MessagingActivator.LOGGING_INFO_LEVEL: logger.info(message); break;
        case MessagingActivator.LOGGING_CONFIG_LEVEL: logger.config(message); break;
        case MessagingActivator.LOGGING_WARNING_LEVEL: logger.warning(message); break;
        case MessagingActivator.LOGGING_SEVERE_LEVEL: logger.severe(message); break;
        default: logger.info(message); break;
        }
      }
    }
  }

  /**
   * @see org.osgi.framework.ServiceListener#serviceChanged(ServiceEvent)
   */
  public void serviceChanged(ServiceEvent event) {
    int eventType = event.getType();
    if ((ServiceEvent.REGISTERED == eventType) ||
        (ServiceEvent.MODIFIED == eventType)) {
      initializeLogger();
    } else if (ServiceEvent.UNREGISTERING == eventType) {
      removeLogger();
    }
  }
  
  /**
   * Gets the logger
   */
  private void initializeLogger() {
    synchronized (lockObject) {
      //gets the logger
      logManagerServiceRef = bc.getServiceReference(LogManager.class.getName());
      if (logManagerServiceRef != null) {
        logManager = (LogManager)bc.getService(logManagerServiceRef);
        logger = logManager.createLogger(Logger.NAME_SQOOSS_MESSAGING);
      }
    }
  }
  
  /**
   * Ungets the logger. 
   */
  private void removeLogger() {
    synchronized (lockObject) {
      if (logManagerServiceRef != null) {
        logManager.releaseLogger(logger.getName());
        bc.ungetService(logManagerServiceRef);
        logManagerServiceRef = null;
        logger = null;
      }
    }
  }
  
}
