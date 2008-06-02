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

package eu.sqooss.scl;

import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Map;

import org.apache.axis2.AxisFault;

import eu.sqooss.scl.accessor.WSUserAccessor;
import eu.sqooss.ws.client.WsStub;
import eu.sqooss.ws.client.datatypes.WSUser;
import eu.sqooss.ws.client.datatypes.WSUserGroup;
import eu.sqooss.ws.client.ws.CreatePendingUser;
import eu.sqooss.ws.client.ws.CreatePendingUserResponse;
import eu.sqooss.ws.client.ws.DeleteUserById;
import eu.sqooss.ws.client.ws.DeleteUserByIdResponse;
import eu.sqooss.ws.client.ws.GetMessageOfTheDay;
import eu.sqooss.ws.client.ws.GetMessageOfTheDayResponse;
import eu.sqooss.ws.client.ws.GetUserByName;
import eu.sqooss.ws.client.ws.GetUserByNameResponse;
import eu.sqooss.ws.client.ws.GetUserGroups;
import eu.sqooss.ws.client.ws.GetUserGroupsResponse;
import eu.sqooss.ws.client.ws.GetUsersByIds;
import eu.sqooss.ws.client.ws.GetUsersByIdsResponse;
import eu.sqooss.ws.client.ws.ModifyUser;
import eu.sqooss.ws.client.ws.ModifyUserResponse;
import eu.sqooss.ws.client.ws.NotifyAdmin;
import eu.sqooss.ws.client.ws.NotifyAdminResponse;

class WSUserAccessorImpl extends WSUserAccessor {

    private static final String METHOD_NAME_CREATE_PENDING_USER  = "createPendingUser";

    private static final String METHOD_NAME_GET_USERS_BY_IDS     = "getUsersByIds";
    
    private static final String METHOD_NAME_GET_USER_GROUPS      = "getUserGroups";

    private static final String METHOD_NAME_GET_USER_BY_NAME     = "getUserByName";

    private static final String METHOD_NAME_MODIFY_USER          = "modifyUser";

    private static final String METHOD_NAME_DELETE_USER_BY_ID    = "deleteUserById";

    private static final String METHOD_NAME_GET_MESSAGE_OF_THE_DAY = "getMessageOfTheDay";
    
    private static final String METHOD_NAME_NOTIFY_ADMIN = "notifyAdmin";

    private static final WSUser[] EMPTY_ARRAY_USERS = new WSUser[0];
    
    private Map<String, Object> parameters;
    private String userName;
    private String password;
    private WsStub wsStub;

    public WSUserAccessorImpl(String userName, String password, String webServiceUrl) throws WSException {
        this.userName = userName;
        this.password = password;
        parameters = new Hashtable<String, Object>();
        try {
            this.wsStub = new WsStub(webServiceUrl);
        } catch (AxisFault af) {
            throw new WSException(af);
        }
    }

    /**
     * @see eu.sqooss.scl.accessor.WSUserAccessor#createPendingUser(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public boolean createPendingUser(String newUserName, String newPassword,
            String email) throws WSException {
        CreatePendingUserResponse response;
        CreatePendingUser params;
        if (!parameters.containsKey(METHOD_NAME_CREATE_PENDING_USER)) {
            params = new CreatePendingUser();
            params.setPasswordForAccess(password);
            params.setUserNameForAccess(userName);
            parameters.put(METHOD_NAME_CREATE_PENDING_USER, params);
        } else {
            params = (CreatePendingUser) parameters.get(
                    METHOD_NAME_CREATE_PENDING_USER);
        }
        synchronized (params) {
            params.setNewUserName(newUserName);
            params.setNewPassword(newPassword);
            params.setEmail(email);
            try {
                response = wsStub.createPendingUser(params);
            } catch (RemoteException re) {
                throw new WSException(re);
            }
        }
        return response.get_return();
    }

    /**
     * @see eu.sqooss.scl.accessor.WSUserAccessor#getUserById(long[])
     */
    @Override
    public WSUser[] getUsersByIds(long[] usersIds) throws WSException {
        if (!isNormalizedWSArrayParameter(usersIds)) return EMPTY_ARRAY_USERS;
        GetUsersByIdsResponse response;
        GetUsersByIds params;
        if (!parameters.containsKey(METHOD_NAME_GET_USERS_BY_IDS)) {
            params = new GetUsersByIds();
            params.setPasswordForAccess(password);
            params.setUserNameForAccess(userName);
            parameters.put(METHOD_NAME_GET_USERS_BY_IDS, params);
        } else {
            params = (GetUsersByIds) parameters.get(
                    METHOD_NAME_GET_USERS_BY_IDS);
        }
        synchronized (params) {
            params.setUsersIds(usersIds);
            try {
                response = wsStub.getUsersByIds(params);
            } catch (RemoteException re) {
                throw new WSException(re);
            }
        }
        return (WSUser[]) normalizeWSArrayResult(response.get_return());
    }

