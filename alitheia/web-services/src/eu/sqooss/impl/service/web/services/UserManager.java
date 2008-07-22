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

package eu.sqooss.impl.service.web.services;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import eu.sqooss.impl.service.web.services.datatypes.WSConstants;
import eu.sqooss.impl.service.web.services.datatypes.WSUser;
import eu.sqooss.impl.service.web.services.datatypes.WSUserGroup;
import eu.sqooss.impl.service.web.services.utils.UserSecurityWrapper;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Group;
import eu.sqooss.service.db.User;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.security.SecurityManager;
import eu.sqooss.service.webadmin.WebadminService;

public class UserManager extends AbstractManager {
    
    private Logger logger;
    private UserSecurityWrapper security;
    private WebadminService webadmin;
    private eu.sqooss.service.security.UserManager userManager;
    private eu.sqooss.service.security.GroupManager groupManager;
    
    public UserManager(Logger logger, SecurityManager securityManager,
            DBService db, WebadminService webadmin) {
        super(db);
        this.logger = logger;
        this.security = new UserSecurityWrapper(securityManager, db, logger);
        this.userManager = securityManager.getUserManager();
        this.groupManager = securityManager.getGroupManager();
        this.webadmin = webadmin;
        db.startDBSession();
        security.addPermissonsToSystemGroup();
        security.addPermissionsToNewUsersGroup();
        db.commitDBSession();
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#createPendingUser(String, String, String, String, String)
     */
    public boolean createPendingUser(String userNameForAccess, String passwordForAccess,
            String newUserName, String newPassword, String email) {
        logger.info("Create pending user! user: " + userNameForAccess +
                "; new user's name: " + newUserName + "; new user's e-mail: " + email);
        
        db.startDBSession();
        
        if (!security.checkUsersWriteAccess(userNameForAccess,
                passwordForAccess, userNameForAccess, null)) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException("Security violation in the create pending user operation!");
        }
        
        super.updateUserActivity(userNameForAccess);
        
        boolean ok = userManager.createPendingUser(newUserName, newPassword, email);
        if (ok) {
            db.commitDBSession();
        } else {
            db.rollbackDBSession();
        }
        return ok;
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getUsersByIds(String, String, long[])
     */
    public WSUser[] getUsersByIds(String userNameForAccess,
            String passwordForAccess, long[] usersIds) {
        logger.info("Get users by ids! user: " + userNameForAccess +
                "; ids: " + Arrays.toString(usersIds));
        
        db.startDBSession();
        User currentUser = null;
        if (usersIds.length == 1) {
            currentUser = userManager.getUser(usersIds[0]);
        }
        if (!isSameUser(userNameForAccess, passwordForAccess, currentUser)) {
            if (!security.checkUsersReadAccess(userNameForAccess,
                    passwordForAccess, null, usersIds)) {
                if (db.isDBSessionActive()) {
                    db.commitDBSession();
                }
                throw new SecurityException("Security violation in the get users by ids operation!");
            }
        }
        
        super.updateUserActivity(userNameForAccess);
        
        Collection<WSUser> users = new HashSet<WSUser>();
        for (long userId : usersIds) {
            currentUser = userManager.getUser(userId);
            if (currentUser != null) {
                users.add(WSUser.getInstance(currentUser));
            }
        }
        db.commitDBSession();
        WSUser[] result;
        if (!users.isEmpty()) {
            result = users.toArray(new WSUser[users.size()]);
        } else {
            result = null;
        }
        return (WSUser[]) normalizeWSArrayResult(result);
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getUserGroups(String, String)
     */
    public WSUserGroup[] getUserGroups(String userName, String password) {
        logger.info("Get user groups! user: " + userName);
        
        db.startDBSession();
        
        if (!security.checkGroupReadAccess(userName, password)) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException("Security violation in the get user groups operation!");
        }
        
        super.updateUserActivity(userName);
        
        Group[] groups = groupManager.getGroups(null);
        
        WSUserGroup[] result = new WSUserGroup[groups.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = WSUserGroup.getInstance(groups[i]);
        }
        db.commitDBSession();
        if (result.length == 0) {
            return null;
        } else {
            return (WSUserGroup[]) normalizeWSArrayResult(result);
        }
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#modifyUser(String, String, String, String, String)
     */
    public boolean modifyUser(String userNameForAccess, String passwordForAccess,
            String userName, String newPassword, String newEmail) {
        logger.info("Modify user! user: " + userNameForAccess +
                "; modified user's name: " + userName + "; new e-mail: " + newEmail);
        
        db.startDBSession();
        
        if (!security.checkUsersWriteAccess(userNameForAccess,
                passwordForAccess, userName, null)) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException("Securty violation in the modify operation!");
        }
        
        super.updateUserActivity(userNameForAccess);
        
        boolean ok = userManager.modifyUser(userName, newPassword, newEmail);
        if (ok) {
            db.commitDBSession();
        } else {
            db.rollbackDBSession();
        }
        return ok;
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#deleteUserById(String, String, long)
     */
    public boolean deleteUserById(String userNameForAccess, String passwordForAccess, long userId) {
        logger.info("Delete user by id! user: " + userNameForAccess +
                "; deleted user's id: " + userId);
        
        db.startDBSession();
        
        if (!security.checkUsersWriteAccess(userNameForAccess,
                passwordForAccess, null, new long[] {userId})) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException("Security violation in the delete user by id operation!");
        }
        
        super.updateUserActivity(userNameForAccess);
        
        boolean ok = userManager.deleteUser(userId);
        if (ok) {
            db.commitDBSession();
        } else {
            db.rollbackDBSession();
        }
        return ok;
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getUserByName(String, String, String)
     */
    public WSUser getUserByName(String userNameForAccess,
            String passwordForAccess, String userName) {
        logger.info("Get user by name! user: " + userNameForAccess +
                "; requested user name: " + userName);
        
        db.startDBSession();
        User user = userManager.getUser(userName);
        if (!isSameUser(userNameForAccess, passwordForAccess, user)) {
            if (!security.checkUsersReadAccess(userNameForAccess,passwordForAccess,
                    userName, null)) {
                if (db.isDBSessionActive()) {
                    db.commitDBSession();
                }
                throw new SecurityException("Security violation in the get user by name operation!");
            }
        }
        
        super.updateUserActivity(userNameForAccess);

        if (user != null) {
            WSUser wsu = WSUser.getInstance(user);
            db.commitDBSession();
            return wsu;
        } else {
            db.rollbackDBSession();
            return null;
        }
    }

    /**
     * @see eu.sqooss.service.web.services.WebServices#getMessageOfTheDay(String)
     */
    public String getMessageOfTheDay(String userName, String password) {
        logger.info("Get message of the day! user: " + userName);
        
        db.startDBSession();
        
        if (!security.checkWebAdminGetMessageAccess(userName, password)) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException("Security violation in the get message " +
            		"of the day operation");
        }
        
        String s = null;
        if (webadmin != null) {
            s = webadmin.getMessageOfTheDay();
        } else {
            s = "No connection to MOTD server.";
        }
        if (userName.length() < 3 /* inches ? */) {
            return "Expand your unit, " + userName;
        }
        if (s != null) {
            return s;
        }
        return "Share and enjoy.";
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#notifyAdmin(String, String, String, String)
     */
    public boolean notifyAdmin(String userName, String password,
            String title, String messageBody) {
        logger.info("Notify admin! user: " + userName +
                "; title: " + title);
        
        db.startDBSession();
        
        if (!security.checkWebAdminNotifyAccess(userName, password)) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException("Security violation in the notify admin operation!");
        }
        
        super.updateUserActivity(userName);
        
        User user = userManager.getUser(userName);
        boolean result;
        if (user != null) {
            result = webadmin.notifyAdmin(title, messageBody, user.getEmail());
        } else {
            result = false;
        }
        db.commitDBSession();
        return result;
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getConstants(String, String)
     */
    public WSConstants getConstants(String userName, String password) {
        logger.info("Get constants! user: " + userName);
        
        db.startDBSession();
        
        if (!security.checkConstantsReadAccess(userName, password)) {
            if (db.isDBSessionActive()) {
                db.commitDBSession();
            }
            throw new SecurityException("Security violation in the get constants operation!");
        }
        
        super.updateUserActivity(userName);
        
        db.commitDBSession();
        
        return WSConstants.instance();
    }
    
    
    private boolean isSameUser(String userNameForAccess,
            String passwordForAccess, User otherUser) {
        String passwordHash = userManager.getHash(passwordForAccess);
        if ((userNameForAccess == null) ||
                (passwordHash == null) ||
                (otherUser == null)) {
            return false;
        }
        return (userNameForAccess.equals(otherUser.getName()) &&
                (passwordHash.equals(otherUser.getPassword())));
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
