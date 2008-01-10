/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

import java.util.Hashtable;
import java.util.Map;

import eu.sqooss.service.db.DBService;
import eu.sqooss.service.security.SecurityAuthorizationRule;
import eu.sqooss.service.security.SecurityUser;

public class DatabaseWrapper {
    
    private DBService db;
    
    public DatabaseWrapper(DBService db) {
        this.db = db;
    }

    /* create methods */
    public long createUser(String userName, String password) {
        return 0;
    }

    public long createGroup(String description) {
        return 0;
    }

    public long createURL(String url) {
        return 0;
    }

    /* create methods */
    
    /* get methods */
    public SecurityAuthorizationRule[] getAuthorizationRules() {
        return null;
    }
    
    public boolean isExistentGroup(long groupId) {
        return false;
    }
    
    public boolean isExistentResourceUrl(long urlId) {
        return false;
    }
    
    public boolean isExistentResourceUrl(String resourceUrl) {
        Map<String, Object> queryParameters = new Hashtable<String, Object>(1);
        queryParameters.put(DatabaseQueries.IS_EXISTENT_RESOURCE_URL_PARAM, resourceUrl);
        if (db.doHQL(DatabaseQueries.IS_EXISTENT_RESOURCE_URL, queryParameters).size() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isExistentUser(long userId) {
        return false;
    }
    /* get methods */

    public boolean checkAuthorizationRule(String resourceUrl, String privilegeName,
            String privilegeValue, String userName, String password) {
        Map<String, Object> queryParameters = new Hashtable<String, Object>(5);
        queryParameters.put(DatabaseQueries.CHECK_AUTHORIZATION_RULE_PARAM_URL, resourceUrl);
        queryParameters.put(DatabaseQueries.CHECK_AUTHORIZATION_RULE_PARAM_PR_NAME, privilegeName);
        queryParameters.put(DatabaseQueries.CHECK_AUTHORIZATION_RULE_PARAM_PR_VALUE, privilegeValue);
        queryParameters.put(DatabaseQueries.CHECK_AUTHORIZATION_RULE_PARAM_USER, userName);
        queryParameters.put(DatabaseQueries.CHECK_AUTHORIZATION_RULE_PARAM_PASS, password.hashCode());
        if (db.doHQL(DatabaseQueries.CHECK_AUTHORIZATION_RULE, queryParameters).size() != 0) {
            return true;
        } else {
            return false;
        }
    }
    
    /* delete methods */
    public void deleteUser(long userId) {
        //TODO:
    }
    /* delete methods */
    
    /* modify methods */
    public void momifyUser(SecurityUser modifiedUser) {
        //TODO:
    }
    /* modify methods */
    
}

//vi: ai nosi sw=4 ts=4 expandtab
