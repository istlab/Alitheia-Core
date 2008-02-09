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

import java.util.Enumeration;
import java.util.Hashtable;

import eu.sqooss.impl.service.security.utils.DatabaseWrapper;
import eu.sqooss.impl.service.security.utils.ParserUtility;
import eu.sqooss.impl.service.security.utils.PrivilegeDatabaseUtility;
import eu.sqooss.impl.service.security.utils.ValidateUtility;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.security.SecurityAuthorizationRule;
import eu.sqooss.service.security.SecurityGroup;
import eu.sqooss.service.security.SecurityManager;
import eu.sqooss.service.security.SecurityPrivilege;
import eu.sqooss.service.security.SecurityResourceURL;
import eu.sqooss.service.security.SecurityUser;

public class SecurityManagerImpl implements SecurityManager {

    private DatabaseWrapper dbWrapper;
    private Logger logger;

    public SecurityManagerImpl(DBService db, Logger logger) {
        this.dbWrapper = new DatabaseWrapper(db);
        this.logger = logger;
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#checkPermission(java.lang.String, java.lang.String, java.lang.String)
     */
    public boolean checkPermission(String fullUrl, String userName, String password) {
        Hashtable < String, String > privileges = new Hashtable < String, String >();
        String resourceUrl = ParserUtility.mangleUrlWithPrivileges(fullUrl, privileges);
        return checkPermission(resourceUrl, privileges, userName, password);
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#checkPermission(java.lang.String, java.util.Hashtable, java.lang.String, java.lang.String)
     */
    public boolean checkPermission(String resourceURL, Hashtable<String, String> privileges, String userName, String password) {
        ValidateUtility.validateValue(resourceURL);

        String tmpUrl = resourceURL;
        while (true) {
            if (dbWrapper.isExistentResourceUrl(tmpUrl)) {
//            if (DatabaseUtility.isExistentResourceUrl(tmpUrl)) {
                String currentPrivilegeName;
                String currentPrivilegeValue;
                for (Enumeration<String> keys = privileges.keys(); keys.hasMoreElements(); ) {
                    currentPrivilegeName = keys.nextElement();
                    currentPrivilegeValue = privileges.get(currentPrivilegeName);
//                    if (!(DatabaseUtility.checkAuthorizationRule(tmpUrl, currentPrivilegeName, currentPrivilegeValue, userName, password))) {
                    if (!dbWrapper.checkAuthorizationRule(tmpUrl, currentPrivilegeName, currentPrivilegeValue, userName, password)) {
                        return false;
                    }
                }
                return true;
            } else {
                int lastIndexOfAmpersand = tmpUrl.lastIndexOf('&');
                if (lastIndexOfAmpersand == -1) {
                    int firstIndexOfQuestionMark = tmpUrl.indexOf('?');
                    if (firstIndexOfQuestionMark == -1) {
                        return false;
                    } else {
                        tmpUrl = tmpUrl.substring(0, firstIndexOfQuestionMark);
                    }
                } else {
                    tmpUrl = tmpUrl.substring(0, lastIndexOfAmpersand);
                }
            }
        }
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#createAuthorizationRule(eu.sqooss.service.security.SecurityGroup, long, eu.sqooss.service.security.SecurityResourceURL)
     */
    public SecurityAuthorizationRule createAuthorizationRule(SecurityGroup group,
            long privilegeValueId, SecurityResourceURL resourceURL) {

        if ((PrivilegeDatabaseUtility.isExistentPrivilegeValue(privilegeValueId)) &&
                (group instanceof SecurityGroupImpl) &&
                (resourceURL instanceof SecurityResourceURLImpl)) {
            SecurityAuthorizationRule newRule = new SecurityAuthorizationRuleImpl(group.getId(), resourceURL.getId(), privilegeValueId);
            logger.info(newRule + " is created");
            return newRule;
        } else {
            logger.info("Can't create a authorization rule with: group id = " + group.getId() +
                    "; privilege value id = " + privilegeValueId + "; url id = " + resourceURL.getId());
            return null;
        }
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#createGroup(java.lang.String)
     */
    public SecurityGroup createGroup(String description) {
        ValidateUtility.validateValue(description);
        long groupId = dbWrapper.createGroup(description);
        SecurityGroup newGroup = new SecurityGroupImpl(groupId);
        logger.info(newGroup + " is created");
        return newGroup;
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#createPrivilege(java.lang.String)
     */
    public SecurityPrivilege createPrivilege(String description) {
        ValidateUtility.validateValue(description);
        long privilegeId = PrivilegeDatabaseUtility.createPrivilege(description);
        SecurityPrivilege newPrivilege = new SecurityPrivilegeImpl(privilegeId);
        logger.info(newPrivilege + " is created");
        return newPrivilege;
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#createResourceURL(java.lang.String)
     */
    public SecurityResourceURL createResourceURL(String resourceURL) {
        ValidateUtility.validateValue(resourceURL);

        String mangledUrl = ParserUtility.mangleUrl(resourceURL);
        long urlId = dbWrapper.createURL(mangledUrl);
        SecurityResourceURL newResourceUrl = new SecurityResourceURLImpl(urlId);
        logger.info(newResourceUrl + " is created");
        return newResourceUrl;
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#createUser(java.lang.String, java.lang.String)
     */
    public SecurityUser createUser(String userName, String password) {
        ValidateUtility.validateValue(userName);
        ValidateUtility.validateValue(password);
        long userId = dbWrapper.createUser(userName, password);
        SecurityUser newUser = new SecurityUserImpl(userId);
        logger.info(newUser + " is created");
        return newUser;
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#getGroup(long)
     */
    public SecurityGroup getGroup(long id) {
        if (dbWrapper.isExistentGroup(id)) {
            return new SecurityGroupImpl(id);
        } else {
            logger.info("The group with id = " + id + " doesn't exist");
            return null;
        }
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#getPrivilege(long)
     */
    public SecurityPrivilege getPrivilege(long id) {
        if (PrivilegeDatabaseUtility.isExistentPrivilege(id)) {
            return new SecurityPrivilegeImpl(id);
        } else {
            logger.info("The privilege with id = " + id + " doesn't exist");
            return null;
        }
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#getReourceURL(long)
     */
    public SecurityResourceURL getReourceURL(long id) {
        if (dbWrapper.isExistentResourceUrl(id)) {
            return new SecurityResourceURLImpl(id);
        } else {
            logger.info("The resource url with id = " + id + " doesn't exist");
            return null;
        }
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#getUser(long)
     */
    public SecurityUser getUser(long id) {
        if (dbWrapper.isExistentUser(id)) {
            return new SecurityUserImpl(id);
        } else {
            logger.info("The user with id = " + id + " doesn't exist");
            return null;
        }
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#getAuthorizationRules()
     */
    public SecurityAuthorizationRule[] getAuthorizationRules() {
        return dbWrapper.getAuthorizationRules();
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#deleteUser(long)
     */
    public void deleteUser(long userId) {
        if (dbWrapper.isExistentUser(userId)) {
            dbWrapper.deleteUser(userId);
        } else {
            //TODO: throws exception
        }
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#modifyUser(eu.sqooss.service.security.SecurityUser)
     */
    public void modifyUser(SecurityUser modifiedUser) {
        if (dbWrapper.isExistentUser(modifiedUser.getId())) {
            dbWrapper.momifyUser(modifiedUser);
        } else {
            //TODO: throws exception
        }
    }

    public Object selfTest() {
        if (logger == null) {
            return new String("No logger available.");
        }

        SecurityGroup myGroup = createGroup("BCM");
        SecurityUser myUser = createUser("adriaan","baaaaa");

        if ( (myGroup == null) || (myUser == null) ) {
            return new String("Could not create test user and group.");
        }

        myUser.addToGroup(myGroup);

        SecurityPrivilege myPrivilege = createPrivilege("egcs");
        SecurityResourceURL myUrl = createResourceURL("http://www.lolcats.com/");

        if ( (myPrivilege == null) || (myUrl == null) ) {
            return new String("Could not create test privilege and URL.");
        }


        if (createAuthorizationRule(myGroup, myPrivilege.getId(), myUrl) == null) {
            return new String("Could not create authorization rule.");
        }

        if (!checkPermission(myUrl.getURL(), myUser.getUserName(), "baaaaa")) {
            return new String("Permission denied -- falsely");
        }
        if (checkPermission(myUrl.getURL(), myUser.getUserName(), "baaa")) {
            return new String("Permission granted -- falsely");
        }
        if (checkPermission(myUrl.getURL(), null, null)) {
            return new String("Permission granted to null user.");
        }
        if (checkPermission("http://thedailywtf.com/", myUser.getUserName(), "baaaaa")) {
            return new String("Permission granted to bogus URL.");
        }

        return null;
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
