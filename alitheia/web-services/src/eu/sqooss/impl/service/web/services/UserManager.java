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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Group;
import eu.sqooss.service.db.User;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.security.SecurityManager;
import eu.sqooss.impl.service.web.services.datatypes.WSUser;
import eu.sqooss.impl.service.web.services.datatypes.WSUserGroup;
import eu.sqooss.impl.service.web.services.utils.SecurityWrapper;

public class UserManager extends AbstractManager {
    
    private Logger logger;
    private SecurityWrapper security;
    private eu.sqooss.service.security.UserManager userManager;
    private eu.sqooss.service.security.GroupManager groupManager;
    
    public UserManager(Logger logger, SecurityManager securityManager, DBService db) {
        super(db);
        this.logger = logger;
        this.security = new SecurityWrapper(securityManager);
        this.userManager = securityManager.getUserManager();
        this.groupManager = securityManager.getGroupManager();
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#createPendingUser(String, String, String, String, String)
     */
    public boolean createPendingUser(String userNameForAccess, String passwordForAccess,
            String newUserName, String newPassword, String email) {
        logger.info("Create pending user! user: " + userNameForAccess +
                "; new user's name: " + newUserName + "; new user's e-mail: " + email);
        
        security.checkUserWriteAccess(userNameForAccess, passwordForAccess, -1, null);
        
        super.updateUserActivity(userNameForAccess);
        
        db.startDBSession();
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
        
        security.checkUsersReadAccess(userNameForAccess,
                passwordForAccess, usersIds, null);
        
        super.updateUserActivity(userNameForAccess);
        
        if (usersIds == null) {
            return null;
        }
        
        db.startDBSession();
        Collection<WSUser> users = new HashSet<WSUser>();
        User currentUser;
        for (long userId : usersIds) {
            currentUser = userManager.getUser(userId);
            if (currentUser != null) {
                users.add(createWSUser(currentUser));
            }
        }
        db.commitDBSession();
        
        if (!users.isEmpty()) {
            return users.toArray(new WSUser[users.size()]);
        } else {
            return null;
        }
        
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#getUserGroups(String, String)
     */
    public WSUserGroup[] getUserGroups(String userName, String password) {
        logger.info("Get user groups! user: " + userName);
        
        //TODO:
        security.checkDBReadAccess(userName, password);
        
        super.updateUserActivity(userName);
        
        //FIXME: the security service doesn't work after the last DB changes
        db.startDBSession();
        Group[] groups = groupManager.getGroups();
        
        WSUserGroup[] result = new WSUserGroup[groups.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = createWSUserGroup(groups[i]);
        }
        db.commitDBSession();
        if (result.length == 0) {
            return null;
        } else {
            return result;
        }
    }
    
    /**
     * @see eu.sqooss.service.web.services.WebServices#modifyUser(String, String, String, String, String)
     */
    public boolean modifyUser(String userNameForAccess, String passwordForAccess,
            String userName, String newPassword, String newEmail) {
        logger.info("Modify user! user: " + userNameForAccess +
                "; modified user's name: " + userName + "; new e-mail: " + newEmail);
        
        security.checkUserWriteAccess(userNameForAccess, passwordForAccess,
                -1, userName);
        
        super.updateUserActivity(userNameForAccess);
        
        db.startDBSession();
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
        
        security.checkUserWriteAccess(userNameForAccess, passwordForAccess, userId, null);
        
        super.updateUserActivity(userNameForAccess);
        
        db.startDBSession();
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
        
        security.checkUsersReadAccess(userNameForAccess, passwordForAccess, null, userName);
        
        super.updateUserActivity(userNameForAccess);

        db.startDBSession();
        User user = userManager.getUser(userName);
        if (user != null) {
            WSUser wsu = createWSUser(user);
            db.commitDBSession();
            return wsu;
        } else {
            db.rollbackDBSession();
            return null;
        }
    }

    private static WSUserGroup createWSUserGroup(Group group) {
        if (group == null) return null;
        WSUserGroup wsug = new WSUserGroup();
        wsug.setId(group.getId());
        wsug.setDescription(group.getDescription());
        return wsug;
    }
    
    private static WSUser createWSUser(User user) {
        if (user == null) return null;
        WSUser wsu = new WSUser();
        wsu.setId(user.getId());
        wsu.setEmail(wsu.getEmail());
        wsu.setLastActivity(user.getLastActivity().getTime());
        wsu.setRegistered(user.getRegistered().getTime());
        wsu.setUserName(user.getName());
        Set<?> securityGroups = user.getGroups();
        WSUserGroup[] userGroups;
        if ((securityGroups != null) && (!securityGroups.isEmpty())) {
            userGroups = new WSUserGroup[securityGroups.size()];
            Iterator<?> iterator = securityGroups.iterator();
            for (int i = 0; i < userGroups.length; i++) {
                userGroups[i] = createWSUserGroup((Group) iterator.next());
            }
        } else {
            userGroups = new WSUserGroup[] {null};
        }
        wsu.setGroups(userGroups);
        return wsu;
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
