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

package eu.sqooss.impl.service.web.services.utils;

import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.GroupType;
import eu.sqooss.service.db.User;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.security.SecurityManager;
import eu.sqooss.service.security.UserManager;

public class UserSecurityWrapper extends AbstractSecurityWrapper{
    
    private UserManager securityUserManager;
    
    public UserSecurityWrapper(SecurityManager security, DBService db, Logger logger) {
        super(security, db, logger);
        this.securityUserManager = security.getUserManager();
    }

    public boolean addPermissionsToNewUsersGroup() {
        return security.createSecurityConfiguration(
                security.getNewUsersGroup(),
                GroupType.Type.USER,
                Privilege.ADMIN_NOTIFY.toString(),
                PrivilegeValue.ALL.toString(),
                ServiceUrl.WEBADMIN.toString());
    }
    
    public boolean addPermissonsToSystemGroup() {
        return security.createSecurityConfiguration(
                security.getSystemGroup(),
                GroupType.Type.SYSTEM,
                Privilege.USER_WRITE.toString(),
                PrivilegeValue.ALL.toString(),
                ServiceUrl.SECURITY.toString());
    }
    
    public boolean checkUsersWriteAccess(String userNameForAccess,
            String passwordForAccess, String userName, long[] usersIds) {
        synchronized (privilegesLockObject) {
            privileges.clear();
            if (userName != null) {
                User user = securityUserManager.getUser(userName);
                if (user == null) return false;
                privileges.put(Privilege.USER_WRITE.toString(),
                        Long.toString(user.getId()));
            }
            if (usersIds != null) {
                for (long userId : usersIds) {
                    privileges.put(Privilege.USER_WRITE.toString(),
                            Long.toString(userId));
                }
            }
            return security.checkPermission(ServiceUrl.SECURITY.toString(),
                    privileges, userNameForAccess, passwordForAccess);
        }
    }
    
    public boolean checkUsersReadAccess(String userName, String password,
            String otherUserName, long[] usersIds) {
        synchronized (privilegesLockObject) {
            privileges.clear();
            if (otherUserName != null) {
                User user = securityUserManager.getUser(userName);
                if (user == null) return false;
                privileges.put(Privilege.USER_READ.toString(),
                        Long.toString(user.getId()));
            }
            for (long userId : usersIds) {
                privileges.put(Privilege.USER_READ.toString(),
                        Long.toString(userId));
            }
            return security.checkPermission(ServiceUrl.SECURITY.toString(),
                    privileges, userName, password);
        }
    }
    
    public boolean checkGroupReadAccess(String userName, String password) {
        synchronized (privilegesLockObject) {
            privileges.clear();
            privileges.put(Privilege.GROUP_READ.toString(), PrivilegeValue.ALL.toString());
            return security.checkPermission(ServiceUrl.SECURITY.toString(),
                    privileges, userName, password);
        }
    }
    
    public boolean checkWebAdminGetMessageAccess(String userName, String password) {
        User user = securityUserManager.getUser(userName);
        if (user == null) return false; 
        synchronized (privilegesLockObject) {
            privileges.clear();
            privileges.put(Privilege.ADMIN_GET_MESSAGE_OF_THE_DAY.toString(),
                    Long.toString(user.getId()));
            return security.checkPermission(ServiceUrl.WEBADMIN.toString(),
                    privileges, userName, password);
        }
    }
    
    public boolean checkWebAdminNotifyAccess(String userName, String password) {
        User user = securityUserManager.getUser(userName);
        if (user == null) return false;
        synchronized (privilegesLockObject) {
            privileges.clear();
            privileges.put(Privilege.ADMIN_NOTIFY.toString(),
                    Long.toString(user.getId()));
            return security.checkPermission(ServiceUrl.WEBADMIN.toString(),
                    privileges, userName, password);
        }
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
