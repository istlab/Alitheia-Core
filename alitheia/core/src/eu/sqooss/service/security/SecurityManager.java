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

import java.util.Dictionary;

/**
 * The <code>SecurityManager</code> class is used for validating the access
 * to resources, and provides methods for creation and management of access
 * privileges.
 * <br/>
 * A resource in the SQO-OSS security scope could be either a project
 * resource, or a service that is mapped and accessible through the SQO-OSS
 * user interfaces.
 */
public interface SecurityManager {

    /**
     * Validates the access to a SQO-OSS resource located by the given
     * resource's URL. The URL must include the access privileges as well.
     * The validation process is then performed against the specified user
     * name and password.
     * 
     * @param fullURL - the URL location (plus access privileges) of the
     *   requested resource
     * @param privileges - a list of access privileges for that resource 
     * @param userName - user account's name
     * @param password - user account's password
     * @return <code>true</code> if the set of access privileges can be
     *   granted for that resource with the specified user account,
     *   or <code>false</code> otherwise.
     */
    public boolean checkPermission(String fullURL, String userName, String password);

    /**
     * Validates the access to a SQO-OSS resource located by the given
     * resource's URL. The URL must not include access privileges, since they
     * are provided in additional parameter. The validation process is then
     * performed against the specified user name and password.
     * 
     * @param resourceURL - the URL location of the requested resource
     * @param privileges - a list of access privileges for that resource 
     * @param userName - user account's name
     * @param password - user account's password
     * @return <code>true</code> if the set of access privileges can be
     *   granted for that resource with the specified user account,
     *   or <code>false</code> otherwise.
     */
    public boolean checkPermission(String resourceUrl, Dictionary<String, String> privileges, String userName, String password);

    /**
     * This method returns the locally stored <code>GroupManager</code>
     * instance.
     * 
     * @return The group manager instance.
     * @see eu.sqooss.service.security.GroupManager
     */
    public GroupManager getGroupManager();

    /**
     * This method returns the locally stored <code>PrivilegeManager</code>
     * instance.
     * 
     * @return The privilege manager instance.
     * @see eu.sqooss.service.security.PrivilegeManager
     */
    public PrivilegeManager getPrivilegeManager();

    /**
     * This method returns the locally stored <code>UserManager</code>
     * instance.
     * 
     * @return The user manager instance.
     * @see eu.sqooss.service.security.UserManager
     */
    public UserManager getUserManager();

    /**
     * This method returns the locally stored <code>ServiceUrlManager</code>
     * instance.
     * 
     * @return The service URL manager instance.
     * @see eu.sqooss.service.security.ServiceUrlManager
     */
    public ServiceUrlManager getServiceUrlManager();

    /**
     * This method creates a new access model for the SQO-OSS resource located
     * by the given URL. Access to that resource are granted or denied for
     * the specified group depending on the given access privilege.
     * <br/>
     * <i>When some of the arguments (like group or privilege) doesn't have
     *   an associated record in the underlying database, then the
     *   corresponding record(s) is(are) created automatically.</i>
     * 
     * @param groupDescription -- group name
     * @param privilege -- privilege name
     * @param privilegeValue -- privilege value
     * @param serviceUrl -- the URL location of the affected resource
     * @return <code>true</code> if the access model is successfully created,
     *   or <code>false</code> otherwise
     */
    public boolean createSecurityConfiguration(String groupDescription,
            String privilege, String privilegeValue, String serviceUrl);

    /**
     * This method deletes an existing access model.
     * 
     * @param groupDescription -- group name
     * @param privilege -- privilege name
     * @param privilegeValue -- privilege value
     * @param serviceUrl -- the URL location of the affected resource
     * @return <code>true</code> if the specified access model is successfully
     *   deleted, or <code>false</code> otherwise
     */
    public boolean deleteSecurityConfiguration(String groupDescription,
            String privilege, String privilegeValue, String serviceUrl);

}

//vi: ai nosi sw=4 ts=4 expandtab
