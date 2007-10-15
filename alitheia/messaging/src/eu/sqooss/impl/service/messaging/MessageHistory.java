package eu.sqooss.impl.service.messaging;

import java.util.Hashtable;

import eu.sqooss.impl.service.messaging.timer.Timer;
import eu.sqooss.service.messaging.Message;

public class MessageHistory {

  private static final String MESSAGE_HISTORY_TIMER_NAME = "Message history timer ";
  
  private Object lockObject = new Object();
  
  private Timer timer;
  private Hashtable messageHistory;
  private long preservingTime;
  
  public MessageHistory(long preservingTime) {
    this.preservingTime = preservingTime;
    messageHistory = new Hashtable();
    timer = new Timer(MESSAGE_HISTORY_TIMER_NAME);
    timer.start();
  }
  
  public void put(MessageImpl message) {
    if (preservingTime != 0) {
      synchronized (lockObject) {
        messageHistory.put(new Long(message.getId()), message);
        timer.addNotifyListener(message, preservingTime);
      }
    }
  }
  
  public Message getMessage(long messageId) {
    return (Message)messageHistory.get(new Long(messageId));
  }
  
  public boolean removeMessage(long messageId) {
    synchronized (lockObject) {
      if (messageHistory.remove(new Long(messageId)) == null) {
        return false;
      } else {
        return true;
      }
    }
  }
  
  public void clear() {
    synchronized (lockObject) {
      timer.stop();
      messageHistory.clear();
    }
  }
  
  public void setPreservingTime(long preservingTime) {
    this.preservingTime = preservingTime;
  }
  
}
