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

package eu.sqooss.impl.service.messaging;

import java.util.Vector;

import eu.sqooss.impl.service.messaging.timer.TimerListener;
import eu.sqooss.service.messaging.Message;

/**
 * This class extends the <code>Message</code> class and implements the <code>TimerListener</code> interface.
 * The messages can be stored for a configurable amount of time. 
 */
public class MessageImpl extends Message implements TimerListener {

    private int status;
    private long id;
    private long queueTime;

    private String body;
    private Vector<String> recipients;
    private String title;
    private String protocol;
    private MessageHistory messageHistory;

    public MessageImpl(String body, Vector<String> recipients, String title, String protocol) {
        setBody(body);
        setRecipients(recipients);
        setTitle(title);
        setProtocol(protocol);

        this.status = STATUS_NEW;
        this.id = 0;
    }

    /**
     * @see eu.sqooss.service.messaging.Message#getBody()
     */
    public String getBody() {
        return body;
    }

    /**
     * @see eu.sqooss.service.messaging.Message#getId()
     */
    public long getId() {
        return id;
    }

    /**
     * @see eu.sqooss.service.messaging.Message#getProtocol()
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * @see eu.sqooss.service.messaging.Message#getRecipients()
     */
    public Vector<String> getRecipients() {
        return recipients;
    }

    /**
     * @see eu.sqooss.service.messaging.Message#getStatus()
     */
    public int getStatus() {
        return status;
    }

    /**
     * @see eu.sqooss.service.messaging.Message#getTitle()
     */
    public String getTitle() {
        return title;
    }

    /**
     * @see eu.sqooss.service.messaging.Message#getBody()
     */
    public void setBody(String body) {
        if (body == null) {
            throw new NullPointerException("The message's body is null!");
        }
        if (body.trim().equals("")) {
            throw new IllegalArgumentException("The message's body is empty!");
        }
        this.body = body;
    }

    /**
     * @see eu.sqooss.service.messaging.Message#setProtocol(java.lang.String)
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * @see eu.sqooss.service.messaging.Message#setRecipients(java.util.Vector)
     */
    public void setRecipients(Vector<String> recipients) {
        if (recipients == null) {
            throw new NullPointerException("The recipients vector is null!");
        }
        if (recipients.contains(null)) {
            throw new NullPointerException("The recipients vector contains null recipient!");
        }
        if (recipients.size() == 0) {
            throw new IllegalArgumentException("The recipients vector is empty!");
        }
        this.recipients = recipients;
    }

    /**
     * @see eu.sqooss.service.messaging.Message#setTitle(java.lang.String)
     */
    public void setTitle(String title) {
        if (title == null) {
            throw new NullPointerException("The message's title is null!");
        }
        if (title.trim().equals("")) {
            throw new IllegalArgumentException("The message's title is empty!");
        }
        this.title = title;
    }

    /**
     * Sets the message status. The message status can be:
     * <ul>
     * <li> <code>Message.STATUS_SENT</code>
     * <li> <code>Message.STATUS_QUEUED</code>
     * <li> <code>Message.STATUS_FAILED</code>
     * </ul>
     * 
     * @param status
     * 
     * @exception IllegalArgumentException - if the message status is not correct
     */
    public void setStatus(int status) {
        if ((status != Message.STATUS_SENT) && (status != Message.STATUS_QUEUED) && 
            (status != Message.STATUS_FAILED)) {
            throw new IllegalArgumentException("Invalid message status: " + status);
        }
        this.status = status;
    }

    /**
     * Sets the message identifier.
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Sets the queuering time of the message.
     * @param time
     */
    public void setQueueTime(long time) {
        this.queueTime = time;
    }

    /**
     * @return the queuering time of the message
     */
    public long getQueueTime() {
        return queueTime;
    }

    /**
     * Two <code>MessageImpl</code> objects are equal if their identifiers are equal.
     * If some of the identifiers is 0 then references are compared.
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof MessageImpl)) {
            return false;
        }
        MessageImpl message = (MessageImpl)obj;
        if ((this.id == 0) || (message.id == 0)){
            return this == message;
        }
        return this.id == message.id;
    }

    public int hashCode() {
        if (id == 0) {
            return super.hashCode();
        } else {
            return new Long(id).hashCode();
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer strRepresentation = new StringBuffer();
        strRepresentation.append("id: ");
        strRepresentation.append(id);
        strRepresentation.append("; recipients: ");
        strRepresentation.append(recipients.toString());
        strRepresentation.append("; status: ");
        strRepresentation.append(status);
        strRepresentation.append("; protocol: ");
        strRepresentation.append(protocol);
        strRepresentation.append("; title: ");
        strRepresentation.append(title);
        strRepresentation.append("; message body: ");
        strRepresentation.append(body);
        return strRepresentation.toString();
    }
    
    /* Message history methods */
    public void setMessageHistory(MessageHistory messageHistory) {
        this.messageHistory = messageHistory;
    }

    /**
     * @see eu.sqooss.impl.service.messaging.timer.TimerListener#timer()
     */
    public void timer() {
        if (messageHistory != null) {
            messageHistory.removeMessage(id);
        }
    }

    /* Message history methods */

}

//vi: ai nosi sw=4 ts=4 expandtab
