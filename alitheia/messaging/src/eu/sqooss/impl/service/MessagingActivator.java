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
import org.osgi.framework.ServiceRegistration;

import eu.sqooss.impl.service.messaging.MessagingConstants;
import eu.sqooss.impl.service.messaging.MessagingServiceImpl;
import eu.sqooss.impl.service.messaging.senders.smtp.SMTPSender;
import eu.sqooss.service.messaging.MessagingService;
import eu.sqooss.service.messaging.sender.MessageSender;

/**
 * The <code>MessagingActivator</code> class registers the messaging service and SMTP sender service. 
 */
public class MessagingActivator implements BundleActivator {
  
  private MessagingServiceImpl messagingService;
  private ServiceRegistration sRegMessagingService;
  private ServiceRegistration sRegSMTPSenderService;
  
  public void start(BundleContext bc) throws Exception {
    //registers SMTP SenderService
    SMTPSender smtpSender = new SMTPSender(bc);
    Properties serviceProps = new Properties();
    serviceProps.setProperty(MessageSender.PROTOCOL_PROPERTY, SMTPSender.PROTOCOL_PROPERTY_VALUE);
    sRegSMTPSenderService = bc.registerService(MessageSender.class.getName(), smtpSender, serviceProps);
    
    //registers messaging service
    long id = readId(bc);
    messagingService = new MessagingServiceImpl(id, bc, smtpSender);
    sRegMessagingService = bc.registerService(MessagingService.class.getName(), messagingService, null);
  }

  public void stop(BundleContext bc) throws Exception {
    writeId(bc, messagingService.stopService());
    sRegSMTPSenderService.unregister();
    sRegMessagingService.unregister();
  }

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
  
}
