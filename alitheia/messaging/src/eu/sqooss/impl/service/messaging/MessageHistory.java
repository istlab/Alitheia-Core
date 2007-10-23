package eu.sqooss.impl.service.messaging;

import java.util.Hashtable;

import eu.sqooss.impl.service.messaging.timer.Timer;
import eu.sqooss.service.messaging.Message;

/**
 * This class is used to store the messages for future reference.
 * It stores the messages for a configurable amount of time. 
 */
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
  
  /**
   * Stores a new message.
   * @param message
   */
  public void put(MessageImpl message) {
    if (preservingTime != 0) {
      synchronized (lockObject) {
        messageHistory.put(new Long(message.getId()), message);
        timer.addNotifyListener(message, preservingTime);
      }
    }
  }
  
  /**
   * Returns the stored message with specified id.
   * @param messageId
   * @return the message to which the id is mapped in this message history; null if the id is not mapped.
   */
  public Message getMessage(long messageId) {
    return (Message)messageHistory.get(new Long(messageId));
  }
  
  /**
   * Removes the message from the message history.
   * @param messageId
   * @return <code>false</code> - if the id is not mapped to the message in the message history,
   * <code>true</code> - otherwise
   */
  public boolean removeMessage(long messageId) {
    synchronized (lockObject) {
      if (messageHistory.remove(new Long(messageId)) == null) {
        return false;
      } else {
        return true;
      }
    }
  }
  
  /**
   * Clears the history.
   */
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
