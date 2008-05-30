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

import eu.sqooss.service.security.SecurityManager;

public class AbstractSecurityWrapper implements SecurityWrapperConstants {
    
    protected SecurityManager security;
    protected Hashtable<String, String> privileges;
    protected Object privilegesLockObject = new Object();
    
    public AbstractSecurityWrapper(SecurityManager security) {
        this.security = security;
        this.privileges = new Hashtable<String, String>();
    }
    
    public void checkDBReadAccess(String userName, String password) {
        synchronized (privilegesLockObject) {
            privileges.clear();
            privileges.put(Privilege.ACTION.toString(),
                    PrivilegeValue.READ.toString());
            if (!security.checkPermission(ServiceUrl.DATABASE.toString(),
                    privileges, userName, password)) {
                throw new SecurityException("Security violation!");
            }
        }
    }
    
    public void checkDBProjectsReadAccess(String userName, String password,
            long[] projectsIds, String projectName) {
        synchronized (privilegesLockObject) {
            privileges.clear();
            privileges.put(Privilege.ACTION.toString(), PrivilegeValue.READ.toString());
            if (projectsIds != null) {
                for (int i = 0; i < projectsIds.length; i++) {
                    privileges.put(Privilege.PROJECT_ID.toString(), Long.toString(projectsIds[i]));
                }
            }
            if (projectName != null) {
                privileges.put(Privilege.PROJECT_ID.toString(), projectName);
            }
            if (!security.checkPermission(ServiceUrl.DATABASE.toString(),
                    privileges, userName, password)) {
                throw new SecurityException("Security violation!");
            }
        }
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
