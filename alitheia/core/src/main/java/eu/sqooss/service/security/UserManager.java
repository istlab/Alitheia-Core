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

import eu.sqooss.service.db.User;

/**
 * <code>UserManager</code> provides methods for users management,
 * and group membership assignment.
 */
public interface UserManager {

    /**
     * Returns the user's object referenced by the given user identifier.
     * 
     * @param userId - the user's identifier
     * 
     * @return The <code>User</code> object referenced by the given
     *   user identifier, or <code>null</code> when such user
     *   doesn't exist.
     */
    public User getUser(long userId);

    /**
     * Returns the user's object associated with the given user name.
     * <br/>
     * <i>Note: The user names have unique values.</i>
     * 
     * @param userName - the user's name
     * 
     * @return The <code>User</code> object associated with the given
     *   user name, or <code>null</code> when such user doesn't exist.
     */
    public User getUser(String userName);

    /**
     * Returns an array of <code>User</code> objects, that represent all
     * currently registered users in the SQO-OSS framework.
     * 
     * @return All users registered in the SQO-OSS framework.
     */
    public User[] getUsers();

    /**
     * This method returns an array of all users, which are members of the
     * given group.
     * 
     * @param groupId - the group's identifier
     * 
     * @return All user members of the specified group.
     */
    public User[] getUsers(long groupId);

    /**
     * This method creates a new user from the specified user parameters.
     * 
     * @param userName - the user's name
     * @param password - the user's password
     * @param email -the user's email
     * 
     * @return The new user's <code>User</code> object, or <code>null</code>
     *   if the user can't be created.
     */
    public User createUser(String userName, String password, String email);

    /**
     * This method creates a pending user, following a user registration
     * request, and upon success sends a confirmation notice to the given
     * email address.
     * 
     * @param userName - the user's name
     * @param password - the user's password
     * @param email -the user's email
     * 
     * @return <code>true</code> if the given user's name is not in use,
     * <code>false</code> otherwise
     */
    public boolean createPendingUser(String userName, String password, String email);

    /**
     * This method modifies the user associated with the given user name.
     * <br/>
     * Both <code>password</code> and <code>email</code> accept a
     * <code>null</code> as a value, which means that this parameter won't be
     * changed during the modification process.
     * 
     * @param userName - the user's name
     * @param newPassword - the new user's password
     * @param newEmail - the new user's email
     * 
     * @return <code>true</code> if the user was successfully modified,
     *   or <code>false</code> upon failure.
     * 
     */
    public boolean modifyUser(String userName, String newPassword, String newEmail);

    /**
     * This method deletes the user referenced by the give user's identifier.
     * 
     * @param userId - the user's identifier
     * 
     * @return <code>true</code> if the user is successfully removed,
     *   or <code>false</code> upon failure.
     */
    public boolean deleteUser(long userId);

    /**
     * This method deletes the user associated with the give user's name.
     * 
     * @param userName - the user's name
     * 
     * @return <code>true</code> if the user is successfully removed,
     *   or <code>false</code> upon failure.
     */
    public boolean deleteUser(String userName);

    /**
     * This method generates a SHA-256 based hash from the specified password
     * string.
     * 
     * @param password - the password string.
     * 
     * @return The SHA-256 hash of the given password.
     */
    public String getHash(String password);

    /**
     * Verifies the existence of a pending user, associated with the given
     * hash value.
     * 
     * @param hashValue - the hash value
     * 
     * @return <code>true</code>, if a corresponding pending user has been
     * found, or <code>false</code> when such user doesn't exists.
     */
    public boolean hasPendingUserHash(String hashValue);

    /**
     * Verifies the existence of a pending user, associated with the given
     * user name.
     * 
     * @param userName - the user's name
     * 
     * @return <code>true</code>, if a corresponding pending user has been
     * found, or <code>false</code> when such user doesn't exists.
     */
    public boolean hasPendingUserName(String userName);

    /**
     * Activates the pending user, associated with the given hash value.
     * 
     * @param hashValue - the hash value
     * 
     * @return <code>true</code>, if the corresponding pending user is
     * activated successfully, or <code>false</code> upon failure.
     */
    public boolean activatePendingUser (String hashValue);

}

//vi: ai nosi sw=4 ts=4 expandtab
