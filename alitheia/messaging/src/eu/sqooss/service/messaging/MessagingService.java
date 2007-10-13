package eu.sqooss.service.messaging;

/**
 * The messaging service is used to send messages.
 */
public interface MessagingService {
  
  /**
   * Adds the listener to the collection of listeners who will be notified when the message's status is modified.
   * @param listener a message listener
   */ 
  public void addMessageListener(MessageListener listener);
  
  /**
   * Removes the listener from the collection of listeners who will be notified when the message's status is modified. 
   * @param listener the listener
   * @return true if the listener was in the collection of listeners; false otherwise.
   */
  public boolean removeMessageListener(MessageListener listener);
  
  /**
   * Sets a message's status, unique identifier and sends to the recipients. 
   * If there is no Sender service for a message protocol or protocol is SMTP, then the message will be send as an e-mail.  
   * @param message the message for sending
   */
  public void sendMessage(Message message);
  
  /**
   * Sets the service property indicated by the specified key.
   * @param key the name of the service property
   * @param value the value of the service property
   */
  public void setConfigurationProperty(String key, String value);
  
  /**
   * Gets the service property indicated by the key.
   * @param key the name of the service property
   * @return the value of the service property; <code>null</code> if there is no property with that key.
   */
  public String getConfigurationProperty(String key);
  
  /**
   * Returns a string array of the configuration keys.
   * @return the configuration keys
   */
  public String[] getConfigurationKeys();
  
  /**
   * Gets the message indicated by the id.
   * @param id the unique identifier of the message
   * @return the message instance
   */
  public Message getMessage(long id);
}
