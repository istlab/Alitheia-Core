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

public interface DatabaseQueries {
    
    //security manager
    public static final String IS_EXISTENT_RESOURCE_URL_PARAM = "url";
    
    public static final String IS_EXISTENT_RESOURCE_URL = "from ServiceURL " +
                                                          "where ServiceURL.url=:" +
                                                          IS_EXISTENT_RESOURCE_URL_PARAM;
    
    public static final String CHECK_AUTHORIZATION_RULE_PARAM_URL = "resource_url";
    
    public static final String CHECK_AUTHORIZATION_RULE_PARAM_PR_NAME = "privilege_name";
    
    public static final String CHECK_AUTHORIZATION_RULE_PARAM_PR_VALUE = "privilege_value";
    
    public static final String CHECK_AUTHORIZATION_RULE_PARAM_USER = "user_name";
    
    public static final String CHECK_AUTHORIZATION_RULE_PARAM_PASS = "password_hash";
        
    public static final String CHECK_AUTHORIZATION_RULE = "select GroupPrivilege " +
                                                          "from User, GroupUser, Group," +
                                                          "     Privilege, PrivilegeValue, GroupPrivilege, ServiceURL " +
                                                          "where User.userID=GroupUser.userID " +
                                                          " and Group.groupID=GroupUser.groupID " +
                                                          " and Privilege.privilegeID=PrivilegeValue.privilegeID " +
                                                          " and PrivilegeValue.privilegeValueID=GroupPrivilege.privilegeValueID " +
                                                          " and Group.groupID=GroupPrivilege.groupID " +
                                                          " and ServiceURL.urlID=GroupPrivilege.urlID " +
                                                          " and User.username=:" + CHECK_AUTHORIZATION_RULE_PARAM_USER +
                                                          " and User.password=:" + CHECK_AUTHORIZATION_RULE_PARAM_PASS +
                                                          " and Privilege.description=:" + CHECK_AUTHORIZATION_RULE_PARAM_PR_NAME +
                                                          " and PrivilegeValue.value=:" + CHECK_AUTHORIZATION_RULE_PARAM_PR_VALUE;
  //security manager
    
}

//vi: ai nosi sw=4 ts=4 expandtab
