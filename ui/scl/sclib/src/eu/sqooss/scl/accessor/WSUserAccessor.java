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

package eu.sqooss.scl.accessor;

import eu.sqooss.scl.WSException;
import eu.sqooss.ws.client.datatypes.WSUser;

/**
 * This class contains the users methods. 
 */
public abstract class WSUserAccessor extends WSAccessor {
    
    /**
     * This method creates a new SQO-OSS user with the given characteristics.
     * 
     * @param newUserName - name for the new user
     * @param newPassword - password of the new user
     * @param email - email address of the new user
     * 
     * @return The <code>WSUser</code> object, that describes the newly created
     * user.
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be established to the SQO-OSS's web services service</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     * 
     * @deprecated This method is deprecated, in favor of
     * <code>createPendingUser()<code>.
     */
    @Deprecated
    public abstract WSUser createUser(String newUserName, String newPassword,
            String email) throws WSException;
    
    /**
     * 
     * This method creates a new pending user entry, and sends an email to the
     * given user address with a request for confirmation. After successful
     * confirmation, the pending user entry is converted into a SQO-OSS user.
     * <br/>
     * Note: If the user doesn't confirm the request in time, then the pending
     * user entry is automatically removed from the system, after its
     * expiration.
     * 
     * @param newUserName - name for the new user
     * @param newPassword - password of the new user
     * @param email - email address of the new user
     * 
     * @return <code>true</code> upon success, or <code>false</code> when a
     * user with the same name already exists.
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be established to the SQO-OSS's web services service</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract boolean createPendingUser(String newUserName, String newPassword,
            String email) throws WSException;
    
    /**
     * This method returns all known information about the user referenced by
     * the given identifier.
     * <br/>
     * <i>The information does not include the user's password hash.<i>
     *  
     * @param userId - the identifier of the requested user
     * 
     * @return The <code>WSUser</code> object describing the requested user.
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be established to the SQO-OSS's web services service</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract WSUser getUserById(long userId) throws WSException;
    
    /**
     * This method returns all known information about the user associated
     * with the given user name.
     * <br/>
     * <i>The information does not include the user's password hash.<i>
     *  
     * @param userName - the name of the requested user
     * 
     * @return The <code>WSUser</code> object describing the requested user.
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be established to the SQO-OSS's web services service</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract WSUser getUserByName (String name) throws WSException;
    
    /**
     * This method modifies the information of the existing user associated
     * with the given user name.
     * <br/>
     * <i>This method can change the user's password and email address
     *   only.</i>
     * 
     * @param userName - the name of the requested user
     * @param newPassword - the new password
     * @param newEmail - the new email address
     * 
     * @return <code>true</code> upon successful modification,
     * or <code>false</code> in case of failure.
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be established to the SQO-OSS's web services service</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract boolean modifyUser(String userName, String newPassword,
            String newEmail) throws WSException;
    
    /**
     * This method deletes the user referenced by the given identifier.
     * 
     * @param userId - the identifier of the requested user
     * 
     * @return <code>true</code> upon successful removal,
     * or <code>false</code> in case of failure.
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be established to the SQO-OSS's web services service</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract boolean deleteUserById(long userId) throws WSException;
    
    /**
     * Returns the user's message of the day. MOTD's are usually created by
     * the SQO-OSS system administrator or the SQO-OSS framework itself,
     * upon occurrence of specific events (like addition of a new project).
     * 
     * @param userName - the user's name
     * 
     * @return The message of the day, which is valid for that user.
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be established to the SQO-OSS's web services service</li>
     *  <li>if web services service throws an exception</li>
     * <ul>
     */
    public abstract String getUserMessageOfTheDay(String userName) throws WSException;
}

//vi: ai nosi sw=4 ts=4 expandtab
