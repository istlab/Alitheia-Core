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

package eu.sqooss.service.security;

import eu.sqooss.service.db.Privilege;
import eu.sqooss.service.db.PrivilegeValue;

/**
 * <code>PrivilegeManager</code> gives an access to the privilege's management. 
 */
public interface PrivilegeManager {
    
    /**
     * @param privilegeId
     * @return the privilege with given identifier,
     * null - if the privilege doesn't exist
     */
    public Privilege getPrivilege(long privilegeId);
    
    /**
     * The description of the privilege is unique.
     * The method returns the privilege with given description. 
     * @param description - the description of the privilege
     * @return <code>Privilege</code> with given description,
     * null - if the privilege doesn't exist
     */
    public Privilege getPrivilege(String description);
    
    /**
     * @return all privileges in the system
     */
    public Privilege[] getPrivileges();
    
    /**
     * @param privilegeValueId
     * @return the privilege value with given identifier,
     * null - if the privilege value doesn't exist
     */
    public PrivilegeValue getPrivilegeValue(long privilegeValueId);
    
    /**
     * The privilege values are unique.
     * The method returns the privilege value.
     * @param privilegeId
     * @param privilegeValue
     * @return null - if the privilege value daesn't exist 
     */
    public PrivilegeValue getPrivilegeValue(long privilegeId,
            String privilegeValue);
    
    /**
     * @return all privileges values in the system
     */
    public PrivilegeValue[] getPrivilegeValues();
    
    /**
     * @param privilegeId the privilege's identifier
     * @return the privilege's values 
     */
    public PrivilegeValue[] getPrivilegeValues(long privilegeId);
    
    /**
     * This method creates a new privilege.
     * @param privilegeName the privilege's name (description)
     * @return the new privilege,
     * null - if the privilege isn't created
     */
    public Privilege createPrivilege(String privilegeName);
    
    /**
     * This method creates a new privilege.
     * @param privilegeName the privilege's name
     * @return the new privilege,
     * null - if the privilege isn't created
     */
    public Privilege createPrivilege(SecurityConstants.Privilege privilege);
    
    /**
     * This method creates a new privilege value.
     * @param privilegeId the privilege identifier
     * @param privilegeValue the privilege's value
     * @return the new privilege value,
     * null - if the privilege value isn't created
     */
    public PrivilegeValue createPrivilegeValue(long privilegeId, String privilegeValue);
    
    /**
     * This method deletes the privilege with given identifier.
     * @param privilegeId the privilege's identifier
     * @return true - if the privilege is deleted successfully, false - otherwise
     */
    public boolean deletePrivilege(long privilegeId);
    
    /**
     * This method deletes the privilege value with given identifier.
     * @param privilegeValueId the privilege value's identifier
     * @return true - if the privilege value is deleted successfully, false - otherwise
     */
    public boolean deletePrivilegeValue(long privilegeValueId);
    
}

//vi: ai nosi sw=4 ts=4 expandtab
