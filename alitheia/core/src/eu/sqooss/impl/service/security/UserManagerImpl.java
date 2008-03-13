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
import java.util.Date;
import java.util.List;

import eu.sqooss.impl.service.security.utils.UserManagerDatabase;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.User;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.security.UserManager;

public class UserManagerImpl implements UserManager {

    private UserManagerDatabase dbWrapper;
    private Logger logger;
    
    public UserManagerImpl(DBService db, Logger logger) {
        super();
        this.dbWrapper = new UserManagerDatabase(db);
        this.logger = logger;
    }

    /**
     * @see eu.sqooss.service.security.UserManager#createUser(java.lang.String, java.lang.String, java.lang.String)
     */
    public User createUser(String userName, String password, String email) {
        logger.info("Create user! username: " + userName + "; e-mail: " + email);
        User newUser = new User();
        newUser.setName(userName);
        newUser.setPassword(password);
        newUser.setEmail(email);
        newUser.setRegistered(new Date());
        if (dbWrapper.createUser(newUser)) {
            return newUser;
        } else {
            return null;
        }
    }

    /**
     * @see eu.sqooss.service.security.UserManager#deleteUser(long)
     */
    public boolean deleteUser(long userId) {
        logger.info("Delete user! user's id: " + userId);
        return dbWrapper.deleteUser(getUser(userId));
    }

    /**
     * @see eu.sqooss.service.security.UserManager#deleteUser(java.lang.String)
     */
    public boolean deleteUser(String userName) {
        logger.info("Delete user! username: " + userName);
        return dbWrapper.deleteUser(getUser(userName));
    }

    /**
     * @see eu.sqooss.service.security.UserManager#getUser(long)
     */
    public User getUser(long userId) {
        logger.info("Get user! user's id: " + userId);
        return dbWrapper.getUser(userId);
    }

    /**
     * @see eu.sqooss.service.security.UserManager#getUser(java.lang.String)
     */
    public User getUser(String userName) {
        logger.info("Get user! username: " + userName);
        List<User> users = dbWrapper.getUsers(userName);
        if (users.size() == 1) {
            return users.get(0);
        } else {
            return null;
        }
    }

    /**
     * @see eu.sqooss.service.security.UserManager#getUsers()
     */
    public User[] getUsers() {
        return convertUsers(dbWrapper.getUsers());
    }

    /**
     * @see eu.sqooss.service.security.UserManager#getUsers(long)
     */
    public User[] getUsers(long groupId) {
        return convertUsers(dbWrapper.getUsers(groupId));
    }
    
    private static User[] convertUsers(Collection<?> users) {
        if (users != null) {
            User[] result = new User[users.size()];
            users.toArray(result);
            return result;
        } else {
            return null;
        }
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
