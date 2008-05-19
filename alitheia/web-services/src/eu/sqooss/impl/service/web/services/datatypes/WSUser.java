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

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import eu.sqooss.service.db.Group;

public class WSUser {
    
    private long id;
    private String userName;
    private String email;
    private long registered;
    private long lastActivity;
    private WSUserGroup[] groups;
    
    public WSUser(long id, String userName, String email,
            long registered, long lastActivity, Set<?> groups) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.registered = registered;
        this.lastActivity = lastActivity;
        this.groups = parseSecurityGroups(groups);
    }
    
    public long getId() {
        return id;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public long getRegistered() {
        return registered;
    }
    
    public long getLastActivity() {
        return lastActivity;
    }
    
    public WSUserGroup[] getUserGroups() {
        return groups;
    }
    
    private WSUserGroup[] parseSecurityGroups(Collection<?> securityGroups) {
        if ((securityGroups != null) && (securityGroups.size() != 0)) {
            WSUserGroup[] userGroups = new WSUserGroup[securityGroups.size()];
            Iterator<?> iterator = securityGroups.iterator();
            for (int i = 0; i < userGroups.length; i++) {
                userGroups[i] = new WSUserGroup((Group) iterator.next());
            }
            return userGroups;
        } else {
            return new WSUserGroup[] {null};
        }
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof WSUser)) {
            return false;
        }
        WSUser otherUser = (WSUser) other;
        return this.getId() == otherUser.getId();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Long.valueOf(this.getId()).hashCode();
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