    /**
     * @see eu.sqooss.scl.accessor.WSUserAccessor#getUserGroups()
     */
    @Override
    public WSUserGroup[] getUserGroups() throws WSException {
        GetUserGroupsResponse response;
        GetUserGroups params;
        if (!parameters.containsKey(METHOD_NAME_GET_USER_GROUPS)) {
            params = new GetUserGroups();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_GET_USER_GROUPS, params);
        } else {
            params = (GetUserGroups) parameters.get(
                    METHOD_NAME_GET_USER_GROUPS);
        }
        synchronized (params) {
            try {
                response = wsStub.getUserGroups(params);
            } catch (RemoteException re) {
                throw new WSException(re);
            }
        }
        return (WSUserGroup[]) normalizeWSArrayResult(response.get_return());
    }

    /**
     * @see eu.sqooss.scl.accessor.WSUserAccessor#getUserByName(String)
     */
    @Override
    public WSUser getUserByName(String name) throws WSException {
        GetUserByNameResponse response;
        GetUserByName params;
        if (!parameters.containsKey(METHOD_NAME_GET_USER_BY_NAME)) {
            params = new GetUserByName();
            params.setPasswordForAccess(password);
            params.setUserNameForAccess(userName);
            parameters.put(METHOD_NAME_GET_USER_BY_NAME, params);
        } else {
            params = (GetUserByName) parameters.get(
                    METHOD_NAME_GET_USER_BY_NAME);
        }
        synchronized (params) {
            params.setUserName(name);
            try {
                response = wsStub.getUserByName(params);
            } catch (RemoteException re) {
                throw new WSException(re);
            }
        }
        return response.get_return();
    }

    /**
     * @see eu.sqooss.scl.accessor.WSUserAccessor#modifyUser(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public boolean modifyUser(String userName, String newPassword,
            String newEmail) throws WSException {
        ModifyUserResponse response;
        ModifyUser params;
        if (!parameters.containsKey(METHOD_NAME_MODIFY_USER)) {
            params = new ModifyUser();
            params.setPasswordForAccess(password);
            params.setUserNameForAccess(userName);
            parameters.put(METHOD_NAME_MODIFY_USER, params);
        } else {
            params = (ModifyUser) parameters.get(
                    METHOD_NAME_MODIFY_USER);
        }
        synchronized (params) {
            params.setUserName(userName);
            params.setNewPassword(newPassword);
            params.setNewEmail(newEmail);
            try {
                response = wsStub.modifyUser(params);
            } catch (RemoteException re) {
                throw new WSException(re);
            }
        }
        return response.get_return();
    }

    /**
     * @see eu.sqooss.scl.accessor.WSUserAccessor#deleteUserById(long)
     */
    @Override
    public boolean deleteUserById(long userId) throws WSException {
        DeleteUserByIdResponse response;
        DeleteUserById params;
        if (!parameters.containsKey(METHOD_NAME_DELETE_USER_BY_ID)) {
            params = new DeleteUserById();
            params.setPasswordForAccess(password);
            params.setUserNameForAccess(userName);
            parameters.put(METHOD_NAME_DELETE_USER_BY_ID, params);
        } else {
            params = (DeleteUserById) parameters.get(
                    METHOD_NAME_DELETE_USER_BY_ID);
        }
        synchronized (params) {
            params.setUserId(userId);
            try {
                response = wsStub.deleteUserById(params);
            } catch (RemoteException re) {
                throw new WSException(re);
            }
        }
        return response.get_return();
    }

    /**
     * @see eu.sqooss.scl.accessor.WSUserAccessor#getUserMessageOfTheDay(java.lang.String)
     */
    public String getMessageOfTheDay() throws WSException {
        GetMessageOfTheDayResponse response;
        GetMessageOfTheDay params;
        if (!parameters.containsKey(METHOD_NAME_GET_MESSAGE_OF_THE_DAY)) {
            params = new GetMessageOfTheDay();
            params.setUserName(userName);
            params.setPassword(password);
            parameters.put(METHOD_NAME_GET_MESSAGE_OF_THE_DAY, params);
        } else {
            params = (GetMessageOfTheDay) parameters.get(
                    METHOD_NAME_GET_MESSAGE_OF_THE_DAY);
        }
        synchronized (params) {
            try {
                response = wsStub.getMessageOfTheDay(params);
            } catch (RemoteException re) {
                throw new WSException(re);
            }
        }
        return response.get_return();
    }
    
    /**
     * @see eu.sqooss.scl.accessor.WSUserAccessor#notifyAdmin(java.lang.String, java.lang.String)
     */
    @Override
    public boolean notifyAdmin(String messageBody, String title) throws WSException {
        if ((messageBody == null) || (title == null)) {
            throw new IllegalArgumentException("Null argument!");
        }
        if ((messageBody.trim().length() == 0) || (title.trim().length() == 0)) {
            throw new IllegalArgumentException("Empty argument!");
        }
        NotifyAdminResponse response;
        NotifyAdmin params;
        if (!parameters.containsKey(METHOD_NAME_NOTIFY_ADMIN)) {
            params = new NotifyAdmin();
            params.setPassword(password);
            params.setUserName(userName);
            parameters.put(METHOD_NAME_NOTIFY_ADMIN, params);
        } else {
            params = (NotifyAdmin) parameters.get(METHOD_NAME_NOTIFY_ADMIN);
        }
        synchronized (params) {
            params.setMessageBody(messageBody);
            params.setTitle(title);
            try {
                response = wsStub.notifyAdmin(params);
            } catch (RemoteException re) {
                throw new WSException(re);
            }
        }
        return response.get_return();
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
