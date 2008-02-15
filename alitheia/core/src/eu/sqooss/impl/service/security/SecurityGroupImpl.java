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

public class SecurityGroupImpl implements SecurityGroup {

    private long groupId;

    public SecurityGroupImpl(long groupId) {
        this.groupId = groupId;
    }

    /**
     * @see eu.sqooss.service.security.SecurityGroup#getDescription()
     */
    public String getDescription() {
        return DatabaseUtility.getGroupDescription(groupId);
    }

    /**
     * @see eu.sqooss.service.security.SecurityGroup#getId()
     */
    public long getId() {
        return groupId;
    }

    /**
     * @see eu.sqooss.service.security.SecurityGroup#setDescription(java.lang.String)
     */
    public void setDescription(String description) {
        ValidateUtility.validateValue(description);
        DatabaseUtility.setGroupDescription(groupId, description);
    }

    /**
     * @see eu.sqooss.service.security.SecurityGroup#remove()
     */
    public void remove() {
        DatabaseUtility.removeGroup(groupId);
    }

    /**
     * The string representation of the object is used for logging.
     */
    public String toString() {
        return "Group (group id = " + groupId + ")";
    }

    /**
     * Two <code>SecurityGroupImpl</code> objects are equal if their identifiers are equal.
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof SecurityGroupImpl)) {
            return false;
        } else {
            SecurityGroupImpl other = (SecurityGroupImpl)obj;
            return (groupId == other.getId());
        }
    }

    /**
     * @see java.lang.Long#hashCode()
     */
    public int hashCode() {
        return (int)(groupId^(groupId>>>32));
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
