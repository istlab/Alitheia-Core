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

import java.util.Vector;

import eu.sqooss.impl.service.messaging.MessageImpl;

/**
 * The <code>Message</code> abstract class represents a message that can be
 * sent through an object instance of a <code>MessagingService</code>
 * implementation class to the specified recipients.<br/><br/>
 * 
 * The <code>MessagingService</code> supports only <code>Messages</code>
 * created by calling the <code>getInstance</code> method of the
 * implementation class <code>MessageImpl</code>.<br/><br/>
 * 
 * This class is not intended to be sub-classed.
 */
public abstract class Message {

    /**
     * Delivery flags - The message is in the queue.
     */
    public static final int STATUS_QUEUED = 1;

    /**
     * Delivery flags - The message was sent successfully.
     */
    public static final int STATUS_SENT   = 2;

    /**
     * Delivery flags - The sending of the message has failed.
     */
    public static final int STATUS_FAILED = 3;

    /**
     * Delivery flags - The message is new and is not being processed by a
     * <code>MessagingService</code>.
     */
    public static final int STATUS_NEW    = 0;

    /**
     * Gets the current content of the message's body.
     * 
     * @return the body of this message.
     */
    public abstract String getBody();

    /**
     * Sets the body content of this messsage.
     * 
     * @param body the message's body
     * 
     * @exception NullPointerException - if <code>body</code> is null
     * @exception IllegalArgumentException - if <code>body</code> is empty
     */
    public abstract void setBody(String body);

    /**
     * Gets the list of recipient addresses of this message.
     * 
     * @return the current recipients list
     */
    public abstract Vector<String> getRecipients();

    /**
     * Sets the list of recipient addresses of this message.<br/>
     * Note: The old list of recipients is replaced.
     * 
     * @param recipients
     * 
     * @exception NullPointerException - when the <code>recipients</code>
     * parameter is <code>null</code> or contains one or more
     * <code>null</code> values
     * @exception IllegalArgumentException - when the <code>recipients</code>
     * parameter contains an empty list
     */
    public abstract void setRecipients(Vector<String> recipients);

    /**
     * Gets the title (i.e. subject line) of that message.
     * 
     * @return the title text
     */
    public abstract String getTitle();

    /**
     * Sets the title (i.e. subject line) of this message.
     * 
     * @param title a title text
     * 
     * @exception NullPointerException - if <code>title</code> is null
     * @exception IllegalArgumentException - if <code>title</code> is empty
     */
    public abstract void setTitle(String title);

    /**
     * Returns the ID (unique identifier) of this message.<br/>
     * Note: During <code>Message</code> creation, the ID's value is set to 0.
     * After the message is queued into a <code>MessagingService</code>,
     * it is assigned an unique identifier by that
     * <code>MessagingService</code>.
     * 
     * @return the message's unique identifier
     */
    public abstract long getId();

    /**
     * Returns the current status of this message. Newly created messsages are
     * always put in a <code>Message.STATUS_NEW</code> state. The messages
     * status is automatically modified by the <code>MessagingService</code>
     * that handles this message. A message can be in one of the following
     * states:
     * <ul>
     *  <li><code>Message.STATUS_QUEUED</code> - the message is in the queue
     *  <li><code>Message.STATUS_SENT</code> - the message was sent
     *  <li><code>Message.STATUS_FAILED</code> - the sending of the message
     *    has failed
     *  <li><code>Message.STATUS_NEW</code> - The message is new and is not
     *  being processed by a <code>MessagingService</code>
     * </ul>
     * 
     * @return the current status if this message
     */
    public abstract int getStatus();

    /**
     * Returns the signature (e.g. class name of a <code>MessageSender</code>
     * implementation) of the messaging protocol, that will be used for
     * sending this message.
     * 
     * @return the messaging protocol's signature (e.g. class name)
     */
    public abstract String getProtocol();

    /**
     * Sets the the messaging protocol, that will be used for sending this
     * message. The <code>MessagingService</code> will use SMTP as default
     * transport, in case a messaging protocol wasn't set.<br/>
     * The <code>MessagingService</code> will use the specified
     * <code>MessageSender</code> to send the message, when an OSGi service 
     * providing such sender is found, or fallback to the 
     * <code>SMTPSender</code> service if not.
     * 
     * @param protocol a messaging protocol signature (e.g. class name)
     */
    public abstract void setProtocol(String protocol);

    /**
     * Creates a new message with body, recipients, title and protocol.
     * The message identifier and status are set to their initial values 0 and
     * <code>Message.STATUS_NEW</code> respectively.<br/>
     * The <code>MessagingService</code> works only with <code>Messages</code>
     * created by calling this method.
     * 
     * @param body the message's body
     * @param recipients the message's recipients
     * @param title the message's title
     * @param protocol the message's protocol signature
     * @return a new message
     * 
     * @exception NullPointerException:
     * <ul>
     *  <li>when <code>body</code> is null
     *  <li>when <code>title</code> is null
     *  <li>when <code>recipients</code> parameter is <code>null</code>,
     *    or contains a <code>null</code> value
     * </ul>
     * @exception IllegalArgumentException:
     * <ul>
     * <li>when the <code>body</code> parameter is empty
     * <li>when the <code>title</code> parameter is empty
     * <li>when the <code>recipients</code> parameter is empty
     * </ul>   
     */
    public static Message getInstance(
            String body,
            Vector<String> recipients,
            String title,
            String protocol) {
        // Create and return a new message
        Message newMessage =
            new MessageImpl(body, recipients, title, protocol);
        return newMessage;
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
