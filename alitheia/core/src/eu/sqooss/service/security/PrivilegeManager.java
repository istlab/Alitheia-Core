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
 * The <code>GroupManager</code> provides methods for privileges management.
 */
public interface PrivilegeManager {

    /**
     * Returns the privilege's object referenced by the given privilege
     * identifier.
     * 
     * @param privilegeId - the privilege's identifier
     * 
     * @return The <code>Privilege</code> object referenced by the given
     *   privilege identifier, or <code>null</code> when such privilege
     *   doesn't exist.
     */
    public Privilege getPrivilege(long privilegeId);

    /**
     * Returns the privilege's object associated with the given privilege
     * descriptor.
     * <br/>
     * <i>Note: The privilege descriptors have unique values.</i>
     *  
     * @param description - the privilege's descriptor
     * 
     * @return The <code>Privilege</code> object associated with the given
     *   privilege descriptor, or <code>null</code> when such privilege
     *   doesn't exist.
     */
    public Privilege getPrivilege(String description);

    /**
     * Returns an array of <code>Privilege</code> objects, that represent all
     * currently defined privileges in the SQO-OSS framework.
     * 
     * @return All privileges defined in the SQO-OSS framework.
     */
    public Privilege[] getPrivileges();

    /**
     * Returns the privilege's value object referenced by the given
     * privilege's value identifier.
     * 
     * @param privilegeValueId - the privilege's value identifier
     * 
     * @return The <code>PrivilegeValue</code> object referenced by the
     *   given privilege's value identifier, or <code>null</code> when such
     *   privilege's value doesn't exist.
     */
    public PrivilegeValue getPrivilegeValue(long privilegeValueId);

    /**
     * Returns the privilege's value object associated with the given
     * privilege's value descriptor, and assigned to the privilege referenced
     * by the specified privilege identifier.
     * <i>Note: The privilege's value's descriptors have unique values.</i>
     * 
     * @param privilegeId  - the privilege's identifier
     * @param privilegeValue  - the privilege's value descriptor
     * 
     * @return The <code>PrivilegeValue</code> object, or <code>null</code>
     *  when such privilege's value doesn't exists. 
     */
    public PrivilegeValue getPrivilegeValue(long privilegeId,
            String privilegeValue);

    /**
     * Returns an array of <code>PrivilegeValue</code> objects, that represent
     * all currently defined privileges' values in the SQO-OSS framework.
     * 
     * @return All privileges' values defined in the SQO-OSS framework.
     */
    public PrivilegeValue[] getPrivilegeValues();

    /**
     * Returns an array of <code>PrivilegeValue</code> objects, that represent
     * all privilege's values, assigned to the privilege referenced
     * by the given privilege identifier.
     * 
     * @param privilegeId - the privilege's identifier
     * 
     * @return All privilege's values assigned to that privilege.
     */
    public PrivilegeValue[] getPrivilegeValues(long privilegeId);

    /**
     * This method creates a new privilege.
     * 
     * @param privilegeName - the new privilege's descriptor
     * 
     * @return The new privilege's <code>Privilege</code> object,
     *   or <code>null</code> if the privilege can not be created.
     */
    public Privilege createPrivilege(String privilegeName);

    /**
     * This method creates a new privilege, based on one of the pre-defined
     * privilege's descriptors {@link SecurityConstants.Privilege}.
     * 
     * @param privilegeName - the privilege's descriptor
     * 
     * @return The new privilege's <code>Privilege</code> object,
     *   or <code>null</code> if the privilege can not be created.
     */
    public Privilege createPrivilege(SecurityConstants.Privilege privilege);

    /**
     * This method creates a new privilege's value associated with the given
     * privilege's value descriptor, and assigns it to the privilege
     * referenced with the specified privilege identifier.
     * 
     * @param privilegeId - the privilege's identifier
     * @param privilegeValue the privilege's value descriptor
     * 
     * @return The new privilege's <code>PrivilegeValue</code> object,
     *   or <code>null</code> if the privilege's value can not be created.
     */
    public PrivilegeValue createPrivilegeValue(long privilegeId, String privilegeValue);

    /**
     * This method deletes the privilege referenced by the given identifier.
     * 
     * @param privilegeId - the privilege's identifier
     * 
     * @return <code>true</code> upon successful removal,
     *   or <code>false</code> otherwise.
     */
    public boolean deletePrivilege(long privilegeId);

    /**
     * This method deletes the privilege's value referenced by the given
     * identifier.
     * 
     * @param privilegeValueId - the privilege's value identifier
     * 
     * @return <code>true</code> upon successful removal,
     *   or <code>false</code> otherwise.
     */
    public boolean deletePrivilegeValue(long privilegeValueId);
    
}

//vi: ai nosi sw=4 ts=4 expandtab
