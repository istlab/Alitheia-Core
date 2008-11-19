/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - Organization for Free and Open Source Software,  
 *                 Athens, Greece.
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

package eu.sqooss.service.db;

/**
 * Entity that holds information about a mail message's thread status.
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 */
public class MailThread extends DAObject {
    
    /** The mail this entry represents */ 
    private MailMessage mail;
    
    /** The parent of this entry in the thread */
    private MailMessage parent;
    
    /** The thread this entry belongs to */
    private MailingListThread thread;    

    public MailThread() {}
    
    public MailThread(MailMessage mail, MailMessage parent, 
            MailingListThread thread) {
        this.mail = mail;
        this.parent = parent;
        this.thread = thread;
    }
    public MailMessage getMail() {
        return mail;
    }
    public void setMail(MailMessage mail) {
        this.mail = mail;
    }
    public MailMessage getParent() {
        return parent;
    }
    public void setParent(MailMessage parent) {
        this.parent = parent;
    }
    public MailingListThread getThread() {
        return thread;
    }
    public void setThread(MailingListThread thread) {
        this.thread = thread;
    }
}
