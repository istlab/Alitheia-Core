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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.impl.service.security.utils.SecurityManagerDatabase;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Group;
import eu.sqooss.service.db.ServiceUrl;
import eu.sqooss.service.db.User;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.messaging.MessagingService;
import eu.sqooss.service.security.GroupManager;
import eu.sqooss.service.security.PrivilegeManager;
import eu.sqooss.service.security.SecurityConstants;
import eu.sqooss.service.security.SecurityManager;
import eu.sqooss.service.security.ServiceUrlManager;
import eu.sqooss.service.security.UserManager;

public class SecurityManagerImpl implements SecurityManager, SecurityConstants {

    private static final String PROPERTY_DEFAULT_USER_NAME     = "eu.sqooss.security.user.name";
    private static final String PROPERTY_DEFAULT_USER_PASSWORD = "eu.sqooss.security.user.password";
    private static final String PROPERTY_DEFAULT_USER_EMAIL    = "eu.sqooss.security.user.email";
    private static final String PROPERTY_DEFAULT_USER_GROUP    = "eu.sqooss.security.user.group";
    private static final String PROPERTY_ENABLE                = "eu.sqooss.security.enable";
    
    private BundleContext bc;
    private UserManager userManager;
    private GroupManager groupManager;
    private PrivilegeManager privilegeManager;
    private ServiceUrlManager serviceUrlManager;
    private SecurityManagerDatabase dbWrapper;
    private Logger logger;
    private ServiceReference srefHttpService = null;
    private HttpService sobjHttpService = null;
    private boolean isEnable;
    
    private class ConfirmationServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;

        protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

            res.setContentType("text/html");
            PrintWriter content = res.getWriter();

