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

package eu.sqooss.service.admin;

import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * An action that is executed by the admin service on behalf of some client.
 * Admin actions are identified by a short mnemonic. The long description
 * should report all returned fields and error codes.
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 */
@XmlRootElement(name="action")
public interface AdminAction {
    
	/**
	 * Add an argument to the list of the action's arguments. Overrides
	 * previously set arguments with the same key. Does not have any effect if
	 * {@link #execute()} has been called.
	 */
	void addArg(String key, Object value);
	
	/**
	 * Set all action arguments, overriding previously set ones. Does not have
	 * any effect if {@link #execute()} has been called.
	 */
    void setArgs(Map<String, Object> args);
    
    /**
     * Execute the administrative action.
     */
    void execute() throws Exception;
    void setId(Long id);
    
    /**
     * Return true if the action status is marked as Error.
     */
    boolean hasErrors();
    
    @XmlElement(name="id")
    Long id();
    
    @XmlElement(name="args")
    Map<String, Object> args();
    
    @XmlElement(name="mnemonic")
    String mnemonic();
    
    @XmlElement(name="descr")
    String descr();
    
    @XmlElement(name="results")
    Map<String, Object> results();
    
    @XmlElement(name="errors")
    Map<String, Object> errors();
    
    @XmlElement(name="warn")
    Map<String, Object> warnings();
    
    @XmlElement(name="status")
    AdminActionStatus status();
    
    public enum AdminActionStatus {
        CREATED,
        EXECUTING,
        FINISHED,
        ERROR,
        UNKNOWN
    }
}
