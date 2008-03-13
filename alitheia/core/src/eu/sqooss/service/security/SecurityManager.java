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
 * The <code>SecurityManager</code> class is used for validating the access to the SQO-OSS resources and
 * their management.
 */
public interface SecurityManager {

    /**
     * Validate the access to the SQO-OSS resource based on the full URL (with privileges), user name and password.
     * @param fullURL the full URL contains the privileges
     * @param userName
     * @param password
     * @return <code>true</code> if the access is allowed, <code>false</code> otherwise
     */
    public boolean checkPermission(String fullURL, String userName, String password);

    /**
     * Validates the access to the SQO-OSS resource based on the resourceURL, privileges, user name and password.
     * @param resourceURL
     * @param privileges
     * @param userName
     * @param password
     * @return <code>true</code> if the access is allowed, <code>false</code> otherwise
     */
    public boolean checkPermission(String resourceUrl, Dictionary<String, String> privileges, String userName, String password);

    /**
     * This method returns the group manager.
     * @return the group manager
     * @see eu.sqooss.service.security.GroupManager
     */
    public GroupManager getGroupManager();
    
    /**
     * This method returns the privilege manager.
     * @return the privilege manager
     * @see eu.sqooss.service.security.PrivilegeManager
     */
    public PrivilegeManager getPrivilegeManager();
    
    /**
     * This method returns the user manager.
     * @return the user manager
     * @see eu.sqooss.service.security.UserManager
     */
    public UserManager getUserManager();
    
    /**
     * This method returns the service url manager.
     * @return the service url manager
     * @see eu.sqooss.service.security.ServiceUrlManager
     */
    public ServiceUrlManager getServiceUrlManager();
    
}

//vi: ai nosi sw=4 ts=4 expandtab
