/*
 * Copyright 2010 - Organization for Free and Open Source Software,  
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

package eu.sqooss.admin;

import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * An action that is executed by the admin service on behalf of some client.
 * Admin actions are identified by a short mnemonic. The long description
 * should report all returned fields and error codes.
 * 
 * This interface does not support execution of actions as it would enable
 * clients to perform the execution themselves. Implementations should
 * implement the private {@link ExecutableAdminAction} interface instead. 
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 * 
 */
@XmlRootElement(name="action")
public interface AdminAction {
    
    void setArgs(Map<String, Object> args);
    
    @XmlElement(name="args")
    Map<String, Object> args();
    
    @XmlElement(name="mnemonic")
    String getMnemonic();
    
    @XmlElement(name="descr")
    String getDescription();
    
    @XmlElement(name="results")
    Map<String, Object> results();
    
    @XmlElement(name="errors")
    Map<String, Object> errors();
    
    @XmlElement(name="status")
    AdminActionStatus getStatus();
    
    void execute();
    
    public enum AdminActionStatus {
        CREATED,
        EXECUTING,
        FINISHED,
        ERROR,
        UNKNOWN
    }
}
