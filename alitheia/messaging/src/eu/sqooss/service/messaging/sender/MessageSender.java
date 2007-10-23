package eu.sqooss.service.messaging.sender;

import eu.sqooss.service.messaging.Message;

/**
 * The services which implements this interface provide method that deal with a dispatch of the message.
 * The services must be registered with a <code>MessageSender.PROTOCOL_PROPERTY</code> property.
 * <code>MessagingService</code> uses a MessageSender when the message protocol and <code>MessageSender.PROTOCOL_PROPERTY</code> value are equal.
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
   * @return message status (see the constants above)
   */
  public int sendMessage(Message message);
  
}
