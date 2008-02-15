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

package eu.sqooss.impl.service.security;

import eu.sqooss.impl.service.security.utils.DatabaseUtility;
import eu.sqooss.impl.service.security.utils.ValidateUtility;
import eu.sqooss.service.security.SecurityGroup;
import eu.sqooss.service.security.SecurityUser;

public class SecurityUserImpl implements SecurityUser {

    private long userId;

    public SecurityUserImpl(long userId) {
        this.userId = userId;
    }

    /**
     * @see eu.sqooss.service.security.SecurityUser#addToGroup(eu.sqooss.service.security.SecurityGroup)
     */
    public void addToGroup(SecurityGroup group) {
        if (!(group instanceof SecurityGroupImpl)) {
            throw new IllegalArgumentException("The group must be created with security manager!");
        }

        DatabaseUtility.addUserToGroup(userId, group.getId());
    }

    /**
     * @see eu.sqooss.service.security.SecurityUser#getGroups()
     */
    public SecurityGroup[] getGroups() {
        long[] groupsId = DatabaseUtility.getUserGroupsId(userId);

        SecurityGroup[] groups = new SecurityGroup[groupsId.length];

        for (int i = 0; i < groupsId.length; i++) {
            groups[i] = new SecurityGroupImpl(groupsId[i]);
        }

        return groups;
    }

    /**
     * @see eu.sqooss.service.security.SecurityUser#getId()
     */
    public long getId() {
        return userId;
    }

    /**
     * @see eu.sqooss.service.security.SecurityUser#getUserName()
     */
    public String getUserName() {
        return DatabaseUtility.getUserName(userId);
    }

    /**
     * @see eu.sqooss.service.security.SecurityUser#removeFromGroup(eu.sqooss.service.security.SecurityGroup)
     */
    public void removeFromGroup(SecurityGroup group) {
        if (!(group instanceof SecurityGroupImpl)) {
            throw new IllegalArgumentException("The group must be created with security manager!");
        }

        DatabaseUtility.removeUserFromGroup(userId, group.getId());
    }

    /**
     * @see eu.sqooss.service.security.SecurityUser#setPassword(java.lang.String)
     */
    public void setPassword(String password) {
        ValidateUtility.validateValue(password);
        DatabaseUtility.setUserPassword(userId, password);
    }

    /**
     * @see eu.sqooss.service.security.SecurityUser#setUserName(java.lang.String)
     */
    public void setUserName(String userName) {
        ValidateUtility.validateValue(userName);
        DatabaseUtility.setUserName(userId, userName);
    }

    /**
     * @see eu.sqooss.service.security.SecurityUser#remove()
     */
    public void remove() {
        DatabaseUtility.removeUser(userId);
    }

    /**
     * The string representation of the object is used for logging.
     */
    public String toString() {
        return "User (user id = " + userId + ")";
    }

    /**
     * Two <code>SecurityUserImpl</code> objects are equal if their identifiers are equal.
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof SecurityUserImpl)) {
            return false;
        } else {
            SecurityUserImpl other = (SecurityUserImpl)obj;
            return (userId == other.getId());
        }
    }

    /**
     * @see java.lang.Long#hashCode()
     */
    public int hashCode() {
        return (int)(userId^(userId>>>32));
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
