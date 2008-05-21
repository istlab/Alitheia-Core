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

package eu.sqooss.impl.service.web.services.utils;

import java.util.Vector;

import eu.sqooss.service.db.User;
import eu.sqooss.service.messaging.Message;
import eu.sqooss.service.messaging.MessageListener;
import eu.sqooss.service.messaging.MessagingService;

public class UserManagerMessageSender implements MessageListener {
    
    private static final String PROPERTY_ADMIN_EMAIL = "eu.sqooss.web.services.admin.email";
    
    private Vector<String> adminEmail;
    private Vector<Pair> messagesAndUserEmails;
    private MessagingService messagingService;
    
    public UserManagerMessageSender(MessagingService messagingService) {
        this.messagingService = messagingService;
        init();
    }
    
    public void sendMessage(String messageBody, String title, User fromUser) {
        if (adminEmail != null) {
            Message message = Message.getInstance(messageBody, adminEmail, title, null);
            messagesAndUserEmails.add(new Pair(message, fromUser.getEmail()));
            messagingService.sendMessage(message);
        } else {
            Vector<String> recipient = new Vector<String>();
            recipient.add(fromUser.getEmail());
            sendUnsuccessfulMessage(recipient);
        }
    }
    
    /* ===[MessageListener methods]=== */
    /**
     * @see eu.sqooss.service.messaging.MessageListener#messageFailed(eu.sqooss.service.messaging.Message)
     */
    public void messageFailed(Message message) {
        String userMail = removeLocalMessage(message);
        if (userMail != null) {
            Vector<String> recipient = new Vector<String>(1);
            recipient.add(userMail);
            sendUnsuccessfulMessage(recipient);
        }
    }

    /**
     * @see eu.sqooss.service.messaging.MessageListener#messageSent(eu.sqooss.service.messaging.Message)
     */
    public void messageSent(Message message) {
        String userMail = removeLocalMessage(message);
        if (userMail != null) {
            Vector<String> recipient = new Vector<String>(1);
            recipient.add(userMail);
            sendSuccessfulMessage(recipient);
        }
    }
    
    /**
     * @see eu.sqooss.service.messaging.MessageListener#messageQueued(eu.sqooss.service.messaging.Message)
     */
    public void messageQueued(Message message) {
        //do nothing here
    }
    /* ===[MessageListener methods]=== */
    
    private String removeLocalMessage(Message message) {
        Pair currentPair;
        Message currectMessage;
        for (int i = 0; i < messagesAndUserEmails.size(); i++) {
            currentPair = messagesAndUserEmails.get(i);
            currectMessage = (Message) currentPair.getFirstElement();
            if (currectMessage.equals(message)) {
                messagesAndUserEmails.remove(i);
                return (String) currentPair.getSecondElement();
            }
        }
        return null;
    }
    
    private void sendSuccessfulMessage(Vector<String> recipients) {
        Message message = Message.getInstance("The administrator of SQO-OSS is nitified!",
                recipients, "SQO-OSS notification seccess", null);
        messagingService.sendMessage(message);
    }
    
    private void sendUnsuccessfulMessage(Vector<String> recipients) {
        Message message = Message.getInstance("The administrator of SQO-OSS isn't notified!",
                recipients, "SQO-OSS notification failed", null);
        messagingService.sendMessage(message);
    }
    
    private void init() {
        String adminEmailProp = System.getProperty(PROPERTY_ADMIN_EMAIL);
        if (adminEmailProp != null) {
            adminEmail = new Vector<String>();
            adminEmail.add(adminEmailProp);
            messagingService.addMessageListener(this);
        }
        messagesAndUserEmails = new Vector<Pair>();
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
