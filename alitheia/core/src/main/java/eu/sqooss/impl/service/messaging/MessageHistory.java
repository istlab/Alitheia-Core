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

import java.util.Hashtable;

import eu.sqooss.impl.service.messaging.timer.Timer;
import eu.sqooss.service.messaging.Message;

/**
 * This class is used to store the messages for future reference.
 * It stores the messages for a configurable amount of time. 
 */
public class MessageHistory {

    private static final String MESSAGE_HISTORY_TIMER_NAME = "Message history timer ";

    private Object lockObject = new Object();

    private Timer timer;
    private Hashtable < Long, MessageImpl > messageHistory;
    private long preservingTime;

    public MessageHistory(long preservingTime) {
        this.preservingTime = preservingTime;
        messageHistory = new Hashtable < Long, MessageImpl >();
        timer = new Timer(MESSAGE_HISTORY_TIMER_NAME);
        timer.start();
    }

    /**
     * Stores a new message.
     * @param message
     */
    public void put(MessageImpl message) {
        if (preservingTime != 0) {
            synchronized (lockObject) {
                messageHistory.put(new Long(message.getId()), message);
                timer.addNotifyListener(message, preservingTime);
            }
        }
        MessagingServiceImpl.log("The message (id = " + message.getId() + ") is stored!",
                MessagingServiceImpl.LOGGING_INFO_LEVEL);
    }

    /**
     * Returns the stored message with specified id.
     * @param messageId
     * @return the message to which the id is mapped in this message history; null if the id is not mapped.
     */
    public Message getMessage(long messageId) {
        return (Message)messageHistory.get(new Long(messageId));
    }

    /**
     * Removes the message from the message history.
     * @param messageId
     * @return <code>false</code> - if the id is not mapped to the message in the message history,
     * <code>true</code> - otherwise
     */
    public boolean removeMessage(long messageId) {
        synchronized (lockObject) {
            if (messageHistory.remove(new Long(messageId)) == null) {
            	MessagingServiceImpl.log("The message (id = " + messageId + ") isn't stored!",
            	        MessagingServiceImpl.LOGGING_INFO_LEVEL);
                return false;
            } else {
            	MessagingServiceImpl.log("The message (id = " + messageId + ") is removed!",
            	        MessagingServiceImpl.LOGGING_INFO_LEVEL);
                return true;
            }
        }
    }

    /**
     * Clears the history.
     */
    public void clear() {
        synchronized (lockObject) {
            timer.stop();
            messageHistory.clear();
        }
    }

    public void setPreservingTime(long preservingTime) {
        this.preservingTime = preservingTime;
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
