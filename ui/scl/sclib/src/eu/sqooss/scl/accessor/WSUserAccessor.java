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

public abstract class WSUserAccessor extends WSAccessor {
    
    /**
     * This method creates a new user.
     * 
     * @param newUserName - user name
     * @param newPassword - user's password
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be establish to the SQO-OSS's web services service</li>
     *  <li>if web services service throws a exception</li>
     * <ul>
     */
    public abstract WSUser submitUser(String newUserName, String newPassword,
            String email) throws WSException;
    
    /**
     * 
     * This method sends a request for a new user account.
     * 
     * @param newUserName
     * @param newPassword
     * @param email
     * @return <code>true</code> if the request is sent,
     * <code>false</code> otherwise
     * @throws WSException
     */
    public abstract boolean submitPendingUser(String newUserName, String newPassword,
            String email) throws WSException;
    
    /**
     * This method returns information about the user with a given id.
     *  
     * @param userId - user's id
     * 
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be establish to the SQO-OSS's web services service</li>
     *  <li>if web services service throws a exception</li>
     * <ul>
     */
    public abstract WSUser displayUser(long userId) throws WSException;
    
    /**
     * This method modifies the existent user with a given user name.
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be establish to the SQO-OSS's web services service</li>
     *  <li>if web services service throws a exception</li>
     * <ul>
     */
    public abstract boolean modifyUser(String userName, String newPassword,
            String newEmail) throws WSException;
    
    /**
     * This method deletes the user with a given id.
     * 
     * @param userId - user's id
     * @throws WSException
     * <ul>
     *  <li>if the connection can't be establish to the SQO-OSS's web services service</li>
     *  <li>if web services service throws a exception</li>
     * <ul>
     */
    public abstract boolean deleteUser(long userId) throws WSException;
    
}

//vi: ai nosi sw=4 ts=4 expandtab
