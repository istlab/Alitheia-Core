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
 * <code>UserManager</code> gives an access to the user's management. 
 */
public interface UserManager {
    
    /**
     * @param userId user's identifier
     * @return the user with given identifier,
     * null - if the user doesn't exist
     */
    public User getUser(long userId);
    
    /**
     * @param userName user's name
     * @return the user with given name,
     * null - if the user doesn't exist
     */
    public User getUser(String userName);
    
    /**
     * @return all users in the system
     */
    public User[] getUsers();
    
    /**
     * This method returns the users from the group.
     * @param groupId group's identifier
     * @return
     */
    public User[] getUsers(long groupId);
    
    /**
     * This method creates a new user.
     * @param userName user's name
     * @param password user's password
     * @param email user's e-mail
     * @return the new user, null - if the user isn't created
     */
    public User createUser(String userName, String password, String email);
    
    /**
     * This method sends a notification about a new user.
     * @param userName
     * @param password
     * @param email
     * @return <code>true</code> if the user name is available,
     * <code>false</code> otherwise
     */
    public boolean createPendingUser(String userName, String password, String email);
    
    /**
     * This method modifies the user with given user name.
     * @param userName
     * @param newPassword
     * @param newEmail
     * @return <code>true</code> if the user is changed, false otherwise
     * 
     */
    public boolean modifyUser(String userName, String newPassword, String newEmail);
    
    /**
     * This method deletes the user with given identifier.
     * @param userId user's identifier
     * @return true - if the user is deleted successfully, false - otherwise
     */
    public boolean deleteUser(long userId);
    
    /**
     * This method deletes the user with given name.
     * @param userName user's name
     * @return true - if the user is deleted successfully, false - otherwise
     */
    public boolean deleteUser(String userName);
    
    /**
     * This method returns the password's hash.
     * The method uses SHA-256.
     * @param password
     * @return the password's hash
     */
    public String getHash(String password);

    // TODO: Javadoc
    /**
     * Checks if there is a pending user record that contains the same hash
     * value.
     * 
     * @param hashValue the hash value
     * 
     * @return true, if a corresponding pending user record is found
     */
    public boolean isPendingUser(String hashValue);

    // TODO: Javadoc
    /**
     * Activates the pending user record with the same hash value.
     * 
     * @param hashValue the hash value
     * 
     * @return true, if the corresponding pending user record is activated
     */
    public boolean activatePendingUser (String hashValue);
}

//vi: ai nosi sw=4 ts=4 expandtab
