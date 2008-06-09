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
import java.util.List;

import eu.sqooss.impl.service.security.utils.GroupManagerDatabase;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Group;
import eu.sqooss.service.db.GroupPrivilege;
import eu.sqooss.service.db.GroupType;
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

    /**
     * @see eu.sqooss.service.security.GroupManager#addPrivilegeToGroup(long, long, long)
     */
    public boolean addPrivilegeToGroup(long groupId, long urlId,
            long privilegeValueId) {
        logger.debug("Add privilege to group! group's id: " + groupId +
                "; privilege value's id: " + privilegeValueId + "; url's id: " + urlId);
        return dbWrapper.addPrivilegeToGroup(groupId, urlId, privilegeValueId);
    }

    /**
     * @see eu.sqooss.service.security.GroupManager#addUserToGroup(long, long)
     */
    public boolean addUserToGroup(long groupId, long userId) {
        logger.debug("Add user to group! group's id: " + groupId +
                "; user's id: " + userId);
        return dbWrapper.addUserToGroup(groupId, userId);
    }

    /**
     * @see eu.sqooss.service.security.GroupManager#createGroup(java.lang.String)
     */
    public Group createGroup(String description, GroupType.Type type) {
        logger.debug("Create group! description: " + description);
        Group result = getGroup(description);
        if (result != null) return null; //the group is in the db
        result = new Group();
        result.setDescription(description);
        GroupType groupType = GroupType.getGroupType(type);
        if (groupType == null) {
            groupType = new GroupType(type);
            dbWrapper.create(groupType);
        }
        result.setGroupType(groupType);
        if (!dbWrapper.create(result)) {
            result = null;
        }
        return result;
    }

    /**
     * @see eu.sqooss.service.security.GroupManager#deleteGroup(long)
     */
    public boolean deleteGroup(long groupId) {
        logger.debug("Delete group! group's id: " + groupId);
        Group group = getGroup(groupId);
        if (group != null) {
            return dbWrapper.delete(getGroup(groupId));            
        } else {
            return false;
        }
    }

    /**
     * @see eu.sqooss.service.security.GroupManager#deletePrivilegeFromGroup(long, long, long)
     */
    public boolean deletePrivilegeFromGroup(long groupId, long urlId,
            long privilegeValueId) {
        logger.debug("Delete privilege from group! group's id: " + groupId +
                "; url's id: " + urlId + "; privilege value's id: " + privilegeValueId);
        return dbWrapper.deletePrivilegeFromGroup(groupId, urlId, privilegeValueId);
    }

    /**
     * @see eu.sqooss.service.security.GroupManager#deleteUserFromGroup(long, long)
     */
    public boolean deleteUserFromGroup(long groupId, long userId) {
        logger.debug("Delete user from group! group's id: " + groupId +
                "; userId: " + userId);
        return dbWrapper.deleteUserFromGroup(groupId, userId);
    }

    /**
     * @see eu.sqooss.service.security.GroupManager#getGroup(long)
     */
    public Group getGroup(long groupId) {
        logger.debug("Get group! group's id: " + groupId);
        return dbWrapper.getGroup(groupId);
    }

    /**
     * @see eu.sqooss.service.security.GroupManager#getGroup(java.lang.String)
     */
    public Group getGroup(String description) {
        logger.debug("Get group! group description: "+ description);
        List<Group> groups = dbWrapper.getGroup(description);
        if (groups.size() != 0) { //the group description is unique
            return groups.get(0);
        } else {
            return null;
        }
    }

    /**
     * @see eu.sqooss.service.security.GroupManager#getGroupPrivileges()
     */
    public GroupPrivilege[] getGroupPrivileges() {
        logger.debug("Get group privileges!");
        return convertGroupPrivileges(dbWrapper.getGroupPrivileges());
    }

    /**
     * @see eu.sqooss.service.security.GroupManager#getGroups()
     */
    public Group[] getGroups() {
        logger.debug("Get gorups!");
        return convertGroups(dbWrapper.getGroups());
    }

    /**
     * @see eu.sqooss.service.security.GroupManager#getGroups(long)
     */
    public Group[] getGroups(long userId) {
        logger.debug("Get gorups! userId: " + userId);
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
