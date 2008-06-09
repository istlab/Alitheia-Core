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
import eu.sqooss.service.db.GroupType;

/**
 * The <code>GroupManager</code> provides methods for group management,
 * and group privileges assignment.
 */
public interface GroupManager {

    /**
     * Returns the group object identified by the given group Id.
     * 
     * @param groupId - The group Id
     * 
     * @return The <code>Group</code> object referenced by the given
     *   group identifier, or <code>null</code> when such group doesn't exist.
     */
    public Group getGroup(long groupId);

    /**
     * Returns the group object associated with the given group descriptor.
     * <br/>
     * <i>Note: The group descriptors have unique values.</i>
     * 
     * @param description - the descriptor of the requested group
     * 
     * @return The <code>Group</code> object associated with the given
     *   group descriptor, or <code>null</code> when such group doesn't exist.
     */
    public Group getGroup(String description);

    /**
     * Returns an array of <code>Group</code> objects, that represent all
     * currently defined groups in the SQO-OSS framework.
     * 
     * @return All groups in the SQO-OSS framework.
     */
    public Group[] getGroups(GroupType.Type type);

    /**
     * Given an user Id, this method returns an array of all groups (their 
     * <code>Group</code> objects), where the referenced user is a member.
     * 
     * @param userId - the user identifier
     * 
     * @return All groups, that this user belongs to.
     */
    public Group[] getGroups(long userId);

    /**
     * This method creates a new group.
     * 
     * @param description - the new group's descriptor
     * 
     * @return The new group's <code>Group</code> object, or <code>null</code>
     *   if the group can not be created.
     */
    public Group createGroup(String description, GroupType.Type type);
    
    /**
     * This method deletes the group referenced by the given identifier.
     * 
     * @param groupId - the group's identifier
     * 
     * @return <code>true</code> upon successful deletion,
     *   or <code>false</code> otherwise.
     */
    public boolean deleteGroup(long groupId);

    /**
     * This method adds the selected user to the specified group.
     * 
     * @param groupId - the group's identifier
     * @param userId - the user's identifier
     * 
     * @return <code>true</code> if the membership is created successfully,
     *   or <code>false</code> otherwise.
     */
    public boolean addUserToGroup(long groupId, long userId);

    /**
     * This method removes the selected user from the specified group.
     * @param groupId - the group's identifier
     * @param userId - the user's identifier
     * 
     * @return <code>true</code> if the membership is successfully terminated,
     *   or <code>false</code> otherwise.
     */
    public boolean deleteUserFromGroup(long groupId, long userId);

    /**
     * This method returns an array with all group privileges <i>(each stored
     * in a separate <code>GroupPrivelege</code> object)</i>, that are
     * currently defined in the SQO-OSS framework.
     * 
     * @return The array of all group privileges defined in the SQO-OSS
     *   framework.
     */
    public GroupPrivilege[] getGroupPrivileges();

    /**
     * This method grants a new privilege to the specified group.
     * 
     * @param groupId - the group's identifier
     * @param urlId - the resource URL's identifier
     * @param privilegeValueId - the privilege value's identifier
     * @return <code>true</code> when the given privilege is successfully
     *   granted to the group, or <code>false</code> otherwise.
     */
    public boolean addPrivilegeToGroup(
            long groupId,
            long urlId,
            long privilegeValueId);

    /**
     * This method removes a granted privilege from the specified group.
     * 
     * @param groupId - the group's identifier
     * @param urlId - the resource URL's identifier
     * @param privilegeValueId - the privilege value's identifier
     * 
     * @return <code>true</code> when the given privilege is successfully
     *   withdrawn from that group, or <code>false</code> otherwise.
     */
    public boolean deletePrivilegeFromGroup(
            long groupId,
            long urlId,
            long privilegeValueId);

}

//vi: ai nosi sw=4 ts=4 expandtab
