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
 * The messaging service is used to send messages.
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
     * Sets the service's configuration property indicated by the specified
     * key.<br/>
     * The <code>getConfigurationKeys</code> methods, can be used for
     * retrieving the set of available keys.<br/>
     * The configuration settings are initially read from a file named
     * <b>messaging.properties</b> located in the framework's configuration
     * folder. The configuration file uses the Java properties file format
     * (see java.util.Properties).<br/>
     * If the configuration file doesn't exist, then a number of default
     * configuration values are used:
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

//vi: ai nosi sw=4 ts=4 expandtab
