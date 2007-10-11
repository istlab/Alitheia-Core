package eu.sqooss.services.messaging;

/**
 * Classes which implement this interface provide methods that deal with the changes of the the message status. 
 */
public interface MessageListener {
  
  /**
   * Sent when the message is queued. 
   * @param message The messages are compared with a equals method.
   */
  public void messageQueued(Message message);
  
  /**
   * Sent when the message is sent.
   * @param message The messages are compared with a equals method.
   */
  public void messageSent(Message message);
  
  /**
   * Sent when the message's sending fail.
   * @param message The messages are compared with a equals method.
   */
  public void messageFailed(Message message);
}
