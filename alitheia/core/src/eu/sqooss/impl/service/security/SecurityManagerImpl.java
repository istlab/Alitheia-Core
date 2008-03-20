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

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import eu.sqooss.impl.service.security.utils.SecurityManagerDatabase;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.security.GroupManager;
import eu.sqooss.service.security.PrivilegeManager;
import eu.sqooss.service.security.SecurityConstants;
import eu.sqooss.service.security.SecurityManager;
import eu.sqooss.service.security.ServiceUrlManager;
import eu.sqooss.service.security.UserManager;

public class SecurityManagerImpl implements SecurityManager, SecurityConstants {

    private UserManager userManager;
    private GroupManager groupManager;
    private PrivilegeManager privilegeManager;
    private ServiceUrlManager serviceUrlManager;
    private SecurityManagerDatabase dbWrapper;
    private Logger logger;

    public SecurityManagerImpl(DBService db, Logger logger) {
        this.dbWrapper = new SecurityManagerDatabase(db);
        this.logger = logger;
        
        userManager = new UserManagerImpl(db, logger);
        groupManager = new GroupManagerImpl(db, logger);
        privilegeManager = new PrivilegeManagerImpl(db, logger);
        serviceUrlManager = new ServiceUrlManagerImpl(db, logger);
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#checkPermission(java.lang.String, java.lang.String, java.lang.String)
     */
    public boolean checkPermission(String fullUrl, String userName, String password) {
        Dictionary<String, String> privileges = new Hashtable<String, String>();
        String resourceUrl;
        try {
            resourceUrl = parseFullUrl(fullUrl, privileges);
        } catch (RuntimeException re) {
            logger.warn("The url isn't correct! url: " + fullUrl);
            return false;
        }
        return checkPermission(resourceUrl, privileges, userName, password);
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#checkPermission(java.lang.String, java.util.Dictionary, java.lang.String, java.lang.String)
     */
    public boolean checkPermission(String resourceUrl, Dictionary<String, String> privileges, String userName, String password) {
        
        logger.info("Check Permission! resourceUrl: " + resourceUrl + "; user name: " + userName);

        try {
            if (dbWrapper.isExistentResourceUrl(resourceUrl, userName, password)) {
                return checkPermissionPrivileges(resourceUrl, privileges, userName, password);
            } else if (dbWrapper.isExistentResourceUrl(URL_SQOOSS, userName, password)) {
                return checkPermissionPrivileges(URL_SQOOSS, privileges, userName, password);
            } else {
                return false; //there aren't privileges
            }
        } catch (RuntimeException re) {
            logger.warn(re.getMessage());
            return false;
        }
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#getGroupManager()
     */
    public GroupManager getGroupManager() {
        return groupManager;
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#getPrivilegeManager()
     */
    public PrivilegeManager getPrivilegeManager() {
        return privilegeManager;
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#getServiceUrlManager()
     */
    public ServiceUrlManager getServiceUrlManager() {
        return serviceUrlManager;
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#getUserManager()
     */
    public UserManager getUserManager() {
        return userManager;
    }

    private boolean checkPermissionPrivileges(String resourceUrl, Dictionary<String, String> privileges, String userName, String password) {
        
        if (dbWrapper.checkAuthorizationRule(resourceUrl, Privilege.ALL.toString(),
                Privilege.ALL.toString(), userName, password)) {
            return true;
        }

        if (privileges != null) {
            String currentPrivilegeName;
            String currentPrivilegeValue;
            for (Enumeration<String> keys = privileges.keys(); keys.hasMoreElements(); ) {
                currentPrivilegeName = keys.nextElement();
                currentPrivilegeValue = privileges.get(currentPrivilegeName);
                return (dbWrapper.checkAuthorizationRule(resourceUrl, currentPrivilegeName, 
                            currentPrivilegeValue, userName, password) ||
                        dbWrapper.checkAuthorizationRule(resourceUrl, Privilege.ALL.toString(), 
                            currentPrivilegeValue, userName, password) ||
                        dbWrapper.checkAuthorizationRule(resourceUrl, currentPrivilegeName,
                            Privilege.ALL.toString(), userName, password));
            }
        }
        
        return false;
    }
    
    private static String parseFullUrl(String fullUrl, Dictionary<String, String> privileges) {
        int resourceDelimiterIndex = fullUrl.indexOf(
                SecurityConstants.URL_DELIMITER_RESOURCE);
        if (resourceDelimiterIndex == -1) {
            return fullUrl;
        }
        String resourceUrl = fullUrl.substring(0, resourceDelimiterIndex);
        String privilegesString = fullUrl.substring(resourceDelimiterIndex + 1);
        
        StringTokenizer privilegesTokenizer = new StringTokenizer(privilegesString,
                Character.toString(URL_DELIMITER_PRIVILEGE));
        
        String currentToken;
        int firstIndexOfEquals;
        int lastIndexOfEquals;
        String privilege;
        String privilegeValue;
        while (privilegesTokenizer.hasMoreTokens()) {
            currentToken = privilegesTokenizer.nextToken();
            firstIndexOfEquals = currentToken.indexOf('=');
            lastIndexOfEquals = currentToken.lastIndexOf('=');
            if ((firstIndexOfEquals == -1) || (firstIndexOfEquals == 0) ||
                    (firstIndexOfEquals != lastIndexOfEquals)) {
                throw new IllegalArgumentException("The parameter is not valid: " + currentToken);
            }
            privilege = currentToken.substring(0, firstIndexOfEquals);
            privilegeValue = currentToken.substring(firstIndexOfEquals + 1);
            privileges.put(privilege, privilegeValue);
        }
        return resourceUrl;
    }

    public Object selfTest() {
        SelfTester tester = new SelfTester(this);
        return tester.test();
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
