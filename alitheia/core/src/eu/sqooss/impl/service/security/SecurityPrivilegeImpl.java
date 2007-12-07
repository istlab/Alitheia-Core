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

import eu.sqooss.impl.service.security.utils.PrivilegeDatabaseUtility;
import eu.sqooss.impl.service.security.utils.ValidateUtility;
import eu.sqooss.service.security.SecurityPrivilege;

public class SecurityPrivilegeImpl implements SecurityPrivilege {

    private long privilegeId;

    public SecurityPrivilegeImpl(long privilegeId) {
        this.privilegeId = privilegeId;
    }

    /**
     * @see eu.sqooss.service.security.SecurityPrivilege#addValue(java.lang.String)
     */
    public long addValue(String value) {
        ValidateUtility.validateValue(value);
        return PrivilegeDatabaseUtility.addPrivilegeValue(privilegeId, value);
    }

    /**
     * @see eu.sqooss.service.security.SecurityPrivilege#getDescription()
     */
    public String getDescription() {
        return PrivilegeDatabaseUtility.getPrivilegeDescription(privilegeId);
    }

    /**
     * @see eu.sqooss.service.security.SecurityPrivilege#getId()
     */
    public long getId() {
        return privilegeId;
    }

    /**
     * @see eu.sqooss.service.security.SecurityPrivilege#getValueId(java.lang.String)
     */
    public long getValueId(String value) {
        return PrivilegeDatabaseUtility.getPrivilegeValueId(privilegeId, value);
    }

    /**
     * @see eu.sqooss.service.security.SecurityPrivilege#getValues()
     */
    public String[] getValues() {
        return PrivilegeDatabaseUtility.getPrivilegeValues(privilegeId);
    }

    /**
     * @see eu.sqooss.service.security.SecurityPrivilege#removeValue(java.lang.String)
     */
    public boolean removeValue(String value) {
        long privilegeValueId = PrivilegeDatabaseUtility.getPrivilegeValueId(privilegeId, value);
        return PrivilegeDatabaseUtility.removePrivilegeValue(privilegeId, privilegeValueId);
    }

    /**
     * @see eu.sqooss.service.security.SecurityPrivilege#removeValue(long)
     */
    public boolean removeValue(long id) {
        return PrivilegeDatabaseUtility.removePrivilegeValue(privilegeId, id);
    }

    /**
     * @see eu.sqooss.service.security.SecurityPrivilege#setDescription(java.lang.String)
     */
    public void setDescription(String description) {
        ValidateUtility.validateValue(description);
        PrivilegeDatabaseUtility.setPrivilegeDescription(privilegeId, description);
    }

    /**
     * @see eu.sqooss.service.security.SecurityPrivilege#setValues(java.lang.String[])
     */
    public void setValues(String[] values) {
        for (int i = 0; i < values.length; i++) {
            ValidateUtility.validateValue(values[i]);
        }

        PrivilegeDatabaseUtility.removePrivilegeValues(privilegeId);
        PrivilegeDatabaseUtility.setPrivilegeValues(privilegeId, values);
    }

    /**
     * @see eu.sqooss.service.security.SecurityPrivilege#remove()
     */
    public void remove() {
        PrivilegeDatabaseUtility.removePrivilege(privilegeId);
    }

    /**
     * The string representation of the object is used for logging.
     */
    public String toString() {
        return "Privilege (privilege id = " + privilegeId + ")";
    }

    /**
     * Two <code>SecurityPrivilegeImpl</code> objects are equal if their identifiers are equal.
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof SecurityPrivilegeImpl)) {
            return false;
        } else {
            SecurityPrivilegeImpl other = (SecurityPrivilegeImpl) obj;
            return (privilegeId == other.getId());
        }
    }

    /**
     * @see java.lang.Long#hashCode()
     */
    public int hashCode() {
        return (int)(privilegeId^(privilegeId>>>32));
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
