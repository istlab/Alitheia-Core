package eu.sqooss.service.messaging;

/**
 * The messaging service is used to send messages.
 */
public interface MessagingService {
  
  /**
   * Adds the listener to the collection of listeners, which will be notified when the message's status is modified.
   * @param listener a message listener
   */ 
  public void addMessageListener(MessageListener listener);
  
  /**
   * Removes the listener from the collection of listeners, which will be notified when the message's status is modified. 
   * @param listener the listener
   * @return true if the listener was in the collection of listeners; false otherwise.
   */
  public boolean removeMessageListener(MessageListener listener);
  
  /**
   * Sets a message's status, unique identifier and sends the message to the recipients. 
   * If there is no Sender service for a message's protocol or protocol is SMTP, then the message will be send as an e-mail.
   *   
   * @param message the message for sending
   * 
   * @exception NullPointerException - if the message is null
   * @exception IllegalArgumentException - if the message isn't created with the <code>Message.getInstance()</code> method!
   */
  public void sendMessage(Message message);
  
  /**
   * Sets the service property indicated by the specified key.
   * The <code>getConfigurationKeys</code> methods returns the available keys.
   * The configuration settings are read from messaging.properties (the configuration file is the properties file see java.util.Properties).
   * If the configuration file doesn't exist then the default configuration settings are used:
   * <ul>
   * <li> queuering.time - 60*1000 milliseconds
   * <li> max.threads.number - 10
   * <li> message.preserving.time - 0
   * <li> smtp.host - localhost
   * <li> smtp.port - 25
   * <li> smtp.user - must be set
   * <li> smtp.pass - must be set
   * <li> smtp.reply - must be set
   * <li> smtp.timeout - 2*60*1000 milliseconds
   * <li> thread.factor - 10
   * </ul>
   * 
   * @param key the name of the service property
   * The available keys are:
   * <ul>
   * <li> queuering.time - sets the message queuering time i.e. how long can the message stay in the queue
   * <li> max.threads.number - sets the number if the messaging threads (these threads send the messages)
   * <li> message.preserving.time - sets the preserving time of the message i.e how long can the message stay in the history
   * <li> smtp.host - sets the SMTP server
   * <li> smtp.port - sets the SMTP port
   * <li> smtp.user - sets the SMTP user's e-mail address
   * <li> smtp.pass - sets the user's password
   * <li> smtp.reply - sets the SMTP reply e-mail address
   * <li> smtp.timeout - sets the time for the sending of the message
   * <li> thread.factor - a messaging thread is responsible for a number of messages;
   * this number is the thread.factor value
   * </ul>
   * 
   * @param value the value of the service property
   * The correct values are:
   * <ul>
   * <li> queuering.time, message.preserving.time and smtp.timeout - a valid long
   * <li> max.threads.number and thread.factor - a valid integer
   * </ul>
   * 
   * @exception IllegalArgumentException - if the value of the property isn't correct
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
   * Gets the message from the message history.
   * @param id the unique identifier of the message
   * @return the message instance
   */
  public Message getMessage(long id);
}
