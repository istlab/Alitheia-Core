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

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import eu.sqooss.impl.service.messaging.senders.smtp.SMTPSender;
import eu.sqooss.service.messaging.Message;
import eu.sqooss.service.messaging.sender.MessageSender;

/**
 * These threads work with <code>MessageQueue</code>.
 * They get the messages from the queue and send them to the message sender service.
 */
public class MessagingServiceThread implements Runnable {

    public static int threadFactor = 10;

    private MessagingServiceImpl messagingService;
    private MessageQueue queue;
    private SMTPSender defaultSender;
    private MessageSender sender;
    private boolean isStopped;
    private long queueringTime;
    private BundleContext bc;
    private ServiceReference sRef;

    public MessagingServiceThread(MessagingServiceImpl messagingService, MessageQueue queue,
            SMTPSender defaultSender, BundleContext bc, long queringTime) {
        this.messagingService = messagingService;
        this.queue = queue;
        this.bc = bc;
        this.defaultSender = defaultSender;
        this.queueringTime = queringTime;
        isStopped = false;
    }

    public void run() {
        MessageImpl message;
        int messageStatus;
        while (!isStopped) {
            message = queue.pop();
            if (message == null) {
                return;
            }
            messagingService.startThreadIfNeeded();
            sender = getMessageSender(message);
            messageStatus = sender.sendMessage(message);
            ungetMessageSender();
            boolean timeout = ((message.getQueueTime() + queueringTime) < System.currentTimeMillis());
            if ((messageStatus == Message.STATUS_FAILED) && !timeout) {
                queue.push(message);
            } else {
                MessagingServiceImpl.log("The message (id = " + message.getId() + ") status is " + messageStatus + " after the message dispatch",
                        MessagingServiceImpl.LOGGING_INFO_LEVEL);
                message.setStatus(messageStatus);
                messagingService.notifyListeners(message, messageStatus);
            }
            messagingService.stopThreadIfNeeded(this);
        }
    }

    /**
     * Stops the thread.
     * @param stopService if <code>true</code> then the thread stops the SMTP service 
     */
    public void stop(boolean stopService) {
        isStopped = true;
        if (stopService && ((sender == null) || (sender == defaultSender))) {
            defaultSender.stopService();
        }
    }

    public void setQueueringTime(long queueringTime) {
        this.queueringTime = queueringTime;
    }


    /**
     * This method returns SMTP sender if a message's protocol is not set or
     * there isn't appropriate service.
     * 
     *  @see eu.sqooss.service.messaging.Message#setProtocol(String)
     */
    private MessageSender getMessageSender(Message message) {
        String messageProtocol = message.getProtocol();
        String filter = "(" + MessageSender.PROTOCOL_PROPERTY + "=" + messageProtocol + ")";

        if ((messageProtocol == null) || (messageProtocol.trim().equals("")) ||
                (SMTPSender.PROTOCOL_PROPERTY_VALUE.equalsIgnoreCase(message.getProtocol().trim()))){
            sRef = null;
            return defaultSender;
        }
        try {
            sRef = bc.getServiceReferences(MessageSender.class.getName(), filter)[0];
            if (sRef == null) {
                return defaultSender;
            } else {
                return (MessageSender)bc.getService(sRef);
            }
        } catch (InvalidSyntaxException ise) {
        	MessagingServiceImpl.log("Invalid message protocol string: " + ise.getMessage(),
        	        MessagingServiceImpl.LOGGING_WARNING_LEVEL);
            throw new IllegalArgumentException("Invalid message protocol string: " + message.getProtocol());
        }
    }

    private void ungetMessageSender() {
        if (sRef != null) {
            bc.ungetService(sRef);
        }
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
