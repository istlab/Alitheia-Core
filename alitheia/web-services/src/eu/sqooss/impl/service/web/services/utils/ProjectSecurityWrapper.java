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
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.security.SecurityManager;

public class ProjectSecurityWrapper extends AbstractSecurityWrapper{
    
    public ProjectSecurityWrapper(SecurityManager security, DBService db, Logger logger) {
        super(security, db, logger);
    }

    public boolean checkDirectoriesReadAccess(String userName, String password,
            long[] directoriesIds) {
        synchronized (privilegesLockObject) {
            privileges.clear();
            for (long directoryId : directoriesIds) {
                privileges.put(Privilege.DIRECTORY_READ.toString(),
                        Long.toString(directoryId));
            }
            return security.checkPermission(ServiceUrl.DATABASE.toString(),
                    privileges, userName, password);
        }
    }
    
    public boolean checkDevelopersReadAccess(String userName, String password,
            long[] developersIds) {
        synchronized (privilegesLockObject) {
            privileges.clear();
            for (long developerId : developersIds) {
                privileges.put(Privilege.DEVELOPER_READ.toString(),
                        Long.toString(developerId));
            }
            return security.checkPermission(ServiceUrl.DATABASE.toString(),
                    privileges, userName, password);
        }
    }
    
    public boolean checkProjectVersionsReadAccess(String userName, String password,
            long[] projectVersionsIds) {
        synchronized (privilegesLockObject) {
            privileges.clear();
            if (projectVersionsIds == null) {
                privileges.put(Privilege.PROJECTVERSION_READ.toString(),
                        PrivilegeValue.ALL.toString());
            } else {
                for (long projectVersionId : projectVersionsIds) {
                    privileges.put(Privilege.PROJECT_READ.toString(),
                            Long.toString(projectVersionId));
                }
            }
            return security.checkPermission(ServiceUrl.DATABASE.toString(),
                    privileges, userName, password);
        }
    }
    
}

//vi: ai nosi sw=4 ts=4 expandtab
