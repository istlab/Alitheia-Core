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

package eu.sqooss.impl.service.security.utils;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.PendingUser;

public class SecurityManagerDatabase implements SecurityManagerDBQueries {
    
    private DBService db;
    
    public SecurityManagerDatabase(DBService db) {
        this.db = db;
    }
    
    public boolean isExistentResourceUrl(String resourceUrl, String userName, String password) {
        Map<String, Object> queryParameters = new Hashtable<String, Object>(3);
        queryParameters.put(IS_EXISTENT_RESOURCE_PARAM_URL, resourceUrl);
        queryParameters.put(IS_EXISTENT_RESOURCE_PARAM_USER, userName);
        queryParameters.put(IS_EXISTENT_RESOURCE_PARAM_PASS, password);
        if (db.doHQL(IS_EXISTENT_RESOURCE_URL, queryParameters).size() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkAuthorizationRule(String resourceUrl, String privilegeName,
            String privilegeValue, String userName, String password) {
        Map<String, Object> queryParameters = new Hashtable<String, Object>(5);
        queryParameters.put(CHECK_AUTHORIZATION_RULE_PARAM_URL, resourceUrl);
        queryParameters.put(CHECK_AUTHORIZATION_RULE_PARAM_PR_NAME, privilegeName);
        queryParameters.put(CHECK_AUTHORIZATION_RULE_PARAM_PR_VALUE, privilegeValue);
        queryParameters.put(CHECK_AUTHORIZATION_RULE_PARAM_USER, userName);
        queryParameters.put(CHECK_AUTHORIZATION_RULE_PARAM_PASS, password);
        if (db.doHQL(CHECK_AUTHORIZATION_RULE, queryParameters).size() != 0) {
            return true;
        } else {
            return false;
        }
    }

}

//vi: ai nosi sw=4 ts=4 expandtab
