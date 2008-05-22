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

import java.io.StringWriter;
import java.util.Vector;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import eu.sqooss.service.db.User;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.messaging.Message;
import eu.sqooss.service.messaging.MessageListener;
import eu.sqooss.service.messaging.MessagingService;

public class UserManagerMessageSender implements MessageListener {
    
    private static final String PROPERTY_ADMIN_EMAIL = "eu.sqooss.web.services.admin.email";
    
    private Vector<String> adminEmail;
    private Vector<Pair> messagesAndUserEmails;
    private MessagingService messagingService;
    private VelocityContext successContext;
    private Template successTemplate;
    private VelocityContext unsuccessContext;
    private Template unsuccessTemplate;
    
    public UserManagerMessageSender(MessagingService messagingService) {
        this.messagingService = messagingService;
        init();
        initVelocityTemplates();
    }
    
    public boolean sendMessage(String messageBody, String title, User fromUser) {
        if (adminEmail != null) {
            Message message = Message.getInstance(messageBody, adminEmail, title, null);
            messagesAndUserEmails.add(new Pair(message, fromUser.getEmail()));
            messagingService.sendMessage(message);
            return true;
        } else {
            Vector<String> recipient = new Vector<String>();
            recipient.add(fromUser.getEmail());
            sendUnsuccessfulMessage(recipient, messageBody, title);
            return false;
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
            sendUnsuccessfulMessage(recipient,
                    message.getBody(), message.getTitle());
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
            sendSuccessfulMessage(recipient, 
                    message.getBody(), message.getTitle());
        }
    }
    
    /**
     * @see eu.sqooss.service.messaging.MessageListener#messageQueued(eu.sqooss.service.messaging.Message)
     */
    public void messageQueued(Message message) {
        //do nothing here
    }
    /* ===[MessageListener methods]=== */
    
    private void initVelocityTemplates() {
        successContext = new VelocityContext();
        unsuccessContext = new VelocityContext();
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
                                   "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
        velocityEngine.setProperty("runtime.log.logsystem.log4j.category", 
                                   Logger.NAME_SQOOSS_WEB_SERVICES);
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER,"bundle");
        velocityEngine.setProperty("bundle.resource.loader.description",
                                   "Loader from the bundle.");
        velocityEngine.setProperty("bundle.resource.loader.class",
                                   "org.apache.velocity.runtime.resource.loader.JarResourceLoader");
        velocityEngine.setProperty("bundle.resource.loader.path",
                                   "jar:file:eu.sqooss.alitheia.web-services-0.0.1.jar");
        try {
            successTemplate = velocityEngine.getTemplate(
                    "/OSGI-INF/configuration/NotifyAdminSuccess.vtl");
            unsuccessTemplate = velocityEngine.getTemplate(
                    "/OSGI-INF/configuration/NotifyAdminUnsuccess.vtl");
        } catch (Exception e) {
            successTemplate = null;
            unsuccessTemplate = null;
        }
    }
    
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
    
    private void sendSuccessfulMessage(Vector<String> recipients,
            String messageBody, String messageTitle) {
        StringWriter bodyWriter = new StringWriter();
        if (successTemplate != null) {
            synchronized (successContext) {
                successContext.put("E_MAIL", recipients.toString());
                successContext.put("MESSAGE_TITLE", messageTitle);
                successContext.put("MESSAGE_BODY", messageBody);
                try {
                    successTemplate.merge(successContext, bodyWriter);
                } catch (Exception e) {
                    bodyWriter.write("");
                }
            }
        }
        Message message = Message.getInstance(bodyWriter.toString(),
                recipients, "SQO-OSS notification is sent successfully", null);
        messagingService.sendMessage(message);
    }
    
    private void sendUnsuccessfulMessage(Vector<String> recipients,
            String messageBody, String messageTitle) {
        StringWriter bodyWriter = new StringWriter();
        if (unsuccessTemplate != null) {
            synchronized (unsuccessContext) {
                unsuccessContext.put("E_MAIL", recipients.toString());
                unsuccessContext.put("MESSAGE_TITLE", messageTitle);
                unsuccessContext.put("MESSAGE_BODY", messageBody);
                try {
                    unsuccessTemplate.merge(unsuccessContext, bodyWriter);
                } catch (Exception e) {
                    bodyWriter.write("");
                }
            }
        }
        Message message = Message.getInstance(bodyWriter.toString(),
                recipients, "SQO-OSS notification is not sent", null);
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
