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

/**
 * This class represents the message queue.
 * The messages in the queue have the <code>Message.STATUS_QUEUED</code> status.
 */
public class MessageQueue {

    private Vector < MessageImpl > vector;
    private Object lockObject = new Object();
    private boolean clear;

    public MessageQueue() {
        vector = new Vector < MessageImpl >();
        clear = false;
    }

    /**
     * Inserts the message to the end of the queue.
     * @param message
     */
    public void push(MessageImpl message) {
        synchronized (lockObject) {
            vector.addElement(message);
            lockObject.notifyAll();
        }
        MessagingServiceImpl.log("The message (id = " + message.getId() + ") is in the queue!",
                MessagingServiceImpl.LOGGING_INFO_LEVEL);
    }

    /**
     * Removes the message from the beginning of the queue.
     */
    public MessageImpl pop() {
        synchronized (lockObject) {
            try {
                while (isEmpty() && !clear) {
                    lockObject.wait();
                }
                if (clear) {
                    return null;
                } else {
                    MessageImpl message = (MessageImpl)vector.remove(0);
                    MessagingServiceImpl.log("The message (id = " + message.getId() + ") is removed from the queue!",
                    		MessagingServiceImpl.LOGGING_INFO_LEVEL);
                    return message;
                }
            } catch (InterruptedException ie) {
            	MessagingServiceImpl.log(ie.getMessage(),
            	        MessagingServiceImpl.LOGGING_WARNING_LEVEL);
                throw new RuntimeException(ie);
            }
        }
    }

    /**
     * @return <code>true</code> - if the queue is empty, <code>false</code> - otherwise
     */
    public boolean isEmpty() {
        synchronized (lockObject) {
            return vector.isEmpty();
        }
    }

    /**
     * Removes all elements from the queue.
     */
    public void clearQueue(){
        synchronized (lockObject) {
            vector.removeAllElements();
            clear = true;
            lockObject.notifyAll();
        }
    }

    /**
     * @return The number of the elements.
     */
    public int size() {
        synchronized (lockObject) {
            return vector.size();
        }
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
