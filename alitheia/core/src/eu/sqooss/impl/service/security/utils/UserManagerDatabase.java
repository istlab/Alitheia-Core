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

package eu.sqooss.impl.service.security.utils;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Group;
import eu.sqooss.service.db.PendingUser;
import eu.sqooss.service.db.User;

public class UserManagerDatabase implements UserManagerDBQueries {

    private static final String ATTRIBUTE_USER_NAME = "name";
    
    private DBService db;
    
    public UserManagerDatabase(DBService db) {
        super();
        this.db = db;
    }

    public User getUser(long userId) {
        return db.findObjectById(User.class, userId);
    }
    
    public List<User> getUser(String userName) {
        Map<String, Object> userProps = new Hashtable<String, Object>(1);
        userProps.put(ATTRIBUTE_USER_NAME, userName);
        return db.findObjectsByProperties(User.class, userProps);
    }
    
    public List<?> getUsers() {
        return db.doHQL(GET_USERS);
    }
    
    public Set<?> getUsers(long groupId) {
        Group group = db.findObjectById(Group.class, groupId);
        if (group != null) {
            return group.getUsers();
        } else {
            return null;
        }
    }
    
    public boolean createUser(User newUser) {
        return db.addRecord(newUser);
    }

    public boolean createPendingUser(PendingUser newPendingUser) {
        return db.addRecord(newPendingUser);
    }
    
    public boolean modifyUser(String userName, String newPasswordHash, String newEmail) {
        List<User> users = getUser(userName);
        if (users.size() == 1) {
            User user = users.get(0);
            if (newPasswordHash != null) {
                user.setPassword(newPasswordHash);
            }
            if (newEmail != null) {
                user.setEmail(newEmail);
            }
            return true;
        }
        return false;
    }
    
    public boolean deleteUser(User user) {
        return db.deleteRecord(user);
    }

    public boolean hasPendingUserHash (String hashValue) {
        return getPendingUser("hash", hashValue) != null;
    }

    public boolean hasPendingUserName(String userName) {
        return getPendingUser("name", userName) != null;
    }

    public PendingUser getPendingUser (String field, String value) {
        // Search for a matching pending user's record
        Map<String, Object> filter = new HashMap<String, Object>(1);
        filter.put(field, value);
        List<PendingUser> pending =
            db.findObjectsByProperties(PendingUser.class, filter);

        if ( !pending.isEmpty() ) {
            return pending.get(0);
        }
        return null;
    }

    public List<?> getFirstPendingUser() {
        return db.doHQL(GET_FIRST_PENDING_USER);
    }
    
    public boolean deletePendingUser (PendingUser pending) {   
        return db.deleteRecord(pending);
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
