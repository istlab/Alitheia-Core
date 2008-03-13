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

import eu.sqooss.impl.service.security.utils.GroupManagerDatabase;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Group;
import eu.sqooss.service.db.GroupPrivilege;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.security.GroupManager;

public class GroupManagerImpl implements GroupManager {

    private GroupManagerDatabase dbWrapper;
    private Logger logger;
    
    public GroupManagerImpl(DBService db, Logger logger) {
        super();
        this.dbWrapper = new GroupManagerDatabase(db);
        this.logger = logger;
    }

    public GroupPrivilege addPrivilegeToGroup(long groupId, long urlId,
            long privilegeValueId) {
        // TODO:
        return null;
    }

    public boolean addUserToGroup(long groupId, long userId) {
        logger.info("Add user to group! group's id: " + groupId +
                "; user's id: " + userId);
        return dbWrapper.addUserToGroup(groupId, userId);
    }

    public Group createGroup(String description) {
        logger.info("Create group! description: " + description);
        Group newGroup = new Group();
        newGroup.setDescription(description);
        if (dbWrapper.create(newGroup)) {
            return newGroup;
        } else {
            return null;
        }
    }

    public boolean deleteGroup(long groupId) {
        logger.info("Delete group! group's id: " + groupId);
        return dbWrapper.delete(getGroup(groupId));
    }

    public boolean deletePrivilegeFromGroup(long groupId, long urlId,
            long privilegeValueId) {
        // TODO:
        return false;
    }

    public boolean deleteUserFromGroup(long groupId, long userId) {
        logger.info("Delete user from group! group's id: " + groupId +
                "; userId: " + userId);
        return dbWrapper.deleteUserFromGroup(groupId, userId);
    }

    public Group getGroup(long groupId) {
        logger.info("Get group! group's id: " + groupId);
        return dbWrapper.getGroup(groupId);
    }

    public GroupPrivilege[] getGroupPrivileges() {
        logger.info("Get group privileges!");
        return convertGroupPrivileges(dbWrapper.getGroupPrivileges());
    }

    public Group[] getGroups() {
        logger.info("Get gorups!");
        return convertGroups(dbWrapper.getGroups());
    }

    public Group[] getGroups(long userId) {
        logger.info("Get gorups! userId: " + userId);
        return convertGroups(dbWrapper.getGroups(userId));
    }
    
    private static Group[] convertGroups(Collection<?> groups) {
        if (groups != null) {
            Group[] result = new Group[groups.size()];
            groups.toArray(result);
            return result;
        } else {
            return null;
        }
    }
    
    private static GroupPrivilege[] convertGroupPrivileges(Collection<?> groupPrivileges) {
        if (groupPrivileges != null) {
            GroupPrivilege[] result = new GroupPrivilege[groupPrivileges.size()];
            groupPrivileges.toArray(result);
            return result;
        } else {
            return null;
        }
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
