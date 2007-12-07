/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
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
import eu.sqooss.service.security.SecurityAuthorizationRule;
import eu.sqooss.service.security.SecurityGroup;
import eu.sqooss.service.security.SecurityResourceURL;

/**
 * This class implements the <code>SecurityAuthorizationRule</code> interface.
 */
public class SecurityAuthorizationRuleImpl implements SecurityAuthorizationRule {

    private long groupId;
    private long urlId;
    private long privilegeValueId;

    public SecurityAuthorizationRuleImpl(long groupId, long urlId, long privilegeValueId) {
        this.groupId = groupId;
        this.urlId = urlId;
        this.privilegeValueId = privilegeValueId;
    }

    /**
     * @see eu.sqooss.service.security.SecurityAuthorizationRule#getGroup()
     */
    public SecurityGroup getGroup() {
        return new SecurityGroupImpl(groupId);
    }

    /**
     * @see eu.sqooss.service.security.SecurityAuthorizationRule#getUrl()
     */
    public SecurityResourceURL getUrl() {
        return new SecurityResourceURLImpl(urlId);
    }

    /**
     * @see eu.sqooss.service.security.SecurityAuthorizationRule#getPrivilegeValueId()
     */
    public long getPrivilegeValueId() {
        //TODO check if the rule exist
        return privilegeValueId;
    }

    /**
     * @see eu.sqooss.service.security.SecurityAuthorizationRule#remove()
     */
    public void remove() {
        DatabaseUtility.removeAuthorizationRule(groupId, urlId, privilegeValueId);
    }

    /**
     * The string representation of the object is used for logging.
     */
    public String toString() {
        return "Authorization rule (group id = " + groupId +
        "; url id = " + urlId + "; privilege value id = " + privilegeValueId + ")"; 
    }

    /**
     * Two <code>SecurityAuthorizationRuleImpl</code> objects are equal
     * if their identifiers of the group, url and privilege value are equal.
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof SecurityAuthorizationRuleImpl)) {
            return false;
        } else {
            SecurityAuthorizationRuleImpl other = (SecurityAuthorizationRuleImpl)obj;
            return ((groupId == other.getGroup().getId()) &&
                    (urlId == other.getUrl().getId()) &&
                    (privilegeValueId == other.getPrivilegeValueId()));
        }
    }

    public int hashCode() {
        return (int)((groupId << 20)|(urlId << 10) | privilegeValueId);
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