            String confId = req.getParameter("confid");
            if ((confId != null) && (confId.length() > 0 )) {
                
                if (userManager.hasPendingUserHash(confId)) {
                    if (userManager.activatePendingUser(confId)) {
                        content.println(
                                "Thank you."
                                + " Your user account is now active.");
                    }
                    else {
                        content.println(
                                "Account activation has failed!");
                    }
                }
                else {
                    content.println("User not found!");
                }
            }
            else {
                content.println("Wrong parameters!");
            }
        }
    }

    public SecurityManagerImpl(BundleContext bc, Logger logger) {
        this.bc = bc;

        // Get the AlitheaCore's object
        ServiceReference srefCore = null;
        srefCore = bc.getServiceReference(AlitheiaCore.class.getName());
        AlitheiaCore sobjCore = (AlitheiaCore) bc.getService(srefCore);

        // Obtain the required core components
        DBService db = sobjCore.getDBService();
        MessagingService messaging = sobjCore.getMessagingService();
        this.logger = logger;

        // Instantiate a wrapper around the DB component
        this.dbWrapper = new SecurityManagerDatabase(sobjCore.getDBService());

        // Instantiate the various security managers
        groupManager = new GroupManagerImpl(db, logger);
        privilegeManager = new PrivilegeManagerImpl(db, logger);
        serviceUrlManager = new ServiceUrlManagerImpl(db, logger);
        userManager = new UserManagerImpl(db, messaging, logger,
                privilegeManager, groupManager, serviceUrlManager);

        // Check if security is enabled in the configuration file
        isEnable = Boolean.valueOf(System.getProperty(PROPERTY_ENABLE, "true"));

        // Create the unprivileged SQO-OSS user
        initDefaultUser();
        
        // Get a reference to the HTTPService, and its object
        srefHttpService = bc.getServiceReference(HttpService.class.getName());
        if (srefHttpService != null) {
            sobjHttpService = (HttpService) bc.getService(srefHttpService);
            if (sobjHttpService != null) {
                try {
                    // Register the user's confirmation servlet
                    sobjHttpService.registerServlet(
                        "/confirmRegistration",
                        new ConfirmationServlet(),
                        new Hashtable<String, String>(),
                        null);
                }
                catch (Exception e) {
                    logger.error(ConfirmationServlet.class
                            + " registration has failed with: " + e);
                }
            }
            else {
                logger.error("Unable to obtain a HttpService object!");
            }
        }
        else {
            logger.error("Unable to obtain a HttpService reference!");
        }
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
        
        logger.debug("Check Permission! resourceUrl: " + resourceUrl + "; user name: " + userName);

        if (!isEnable) {
        	return true;
        }
        
        String passwordHash = userManager.getHash(password);
        
        if ((resourceUrl == null) || (userName == null) || (passwordHash == null)) {
            return false;
        }
        
        try {
            if (dbWrapper.isExistentResourceUrl(resourceUrl, userName, passwordHash)) {
                return checkPermissionPrivileges(resourceUrl, privileges, userName, passwordHash);
            } else if (dbWrapper.isExistentResourceUrl(URL_SQOOSS, userName, passwordHash)) {
                return checkPermissionPrivileges(URL_SQOOSS, privileges, userName, passwordHash);
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

    /**
     * @see eu.sqooss.service.security.SecurityManager#createSecurityConfiguration(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public boolean createSecurityConfiguration(String groupDescription,
            String privilege, String privilegeValue, String serviceUrl) {

        Group userGroup = groupManager.getGroup(groupDescription);
        if (userGroup == null) {
            return false;
        }
        eu.sqooss.service.db.Privilege userPrivilege =
            privilegeManager.getPrivilege(privilege); 
        if (userPrivilege == null) {
            userPrivilege = privilegeManager.createPrivilege(privilege);
        }
        eu.sqooss.service.db.PrivilegeValue userPrivilegeValue =
            privilegeManager.getPrivilegeValue(
                userPrivilege.getId(), privilegeValue);
        if (userPrivilegeValue == null) {
            userPrivilegeValue = privilegeManager.createPrivilegeValue(
                    userPrivilege.getId(), privilegeValue);
        }
        ServiceUrl userServiceUrl = serviceUrlManager.getServiceUrl(serviceUrl);
        if (userServiceUrl == null) {
            userServiceUrl = serviceUrlManager.createServiceUrl(serviceUrl);
        }
        if (groupManager.addPrivilegeToGroup(userGroup.getId(),
                userServiceUrl.getId(), userPrivilegeValue.getId())) {
            return true;
        } else {
            //clean if possible
            privilegeManager.deletePrivilegeValue(userPrivilegeValue.getId());
            privilegeManager.deletePrivilege(userPrivilege.getId());
            serviceUrlManager.deleteServiceUrl(userServiceUrl.getId());
            return false;
        }
    }

    /**
     * @see eu.sqooss.service.security.SecurityManager#deleteSecurityConfiguration(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public boolean deleteSecurityConfiguration(String groupDescription,
            String privilege, String privilegeValue, String serviceUrl) {

        Group userGroup = groupManager.getGroup(groupDescription);
        eu.sqooss.service.db.Privilege userPrivilege =
            privilegeManager.getPrivilege(privilege);
        ServiceUrl userServiceUrl = serviceUrlManager.getServiceUrl(serviceUrl);
        if ((userGroup == null) || (userPrivilege == null) || (userServiceUrl == null)) {
            return false;
        }
        eu.sqooss.service.db.PrivilegeValue userPrivilegeValue = 
            privilegeManager.getPrivilegeValue(userPrivilege.getId(),
                    privilegeValue);
        if (userPrivilegeValue == null) {
            return false;
        }
        boolean result = groupManager.deletePrivilegeFromGroup(userGroup.getId(),
                userServiceUrl.getId(), userPrivilegeValue.getId());
        //clean if possible
        privilegeManager.deletePrivilegeValue(userPrivilegeValue.getId());
        privilegeManager.deletePrivilege(userPrivilege.getId());
        serviceUrlManager.deleteServiceUrl(userServiceUrl.getId());
        return result;
    }

    private boolean checkPermissionPrivileges(String resourceUrl, Dictionary<String, String> privileges, String userName, String password) {
        
        if (dbWrapper.checkAuthorizationRule(resourceUrl, Privilege.ALL.toString(),
                PrivilegeValue.ALL.toString(), userName, password)) {
            return true;
        }

        boolean result = false;
        if ((privileges != null) && (privileges.size() != 0)) {
            String currentPrivilegeName;
            String currentPrivilegeValue;
            result = true;
            for (Enumeration<String> keys = privileges.keys(); (keys.hasMoreElements() && result); ) {
                currentPrivilegeName = keys.nextElement();
                currentPrivilegeValue = privileges.get(currentPrivilegeName);
                result = result &&
                       (dbWrapper.checkAuthorizationRule(resourceUrl, currentPrivilegeName, 
                            currentPrivilegeValue, userName, password) ||
                        dbWrapper.checkAuthorizationRule(resourceUrl, Privilege.ALL.toString(), 
                            currentPrivilegeValue, userName, password) ||
                        dbWrapper.checkAuthorizationRule(resourceUrl, currentPrivilegeName,
                            PrivilegeValue.ALL.toString(), userName, password));
            }
        }
        
        return result;
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

    private void initDefaultUser() {
        String userName = bc.getProperty(PROPERTY_DEFAULT_USER_NAME);
        String userPassword = bc.getProperty(PROPERTY_DEFAULT_USER_PASSWORD);
        String userEmail = bc.getProperty(PROPERTY_DEFAULT_USER_EMAIL);
        String userGroup = bc.getProperty(PROPERTY_DEFAULT_USER_GROUP);
        if ((userName == null) || (userPassword == null) ||
                (userEmail == null) || (userGroup == null)) {
            logger.error("The default security user is not created! " +
            		"See the configuration properties of the security service!");
        } else {
            User defaultUser = userManager.getUser(userName);
            if (defaultUser != null) {
                if (!defaultUser.getPassword().equals(userManager.getHash(userPassword))) {
                    userManager.modifyUser(userName, userPassword, userEmail);
                }
            } else {
                defaultUser = userManager.createUser(userName, userPassword, userEmail);
                Group defaultUserGroup = null;
                Group[] groups = groupManager.getGroups();
                for (Group group : groups) {
                    if (userGroup.equals(group.getDescription())) {
                        defaultUserGroup = group;
                        break;
                    }
                }
                if (defaultUserGroup == null) {
                    defaultUserGroup = groupManager.createGroup(userGroup);
                }
                if (groupManager.addUserToGroup(defaultUserGroup.getId(), defaultUser.getId())) {
                    if (createSecurityConfiguration(userGroup, Privilege.ACTION.toString(),
                            PrivilegeValue.WRITE.toString(), URL_SQOOSS_SECURITY)) {
                        return;
                    } else {
                        logger.error("The permissions of the default security user are not set!");
                    }
                } else {
                    logger.error("The default user isn't created!");
                }
                //clean if possible
                groupManager.deleteUserFromGroup(defaultUserGroup.getId(), defaultUser.getId());
                userManager.deleteUser(defaultUser.getId());
                groupManager.deleteGroup(defaultUserGroup.getId());
            }
        }
    }

    public Object selfTest() {
        SelfTester tester = new SelfTester(this);
        return tester.test();
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
