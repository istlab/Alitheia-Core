package eu.sqooss.services.messaging.sender;

import eu.sqooss.services.messaging.Message;

/**
 * The services which implements this interface provide method that deal with a dispatch of the message.
 * The services must be registered with a Sender.PROTOCOL_PROPERTY property.
 * The services must be thread safe. 
 */
public interface MessageSender {
  public static final String PROTOCOL_PROPERTY = "protocol";
    
  /**
   * Sends a message and returns a message status:
   * <ul>
   *  <li><code>Message.STATUS_SENT</code> - if the message is sent successful
   *  <li><code>Message.STATUS_FAILED</code> - if the message isn't sent successful
   * </ul>
   * 
   * @param message the message for sending
   * @return message status
   */
  public int sendMessage(Message message);
}
