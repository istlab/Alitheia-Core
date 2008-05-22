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

package eu.sqooss.impl.service.web.services.datatypes;

import eu.sqooss.service.db.Developer;

public class WSDeveloper {
    
    private long id;
    private String name;
    private String email;
    private String username;
    private long storedProjectId;
    
    /**
     * @return the id
     */
    public long getId() {
        return id;
    }
    
    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * @return the storedProjectId
     */
    public long getStoredProjectId() {
        return storedProjectId;
    }
    
    /**
     * @param storedProjectId the storedProjectId to set
     */
    public void setStoredProjectId(long storedProjectId) {
        this.storedProjectId = storedProjectId;
    }
    
    /**
     * The method creates a new <code>WSDeveloper</code> object
     * from the existent DAO object.
     * The method doesn't care of the db session. 
     * 
     * @param developer - DAO developer object
     * 
     * @return The new <code>WSDeveloper</code> object
     */
    public static WSDeveloper getInstance(Developer developer) {
        if (developer == null) return null;
        try {
            WSDeveloper wsDeveloper = new WSDeveloper();
            wsDeveloper.setId(developer.getId());
            wsDeveloper.setEmail(developer.getEmail());
            wsDeveloper.setName(developer.getName());
            wsDeveloper.setStoredProjectId(developer.getStoredProject().getId());
            wsDeveloper.setUsername(developer.getUsername());
            return wsDeveloper;
        } catch (Exception e) {
            return null;
        }
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
