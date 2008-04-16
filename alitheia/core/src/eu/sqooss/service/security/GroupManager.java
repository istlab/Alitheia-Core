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

import eu.sqooss.service.db.Group;
import eu.sqooss.service.db.GroupPrivilege;

/**
 * <code>GroupManager</code> gives an access to the groups and their relations.
 */
public interface GroupManager {
    
    /**
     * @param groupId
     * @return <code>Group</code> with given identifier,
     * null - if the group doesn't exist  
     */
    public Group getGroup(long groupId);
    
    /**
     * The description of the group is unique.
     * The method returns the group with given description. 
     * @param description - the description of the group
     * @return <code>Group</code> with given description,
     * null - if the group doesn't exist
     */
    public Group getGroup(String description);
    
    /**
     * @return all groups in the system
     */
    public Group[] getGroups();
    
    /**
     * @param userId the user identifier
     * @return the user's groups
     */
    public Group[] getGroups(long userId);
    
    /**
     * This method creates a new group.
     * @param description the group's description
     * @return the new Group, null if the group isn't created
     */
    public Group createGroup(String description);
    
    /**
     * This method deletes the group with given identifier.
     * @param groupId the group's identifier
     * @return true - if the group is deleted successfully, false - otherwise 
     */
    public boolean deleteGroup(long groupId);
    
    /**
     * This method adds the user to the given group.
     * @param groupId
     * @param userId
     * @return true - if the relation is created successfully, false otherwise
     */
    public boolean addUserToGroup(long groupId, long userId);
    
    /**
     * This method removes the user from the given group.
     * @param groupId
     * @param userId
     * @return true - if the relation is deleted successfully, false otherwise
     */
    public boolean deleteUserFromGroup(long groupId, long userId);
    
    public GroupPrivilege[] getGroupPrivileges();
    
    public boolean addPrivilegeToGroup(long groupId, long urlId, long privilegeValueId);
    
    public boolean deletePrivilegeFromGroup(long groupId, long urlId, long privilegeValueId);
    
}

//vi: ai nosi sw=4 ts=4 expandtab
