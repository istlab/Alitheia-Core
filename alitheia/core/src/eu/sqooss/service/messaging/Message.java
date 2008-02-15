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
 * The <code>Message</code> class represents a message used from the messaging service.
 * The messaging service works only with the messages created from a <code>getInstance</code> method.
 * This class is not intended to be subclassed. 
 */
public abstract class Message {

    /**
     * The message is in the queue.
     */
    public static final int STATUS_QUEUED = 1;

    /**
     * The message is sent successfully.
     */
    public static final int STATUS_SENT   = 2;

    /**
     * The sending of the message is failed.
     */
    public static final int STATUS_FAILED = 3;

    /**
     * The message is new and is not processed in a messaging service.
     */
    public static final int STATUS_NEW    = 0;

    /**
     * @return returns the body of the message.
     */
    public abstract String getBody();

    /**
     * Sets a new message body.
     * @param body the message's body
     * 
     * @exception NullPointerException - if <code>body</code> is null
     * @exception IllegalArgumentException - if <code>body</code> is empty
     */
    public abstract void setBody(String body);

    /**
     * Gets the recipients of the message.
     * @return returns the recipients
     */
    public abstract Vector<String> getRecipients();

    /**
     * Sets the new message's recipients.
     * The old recipients are replaced.
     * 
     * @param recipients
     * 
     * @exception NullPointerException - if <code>recipients</code> parameter is null or contains null value
     * @exception IllegalArgumentException - if <code>recipients</code> parameter is empty
     */
    public abstract void setRecipients(Vector<String> recipients);

    /**
     * Gets the title(subject) of the message.
     * @return returns a title(subject)
     */
    public abstract String getTitle();

    /**
     * Sets a new message title(subject).
     * @param title a new message title(subject)
     * 
     * @exception NullPointerException - if <code>title</code> is null.
     * @exception IllegalArgumentException - if <code>title</code> is empty
     */
    public abstract void setTitle(String title);

    /**
     * Returns a message id. <b>The messaging service</b> assigns a unique identifier to every message,
     * before this the identifier is 0. 
     * @return returns a message unique identifier
     */
    public abstract long getId();

    /**
     * Returns a message's status. <b>The messaging service</b> sets a message status.
     * <ul>
     *  <li><code>Message.STATUS_QUEUED</code> - the message is in the queue
     *  <li><code>Message.STATUS_SENT</code> - the message is sent
     *  <li><code>Message.STATUS_FAILED</code> - the sending of the message is failed
     *  <li><code>Message.STATUS_NEW</code> - the message is new and is not processed in a messaging service 
     * </ul>
     * @return message's status
     */
    public abstract int getStatus();

    /**
     * Returns a message's protocol.
     * @return message's protocol
     */
    public abstract String getProtocol();

    /**
     * Sets a new message protocol. The messaging service uses SMTP if a message's protocol is not set.
     * <code>MessagingService</code> uses a specified MessageSender when the message protocol and <code>MessageSender.PROTOCOL_PROPERTY</code> value are equal.
     * 
     * @param protocol a message protocol used for transmission of the message
     */
    public abstract void setProtocol(String protocol);

    /**
     * Creates a new message with body, recipients, title and protocol.
     * The message identifier and status aren't set. Their values are 0 and Message.STATUS_NEW respectively. 
     * The messaging service changes this values after a message posting with <code>sendMessage</code> method.
     * The messaging service works only with the messages created from this method.
     * 
     * @param body the message's body
     * @param recipients the message's recipients
     * @param title the message's title
     * @param protocol the message's protocol
     * @return a new message
     * 
     * @exception NullPointerException:
     * <ul>
     *  <li>if <code>body</code> is null
     *  <li>if <code>title</code> is null
     *  <li>if <code>recipients</code> parameter is null or contains null value
     * </ul>
     * 
     * @exception IllegalArgumentException:
     * <ul>
     * <li>if <code>body</code> is empty
     * <li>if <code>title</code> is empty
     * <li>if <code>recipients</code> parameter is empty
     * </ul>   
     */
    public static Message getInstance(String body, Vector<String> recipients, String title, String protocol) {
        Message newMessage = new MessageImpl(body, recipients, title, protocol);
        return newMessage;
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
