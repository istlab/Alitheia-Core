/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - 2010 - Organization for Free and Open Source Software,  
 *                Athens, Greece.
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

import java.util.*;

import eu.sqooss.service.db.DAObject;

/**
 * Part of the security system, instances of this class represent the
 * details of an Alitheia Core system user, as stored in the database
 */
public class User extends DAObject {
    /**
     * The name of the Alitheia Core user
     */
    private String name;

    /**
     * The date on which the user was registered into the system
     */
    private Date registered;

    /**
     * The date on which the user last logged into the Alitheia Core
     */
    private Date lastActivity;

    /**
     * The password for this Alitheia Core user
     */
    private String password;

    /**
     * The email address for this Alitheia Core user
     */
    private String email;

    /**
     * A set representing the security system groups of which this
     * user is a member
     */
    private Set groups = new HashSet();
    
    public User() {};

    public String getName() {
        return name;
    }

    public void setName( String value ) {
        name = value;
    }

    public Date getRegistered() {
        return registered;
    }

    public void setRegistered( Date value ) {
        registered = value;
    }

    public Date getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity( Date value ) {
        lastActivity = value;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword( String value ) {
        password = value;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail( String value ) {
        email = value;
    }

    public Set getGroups() {
        return groups;
    }

    public void setGroups(Set groups) {
        this.groups = groups;
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
