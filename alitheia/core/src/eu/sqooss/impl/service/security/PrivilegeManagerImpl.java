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

import java.util.Collection;
import java.util.List;

import eu.sqooss.impl.service.security.utils.PrivilegeManagerDatabase;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Privilege;
import eu.sqooss.service.db.PrivilegeValue;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.security.PrivilegeManager;
import eu.sqooss.service.security.SecurityConstants;

public class PrivilegeManagerImpl implements PrivilegeManager {

    private PrivilegeManagerDatabase dbWrapper;
    private Logger logger;
    
    public PrivilegeManagerImpl(DBService db, Logger logger) {
        super();
        this.dbWrapper = new PrivilegeManagerDatabase(db);
        this.logger = logger;
    }

    /**
     * @see eu.sqooss.service.security.PrivilegeManager#createPrivilege(java.lang.String)
     */
    public Privilege createPrivilege(String privilegeName) {
        logger.debug("Create privilege! privilege's name: " + privilegeName);
        Privilege newPrivilege = new Privilege();
        newPrivilege.setDescription(privilegeName);
        if (dbWrapper.create(newPrivilege)) {
            return newPrivilege;
        } else {
            return null;
        }
    }

    /**
     * @see eu.sqooss.service.security.PrivilegeManager#createPrivilege(eu.sqooss.service.security.SecurityConstants.Privilege)
     */
    public Privilege createPrivilege(SecurityConstants.Privilege privilege) {
        return createPrivilege(privilege.toString());
    }
    
    /**
     * @see eu.sqooss.service.security.PrivilegeManager#createPrivilegeValue(long, java.lang.String)
     */
    public PrivilegeValue createPrivilegeValue(long privilegeId,
            String privilegeValue) {
        logger.debug("Create privilege value! privilege's id: " + privilegeId +
                "; privilege's value: " + privilegeValue);
        Privilege privilege = getPrivilege(privilegeId);
        if (privilege != null) {
            PrivilegeValue newPrivilegeValue = new PrivilegeValue();
            newPrivilegeValue.setPrivilege(privilege);
            newPrivilegeValue.setValue(privilegeValue);
            if (dbWrapper.create(newPrivilegeValue)) {
                return newPrivilegeValue;
            }
        }
        return  null;
    }

    /**
     * @see eu.sqooss.service.security.PrivilegeManager#deletePrivilege(long)
     */
    public boolean deletePrivilege(long privilegeId) {
        logger.debug("Delete privilege! privilege's id: " + privilegeId);
        Privilege privilege = getPrivilege(privilegeId);
        if (privilege != null) {
            return dbWrapper.delete(privilege);
        } else {
            return false;
        }
    }

    /**
     * @see eu.sqooss.service.security.PrivilegeManager#deletePrivilegeValue(long)
     */
    public boolean deletePrivilegeValue(long privilegeValueId) {
        logger.debug("Delete privilege value! privilege value's id: " + privilegeValueId);
        PrivilegeValue privilegeValue = getPrivilegeValue(privilegeValueId);
        if (privilegeValue != null) {
            return dbWrapper.delete(privilegeValue);
        } else {
            return false;
        }
    }

    /**
     * @see eu.sqooss.service.security.PrivilegeManager#getPrivilege(long)
     */
    public Privilege getPrivilege(long privilegeId) {
        logger.debug("Get privilege! privilege's id: " + privilegeId);
        return dbWrapper.getPrivilege(privilegeId);
    }

    /**
     * @see eu.sqooss.service.security.PrivilegeManager#getPrivilege(java.lang.String)
     */
    public Privilege getPrivilege(String description) {
        logger.debug("Get privilege! privilege description: " + description);
        List<Privilege> privileges = dbWrapper.getPrivilege(description);
        if (privileges.size() != 0) { //privilege description is unique
            return privileges.get(0);
        } else {
            return null;
        }
    }

    /**
     * @see eu.sqooss.service.security.PrivilegeManager#getPrivilegeValue(long)
     */
    public PrivilegeValue getPrivilegeValue(long privilegeValueId) {
        logger.debug("Get privilege value! privilege value's id: " + privilegeValueId);
        return dbWrapper.getPrivilegeValue(privilegeValueId);
    }

    /**
     * @see eu.sqooss.service.security.PrivilegeManager#getPrivilegeValue(long, java.lang.String)
     */
    public PrivilegeValue getPrivilegeValue(long privilegeId,
            String privilegeValue) {
        logger.debug("Get privilege value! privilege value: " + privilegeValue +
                "; privilege id: " + privilegeId);
        List<PrivilegeValue> privilegeValues = dbWrapper.getPrivilegeValue(
                privilegeId, privilegeValue);
        if (privilegeValues.size() != 0) {
            return privilegeValues.get(0);
        } else {
            return null;
        }
    }
    
    /**
     * @see eu.sqooss.service.security.PrivilegeManager#getPrivilegeValues()
     */
    public PrivilegeValue[] getPrivilegeValues() {
        logger.debug("Get privilege values!");
        return convertPrivilegeValues(dbWrapper.getPrivilegeValues());
    }

    /**
     * @see eu.sqooss.service.security.PrivilegeManager#getPrivilegeValues(long)
     */
    public PrivilegeValue[] getPrivilegeValues(long privilegeId) {
        logger.debug("Get privilege values! privilege's id: " + privilegeId);
        return convertPrivilegeValues(dbWrapper.getPrivilegeValues(privilegeId));
    }

    /**
     * @see eu.sqooss.service.security.PrivilegeManager#getPrivileges()
     */
    public Privilege[] getPrivileges() {
        logger.debug("Get privileges!");
        return convertPrivileges(dbWrapper.getPrivileges());
    }
    
    private static Privilege[] convertPrivileges(Collection<?> privileges) {
        if (privileges != null) {
            Privilege[] result = new Privilege[privileges.size()];
            privileges.toArray(result);
            return result;
        } else {
            return null;
        }
    }
    
    private static PrivilegeValue[] convertPrivilegeValues(Collection<?> privilegeValues) {
        if (privilegeValues != null) {
            PrivilegeValue[] result = new PrivilegeValue[privilegeValues.size()];
            privilegeValues.toArray(result);
            return result;
        } else {
            return null;
        }
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
