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

import java.util.Hashtable;

import eu.sqooss.service.security.SecurityConstants;
import eu.sqooss.service.security.SecurityManager;

public class SecurityWrapper implements SecurityConstants {
    
    private SecurityManager security;
    private Hashtable<String, String> privileges;
    private Object privilegesLockObject = new Object();
    
    public SecurityWrapper(SecurityManager security) {
        this.security = security;
        this.privileges = new Hashtable<String, String>();
    }
    
    public void checkDBReadAccess(String userName, String password) {
        synchronized (privilegesLockObject) {
            privileges.clear();
            privileges.put(Privilege.ACTION.toString(),
                    PrivilegeValue.READ.toString());
            if (!security.checkPermission(URL_SQOOSS_DATABASE, privileges, userName, password)) {
                throw new SecurityException("Security violation!");
            }
        }
    }
    
    public void checkDBWriteAccess(String userName, String password) {
        synchronized (privilegesLockObject) {
            privileges.clear();
            privileges.put(Privilege.ACTION.toString(),
                    PrivilegeValue.WRITE.toString());
            if (!security.checkPermission(URL_SQOOSS_DATABASE, privileges, userName, password)) {
                throw new SecurityException("Security violation!");
            }
        }
    }
    
    public void checkProjectsReadAccess(String userName, String password, long[] projectsIds) {
        synchronized (privilegesLockObject) {
            privileges.clear();
            privileges.put(Privilege.ACTION.toString(), PrivilegeValue.READ.toString());
            for (int i = 0; i < projectsIds.length; i++) {
                privileges.put(Privilege.PROJECT_ID.toString(), Long.toString(projectsIds[i]));
            }
            if (!security.checkPermission(URL_SQOOSS_PROJECTS, privileges, userName, password)) {
                throw new SecurityException("Security violation!");
            }
        }
    }
    
    public void checkProjectVersionsReadAccess(String userName, String password, long[] projectVersionsIds) {
        synchronized (privilegesLockObject) {
            privileges.clear();
            privileges.put(Privilege.ACTION.toString(), PrivilegeValue.READ.toString());
            for (int i = 0; i < projectVersionsIds.length; i++) {
                privileges.put(Privilege.PROJECT_VERSION_ID.toString(),
                        Long.toString(projectVersionsIds[i]));
            }
            if (!security.checkPermission(URL_SQOOSS_PROJECTS, privileges, userName, password)) {
                throw new SecurityException("Security violation!");
            }
        }
    }
    
    public void checkMetricsReadAccess(String userName, String password,
            String[] mnemonics) {
        synchronized (privilegesLockObject) {
            privileges.clear();
            privileges.put(Privilege.ACTION.toString(), PrivilegeValue.READ.toString());
            if ((mnemonics == null) || (mnemonics.length == 0)) {
                privileges.put(Privilege.METRIC_MNEMONIC.toString(), PrivilegeValue.ALL.toString());
            } else {
                for (String currentMnemonic : mnemonics) {
                    privileges.put(Privilege.METRIC_MNEMONIC.toString(),
                            currentMnemonic);
                }
            }
            if (!security.checkPermission(URL_SQOOSS_PROJECTS, privileges, userName, password)) {
                throw new SecurityException("Security violation!");
            }
        }
    }
    
    public void checkUsersReadAccess(String userName, String password,
            long[] usersIds, String privilegeUserName) {
        synchronized (privilegesLockObject) {
            privileges.clear();
            privileges.put(Privilege.ACTION.toString(), PrivilegeValue.READ.toString());
            if (usersIds != null) {
                for (long userId : usersIds) {
                    privileges.put(Privilege.USER_ID.toString(), Long.toString(userId));
                }
            } else if (privilegeUserName != null) {
                privileges.put(Privilege.USER_ID.toString(), privilegeUserName);
            }
            if (!security.checkPermission(URL_SQOOSS_SECURITY, privileges, userName, password)) {
                throw new SecurityException("Security violation!");
            }
        }
    }
    
    public void checkUserWriteAccess(String userName, String password,
            long userId, String privilegeUserName) {
        synchronized (privilegesLockObject) {
            privileges.clear();
            privileges.put(Privilege.ACTION.toString(), PrivilegeValue.WRITE.toString());
            if (privilegeUserName != null) {
                privileges.put(Privilege.USER_ID.toString(), privilegeUserName);
            } else if (userId >= 0) {
                privileges.put(Privilege.USER_ID.toString(), Long.toString(userId));
            }
            if (!security.checkPermission(URL_SQOOSS_SECURITY, privileges, userName, password)) {
                throw new SecurityException("Security violation!");
            }
        }
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
