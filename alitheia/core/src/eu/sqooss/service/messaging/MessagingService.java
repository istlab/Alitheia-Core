/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package eu.sqooss.service.messaging;

/**
 * The <code>MessagingService</code> is used for sending messages to selected
 * recipients. Each message has to be encapsulated into a <code>Message</code>
 * object's instance (see eu.sqooss.service.messaging.Message) and put for
 * delivery by calling the <code>sendMessage</code> method of
 * <code>MessagingService</code>.<br/>
 * 
 * <p><b>Configuration</b></p>
 * During creation, each <code>MessagingService</code> instance is configured
 * with a set of predefined configuration settings that specify various
 * features in the object's logic. Those setting can be modified later on, by
 * calling the <code>setConfigurationProperty</code> method.
 * The <code>getConfigurationKeys</code> method provided in a
 * <code>MesssagingService</code> instance, can be used for retrieving the set
 * of predefined configuration settings for that instance.<br/>
 * The configuration settings (prefixed by <i>eu.sqooss.messaging</i>) are
 * initially read from a file named <i>config.ini</i> located in the
 * framework's configuration folder. The configuration file itself uses the
 * Java properties file format (see java.util.Properties).<br/><br/>
 * 
 * The messaging service support the following configuration properties.
 * <ul>
 *   <li> <code>queueing.time</code> - sets the message queueing time
 *     i.e. how long the message can stay in the send queue, before being
 *     released as undeliverable.
 *   <li> <code>max.threads.number</code> - sets the number of simultaneously
 *     working messaging threads in the send queue.
 *   <li> <code>thread.factor</code> - single messaging thread can serve up to
 *     <code>thread.factor</code> number of messages at any time.
 *   <li> <code>message.preserving.time</code> - defines how long can a
 *     message be kept in the messages history store.
 *   <li> <code>smtp.host</code> - sets the SMTP server's host name
 *   <li> <code>smtp.port</code> - sets the SMTP server's port number
 *   <li> <code>smtp.user</code> - sets the user name required for
 *     authentication with the selected SMTP server (<i>if required</i>)
 *   <li> <code>smtp.pass</code> - sets the password required for
 *     authentication with the selected SMTP server (<i>if required</i>)
 *   <li> <code>smtp.reply</code> - sets the email address used in the SMTP
 *     reply field
 *   <li> <code>smtp.timeout</code> - sets the time for the sending of the
 *     message
 * </ul>
 * The values of the various configuration properties must be in the following
 * formats:
 * <ul>
 *   <li> queueing.time, message.preserving.time and smtp.timeout
 *     - a valid <code>long</code>
 *   <li> max.threads.number and thread.factor
 *     - a valid <code>integer</code>
 *   <li> smtp.host, smtp.user, smtp.pass, smtp.reply
 *     - a valid <code>String</code>
 * </ul>
 * If the configuration file doesn't exist or doesn't specify some of the
 * required settings, then a number of default configuration values are
 * used:
 * <ul>
 *   <li> <code>queueing.time</code> - 60*1000 milliseconds
 *   <li> <code>max.threads.number</code> - 10
 *   <li> <code>message.preserving.time</code> - 0
 *   <li> <code>smtp.host</code> - localhost
 *   <li> <code>smtp.port</code> - 25
 *   <li> <code>smtp.timeout</code> - 2*60*1000 milliseconds
 *   <li> <code>thread.factor</code> - 10
 * </ul>
 * Some of the configuration properties must be explicitly specified in the
 * configuration file, since they can not be initialised with a default value:
 * <ul>
 *   <li> <code>smtp.user</code> - must be set from the configuration file
 *   <li> <code>smtp.pass</code> - must be set from the configuration file
 *   <li> <code>smtp.reply</code> - must be set from the configuration file
 * </ul>
 * 
 * <p><b>Using listeners</b></p>
 * The <code>MessagingService</code> provides mechanisms for tracking the
 * message delivery. For this goal the client must attach its own
 * <code>MessageListener</code> listener
 * (see eu.sqooss.service.messaging.MessageListener). Each attached listener
 * gets notified on the following events:
 * <ul>
 *   <li> when a <code>Message</code> is queued for delivery
 *   <li> when a <code>Message</code> has been successfully sent
 *   <li> when a <code>Message</code> delivery has failed
 * </ul>
 * 
 * <p><b>Messages history</b></p>
 * Each <code>MessagingService<code> instance keeps a history of all messages
 * that went through it, for later observation. For retrieving a message from
 * the history store the client must know the ID of that message, and then
 * call the <code>getMessage</code> method with that message ID. The behavior
 * of the history store is configurable i.e. the creator of the
 * <code>MessagingService</code> can specify for example how long a single
 * message should should be kept, before being disposed.
 */
public interface MessagingService {
    
    /**
     * Adds the provided listener to the collection of listeners, that will
     * be notified when the matching message's status is modified.
     * 
     * @param listener a message listener
     */ 
    public void addMessageListener(MessageListener listener);

    /**
     * Removes the selected listener from the collection of listeners, that
     * will be notified when the matching message's status is modified.
     * 
     * @param listener the message listener
     * @return true if the listener was in the collection of listeners;
     * false otherwise.
     */
    public boolean removeMessageListener(MessageListener listener);

    /**
     * This method queues the given message for sending. It sets a message's
     * status, an unique identifier, and finally tries to send the message to
     * the specified recipients.<br/>
     * If there is no <code>Sender</code> service for the specified messaging
     * protocol, or the protocol is SMTP, then this message will be send as
     * an e-mail.
     * 
     * @param message the message that has to be sent
     * 
     * @exception NullPointerException - if the <code>Message<code/> is null
     * @exception IllegalArgumentException - if the <code>Message<code> isn't
     * created with the <code>Message.getInstance()</code> method
     */
    public void sendMessage(Message message);

    /**
     * Sets the configuration property indicated by the specified key.
     * 
     * @param key the name of the configuration property
     * @param value the value of the configuration property
     * 
     * @exception IllegalArgumentException - if the value of the property isn't correct
     */
    public void setConfigurationProperty(String key, String value);

    /**
     * Gets the value of the configuration property, indicated by the given
     * key name.
     * 
     * @param key the name of the configuration property
     * @return the value of the configuration property, or <code>null</code>
     * if there is no property with that name
     */
    public String getConfigurationProperty(String key);

    /**
     * Returns a string array containing the names of all configuration
     * properties defined for this <code>MessagingService<code>'s instance.
     * @return the configuration keys
     */
    public String[] getConfigurationKeys();

    /**
     * Retrieves the message with the given ID from the message history store.
     * 
     * @param id the unique message identifier
     * @return a <code>Message</code> instance
     */
    public Message getMessage(long id);
}

//vi: ai nosi sw=4 ts=4 expandtab
