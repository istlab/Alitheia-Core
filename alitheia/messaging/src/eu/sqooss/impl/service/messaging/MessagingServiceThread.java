package eu.sqooss.impl.service.messaging;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import eu.sqooss.impl.service.messaging.senders.smtp.SMTPSender;
import eu.sqooss.service.messaging.Message;
import eu.sqooss.service.messaging.sender.MessageSender;

public class MessagingServiceThread implements Runnable {

  public static int threadFactor = 10;
  
  private MessagingServiceImpl messagingService;
  private MessageQueue queue;
  private SMTPSender defaultSender;
  private MessageSender sender;
  private boolean isStopped;
  private long queueringTime;
  private BundleContext bc;
  private ServiceReference sRef;
  
  public MessagingServiceThread(MessagingServiceImpl messagingService, MessageQueue queue,
                                SMTPSender defaultSender, BundleContext bc, long queringTime) {
    this.messagingService = messagingService;
    this.queue = queue;
    this.bc = bc;
    this.defaultSender = defaultSender;
    this.queueringTime = queringTime;
    isStopped = false;
  }
  
  public void run() {
    MessageImpl message;
    int messageStatus;
    while (!isStopped) {
      message = queue.pop();
      if (message == null) {
        return;
      }
      messagingService.startThreadIfNeeded();
      sender = getMessageSender(message);
      messageStatus = sender.sendMessage(message);
      ungetMessageSender();
      message.setStatus(messageStatus);
      messagingService.notifyListeners(message, messageStatus);
      
      boolean timeout = ((message.getQueueTime() + queueringTime) < System.currentTimeMillis());
      if ((messageStatus == Message.STATUS_FAILED) && !timeout) {
        queue.push(message);
      }
      messagingService.stopThreadIfNeeded(this);
    }
  }

  public void stop(boolean stopService) {
    isStopped = true;
    if (stopService && ((sender == null) || (sender == defaultSender))) {
      defaultSender.stopService();
    }
  }

  public void setQueueringTime(long queueringTime) {
    this.queueringTime = queueringTime;
  }
  
  private MessageSender getMessageSender(Message message) {
    String messageProtocol = message.getProtocol();
    String filter = "(" + MessageSender.PROTOCOL_PROPERTY + "=" + messageProtocol + ")";
    
    if ((messageProtocol == null) || (messageProtocol.trim().equals("")) ||
        (SMTPSender.PROTOCOL_PROPERTY_VALUE.equalsIgnoreCase(message.getProtocol().trim()))){
      sRef = null;
      return defaultSender;
    }
    try {
      sRef = bc.getServiceReferences(MessageSender.class.getName(), filter)[0];
      if (sRef == null) {
        return defaultSender;
      } else {
        return (MessageSender)bc.getService(sRef);
      }
    } catch (InvalidSyntaxException ise) {
      throw new IllegalArgumentException("Invalid message protocol string: " + message.getProtocol());
    }
  }
  
  private void ungetMessageSender() {
    if (sRef != null) {
      bc.ungetService(sRef);
    }
  }
  
}
